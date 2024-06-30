package com.example.doctor_appointment;

public class Endpoints {
    private static final String BASE_URL = "http://192.168.1.12:8080/api/";


    public static final String login = BASE_URL + "auth";
    public static final String register = BASE_URL + "register";

    public static final String get_user_info = BASE_URL + "user_info";

    public static final String get_service_name=BASE_URL + "service_name";

    public  static final String get_service_info=BASE_URL + "Service_info";

    public  static final String get_doctor_info=BASE_URL + "doctor_info";

    public  static final String get_doctor_info_for_userdashboard=BASE_URL + "doctor_info_for_userdashboard";


}
