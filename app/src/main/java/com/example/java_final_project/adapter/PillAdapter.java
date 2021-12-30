package com.example.java_final_project.adapter;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.java_final_project.utils.AlarmReceiver;
import com.example.java_final_project.utils.DeviceBootReceiver;
import com.example.java_final_project.R;
import com.example.java_final_project.model.Pill_items;
import com.example.java_final_project.utils.DataBaseHelper;
import com.example.java_final_project.utils.SharedPreference;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class PillAdapter
        extends RecyclerView.Adapter<PillAdapter.PillViewHolder> {
    private static Context context;
    private static ArrayList<Pill_items> pill_items;
    private static final ArrayList<String> relatePill_items = new ArrayList<String>();
    private static ArrayList<String> b;
    private static DataBaseHelper myDB;
    private static Cursor cursor1, cursor2;
    private static String pill_name, alarm_time, alarm;
    private static String[] all_p_alarm;
    private static String[] alarm_list;
    private static BottomSheetDialog bottomSheetDialog0, bottomSheetDialog1;
    private static int t_h, t_m;
    private static Calendar calendar_morning, calendar_lunch, calendar_dinner;
    private static TimePickerDialog timePicker_morning, timePicker_lunch, timePicker_dinner;

    public PillAdapter(Context context, ArrayList<Pill_items> pill_item) {
        PillAdapter.context = context;
        pill_items = pill_item;
    }

    @NonNull
    @Override
    public PillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_items_pills, parent, false);
        return new PillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PillViewHolder holder, int position) {
        Pill_items pill_item = pill_items.get(position);
        holder.list_name.setText(pill_item.getList_pill());
        holder.list_ache.setText(pill_item.getList_aches());
        holder.list_fd.setText(pill_item.getList_st_date());
        holder.list_ld.setText(pill_item.getList_lt_date());
        if (!pill_item.getAlarm().equals("알림 세팅 전입니다...")) {
            holder.list_alarm.setText(pill_item.getAlarm()
                    .substring(1, pill_item.getAlarm().indexOf("]")));
        } else {
            holder.list_alarm.setText(pill_item.getAlarm());
        }

    }

    @Override
    public int getItemCount() {
        return pill_items.size();
    }



    public static class PillViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener{

        private final TextView list_name, list_ache, list_fd, list_ld, list_alarm;

        public PillViewHolder(@NonNull View itemView) {
            super(itemView);
            list_name = itemView.findViewById(R.id.tv_name_pill);
            list_ache = itemView.findViewById(R.id.tv_name_ache);
            list_fd = itemView.findViewById(R.id.tv_name_f_d);
            list_ld = itemView.findViewById(R.id.tv_name_l_d);
            list_alarm = itemView.findViewById(R.id.tv_name_alarm);

            myDB = new DataBaseHelper(itemView.getContext());

            /*
                이 로직은 리사이클러뷰를 클릭하였을 때 컨텍스트 메뉴로 반응하게 하는 로직이다
             */
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu,
                                        View view,
                                        ContextMenu.ContextMenuInfo
                                                        contextMenuInfo) {
            contextMenu.setHeaderTitle("INFO");
            contextMenu.add(0, 0, 0, "알람 설정");
            contextMenu.add(0, 1, 0, "세부사항 변경");
            contextMenu.add(0, 2, 0, "삭제하기");

            contextMenu.getItem(0).setOnMenuItemClickListener(
                    onMenuItemClickListener
            );
            contextMenu.getItem(1).setOnMenuItemClickListener(
                    onMenuItemClickListener
            );
            contextMenu.getItem(2).setOnMenuItemClickListener(
                    onMenuItemClickListener
            );

        }
        private final MenuItem.OnMenuItemClickListener onMenuItemClickListener =
                new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        String name = SharedPreference
                                .getName(context,
                                        "Name");
                        relatePill_items.clear();
                        pill_name =
                                String.valueOf
                                        (PillViewHolder.this.
                                                list_name.getText());
                        cursor1 = myDB.getUserPillId(
                                pill_name
                        );
                        Cursor cursor4 = myDB.getAlarmSetWhen(pill_name);


                        switch (menuItem.getItemId()) {
                            case 0:
                                final Calendar calendar = Calendar.getInstance();
                                int mhour = calendar.get(Calendar.HOUR);
                                int mminute = calendar.get(Calendar.MINUTE);
                                TextView tv_first_date, tv_last_date
                                        , tv_morning, tv_lunch, tv_dinner
                                        , btn_set_refresh, btn_set_alarm;
                                ImageView iv_info_time;

                                bottomSheetDialog0 = new BottomSheetDialog(
                                        context, R.style.BottomSheetDialogTheme
                                );
                                bottomSheetDialog0.setContentView(R.layout.bottom_sheet_alarm_set);
                                bottomSheetDialog0.setCanceledOnTouchOutside(true);
                                bottomSheetDialog0.setCancelable(true);
                                tv_first_date = bottomSheetDialog0.findViewById(R.id.tv_start_date);
                                tv_last_date = bottomSheetDialog0.findViewById(R.id.tv_last_date);
                                tv_morning = bottomSheetDialog0.findViewById(R.id.tv_morning);
                                tv_lunch = bottomSheetDialog0.findViewById(R.id.tv_lunch);
                                tv_dinner = bottomSheetDialog0.findViewById(R.id.tv_dinner);
                                iv_info_time = bottomSheetDialog0.findViewById(R.id.tv_time_info);
                                btn_set_refresh = bottomSheetDialog0.findViewById(R.id.btn_alarm_set_refresh);
                                btn_set_alarm = bottomSheetDialog0.findViewById(R.id.btn_alarm_set_finish);

                                if (tv_first_date != null) {
                                    tv_first_date.setText(list_fd.getText());
                                }
                                if (tv_last_date != null) {
                                    tv_last_date.setText(list_ld.getText());
                                }
                                if (iv_info_time != null) {
                                    iv_info_time.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                            builder.setTitle("NOTICE");
                                            builder.setMessage("아침, 점심, 저녁 시간대를 설정하셔서 \n" +
                                                    "약을 복용하실 시간에 알람을 드립니다.");
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
                                while (cursor1.moveToNext()) {
                                    cursor2 = myDB.getUserPillByName(pill_name);
                                    while (cursor2.moveToNext()) {
                                        if (cursor1.getString(0).equals(
                                                cursor2.getString(0))) {
                                            alarm = cursor2.getString(5);
                                            if (!alarm.equals("알림 세팅 전입니다...")) {
                                                alarm_list = alarm.substring(
                                                        1, alarm.indexOf("]")
                                                ).split(",");
                                                relatePill_items.addAll
                                                        (Arrays.asList(alarm_list));
                                                for (int i = 0; i < relatePill_items.size(); i ++) {
                                                    if (!relatePill_items.get(i)
                                                            .equals("설정 값 없음")) {
                                                        if (relatePill_items.get(i)
                                                                .equals("알림 세팅 전입니다...")) {
                                                            switch (i) {
                                                                case 0:
                                                                    if (tv_morning != null) {
                                                                        tv_morning.setText
                                                                                ("설정 값 없음");
                                                                    }
                                                                    break;
                                                                case 1:
                                                                    if (tv_lunch != null) {
                                                                        tv_lunch.setText
                                                                                ("설정 값 없음");
                                                                    }
                                                                    break;
                                                                case 2:
                                                                    if (tv_dinner != null) {
                                                                        tv_dinner.setText
                                                                                ("설정 값 없음");
                                                                    }
                                                                    break;

                                                            }
                                                        } else {
                                                            switch (i) {
                                                                case 0:
                                                                    if (tv_morning != null) {
                                                                        tv_morning.setText
                                                                                (relatePill_items.get(i).trim());
                                                                    }
                                                                    break;
                                                                case 1:
                                                                    if (tv_lunch != null) {
                                                                        tv_lunch.setText
                                                                                (relatePill_items.get(i).trim());
                                                                    }
                                                                    break;
                                                                case 2:
                                                                    if (tv_dinner != null) {
                                                                        tv_dinner.setText
                                                                                (relatePill_items.get(i).trim());
                                                                    }
                                                                    break;

                                                            }
                                                        }


                                                    } else {
                                                        switch (i) {
                                                            case 0:
                                                                if (tv_morning != null) {
                                                                    tv_morning.setText
                                                                            ("설정 값 없음");
                                                                }
                                                                break;
                                                            case 1:
                                                                if (tv_lunch != null) {
                                                                    tv_lunch.setText
                                                                            ("설정 값 없음");
                                                                }
                                                                break;
                                                            case 2:
                                                                if (tv_dinner != null) {
                                                                    tv_dinner.setText
                                                                            ("설정 값 없음");
                                                                }
                                                                break;

                                                        }
                                                    }
                                                }


                                            } else {
                                                Log.i(String.valueOf(this),
                                                        "morning_set3");
                                                if (tv_morning != null) {
                                                    tv_morning.setText
                                                            ("설정 값 없음");
                                                }
                                                if (tv_lunch != null) {
                                                    tv_lunch.setText
                                                            ("설정 값 없음");
                                                }
                                                if (tv_dinner != null) {
                                                    tv_dinner.setText
                                                            ("설정 값 없음");
                                                }
                                            }
                                        }

                                    }

                                }
                                if (tv_morning != null) {
                                    tv_morning.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            timePicker_morning =
                                                    new TimePickerDialog(
                                                            context,
                                                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                                            new TimePickerDialog.OnTimeSetListener() {
                                                                @Override
                                                                public void onTimeSet
                                                                        (TimePicker timePicker, int hour, int minute) {
                                                                    t_h = hour;
                                                                    t_m = minute;
                                                                    String time = t_h + ":" + t_m;
                                                                    SimpleDateFormat f24 = new SimpleDateFormat(
                                                                            "HH:mm"
                                                                    );
                                                                    try {
                                                                        Date date = f24.parse(time);
                                                                        SimpleDateFormat f12hour = new SimpleDateFormat(
                                                                                "hh:mm(aa)"
                                                                        );
                                                                        if (date != null) {
                                                                            tv_morning.setText(f12hour.format(date));
                                                                        }

                                                                    } catch (ParseException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    /*String am_pm = tv_morning.getText().toString().substring(
                                                                            tv_morning.getText().toString().indexOf("(") + 1,
                                                                            tv_morning.getText().toString().indexOf(")")
                                                                    );
                                                                    if (am_pm.equals("AM") || am_pm.equals("오전")) {
                                                                        if (t_h == 12) {
                                                                            t_h = 0;
                                                                        }
                                                                    } else {
                                                                        if (t_h != 12) {
                                                                            t_h += 12;
                                                                        }
                                                                    }*/

                                                                    while (cursor4.moveToNext()) {
                                                                        calendar_morning = Calendar.getInstance();
                                                                        calendar_morning.set(Calendar.HOUR_OF_DAY, t_h);
                                                                        calendar_morning.set(Calendar.MINUTE, t_m);
                                                                        calendar_morning.set(Calendar.SECOND, 0);
                                                                        if (calendar_morning.before(Calendar.getInstance())) {
                                                                            calendar_morning.add(Calendar.DATE, 1);
                                                                        }
                                                                        Log.i(String.valueOf(this), "morning_start");
                                                                        Intent alarm_morning = new Intent(context, AlarmReceiver.class);
                                                                        alarm_morning.putExtra("message", cursor4.getString(6));
                                                                        alarm_morning.putExtra("time", "아침");
                                                                        alarm_morning.putExtra("code", cursor4.getString(3));
                                                                        PendingIntent pendingIntent_morning =
                                                                                PendingIntent.getBroadcast
                                                                                        (context, Integer.parseInt(cursor4.getString(3)),
                                                                                                alarm_morning, PendingIntent.FLAG_CANCEL_CURRENT);
                                                                        AlarmManager alarmManager_morning =
                                                                                (AlarmManager) context.getSystemService(
                                                                                        Context.ALARM_SERVICE
                                                                                );
                                                                        alarmManager_morning.setRepeating(AlarmManager.RTC_WAKEUP,
                                                                                calendar_morning.getTimeInMillis(),
                                                                                AlarmManager.INTERVAL_DAY, pendingIntent_morning);
                                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                                            alarmManager_morning.setExactAndAllowWhileIdle
                                                                                    (AlarmManager.RTC_WAKEUP,
                                                                                            calendar_morning.getTimeInMillis(),
                                                                                            pendingIntent_morning);
                                                                        }

                                                                    }


                                                                }
                                                            }, mhour, mminute, false
                                                    );
                                            timePicker_morning.getWindow().setBackgroundDrawable(
                                                    new ColorDrawable(Color.TRANSPARENT)
                                            );
                                            timePicker_morning.updateTime(t_h, t_m);
                                            timePicker_morning.setTitle("아침 복용 시간");
                                            timePicker_morning.show();

                                        }
                                    });
                                }
                                if (tv_lunch != null) {
                                    tv_lunch.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            TimePickerDialog timePickerDialog =
                                                    new TimePickerDialog(
                                                            context,
                                                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                                            new TimePickerDialog.OnTimeSetListener() {
                                                                @Override
                                                                public void onTimeSet
                                                                        (TimePicker timePicker, int hour, int minute) {
                                                                    t_h = hour;
                                                                    t_m = minute;
                                                                    String time = t_h + ":" + t_m;
                                                                    SimpleDateFormat f24 = new SimpleDateFormat(
                                                                            "HH:mm"
                                                                    );
                                                                    try {
                                                                        Date date = f24.parse(time);
                                                                        SimpleDateFormat f12hour = new SimpleDateFormat(
                                                                                "hh:mm(aa)"
                                                                        );

                                                                        if (date != null) {
                                                                            tv_lunch.setText(f12hour.format(date));
                                                                        }
                                                                    } catch (ParseException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    /*String am_pm = tv_lunch.getText().toString().substring(
                                                                            tv_lunch.getText().toString().indexOf("(") + 1,
                                                                            tv_lunch.getText().toString().indexOf(")")
                                                                    );
                                                                    if (am_pm.equals("AM") || am_pm.equals("오전")) {
                                                                        if (t_h == 12) {
                                                                            t_h = 0;
                                                                        }
                                                                    } else {
                                                                        if (t_h != 12) {
                                                                            t_h += 12;
                                                                        }
                                                                    }*/
                                                                    while (cursor4.moveToNext()) {
                                                                        calendar_lunch = Calendar.getInstance();
                                                                        calendar_lunch.set(Calendar.HOUR_OF_DAY, hour);
                                                                        calendar_lunch.set(Calendar.MINUTE, minute);
                                                                        calendar_lunch.set(Calendar.SECOND, 0);
                                                                        if (calendar_lunch.before(Calendar.getInstance())) {
                                                                            calendar_lunch.add(Calendar.DATE, 1);
                                                                        }
                                                                        Log.i(String.valueOf(this), "lunch_start");
                                                                        Intent alarm_lunch = new Intent(context, AlarmReceiver.class);
                                                                        alarm_lunch.putExtra("message", cursor4.getString(6));
                                                                        alarm_lunch.putExtra("time", "점심");
                                                                        alarm_lunch.putExtra("code", cursor4.getString(4));
                                                                        PendingIntent pendingIntent_lunch =
                                                                                PendingIntent.getBroadcast
                                                                                        (context, Integer.parseInt(cursor4.getString(4)),
                                                                                                alarm_lunch, PendingIntent.FLAG_CANCEL_CURRENT);
                                                                        AlarmManager alarmManager_lunch =
                                                                                (AlarmManager) context.getSystemService(
                                                                                        Context.ALARM_SERVICE
                                                                                );
                                                                        alarmManager_lunch.setRepeating(AlarmManager.RTC_WAKEUP,
                                                                                calendar_lunch.getTimeInMillis(),
                                                                                AlarmManager.INTERVAL_DAY, pendingIntent_lunch);
                                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                                            alarmManager_lunch.setExactAndAllowWhileIdle
                                                                                    (AlarmManager.RTC_WAKEUP,
                                                                                            calendar_lunch.getTimeInMillis(),
                                                                                            pendingIntent_lunch);
                                                                        }
                                                                    }


                                                                }
                                                            }, mhour, mminute, false
                                                    );
                                            timePickerDialog.getWindow().setBackgroundDrawable(
                                                    new ColorDrawable(Color.TRANSPARENT)
                                            );
                                            timePickerDialog.updateTime(t_h, t_m);
                                            timePickerDialog.setTitle("점심 복용 시간");
                                            timePickerDialog.show();

                                        }
                                    });
                                }
                                if (tv_dinner != null) {
                                    tv_dinner.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            TimePickerDialog timePickerDialog =
                                                    new TimePickerDialog(
                                                            context,
                                                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                                            new TimePickerDialog.OnTimeSetListener() {
                                                                @Override
                                                                public void onTimeSet
                                                                        (TimePicker timePicker, int hour, int minute) {
                                                                    t_h = hour;
                                                                    t_m = minute;
                                                                    String time = t_h + ":" + t_m;
                                                                    SimpleDateFormat f24 = new SimpleDateFormat(
                                                                            "HH:mm"
                                                                    );
                                                                    try {
                                                                        Date date = f24.parse(time);
                                                                        SimpleDateFormat f12hour = new SimpleDateFormat(
                                                                                "hh:mm(aa)"
                                                                        );
                                                                        if (date != null) {
                                                                            tv_dinner.setText(f12hour.format(date));
                                                                        }
                                                                    } catch (ParseException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    /*String am_pm = tv_dinner.getText().toString().substring(
                                                                            tv_dinner.getText().toString().indexOf("(") + 1,
                                                                            tv_dinner.getText().toString().indexOf(")")
                                                                    );
                                                                    if (am_pm.equals("AM") || am_pm.equals("오전")) {
                                                                        if (t_h == 12) {
                                                                            t_h = 0;
                                                                        }
                                                                    } else {
                                                                        if (t_h != 12) {
                                                                            t_h += 12;
                                                                        }
                                                                    }*/


                                                                    while (cursor4.moveToNext()) {
                                                                        calendar_dinner = Calendar.getInstance();
                                                                        calendar_dinner.set(Calendar.HOUR_OF_DAY, t_h);
                                                                        calendar_dinner.set(Calendar.MINUTE, t_m);
                                                                        calendar_dinner.set(Calendar.SECOND, 0);
                                                                        if (calendar_dinner.before(Calendar.getInstance())) {
                                                                            calendar_dinner.add(Calendar.DATE, 1);
                                                                        }
                                                                        Log.i(String.valueOf(this), "dinner_let");
                                                                        Intent alarm_dinner = new Intent(context, AlarmReceiver.class);
                                                                        alarm_dinner.putExtra("message", cursor4.getString(6));
                                                                        alarm_dinner.putExtra("time", "저녁");
                                                                        alarm_dinner.putExtra("code", cursor4.getString(5));
                                                                        PendingIntent pendingIntent_dinner =
                                                                                PendingIntent.getBroadcast
                                                                                        (context, Integer.parseInt(cursor4.getString(5)),
                                                                                                alarm_dinner, PendingIntent.FLAG_CANCEL_CURRENT);
                                                                        AlarmManager alarmManager_dinner =
                                                                                (AlarmManager) context.getSystemService(
                                                                                        Context.ALARM_SERVICE
                                                                                );
                                                                        alarmManager_dinner.setRepeating(AlarmManager.RTC_WAKEUP,
                                                                                calendar_dinner.getTimeInMillis(),
                                                                                AlarmManager.INTERVAL_DAY,
                                                                                pendingIntent_dinner);
                                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                                            alarmManager_dinner.setExactAndAllowWhileIdle
                                                                                    (AlarmManager.RTC_WAKEUP,
                                                                                            calendar_dinner.getTimeInMillis(),
                                                                                            pendingIntent_dinner);
                                                                        }
                                                                    }

                                                                }
                                                            }, mhour, mminute, false
                                                    );
                                            timePickerDialog.getWindow().setBackgroundDrawable(
                                                    new ColorDrawable(Color.TRANSPARENT)
                                            );
                                            timePickerDialog.updateTime(t_h, t_m);
                                            timePickerDialog.setTitle("저녁 복용 시간");
                                            timePickerDialog.show();

                                        }
                                    });
                                }
                                if (btn_set_refresh != null) {
                                    btn_set_refresh.setOnClickListener(new View.OnClickListener() {
                                        final String pill_name =
                                                String.valueOf
                                                (PillViewHolder.this.list_name.getText());
                                        final Cursor cursor1 = myDB.getUserPillId(
                                                pill_name
                                            );
                                        final Cursor cursor4 = myDB.getAlarmSetWhen(pill_name);
                                        @Override
                                        public void onClick(View view) {
                                            PackageManager pm = context.getPackageManager();
                                            ComponentName receiver = new ComponentName
                                                    (context, DeviceBootReceiver.class);
                                            if (tv_morning != null) {
                                                tv_morning.setText
                                                        ("설정 값 없음");
                                            }
                                            if (tv_lunch != null) {
                                                tv_lunch.setText
                                                        ("설정 값 없음");
                                            }
                                            if (tv_dinner != null) {
                                                tv_dinner.setText
                                                        ("설정 값 없음");
                                            }
                                            while (cursor4.moveToNext()) {
                                                if (tv_morning != null && tv_morning.getText().toString().equals("설정 값 없음")) {
                                                    Intent alarm_morning = new Intent(context, AlarmReceiver.class);
                                                    PendingIntent pendingIntent_morning =
                                                            PendingIntent.getBroadcast
                                                                    (context, Integer.parseInt(cursor4.getString(3)),
                                                                            alarm_morning, PendingIntent.FLAG_CANCEL_CURRENT);
                                                    AlarmManager alarmManager_morning =
                                                            (AlarmManager) context.getSystemService(
                                                                    Context.ALARM_SERVICE
                                                            );
                                                    alarmManager_morning.cancel(pendingIntent_morning);
                                                }
                                                if (tv_lunch != null && tv_lunch.getText().toString().equals("설정 값 없음")) {
                                                    Intent alarm_lunch = new Intent(context, AlarmReceiver.class);
                                                    PendingIntent pendingIntent_lunch =
                                                            PendingIntent.getBroadcast
                                                                    (context, Integer.parseInt(cursor4.getString(4)),
                                                                            alarm_lunch, PendingIntent.FLAG_CANCEL_CURRENT);
                                                    AlarmManager alarmManager_lunch =
                                                            (AlarmManager) context.getSystemService(
                                                                    Context.ALARM_SERVICE
                                                            );
                                                    alarmManager_lunch.cancel(pendingIntent_lunch);
                                                }
                                                if (tv_dinner != null && tv_dinner.getText().toString().equals("설정 값 없음")) {
                                                    Intent alarm_dinner = new Intent(context, AlarmReceiver.class);
                                                    PendingIntent pendingIntent_dinner =
                                                            PendingIntent.getBroadcast
                                                                    (context, Integer.parseInt(cursor4.getString(5)),
                                                                            alarm_dinner, PendingIntent.FLAG_CANCEL_CURRENT);
                                                    AlarmManager alarmManager_dinner =
                                                            (AlarmManager) context.getSystemService(
                                                                    Context.ALARM_SERVICE
                                                            );
                                                    alarmManager_dinner.cancel(pendingIntent_dinner);
                                                }
                                                pm.setComponentEnabledSetting(receiver,
                                                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                                        PackageManager.DONT_KILL_APP);

                                            }
                                        }
                                    });
                                }
                                if (btn_set_alarm != null) {
                                    btn_set_alarm.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            pill_name =
                                                    String.valueOf
                                                            (PillViewHolder.this.list_name.getText());
                                            cursor1 = myDB.getUserPillId(
                                                    pill_name
                                            );
                                            Cursor cursor4 = myDB.getAlarmSetWhen(pill_name);

                                            b = new ArrayList<>();
                                            //myDB.updatePaper();
                                            if (tv_morning != null) {
                                                if (!tv_morning.getText().toString()
                                                        .equals("설정 값 없음")
                                                        || !tv_morning.getText().toString().equals("알림 세팅 전입니다...")) {
                                                    alarm_time = tv_morning
                                                            .getText().toString();
                                                }else {
                                                    alarm_time = "설정 값 없음";
                                                }
                                                b.add(alarm_time);
                                            }
                                            if (tv_lunch != null) {
                                                if (!tv_lunch.getText().toString()
                                                        .equals("설정 값 없음")) {
                                                    alarm_time = tv_lunch
                                                            .getText().toString();
                                                }else {
                                                    alarm_time = "설정 값 없음";
                                                }
                                                b.add(alarm_time);
                                            }
                                            if (tv_dinner != null) {
                                                if (!tv_dinner.getText().toString()
                                                        .equals("설정 값 없음")) {
                                                    alarm_time = tv_dinner
                                                            .getText().toString();
                                                }else {
                                                    alarm_time = "설정 값 없음";
                                                }
                                                b.add(alarm_time);
                                            }
                                            if (tv_dinner != null && tv_lunch != null && tv_morning != null
                                                    && tv_morning.getText().toString().equals("설정 값 없음")
                                                    && tv_lunch.getText().toString().equals("설정 값 없음")
                                                    && tv_dinner.getText().toString().equals("설정 값 없음")) {
                                                b.clear();
                                                b.add("알림 세팅 전입니다...");
                                            }
                                            Log.i(String.valueOf(this), "Here : -2");
                                            all_p_alarm = b.toArray(new String[b.size()]);

                                            while (cursor1.moveToNext()) {
                                                boolean var = myDB.updatePill(
                                                        cursor1.getString(0),
                                                        name,
                                                        list_name.getText().toString(),
                                                        list_ache.getText().toString(),
                                                        list_fd.getText().toString(),
                                                        list_ld.getText().toString(),
                                                        all_p_alarm
                                                );
                                                boolean var1 = false;
                                                Log.i(String.valueOf(this), "Here : -1");
                                                Cursor cursor3 = myDB.getAlarmList(cursor1.getString(0));
                                                if (cursor3.getCount() < 1) {
                                                    Log.i(String.valueOf(this), "Here : 0");
                                                    if (tv_morning != null) {
                                                        if (tv_lunch != null) {
                                                            if (tv_dinner != null) {
                                                                var1 = myDB.addAlarm(
                                                                        tv_morning.getText().toString(),
                                                                        tv_lunch.getText().toString(),
                                                                        tv_dinner.getText().toString(),
                                                                        pill_name,
                                                                        cursor1.getString(0),
                                                                        Integer.parseInt(cursor1.getString(0)) * 1000,
                                                                        Integer.parseInt(cursor1.getString(0)) * 1001,
                                                                        Integer.parseInt(cursor1.getString(0)) * 1002
                                                                );
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    while (cursor3.moveToNext()) {
                                                        Log.i(String.valueOf(this), "Here : 1");
                                                        while (cursor4.moveToNext()) {
                                                            if (tv_morning != null) {
                                                                if (tv_lunch != null) {
                                                                    if (tv_dinner != null) {
                                                                        var1 = myDB.updateAlarmList(
                                                                                cursor3.getString(0),
                                                                                tv_morning.getText().toString(),
                                                                                tv_lunch.getText().toString(),
                                                                                tv_dinner.getText().toString(),
                                                                                pill_name,
                                                                                cursor1.getString(0),
                                                                                Integer.parseInt(cursor1.getString(0)) * 1000,
                                                                                Integer.parseInt(cursor1.getString(0)) * 1001,
                                                                                Integer.parseInt(cursor1.getString(0)) * 1002
                                                                        );
                                                                    }
                                                                }
                                                            }
                                                        }

                                                    }

                                                }
                                                cursor3.close();
                                                if (var && var1) {
                                                    //setAlarm();
                                                    Toast.makeText(context,
                                                            "정상적으로 알람 설정이 완료되었습니다.",
                                                            Toast.LENGTH_SHORT).show();
                                                    Log.i(String.valueOf(this), "alarm_Set");
                                                    PackageManager pm = context.getPackageManager();
                                                    ComponentName receiver = new ComponentName
                                                            (context, DeviceBootReceiver.class);

                                                    pm.setComponentEnabledSetting(receiver,
                                                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                                            PackageManager.DONT_KILL_APP);

                                                    bottomSheetDialog0.dismiss();
                                                } else {
                                                    Log.i(String.valueOf(this), "alarm_Set_Error");
                                                }


                                            }
                                        }
                                    });
                                }

                                bottomSheetDialog0.show();
                                return true;
                            case 1:
                                final Calendar c = new GregorianCalendar();
                                int mYear = c.get(Calendar.YEAR);
                                int mMonth = c.get(Calendar.MONTH);
                                int mDay = c.get(Calendar.DAY_OF_MONTH);
                                EditText et_what_medicine, et_what_aches;
                                TextView tv_what_first_date, tv_what_last_date
                                        , tv_name_toolbar;
                                Button btn_finish_adding;
                                ImageView iv_search, iv_back, iv_info;
                                bottomSheetDialog1 = new BottomSheetDialog(
                                        context, R.style.BottomSheetDialogTheme
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
                                iv_info = bottomSheetDialog1.findViewById(R.id.img_info);
                                tv_name_toolbar = bottomSheetDialog1.findViewById(R.id.toolbar_title_makers);

                                if (iv_info != null) {
                                    iv_info.setVisibility(View.GONE);
                                }
                                if (tv_name_toolbar != null) {
                                    tv_name_toolbar.setText("변경사항이 있으신가요?");
                                }
                                if (tv_what_first_date != null) {
                                    tv_what_first_date.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (android.os.Build.VERSION.SDK_INT
                                                    >= android.os.Build.VERSION_CODES.N) {
                                                DatePickerDialog datePickerDialog =
                                                        new DatePickerDialog
                                                                (context,
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
                                                                (context,
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
                                assert et_what_medicine != null;
                                et_what_medicine.setText(list_name.getText());
                                assert et_what_aches != null;
                                et_what_aches.setText(list_ache.getText());
                                assert tv_what_first_date != null;
                                tv_what_first_date.setText(list_fd.getText());
                                assert tv_what_last_date != null;
                                tv_what_last_date.setText(list_ld.getText());
                                pill_name =
                                        String.valueOf
                                                (PillViewHolder.this.list_name.getText());
                                cursor1 = myDB.getUserPillId(
                                        pill_name
                                );
                                all_p_alarm = new String[]{"알람 세팅 전입니다..."};
                                if (btn_finish_adding != null) {
                                    btn_finish_adding.setOnClickListener
                                            (new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            while (cursor1.moveToNext()) {
                                                boolean var = myDB.updatePill(
                                                        cursor1.getString(0),
                                                        name,
                                                        et_what_medicine.getText().toString(),
                                                        et_what_aches.getText().toString(),
                                                        tv_what_first_date.getText().toString(),
                                                        tv_what_last_date.getText().toString(),
                                                        all_p_alarm
                                                );
                                                if (var) {
                                                    Toast.makeText(context,
                                                            "정보변경이 정상적으로 처리되었습니다.", Toast.LENGTH_SHORT).show();
                                                    bottomSheetDialog1.dismiss();
                                                } else {
                                                    Toast.makeText(context,
                                                            "정보변경 과정에서 에러가 발생했습니다. " +
                                                                    "\n다시 시도 부탁드립니다.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                        }
                                    });
                                }
                                bottomSheetDialog1.show();



                                return true;
                            case 2:
                                pill_name =
                                        String.valueOf
                                                (PillViewHolder.this.list_name.getText());
                                cursor1 = myDB.getUserPillId(
                                        pill_name
                                );
                                if (cursor1.getCount() == 1) {
                                    while (cursor1.moveToNext()) {
                                        Log.i(String.valueOf(this),
                                                String.valueOf(cursor1.getString(0)));
                                        myDB.deletePill(cursor1.getString(0));
                                        myDB.deleteAlarm(cursor1.getString(0));
                                    }
                                } else {
                                    while (cursor1.moveToFirst()) {
                                        myDB.deletePill(cursor1.getString(0));
                                        myDB.deleteAlarm(cursor1.getString(0));
                                    }
                                }

                                Toast.makeText(context,
                                        "삭제가 정상적으로 처리되었습니다.", Toast.LENGTH_SHORT).show();


                                return true;
                        }
                        return false;
                    }
                };
    }


}
