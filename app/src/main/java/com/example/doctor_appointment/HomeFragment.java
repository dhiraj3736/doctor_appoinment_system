package com.example.doctor_appointment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.doctor_appointment.Adapter.DaysAdapter;
import com.example.doctor_appointment.Adapter.doctorAdaptor;
import com.example.doctor_appointment.Adapter.doctor_Adapter_for_userdashboard;
import com.example.doctor_appointment.model.doctorList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    private String user_id;
    private String user_info, user_image;

    TextView name,see_all_doctor;
    ImageView image;
    EditText searchInput;
    ImageView searchButton;
    LinearLayout servicesContainer;
    private RequestQueue requestQueue;

    private RecyclerView rvDays;
    private TextView tvMonthYear;

    CustomGridView gridView;

    doctor_Adapter_for_userdashboard adapter;

    public static ArrayList<doctorList> arrayListdoctor=new ArrayList<>();

    doctorList doctor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {





        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        name = view.findViewById(R.id.name);
        image = view.findViewById(R.id.user_image);
        searchInput = view.findViewById(R.id.search_input);
        searchButton = view.findViewById(R.id.search_button);


        servicesContainer = view.findViewById(R.id.services_container);

        rvDays = view.findViewById(R.id.rv_days);
        tvMonthYear = view.findViewById(R.id.tv_month_year);

        requestQueue = Volley.newRequestQueue(requireContext());

        see_all_doctor=view.findViewById(R.id.see_all_doctor);
        gridView=view.findViewById(R.id.gridView);
        adapter=new doctor_Adapter_for_userdashboard(getActivity(),arrayListdoctor);
        gridView.setAdapter(adapter);

        see_all_doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), See_all_doctor.class);
                startActivity(intent);


            }
        });

        // Setup days RecyclerView
        rvDays.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        DaysAdapter daysAdapter = new DaysAdapter(getDaysOfMonth(), day ->
                Toast.makeText(requireContext(), "Clicked on day: " + day, Toast.LENGTH_SHORT).show());
        rvDays.setAdapter(daysAdapter);

        // Set current month and year
        Calendar calendar = Calendar.getInstance();
        String monthYear = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.getTime());
        tvMonthYear.setText(monthYear);


        HorizontalScrollView horizontalScrollView = view.findViewById(R.id.horizontal_scroll_view);
        horizontalScrollView.setHorizontalScrollBarEnabled(false);

        retrivedata();
        retriveservice();
        retrive_doctor_info();





        // Load a static image using Glide
//        String staticImageUrl = "http://192.168.1.9:8080/storage/uploads/1711802622-doctor.jpg";
//        Glide.with(this).load(staticImageUrl).into(image);

        return view;
    }




    private List<String> getDaysOfMonth() {
        List<String> days = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_MONTH);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Add current day to the end of the month
        for (int i = today; i <= daysInMonth; i++) {
            days.add(String.valueOf(i));
        }
        // Wrap around and add days from the beginning to just before today
        for (int i = 1; i < today; i++) {
            days.add(String.valueOf(i));
        }
        return days;
    }


    public void retrivedata() {
        String url = Endpoints.get_user_info;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");

                    if (result.equals("success")) {
                        String fullname = jsonObject.getString("Fullname");
                        String imageUrl = jsonObject.getString("image");

                        name.setText("Hi,"+fullname);

                        // Load the dynamic image from server response using Glide
                        Glide.with(requireContext()).load(imageUrl).circleCrop().into(image);

                        Log.d("HomeFragment", "Fullname: " + fullname);
                        Log.d("HomeFragment", "Image URL: " + imageUrl);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error response
                Log.e("HomeFragment", "Error: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SessionManagement sessionManagement = new SessionManagement(getActivity());
                int user_id = sessionManagement.getSession();
                Map<String, String> data = new HashMap<>();
                data.put("u_id", String.valueOf(user_id));
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void retriveservice() {
        String url = Endpoints.get_service_name;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");

                    if (result.equals("success")) {
                        JSONArray serviceArray = jsonObject.getJSONArray("services");

                        servicesContainer.removeAllViews();
                        for (int i = 0; i < serviceArray.length(); i++) {
                            JSONObject object = serviceArray.getJSONObject(i);
                            String serviceName = object.getString("service");
                            String s_id=object.getString("s_id");

                            View serviceView = LayoutInflater.from(getContext()).inflate(R.layout.service_text_view, servicesContainer, false);

                           //for scrollview
                            TextView serviceNameTextView = serviceView.findViewById(R.id.service_name);


                            serviceNameTextView.setText(serviceName);

                            // Add the service view to the container
                            servicesContainer.addView(serviceView);
                            Log.d("services", "service: " + serviceName);

                        serviceView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent=new Intent(getActivity(),service.class);
                                intent.putExtra("service_id", s_id);
                                intent.putExtra("service_name", serviceName);
                                startActivity(intent);



                            }
                        });

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error response
                Log.e("HomeFragment", "Error: " + error.getMessage());
            }
        });
        requestQueue.add(stringRequest);
    }
    public void retrive_doctor_info(){
        String url = Endpoints.get_doctor_info_for_userdashboard;
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
                        adapter.notifyDataSetChanged();
                    }else {
                        Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                    Toast.makeText(getContext(), "No doctors found for this service", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        requestQueue.add(request);
    }


}
