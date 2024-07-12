package com.example.doctor_appointment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditBooking extends AppCompatActivity {

    private CalendarView calendarView;
    private GridLayout timeGrid;
    private Button book;
    private RequestQueue requestQueue;
    private String selectedDate = "";
    private String selectedTimeSlot = "";
    private TextView selectedTextView = null;
    private EditText reason;
    private String Reasons;
    private String booking_time;
    String b_id;
    private List<String> bookedSlots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_booking);

        calendarView = findViewById(R.id.calendarView);
        timeGrid = findViewById(R.id.timeGrid);
        book = findViewById(R.id.book);
        reason = findViewById(R.id.reason);

        requestQueue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        String doctorID = intent.getStringExtra("d_id");
       b_id = intent.getStringExtra("b_id");
        String booking_date = intent.getStringExtra("b_date");
        booking_time = intent.getStringExtra("b_time");
        String b_reason = intent.getStringExtra("reason");
        Log.d("d_id", doctorID);
        Log.d("b_id", b_id);
        Log.d("b_date", booking_date);
        Log.d("booking_time", booking_time);
        Log.d("reason", b_reason);

        reason.setText(b_reason);

        setCalendarDate(calendarView, booking_date);
        selectedDate = booking_date;

        getTimeSlot(doctorID, selectedDate);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", dayOfMonth);
                // Clear the previous time slots
                timeGrid.removeAllViews();
                // Retrieve the time slots again based on the new date
                getTimeSlot(doctorID, selectedDate);
            }
        });

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reasons = reason.getText().toString().trim();

                    saveAppoinment(doctorID, selectedDate, selectedTimeSlot, Reasons);

            }
        });
    }

    private void setCalendarDate(CalendarView calendarView, String bookingDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            Date date = dateFormat.parse(bookingDate);
            if (date != null) {
                calendarView.setDate(date.getTime(), true, true);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void saveAppoinment(String doctorId, String selectedDate, String selectedTimeSlot, String Reasons) {
        String url = Endpoints.EditAppoinment;

        SessionManagement sessionManagement = new SessionManagement(EditBooking.this);
        int user_id = sessionManagement.getSession();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            String result = object.getString("result");
                            if (result.equals("success")) {
                                String message = object.getString("message");

                                reason.setText("");
                                Toast.makeText(EditBooking.this, message, Toast.LENGTH_SHORT).show();
                                getTimeSlot(doctorId, selectedDate);

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

                data.put("date", selectedDate);
                data.put("time", selectedTimeSlot);
                data.put("reason", Reasons);

                data.put("b_id",b_id);
                return data;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void getTimeSlot(String doctorId, String date) {
        String url = Endpoints.getAvailableTimeSlots;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            String result = object.getString("result");
                            if (result.equals("success")) {
                                JSONArray availableSlotsArray = object.getJSONArray("availableSlots");
                                JSONArray bookedSlotsArray = object.getJSONArray("bookedSlots");

                                List<String> availableSlots = new ArrayList<>();
                                bookedSlots.clear();
                                for (int i = 0; i < availableSlotsArray.length(); i++) {
                                    availableSlots.add(availableSlotsArray.getString(i));
                                }
                                for (int i = 0; i < bookedSlotsArray.length(); i++) {
                                    String bookedSlot = bookedSlotsArray.getString(i);
                                    bookedSlots.add(bookedSlot.substring(0, 5));
                                }

                                Log.d("AvailableSlots", availableSlots.toString());
                                Log.d("BookedSlots", bookedSlots.toString());

                                displayTimeSlots(availableSlots);
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
                data.put("date", date);
                return data;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void displayTimeSlots(List<String> timeSlots) {
        timeGrid.removeAllViews();

        for (String timeSlot : timeSlots) {
            TextView textView = new TextView(this);
            textView.setText(timeSlot);
            textView.setPadding(25, 25, 25, 25);

            int defaultTextColor = textView.getCurrentTextColor();
            if (bookedSlots.contains(timeSlot)) {
                textView.setBackgroundResource(R.drawable.select_book);
                textView.setTextColor(getResources().getColor(R.color.purple_200));
                textView.setEnabled(false);
                Log.d("TimeSlot", "Booked slot: " + timeSlot);
            } else {
                textView.setBackgroundResource(R.drawable.time_slot_background);
                textView.setEnabled(true);
                if (timeSlot.equals(booking_time)) {
                    selectedTimeSlot = timeSlot;
                    selectedTextView = textView;
                    textView.setBackgroundResource(R.drawable.time_slot_selected_background);
                }
            }

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            int marginInDp = 8;
            int marginInPx = (int) (marginInDp * getResources().getDisplayMetrics().density);
            params.setMargins(marginInPx, marginInPx, marginInPx, marginInPx);
            textView.setLayoutParams(params);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedTextView != null) {
                        selectedTextView.setBackgroundResource(R.drawable.time_slot_background);
                    }
                    selectedTimeSlot = timeSlot;
                    selectedTextView = textView;
                    textView.setBackgroundResource(R.drawable.time_slot_selected_background);
                    Toast.makeText(EditBooking.this, "Selected: " + timeSlot, Toast.LENGTH_SHORT).show();
                }
            });

            timeGrid.addView(textView);
        }
    }
}
