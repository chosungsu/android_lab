package com.example.java_final_project.model;

public class Pill_items{
    String id, list_pill, list_aches,
            list_st_date, list_lt_date, alarm;
    boolean is_checked;

    public Pill_items(String id, String list_pill, String list_aches,
                      String list_st_date, String list_lt_date,
                      String alarms) {
        this.id = id;
        this.list_pill = list_pill;
        this.list_aches = list_aches;
        this.list_st_date = list_st_date;
        this.list_lt_date = list_lt_date;
        this.alarm = alarms;
    }

    public Pill_items(String id, String pill_name, String ache,
                      String f_d, String l_d, boolean is_checked) {
        this.id = id;
        this.list_pill = pill_name;
        this.list_aches = ache;
        this.list_st_date = f_d;
        this.list_lt_date = l_d;
        this.is_checked = is_checked;
    }


    public String getId() {
        return id;
    }
    public String getList_pill() {
        return list_pill;
    }
    public String getList_aches() {
        return list_aches;
    }
    public String getList_st_date() {
        return list_st_date;
    }
    public String getList_lt_date() {
        return list_lt_date;
    }
    public String getAlarm() {
        return alarm;
    }
    public String getIs_checked() {
        return String.valueOf(is_checked);
    }
    public void setIs_checked(boolean is_checked) {
        this.is_checked = is_checked;
    }
}
