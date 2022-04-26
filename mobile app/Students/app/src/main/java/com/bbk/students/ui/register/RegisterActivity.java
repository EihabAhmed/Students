package com.bbk.students.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bbk.students.R;
import com.bbk.students.common.Persistent;
import com.bbk.students.model.RegisterModel;
import com.bbk.students.network.StudentsApi;
import com.bbk.students.ui.BaseActivity;
import com.bbk.students.ui.login.LoginActivity;
import com.bbk.students.ui.main.MainActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends BaseActivity {
    private final int REGISTER = 0;
    private final int LOGIN = 1;
    private int state = REGISTER;
    private RegisterModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setupListeners();
    }

    private void setupListeners() {
        final TextView btnRegister = findViewById(R.id.btn_register);
        final TextView btnAlreadyHaveAccount = findViewById(R.id.btn_already_have_account);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeRequest();
            }
        });

        btnAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void makeRequest() {
        final EditText etEmail = findViewById(R.id.et_register_email);
        final EditText etPassword = findViewById(R.id.et_register_password);
        final EditText etConfirmPassword = findViewById(R.id.et_confirm_password);

        model = new RegisterModel();
        model.email = etEmail.getText().toString();
        model.password = etPassword.getText().toString();
        model.confirmPassword = etConfirmPassword.getText().toString();
        if (model.email.length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!model.password.equals(model.confirmPassword)) {
            Toast.makeText(getApplicationContext(), "Password and Confirm Password must be identical", Toast.LENGTH_SHORT).show();
            return;
        }
        if (model.password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        StudentsApi api = new StudentsApi(this, "Register");
        String url = baseUrl + "/api/Account/Register";
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Content-Type", "application/json");
        requestProperties.put("Accept", "application/json");
        String body = convertObjectToJson(model);
        api.request(StudentsApi.POST, url, requestProperties, body);
    }

    @Override
    public void receiveResponse(StudentsApi.Response response) {
        if (response.responseCode >= 200 && response.responseCode <= 299) {
            if (state == REGISTER) {
                state = LOGIN;
                login();
            } else {
                getToken(response);
                Persistent.setUserName(model.email, this);
                Persistent.setAdmin(false);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            showResult(response);
            state = REGISTER;
        }
    }

    @Override
    public String convertObjectToJson(Object o) {
        RegisterModel model = (RegisterModel) o;
        String json = "{";
        json += "Email:\"" + model.email + "\"";
        json += ",Password:\"" + model.password + "\"";
        json += ",ConfirmPassword:\"" + model.confirmPassword + "\"";
        json += "}";

        return json;
    }

    private void showResult(StudentsApi.Response response) {
        if (response.responseCode == 0) {
            Toast.makeText(getApplicationContext(), "Cannot connect to server", Toast.LENGTH_SHORT).show();
        } else {
            try {
                JSONObject json = new JSONObject(response.responseBody);
                String message;
                if (state == REGISTER) {
                    JSONObject modelState = json.getJSONObject("modelState");
                    message = (String) (modelState.getJSONArray("").get(0));
                } else {
                    message = json.getString("error_description");
                }

                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            } catch(Exception ex) {
                Log.d("Register", Objects.requireNonNull(ex.getMessage()));
            }
        }
    }

    public void login() {
        StudentsApi api = new StudentsApi(this, "Login");
        String url = baseUrl + "/token";
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Content-Type", "application/x-www-form-urlencoded");
        requestProperties.put("Accept", "application/json");
        String body = convertLoginObjectToJson(model);
        api.request(StudentsApi.POST, url, requestProperties, body);
    }

    private void getToken(StudentsApi.Response response) {
        try {
            JSONObject json = new JSONObject(response.responseBody);
            String token = json.getString("access_token");

            Persistent.setToken(token, this);
        } catch(Exception ex) {
            Log.d("Login", Objects.requireNonNull(ex.getMessage()));
        }
    }

    public String convertLoginObjectToJson(Object o) {
        RegisterModel model = (RegisterModel) o;
        String json = "grant_type=password&username=";
        json += model.email + "&password=";
        json += model.password;

        return json;
    }
}
