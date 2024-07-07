package com.example.doctor_appointment.Algorithm;

import android.content.Context;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.doctor_appointment.Endpoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorRatingProcessor {

    private Context context;
    private List<Integer> ratings = new ArrayList<>();

    public DoctorRatingProcessor(Context context) {
        this.context = context;
    }

    public void fetchRatings(String doctorId) {
        String url = Endpoints.retrieveRatingsByDoctorId;

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String result = jsonObject.getString("result");

                            if ("success".equals(result)) {
                                JSONArray data = jsonObject.getJSONArray("data");

                                // Process each rating
                                for (int i = 0; i < data.length(); i++) {
                                    int rating = data.getInt(i);
                                    ratings.add(rating);
                                }

                                // Calculate average rating
                                double averageRating = calculateAverageRating(ratings);

                                int numberOfReviews = ratings.size(); // Count the number of reviews

                                // Send average rating and number of reviews to the database
                                sendAverageRatingToDatabase(doctorId, averageRating, numberOfReviews);

                            } else {
                                String message = jsonObject.getString("message");

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "JSON parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error retrieving ratings: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("doctor_id", doctorId);
                return params;
            }
        };

        requestQueue.add(request);
    }

    private double calculateAverageRating(List<Integer> ratings) {
        if (ratings == null || ratings.isEmpty()) {
            throw new IllegalArgumentException("Ratings list must not be null or empty.");
        }

        int sumOfRatings = 0;
        int numberOfRatings = ratings.size();

        // Calculate sum of all ratings
        for (int rating : ratings) {
            sumOfRatings += rating;
        }

        // Calculate average rating
        return (double) sumOfRatings / numberOfRatings;
    }

    private void sendAverageRatingToDatabase(String doctorId, double averageRating, int numberOfReviews) {
        String url = Endpoints.saveAverageRating;

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle server response for the average rating update
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error sending average rating: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("doctor_id", doctorId);
                params.put("average_rating", String.valueOf(averageRating));
                params.put("numberOfReviews", String.valueOf(numberOfReviews));
                return params;
            }
        };

        requestQueue.add(request);
    }
}
