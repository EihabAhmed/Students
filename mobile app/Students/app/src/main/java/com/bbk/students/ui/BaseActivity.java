package com.bbk.students.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bbk.students.R;
import com.bbk.students.network.StudentsApi;

public abstract class BaseActivity extends AppCompatActivity {
    protected String baseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //baseUrl = getString(R.string.local_url);
        baseUrl = getString(R.string.global_url);
    }

    public abstract void makeRequest();
    public abstract void receiveResponse(StudentsApi.Response response);
    public abstract String convertObjectToJson(Object o);
}