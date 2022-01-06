package com.bbk.students.ui.createstudent;

import android.util.Log;

import com.bbk.students.model.Student;

import org.json.JSONObject;

import java.util.Objects;

public class CreateStudentMapper {
    String mapErrorMessage(String result) {
        try {
            JSONObject json = new JSONObject(result);
            return json.getString("message");
        } catch(Exception ex) {
            Log.d("GetStudentsByAge", Objects.requireNonNull(ex.getMessage()));
            return null;
        }
    }
}
