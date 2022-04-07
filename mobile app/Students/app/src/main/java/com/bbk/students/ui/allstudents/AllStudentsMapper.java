package com.bbk.students.ui.allstudents;

import android.util.Log;

import com.bbk.students.model.Student;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class AllStudentsMapper {
    private ArrayList<Student> students = new ArrayList<>();

    public ArrayList<Student> map(String result) {
        try {
            JSONObject json = new JSONObject(result);

            JSONArray jsonStudents = json.getJSONArray("students");

            JSONObject jsonStudent;
            for (int i = 0; i < jsonStudents.length(); i++) {
                jsonStudent = jsonStudents.getJSONObject(i);
                Student student = new Student();
                student.id = jsonStudent.getInt("id");
                student.name = jsonStudent.getString("firstName");
                student.age = jsonStudent.getInt("age");
                student.grade = jsonStudent.getInt("grade");
                students.add(student);
            }

//            JSONArray json = new JSONArray(result);
//
//            JSONObject jsonStudent;
//            for (int i = 0; i < json.length(); i++) {
//                jsonStudent = json.getJSONObject(i);
//                Student student = new Student();
//                student.id = jsonStudent.getInt("id");
//                student.name = jsonStudent.getString("firstName");
//                student.age = jsonStudent.getInt("age");
//                student.grade = jsonStudent.getInt("grade");
//                students.add(student);
//            }

            return students;

        } catch(Exception ex) {
            Log.d("GetAllStudents", Objects.requireNonNull(ex.getMessage()));
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
