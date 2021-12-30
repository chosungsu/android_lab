package com.example.java_final_project.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.java_final_project.MainBoard;
import com.example.java_final_project.R;

public class PedometerService extends Service {

    //private BackgroundTask task;
    //private int step;
    private static String CHANNEL_ID = "channel2";
    private static String CHANNEL_NAME = "만보기 채널";
    private static int CHANNEL_NOTI = 99999;
    private int step_counter_from_main;
    private String step_counter_to_main;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        step_counter_from_main = intent.getIntExtra("COUNT", 0);
        //task = new BackgroundTask();
        //task.execute();
        init();
        return START_REDELIVER_INTENT;
    }
    public void init() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle("현재 걸음 수");
        builder.setContentText(String.valueOf(step_counter_from_main));
        builder.setSmallIcon(R.drawable.main_icon);
        Intent notify = new Intent(this, MainBoard.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notify, 0
        );
        builder.setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService
                (NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                manager.createNotificationChannel
                        (new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                                NotificationManager.IMPORTANCE_NONE));
            }
        }
        Notification notification = builder.build();
        startForeground(CHANNEL_NOTI, notification);
    }
    /*class BackgroundTask extends AsyncTask<Integer, String, Integer> {

        @Override
        protected Integer doInBackground(Integer... integers) {
            while (!isCancelled()) {
                step_counter_from_main++;
            }
            return step_counter_from_main;
        }

    }*/
}
