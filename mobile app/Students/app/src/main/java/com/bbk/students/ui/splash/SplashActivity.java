package com.bbk.students.ui.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.bbk.students.R;
import com.bbk.students.common.Persistent;
import com.bbk.students.network.StudentsApi;
import com.bbk.students.ui.BaseActivity;
import com.bbk.students.ui.login.LoginActivity;
import com.bbk.students.ui.main.MainActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SplashActivity extends BaseActivity {
    private long SPLASH_TIMEOUT = TimeUnit.SECONDS.toMillis(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        checkLoggedIn();
    }

    private void checkLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("Students", MODE_PRIVATE);
        String token = prefs.getString("token", "");
        String userName = prefs.getString("username", "");

        if (token.equals("")) {
            Toast.makeText(getApplicationContext(), "Token empty", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openLogin();
                    finish();
                }
            }, SPLASH_TIMEOUT);
        } else {
            Toast.makeText(getApplicationContext(), "Token found", Toast.LENGTH_SHORT).show();
            Persistent.setToken(token, this);
            Persistent.setUserName(userName, this);
            makeRequest();
        }
    }

    private void openLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void openMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void makeRequest() {
        StudentsApi api = new StudentsApi(this, "Splash");
        String url = baseUrl + "/api/IsAdmin?username=" + Persistent.getUserName();
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Accept", "application/json");
        requestProperties.put("Authorization", "Bearer " + Persistent.getToken());
        api.request(StudentsApi.GET, url, requestProperties, null);
    }

    @Override
    public void receiveResponse(StudentsApi.Response response) {
        if (response.responseCode >= 200 && response.responseCode <= 299) {
            getAdminFromJson(response);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openMain();
                    finish();
                }
            }, SPLASH_TIMEOUT);
        } else if (response.responseCode == 401) {
            Toast.makeText(getApplicationContext(), "Token expired", Toast.LENGTH_SHORT).show();
            Persistent.logout(this);
        } else {
            showResult(response);
        }
    }

    @Override
    public String convertObjectToJson(Object o) {
        return null;
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

    private void showResult(StudentsApi.Response response) {
        if (response.responseCode == 0) {
            Toast.makeText(getApplicationContext(), "Cannot connect to server", Toast.LENGTH_SHORT).show();
            openLogin();
        } else {
            try {
                JSONObject json = new JSONObject(response.responseBody);
                String message;
                message = json.getString("message");

                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            } catch(Exception ex) {
                Log.d("Splash", Objects.requireNonNull(ex.getMessage()));
            }
        }
    }
}
