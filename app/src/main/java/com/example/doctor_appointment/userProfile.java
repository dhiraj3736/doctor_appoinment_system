package com.example.doctor_appointment;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class userProfile extends AppCompatActivity {

    // Declare the UI components
    ImageView photo;
    TextView fullname, username, location, phoneno, phoneEmail, joinDate, currentTime;
    Button btnEditInfo, btnChangePassword;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize the UI components
        photo = findViewById(R.id.profile_image);
        fullname = findViewById(R.id.full_name);
        username = findViewById(R.id.username);
        location = findViewById(R.id.location);
        phoneno = findViewById(R.id.phoneno);
        phoneEmail = findViewById(R.id.phone_email);
        joinDate = findViewById(R.id.join);


        btnEditInfo = findViewById(R.id.btn_edit_info);
        btnChangePassword = findViewById(R.id.btn_change_password);

        requestQueue = Volley.newRequestQueue(this);

        // Set up any click listeners if necessary
        btnEditInfo.setOnClickListener(v -> {
            // Handle edit information button click
        });

        btnChangePassword.setOnClickListener(v -> {
            // Handle change password button click
        });

        retrieveUserInfo();
    }

    private void retrieveUserInfo() {
        String url = Endpoints.select_user_info_for_profile;
        SessionManagement sessionManagement = new SessionManagement(userProfile.this);
        int user_id = sessionManagement.getSession();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                String result = obj.getString("result");

                if (result.equals("success")) {
                    JSONObject userData = obj.getJSONObject("data");

                    // Populate UI with user data
                    fullname.setText(userData.getString("fullname"));
                    username.setText(userData.getString("username"));
                    location.setText(userData.getString("address"));
                    phoneno.setText(userData.getString("number"));
                    phoneEmail.setText(userData.getString("email"));
                    joinDate.setText(userData.getString("join"));

                    // Load profile image using Glide
                    String imageUrl = userData.getString("image");
                    Glide.with(userProfile.this)
                            .load(imageUrl)
                            .into(photo);

                } else {
                    Toast.makeText(userProfile.this, "Failed to retrieve user data.", Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("VolleyError", error.toString());
            Toast.makeText(userProfile.this, "Server error. Please try again later.", Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("u_id", String.valueOf(user_id));
                return data;
            }
        };

        requestQueue.add(stringRequest);
    }


}
