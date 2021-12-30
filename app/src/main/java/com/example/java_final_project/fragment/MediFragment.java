package com.example.java_final_project.fragment;


import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.wifi.aware.WifiAwareManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.java_final_project.MainBoard;
import com.example.java_final_project.R;
import com.example.java_final_project.adapter.PillAdapter;
import com.example.java_final_project.model.Pill_items;
import com.example.java_final_project.utils.DataBaseHelper;
import com.example.java_final_project.utils.SharedPreference;

import java.util.ArrayList;

public class MediFragment extends Fragment {
    private View view;
    private ScrollView scrollView;
    private LinearLayout li_list, li_error;
    private RecyclerView recyclerView_pill;
    private PillAdapter pillAdapter;
    ArrayList<Pill_items> pill_items = new ArrayList<>();
    private DataBaseHelper myDB;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_pill_noti, container, false);


        /*
            툴바타이틀 변경 및 뷰 모던화
         */
        myDB = new DataBaseHelper(getContext());
        FragmentActivity activity = getActivity();
        if (activity != null) {
            ((MainBoard) activity).setActionBarTitle("");
            ((MainBoard) activity).setActionBarSecondTitle("");
            ((MainBoard) activity).setFeed("복용 기록");
            ((MainBoard) activity).setImageButton_l(0);
            ((MainBoard) activity).setImageButton_r(0);
        }
        PillAlarm();

        return view;
    }
    /*
        이 함수는 복용할 약 목록을 보여주기 위한 어댑터 생성함수이다.
     */
    public void PillAlarm() {
        /*
            객체 지정란
         */
        scrollView = view.findViewById(R.id.sv_pill);
        li_error = view.findViewById(R.id.li_error_pill);
        li_list = view.findViewById(R.id.li_all_list_pills);
        recyclerView_pill = view.findViewById(R.id.pills_lists);
        li_error.setVisibility(View.GONE);
        li_list.setVisibility(View.VISIBLE);
        recyclerView_pill.setLayoutManager
                (new LinearLayoutManager(getContext(),
                        LinearLayoutManager.VERTICAL, false));
        String name = SharedPreference
                .getName(getContext(),
                        "Name");
        pill_items.clear();
        String id, pill_name, ache, f_d, l_d, alarm;
        Cursor cursor = myDB.getUserPills(name);
        while (cursor.moveToNext()) {
            id = cursor.getString(0);
            pill_name = cursor.getString(1);
            ache = cursor.getString(2);
            f_d = cursor.getString(3);
            l_d = cursor.getString(4);
            alarm = cursor.getString(5);
            pill_items.add(0,
                    new Pill_items(id, pill_name,
                            ache, f_d, l_d, alarm)
            );
        }
        if (pill_items.size() != 0) {
            li_error.setVisibility(View.GONE);
            li_list.setVisibility(View.VISIBLE);
            pillAdapter = new PillAdapter(getContext(), pill_items);
            recyclerView_pill.setAdapter(pillAdapter);
        } else {
            li_error.setVisibility(View.VISIBLE);
            li_list.setVisibility(View.GONE);
        }
        cursor.close();
    }



    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
