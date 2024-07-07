package com.example.doctor_appointment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.doctor_appointment.Algorithm.DoctorRatingProcessor;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class doctor_profile extends AppCompatActivity {

    ImageView doctor_image;
    TextView doctor_name, doctor_specialty, doctor_experience, start_time, end_time,rating;
    String doctor_id;
    private RequestQueue requestQueue;
    private ViewPager2 viewPager;
    ImageView back;


    AppCompatButton book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        Intent intent = getIntent();
        doctor_id = intent.getStringExtra("doctor_id");
        Log.d("doc_id", doctor_id);

        doctor_image = findViewById(R.id.doctor_image);
        doctor_name = findViewById(R.id.doctor_name);
        doctor_specialty = findViewById(R.id.doctor_specialty);
        doctor_experience = findViewById(R.id.experience);
        start_time = findViewById(R.id.start_time);
        end_time = findViewById(R.id.end_time);
        rating=findViewById(R.id.rating);
        back=findViewById(R.id.back_button);
        book=findViewById(R.id.book);

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(doctor_profile.this,Appoinment.class);
                intent1.putExtra("d_id",doctor_id);
                startActivity(intent1);

            }
        });

        requestQueue = Volley.newRequestQueue(this);
        viewPager = findViewById(R.id.view_pager);

        setupViewPager();

        retrieveDoctorInfo();








        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setupViewPager() {
        viewPager.setAdapter(new ViewPagerAdapter(this, doctor_id));

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("About");
                    break;
                case 1:
                    tab.setText("Reviews");
                    break;
            }
        }).attach();
    }

    private void retrieveDoctorInfo() {
        String url = Endpoints.get_doctor_info_for_profile;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                String result = obj.getString("result");

                if (result.equals("success")) {
                    JSONObject doctorData = obj.getJSONObject("data");
                    String name = doctorData.getString("name");
                    String specialist = doctorData.getString("specialist");
                    String experience = doctorData.getString("experiance");
                    String fromtime = doctorData.getString("starttime");
                    String totime = doctorData.getString("endtime");
                    String image = doctorData.getString("image");
                    String rating_value=doctorData.getString("rating_value");

                    Glide.with(this).load(image).circleCrop().into(doctor_image);
                    doctor_name.setText(name);
                    doctor_specialty.setText(specialist);
                    doctor_experience.setText(experience);
                    start_time.setText(fromtime);
                    end_time.setText(totime);
                    rating.setText(rating_value);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("VolleyError", error.toString());
            Toast.makeText(doctor_profile.this, "Server error. Please try again later.", Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("d_id", doctor_id);
                return data;
            }
        };

        requestQueue.add(stringRequest);
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {
        private final String doctor_id;

        public ViewPagerAdapter(FragmentActivity fa, String doctor_id) {
            super(fa);
            this.doctor_id = doctor_id;
        }

        @Override
        public int getItemCount() {
            return 2; // Two fragments: About and Reviews
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    fragment_about aboutFragment = new fragment_about();
                    aboutFragment.setDoctorId(doctor_id);
                    return aboutFragment;
                case 1:
                    fragment_reviews reviewsFragment = new fragment_reviews();
                    reviewsFragment.setDoctorId(doctor_id);
                    return reviewsFragment;


                default:
                    throw new IllegalArgumentException("Unexpected position: " + position);
            }



        }


    }
}
