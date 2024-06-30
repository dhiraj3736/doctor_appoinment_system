package com.example.doctor_appointment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {
    WebView myweb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myweb=findViewById(R.id.myweb);
        myweb.getSettings().setJavaScriptEnabled(true);
        myweb.setWebViewClient(new WebViewClient());
        myweb.loadUrl("http://10.0.2.2:8000/dashboard");

    }
}