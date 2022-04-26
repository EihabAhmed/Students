package com.bbk.students.ui.astudentbyid;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.bbk.students.ui.login.LoginActivity;

import java.util.HashMap;
import java.util.Map;

public class AStudentByIdActivity extends BaseActivity {
    Student student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_astudent_by_id);

        setupListeners();
    }

    private void setupListeners() {
        final EditText etID = findViewById(R.id.et_id);
        final Button btnGetInfo = findViewById(R.id.btn_get_info);

        etID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etID.getText().toString().equals(""))
                    btnGetInfo.setEnabled(false);
                else
                    btnGetInfo.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnGetInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeRequest();
            }
        });
    }

    @Override
    public void makeRequest() {
        final EditText etID = findViewById(R.id.et_id);

        int id = Integer.parseInt(etID.getText().toString());

        StudentsApi api = new StudentsApi(this, "GetAStudentById");
        String url = baseUrl + "/api/students?id=" + id;

        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Authorization", "Bearer " + Persistent.getToken());

        api.request(StudentsApi.GET, url, requestProperties, null);
    }

    @Override
    public void receiveResponse(StudentsApi.Response response) {
        AStudentByIdMapper mapper = new AStudentByIdMapper();
        if (response.responseCode == 0) {
            Toast.makeText(getApplicationContext(), "Cannot connect to server", Toast.LENGTH_LONG).show();
        }
        else if (response.responseCode == 200) {
            student = mapper.map(response.responseBody);
            showResult();
        } else if (response.responseCode == 401) {
            Toast.makeText(getApplicationContext(), "Token expired", Toast.LENGTH_SHORT).show();
            Persistent.logout(this);
        } else {
            String message = mapper.mapErrorMessage(response.responseBody);
            showError(message);
        }
    }

    @Override
    public String convertObjectToJson(Object o) {
        return null;
    }

    public void showError(String responseMessage) {
        TextView tvNotFound = findViewById(R.id.tv_not_found);
        View view = findViewById(R.id.vh_student_info);
        tvNotFound.setText(responseMessage);
        tvNotFound.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    public void showResult() {
        TextView tvId = findViewById(R.id.tv_id);
        TextView tvName = findViewById(R.id.tv_name);
        TextView tvAge = findViewById(R.id.tv_age);
        TextView tvGrade = findViewById(R.id.tv_grade);

        tvId.setText(Integer.toString(student.id));
        tvName.setText(student.name);
        tvAge.setText(Integer.toString(student.age));
        tvGrade.setText(Integer.toString(student.grade));

        TextView tvNotFound = findViewById(R.id.tv_not_found);
        View view = findViewById(R.id.vh_student_info);
        tvNotFound.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
    }
}
