package com.example.doctor_appointment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Appoinment extends AppCompatActivity {

    private CalendarView calendarView;
    private GridLayout timeGrid;
    private Button bookAppointmentButton;

    private RequestQueue requestQueue;
    private String selectedDate = "";
    private String selectedTimeSlot = "";
    private TextView selectedTextView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appoinment);

        calendarView = findViewById(R.id.calendarView);
        timeGrid = findViewById(R.id.timeGrid);
        bookAppointmentButton = findViewById(R.id.bookAppointmentButton);
        requestQueue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        String doctorID = intent.getStringExtra("d_id");

        getTimeSlot(doctorID);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", dayOfMonth);
                // Clear the previous time slots
                timeGrid.removeAllViews();
                // Retrieve the time slots again based on the new date if needed
                getTimeSlot(doctorID);
            }
        });

        // Set click listener for book appointment button
        bookAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectedTimeSlot.isEmpty()) {
                    saveAppoinment(doctorID,selectedDate,selectedTimeSlot);                    // Perform booking logic here
                } else {
                    Toast.makeText(Appoinment.this, "Please select a time slot", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    public void saveAppoinment(String doctorId, String selectedDate,String selectedTimeSlot){
        String url = Endpoints.saveAppoinment;

        SessionManagement sessionManagement=new SessionManagement(Appoinment.this);
        int user_id=sessionManagement.getSession();

        Log.d("d_id",doctorId);
        Log.d("date",selectedDate);
        Log.d("time",selectedTimeSlot);
        Log.d("user", String.valueOf(user_id));

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            String result = object.getString("result");
                            if (result.equals("success")) {
                                String message=object.getString("message");
                                Toast.makeText(Appoinment.this, message, Toast.LENGTH_SHORT).show();



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
                        // Handle the error response
                        Log.e("Volley Error", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("d_id", doctorId);
                data.put("date",selectedDate);
                data.put("time",selectedTimeSlot);
                data.put("user_id", String.valueOf(user_id));
                return data;
            }
        };

        // Add the request to the RequestQueue
        requestQueue.add(stringRequest);
    }

    public void getTimeSlot(String doctorId) {
        String url = Endpoints.getAvailableTimeSlots; // Replace with your URL

        // Create the StringRequest
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            String result = object.getString("result");
                            if (result.equals("success")) {
                                // Handle the successful response
                                JSONArray timeSlotsArray = object.getJSONArray("data");
                                List<String> timeSlots = new ArrayList<>();
                                for (int i = 0; i < timeSlotsArray.length(); i++) {
                                    timeSlots.add(timeSlotsArray.getString(i));
                                }

                                // Display the time slots in the GridLayout
                                displayTimeSlots(timeSlots);
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
                        // Handle the error response
                        Log.e("Volley Error", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("d_id", doctorId);
                return data;
            }
        };

        // Add the request to the RequestQueue
        requestQueue.add(stringRequest);
    }

    private void displayTimeSlots(List<String> timeSlots) {
        timeGrid.removeAllViews(); // Clear any previous time slots

        for (String timeSlot : timeSlots) {
            TextView textView = new TextView(this);
            textView.setText(timeSlot);
            textView.setPadding(50, 26, 50, 26);
            textView.setBackgroundResource(R.drawable.time_slot_background); // Add background resource for styling

            // Set layout parameters and margins
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            int marginInDp = 8; // Margin size in dp
            int marginInPx = (int) (marginInDp * getResources().getDisplayMetrics().density); // Convert dp to pixels
            params.setMargins(marginInPx, marginInPx, marginInPx, marginInPx);
            textView.setLayoutParams(params);

            // Add click listener to select time slot
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedTextView != null) {
                        selectedTextView.setBackgroundResource(R.drawable.time_slot_background); // Reset previous selection
                    }
                    selectedTimeSlot = timeSlot;
                    selectedTextView = textView;
                    textView.setBackgroundResource(R.drawable.time_slot_selected_background); // Highlight current selection
                    Toast.makeText(Appoinment.this, "Selected: " + timeSlot, Toast.LENGTH_SHORT).show();
                }
            });

            // Add the TextView to the GridLayout
            timeGrid.addView(textView);
        }
    }
}
