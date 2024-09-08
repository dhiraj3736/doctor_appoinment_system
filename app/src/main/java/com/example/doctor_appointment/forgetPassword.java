package com.example.doctor_appointment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class forgetPassword extends AppCompatActivity {

    EditText email;
    Button submit;
    TextView info;
    ImageView back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgetpassword);

        email = findViewById(R.id.email);
        submit = findViewById(R.id.submit);
        info = findViewById(R.id.message);
        back=findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String memail = email.getText().toString().trim();

                if (memail.isEmpty()) {
                    Toast.makeText(forgetPassword.this, "Please enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                String url = Endpoints.forgetpasswordpost;
                RequestQueue requestQueue = Volley.newRequestQueue(forgetPassword.this);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("ServerResponse", "Response: " + response); // Log the server response
                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    // Extract the result and message from the response
                                    String result = jsonObject.getString("result");
                                    String message = jsonObject.getString("message");

                                    // Handle different types of responses based on the result
                                    if (result.equalsIgnoreCase("success")) {
                                        info.setVisibility(View.VISIBLE);
                                        info.setText(message);
                                        Toast.makeText(forgetPassword.this, message, Toast.LENGTH_SHORT).show();
                                        email.setText("");

                                    } else if (result.equalsIgnoreCase("failure")) {
                                        info.setVisibility(View.VISIBLE);
                                        info.setText(message);
                                        Toast.makeText(forgetPassword.this, message, Toast.LENGTH_SHORT).show();
                                        email.setText("");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(forgetPassword.this, "Error parsing server response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("VolleyError", "Error: " + error.getMessage());
                                if (error.networkResponse != null) {
                                    String errorResponseBody = new String(error.networkResponse.data);
                                    Log.e("VolleyError", "Network Response Code: " + error.networkResponse.statusCode);
                                    Log.e("VolleyError", "Network Response Body: " + errorResponseBody);

                                    // Optionally parse the error response body if it's JSON
                                    try {
                                        JSONObject errorResponse = new JSONObject(errorResponseBody);
                                        String errorMessage = errorResponse.optString("message", "Network error. Please try again later.");
                                        info.setVisibility(View.VISIBLE);
                                        info.setText(errorMessage);
                                        Toast.makeText(forgetPassword.this, errorMessage, Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(forgetPassword.this, "Error parsing error response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(forgetPassword.this, "Network error. Please try again later.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> data = new HashMap<>();
                        data.put("email", memail);
                        return data;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Accept", "application/json");
                        return headers;
                    }
                };

                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        10000, // 10 seconds timeout
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                requestQueue.add(stringRequest);
            }
        });
    }
}
