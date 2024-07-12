package com.example.doctor_appointment.model;

public class bookingList {
    private String doctorName;
    private String specialization;
    private String date;
    private String time;
    private String image;

    private  String reason;
    private  int b_id;
    private  int d_id;


    public bookingList(int d_id,int b_id,String doctorName, String specialization, String date, String time,String image,String reason) {
       this.d_id=d_id;
        this.b_id=b_id;
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.date = date;
        this.time = time;
        this.image=image;
        this.reason=reason;
    }

    public int getD_id() {
        return d_id;
    }

    public void setD_id(int d_id) {
        this.d_id = d_id;
    }

    public int getB_id() {
        return b_id;
    }

    public void setB_id(int b_id) {
        this.b_id = b_id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
