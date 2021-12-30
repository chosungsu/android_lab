package com.example.java_final_project.model;

import android.graphics.Bitmap;

public class Walkview_items {
    String list_date, list_total_num, list_exercise_num, list_hour;
    float rates;

    public Walkview_items(String date, String list_total_num,
                          String list_exercise_num, String list_hour,
                          float rate) {
        this.list_date = date;
        this.list_total_num = list_total_num;
        this.list_exercise_num = list_exercise_num;
        this.list_hour = list_hour;
        this.rates = rate;
    }


    public String getList_date() {
        return list_date;
    }

    public String getList_exercise_num() {
        return list_exercise_num;
    }

    public String getList_hour() {
        return list_hour;
    }

    public String getList_total_num() {
        return list_total_num;
    }

    public float getRates() {
        return rates;
    }
}
