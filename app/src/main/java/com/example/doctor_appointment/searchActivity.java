package com.example.doctor_appointment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.example.doctor_appointment.Adapter.doctorAdaptor;
import com.example.doctor_appointment.Adapter.doctor_Adapter_for_userdashboard;
import com.example.doctor_appointment.model.doctorList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class searchActivity extends AppCompatActivity {

    private ListView doctorListView;
    private ArrayList<doctorList> doctorList = new ArrayList<>();
    private doctor_Adapter_for_userdashboard doctoradapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        doctorListView = findViewById(R.id.doctor_list);
        doctoradapter = new doctor_Adapter_for_userdashboard(this, doctorList);
        doctorListView.setAdapter(doctoradapter);

        // Retrieve the search results from the intent
        Intent intent = getIntent();
        String doctorsData = intent.getStringExtra("doctors_data");

        try {
            JSONArray doctorArray = new JSONArray(doctorsData);
            for (int i = 0; i < doctorArray.length(); i++) {
                JSONObject doctorObject = doctorArray.getJSONObject(i);
                String d_id = doctorObject.getString("d_id");
                String name = doctorObject.getString("name");
                String specialist = doctorObject.getString("specialist");
                String qualification = doctorObject.getString("qualification");
                String experience = doctorObject.getString("experiance");
                String image = doctorObject.getString("image");
                String rating_value = doctorObject.getString("rating_value");

                doctorList doctor = new doctorList(d_id, name, specialist, qualification, experience, image, rating_value);
                doctorList.add(doctor);
            }

            doctoradapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
