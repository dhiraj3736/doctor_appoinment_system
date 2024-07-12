package com.example.doctor_appointment.Adapter;

import android.content.Context;
import android.content.Intent;
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
        TextView rating_value=view.findViewById(R.id.rating_value);
        CardView cardView=view.findViewById(R.id.card);



        Glide.with(context).load(doctorarraylist.get(position).getImage()).circleCrop().into(d_image);
        String d_id=(doctorarraylist.get(position).getD_id());
        d_name.setText(doctorarraylist.get(position).getName());
        d_specialty.setText(doctorarraylist.get(position).getSpecialist());
        rating_value.setText(doctorarraylist.get(position).getRating_value());
//        d_qualification.setText(doctorarraylist.get(position).getQualification());
//        d_experiance.setText(doctorarraylist.get(position).getExperiance());


        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, doctor_profile.class);
                intent.putExtra("doctor_id",d_id);
                context.startActivity(intent);


            }
        });

        return view;
    }
}
