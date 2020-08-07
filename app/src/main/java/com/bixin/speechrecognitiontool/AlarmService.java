package com.bixin.speechrecognitiontool;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

import com.bixin.speechrecognitiontool.txz.TXZManagerTool;

public class AlarmService extends Service {
    private PendingIntent pi;
    private final int anHour = 60 * 1000;// 这是8小时的毫秒数 为了少消耗流量和电量，8小时自动更新一次


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("AlarmService", "onCreate: ");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("AlarmService", "onStartCommand: ");
        scheduleAlarms(SpeechApplication.getInstance());
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("NewApi")
    private void scheduleAlarms(Context context) {
        TXZManagerTool.getWeatherInfo();
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (pi != null) {
            mgr.cancel(pi);
        }
        Intent i = new Intent(context, AlarmService.class);
        i.setAction("great");
        pi = PendingIntent.getService(context, 0, i, 0);
        mgr.setWindow(AlarmManager.ELAPSED_REALTIME, triggerAtTime, anHour, pi);
    }

}
