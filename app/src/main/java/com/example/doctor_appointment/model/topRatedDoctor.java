package com.example.doctor_appointment.model;

import androidx.annotation.Nullable;

public class topRatedDoctor {
    String d_id, name,specialist,qualification,experiance,rating_value;
    String image;

    public topRatedDoctor(String d_id,String name, String specialist, String qualification, String experiance, String image) {
        this.d_id=d_id;
        this.name = name;
        this.specialist = specialist;
        this.qualification = qualification;
        this.experiance = experiance;
        this.image = image;

    }
    public topRatedDoctor(String d_id,String name, String specialist, String qualification, String experiance, String image,String rating_value) {
        this.d_id=d_id;
        this.name = name;
        this.specialist = specialist;
        this.qualification = qualification;
        this.experiance = experiance;
        this.image = image;
        this.rating_value=rating_value;

    }

    public String getD_id() {
        return d_id;
    }

    public void setD_id(String d_id) {
        this.d_id = d_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialist() {
        return specialist;
    }

    public void setSpecialist(String specialist) {
        this.specialist = specialist;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getExperiance() {
        return experiance;
    }

    public void setExperiance(String experiance) {
        this.experiance = experiance;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRating_value() {
        return rating_value;
    }

    public void setRating_value(String rating_value) {
        this.rating_value = rating_value;
    }
}
