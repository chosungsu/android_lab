package com.example.java_final_project;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.java_final_project.adapter.CheckAdapter;
import com.example.java_final_project.adapter.SummaryAdapter;
import com.example.java_final_project.model.Paperview_items;
import com.example.java_final_project.model.Pill_items;
import com.example.java_final_project.model.RelatePill_items;
import com.example.java_final_project.utils.DataBaseHelper;
import com.example.java_final_project.utils.SharedPreference;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * SummaryActivity
 * 처방전의 상세정보를 보여주는 액티비티이다.
 */
public class SummaryActivity extends AppCompatActivity {

    private RecyclerView recyclerView0, recyclerView1;
    private SummaryAdapter summaryAdapter;
    private CheckAdapter checkAdapter;
    private ProgressDialog progressDialog;
    private BottomSheetDialog bottomSheetDialog;
    ArrayList<Pill_items> pill_items = new ArrayList<>();
    ArrayList<Paperview_items> paperview_items = new ArrayList<>();
    ArrayList<RelatePill_items> relatePill_items = new ArrayList<>();
    ArrayList<String> a;
    ArrayList<String> b;
    ArrayList<String> c;
    private DataBaseHelper myDB;
    private Cursor cursor, cursor1, cursor2, cursor3;
    private String namecur,p_name,check_pill_list, array_list_json;
    private String[] all_p_name, check_list;
    private int position, sort;
    private boolean saved;
    private Toolbar toolbar;
    private TextView tv_summary_photo, tv_save, tv_delete;
    private ImageView iv_summary_pick, iv_back, iv_add;
    private LinearLayout li_error;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        /*
            객체 선언
         */
        toolbar = findViewById(R.id.app_bar_summary);
        iv_back = findViewById(R.id.toolbar_back_btn);
        //tv_edit = findViewById(R.id.iv_edit_photo);
        tv_save = findViewById(R.id.iv_save_photo);
        tv_delete = findViewById(R.id.iv_delete_photo);
        iv_add = findViewById(R.id.add_pill);
        iv_summary_pick = findViewById(R.id.iv_summary_photo);
        tv_summary_photo = findViewById(R.id.tv_summary_photo);
        //recyclerView0 = findViewById(R.id.pills_lists_summary);
        recyclerView1 = findViewById(R.id.checked_lists_summary);
        li_error = findViewById(R.id.li_error_summary);
        Intent intent = getIntent();
        position = intent.getExtras().getInt("PICTURE");
        sort = intent.getExtras().getInt("SORT");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        /*
            UI 선언
         */
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Back(view);
            }
        });
        myDB = new DataBaseHelper(this);
        SharedPreference.setSelectPill(SummaryActivity.this,
                "DOUBLE_CHECK", 0);
        ShowPic();
        CHECKLIST(position, sort);

    }

    /*
        백버튼을 누르면 저장여부를 판단하여 변경 후 저장이 안되면 저장을 유도하게 하는 로직이 있다.
     */
    public void Back(View view) {
        RECOMMEND();
        progressDialog = new ProgressDialog
                (SummaryActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("저장 상태를 확인 중입니다...");
        progressDialog.show();

        //반드시 저장 후 나가도록 로직 작동함
        saved = SharedPreference.getSavedPill(
                SummaryActivity.this, "Saved_pill"
        );
        int count =
                SharedPreference.getSelectPill
                        (SummaryActivity.this, "DOUBLE_CHECK");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (saved) {
                    progressDialog.dismiss();
                    Intent intent = new Intent(SummaryActivity.this,
                            MainBoard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    if (count != 1) {
                        Intent intent = new Intent(SummaryActivity.this,
                                MainBoard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder
                                (view.getContext());
                        builder.setTitle("이런!");
                        builder.setMessage("저장을 안하셨어요...\n저장 부탁드립니다");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                bottomSheetDialog.show();
                                bottomSheetDialog.dismiss();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                    progressDialog.dismiss();

                }
                SharedPreference
                        .setSavedPill(SummaryActivity.this,
                                "Saved_pill", false);
            }
        }, 1000);



    }
    /*
        크게 처방전 사진을 띄어주는 함수이다.
     */
    public void ShowPic() {
        namecur = SharedPreference.getName(this, "Name");
        String show_date;
        Bitmap bitmap, resizebitmap;
        paperview_items.clear();

        Cursor cursor = myDB.getUserPaper(namecur);
        while (cursor.moveToNext()) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    bitmap = ImageDecoder.decodeBitmap
                            (ImageDecoder.createSource
                                    (this.getContentResolver(),
                                            Uri.parse(cursor.getString(1))));
                } else {
                    bitmap = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(),
                            Uri.parse(cursor.getString(1)));
                }

                /*resizebitmap =
                        Bitmap.createScaledBitmap(
                                bitmap, 350,
                                350, false
                        );*/
                resizebitmap =
                        Bitmap.createBitmap(
                                bitmap, 0, 0,  bitmap.getWidth(),
                                bitmap.getHeight(), null, false
                        );
                show_date = cursor.getString(2);
                //date = show_date.substring(0, show_date.indexOf("/"));
                paperview_items.add(0,
                        new Paperview_items(show_date, resizebitmap)
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (sort == 1) {
            iv_summary_pick.setImageBitmap(
                    paperview_items.get(paperview_items.size()-position-1)
                            .getList_bitmap_paper());
            tv_summary_photo.setText(
                    paperview_items.get(paperview_items.size()-position-1)
                            .getList_date());
        }else {
            iv_summary_pick.setImageBitmap(
                    paperview_items.get(position).getList_bitmap_paper());
            tv_summary_photo.setText(
                    paperview_items.get(position).getList_date());
        }
        cursor.close();
        ChangePaper(position, sort);

    }

    /**
     * ChangePaper
     * 이 함수는 처방전 데이터의 변경을 처리하는 로직이 담겨있다.
     * @param position
     * @param sort
     */
    public void ChangePaper(int position, int sort) {
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RECOMMEND();
            }
        });
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder
                        (view.getContext());
                builder.setTitle("NOTICE");
                builder.setMessage("이대로 저장하시겠습니까?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SAVE(position, sort);
                        SharedPreference
                                .setSavedPill(SummaryActivity.this,
                                        "Saved_pill", true);
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder
                        (view.getContext());
                builder.setTitle("NOTICE");
                builder.setMessage("처방전 사진을 삭제하시겠습니까?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DELETE(position, sort);
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
    }

    /**
     * EDIT logic
     * @param position
     * @param sort
     */
    public void SAVE(int position, int sort) {
        //myDB.updatePaper();
        if (sort == 1) {
            cursor1 = myDB.getUserPaperId(
                    paperview_items
                            .get(paperview_items.size() - position - 1)
                            .getList_date()
            );
            a = summaryAdapter.getArrayList();
            b = new ArrayList<>();
            //myDB.updatePaper();
            if (a.size() != 0) {
                for (int i = 0; i < a.size(); i++) {
                    cursor = myDB.getUserPillName(a.get(i));
                    while (cursor.moveToNext()) {
                        p_name = cursor.getString(0);

                    }
                    b.add(p_name);
                }
                //list 그대로 string[] 배열에 저장
                all_p_name = b.toArray(new String[b.size()]);

            } else {
                all_p_name = new String[]{"null"};
            }

        }else {
            cursor1 = myDB.getUserPaperId(
                    paperview_items
                            .get(position)
                            .getList_date()
            );
            a = summaryAdapter.getArrayList();
            Log.i(String.valueOf(this), String.valueOf(a.size()));
            b = new ArrayList<>();
            //myDB.updatePaper();
            if (a.size() != 0) {
                for (int i = 0; i < a.size(); i++) {
                    cursor = myDB.getUserPillName(a.get(i));
                    while (cursor.moveToNext()) {
                        p_name = cursor.getString(0);
                    }
                    b.add(p_name);
                }
                //list 그대로 string[] 배열에 저장
                all_p_name = b.toArray(new String[b.size()]);

            } else {
                all_p_name = new String[]{"null"};
            }
        }
        if (cursor1.getCount() == 1) {
            while (cursor1.moveToNext()) {
                //myDB.updatePaper();
                cursor2 = myDB.getUserPaperAll
                        (cursor1.getString(0));
                while (cursor2.moveToNext()) {
                    myDB.updatePaper(
                            cursor1.getString(0),
                            cursor2.getString(0),
                            cursor2.getString(1),
                            cursor2.getString(2),
                            all_p_name);
                }
            }
        }

        //화면갱신
        CHECKLIST(position, sort);

    }


    /**
     * DELETE logic
     * @param position
     * @param sort
     */
    public void DELETE(int position, int sort) {
        if (sort == 1) {
            Log.i(String.valueOf(this), String.valueOf(position));
            cursor1 = myDB.getUserPaperId(
                    paperview_items
                            .get(paperview_items.size() - position - 1)
                            .getList_date()
            );
            if (cursor1.getCount() == 1) {
                while (cursor1.moveToNext()) {
                    Log.i(String.valueOf(this),
                            String.valueOf(cursor1.getString(0)));
                    myDB.deletePaper(cursor1.getString(0));
                }
            }


        }else {
            cursor1 = myDB.getUserPaperId(
                    paperview_items
                            .get(position)
                            .getList_date()
            );
            if (cursor1.getCount() == 1) {
                while (cursor1.moveToNext()) {
                    Log.i(String.valueOf(this),
                            String.valueOf(cursor1.getString(0)));
                    myDB.deletePaper(cursor1.getString(0));
                }
            }

        }
        Intent intent = new Intent(
                SummaryActivity.this,
                MainBoard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    public void CHECKLIST(int position, int sort) {
        recyclerView1.setLayoutManager
                (new LinearLayoutManager(this,
                        LinearLayoutManager.HORIZONTAL, false));
        relatePill_items.clear();
        if (sort == 1) {
            cursor1 = myDB.getUserPaperId(
                    paperview_items
                            .get(paperview_items.size() - position - 1)
                            .getList_date()
            );
            if (cursor1.getCount() == 1) {
                while (cursor1.moveToNext()) {
                    //myDB.updatePaper();
                    cursor2 = myDB.getUserPaperAll
                            (cursor1.getString(0));
                    while (cursor2.moveToNext()) {
                        check_pill_list = cursor2.getString(3);
                        if (check_pill_list == null) {

                        } else {
                            check_list = check_pill_list.substring(1, check_pill_list.indexOf("]")).split(",");
                            for (String chP : check_list) {
                                if (chP.equals("null")) {

                                } else {
                                    relatePill_items.add(0,
                                            new RelatePill_items
                                                    (chP)
                                    );
                                }

                            }
                        }

                    }
                }
            }
        }else {
            cursor1 = myDB.getUserPaperId(
                    paperview_items
                            .get(position)
                            .getList_date()
            );
            if (cursor1.getCount() == 1) {
                while (cursor1.moveToNext()) {
                    //myDB.updatePaper();
                    cursor2 = myDB.getUserPaperAll
                            (cursor1.getString(0));
                    while (cursor2.moveToNext()) {
                        check_pill_list = cursor2.getString(3);
                        if (check_pill_list == null) {

                        } else {
                            check_list = check_pill_list.substring(1, check_pill_list.indexOf("]")).split(",");
                            for (String chP : check_list) {
                                if (chP.equals("null")) {

                                } else {
                                    relatePill_items.add(0,
                                            new RelatePill_items
                                                    (chP)
                                    );
                                }

                            }
                        }

                    }
                }
            }
        }
        if (relatePill_items.size() == 0) {
            li_error.setVisibility(View.VISIBLE);
        } else {
            li_error.setVisibility(View.GONE);
        }
        cursor1.close();
        checkAdapter = new CheckAdapter(this,
                relatePill_items);
        recyclerView1.setAdapter(checkAdapter);
    }

    /**
     * RECOMMEND
     * 이 함수는 바텀시트를 통해 처방전과 연관된 약을 선택하게 도와주는 함수이다.
     */
    public void RECOMMEND() {
        TextView tv_close_dialog;
        bottomSheetDialog = new BottomSheetDialog(
                this, R.style.BottomSheetDialogTheme
        );
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_add_checklist_pill);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setCancelable(false);
        recyclerView0 = bottomSheetDialog.findViewById(R.id.pills_lists_summary);
        tv_close_dialog = bottomSheetDialog.findViewById(R.id.tv_close_dialog);

        RefreshList();

        if (tv_close_dialog != null) {
            tv_close_dialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetDialog.dismiss();
                }
            });
        }
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                SAVE(position, sort);
            }
        });
        bottomSheetDialog.show();

    }
    public void RefreshList() {
        recyclerView0.setLayoutManager
                (new LinearLayoutManager(getApplicationContext(),
                        LinearLayoutManager.HORIZONTAL, false));
        String name = SharedPreference
                .getName(getApplicationContext(),
                        "Name");
        pill_items.clear();
        //relatePill_items.clear();
        String id,pill_name,ache,f_d,l_d;
        boolean is_checked;

        cursor3 = myDB.getUserPills(name);
        c = new ArrayList<>();
        if (sort == 1) {
            cursor1 = myDB.getUserPaperId(
                    paperview_items
                            .get(paperview_items.size() - position - 1)
                            .getList_date()
            );
        }else {
            cursor1 = myDB.getUserPaperId(
                    paperview_items
                            .get(position)
                            .getList_date()
            );
        }

        /*
            약의 개수만큼 반복한다.
         */
        while (cursor3.moveToNext()) {
            int count = 0;
            id = cursor3.getString(0);
            pill_name = cursor3.getString(1);
            ache = cursor3.getString(2);
            f_d = cursor3.getString(3);
            l_d = cursor3.getString(4);
            //alarm = cursor3.getString(5);
            if (cursor1.getCount() == 1) {
                while (cursor1.moveToNext()) {
                    //myDB.updatePaper();
                    cursor2 = myDB.getUserPaperAll
                            (cursor1.getString(0));
                    relatePill_items.clear();
                    while (cursor2.moveToNext()) {
                        check_pill_list = cursor2.getString(3);
                        if (check_pill_list != null) {
                            check_list = check_pill_list.substring
                                    (1, check_pill_list.indexOf("]")).split(",");
                            for (String chP : check_list) {
                                if (chP.equals("null") || chP.equals("")) {
                                    relatePill_items.add(0, new RelatePill_items(null));
                                } else {
                                    relatePill_items.add(0,
                                            new RelatePill_items
                                                    (chP)
                                    );
                                }
                            }
                        } else {
                            relatePill_items.add(0, new RelatePill_items(null));
                        }


                    }
                }
            }
            if (relatePill_items.size() != 0) {
                for (RelatePill_items n : relatePill_items) {
                    if (n.getList_pill() == null) {

                    } else {
                        String n1 = String.valueOf(n.getList_pill());
                        String n2 = String.valueOf(pill_name);
                        Log.i(String.valueOf(this), n1.trim());
                        Log.i(String.valueOf(this), n2);
                        if (n1.trim().equals(n2)) {
                            count += 1;
                            Log.i(String.valueOf(this), n.getList_pill() + "/pos");
                        }
                    }

                }

                if (count == 1) {
                    is_checked = true;
                } else {
                    is_checked = false;
                }

            } else {
                Log.i(String.valueOf(this),
                         "/none");
                is_checked = false;
            }
            pill_items.add(0,
                    new Pill_items(id, pill_name, ache,
                            f_d, l_d, is_checked)
            );

        }

        cursor3.close();
        summaryAdapter = new SummaryAdapter
                (getApplicationContext(), pill_items);
        recyclerView0.setAdapter(summaryAdapter);
    }

    @Override
    public void onBackPressed() {

    }
}
