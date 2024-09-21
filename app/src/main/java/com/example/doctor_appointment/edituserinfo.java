package com.example.doctor_appointment;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class edituserinfo extends AppCompatActivity {
    EditText name, email, number, password, address;
    Button edit;

    ImageView profilephoto,addphoto,back;


    String mname, memail, mnumber, mpassword, maddress;

    private static final int PICK_IMAGE = 100;
    private static final int TAKE_PHOTO = 101;
    private RequestQueue requestQueue;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editinfo);
        name = findViewById(R.id.full_name);
        number = findViewById(R.id.phone_number);
        address = findViewById(R.id.address);
        profilephoto=findViewById(R.id.profile_image);
        addphoto=findViewById(R.id.addphoto);

        edit=findViewById(R.id.edit);
        back=findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        requestQueue = Volley.newRequestQueue(this);
        retriveInfo();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mname = name.getText().toString().trim();

                mnumber = number.getText().toString().trim();
                maddress = address.getText().toString().trim();
                editInfo();
                Intent intent=new Intent(edituserinfo.this,userProfile.class);
                startActivity(intent);



            }
        });



        //popup for take image
        addphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(edituserinfo.this, view);
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
                    profilephoto.setImageBitmap(bitmap);
                    changePhoto(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == TAKE_PHOTO) {
                // Handle camera photo capture
                Bundle extras = data.getExtras();
                Bitmap bitmap = (Bitmap) extras.get("data");
                profilephoto.setImageBitmap(bitmap);
                changePhoto(bitmap);
            }
        }
    }


    // send image in database
        public void changePhoto(Bitmap bitmap){

            String url = Endpoints.saveChangePhoto;

            SessionManagement sessionManagement = new SessionManagement(edituserinfo.this);
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


                                    Toast.makeText(edituserinfo.this, message, Toast.LENGTH_SHORT).show();
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


    public void editInfo() {
        String url = Endpoints.saveEditInfo;

        SessionManagement sessionManagement = new SessionManagement(edituserinfo.this);
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


                                Toast.makeText(edituserinfo.this, message, Toast.LENGTH_SHORT).show();
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
                data.put("fullname", mname);
                data.put("number", mnumber);
                data.put("address", maddress);



                return data;
            }
        };

        requestQueue.add(stringRequest);
    }


    public void retriveInfo() {
        String url = Endpoints.select_user_info_for_editprofile;

        SessionManagement sessionManagement = new SessionManagement(edituserinfo.this);
        int user_id = sessionManagement.getSession();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            String result = object.getString("result");
                            if (result.equals("success")) {
                                String fullname = object.getString("fullname");
                                String location=object.getString("address");
                                String contactNo=object.getString("number");


                                String imageUrl = object.getString("image");
                                Glide.with(edituserinfo.this)
                                        .load(imageUrl)
                                        .into(profilephoto);
                                name.setText(fullname);
                                address.setText(location);
                                number.setText(contactNo);


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
                return data;
            }
        };

        requestQueue.add(stringRequest);
    }


}
