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
import com.example.doctor_appointment.R;
import com.example.doctor_appointment.doctor_profile;
import com.example.doctor_appointment.model.doctorList;
import com.example.doctor_appointment.model.topRatedDoctor;

import java.util.List;

public class topDoctorAdapter extends ArrayAdapter<topRatedDoctor> {

    Context context;
    List<topRatedDoctor> toprateddoctor;

    public topDoctorAdapter(@NonNull Context context, List<topRatedDoctor> toprateddoctor) {
        super(context, R.layout.doctor_list_for_userdashboard, toprateddoctor);
        this.context = context;
        this.toprateddoctor = toprateddoctor;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_list_for_userdashboard, null, true);

        ImageView d_image = view.findViewById(R.id.doctor_image);
        TextView d_name = view.findViewById(R.id.doctor_name);
        TextView d_specialty = view.findViewById(R.id.doctor_specialty);
        TextView rating_value = view.findViewById(R.id.rating_value);
        CardView cardView = view.findViewById(R.id.card);
        String name=toprateddoctor.get(position).getName();
        Glide.with(context).load(toprateddoctor.get(position).getImage()).circleCrop().into(d_image);
        String d_id = toprateddoctor.get(position).getD_id();
        d_name.setText(name);
        d_specialty.setText(toprateddoctor.get(position).getSpecialist());
        rating_value.setText(toprateddoctor.get(position).getRating_value());

Log.d("adap",name);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, doctor_profile.class);
                intent.putExtra("doctor_id", d_id);
                context.startActivity(intent);
            }
        });

        return view;
    }
}
