package com.example.doctor_appointment.Adapter;



import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doctor_appointment.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.DayViewHolder> {

    private List<String> days;
    private OnItemClickListener listener;
    private int today;

    public DaysAdapter(List<String> days, OnItemClickListener listener) {
        this.days = days;
        this.listener = listener;
        Calendar calendar = Calendar.getInstance();
        this.today = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        holder.tvDay.setText(days.get(position));
        holder.tvDayOfWeek.setText(getDayOfWeek(days.get(position))); // Correct day of the week

        // Highlight the current day
        if (days.get(position).equals(String.valueOf(today))) {
            holder.dayContainer.setBackgroundResource(R.drawable.bg_day_box2);
            holder.tvDay.setTextColor(Color.WHITE);
            holder.tvDayOfWeek.setTextColor(Color.WHITE);

        } else {
            holder.dayContainer.setBackgroundResource(R.drawable.bg_day_box);
            holder.tvDay.setTextColor(Color.parseColor("#70000000"));
            holder.tvDayOfWeek.setTextColor(Color.parseColor("#70000000"));
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(days.get(position)));
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;
        TextView tvDayOfWeek;
        View dayContainer;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tv_day);
            tvDayOfWeek = itemView.findViewById(R.id.tv_day_of_week);
            dayContainer = itemView.findViewById(R.id.day_container);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String day);
    }

    private String getDayOfWeek(String day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
        return new SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.getTime());
    }
}
