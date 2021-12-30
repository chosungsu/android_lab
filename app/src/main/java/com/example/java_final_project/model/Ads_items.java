package com.example.java_final_project.model;

public class Ads_items {
    private int img;
    private String title;
    private String serve;

    public Ads_items(int img, String title, String desc) {
        this.img = img;
        this.title = title;
        this.serve = desc;

    }
    public int getImg() {
        return img;
    }
    public void setImg(int img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getServe() {
        return serve;
    }

    public void setServe(String serve) {
        this.serve = serve;
    }
}
