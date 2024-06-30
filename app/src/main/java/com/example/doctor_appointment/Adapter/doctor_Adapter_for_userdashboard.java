package com.example.doctor_appointment.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.doctor_appointment.R;
import com.example.doctor_appointment.model.doctorList;

import java.util.ArrayList;
import java.util.List;

public class doctor_Adapter_for_userdashboard extends ArrayAdapter<doctorList> {

    Context context;
    List<doctorList> doctorarraylist;
    public doctor_Adapter_for_userdashboard(@NonNull Context context, ArrayList<doctorList> doctorarraylist) {
        super(context, R.layout.doctor_list_for_userdashboard, doctorarraylist);
        this.context=context;
        this.doctorarraylist=doctorarraylist;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_list_for_userdashboard,null,true);

        ImageView d_image=view.findViewById(R.id.doctor_image);
        TextView d_name=view.findViewById(R.id.doctor_name);
        TextView d_specialty=view.findViewById(R.id.doctor_specialty);
//        TextView d_qualification=view.findViewById(R.id.doctor_qualifications);
//        TextView d_experiance=view.findViewById(R.id.doctor_experience);


        Glide.with(context).load(doctorarraylist.get(position).getImage()).circleCrop().into(d_image);

        d_name.setText(doctorarraylist.get(position).getName());
        d_specialty.setText(doctorarraylist.get(position).getSpecialist());
//        d_qualification.setText(doctorarraylist.get(position).getQualification());
//        d_experiance.setText(doctorarraylist.get(position).getExperiance());


        return view;
    }
}
