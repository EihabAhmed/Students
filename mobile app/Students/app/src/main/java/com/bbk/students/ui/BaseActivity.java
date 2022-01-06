package com.bbk.students.ui;

import androidx.appcompat.app.AppCompatActivity;

import com.bbk.students.network.StudentsApi;

public abstract class BaseActivity extends AppCompatActivity {
    public abstract void makeRequest();
    public abstract void receiveResponse(StudentsApi.Response response);
    public abstract String convertObjectToJson(Object o);
}
