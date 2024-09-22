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
import com.example.doctor_appointment.Adapter.ReportAdaptor;
import com.example.doctor_appointment.Adapter.doctorAdaptor;
import com.example.doctor_appointment.model.doctorList;
import com.example.doctor_appointment.model.reportList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewReport extends AppCompatActivity {

    ListView listview;

    ReportAdaptor adaptor;
  reportList report;
ImageView back;

    public static ArrayList<reportList> arrayListreport=new ArrayList<>();
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewreport);

        listview=findViewById(R.id.report_list);
        adaptor=new ReportAdaptor(this,arrayListreport);
        listview.setAdapter(adaptor);



        requestQueue = Volley.newRequestQueue(this);



        retrive_report_detail();

        back=findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void retrive_report_detail(){
        String url = Endpoints.selectReport;
        SessionManagement sessionManagement = new SessionManagement(ViewReport.this);
        int user_id = sessionManagement.getSession();
        arrayListreport.clear();
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                arrayListreport.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result=jsonObject.getString("result");

                    if (result.equals("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            String doctor_name = obj.getString("doctor");
                            String report1 = obj.getString("report");
                            String formattedDate = obj.getString("formatted_date"); // Get the formatted date

                            report = new reportList(doctor_name, report1, formattedDate);


                            arrayListreport.add(report);
                        }
                        adaptor.notifyDataSetChanged();
                    }else {
                        Toast.makeText(ViewReport.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                    Toast.makeText(ViewReport.this, "No Repoet found ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ViewReport.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("user_id", String.valueOf(user_id));






                return data;
            }
        };


        requestQueue.add(request);
    }
}