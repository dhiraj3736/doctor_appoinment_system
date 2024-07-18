package com.example.doctor_appointment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.doctor_appointment.Adapter.scheduleAdapter;
import com.example.doctor_appointment.model.bookingList;
import com.example.doctor_appointment.model.comment_info;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleFragment extends Fragment {

    private ListView listView;
    private scheduleAdapter adapter;

    bookingList booking;
    public static ArrayList<bookingList> bookingarraylist = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        // Initialize RecyclerView
        listView = view.findViewById(R.id.listview);

        adapter = new scheduleAdapter(getContext(), bookingarraylist,true);
        listView.setAdapter(adapter);

        // Initialize TabLayout
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Upcoming"));
        tabLayout.addTab(tabLayout.newTab().setText("Completed"));


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Handle tab selection
                switch (tab.getPosition()) {
                    case 0: // Upcoming

                        loadUpcomingAppointments();
                        break;
                    case 1: // Completed
                        loadCompletedAppointments();
                        break;

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });


        loadUpcomingAppointments();

        return view;
    }

    private void loadUpcomingAppointments() {



        String url = Endpoints.get_upcoming_schedule;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        bookingarraylist.clear();

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        bookingarraylist.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String result = jsonObject.getString("result");

                            if (result.equals("success")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    String name = obj.getString("doctor_name");
                                    String specialist = obj.getString("specialist");
                                    String date= obj.getString("date");
                                    String time= obj.getString("time");
                                    String image=obj.getString("image");
                                    String reason= obj.getString("reason");
                                    String status=obj.getString("status");
                                    int b_id=obj.getInt("b_id");
                                    int d_id=obj.getInt("d_id");
                                    Log.d("img",image);
                                    booking=new bookingList(d_id,b_id,name,specialist,date,time,image,reason,status);
                                    bookingarraylist.add(booking);
                                }
                                adapter.notifyDataSetChanged();
                                adapter.setShowButtons(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "JSON parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NetworkError) {
                            Toast.makeText(getContext(), "Network error. Check your internet connection.", Toast.LENGTH_SHORT).show();
                        }
                        Log.e("Volley Error", "Error: " + error.getMessage());
                    }
                }) {

            SessionManagement sessionManagement=new SessionManagement(getContext());
            int user_id= sessionManagement.getSession();
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("u_id", String.valueOf(user_id));
                return data;
            }
        };

        requestQueue.add(request);
    }

    private void loadCompletedAppointments() {


        String url = Endpoints.get_completed_schedule;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        bookingarraylist.clear();

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        bookingarraylist.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String result = jsonObject.getString("result");

                            if (result.equals("success")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    String name = obj.getString("doctor_name");
                                    String specialist = obj.getString("specialist");
                                    String date= obj.getString("date");
                                    String time= obj.getString("time");
                                    String image=obj.getString("image");
                                    String reason=obj.getString("reason");
                                    String status=obj.getString("status");
                                    int b_id=obj.getInt("b_id");
                                    int d_id=obj.getInt("d_id");

                                    booking=new bookingList(d_id,b_id,name,specialist,date,time,image,reason,status);
                                    bookingarraylist.add(booking);
                                }
                                adapter.notifyDataSetChanged();
                                adapter.setShowButtons(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "JSON parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NetworkError) {
                            Toast.makeText(getContext(), "Network error. Check your internet connection.", Toast.LENGTH_SHORT).show();
                        }
                        Log.e("Volley Error", "Error: " + error.getMessage());
                    }
                }) {

            SessionManagement sessionManagement=new SessionManagement(getContext());
            int user_id= sessionManagement.getSession();
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("u_id", String.valueOf(user_id));
                return data;
            }
        };

        requestQueue.add(request);
    }


}
