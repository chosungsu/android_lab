package com.example.java_final_project.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.java_final_project.LoginActivity;
import com.example.java_final_project.MainBoard;
import com.example.java_final_project.R;
import com.example.java_final_project.SplashActivity;
import com.example.java_final_project.utils.DataBaseHelper;
import com.example.java_final_project.utils.SharedPreference;

public class SettingFragment extends Fragment {
    private View view;
    private LinearLayout tv_logout, tv_change, tv_delete_user;
    private DataBaseHelper myDB;
    private Cursor cursor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate
                (R.layout.fragment_setting, container, false);

        /*
            객체 지정
         */
        tv_logout = view.findViewById(R.id.tv_logout);
        tv_change = view.findViewById(R.id.tv_change_data);
        tv_delete_user = view.findViewById(R.id.tv_delete_user);

        /*
            툴바 이름 변경 및 뷰 모던화
         */
        FragmentActivity activity = getActivity();
        if (activity != null) {
            ((MainBoard) activity).setActionBarTitle("");
            ((MainBoard) activity).setActionBarSecondTitle("");
            ((MainBoard) activity).setFeed("설정");
            ((MainBoard) activity).setImageButton_l(0);
            ((MainBoard) activity).setImageButton_r(0);
        }
        myDB = new DataBaseHelper(getContext());
        String your_name = SharedPreference.getName(getContext(),
                "Name");
        cursor = myDB.getUserId(your_name);
        /*
            고객정보를 변경시 지원
         */
        tv_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder
                        (getContext());
                builder.setTitle("계정 정보 변경");
                final View customlayout = getLayoutInflater().inflate(
                        R.layout.dialog_change_user_data, null
                );
                builder.setView(customlayout);
                /*
                    고객 id값 얻기
                 */
                String id = null;
                while (cursor.moveToNext()) {
                    id = cursor.getString(0);
                }
                final EditText et_email = customlayout
                        .findViewById(R.id.et_email_change);
                final EditText et_name = customlayout
                        .findViewById(R.id.et_name_change);
                final EditText et_pw = customlayout
                        .findViewById(R.id.et_pw_change);
                String finalId = id;
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean var = myDB.updateData(finalId, et_name.getText().toString(),
                                et_email.getText().toString(),
                                et_pw.getText().toString());
                        if (var) {
                            Toast.makeText(getContext(),
                                    "정보변경이 정상적으로 처리되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(),
                                    "정보변경 과정에서 에러가 발생했습니다. " +
                                            "\n다시 시도 부탁드립니다.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        /*
            로그아웃 시 로직
         */
        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),
                        "로그아웃이 정상적으로 처리되었습니다.",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext()
                        , LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                //자동로그인을 해제시킴으로써 로그아웃효과를 줌
                SharedPreference
                        .setLoginVal(getContext(),
                                "자동 로그인",
                                false);
                startActivity(intent);
            }
        });
        tv_delete_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = null;
                while (cursor.moveToNext()) {
                    id = cursor.getString(0);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder
                        (view.getContext());
                builder.setTitle("NOTICE");
                builder.setMessage("정말 회원탈퇴를 하시겠습니까?");
                String finalId = id;
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        myDB.deleteUser(finalId);
                        Toast.makeText(getContext(),
                                "회원탈퇴가 정상적으로 처리되었습니다.",
                                Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });



        return view;

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
