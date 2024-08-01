package com.example.doctor_appointment.model;

public class notification_list {

    String name,doctor,date,notification_id,read_at,b_id;
    public notification_list(String name,String doctor, String date,String notification_id,String read_at,String b_id) {
        this.name=name;
    this.doctor=doctor;
        this.date=date;
        this.notification_id=notification_id;
        this.read_at=read_at;
        this.b_id=b_id;
    }

    public String getB_id() {
        return b_id;
    }

    public void setB_id(String b_id) {
        this.b_id = b_id;
    }

    public String getRead_at() {
        return read_at;
    }

    public void setRead_at(String read_at) {
        this.read_at = read_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDoctor() {
        return doctor;
    }


    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }


    public String getDate() {
        return date;
    }


    public void setDate(String date) {
        this.date = date;
    }

    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }
}
