package com.example.doctor_appointment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class login extends Activity {

    EditText lemail,lpassword;
    Button login_button,register;
    String email,password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        lemail=findViewById(R.id.etEmail);
        lpassword=findViewById(R.id.etPassword);
        login_button=findViewById(R.id.btnLogin);
        register=findViewById(R.id.tvCreateAccount);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, Activity_register.class);

                startActivity(intent);
            }
        });
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email=lemail.getText().toString().trim();
                password=lpassword.getText().toString().trim();

                if(email.isEmpty()){
                    Toast.makeText(login.this, "Enter email", Toast.LENGTH_SHORT).show();

                }else if(password.isEmpty()){
                    Toast.makeText(login.this, "Enter password", Toast.LENGTH_SHORT).show();

                }
                if (!email.equals("")&& !password.equals("")){
                    loginUser(email,password);
                }
            }
        });

    }
    private void loginUser(String email, String password) {
        String url =Endpoints.login;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("result").equals("success")) {
                                String session_Id = jsonObject.getString("session_id");
                                String user_info=jsonObject.getString("user");

                                Intent intent = new Intent(login.this, user_dashboard.class);
                                intent.putExtra("session_Id", session_Id);
                                intent.putExtra("user_info",user_info);
                                startActivity(intent);

                                Toast.makeText(login.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            } else if (jsonObject.has("error")) {
                                handleErrorCode(jsonObject.getString("error"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(login.this, "Error parsing server response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message = "An error occurred.";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                String errorData = new String(error.networkResponse.data, "UTF-8");
                                JSONObject errorObj = new JSONObject(errorData);
                                if (errorObj.has("message")) {
                                    message = errorObj.getString("message");
                                }
                            } catch (UnsupportedEncodingException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(login.this, message, Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void handleErrorCode(String errorCode) {
        String message;
        switch (errorCode) {
            case "USER_NOT_FOUND":
                message = "User not found. Please check your email and password.";
                break;
            case "USER_SUSPENDED":
                message = "Your account is suspended. Contact support.";
                break;
            case "INVALID_PASSWORD":
                message = "Incorrect password. Please try again.";
                break;
            default:
                message = "An unknown error occurred.";
                break;
        }
        Toast.makeText(login.this, message, Toast.LENGTH_SHORT).show();
    }}