package com.bbk.students.common;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import com.bbk.students.ui.login.LoginActivity;

import static android.content.Context.MODE_PRIVATE;

public class Persistent {
    private static String token = "";
    private static boolean admin = false;
    private static String userName = "";

    public static String getToken() {
        return token;
    }

    public static void setToken(String token, Activity activity) {
        Persistent.token = token;

        SharedPreferences prefs = activity.getSharedPreferences("Students", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("token", token);
        editor.apply();
    }

    public static boolean isAdmin() {
        return admin;
    }

    public static void setAdmin(boolean admin) {
        Persistent.admin = admin;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName, Activity activity) {
        Persistent.userName = userName;

        SharedPreferences prefs = activity.getSharedPreferences("Students", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", userName);
        editor.apply();
    }

    public static void logout(Activity activity) {
        setToken("", activity);
        setAdmin(false);
        setUserName("", activity);
        Intent intent = new Intent(activity.getBaseContext(), LoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }
}
