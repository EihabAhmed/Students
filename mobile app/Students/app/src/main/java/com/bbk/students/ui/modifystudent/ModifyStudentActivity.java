package com.bbk.students.ui.modifystudent;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bbk.students.R;
import com.bbk.students.common.Persistent;
import com.bbk.students.model.Student;
import com.bbk.students.network.StudentsApi;
import com.bbk.students.ui.BaseActivity;

import java.util.HashMap;
import java.util.Map;

public class ModifyStudentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_student);

        setupListeners();
    }

    private void setupListeners() {
        final EditText etId = findViewById(R.id.et_id);
        final EditText etName = findViewById(R.id.et_name);
        final EditText etAge = findViewById(R.id.et_age);
        final EditText etGrade = findViewById(R.id.et_grade);
        final Button btnModifyStudent = findViewById(R.id.btn_modify_student);

        etId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etId.getText().toString().equals("") || etName.getText().toString().equals("") ||
                        etAge.getText().toString().equals("") || etGrade.getText().toString().equals(""))
                    btnModifyStudent.setEnabled(false);
                else
                    btnModifyStudent.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etId.getText().toString().equals("") || etName.getText().toString().equals("") ||
                        etAge.getText().toString().equals("") || etGrade.getText().toString().equals(""))
                    btnModifyStudent.setEnabled(false);
                else
                    btnModifyStudent.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etId.getText().toString().equals("") || etName.getText().toString().equals("") ||
                        etAge.getText().toString().equals("") || etGrade.getText().toString().equals(""))
                    btnModifyStudent.setEnabled(false);
                else
                    btnModifyStudent.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etGrade.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etId.getText().toString().equals("") || etName.getText().toString().equals("") ||
                        etAge.getText().toString().equals("") || etGrade.getText().toString().equals(""))
                    btnModifyStudent.setEnabled(false);
                else
                    btnModifyStudent.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnModifyStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeRequest();
            }
        });
    }

    @Override
    public void makeRequest() {
        final EditText etId = findViewById(R.id.et_id);
        final EditText etName = findViewById(R.id.et_name);
        final EditText etAge = findViewById(R.id.et_age);
        final EditText etGrade = findViewById(R.id.et_grade);
        final TextView tvStatus = findViewById(R.id.tv_status);

        tvStatus.setText("");
        Student student = new Student();
        student.id = Integer.parseInt(etId.getText().toString());
        student.name = etName.getText().toString();
        student.age = Integer.parseInt(etAge.getText().toString());
        student.grade = Integer.parseInt(etGrade.getText().toString());
        etId.setText("");
        etName.setText("");
        etAge.setText("");
        etGrade.setText("");

        StudentsApi api = new StudentsApi(this, "ModifyStudent");
        String url = getString(R.string.global_url) + "/api/students?id=" + student.id;
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Content-Type", "application/json");
        requestProperties.put("Accept", "application/json");
        requestProperties.put("Authorization", "Bearer " + Persistent.getToken());
        String body = convertObjectToJson(student);
        api.request(StudentsApi.PUT, url, requestProperties, body);
    }

    @Override
    public void receiveResponse(StudentsApi.Response response) {
        ModifyStudentMapper mapper = new ModifyStudentMapper();

        if (response.responseCode == 0) {
            Toast.makeText(getApplicationContext(), "Cannot connect to server", Toast.LENGTH_LONG).show();
        }
        else if (response.responseCode == 200) {
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
        Student student = (Student)o;
        String json = "{";
        //json += "id:" + student.id;
        json += "firstName:\"" + student.name + "\"";
        json += ",age:" + student.age;
        json += ",grade:" + student.grade;
        json += "}";

        return json;
    }

    @SuppressLint("SetTextI18n")
    private void showResult() {
        TextView tvStatus = findViewById(R.id.tv_status);
        tvStatus.setText("Done");
    }
}
