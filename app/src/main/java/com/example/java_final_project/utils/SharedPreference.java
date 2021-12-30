package com.example.java_final_project.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;

import java.util.ArrayList;

public final class SharedPreference {
    private final static String TAG = "Pref.start";
    public final static String ACCESS_KEY = "access_key";

    public SharedPreference() {
        throw new IllegalStateException("인스턴스 생성 금지");
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }
    public static void setLoginVal(Context context, String key, boolean data) {
        SharedPreferences pref = getPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, data);
        editor.apply();
    }
    public static void setName(Context context, String key, String data) {
        SharedPreferences pref = getPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, data);
        editor.apply();
    }
    public static void setFrags(Context context, String key, String data) {
        SharedPreferences pref = getPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, data);
        editor.apply();
    }
    public static void setStartDate(Context context, String key, String data) {
        SharedPreferences pref = getPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, data);
        editor.apply();
    }
    public static void setFinishDate(Context context, String key, String data) {
        SharedPreferences pref = getPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, data);
        editor.apply();
    }
    public static void setMakers(Context context, String key, int data) {
        SharedPreferences pref = getPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, data);
        editor.apply();
    }
    public static void setpopupmenu(Context context, String key, int data) {
        SharedPreferences pref = getPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, data);
        editor.apply();
    }
    public static void setProfile_To_Medi(Context context, String key, int data) {
        SharedPreferences pref = getPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, data);
        editor.apply();
    }

    public static void setNumPills(Context context, String key, int data) {
        SharedPreferences pref = getPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, data);
        editor.apply();
    }
    public static void setNumPapers(Context context, String key, int data) {
        SharedPreferences pref = getPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, data);
        editor.apply();
    }
    public static void setsort_paper(Context context, String key, int data) {
        SharedPreferences pref = getPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, data);
        editor.apply();
    }
    public static void setSelectPill(Context context, String key, int data) {
        SharedPreferences pref = getPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, data);
        editor.apply();
    }
    public static void setSavedPill(Context context, String key, boolean data) {
        SharedPreferences pref = getPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, data);
        editor.apply();
    }
    public static void setCheckNum(Context context, String key, boolean data) {
        SharedPreferences pref = getPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, data);
        editor.apply();
    }
    public static void setStart(Context context, String key, boolean data) {
        SharedPreferences pref = getPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, data);
        editor.apply();
    }
    public static void setSortWalks(Context context, String key, boolean data) {
        SharedPreferences pref = getPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, data);
        editor.apply();
    }

    public static boolean getLoginVal(Context context, String key) {
        SharedPreferences pref = getPreferences(context);
        return pref.getBoolean(key, false);
    }
    public static String getName(Context context, String key) {
        SharedPreferences pref = getPreferences(context);
        return pref.getString(key, "");
    }
    public static String getFrags(Context context, String key) {
        SharedPreferences pref = getPreferences(context);
        return pref.getString(key, "");
    }
    public static String getStartDate(Context context, String key) {
        SharedPreferences pref = getPreferences(context);
        return pref.getString(key, "null");
    }
    public static String getFinishDate(Context context, String key) {
        SharedPreferences pref = getPreferences(context);
        return pref.getString(key, "null");
    }
    public static int getMakers(Context context, String key) {
        SharedPreferences pref = getPreferences(context);
        return pref.getInt(key, 0);
    }
    public static int getpopupmenu(Context context, String key) {
        SharedPreferences pref = getPreferences(context);
        return pref.getInt(key, 0);
    }
    public static int getProfile_To_Medi(Context context, String key) {
        SharedPreferences pref = getPreferences(context);
        return pref.getInt(key, 0);
    }
    public static int getNumPills(Context context, String key) {
        SharedPreferences pref = getPreferences(context);
        return pref.getInt(key, 0);
    }
    public static int getNumPapers(Context context, String key) {
        SharedPreferences pref = getPreferences(context);
        return pref.getInt(key, 0);
    }
    public static int getsort_paper(Context context, String key) {
        SharedPreferences pref = getPreferences(context);
        return pref.getInt(key, 0);
    }
    public static int getSelectPill(Context context, String key) {
        SharedPreferences pref = getPreferences(context);
        return pref.getInt(key, 0);
    }
    public static boolean getSavedPill(Context context, String key) {
        SharedPreferences pref = getPreferences(context);
        return pref.getBoolean(key, false);
    }
    public static boolean getCheckNum(Context context, String key) {
        SharedPreferences pref = getPreferences(context);
        return pref.getBoolean(key, false);
    }
    public static boolean getStart(Context context, String key) {
        SharedPreferences pref = getPreferences(context);
        return pref.getBoolean(key, false);
    }
    public static boolean getSortWalks(Context context, String key) {
        SharedPreferences pref = getPreferences(context);
        return pref.getBoolean(key, false);
    }
}
