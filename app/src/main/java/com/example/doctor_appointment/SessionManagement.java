package com.example.doctor_appointment;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManagement {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARE_PRE_NAME="session";
    String SESSION_KEY= "session_user";

    public SessionManagement(Context context){
        sharedPreferences=context.getSharedPreferences(SHARE_PRE_NAME,Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    public void saveSession(User user){

        int id=user.getId();
        editor.putInt(SESSION_KEY,id).commit();

    }

    public int getSession(){
        return sharedPreferences.getInt(SESSION_KEY,-1);

    }


    public void removeSession(){
        editor.putInt(SESSION_KEY,-1).commit();
    }

}
