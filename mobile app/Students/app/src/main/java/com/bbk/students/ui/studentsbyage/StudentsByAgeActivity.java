package com.bbk.students.ui.studentsbyage;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.List;
import java.util.Map;

public class StudentsByAgeActivity extends BaseActivity {
    StudentsAdapter adapter;
    ArrayList<Student> students = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_by_age);

        setupListeners();
        setupSpinner();
        setupRecyclerView();
    }

    private void setupSpinner() {
        List<String> operators = new ArrayList<>();
        operators.add("less than");
        operators.add("equals");
        operators.add("greater than");

        ArrayAdapter<String> operatorsAdapter = new ArrayAdapter<>(this , android.R.layout.simple_spinner_item, operators);
        operatorsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner operatorsSpinner = findViewById(R.id.sp_operators);
        operatorsSpinner.setAdapter(operatorsAdapter);
    }

    private void setupListeners() {
        final EditText etAge = findViewById(R.id.et_age);
        final Button btnGetStudents = findViewById(R.id.btn_get_students);

        etAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etAge.getText().toString().equals(""))
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
        final EditText etAge = findViewById(R.id.et_age);
        final Spinner operatorsSpinner = findViewById(R.id.sp_operators);

        int age = Integer.parseInt(etAge.getText().toString());
        int pos = operatorsSpinner.getSelectedItemPosition();
        String operator;
        switch (pos) {
            case 0:
                operator = "lt";
                break;
            case 1:
                operator = "eq";
                break;
            default:
                operator = "gt";
                break;
        }
        String url = getString(R.string.global_url) + "/api/students/GetWithAge?criteria=" + operator + "&age=" + age;

        StudentsApi api = new StudentsApi(this, "GetStudentsByAge");

        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Authorization", "Bearer " + Persistent.getToken());

        api.request(StudentsApi.GET, url, requestProperties, null);
    }

    @Override
    public void receiveResponse(StudentsApi.Response response) {
        StudentsByAgeMapper mapper = new StudentsByAgeMapper();
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
