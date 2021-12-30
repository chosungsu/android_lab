package com.example.java_final_project;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.java_final_project.adapter.AdAdapter;
import com.example.java_final_project.model.Ads_items;
import com.example.java_final_project.utils.BackPressCloseHandler;

import java.util.ArrayList;

/**
 * SplashActivity
 * 사용자에게 앱에 대한 설명을 해주는 스플래쉬액티비티
 */
public class SplashActivity extends AppCompatActivity {

    private AdAdapter adAdapter;
    private ViewPager viewPager;
    ArrayList<Ads_items> ads_items = new ArrayList<>();
    private BackPressCloseHandler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        viewPager = findViewById(R.id.vp_main_ad);

        AddInfo();
        handler = new BackPressCloseHandler(this);
    }


    /*
        앱에 대한 설명을 해주는 함수
     */
    public void AddInfo() {
        ads_items.clear();
        ads_items.add(new Ads_items(R.drawable.ic_baseline_healing_24, "건강을 놓치지 않고 싶은 당신", " 맞춤형 건강 플랫폼이 되고자 하는 \n힐온유가 당신을 기다리고 있었답니다."));
        ads_items.add(new Ads_items(R.drawable.ic_baseline_monitor_heart_24, "많고 많은 약들과 처방전들", " 힐온유는 당신의 일상에 도움이 되고자 노력합니다."));
        ads_items.add(new Ads_items(R.drawable.ic_baseline_emoji_people_24, "헬스케어시대 현명한 선택", " 바로 지금 느껴보시기 바랍니다."));
        adAdapter = new AdAdapter(ads_items,this);
        viewPager.setAdapter(adAdapter);
        viewPager.setPadding(130, 30, 130, 100);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        handler.onBackPressed();
    }
}
