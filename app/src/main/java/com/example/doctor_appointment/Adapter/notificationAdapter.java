package com.example.doctor_appointment.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.doctor_appointment.Endpoints;
import com.example.doctor_appointment.EsewaPayment;
import com.example.doctor_appointment.R;
import com.example.doctor_appointment.ScheduleFragment;
import com.example.doctor_appointment.See_all_doctor;
import com.example.doctor_appointment.SessionManagement;
import com.example.doctor_appointment.model.notification_list;
import com.example.doctor_appointment.service;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class notificationAdapter extends ArrayAdapter<notification_list> {

    private final Context context;
    private final List<notification_list> notification_lists;
    private RequestQueue requestQueue;

    public notificationAdapter(@NonNull Context context, @NonNull List<notification_list> notification_lists) {
        super(context, R.layout.notification_list, notification_lists);
        this.context = context;
        this.notification_lists = notification_lists;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.notification_list, parent, false);
        }
        requestQueue = Volley.newRequestQueue(getContext());

        // Get the current notification
        notification_list currentNotification = notification_lists.get(position);

        // Find and set the views
        TextView n_date = convertView.findViewById(R.id.notification_date);
        TextView message = convertView.findViewById(R.id.notification_message);
        CardView cardView = convertView.findViewById(R.id.not_card);

        // Set the data
        String date = currentNotification.getDate();
        String name = currentNotification.getName();
        String doctor = currentNotification.getDoctor();
        String n_id = currentNotification.getNotification_id();
        String read_at = currentNotification.getRead_at();

        // Format the date
        String dateOnly = date.split("T")[0];
        n_date.setText(dateOnly);
        Log.d("read_at", read_at);

        // Update the message based on the conditions
        if ("1".equals(name)) {
            message.setText("New report is added by " + doctor);
            if (!"read".equals(read_at)) {
                int color = getContext().getResources().getColor(R.color.white, null);
                cardView.setCardBackgroundColor(color);
            }
        } else if ("1".equals(doctor)) {
            message.setText("Hello " + name + ", your appointment is approved");
            if (!"read".equals(read_at)) {
                int color = getContext().getResources().getColor(R.color.white, null);
                cardView.setCardBackgroundColor(color);
            }
        } else {
            message.setText("No information available.");
        }

        SessionManagement sessionManagement = new SessionManagement(getContext());
        int u_id = sessionManagement.getSession();
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                read_at(n_id, u_id);
                if ("1".equals(name)) {
                    Intent intent = new Intent(getContext(), service.class);
                    getContext().startActivity(intent);
                } else if ("1".equals(doctor)) {
                    Intent intent1 = new Intent(getContext(), EsewaPayment.class);
                    getContext().startActivity(intent1);
                }
            }
        });

        return convertView;
    }

    public void read_at(String n_id, int u_id) {
        String url = Endpoints.markAsRead;
        Log.d("read_at", "Requesting to mark as read for n_id: " + n_id);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            String result = object.getString("result");
                            if ("success".equals(result)) {
                                Log.d("read_at", "Notification marked as read successfully");
                                // Handle success, e.g., update UI or notify the adapter
                            } else {
                                Log.e("read_at", "Server response indicates failure: " + object.optString("message", "No message provided"));
                            }
                        } catch (JSONException e) {
                            Log.e("read_at", "JSON Parsing error: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("read_at", "Volley error: " + error.getMessage());
                        if (error.networkResponse != null) {
                            Log.e("read_at", "Status Code: " + error.networkResponse.statusCode);
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("n_id", n_id);
                data.put("u_id", String.valueOf(u_id));
                return data;
            }
        };

        requestQueue.add(stringRequest);
    }
}
