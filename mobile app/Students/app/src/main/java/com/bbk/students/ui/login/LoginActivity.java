package com.bbk.students.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bbk.students.R;
import com.bbk.students.common.Persistent;
import com.bbk.students.model.Profile;
import com.bbk.students.model.RegisterModel;
import com.bbk.students.network.StudentsApi;
import com.bbk.students.ui.BaseActivity;
import com.bbk.students.ui.main.MainActivity;
import com.bbk.students.ui.register.RegisterActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends BaseActivity {
    private final int LOGIN = 0;
    private final int GET_ADMIN = 1;
    private int state = LOGIN;
    private RegisterModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        setupListeners();
    }

    private void setupListeners() {
        final TextView btnLogin = findViewById(R.id.btn_login);
        final TextView btnDontHaveAccount = findViewById(R.id.btn_dont_have_account);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeRequest();
            }
        });

        btnDontHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void makeRequest() {
        final EditText etEmail = findViewById(R.id.et_login_email);
        final EditText etPassword = findViewById(R.id.et_login_password);

        model = new RegisterModel();
        model.email = etEmail.getText().toString();
        model.password = etPassword.getText().toString();

        if (model.email.length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        StudentsApi api = new StudentsApi(this, "Login");
        String url = getString(R.string.global_url) + "/token";
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Content-Type", "application/x-www-form-urlencoded");
        requestProperties.put("Accept", "application/json");
        String body = convertObjectToJson(model);
        api.request(StudentsApi.POST, url, requestProperties, body);
    }

    @Override
    public void receiveResponse(StudentsApi.Response response) {
        if (response.responseCode >= 200 && response.responseCode <= 299) {
            if (state == LOGIN) {
                getToken(response);
                Persistent.setUserName(model.email, this);
                state = GET_ADMIN;
                getAdmin(model.email);
            } else {
                getAdminFromJson(response);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            showResult(response);
            state = LOGIN;
        }
    }

    @Override
    public String convertObjectToJson(Object o) {
        RegisterModel model = (RegisterModel) o;
        String json = "grant_type=password&username=";
        json += model.email + "&password=";
        json += model.password;

        return json;
    }

    private void showResult(StudentsApi.Response response) {
        if (response.responseCode == 0) {
            Toast.makeText(getApplicationContext(), "Cannot connect to server", Toast.LENGTH_SHORT).show();
        } else {
            try {
                JSONObject json = new JSONObject(response.responseBody);
                String message;
                if (state == LOGIN) {
                    message = json.getString("error_description");
                } else {
                    message = json.getString("message");
                }

                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            } catch(Exception ex) {
                Log.d("Login", Objects.requireNonNull(ex.getMessage()));
            }
        }
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

    public void getAdmin(String userName) {
        StudentsApi api = new StudentsApi(this, "Login");
        String url = getString(R.string.global_url) + "/api/IsAdmin?username=" + userName;
        Map<String, String> requestProperties = new HashMap<>();
        //requestProperties.put("Content-Type", "application/json");
        requestProperties.put("Accept", "application/json");
        requestProperties.put("Authorization", "Bearer " + Persistent.getToken());
        //Profile profile = new Profile();
        //profile.email = model.email;
        //String body = convertProfileObjectToJson(profile);
        api.request(StudentsApi.GET, url, requestProperties, null);
    }

    public String convertProfileObjectToJson(Object o) {
        Profile profile = (Profile) o;
        String json = "{";
        json += "UserName:\"" + profile.email + "\"";
        json += ",Admin:false";
        json += "}";

        return json;
    }

    private void getAdminFromJson(StudentsApi.Response response) {
        try {
            JSONObject json = new JSONObject(response.responseBody);
            boolean admin = json.getBoolean("admin");

            Persistent.setAdmin(admin);
        } catch(Exception ex) {
            Log.d("Login", Objects.requireNonNull(ex.getMessage()));
        }
    }
}

