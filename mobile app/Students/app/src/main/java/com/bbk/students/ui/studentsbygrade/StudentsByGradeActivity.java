package com.bbk.students.ui.studentsbygrade;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bbk.students.R;
import com.bbk.students.adapter.StudentsAdapter;
import com.bbk.students.common.Persistent;
import com.bbk.students.model.Student;
import com.bbk.students.network.StudentsApi;
import com.bbk.students.ui.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudentsByGradeActivity extends BaseActivity {
    StudentsAdapter adapter;
    ArrayList<Student> students = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_by_grade);

        setupListeners();
        setupRecyclerView();
    }

    private void setupListeners() {
        final EditText etGrade = findViewById(R.id.et_grade);
        final Button btnGetStudents = findViewById(R.id.btn_get_students);

        etGrade.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etGrade.getText().toString().equals(""))
                    btnGetStudents.setEnabled(false);
                else
                    btnGetStudents.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnGetStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView recyclerView = findViewById(R.id.rv_students);
                adapter = new StudentsAdapter(getApplicationContext(), new ArrayList<Student>());
                recyclerView.setAdapter(adapter);

                makeRequest();
            }
        });
    }

    @Override
    public void makeRequest() {
        final EditText etGrade = findViewById(R.id.et_grade);

        int grade = Integer.parseInt(etGrade.getText().toString());
        String url = getString(R.string.global_url) + "/api/students/GetWithGrade?grade=" + grade;

        StudentsApi api = new StudentsApi(this, "GetStudentsByGrade");

        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Authorization", "Bearer " + Persistent.getToken());

        api.request(StudentsApi.GET, url, requestProperties, null);
    }

    @Override
    public void receiveResponse(StudentsApi.Response response) {
        StudentsByGradeMapper mapper = new StudentsByGradeMapper();
        if (response.responseCode == 0) {
            Toast.makeText(getApplicationContext(), "Cannot connect to server", Toast.LENGTH_LONG).show();
        }
        else if (response.responseCode == 200) {
            students = mapper.map(response.responseBody);
            showResult();
        } else if (response.responseCode == 401) {
            Toast.makeText(getApplicationContext(), "Token expired", Toast.LENGTH_SHORT).show();
            Persistent.logout(this);
        } else {
            String message = mapper.mapErrorMessage(response.responseBody);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public String convertObjectToJson(Object o) {
        return null;
    }

    private void setupRecyclerView() {

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rv_students);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void showResult() {
        RecyclerView recyclerView = findViewById(R.id.rv_students);
        adapter = new StudentsAdapter(this, students);
        recyclerView.setAdapter(adapter);
    }
}
