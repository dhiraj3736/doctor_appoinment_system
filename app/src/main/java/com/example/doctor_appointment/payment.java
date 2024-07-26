package com.example.doctor_appointment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class payment extends AppCompatActivity {
    private RequestQueue requestQueue;
    private String doctorID;
    private String selectedDate;
    private String selectedTimeSlot;
    private String Reason;
    private String b_id;
    private String status,fee;
    TextView date, time, reasons;
    TextView cash, online;
    ImageView doctor_image;
    TextView doctor_name, doctor_specialty, nmc_no, stat, paymentdetail;

Button paymentbtn,esewa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        requestQueue = Volley.newRequestQueue(this);

        // Retrieve data from the Intent
        doctorID = getIntent().getStringExtra("doctorID");
        selectedDate = getIntent().getStringExtra("selectedDate");
        selectedTimeSlot = getIntent().getStringExtra("selectedTimeSlot");
        Reason = getIntent().getStringExtra("reason");
        b_id = getIntent().getStringExtra("b_id");
        status = getIntent().getStringExtra("status");
        fee=getIntent().getStringExtra("fee");
        Log.d("status",status);





        doctor_image = findViewById(R.id.doctor_image);
        doctor_name = findViewById(R.id.doctor_name);
        doctor_specialty = findViewById(R.id.doctor_specialty);
        nmc_no = findViewById(R.id.doctor_nmc);
        date = findViewById(R.id.appointment_date);
        time = findViewById(R.id.appointment_time);
        reasons = findViewById(R.id.appointment_reason);
        stat = findViewById(R.id.status);
        paymentdetail = findViewById(R.id.paymentdetail);

        esewa=findViewById(R.id.esewabtn);

        date.setText(selectedDate);
        time.setText("||" + selectedTimeSlot);
        reasons.setText(Reason);
//        stat.setText(status);


        // Parsing selectedDate to Date object
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date appointmentDate = null;
        try {
            appointmentDate = dateFormat.parse(selectedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Assuming getCurrentDate() returns a Date object representing the current date
        if ("1".equals(status) && appointmentDate != null && appointmentDate.before(getCurrentDate())) {
            stat.setText("accepted");
            esewa.setVisibility(View.GONE);

        } else if ( "1".equals(status) && appointmentDate != null && appointmentDate.after(getCurrentDate())) {
            stat.setText("accepted");
            esewa.setVisibility(View.VISIBLE);
        } else if ( "2".equals(status) && appointmentDate != null && appointmentDate.after(getCurrentDate())) {
            stat.setText("accepted");
            paymentdetail.setText("payment complete");
            esewa.setVisibility(View.GONE);
        } else if ( "2".equals(status) && appointmentDate != null && appointmentDate.before(getCurrentDate())) {
            stat.setText("accepted");
            paymentdetail.setText("payment complete");
            esewa.setVisibility(View.GONE);
        }else if ("0".equals(status)) {
            stat.setText("Not accepted");
           esewa.setVisibility(View.GONE);  // Ensure visibility is set if needed
        // Ensure visibility is set if needed
        }

        getdoctorinfo(String.valueOf(doctorID));



    }

    public Date getCurrentDate() {
        return new Date();
    }

    public void getdoctorinfo(String doctorId) {
        String url = Endpoints.select_doctor_info;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            String result = object.getString("result");
                            if (result.equals("success")) {
                                String name = object.getString("name");
                                String specialist = object.getString("specialist");
                                String nmc = object.getString("nmc_no");
                                String imageUrl = object.getString("image");

                                Glide.with(payment.this).load(imageUrl).circleCrop().into(doctor_image);
                                doctor_name.setText(name);
                                doctor_specialty.setText(specialist);
                                nmc_no.setText(nmc);
                            } else {
                                Log.e("Error", "Doctor ID is null or empty");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("d_id", doctorId);

                return data;
            }
        };

        requestQueue.add(stringRequest);
    }

}


