package com.bbk.students.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bbk.students.R;
import com.bbk.students.model.Student;

import java.util.ArrayList;

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.ViewHolder> {
    private ArrayList<Student> mData;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    public StudentsAdapter(Context context, ArrayList<Student> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.view_holder_student_info, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.id.setText(Integer.valueOf(mData.get(position).id).toString());
        holder.name.setText(mData.get(position).name);
        holder.age.setText(Integer.valueOf(mData.get(position).age).toString());
        holder.grade.setText(Integer.valueOf(mData.get(position).grade).toString());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView id;
        TextView name;
        TextView age;
        TextView grade;

        ViewHolder(View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.tv_id);
            name = itemView.findViewById(R.id.tv_name);
            age = itemView.findViewById(R.id.tv_age);
            grade = itemView.findViewById(R.id.tv_grade);
        }
    }
}
