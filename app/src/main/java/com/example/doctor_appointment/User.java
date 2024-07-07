package com.example.doctor_appointment;

public class User {

    int id;
    String name;

    public User(int session_id, String user_info) {
        this.id=session_id;
        this.name=user_info;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
