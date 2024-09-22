package com.example.doctor_appointment.model;

public class reportList {
    String doctor,report,date;

    public reportList(String doctor, String report, String date) {
        this.doctor = doctor;
        this.report = report;
        this.date = date;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
