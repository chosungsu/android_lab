package com.example.java_final_project.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.java_final_project.MainBoard;
import com.example.java_final_project.R;
import com.example.java_final_project.adapter.PillAdapter;
import com.example.java_final_project.fragment.MediFragment;
import com.example.java_final_project.model.Pill_items;
import com.example.java_final_project.utils.DataBaseHelper;
import com.example.java_final_project.utils.SharedPreference;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    //오레오 이상은 반드시 채널을 설정해줘야 Notification이 작동함
    private static String CHANNEL_ID = "약";
    private static String CHANNEL_NAME = "복용알람";
    private static String pill_name;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager manager = (NotificationManager)
                context.getSystemService
                        (Context.NOTIFICATION_SERVICE);
        Log.w(String.valueOf(this), "entering...1");
        pill_name = intent.getStringExtra("message");
        String time = intent.getStringExtra("time");
        String code = intent.getStringExtra("code");
        Intent intent2 = new Intent(context, MainBoard.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity
                (context, Integer.parseInt(code),
                        intent2, 0);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID + Integer.parseInt(code))
                        .setContentTitle("복용 시간 알려드립니다.")
                        .setContentText(time + "타임 알람 : " + pill_name + "약을 복용하실 시간입니다.")
                        .setSmallIcon(R.drawable.main_icon)
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                manager.createNotificationChannel(new NotificationChannel(
                        CHANNEL_ID + Integer.parseInt(code), CHANNEL_NAME + Integer.parseInt(code),
                        NotificationManager.IMPORTANCE_HIGH
                ));
            }
        }
        Vibrator vibrator = (Vibrator) context.getSystemService
                (Context.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[] {
                1000, 1000, 1000, 1000
        }, -1);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
        ringtone.play();

        Log.w(String.valueOf(this), "entering...2");
        manager.notify(Integer.parseInt(code), builder.build());
        Log.w(String.valueOf(this), "entering...3");
        Calendar nextnoti = Calendar.getInstance();
        nextnoti.add(Calendar.DATE, 1);


    }
}
