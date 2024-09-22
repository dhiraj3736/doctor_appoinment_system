package com.example.doctor_appointment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class userProfile extends AppCompatActivity {

    // Declare the UI components
    ImageView photo,addphoto,back;
    TextView fullname, location, phoneno, phoneEmail, joinDate, currentTime;
    Button btnEditInfo, btnChangePassword;
    private RequestQueue requestQueue;
    private static final int PICK_IMAGE = 100;
    private static final int TAKE_PHOTO = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize the UI components
        photo = findViewById(R.id.profile_image);
        fullname = findViewById(R.id.full_name);

        location = findViewById(R.id.location);
        phoneno = findViewById(R.id.phoneno);
        phoneEmail = findViewById(R.id.phone_email);
        joinDate = findViewById(R.id.join);
        addphoto=findViewById(R.id.addphoto);
        back=findViewById(R.id.back);
        btnChangePassword=findViewById(R.id.btn_change_password);


        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(userProfile.this,changePassword.class);
                startActivity(intent);

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



//addphoto popup
        addphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a PopupMenu
                PopupMenu popupMenu = new PopupMenu(userProfile.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                // Handle menu item clicks
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_gallery:
                                // Open gallery to pick an image
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(galleryIntent, PICK_IMAGE);
                                return true;

                            case R.id.menu_camera:
                                // Open camera to capture a photo
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, TAKE_PHOTO);
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show(); // Show the popup menu
            }
        });


        btnEditInfo = findViewById(R.id.btn_edit_info);


        requestQueue = Volley.newRequestQueue(this);

        // Set up any click listeners if necessary
        btnEditInfo.setOnClickListener(v -> {
            // Handle edit information button click
        });



        retrieveUserInfo();

        btnEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(userProfile.this,edituserinfo.class);
                startActivity(intent);
            }
        });
    }



    //handle image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE) {
                // Handle gallery image selection
                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    photo.setImageBitmap(bitmap);
                    changePhoto(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == TAKE_PHOTO) {
                // Handle camera photo capture
                Bundle extras = data.getExtras();
                Bitmap bitmap = (Bitmap) extras.get("data");
                photo.setImageBitmap(bitmap);
                changePhoto(bitmap);
            }
        }
    }


    // send image in database
    public void changePhoto(Bitmap bitmap){

        String url = Endpoints.saveChangePhoto;

        SessionManagement sessionManagement = new SessionManagement(userProfile.this);
        int user_id = sessionManagement.getSession();
        Log.d("user", String.valueOf(user_id));

        String imageBase64 = convertBitmapToBase64(bitmap);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            String result = object.getString("result");
                            if (result.equals("success")) {
                                String message = object.getString("message");


                                Toast.makeText(userProfile.this, message, Toast.LENGTH_SHORT).show();
                                // Refresh time slots


                            } else {
                                Log.e("Error", "user ID is null or empty");
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
                data.put("user_id", String.valueOf(user_id));
                data.put("image", imageBase64);





                return data;
            }
        };

        requestQueue.add(stringRequest);


    }

    //convertBitmapToBase64
    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
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
