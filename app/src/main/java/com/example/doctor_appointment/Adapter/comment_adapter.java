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
import com.example.doctor_appointment.model.comment_info;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class comment_adapter extends ArrayAdapter<comment_info> {
    Context context;
    List<comment_info>commentArraylist;
    public comment_adapter(Context context, ArrayList<comment_info>commentArraylist){
        super(context, R.layout.comment_list, commentArraylist);
        this.context=context;
        this.commentArraylist=commentArraylist;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view= convertView;
    if (view==null){
        view = LayoutInflater.from(getContext()).inflate(R.layout.comment_list, parent, false);

    }

        TextView user_name=view.findViewById(R.id.user_name);
        TextView comment=view.findViewById(R.id.comment);





        user_name.setText(commentArraylist.get(position).getComment_sender());
        comment.setText(commentArraylist.get(position).getComment());




        return view;
    }
}



