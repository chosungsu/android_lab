package com.example.java_final_project.model;

import android.graphics.Bitmap;

public class Paperview_items {
    String list_date;
    Bitmap list_bitmap_paper;

    public Paperview_items(String date,
                           Bitmap hospital_name) {
        this.list_date = date;
        this.list_bitmap_paper = hospital_name;
    }


    public String getList_date() {
        return list_date;
    }
    public Bitmap getList_bitmap_paper() {
        return list_bitmap_paper;
    }
}
