package com.example.doctor_appointment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class user_dashboard extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;


String user_info,user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        frameLayout = findViewById(R.id.frameLayout);


        Intent intent=getIntent();
        user_id=intent.getStringExtra("session_Id");
        user_info=intent.getStringExtra("user_info");


        // Load the default fragment when the activity is created
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), true);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                Fragment selectedFragment = null;

                switch (itemId) {
                    case R.id.navHome:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.navNotification:
                        selectedFragment = new NotificationFragment();
                        break;
                    case R.id.navBook:
                        selectedFragment = new ScheduleFragment();
                        break;
                    case R.id.navMenu:
                        selectedFragment = new menuFragment();
                        break;
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment, false);
                }

                return true;
            }
        });
    }

    private void loadFragment(Fragment fragment, boolean isAppInitialized) {

        Bundle bundle = new Bundle();
        bundle.putString("user_id", user_id);
        bundle.putString("user_info", user_info);
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInitialized) {
            fragmentTransaction.add(R.id.frameLayout, fragment);
        } else {
            fragmentTransaction.replace(R.id.frameLayout, fragment);
        }
        fragmentTransaction.commit();
    }
}
