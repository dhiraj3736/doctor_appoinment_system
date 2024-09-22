package com.example.doctor_appointment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
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

public class changePassword extends AppCompatActivity {

    EditText currentPassword, newPassword, confirmPassword;
    String current, newp, confirm;
    Button changePasswordButton;

    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepassword);

        // Initialize the views
        currentPassword = findViewById(R.id.currentPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.ConformPassword);  // Correct the ID here

        changePasswordButton = findViewById(R.id.change);
        // Fixed variable name

        back=findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // Set OnClickListener for the button
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the input values
                current = currentPassword.getText().toString().trim();
                newp = newPassword.getText().toString().trim();
                confirm = confirmPassword.getText().toString().trim();

                // Validation checks
                if (current.isEmpty()) {
                    Toast.makeText(changePassword.this, "Please enter current password", Toast.LENGTH_SHORT).show();
                } else if (newp.isEmpty()) {
                    Toast.makeText(changePassword.this, "Please enter new password", Toast.LENGTH_SHORT).show();
                } else if (confirm.isEmpty()) {
                    Toast.makeText(changePassword.this, "Please enter confirm password", Toast.LENGTH_SHORT).show();
                } else if (!newp.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
                    Toast.makeText(changePassword.this, "Password must be at least 8 characters long, and include uppercase, lowercase, digit, and special character", Toast.LENGTH_SHORT).show();
                } else if (!newp.equals(confirm)) {
                    Toast.makeText(changePassword.this, "New password and confirm password do not match", Toast.LENGTH_SHORT).show();
                } else {
                    SubmitnewPassword();  // Call the method to submit the new password
                }
            }
        });
    }

    private void SubmitnewPassword() {
        String url = Endpoints.resetPasswordPost;  // Assuming the correct endpoint for changing password

        SessionManagement sessionManagement = new SessionManagement(changePassword.this);
        int user_id = sessionManagement.getSession();  // Retrieve the user ID from the session
        RequestQueue requestQueue = Volley.newRequestQueue(changePassword.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("ServerResponse", response); // Log the server response

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String result = jsonObject.optString("result");

                            if (result.equalsIgnoreCase("success")) {
                                // Handle successful password change
                                Toast.makeText(changePassword.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                                clearInputs();  // Clear the input fields

                            } else if (result.equalsIgnoreCase("error")) {
                                // Handle server-side errors
                                String errorMsg = jsonObject.getString("message");
                                Toast.makeText(changePassword.this, errorMsg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(changePassword.this, "Error parsing server response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            Log.e("VolleyError", "Status Code: " + error.networkResponse.statusCode);
                            Log.e("VolleyError", "Response Data: " + new String(error.networkResponse.data));
                        } else {
                            Log.e("VolleyError", "Error Message: " + error.getMessage());
                        }

                        Toast.makeText(changePassword.this, "Current password is incorrect.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("user_id", String.valueOf(user_id));

                // Send user ID
                data.put("confirmpassword",confirm);
                data.put("currentpassword", current);  // Current password
                data.put("newpassword", newp);         // New password

                return data;
            }
        };

        requestQueue.add(stringRequest);  // Add the request to the queue
    }

    private void clearInputs() {
        currentPassword.setText("");  // Clear the current password field
        newPassword.setText("");      // Clear the new password field
        confirmPassword.setText("");  // Clear the confirm password field
    }
}
