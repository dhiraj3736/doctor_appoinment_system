package com.example.doctor_appointment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.doctor_appointment.Adapter.doctorAdaptor;
import com.example.doctor_appointment.model.doctorList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class See_all_doctor extends AppCompatActivity {

    ListView listview;

    doctorAdaptor adaptor;
    doctorList doctor;

    ImageView service_image,back;
    public static ArrayList<doctorList> arrayListdoctor=new ArrayList<>();
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_all_doctor);
        back = findViewById(R.id.back);
        listview=findViewById(R.id.doctor_list);
        adaptor=new doctorAdaptor(this,arrayListdoctor);
        listview.setAdapter(adaptor);



        requestQueue = Volley.newRequestQueue(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

        retrive_doctor_info();

    }

    public void retrive_doctor_info(){
        String url = Endpoints.doctor_info_for_doctor_list;
        arrayListdoctor.clear();
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                arrayListdoctor.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result=jsonObject.getString("result");

                    if (result.equals("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String d_id= obj.getString("d_id");
                            String name = obj.getString("name");
                            String specialist = obj.getString("specialist");
                            String qualification = obj.getString("qualification");
                            String experience = obj.getString("experiance");
                            String image = obj.getString("image");
                            String rating_value = obj.getString("rating_value");


                            doctor = new doctorList(d_id,name, specialist, qualification, experience, image,rating_value);
                            arrayListdoctor.add(doctor);
                        }
                        adaptor.notifyDataSetChanged();
                    }else {
                        Toast.makeText(See_all_doctor.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                    Toast.makeText(See_all_doctor.this, "No doctors found for this service", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(See_all_doctor.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        requestQueue.add(request);
    }
}