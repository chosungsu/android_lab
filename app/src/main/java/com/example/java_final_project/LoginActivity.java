package com.example.java_final_project;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.java_final_project.utils.BackPressCloseHandler;
import com.example.java_final_project.utils.DataBaseHelper;
import com.example.java_final_project.utils.SharedPreference;

/**
 * LoginActivity
 * 유저가 로그인 및 회원가입을 할 수 있게 해주는 액티비티이다.
 */

public class LoginActivity extends AppCompatActivity {
    private FrameLayout fl_l, fl_s;
    private LinearLayout cv_s, cv_l;
    private ImageView iv_back;
    private TextView tv_name;
    private EditText email_s, password_s, username_s;
    private EditText username_l, password_l;
    private Button login, signup;
    private CheckBox checkBox;
    private DataBaseHelper myDB;
    private SQLiteDatabase sqLiteDatabase;
    private BackPressCloseHandler handler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*
            객체 선언
         */
        fl_l = findViewById(R.id.fl_loginform);
        fl_s = findViewById(R.id.fl_signform);
        cv_l = findViewById(R.id.cv_login);
        cv_s = findViewById(R.id.cv_signup);
        iv_back = findViewById(R.id.iv_form_back);
        tv_name = findViewById(R.id.tv_form_name);
        email_s = findViewById(R.id.et_email_s);
        password_s = findViewById(R.id.et_password_s);
        username_s = findViewById(R.id.et_name_s);
        signup = findViewById(R.id.btn_signup);
        username_l = findViewById(R.id.et_name_l);
        password_l = findViewById(R.id.et_password_l);
        login = findViewById(R.id.btn_login);
        checkBox = findViewById(R.id.checkbox_login);

        /*
            UI 설정
         */
        fl_l.setVisibility(View.GONE);
        fl_s.setVisibility(View.GONE);
        iv_back.setVisibility(View.GONE);
        myDB = new DataBaseHelper(this);
        Handler handler_loop = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                tv_name.setVisibility(View.VISIBLE);
                cv_l.setVisibility(View.VISIBLE);
                cv_s.setVisibility(View.VISIBLE);
            }
        };
        handler_loop.postDelayed(runnable, 3000);
        Main_UI();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        handler = new BackPressCloseHandler(this);

    }

    public void Main_UI() {
        /*
            자동 로그인 설정값에 따라 화면 변경
         */
        boolean is_check_login_pass = SharedPreference
                .getLoginVal(this, "자동 로그인");
        if (is_check_login_pass) {
            String your_name = SharedPreference
                    .getName(LoginActivity.this,
                            "Name");
            Intent intent = new Intent(LoginActivity.this
                    , MainBoard.class);
            intent.putExtra("Name", your_name);
            startActivity(intent);
            finish();
        } else {
            /*
                화면 변환
            */
            cv_l.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    lo_form();
                    iv_back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tv_name.setText("Welcome PLAFIN :)");
                            cv_l.setVisibility(View.VISIBLE);
                            cv_s.setVisibility(View.VISIBLE);
                            fl_l.setVisibility(View.GONE);
                            iv_back.setVisibility(View.GONE);
                        }
                    });
                /*
                    로그인하기
                */
                    loginUser();
                }
            });
            cv_s.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    si_form();
                    iv_back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tv_name.setText("Welcome PLAFIN :)");
                            cv_l.setVisibility(View.VISIBLE);
                            cv_s.setVisibility(View.VISIBLE);
                            fl_s.setVisibility(View.GONE);
                            iv_back.setVisibility(View.GONE);
                        }
                    });
                /*
                    sqlite에 데이터 넣기(유저 데이터)
                */
                    insertUser();
                }
            });
        }
    }

    /*
        키보드를 사라지게 하는 함수
     */
    public void keyboardhide() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService
                        (Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput
                (InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
    /*
        로그인 폼 UI
     */
    private void lo_form() {
        tv_name.setText("로그인 폼");
        checkBox.setChecked(false);
        cv_l.setVisibility(View.GONE);
        cv_s.setVisibility(View.GONE);
        fl_l.setVisibility(View.VISIBLE);
        iv_back.setVisibility(View.VISIBLE);
        username_l.setText("");
        password_l.setText("");
    }
    /*
        회원가입 UI
     */
    private void si_form() {
        tv_name.setText("회원가입 폼");
        cv_l.setVisibility(View.GONE);
        cv_s.setVisibility(View.GONE);
        fl_s.setVisibility(View.VISIBLE);
        iv_back.setVisibility(View.VISIBLE);
        username_s.setText("");
        email_s.setText("");
        password_s.setText("");
    }

    /**
     * loginUser
     * 로그인 하는 로직
     */
    private void loginUser() {

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyboardhide();
                onCheckClicked();
                boolean var = myDB.checkUser(username_l.getText().toString(),
                        password_l.getText().toString());
                if (var) {
                    Intent intent = new Intent(LoginActivity.this
                            , SplashActivity.class);
                    SharedPreference
                            .setName(LoginActivity.this,
                                    "Name", username_l.getText().toString());
                    String your_name = SharedPreference
                            .getName(LoginActivity.this,
                                    "Name");
                    Toast.makeText(LoginActivity.this,
                            your_name + "님 환영합니다.",
                            Toast.LENGTH_SHORT).show();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    finish();
                } else {
                    Toast.makeText(LoginActivity.this,
                            "로그인 과정에서 에러가 발생했습니다. \n다시 시도 부탁드립니다.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /*
        자동 로그인 체크 여부를 확인하는 로직
     */
    public void onCheckClicked() {
        boolean checked = checkBox.isChecked();
        if (checked) {
            SharedPreference
                    .setLoginVal(this, "자동 로그인", true);
        } else {
            SharedPreference
                    .setLoginVal(this, "자동 로그인", false);
        }
    }

    /**
     * insertUser
     * 회원가입하는 로직
     */
    private void insertUser() {
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyboardhide();
                boolean var2 = myDB.checkUserEmail(username_s.getText().toString(),
                        email_s.getText().toString());
                if (var2) {
                    Toast.makeText(LoginActivity.this,
                            "회원가입 과정에서 동일 유저이메일 또는 유저닉네임이 \n발견되었습니다. 다시 시도 부탁드립니다.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    boolean var = myDB.addUser(username_s.getText().toString(),
                            email_s.getText().toString(),
                            password_s.getText().toString());
                    if (var) {
                        Toast.makeText(LoginActivity.this,
                                "회원가입이 정상적으로 처리되었습니다.", Toast.LENGTH_SHORT).show();
                        fl_l.setVisibility(View.VISIBLE);
                        fl_s.setVisibility(View.GONE);
                        lo_form();
                        loginUser();
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "회원가입 과정에서 오류가 발생하였습니다. \n" +
                                        "다시 시도 부탁드립니다.", Toast.LENGTH_SHORT).show();
                    }
                }

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
