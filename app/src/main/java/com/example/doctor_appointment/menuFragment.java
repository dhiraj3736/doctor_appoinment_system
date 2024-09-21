package com.example.doctor_appointment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class menuFragment extends Fragment {

    TextView logout,email,fullname;
    ImageView photo;
    LinearLayout profile,report,editprofile;

    private RequestQueue requestQueue;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_menu_, container, false);

        logout=view.findViewById(R.id.logout);
        profile=view.findViewById(R.id.profile);
        report=view.findViewById(R.id.report);
        editprofile=view.findViewById(R.id.editProfile);

        photo = view.findViewById(R.id.photo);
        fullname = view.findViewById(R.id.fullname);
        email = view.findViewById(R.id.email);

        requestQueue = Volley.newRequestQueue(getContext());

        retrieveUserInfo();


        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),edituserinfo.class);
                startActivity(intent);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),userProfile.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SessionManagement sessionManagement=new SessionManagement(getContext());
                sessionManagement.removeSession();
                Intent intent = new Intent(getContext(), login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);


                startActivity(intent);

            }
        });


        return view;
    }
    private void retrieveUserInfo() {
        String url = Endpoints.select_user_info_for_profile;
        SessionManagement sessionManagement = new SessionManagement(getContext());
        int user_id = sessionManagement.getSession();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                String result = obj.getString("result");

                if (result.equals("success")) {
                    JSONObject userData = obj.getJSONObject("data");

                    // Populate UI with user data
                    fullname.setText(userData.getString("fullname"));

                    email.setText(userData.getString("email"));
                 ;

                    // Load profile image using Glide
                    String imageUrl = userData.getString("image");
                    Glide.with(getContext())
                            .load(imageUrl)
                            .circleCrop()
                            .into(photo);

                } else {
                    Toast.makeText(getContext(), "Failed to retrieve user data.", Toast.LENGTH_LONG).show();
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
                data.put("u_id", String.valueOf(user_id));
                return data;
            }
        };

        requestQueue.add(stringRequest);
    }


}