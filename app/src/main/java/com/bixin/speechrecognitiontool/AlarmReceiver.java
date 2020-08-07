package com.bixin.speechrecognitiontool;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.bixin.speechrecognitiontool.txz.TXZManagerTool;

public class AlarmReceiver extends BroadcastReceiver {
    private PendingIntent pi;
    private final int anHour = 60 * 1000;// 这是8小时的毫秒数 为了少消耗流量和电量，8小时自动更新一次

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "AlarmReceiver onReceive: ");
        scheduleAlarms(context);
    }

    @SuppressLint("NewApi")
    private void scheduleAlarms(Context context) {
        TXZManagerTool.getWeatherInfo();
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (pi != null) {
            mgr.cancel(pi);
        }
        Intent i = new Intent(context, AlarmReceiver.class);
        i.setAction("great");
        pi = PendingIntent.getBroadcast(context, 0, i, 0);
        mgr.setWindow(AlarmManager.ELAPSED_REALTIME, triggerAtTime, anHour, pi);
    }
}
