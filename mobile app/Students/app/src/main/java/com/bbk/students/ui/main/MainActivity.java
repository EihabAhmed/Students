package com.bbk.students.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bbk.students.R;
import com.bbk.students.common.Persistent;
import com.bbk.students.ui.allstudents.AllStudentsActivity;
import com.bbk.students.ui.astudentbyid.AStudentByIdActivity;
import com.bbk.students.ui.createstudent.CreateStudentActivity;
import com.bbk.students.ui.deletestudent.DeleteStudentActivity;
import com.bbk.students.ui.login.LoginActivity;
import com.bbk.students.ui.modifystudent.ModifyStudentActivity;
import com.bbk.students.ui.studentsbyage.StudentsByAgeActivity;
import com.bbk.students.ui.studentsbygrade.StudentsByGradeActivity;

public class MainActivity extends AppCompatActivity {
    MainActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;

        setupClickListeners();

        checkAdmin();

        ((TextView)findViewById(R.id.tv_welcome)).setText("Welcome " + Persistent.getUserName());
    }

    public void checkAdmin() {
        if (!Persistent.isAdmin()) {
            TextView btnCreateStudent = findViewById(R.id.btn_create_student);
            TextView btnUpdateStudent = findViewById(R.id.btn_update_student);
            TextView btnDeleteStudent = findViewById(R.id.btn_delete_student);

            btnCreateStudent.setEnabled(false);
            btnUpdateStudent.setEnabled(false);
            btnDeleteStudent.setEnabled(false);

            btnCreateStudent.setBackground(getResources().getDrawable(R.drawable.background_darkgray_color_border));
            btnUpdateStudent.setBackground(getResources().getDrawable(R.drawable.background_darkgray_color_border));
            btnDeleteStudent.setBackground(getResources().getDrawable(R.drawable.background_darkgray_color_border));
        }
    }

    private void setupClickListeners() {
        findViewById(R.id.btn_get_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), AllStudentsActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_get_with_age).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), StudentsByAgeActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_get_with_grade).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), StudentsByGradeActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_get_with_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), AStudentByIdActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_create_student).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), CreateStudentActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_update_student).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ModifyStudentActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_delete_student).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), DeleteStudentActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Persistent.setToken("", activity);
                Persistent.setAdmin(false);
                Persistent.setUserName("", activity);
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
