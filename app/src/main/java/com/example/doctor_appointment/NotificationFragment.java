package com.example.doctor_appointment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.doctor_appointment.Adapter.notificationAdapter;
import com.example.doctor_appointment.model.notification_list;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationFragment extends Fragment {

    private RequestQueue requestQueue;
    private notificationAdapter adapter;
    private ListView listView;
    public static ArrayList<notification_list> notification_lists = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        // Initialize session management
        SessionManagement sessionManagement = new SessionManagement(getContext());
        int u_id = sessionManagement.getSession();

        // Initialize RequestQueue
        requestQueue = Volley.newRequestQueue(getContext());

        // Initialize ListView and Adapter
        listView = view.findViewById(R.id.notification);
        adapter = new notificationAdapter(getContext(), notification_lists);
        listView.setAdapter(adapter);

        // Fetch notifications
        getNotification(u_id);

        return view;
    }

    public void getNotification(int u_id) {
        String url = Endpoints.getnotigication;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            String result = object.getString("result");
                            if (result.equals("success")) {
                                JSONArray jsonArray = object.getJSONArray("notificationlist");

                                // Clear the existing list to avoid duplication
                                notification_lists.clear();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject notification = jsonArray.getJSONObject(i);
                                    String name = notification.optString("name");

                                    String doctor = notification.optString("doctor");
                                    String b_id=notification.optString("b_id");

                                    String date = notification.getString("date");
                                    String notification_id = notification.getString("notification_id");

                                    String read_at=notification.getString("read_at");

                                    // Add the new notification to the list
                                    notification_list list = new notification_list(name,doctor, date, notification_id,read_at,b_id);
                                    notification_lists.add(list);
                                }

                                // Notify the adapter of data changes
                                adapter.notifyDataSetChanged();
                            } else {
                                Log.e("Error", "Result is not success");
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
                data.put("u_id", String.valueOf(u_id));
                return data;
            }
        };

        requestQueue.add(stringRequest);
    }


}
