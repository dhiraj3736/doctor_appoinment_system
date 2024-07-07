package com.example.doctor_appointment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fragment_about extends Fragment {
    private String doctorId;
    private RequestQueue requestQueue;
    TextView des, quli, nmc;
    ListView listView;
    String service_id;

    public fragment_about() {
        // Required empty public constructor
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_fragment_about, container, false);

        des = view.findViewById(R.id.des);
        quli = view.findViewById(R.id.quli);
        nmc = view.findViewById(R.id.nmc);
        listView = view.findViewById(R.id.listview); // Ensure listView is initialized
        requestQueue = Volley.newRequestQueue(requireContext());

        retrieveDoctorInfo(); // Fetch doctor info and call retrieve_service_name accordingly

        return view;
    }

    private void retrieveDoctorInfo() {
        String url = Endpoints.get_doctor_info_for_profile;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                String result = obj.getString("result");

                if (result.equals("success")) {
                    JSONObject doctorData = obj.getJSONObject("data");
                    String name = doctorData.getString("name");
                    String specialist = doctorData.getString("specialist");
                    String experience = doctorData.getString("experiance");
                    String fromtime = doctorData.getString("starttime");
                    String totime = doctorData.getString("endtime");
                    String image = doctorData.getString("image");
                    String nmc_no = doctorData.getString("nmc_no");
                    String description = doctorData.getString("description");
                    String qualification = doctorData.getString("qualification");
                    service_id = doctorData.getString("service_id");

                    des.setText(description);
                    quli.setText(qualification);
                    nmc.setText(nmc_no);

                    Log.d("service_id", service_id); // Log the service_id

                    // Fetch service name using the retrieved service_id
                    if (service_id != null && !service_id.isEmpty()) {
                        retrieve_service_name(service_id);
                    } else {
                        Log.e("Service ID Error", "Service ID is null or empty");
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("VolleyError", error.toString());
            Toast.makeText(getContext(), "Server error. Please try again later.", Toast.LENGTH_LONG).show();
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

    private void retrieve_service_name(String serviceId) {
        String url = Endpoints.service_name;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                String result = obj.getString("result");

                if (result.equals("success")) {
                    JSONArray serviceDataArray = obj.getJSONArray("data");

                    // Create a list to hold the service names
                    List<String> serviceNames = new ArrayList<>();

                    for (int i = 0; i < serviceDataArray.length(); i++) {
                        JSONObject serviceData = serviceDataArray.getJSONObject(i);
                        String service_name = serviceData.getString("service_name");
                        serviceNames.add(service_name);
                    }

                    // Create ArrayAdapter to populate ListView with service names
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                            R.layout.custom_list_item_service, android.R.id.text1, serviceNames);

                    // Set the ArrayAdapter to the ListView
                    listView.setAdapter(adapter);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("VolleyError", error.toString());
            Toast.makeText(getContext(), "", Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("service_id", serviceId);
                return data;
            }
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        requestQueue.add(stringRequest);
    }
}
