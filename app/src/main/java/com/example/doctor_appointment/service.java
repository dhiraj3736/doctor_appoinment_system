package com.example.doctor_appointment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.doctor_appointment.Adapter.doctorAdaptor;
import com.example.doctor_appointment.model.doctorList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class service extends AppCompatActivity {

    ListView listview;

    doctorAdaptor adaptor;

    public static ArrayList<doctorList> arrayListdoctor=new ArrayList<>();

    Button see_more_button;
    doctorList doctor;

    TextView service_title, service_description,service;
    ImageView service_image,back;
    private RequestQueue requestQueue;
    private String s_id;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        listview=findViewById(R.id.doctor_list);
        adaptor=new doctorAdaptor(this,arrayListdoctor);
        listview.setAdapter(adaptor);

        Intent intent = getIntent();
        s_id = intent.getStringExtra("service_id");
        String service_name = intent.getStringExtra("service_name");


        requestQueue = Volley.newRequestQueue(this);
        back = findViewById(R.id.back);
//        service_title = findViewById(R.id.serice_title);




        service_description=findViewById(R.id.description);
        service_image=findViewById(R.id.service_image);
        service=findViewById(R.id.service_name);
        see_more_button=findViewById(R.id.see_more_button);

//        service_title.setText(service_name);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        retrive_service_info();
        retrive_doctor_info();
    }

    public void retrive_service_info() {
        String url = Endpoints.get_service_info;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result=jsonObject.getString("result");

                    if (result.equals("success")){
                        String discription=jsonObject.getString("discription");
                        String imageurl=jsonObject.getString("image");
                        String service_name=jsonObject.getString("service");

                        int maxLength = 75; // Adjust this to your desired max length
                        if (discription.length() > maxLength) {
                            String truncatedDescription = discription.substring(0, maxLength) + "...";
                            service_description.setText(truncatedDescription);
                            see_more_button.setVisibility(View.VISIBLE); // Show "See More" button

                            // Click listener for "See More" button
                            see_more_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    service_description.setText(discription); // Show full description
                                    see_more_button.setVisibility(View.GONE); // Hide "See More" button after expanding
                                }
                            });
                        } else {
                            // If description is shorter than maxLength, display full description
                            service_description.setText(discription);
                        }

                        service.setText(service_name);
                        Glide.with(service.this).load(imageurl).into(service_image);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error response

            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();

                data.put("s_id",s_id);
                return data;
            }
        };

        requestQueue.add(stringRequest);
    }


    public void retrive_doctor_info(){
        String url = Endpoints.get_doctor_info;
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

                            doctor = new doctorList(d_id,name, specialist, qualification, experience, image);
                            arrayListdoctor.add(doctor);
                        }
                        adaptor.notifyDataSetChanged();
                    }else {
                        Toast.makeText(service.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                    Toast.makeText(service.this, "No doctors found for this service", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(service.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();

                data.put("s_id",s_id);
                Log.d("service_id", s_id);
                return data;
            }
        };

        requestQueue.add(request);
    }
}