package com.example.doctor_appointment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class doctor_profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        Intent intent = getIntent();
        String doctor_id = intent.getStringExtra("doctor_id");
        Log.d("doc_id", doctor_id);
    }
}