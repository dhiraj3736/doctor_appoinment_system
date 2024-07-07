package com.example.doctor_appointment.model;

public class comment_info {

    String comment_sender;
    String comment;

    public comment_info(String comment_sender,String comment){
        this.comment_sender=comment_sender;
        this.comment=comment;

    }

    public String getComment_sender() {
        return comment_sender;
    }

    public void setComment_sender(String comment_sender) {
        this.comment_sender = comment_sender;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
