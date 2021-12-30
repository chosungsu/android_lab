package com.example.java_final_project.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.java_final_project.MainBoard;
import com.example.java_final_project.R;
import com.example.java_final_project.adapter.PaperAdapter;
import com.example.java_final_project.model.Paperview_items;
import com.example.java_final_project.utils.DataBaseHelper;
import com.example.java_final_project.utils.SharedPreference;

import java.io.IOException;
import java.util.ArrayList;

/**
 * HomeFragment 소개
 * 이 프래그먼트에는 메인보드역할을 하는 각종 피드들을 개제할 목적으로 코딩되었다.
 */
public class HomeFragment extends Fragment {
    private View view;
    private int paper_sort;
    private DataBaseHelper myDB;
    private Cursor cursor1, cursor2;
    private int pill_nums, paper_nums;
    private String your_name;
    private FragmentActivity activity;
    private LinearLayout li_medi, li_paper;
    private TextView tv_toUp, tv_num_medicines, tv_user_name, tv_num_papers;
    private ScrollView scrollView;
    private LinearLayout li_user_summary;
    private RecyclerView recyclerView_h1, recyclerView_h2;
    private ImageView iv_sort_fitness, iv_sort_papers, iv_more_info;
    private PaperAdapter paperAdapter;
    ArrayList<Paperview_items> paperview_items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);

        /*
            객체 지정란
         */
        scrollView = view.findViewById(R.id.ssv);
        li_medi = view.findViewById(R.id.li_medi_channel);
        li_paper = view.findViewById(R.id.li_paper_channel);
        li_user_summary = view.findViewById(R.id.li_user_summary);
        recyclerView_h1 = view.findViewById(R.id.paper_lists);
        iv_sort_fitness = view.findViewById(R.id.iv_sort_fitness);
        iv_sort_papers = view.findViewById(R.id.iv_sort_papers);
        tv_num_medicines = view.findViewById(R.id.tv_num_medicines);
        tv_user_name = view.findViewById(R.id.tv_user_name_);
        tv_num_papers = view.findViewById(R.id.tv_num_papers);



        /*
            툴바 이름 변경 및 뷰 모던화
         */
        activity = getActivity();
        if (activity != null) {
            ((MainBoard) activity).setActionBarTitle("HealOnYou");
            ((MainBoard) activity).setActionBarSecondTitle("");
            ((MainBoard) activity).setFeed("");
            ((MainBoard) activity).setImageButton_l(0);
            ((MainBoard) activity).setImageButton_r(0);
        }


        /*
            view
         */
        myDB = new DataBaseHelper(getContext());
        your_name = SharedPreference.getName(getContext(), "Name");
        Nums();
        Fitness(your_name);
        Paper();
        //Hospital();


        return view;
    }

    /*
        사용자의 개인 정보를 받아오는 함수이다.
     */
    public void Nums() {
        cursor1 = myDB.getUserPills(your_name);
        SharedPreference
                .setNumPills(getContext(),
                        "Pills", cursor1.getCount());
        cursor2 = myDB.getUserPaper(your_name);
        SharedPreference
                .setNumPapers(getContext(),
                        "Papers", cursor2.getCount());
        pill_nums = SharedPreference
                .getNumPills(getContext(),
                        "Pills");
        paper_nums = SharedPreference
                .getNumPapers(getContext(),
                        "Papers");
        tv_user_name.setText(your_name);
        tv_num_medicines.setText(pill_nums + "개");
        tv_num_papers.setText(paper_nums + "개");
        li_medi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity = getActivity();
                if (activity != null) {
                    SharedPreference
                            .setProfile_To_Medi(getContext(),
                                    "Medi", 1);
                    ((MainBoard) activity).
                            replaceFragment
                                    (new MediFragment(), "medi");

                }
            }
        });
        li_paper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder
                        (view.getContext());
                builder.setTitle("NOTICE");
                builder.setMessage("아래 쪽의 처방전 뷰에서 사진을 클릭하여 정보를 확인해보세요.");
                builder.setPositiveButton("네, 그럴게요.", new DialogInterface.OnClickListener() {
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
        이 함수는 건강센터의 데이터를 불러와준다.
     */
    public void Fitness(String your_name) {
        LinearLayout li_error, li_record_walk;
        TextView tv_date, tv_today_walk, tv_real_walk,
                tv_walk_hour, tv_save_walk;
        li_error = view.findViewById(R.id.li_error_fitness);
        li_record_walk = view.findViewById(R.id.li_record_fitness);
        tv_date = view.findViewById(R.id.tv_today_date_walks);
        tv_today_walk = view.findViewById(R.id.tv_today_total_walking);
        tv_walk_hour = view.findViewById(R.id.tv_today_real_walking_hour);
        tv_real_walk = view.findViewById(R.id.tv_today_real_walk_num);
        tv_save_walk = view.findViewById(R.id.tv_save_walk);

        Cursor cursor3 = myDB.getWalkSaveData(your_name);
        if (cursor3.getCount() != 0) {
            //데이터가 존재할 때
            //새로운 데이터가 추가되면 그 데이터를 업데이트해야하므로
            //movetolast로 제일 마지막행으로 가게끔 해준다.
            for (cursor3.moveToLast(); !cursor3.isAfterLast(); cursor3.moveToNext()) {
                li_error.setVisibility(View.GONE);
                li_record_walk.setVisibility(View.VISIBLE);
                tv_date.setText(cursor3.getString(2));
                tv_today_walk.setText(cursor3.getString(1));
                tv_real_walk.setText(cursor3.getString(4));
                tv_save_walk.setText(String.valueOf(cursor3.getCount()));
                if (cursor3.getString(3) == null) {
                    tv_walk_hour.setText("아직 운동기록이 없습니다. 운동시작을 눌러 시작해보세요!");
                } else {
                    int hour = Integer.parseInt(cursor3.getString(3).substring(0,
                            cursor3.getString(3).indexOf(",")));
                    int minute = Integer.parseInt(cursor3.getString(3)
                            .substring(cursor3.getString(3).indexOf(",") + 1));
                    tv_walk_hour.setText(hour + "시간 " + minute + "분 동안 운동하셨습니다.");
                }
            }
            /*while (cursor3.moveToNext()) {
                li_error.setVisibility(View.GONE);
                li_record_walk.setVisibility(View.VISIBLE);
                tv_date.setText(cursor3.getString(2));
                tv_today_walk.setText(cursor3.getString(1));
                tv_real_walk.setText(cursor3.getString(4));
                tv_save_walk.setText(String.valueOf(cursor3.getCount()));
                if (cursor3.getString(3) == null) {
                    tv_walk_hour.setText("아직 운동기록이 없습니다. 운동시작을 눌러 시작해보세요!");
                } else {
                    int hour = Integer.parseInt(cursor3.getString(3).substring(0,
                            cursor3.getString(3).indexOf(",")));
                    int minute = Integer.parseInt(cursor3.getString(3)
                            .substring(cursor3.getString(3).indexOf(",") + 1));
                    tv_walk_hour.setText(hour + "시간 " + minute + "분 동안 운동하셨습니다.");
                }

            }*/

        } else {
            //데이터가 존재하지 않을 때
            li_error.setVisibility(View.VISIBLE);
            li_record_walk.setVisibility(View.GONE);
        }
        iv_sort_fitness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_sort_fitness.setImageResource
                        (R.drawable.ic_baseline_arrow_drop_up_24);
                PopupMenu popupMenu = new PopupMenu(getContext(), iv_sort_fitness);
                popupMenu.getMenuInflater().inflate
                        (R.menu.sort_menu_fitness, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(
                        new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.fitness_record:
                                        //레코드 생성 홈으로 이동
                                        activity = getActivity();
                                        if (activity != null) {
                                            ((MainBoard) activity).
                                                    replaceFragment
                                                            (new WalkFragment(), "walk");
                                            SharedPreference
                                                    .setpopupmenu(getContext(),
                                                            "popup", 0);
                                        }
                                        return true;
                                    case R.id.fitness_data:
                                        //기록 보기 홈으로 이동
                                        activity = getActivity();
                                        if (activity != null) {
                                            ((MainBoard) activity).
                                                    replaceFragment
                                                            (new WalkFragment(), "walk");
                                            SharedPreference
                                                    .setpopupmenu(getContext(),
                                                            "popup", 1);
                                        }
                                        return true;

                                }

                                return true;
                            }
                        });
                popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                    @Override
                    public void onDismiss(PopupMenu popupMenu) {
                        iv_sort_fitness.setImageResource
                                (R.drawable.ic_baseline_arrow_drop_down_24);
                    }
                });
                popupMenu.show();
            }
        });
    }

    /*
        이 함수는 병원 처방전 현황을 보여주기 위한 어댑터 생성함수이다.
     */
    public void Paper() {
        Sort_Paper();

        iv_sort_papers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_sort_papers.setImageResource
                        (R.drawable.ic_baseline_arrow_drop_up_24);
                PopupMenu popupMenu = new PopupMenu(getContext(), iv_sort_papers);
                popupMenu.getMenuInflater().inflate
                        (R.menu.sort_menu_paper, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(
                        new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.neartime:
                                        //최신순으로 데이터 정렬
                                        SharedPreference
                                                .setsort_paper(getContext(),
                                                        "p_sort", 0);
                                        Sort_Paper();
                                        return true;
                                    case R.id.fartime:
                                        //역순으로 데이터 정렬
                                        SharedPreference
                                                .setsort_paper(getContext(),
                                                        "p_sort", 1);
                                        Sort_Paper();
                                        return true;

                                }

                                return true;
                            }
                        });
                popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                    @Override
                    public void onDismiss(PopupMenu popupMenu) {
                        iv_sort_papers.setImageResource
                                (R.drawable.ic_baseline_arrow_drop_down_24);
                    }
                });
                popupMenu.show();
            }
        });
    }

    /*public void Hospital() {
        recyclerView_h2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        hospital_items.clear();
        hospital_items.add(new Hospital_items("1위", "jo", "5000"));
        hospital_items.add(new Hospital_items("2위", "jor", "10000"));
        hospital_items.add(new Hospital_items("3위", "joo", "30000"));
        hospitalAdapter = new HospitalAdapter(getContext(), hospital_items, this);
        recyclerView_h2.setAdapter(hospitalAdapter);
        iv_more_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),
                        "잠시만 기다려주세요...", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    public void Sort_Paper() {
        LinearLayout li_error;
        li_error = view.findViewById(R.id.li_error_message);
        String date;
        Bitmap bitmap, resizebitmap;
        String namecur = tv_user_name.getText().toString();
        recyclerView_h1.setLayoutManager
                (new LinearLayoutManager(getContext(),
                        LinearLayoutManager.HORIZONTAL, false));
        paper_sort = SharedPreference
                .getsort_paper(getContext(),
                        "p_sort");
        paperview_items.clear();
        Cursor cursor = myDB.getUserPaper(namecur);
        while (cursor.moveToNext()) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    bitmap = ImageDecoder.decodeBitmap
                            (ImageDecoder.createSource
                                    (requireContext()
                                                    .getContentResolver(),
                                            Uri.parse(cursor.getString(1))));
                } else {
                    bitmap = MediaStore.Images.Media.getBitmap(
                            requireContext()
                                    .getContentResolver(),
                            Uri.parse(cursor.getString(1)));
                }
                /*resizebitmap =
                        Bitmap.createScaledBitmap(
                                bitmap, 120,
                                120, false
                        );*/
                resizebitmap =
                        Bitmap.createBitmap(
                                bitmap, 0, 0,  bitmap.getWidth(),
                                bitmap.getHeight(), null, false
                        );
                date = cursor.getString(2);
                //date = date.substring(0, date.indexOf("/"));
                if (paper_sort == 0) {
                    paperview_items.add(0,
                            new Paperview_items(date, resizebitmap)
                    );
                } else if (paper_sort == 1) {
                    paperview_items.add(
                            new Paperview_items(date, resizebitmap)
                    );
                } else {
                    paperview_items.add(0,
                            new Paperview_items(date, resizebitmap)
                    );
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (paperview_items.size() != 0) {
            li_error.setVisibility(View.GONE);
            paperAdapter = new PaperAdapter
                    (getContext(), paperview_items);
            recyclerView_h1.setAdapter(paperAdapter);
        } else {
            li_error.setVisibility(View.VISIBLE);
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
