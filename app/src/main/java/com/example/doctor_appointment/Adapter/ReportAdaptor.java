package com.example.doctor_appointment.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.doctor_appointment.Appoinment;
import com.example.doctor_appointment.R;
import com.example.doctor_appointment.See_all_doctor;
import com.example.doctor_appointment.doctor_profile;
import com.example.doctor_appointment.model.doctorList;
import com.example.doctor_appointment.model.reportList;
import com.example.doctor_appointment.service;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;


public class ReportAdaptor extends ArrayAdapter<reportList> {

    Context context;
    List<reportList>reportarraylist;
    public ReportAdaptor(@NonNull Context context, ArrayList<reportList>reportarraylist) {
        super(context, R.layout.reportdetail, reportarraylist);

        this.context=context;
        this.reportarraylist=reportarraylist;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.reportdetail,null,true);


        TextView doctor_name=view.findViewById(R.id.doctor_name);
        TextView date=view.findViewById(R.id.date);


        TextView report=view.findViewById(R.id.report);



        doctor_name.setText(reportarraylist.get(position).getDoctor());
        date.setText(reportarraylist.get(position).getDate());
        report.setText(reportarraylist.get(position).getReport());







        return view;
    }
}
