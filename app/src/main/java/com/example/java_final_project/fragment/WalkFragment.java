package com.example.java_final_project.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.java_final_project.MainBoard;
import com.example.java_final_project.adapter.PaperAdapter;
import com.example.java_final_project.adapter.WalkAdapter;
import com.example.java_final_project.model.Paperview_items;
import com.example.java_final_project.model.Walkview_items;
import com.example.java_final_project.utils.DataBaseHelper;
import com.example.java_final_project.utils.PedometerService;
import com.example.java_final_project.R;
import com.example.java_final_project.utils.SharedPreference;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * HomeFragment 소개
 * 이 프래그먼트에는 메인보드역할을 하는 각종 피드들을 개제할 목적으로 코딩되었다.
 */
public class WalkFragment extends Fragment{
    private View view;
    private LinearLayout li_step;
    private FrameLayout fr1, fr2;
    private DataBaseHelper myDB;
    private ImageView sort_pre, sort_post, walk_image;
    private TextView step, record, system, pedometer_status, start_stop;
    private RecyclerView recyclerView_h1;
    private Switch aSwitch;
    private Sensor stepDetectorSensor;
    private SensorManager sensorManager;
    private BottomSheetDialog bottomSheetDialog;
    private Intent intent;
    private String step_counter, your_name;
    private Date sd, fd;
    private boolean is_checked, is_started, activityRunning;
    private WalkAdapter walkAdapter;
    ArrayList<Walkview_items> walkview_items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_walk, container, false);

        /*
            객체 지정란
         */
        sort_pre = view.findViewById(R.id.sort_walks_pre);
        sort_post = view.findViewById(R.id.sort_walks_post);
        walk_image = view.findViewById(R.id.iv_walk_image);
        step = view.findViewById(R.id.step_num);
        start_stop = view.findViewById(R.id.health_start_stop);
        record = view.findViewById(R.id.tv_show_walk_lists);
        li_step = view.findViewById(R.id.tv_step_count);
        system = view.findViewById(R.id.tv_show_walksystem);
        pedometer_status = view.findViewById(R.id.tv_pedometer_start);
        aSwitch = view.findViewById(R.id.switch_pedometer);
        fr1 = view.findViewById(R.id.frame_health_center);
        fr2 = view.findViewById(R.id.frame_health_record);
        recyclerView_h1 = view.findViewById(R.id.walk_lists);

        /*
            툴바 이름 변경 및 뷰 모던화
         */
        FragmentActivity activity = getActivity();
        if (activity != null) {
            ((MainBoard) activity).setActionBarTitle("");
            ((MainBoard) activity).setActionBarSecondTitle("");
            ((MainBoard) activity).setFeed("건강센터");
            ((MainBoard) activity).setImageButton_l(0);
            ((MainBoard) activity).setImageButton_r(0);
        }

        myDB = new DataBaseHelper(requireContext());
        your_name = SharedPreference.getName(requireContext(), "Name");

        Log.i(String.valueOf(this), "1");
        ShowSystem();
        Change_Frame();
        ChangeCheck();
        Change_Pedometer();




        return view;
    }

    public void Change_Frame() {
        int popup = SharedPreference
                .getpopupmenu(requireContext(), "popup");
        boolean issort = SharedPreference
                .getSortWalks(requireContext(), "sort");
        switch (popup) {
            case 0:
                fr1.setVisibility(View.VISIBLE);
                sort_pre.setVisibility(View.GONE);
                sort_post.setVisibility(View.GONE);
                walk_image.setVisibility(View.GONE);
                fr2.setVisibility(View.GONE);
                record.setText("기록을 보시겠습니까?");
                record.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreference
                                .setpopupmenu(getContext(),
                                        "popup", 1);
                        fr1.setVisibility(View.GONE);
                        sort_pre.setVisibility(View.VISIBLE);
                        sort_post.setVisibility(View.VISIBLE);
                        walk_image.setVisibility(View.VISIBLE);
                        fr2.setVisibility(View.VISIBLE);
                        Change_Frame();
                    }
                });
                break;
            case 1:
                LinearLayout li_error;
                li_error = view.findViewById(R.id.li_error_walk);
                record.setText("측정화면으로 돌아가기");
                fr1.setVisibility(View.GONE);
                fr2.setVisibility(View.VISIBLE);
                walk_image.setVisibility(View.VISIBLE);
                if (issort) {//과거 데이터순 정렬
                    sort_pre.setVisibility(View.VISIBLE);
                    sort_post.setVisibility(View.GONE);
                    recyclerView_h1.setLayoutManager
                            (new LinearLayoutManager(getContext(),
                                    LinearLayoutManager.VERTICAL, false));
                    walkview_items.clear();
                    Cursor cursor = myDB.getWalkSaveData(your_name);
                    if (cursor.getCount() != 0) {
                        while (cursor.moveToNext()) {
                            if (cursor.getString(3) == null) {
                                walkview_items.add(
                                        new Walkview_items(
                                                cursor.getString(2),
                                                cursor.getString(1),
                                                cursor.getString(4),
                                                "아직 운동기록이 없습니다. 운동시작을 눌러 시작해보세요!",
                                                cursor.getFloat(5)
                                        )
                                );
                            } else {
                                int hour = Integer.parseInt(cursor.getString(3).substring(0,
                                        cursor.getString(3).indexOf(",")));
                                int minute = Integer.parseInt(cursor.getString(3)
                                        .substring(cursor.getString(3).indexOf(",") + 1));
                                walkview_items.add(
                                        new Walkview_items(
                                                cursor.getString(2),
                                                cursor.getString(1),
                                                cursor.getString(4),
                                                hour + "시간 " + minute + "분",
                                                cursor.getFloat(5)
                                        )
                                );
                            }

                        }
                    } else {

                    }

                    if (walkview_items.size() != 0) {
                        li_error.setVisibility(View.GONE);
                        recyclerView_h1.setVisibility(View.VISIBLE);
                        walkAdapter = new WalkAdapter
                                (getContext(), walkview_items);
                        recyclerView_h1.setAdapter(walkAdapter);
                    } else {
                        li_error.setVisibility(View.VISIBLE);
                        recyclerView_h1.setVisibility(View.GONE);
                    }
                    sort_pre.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SharedPreference
                                    .setSortWalks(getContext(),
                                            "sort", false);
                            sort_post.setVisibility(View.VISIBLE);
                            sort_pre.setVisibility(View.GONE);
                            Change_Frame();
                        }
                    });
                } else {
                    //최신 데이터순 정렬
                    sort_pre.setVisibility(View.GONE);
                    sort_post.setVisibility(View.VISIBLE);
                    recyclerView_h1.setLayoutManager
                            (new LinearLayoutManager(getContext(),
                                    LinearLayoutManager.VERTICAL, false));
                    walkview_items.clear();
                    Cursor cursor = myDB.getWalkSaveData(your_name);
                    if (cursor.getCount() != 0) {
                        while (cursor.moveToNext()) {
                            if (cursor.getString(3) == null) {
                                walkview_items.add(0,
                                        new Walkview_items(
                                                cursor.getString(2),
                                                cursor.getString(1),
                                                cursor.getString(4),
                                                "운동 기록이 없습니다.",
                                                cursor.getFloat(5)
                                        )
                                );
                            } else {
                                int hour = Integer.parseInt(cursor.getString(3).substring(0,
                                        cursor.getString(3).indexOf(",")));
                                int minute = Integer.parseInt(cursor.getString(3)
                                        .substring(cursor.getString(3).indexOf(",") + 1));
                                walkview_items.add(0,
                                        new Walkview_items(
                                                cursor.getString(2),
                                                cursor.getString(1),
                                                cursor.getString(4),
                                                hour + "시간 " + minute + "분",
                                                cursor.getFloat(5)
                                        )
                                );
                            }

                        }
                    } else {

                    }

                    if (walkview_items.size() != 0) {
                        li_error.setVisibility(View.GONE);
                        recyclerView_h1.setVisibility(View.VISIBLE);
                        walkAdapter = new WalkAdapter
                                (getContext(), walkview_items);
                        recyclerView_h1.setAdapter(walkAdapter);
                    } else {
                        li_error.setVisibility(View.VISIBLE);
                        recyclerView_h1.setVisibility(View.GONE);
                    }
                    sort_post.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SharedPreference
                                    .setSortWalks(getContext(),
                                            "sort", true);
                            sort_post.setVisibility(View.GONE);
                            sort_pre.setVisibility(View.VISIBLE);
                            Change_Frame();
                        }
                    });
                }

                record.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreference
                                .setpopupmenu(getContext(),
                                        "popup", 0);
                        fr1.setVisibility(View.VISIBLE);
                        sort_post.setVisibility(View.GONE);
                        sort_pre.setVisibility(View.GONE);
                        walk_image.setVisibility(View.GONE);
                        fr2.setVisibility(View.GONE);
                        Change_Frame();
                    }
                });

                break;
        }

    }

    public void ShowSystem() {
        system.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog = new BottomSheetDialog(
                        requireContext(), R.style.BottomSheetDialogTheme
                );
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_walksystem);
                bottomSheetDialog.setCanceledOnTouchOutside(true);
                bottomSheetDialog.setCancelable(true);
                bottomSheetDialog.show();
            }
        });
    }
    public void ChangeCheck() {
        /*
            스위치 On일 경우 포그라운드로 만보기 실행
            스위치 Off일 경우 포그라운드 실행 중지
         */
        Log.i(String.valueOf(this), "2");
        is_checked = SharedPreference
                .getCheckNum(requireContext(), "Pedo");
        aSwitch.setChecked(is_checked);
        if (is_checked) {
            pedometer_status.setText("만보기 ON");
            SharedPreference.setCheckNum
                    (requireContext(), "Pedo", true);
            //activityRunning = true;
            startService_pedo();
        } else {
            pedometer_status.setText("만보기 OFF");
            SharedPreference.setCheckNum
                    (requireContext(), "Pedo", false);
            //activityRunning = false;
            stopService_pedo();
        }
        new MainBoard();
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged
                    (CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    pedometer_status.setText("만보기 ON");
                    //activityRunning = true;
                    SharedPreference.setCheckNum
                            (requireContext(), "Pedo", true);
                    startService_pedo();
                } else {
                    pedometer_status.setText("만보기 OFF");
                    //activityRunning = false;
                    SharedPreference.setCheckNum
                            (requireContext(), "Pedo", false);
                    stopService_pedo();
                }
                new MainBoard();
            }
        });
    }

    public void Change_Pedometer() {
        Log.i(String.valueOf(this), "3");
        is_checked = SharedPreference
                .getCheckNum(requireContext(), "Pedo");
        is_started = SharedPreference.getStart(requireContext(), "START");
        Date date_now = Calendar.getInstance().getTime();
        SimpleDateFormat lf_start =
                new SimpleDateFormat("yyyyMMdd");

        if (is_started) {
            activityRunning = true;
            start_stop.setText("운동 종료");
            step.setText("데이터 기록 중...");
            /*Handler handler_loop = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Cursor cursor = myDB.getWalkSaveData(your_name);
                    while (cursor.moveToNext()) {
                        step_counter = cursor.getString(4);
                    }
                    step.setText("걸음 수 : " + step_counter);
                }
            };
            handler_loop.postDelayed(runnable, 3000);*/

            li_step.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder
                            (view.getContext());
                    builder.setTitle("NOTICE");
                    builder.setMessage("측정 종료하겠습니다.");
                    builder.setPositiveButton("네!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getContext(),
                                    "만보기 OFF",
                                    Toast.LENGTH_SHORT).show();
                            activityRunning = false;
                            step.setText("클릭하여 시작하기");
                            start_stop.setText("운동 시작");
                            SharedPreference.setStart(requireContext(), "START", false);

                            if (SharedPreference.getFinishDate(requireContext(),
                                    "Finish_Time_Walk").equals("null")) {
                                Log.i(String.valueOf(this), "(fin)");
                                SimpleDateFormat sdf = new SimpleDateFormat(
                                        "HH:mm:ss"
                                );
                                Date ftd = Calendar.getInstance().getTime();
                                SharedPreference.setFinishDate
                                        (requireContext(), "Finish_Time_Walk",
                                                sdf.format(ftd));
                            } else {
                                SharedPreference.setFinishDate(requireContext(), "Finish_Time_Walk",
                                        "null");
                                Log.i(String.valueOf(this), "(fin)");
                                SimpleDateFormat sdf = new SimpleDateFormat(
                                        "HH:mm:ss"
                                );
                                Date ftd = Calendar.getInstance().getTime();
                                SharedPreference.setFinishDate
                                        (requireContext(), "Finish_Time_Walk",
                                                sdf.format(ftd));
                            }
                            SimpleDateFormat sdf = new SimpleDateFormat(
                                    "HH:mm:ss"
                            );
                            long diff = 0;
                            String std_parse = SharedPreference.getStartDate(
                                    requireContext(), "Start_Time_Walk"
                            );
                            String ftd_parse = SharedPreference.getFinishDate(
                                    requireContext(), "Finish_Time_Walk"
                            );
                            Log.i(String.valueOf(this), "std" + std_parse);
                            Log.i(String.valueOf(this), "ftd" + ftd_parse);
                            try {
                                if (sdf.parse(ftd_parse).getTime() > sdf.parse(std_parse).getTime()) {
                                    diff = sdf.parse(ftd_parse).getTime() - sdf.parse(std_parse).getTime();
                                } else {
                                    diff = sdf.parse(std_parse).getTime() - sdf.parse(ftd_parse).getTime();
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Log.i(String.valueOf(this), String.valueOf(diff));
                            int hour = (int) (diff / (1000*60*60));
                            int min = (int) (diff / (1000*60) - 60*hour);
                            Log.i(String.valueOf(this), String.valueOf(min));
                            Log.i(String.valueOf(this), String.valueOf(hour));
                            Cursor cursor2 = myDB.getWalkSaveData(your_name);
                            if (cursor2.getCount() != 0) {
                                //db가 있을 경우
                                //새로운 데이터가 추가되면 그 데이터를 업데이트해야하므로
                                //movetolast로 제일 마지막행으로 가게끔 해준다.
                                for (cursor2.moveToLast(); !cursor2.isAfterLast(); cursor2.moveToNext()) {
                                    if (lf_start.format(date_now).equals(
                                            cursor2.getString(2)
                                    )) {
                                        //같은 날짜인 경우
                                        myDB.updateWalk(
                                                cursor2.getString(0),
                                                your_name,
                                                cursor2.getString(1),
                                                cursor2.getString(2),
                                                cursor2.getString(3),
                                                cursor2.getString(4),
                                                cursor2.getString(5)
                                        );
                                    } else {
                                        //다른 날짜인 경우 : 데이터베이스 추가
                                        boolean val1 = myDB.addWalkData(
                                                your_name,
                                                String.valueOf(0),
                                                lf_start.format(date_now),
                                                null,
                                                String.valueOf(0),
                                                String.valueOf(0)
                                        );
                                        if (val1) {
                                            Log.i(String.valueOf(this), "기록 저장 완료!");
                                            Toast.makeText(requireContext(),
                                                    "기록 저장 완료!",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {

                                        }
                                    }
                                    if (cursor2.getString(3) != null) {
                                        int hour_change = Integer.parseInt
                                                (cursor2.getString(3).substring(0,
                                                        cursor2.getString(3).indexOf(",")));
                                        hour_change += hour;
                                        int minute_change = Integer.parseInt
                                                (cursor2.getString(3)
                                                        .substring(cursor2.getString(3).indexOf(",") + 1));
                                        minute_change += min;
                                        myDB.updateWalk(
                                                cursor2.getString(0),
                                                your_name,
                                                cursor2.getString(1),
                                                cursor2.getString(2),
                                                hour_change + "," + minute_change,
                                                cursor2.getString(4),
                                                cursor2.getString(5)
                                        );
                                    } else {
                                        myDB.updateWalk(
                                                cursor2.getString(0),
                                                your_name,
                                                cursor2.getString(1),
                                                cursor2.getString(2),
                                                hour + "," + min,
                                                cursor2.getString(4),
                                                cursor2.getString(5)
                                        );
                                    }
                                }

                            } else {
                                //db가 없을 경우
                                boolean val1 = myDB.addWalkData(
                                        your_name,
                                        String.valueOf(0),
                                        null,
                                        hour + "," + min,
                                        null,
                                        String.valueOf(0)
                                );
                                if (val1) {
                                    Log.i(String.valueOf(this), "기록 저장 완료!");
                                    Toast.makeText(requireContext(),
                                            "기록 저장 완료!",
                                            Toast.LENGTH_SHORT).show();

                                } else {

                                }
                            }
                            //stopService_pedo();
                            Change_Pedometer();
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setNegativeButton("아직 더 할게요!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            start_stop.setText("운동 종료");
                            SharedPreference.setStart(requireContext(), "START", true);
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setCancelable(true);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        } else {
            activityRunning = false;
            start_stop.setText("운동 시작");
            step.setText("클릭하여 시작하기");
            li_step.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChangeCheck();
                    AlertDialog.Builder builder = new AlertDialog.Builder
                            (view.getContext());
                    builder.setTitle("NOTICE");
                    builder.setMessage("지금부터 운동 중의 걸음 수를 측정하겠습니다.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (String.valueOf(is_checked).equals("false")) {
                                //설정이 off일 때
                                AlertDialog.Builder builder = new AlertDialog.Builder
                                        (view.getContext());
                                builder.setTitle("앗! 알려드립니다!");
                                builder.setMessage("만보기 설정이 OFF상태입니다.");
                                builder.setPositiveButton("설정 변경", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        SharedPreference.setCheckNum
                                                (requireContext(), "Pedo", true);
                                        SharedPreference.setStart(requireContext(), "START", true);
                                        Toast.makeText(getContext(),
                                                "만보기 ON",
                                                Toast.LENGTH_SHORT).show();
                                        activityRunning = true;
                                        start_stop.setText("운동 종료");
                                        step.setText("데이터 기록 중...");
                                        /*Handler handler_loop = new Handler();
                                        Runnable runnable = new Runnable() {
                                            @Override
                                            public void run() {
                                                Cursor cursor = myDB.getWalkSaveData(your_name);
                                                if (cursor.getCount() != 0) {
                                                    while (cursor.moveToNext()) {
                                                        step_counter = cursor.getString(4);
                                                    }
                                                }
                                                step.setText("걸음 수 : " + step_counter);

                                            }
                                        };
                                        handler_loop.postDelayed(runnable, 3000);*/

                                        if (SharedPreference.getStartDate(requireContext(),
                                                "Start_Time_Walk").equals("null")) {
                                            Log.i(String.valueOf(this), "(std)");
                                            SimpleDateFormat sdf = new SimpleDateFormat(
                                                    "HH:mm:ss"
                                            );
                                            Date std = Calendar.getInstance().getTime();
                                            SharedPreference.setStartDate
                                                    (requireContext(), "Start_Time_Walk",
                                                            sdf.format(std));
                                        } else {
                                            SharedPreference.setStartDate(requireContext(), "Start_Time_Walk",
                                                    "null");
                                            Log.i(String.valueOf(this), "(std)");
                                            SimpleDateFormat sdf = new SimpleDateFormat(
                                                    "HH:mm:ss"
                                            );
                                            Date std = Calendar.getInstance().getTime();
                                            SharedPreference.setStartDate
                                                    (requireContext(), "Start_Time_Walk",
                                                            sdf.format(std));
                                        }
                                        ChangeCheck();
                                        Change_Pedometer();
                                        dialogInterface.dismiss();
                                    }
                                });
                                builder.setNegativeButton("변경 안함", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        start_stop.setText("운동 시작");
                                        activityRunning = false;
                                        step.setText("클릭하여 시작하기");
                                        SharedPreference.setStart(requireContext(), "START", false);
                                        dialogInterface.dismiss();
                                    }
                                });
                                builder.setCancelable(true);
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            } else {
                                //설정이 on일 때
                                Toast.makeText(getContext(),
                                        "만보기 ON",
                                        Toast.LENGTH_SHORT).show();
                                activityRunning = true;
                                start_stop.setText("운동 종료");
                                step.setText("데이터 기록 중...");
                                /*Handler handler_loop = new Handler();
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        Cursor cursor = myDB.getWalkSaveData(your_name);
                                        if (cursor.getCount() != 0) {
                                            while (cursor.moveToNext()) {
                                                step_counter = cursor.getString(4);
                                            }
                                        }
                                        step.setText("걸음 수 : " + step_counter);
                                    }
                                };
                                handler_loop.postDelayed(runnable, 3000);*/

                                if (SharedPreference.getStartDate(requireContext(),
                                        "Start_Time_Walk").equals("null")) {
                                    Log.i(String.valueOf(this), "(std)");
                                    SimpleDateFormat sdf = new SimpleDateFormat(
                                            "HH:mm:ss"
                                    );
                                    Date std = Calendar.getInstance().getTime();
                                    SharedPreference.setStartDate
                                            (requireContext(), "Start_Time_Walk",
                                                    sdf.format(std));
                                } else {
                                    SharedPreference.setStartDate(requireContext(), "Start_Time_Walk",
                                            "null");
                                    Log.i(String.valueOf(this), "(std)");
                                    SimpleDateFormat sdf = new SimpleDateFormat(
                                            "HH:mm:ss"
                                    );
                                    Date std = Calendar.getInstance().getTime();
                                    SharedPreference.setStartDate
                                            (requireContext(), "Start_Time_Walk",
                                                    sdf.format(std));
                                }

                                SharedPreference.setStart(requireContext(), "START", true);
                                Change_Pedometer();
                                dialogInterface.dismiss();
                            }


                        }
                    });
                    builder.setCancelable(true);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

            });

        }
    }
    public void stopService_pedo() {
        Intent intent = new Intent(requireContext(), PedometerService.class);
        requireContext().stopService(intent);
    }

    public void startService_pedo() {
        Intent intent;
        your_name = SharedPreference.getName(requireContext(), "Name");
        Cursor cursor = myDB.getWalkSaveData(your_name);
        int step;
        if (cursor.getCount() != 0) {
            //저장된 데이터가 있는 경우
            while (cursor.moveToNext()) {
                step = Integer.parseInt(cursor.getString(1));
                intent = new Intent(requireContext(), PedometerService.class);
                intent.putExtra("COUNT", step);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    requireContext().startForegroundService(intent);
                }
                requireContext().startService(intent);
            }
        } else {
            //최초의 호출로 데이터가 아직 없는 경우
            step = 0;
            intent = new Intent(requireContext(), PedometerService.class);
            intent.putExtra("COUNT", step);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requireContext().startForegroundService(intent);
            }
            requireContext().startService(intent);
        }

    }

}
