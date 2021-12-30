package com.example.java_final_project.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;

import java.util.Calendar;
import java.util.Objects;

public class DeviceBootReceiver extends BroadcastReceiver {

    private static DataBaseHelper myDB;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {

            String your_name = SharedPreference.getName(context,
                    "Name");
            Cursor cursor = myDB.getUserPills(your_name);
            for (int i = 0; i < cursor.getCount(); i++) {
                while (cursor.moveToNext()) {
                    String pill_name = cursor.getString(1);
                    Cursor cursor4 = myDB.getAlarmSetWhen(pill_name);
                    while (cursor4.moveToNext()) {
                        // on device boot complete, reset the alarm
                        if (!cursor4.getString(0).equals("설정 값 없음")) {
                            Intent alarm_morning = new Intent(context, AlarmReceiver.class);
                            alarm_morning.putExtra("message", cursor4.getString(6));
                            alarm_morning.putExtra("time", "아침");
                            alarm_morning.putExtra("code", cursor4.getString(3));
                            String time_all = cursor4.getString(0);
                            int hour, minute;
                            hour = Integer.parseInt(time_all.substring
                                    (0, time_all.indexOf(":")));
                            minute = Integer.parseInt(time_all.substring
                                    (time_all.indexOf(":") + 1,
                                            time_all.indexOf("(")));
                            Calendar calendar_morning = Calendar.getInstance();
                            Calendar now = Calendar.getInstance();
                            calendar_morning.set(Calendar.HOUR_OF_DAY, hour);
                            calendar_morning.set(Calendar.MINUTE, minute);
                            calendar_morning.set(Calendar.SECOND, 0);
                            if (now.after(calendar_morning)) {
                                calendar_morning.add(Calendar.DATE, 1);
                            }
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
                                    AlarmManager.INTERVAL_DAY,
                                    pendingIntent_morning);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                alarmManager_morning.setExactAndAllowWhileIdle
                                        (AlarmManager.RTC_WAKEUP,
                                                calendar_morning.getTimeInMillis(),
                                                pendingIntent_morning);
                            }
                            alarmManager_morning.setRepeating(AlarmManager.RTC_WAKEUP,
                                    calendar_morning.getTimeInMillis(),
                                    AlarmManager.INTERVAL_DAY, pendingIntent_morning);
                        }
                        if (!cursor4.getString(1).equals("설정 값 없음")) {
                            Intent alarm_lunch = new Intent(context, AlarmReceiver.class);
                            alarm_lunch.putExtra("message", cursor4.getString(6));
                            alarm_lunch.putExtra("time", "점심");
                            alarm_lunch.putExtra("code", cursor4.getString(4));
                            String time_all = cursor4.getString(1);
                            int hour, minute;
                            hour = Integer.parseInt(time_all.substring
                                    (0, time_all.indexOf(":")));
                            minute = Integer.parseInt(time_all.substring
                                    (time_all.indexOf(":") + 1,
                                            time_all.indexOf("(")));
                            Calendar calendar_lunch = Calendar.getInstance();
                            Calendar now = Calendar.getInstance();
                            calendar_lunch.set(Calendar.HOUR_OF_DAY, hour);
                            calendar_lunch.set(Calendar.MINUTE, minute);
                            calendar_lunch.set(Calendar.SECOND, 0);
                            if (now.after(calendar_lunch)) {
                                calendar_lunch.add(Calendar.DATE, 1);
                            }
                            PendingIntent pendingIntent_lunch =
                                    PendingIntent.getBroadcast
                                            (context, Integer.parseInt(cursor4.getString(5)),
                                                    alarm_lunch, PendingIntent.FLAG_CANCEL_CURRENT);
                            AlarmManager alarmManager_lunch =
                                    (AlarmManager) context.getSystemService(
                                            Context.ALARM_SERVICE
                                    );
                            alarmManager_lunch.setRepeating(AlarmManager.RTC_WAKEUP,
                                    calendar_lunch.getTimeInMillis(),
                                    AlarmManager.INTERVAL_DAY,
                                    pendingIntent_lunch);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                alarmManager_lunch.setExactAndAllowWhileIdle
                                        (AlarmManager.RTC_WAKEUP,
                                                calendar_lunch.getTimeInMillis(),
                                                pendingIntent_lunch);
                            }
                            alarmManager_lunch.setRepeating(AlarmManager.RTC_WAKEUP,
                                    calendar_lunch.getTimeInMillis(),
                                    AlarmManager.INTERVAL_DAY, pendingIntent_lunch);
                        }
                        if (!cursor4.getString(2).equals("설정 값 없음")) {
                            Intent alarm_dinner = new Intent(context, AlarmReceiver.class);
                            alarm_dinner.putExtra("message", cursor4.getString(6));
                            alarm_dinner.putExtra("time", "저녁");
                            alarm_dinner.putExtra("code", cursor4.getString(5));
                            String time_all = cursor4.getString(2);
                            int hour, minute;
                            hour = Integer.parseInt(time_all.substring
                                    (0, time_all.indexOf(":")));
                            minute = Integer.parseInt(time_all.substring
                                    (time_all.indexOf(":") + 1,
                                            time_all.indexOf("(")));
                            Calendar calendar_dinner = Calendar.getInstance();
                            Calendar now = Calendar.getInstance();
                            calendar_dinner.set(Calendar.HOUR_OF_DAY, hour);
                            calendar_dinner.set(Calendar.MINUTE, minute);
                            calendar_dinner.set(Calendar.SECOND, 0);
                            if (now.after(calendar_dinner)) {
                                calendar_dinner.add(Calendar.DATE, 1);
                            }
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
                            alarmManager_dinner.setRepeating(AlarmManager.RTC_WAKEUP,
                                    calendar_dinner.getTimeInMillis(),
                                    AlarmManager.INTERVAL_DAY, pendingIntent_dinner);
                        }

                    }

                }
            }

        }
    }
}
