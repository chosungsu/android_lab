package com.example.java_final_project.utils;

import android.app.Activity;
import android.widget.Toast;

import com.example.java_final_project.MainBoard;

/**
 * back 버튼 누르기 핸들러
 */
public class BackPressCloseHandler {
    private long backKeyClickTime = 0;
    private final Activity activity;
    public BackPressCloseHandler(Activity activity) {
        this.activity = activity;
    }
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyClickTime + 2000) {
            backKeyClickTime = System.currentTimeMillis();
            showToast();
            return;
        }
        if (System.currentTimeMillis() <= backKeyClickTime + 2000) {
            activity.finish();
        }
    }
    public void showToast() {
        Toast.makeText(activity, "뒤로 가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
    }
}

