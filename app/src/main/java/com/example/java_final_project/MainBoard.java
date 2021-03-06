package com.example.java_final_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.util.LocaleData;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.java_final_project.fragment.HomeFragment;
import com.example.java_final_project.fragment.MediFragment;
import com.example.java_final_project.fragment.SettingFragment;
import com.example.java_final_project.fragment.WalkFragment;
import com.example.java_final_project.utils.BackPressCloseHandler;
import com.example.java_final_project.utils.Camera2_API;
import com.example.java_final_project.utils.DataBaseHelper;
import com.example.java_final_project.utils.PedometerService;
import com.example.java_final_project.utils.SharedPreference;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

/**
 * MainBoard
 * ?????? ??????????????????.
 * ??????????????? 4?????? ???????????? ??????.
 */
public class MainBoard extends AppCompatActivity implements SensorEventListener{

    //main??? ?????? ?????????
    private String what_frags, your_name;
    private Toolbar toolbar;
    private Bundle bundle;
    private Sensor stepDetectorSensor;
    private static SensorManager sensorManager;
    private int pills_num;
    private boolean is_checked, is_started;
    private DataBaseHelper myDB;
    private ImageButton imageButton_l, imageButton_r;
    private BottomNavigationView bottomNavigationView;
    private BackPressCloseHandler handler;
    private MediFragment mediFragment;
    private SettingFragment settingFragment;
    private HomeFragment homeFragment;
    private WalkFragment walkFragment;
    private TextView tv_toolbar_name, tv_second_name,
            tv_name_toolbar, tv_feed, tv_post, tv_path_image, tv_path;
    private ImageView iv_back, iv_info, iv_capture_pic;
    private Button btn_done_capture;
    private FragmentTransaction transaction;
    private BottomSheetDialog bottomSheetDialog0, bottomSheetDialog1, bottomSheetDialog2;
    private int MULTI_PERMISSION = 999;
    private final String[] permissionArray = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.FOREGROUND_SERVICE
    };
    private Camera2_API camera2_api;;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
            ?????? ??????
         */
        toolbar = findViewById(R.id.app_bar_main);
        tv_toolbar_name = findViewById(R.id.toolbar_title);
        tv_second_name = findViewById(R.id.toolbar_title_second);
        tv_feed = findViewById(R.id.feed);
        imageButton_l = findViewById(R.id.toolbar_menu_btn);
        imageButton_r = findViewById(R.id.toolbar_image_btn);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        mediFragment = new MediFragment();
        settingFragment = new SettingFragment();
        homeFragment = new HomeFragment();
        //mapFragment = new MapFragment();
        walkFragment = new WalkFragment();

        /*
            permission ????????????
         */
        checkPermission();

        /*
            UI ??????
         */
        myDB = new DataBaseHelper(this);
        your_name = SharedPreference.getName(this, "Name");
        sensorManager = (SensorManager) this
                .getSystemService(Context.SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(
                Sensor.TYPE_STEP_DETECTOR
        );
        PEDO();
        transaction =
                getSupportFragmentManager()
                        .beginTransaction();
        bottomNavigationView =
                findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        transaction
                .replace(R.id.fragment_container, homeFragment)
                .commitAllowingStateLoss();
        SharedPreference
                .setFrags(this,
                        "Fragment", "home");
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            FragmentTransaction transaction1 =
                    getSupportFragmentManager()
                            .beginTransaction();
            switch (item.getItemId()) {
                case R.id.nav_medi:
                    transaction1.replace(R.id.fragment_container
                            , mediFragment).commitAllowingStateLoss();
                    SharedPreference
                            .setFrags(this,
                                    "Fragment", "medi");
                    break;
                case R.id.nav_home:
                    transaction1.replace(R.id.fragment_container
                            , homeFragment).commitAllowingStateLoss();
                    SharedPreference
                            .setFrags(this,
                                    "Fragment", "home");
                    break;
                case R.id.nav_post:
                    PlusBottomSheet(transaction1);
                    break;
                case R.id.nav_walk:
                    /*transaction1.replace(R.id.fragment_container
                            , mapFragment).commitAllowingStateLoss();
                    SharedPreference
                            .setFrags(this,
                                    "Fragment", "map");*/
                    transaction1.replace(R.id.fragment_container
                            , walkFragment).commitAllowingStateLoss();
                    SharedPreference
                            .setFrags(this,
                                    "Fragment", "walk");
                    break;
                case R.id.nav_set:
                    transaction1.replace(R.id.fragment_container
                            , settingFragment).commitAllowingStateLoss();
                    SharedPreference
                            .setFrags(this,
                                    "Fragment", "set");
                    break;
            }
            return true;
        });
        /*
            ??????????????? ???????????? ????????????.
         */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        /*
            back?????? ????????? ?????? ???????????????.
         */
        handler = new BackPressCloseHandler(this);
    }

    /**
     * replaceFragment
     * ????????????????????? ?????? ?????????????????? ?????? ??? ????????? ????????????.
     * @param fragment
     * @param strings
     */
    public void replaceFragment(Fragment fragment, String strings) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace
                (R.id.fragment_container, fragment).commit();
        int popup = SharedPreference
                .getpopupmenu(this, "popup");
        int profile = SharedPreference
                .getProfile_To_Medi(this, "Medi");
        if (strings.equals("medi")) {
            switch (profile) {
                case 0:
                    break;
                case 1:
                    bottomNavigationView.setSelectedItemId(R.id.nav_medi);
                    break;
            }
        } else if (strings.equals("walk")) {
            switch (popup) {
                case 0:
                    SharedPreference
                            .setpopupmenu(this,
                                    "popup", 0);
                    bottomNavigationView.setSelectedItemId(R.id.nav_walk);
                    break;
                case 1:
                    SharedPreference
                            .setpopupmenu(this,
                                    "popup", 1);
                    bottomNavigationView.setSelectedItemId(R.id.nav_walk);
                    break;
            }
        } else {

        }

    }
    public void setActionBarTitle(String title) {
        if (tv_toolbar_name != null) {
            if (title.equals("")) {
                tv_toolbar_name.setVisibility(View.GONE);
            } else {
                tv_toolbar_name.setVisibility(View.VISIBLE);
                tv_toolbar_name.setText(title);
            }
        }
    }

    public void setActionBarSecondTitle(String title) {
        if (tv_second_name != null) {
            if (title.equals("")) {
                tv_second_name.setVisibility(View.GONE);
            } else {
                tv_second_name.setVisibility(View.VISIBLE);
                tv_second_name.setText(title);
            }
        }
    }

    public void setFeed(String title) {
        if (tv_feed != null) {
            if (title.equals("")) {
                tv_feed.setVisibility(View.GONE);
            } else {
                tv_feed.setVisibility(View.VISIBLE);
                tv_feed.setText(title);
            }
        }
    }

    public void setImageButton_l(int drawable) {

        if (imageButton_l != null) {
            if (drawable == 0) {
                imageButton_l.setVisibility(View.GONE);
            } else {
                imageButton_l.setVisibility(View.VISIBLE);
                imageButton_l.setImageResource(drawable);
            }
        }
    }

    public void setImageButton_r(int drawable) {
        if (imageButton_r != null) {
            if (drawable == 0) {
                imageButton_r.setVisibility(View.GONE);
            } else {
                imageButton_r.setVisibility(View.VISIBLE);
                imageButton_r.setImageResource(drawable);
            }
        }
    }

    /**
     * changeMainUI
     * ??? ????????? ???????????? ??????????????? ????????? ??? ?????? ?????????????????? ???????????? ????????? ????????????.
     * @param bottomSheetDialog
     */
    public void changeMainUI(BottomSheetDialog bottomSheetDialog) {
        what_frags = SharedPreference
                .getFrags(this, "Fragment");
        bottomSheetDialog.setOnDismissListener
                (new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                FragmentTransaction transaction =
                        getSupportFragmentManager()
                                .beginTransaction();
                if (what_frags.equals("medi")) {
                    //medifragment??? ????????????
                    mediFragment.PillAlarm();
                    transaction.replace(R.id.fragment_container
                            , mediFragment).commitAllowingStateLoss();
                    bottomNavigationView.setSelectedItemId(R.id.nav_medi);
                } else if (what_frags.equals("home")) {
                    //homefragment??? ????????????
                    homeFragment.Nums();
                    homeFragment.Paper();
                    transaction.replace(R.id.fragment_container
                            , homeFragment).commitAllowingStateLoss();
                    bottomNavigationView.setSelectedItemId(R.id.nav_home);
                } else if (what_frags.equals("walk")) {
                    transaction.replace(R.id.fragment_container
                            , walkFragment).commitAllowingStateLoss();
                    bottomNavigationView.setSelectedItemId(R.id.nav_walk);
                } else {
                    transaction.replace(R.id.fragment_container
                            , settingFragment).commitAllowingStateLoss();
                    bottomNavigationView.setSelectedItemId(R.id.nav_set);
                }
            }
        });

    }

    /**
     * PlusBottomSheet
     * ??? ????????? ???????????? ?????????????????? ?????? ???????????? ???????????? ????????? ??? ??? ??????????????? ???????????? ????????????.
     * @param transaction1
     */
    public void PlusBottomSheet(FragmentTransaction transaction1) {
        bottomSheetDialog0 = new BottomSheetDialog(
                this, R.style.BottomSheetDialogTheme
        );
        bottomSheetDialog0.setContentView(R.layout.bottom_sheet_postwhat);
        bottomSheetDialog0.setCanceledOnTouchOutside(true);
        LinearLayout li1, li2;
        li1 = bottomSheetDialog0.findViewById(R.id.gotomedicinecenter);
        li2 = bottomSheetDialog0.findViewById(R.id.gotoposthospitalpaper);

        if (li1 != null) {
            li1.setOnClickListener(view -> {
                SharedPreference.setMakers(this,
                        SharedPreference.ACCESS_KEY, 0);
                AddDialog(transaction1);
            });
        }
        if (li2 != null) {
            li2.setOnClickListener(view -> {
                SharedPreference.setMakers(this,
                        SharedPreference.ACCESS_KEY, 1);
                AddDialog(transaction1);
            });
        }
        changeMainUI(bottomSheetDialog0);
        bottomSheetDialog0.show();
    }
    public void AddDialog(FragmentTransaction transaction1) {
        //enter_key????????? framelayout??? ????????? ??? ????????? ????????????.
        int enter_key = SharedPreference.getMakers
                (this, SharedPreference.ACCESS_KEY);
        bottomSheetDialog0.dismiss();
        if (enter_key == 0) {
            Medi();
        } else if (enter_key == 1) {
            Hospital_paper();
        } else {

        }
    }

    /**
     * Medi
     * ??? ????????? ??? ???????????? ????????? ????????????.
     */
    public void Medi() {
        final Calendar c = new GregorianCalendar();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        EditText et_what_medicine, et_what_aches;
        TextView tv_what_first_date, tv_what_last_date;
        Button btn_finish_adding;
        ImageView iv_search;
        bottomSheetDialog1 = new BottomSheetDialog(
                this, R.style.BottomSheetDialogTheme
        );
        bottomSheetDialog1.setContentView(R.layout.bottom_sheet_medicine);
        bottomSheetDialog1.setCanceledOnTouchOutside(true);
        bottomSheetDialog1.setCancelable(true);
        et_what_medicine = bottomSheetDialog1.findViewById(R.id.add_medicine);
        et_what_aches = bottomSheetDialog1.findViewById(R.id.add_why_eat);
        //iv_search = bottomSheetDialog1.findViewById(R.id.iv_search_medicine);
        tv_what_first_date = bottomSheetDialog1.findViewById(R.id.add_first_date);
        tv_what_last_date = bottomSheetDialog1.findViewById(R.id.add_last_date);
        btn_finish_adding = bottomSheetDialog1.findViewById(R.id.btn_finish_add);
        //iv_back = bottomSheetDialog1.findViewById(R.id.iv_back_btn);
        iv_info = bottomSheetDialog1.findViewById(R.id.img_info);
        tv_name_toolbar = bottomSheetDialog1.findViewById(R.id.toolbar_title_makers);

        changeMainUI(bottomSheetDialog1);

        if (tv_name_toolbar != null) {
            tv_name_toolbar.setText("??? ??????");
        }

        if (iv_info != null) {
            iv_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("NOTICE");
                    builder.setMessage("??? ???????????? ???????????? ?????? ??? ??????\n??????????????? ?????????????????? ?????? ??? " +
                            "???????????? ???????????? ???????????? ????????????????????????.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }
        if (tv_what_first_date != null) {
            tv_what_first_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (android.os.Build.VERSION.SDK_INT
                            >= android.os.Build.VERSION_CODES.N) {
                        DatePickerDialog datePickerDialog =
                                new DatePickerDialog
                                        (MainBoard.this,
                                                new DatePickerDialog.OnDateSetListener() {
                                                    @Override
                                                    public void onDateSet
                                                            (DatePicker datePicker,
                                                             int year, int month, int day) {
                                                        tv_what_first_date.setText(
                                                                year + "-" + (month + 1) + "-" +
                                                                        day
                                                        );
                                                    }
                                                }, mYear, mMonth, mDay);
                        datePickerDialog.show();
                    }
                }
            });
        }
        if (tv_what_last_date != null) {
            tv_what_last_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (android.os.Build.VERSION.SDK_INT
                            >= android.os.Build.VERSION_CODES.N) {
                        DatePickerDialog datePickerDialog =
                                new DatePickerDialog
                                        (MainBoard.this,
                                                new DatePickerDialog.OnDateSetListener() {
                                                    @Override
                                                    public void onDateSet
                                                            (DatePicker datePicker,
                                                             int year, int month, int day) {
                                                        tv_what_last_date.setText(
                                                                year + "-" + (month + 1) + "-" +
                                                                        day
                                                        );
                                                    }
                                                }, mYear, mMonth, mDay);
                        datePickerDialog.show();
                    }
                }
            });
        }
        if (btn_finish_adding != null) {
            btn_finish_adding.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(String.valueOf(this),
                            "wait");
                    if (et_what_medicine != null && et_what_aches != null
                            && tv_what_first_date != null && tv_what_last_date != null) {
                        String a = et_what_medicine.getText().toString();
                        String b = et_what_aches.getText().toString();
                        String c = tv_what_first_date.getText().toString();
                        String d = tv_what_last_date.getText().toString();

                        if (!a.equals("") && !b.equals("") &&
                                !c.equals("") && !d.equals("")) {
                            boolean var = myDB.addPill(SharedPreference
                                            .getName(MainBoard.this,
                                                    "Name"),
                                    a, b, c, d, "?????? ?????? ????????????...");
                            if (var) {
                                Toast.makeText(MainBoard.this,
                                        "??????????????? ???????????? ??????????????????.",
                                        Toast.LENGTH_SHORT).show();

                                bottomSheetDialog1.dismiss();
                            } else {
                                Toast.makeText(MainBoard.this,
                                        "????????? ?????????????????????. ???????????? ??????????????????.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainBoard.this,
                                    "????????? ?????? ??? ???????????? ????????? ???????????????",
                                    Toast.LENGTH_SHORT).show();
                            if (a.equals("")) {
                                et_what_medicine.requestFocus();
                            } else if (b.equals("")) {
                                et_what_aches.requestFocus();
                            } else if (c.equals("")) {
                                tv_what_first_date.requestFocus();
                            } else {
                                tv_what_last_date.requestFocus();
                            }

                        }
                    }

                }
            });
        }

        bottomSheetDialog1.show();
    }

    /**
     * Hospital_paper
     * ???????????? ????????? ??? ?????? ??????????????? ???????????? ??????
     */
    private void Hospital_paper() {
        bottomSheetDialog2 = new BottomSheetDialog(
                this, R.style.BottomSheetDialogTheme
        );
        bottomSheetDialog2.setContentView(R.layout.bottom_sheet_paper);
        bottomSheetDialog2.setCanceledOnTouchOutside(true);
        bottomSheetDialog2.setCancelable(true);
        tv_path = bottomSheetDialog2.findViewById(R.id.tv_picture);
        tv_path_image = bottomSheetDialog2.findViewById(R.id.tv_path);
        //iv_back = bottomSheetDialog2.findViewById(R.id.iv_back_btn2);
        iv_info = bottomSheetDialog2.findViewById(R.id.img_info2);
        iv_capture_pic = bottomSheetDialog2.findViewById(R.id.iv_captured_pic);
        tv_name_toolbar = bottomSheetDialog2.findViewById(R.id.toolbar_title_makers2);
        //camera2??? ?????? ?????????
        TextureView textureView = bottomSheetDialog2.findViewById(R.id.textureview_1);

        btn_done_capture = bottomSheetDialog2.findViewById(R.id.btn_pick_paper);

        changeMainUI(bottomSheetDialog2);

        if (tv_name_toolbar != null) {
            tv_name_toolbar.setText("????????? ?????????");
        }
        /*if (iv_back != null) {
            iv_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetDialog2.dismiss();
                }
            });
        }*/
        if (iv_info != null) {
            iv_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("NOTICE");
                    builder.setMessage("??? ???????????? ???????????? ?????? ????????? ????????? \n?????????????????? ?????? ??? " +
                            "???????????? ???????????? \n????????? ???????????? ??????????????????");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }
        /*
            textureView ??????-?????????2_api??? ????????????
         */
        TextureView.SurfaceTextureListener textureListener =
                new TextureView.SurfaceTextureListener() {
                    @Override
                    public void onSurfaceTextureAvailable
                            (@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
                        camera2_api.openCamera();
                        //camera2_api.openCamera();
                    }

                    @Override
                    public void onSurfaceTextureSizeChanged
                            (@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

                    }

                    @Override
                    public boolean onSurfaceTextureDestroyed
                            (@NonNull SurfaceTexture surfaceTexture) {
                        return false;
                    }

                    @Override
                    public void onSurfaceTextureUpdated
                            (@NonNull SurfaceTexture surfaceTexture) {

                    }
                };
        if (textureView != null) {
            textureView.setSurfaceTextureListener(textureListener);
            camera2_api = new Camera2_API
                    (this, textureView, iv_capture_pic, tv_path_image);
        }

        btn_done_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera2_api.takePicture();
                tv_path.setVisibility(View.GONE);
                btn_done_capture.setText("?????????");
                btn_done_capture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bitmap bitmap = BitmapFactory.decodeFile
                                (tv_path_image.getText().toString());
                        /*Bitmap resizebitmap =
                                Bitmap.createScaledBitmap(
                                        bitmap, 300,
                                        200, false
                                );*/
                        Matrix rotate = new Matrix();
                        rotate.preRotate(90);
                        Bitmap resizebitmap =
                                Bitmap.createBitmap(
                                        bitmap, 0, 0,  bitmap.getWidth(),
                                        bitmap.getHeight(), rotate, false
                                );
                        String date = new SimpleDateFormat
                                ("yyyy-MM-dd/hh:mm:ss", Locale.getDefault())
                                .format(Calendar.getInstance().getTime());
                        boolean var = myDB.addPaper(SharedPreference
                                        .getName(MainBoard.this,
                                                "Name"),
                                camera2_api.bitmapToUri(resizebitmap),
                                date);
                        if (var) {
                            Toast.makeText(MainBoard.this,
                                    "??????????????? ???????????? ??????????????????.",
                                    Toast.LENGTH_SHORT).show();
                            bottomSheetDialog2.dismiss();
                        } else {
                            Toast.makeText(MainBoard.this,
                                    "????????? ?????????????????????. ???????????? ??????????????????.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
        bottomSheetDialog2.show();

    }

    /**
     * PEDO
     * ??? ????????? ??????????????? ????????? ?????? ???????????? ???????????? ???????????? ????????? ????????????.
     */
    public void PEDO() {
        is_checked = SharedPreference
                .getCheckNum(this, "Pedo");
        if (is_checked) {
            startService_pedo();
        } else {
            stopService_pedo();
        }
    }
    public void stopService_pedo() {
        sensorManager.unregisterListener(this);
        Intent intent = new Intent(this, PedometerService.class);
        stopService(intent);
    }

    public void startService_pedo() {
        Intent intent;
        your_name = SharedPreference.getName(this, "Name");
        if (stepDetectorSensor != null) {
            sensorManager.registerListener
                    (this, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
        } else {

        }
        Date date_now = Calendar.getInstance().getTime();
        SimpleDateFormat lf_start =
                new SimpleDateFormat("yyyyMMdd");
        Cursor cursor = myDB.getWalkSaveData(your_name);
        int step;
        if (cursor.getCount() != 0) {
            //????????? ???????????? ?????? ??????
            for (cursor.moveToLast(); !cursor.isAfterLast(); cursor.moveToNext()) {
                if (cursor.getString(2).equals(lf_start.format(date_now))) {
                    step = Integer.parseInt(cursor.getString(1));
                    intent = new Intent(this, PedometerService.class);
                    intent.putExtra("COUNT", step);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    }
                    startService(intent);
                } else {

                }
            }

            /*while (cursor.moveToNext()) {
                if (cursor.getString(2).equals(lf_start.format(date_now))) {
                    step = Integer.parseInt(cursor.getString(1));
                    intent = new Intent(this, PedometerService.class);
                    intent.putExtra("COUNT", step);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    }
                    startService(intent);
                } else {

                }

            }*/

        } else {
            //????????? ????????? ???????????? ?????? ?????? ??????
            step = 0;
            intent = new Intent(this, PedometerService.class);
            intent.putExtra("COUNT", step);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            }
            startService(intent);
        }

    }

    @Override
    public void onBackPressed() {
        handler.onBackPressed();
    }



    /**
     * ??? ?????? ????????? ?????? ?????? ????????? ?????? ????????????
     */
    public void checkPermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;
        try {
            ArrayList<String> permissionOk = new ArrayList<>();
            ArrayList<String> permissionNo = new ArrayList<>();
            if (permissionArray.length > 0) {
                for (String data : permissionArray) {
                    int check = ContextCompat.checkSelfPermission
                            (this, data);
                    if (check != PackageManager.PERMISSION_GRANTED) {
                        //?????? ???????????? ?????? ??????
                        permissionNo.add(data);
                    } else {
                        //?????? ????????? ??????
                        permissionOk.add(data);
                    }
                }
                if (permissionNo.size() > 0) {
                    //????????? ????????? ???????????? ?????? ?????? ??????
                    ActivityCompat.requestPermissions
                            (this, permissionNo.toArray
                                    (new String[permissionNo.size()]), MULTI_PERMISSION);
                } else {
                    return;
                }
            } else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        ArrayList<String> permissionNoRealTime = new ArrayList<>();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MULTI_PERMISSION) {
            if (grantResults.length > 0) {
                //?????? ????????? ??????????????? ?????? ??????
                permissionNoRealTime.size();
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] != PackageManager
                            .PERMISSION_GRANTED) {
                        permissionNoRealTime.add(permissions[i]);
                    }
                }
                if (permissionNoRealTime.size() > 0) {
                    String title = "????????? ?????? ??????";
                    String message = "???????????? ???????????? ?????? ?????????????????????.\n" +
                            "[??????-??????]?????? ?????????????????? ?????????.\n" +
                            "?????? ?????????????????? ????????? ??????????????????!";
                    String buttonNo = "??? ???";
                    String buttonYes = "??? ???";
                    new AlertDialog.Builder(this)
                            .setTitle(title)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton(buttonYes,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            try {
                                                Intent intent = new Intent(
                                                        Settings
                                                                .ACTION_APPLICATION_DETAILS_SETTINGS
                                                );
                                                intent.setData(Uri.parse("package:" +
                                                        getPackageName()));
                                                startActivity(intent);
                                            }catch (ActivityNotFoundException e) {
                                                e.printStackTrace();
                                                Intent intent = new Intent(Settings.
                                                        ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                                startActivity(intent);
                                            }
                                            overridePendingTransition(0, 0);
                                        }
                                    })
                            .setNegativeButton(buttonNo,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            try {
                                                finishAffinity();
                                                overridePendingTransition(0, 0);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }).show();
                }
            } else {

            }
        }

    }


    /**
     * onSensorChanged
     * ??? ????????? ????????? ?????? ????????? db??? ????????? ???????????? ????????? ??????.
     * @param sensorEvent
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensor_total = 0;
        String isnull;
        int total_walk, real_walk = 0;
        is_checked = SharedPreference
                .getCheckNum(this, "Pedo");
        is_started = SharedPreference
                .getStart(this, "START");
        Date date_now = Calendar.getInstance().getTime();
        SimpleDateFormat lf_start =
                new SimpleDateFormat("yyyyMMdd");
        if (is_checked) {
            //????????? ????????? ?????? ?????? ?????? ??????
            if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                if (sensorEvent.values[0] == 1.0f) {
                    sensor_total+=1;
                    Cursor cursor4 = myDB.getWalkSaveData(your_name);
                    if (is_started) {
                        //?????? ???????????? ????????? ??????
                        if (cursor4.getCount() != 0) {
                            //db??? ?????? ??????
                            //????????? ???????????? ???????????? ??? ???????????? ???????????????????????????
                            //movetolast??? ?????? ?????????????????? ????????? ?????????.
                            for (cursor4.moveToLast();
                                 !cursor4.isAfterLast();
                                 cursor4.moveToNext()) {
                                isnull = cursor4.getString(4);
                                if (isnull != null) {
                                    real_walk = Integer.parseInt(isnull);
                                } else {
                                    real_walk = 0;
                                }
                                real_walk += 1;
                                total_walk = Integer.parseInt(cursor4.getString(1));
                                total_walk += 1;

                                if (lf_start.format(date_now).equals(
                                        cursor4.getString(2)
                                )) {
                                    //?????? ????????? ??????
                                    myDB.updateWalk(
                                            cursor4.getString(0),
                                            your_name,
                                            String.valueOf(total_walk),
                                            lf_start.format(date_now),
                                            cursor4.getString(3),
                                            String.valueOf(real_walk),
                                            cursor4.getString(5)
                                    );
                                } else {
                                    //?????? ????????? ?????? : ?????????????????? ??????
                                    boolean val1 = myDB.addWalkData(
                                            your_name,
                                            String.valueOf(sensor_total),
                                            lf_start.format(date_now),
                                            null,
                                            String.valueOf(real_walk),
                                            String.valueOf(0)
                                    );
                                    if (val1) {
                                        Log.i(String.valueOf(this), "?????? ?????? ??????!");
                                        Toast.makeText(this,
                                                "?????? ?????? ??????!",
                                                Toast.LENGTH_SHORT).show();
                                    } else {

                                    }
                                }
                            }

                        } else {
                            //db??? ?????? ??????
                            boolean val1 = myDB.addWalkData(
                                    your_name,
                                    String.valueOf(0),
                                    lf_start.format(date_now),
                                    null,
                                    String.valueOf(0),
                                    String.valueOf(0)
                            );
                            if (val1) {
                                Log.i(String.valueOf(this), "?????? ?????? ??????!");
                                Toast.makeText(this,
                                        "?????? ?????? ??????!",
                                        Toast.LENGTH_SHORT).show();
                            } else {

                            }
                        }

                    } else {
                        //?????? ?????? ?????? ?????????
                        if (cursor4.getCount() != 0) {
                            //db??? ?????? ??????
                            //????????? ???????????? ???????????? ??? ???????????? ???????????????????????????
                            //movetolast??? ?????? ?????????????????? ????????? ?????????.
                            for (cursor4.moveToLast(); !cursor4.isAfterLast(); cursor4.moveToNext()) {
                                isnull = cursor4.getString(4);
                                total_walk = Integer.parseInt(cursor4.getString(1));
                                total_walk += 1;
                                if (lf_start.format(date_now).equals(
                                        cursor4.getString(2)
                                )) {
                                    //?????? ????????? ??????
                                    myDB.updateWalk(
                                            cursor4.getString(0),
                                            your_name,
                                            String.valueOf(total_walk),
                                            lf_start.format(date_now),
                                            cursor4.getString(3),
                                            isnull,
                                            cursor4.getString(5)
                                    );
                                } else {
                                    //?????? ????????? ?????? : ?????????????????? ??????
                                    boolean val1 = myDB.addWalkData(
                                            your_name,
                                            String.valueOf(sensor_total),
                                            lf_start.format(date_now),
                                            null,
                                            String.valueOf(real_walk),
                                            String.valueOf(0)
                                    );
                                    if (val1) {
                                        Log.i(String.valueOf(this), "?????? ?????? ??????!");
                                        Toast.makeText(this,
                                                "?????? ?????? ??????!",
                                                Toast.LENGTH_SHORT).show();
                                    } else {

                                    }
                                }
                            }

                        } else {
                            //db??? ?????? ??????
                            boolean val1 = myDB.addWalkData(
                                    your_name,
                                    String.valueOf(0),
                                    lf_start.format(date_now),
                                    null,
                                    String.valueOf(0),
                                    String.valueOf(0)
                            );
                            if (val1) {
                                Log.i(String.valueOf(this), "?????? ?????? ??????!");
                                Toast.makeText(this,
                                        "?????? ?????? ??????!",
                                        Toast.LENGTH_SHORT).show();
                            } else {

                            }
                        }

                    }

                    startService_pedo();
                    homeFragment.Fitness(your_name);
                }
            }
        } else {
            /*
            //?????? ????????? ?????? ?????? ??????
            Cursor cursor4 = myDB.getWalkSaveData(your_name);
            if (cursor4.getCount() != 0) {
                //db??? ?????? ??????
                //????????? ???????????? ???????????? ??? ???????????? ???????????????????????????
                //movetolast??? ?????? ?????????????????? ????????? ?????????.
                cursor4.moveToLast();
                while (cursor4.moveToNext()) {
                    isnull = cursor4.getString(4);
                    total_walk = Integer.parseInt(cursor4.getString(1));
                    myDB.updateWalk(
                            cursor4.getString(0),
                            your_name,
                            String.valueOf(total_walk),
                            lf_start.format(date_now),
                            cursor4.getString(3),
                            isnull
                    );
                }

            } else {
                //db??? ?????? ??????
                boolean val1 = myDB.addWalkData(
                        your_name,
                        String.valueOf(sensor_total),
                        lf_start.format(date_now),
                        null,
                        String.valueOf(real_walk)
                );
                if (val1) {
                    Log.i(String.valueOf(this), "?????? ?????? ??????!");
                    Toast.makeText(this,
                            "?????? ?????? ??????!",
                            Toast.LENGTH_SHORT).show();
                } else {

                }
            }*/
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}