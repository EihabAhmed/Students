package com.bbk.students.ui.astudentbyid;

import android.util.Log;

import com.bbk.students.model.Student;

import org.json.JSONObject;

import java.util.Objects;

public class AStudentByIdMapper {
    public Student map(String result) {
        try {
            JSONObject json = new JSONObject(result);

            Student student = new Student();
            student.id = json.getInt("id");
            student.name = json.getString("firstName");
            student.age = json.getInt("age");
            student.grade = json.getInt("grade");

            return student;

        } catch(Exception ex) {
            Log.d("GetAStudentById", Objects.requireNonNull(ex.getMessage()));
            return null;
        }
    }

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
