package com.example.doctor_appointment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.doctor_appointment.Adapter.comment_adapter;
import com.example.doctor_appointment.Adapter.doctorAdaptor;
import com.example.doctor_appointment.Adapter.doctor_Adapter_for_userdashboard;
import com.example.doctor_appointment.Algorithm.DoctorRatingProcessor;
import com.example.doctor_appointment.model.comment_info;
import com.example.doctor_appointment.model.doctorList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class fragment_reviews extends Fragment {

    ListView listView;

    comment_adapter adapter;

    public static ArrayList<comment_info> commentArraylist=new ArrayList<>();

    comment_info comment_info;
    private String doctorId;

    RatingBar ratingBar;

    EditText comment;
    Button commentbtn;

    public fragment_reviews() {
        // Required empty public constructor
    }
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        Log.d("dhir",doctorId);
        View view= inflater.inflate(R.layout.activity_fragment_reviews, container, false);


        ratingBar=view.findViewById(R.id.ratingBar);
        comment=view.findViewById(R.id.comment);
        commentbtn=view.findViewById(R.id.commentButton);
        listView=view.findViewById(R.id.show_comment);
        adapter=new comment_adapter(getContext(),commentArraylist);
        listView.setAdapter(adapter);

        retrive_comment();
        get_rating();

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                // Handle rating change here
//                Toast.makeText(getContext(), "Rating: " + rating, Toast.LENGTH_SHORT).show();
                rating(rating);


                DoctorRatingProcessor doctorRatingProcessor=new DoctorRatingProcessor(getContext());
                doctorRatingProcessor.fetchRatings(doctorId);
            }
        });

        commentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insert_comment();

            }
        });

        return view;
    }


    public void get_rating(){



        String url =Endpoints.get_rating;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);


                    if (jsonObject.getString("result").equals("success")){
                        String message=jsonObject.getString("message");
                        float rate= Float.parseFloat(jsonObject.getString("rating"));

                        ratingBar.setRating(rate);




                    }else if (response.equals("failure")) {
                        Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

            SessionManagement sessionManagement=new SessionManagement(getContext());
            int user_id=sessionManagement.getSession();
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String, String> data=new HashMap<>();

                data.put("user_id", String.valueOf(user_id));
                data.put("doctor_id", doctorId);


                return data;
            }
        };
        requestQueue.add(stringRequest);

    }





    public void retrive_comment() {
        String url = Endpoints.retrive_comment;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        commentArraylist.clear();

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        commentArraylist.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String result = jsonObject.getString("result");

                            if (result.equals("success")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    String name = obj.getString("user_name");
                                    String comment1 = obj.getString("comment");

                                    comment_info comment = new comment_info(name, comment1);
                                    commentArraylist.add(comment);
                                }
                                adapter.notifyDataSetChanged();
                            } else if (result.equals("empty")) {
                                Toast.makeText(getContext(), "No comments found.", Toast.LENGTH_SHORT).show();
                            } else if (result.equals("not_found")) {
                                Toast.makeText(getContext(), "doctor_id not found", Toast.LENGTH_SHORT).show();
                            } else {
                                // Handle unexpected result
                                Toast.makeText(getContext(), "Unexpected response.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "JSON parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NetworkError) {
                            Toast.makeText(getContext(), "Network error. Check your internet connection.", Toast.LENGTH_SHORT).show();
                        }
                        Log.e("Volley Error", "Error: " + error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("doctor_id", doctorId);
                return data;
            }
        };

        requestQueue.add(request);
    }

    public void insert_comment(){

              String  cmt=comment.getText().toString().trim();

Log.d("cmt",cmt);
                String url =Endpoints.insert_comment;
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);


                            if (jsonObject.getString("result").equals("success")){
                                String message=jsonObject.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                comment.setText("");





                            }else if (response.equals("failure")) {
                                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){

                    SessionManagement sessionManagement=new SessionManagement(getContext());
                    int user_id=sessionManagement.getSession();
                    protected Map<String,String> getParams() throws AuthFailureError {
                        Map<String, String> data=new HashMap<>();
                        data.put("comment", cmt);
                        data.put("user_id", String.valueOf(user_id));
                        data.put("doctor_id", doctorId);


                        return data;
                    }
                };
                requestQueue.add(stringRequest);

            }




    public void rating(float rating){
        String url =Endpoints.insert_rating;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
        try {
            JSONObject jsonObject=new JSONObject(response);

            if (jsonObject.getString("result").equals("success")){
//                String message=jsonObject.getString("message");
//                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();




                ratingBar.setClickable(false);
            }else if (response.equals("failure")) {
                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }

        }catch (JSONException e){
            e.printStackTrace();
        }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            SessionManagement sessionManagement=new SessionManagement(getContext());
            int user_id=sessionManagement.getSession();

            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String, String> data=new HashMap<>();
                data.put("rating", String.valueOf(rating));
                data.put("user_id", String.valueOf(user_id));
                data.put("doctor_id", doctorId);


                return data;
            }
        };
        requestQueue.add(stringRequest);

    }

}