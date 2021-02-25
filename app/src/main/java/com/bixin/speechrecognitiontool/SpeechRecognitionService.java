package com.bixin.speechrecognitiontool;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bixin.speechrecognitiontool.mode.CustomValue;
import com.bixin.speechrecognitiontool.txz.TXZBroadcastReceiver;
import com.bixin.speechrecognitiontool.txz.TXZManagerTool;

import java.lang.ref.WeakReference;

/**
 * @author Altair
 * @date :2020.03.23 上午 10:35
 * @description: 注册语音广播监听
 */
@SuppressLint("Registered")
public class SpeechRecognitionService extends Service {
    public static final String CHANNEL_ID_STRING = "service_01";
    private static final String TAG = "SpeechService";
    private TXZBroadcastReceiver mTxzBroadcastReceiver;
    private int Time = 1000 * 3;//周期时间
    private final static int anHour = 60 * 60 * 1000;
    private AlarmManager manager;
    private PendingIntent pi;
    private AlarmWeatherReceiver alarmReceiver;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "SpeechService onCreate: ");
        TXZManagerTool.iniTxz();
        mTxzBroadcastReceiver = new TXZBroadcastReceiver();
        registerTXZReceiver();
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmReceiver = new AlarmWeatherReceiver(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID_STRING,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_LOW);
        notificationManager.createNotificationChannel(mChannel);
        Notification notification = new Notification.Builder(getApplicationContext(),
                CHANNEL_ID_STRING).build();
        startForeground(1, notification);
        initUpdateWeatherAlarm();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 定时获取同行者天气,时钟
     */
    private void initUpdateWeatherAlarm() {
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AlarmReceiver.class);
        pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.setWindow(AlarmManager.ELAPSED_REALTIME, triggerAtTime, anHour, pi);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void registerTXZReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CustomValue.ACTION_TXZ_SEND);
        intentFilter.addAction(CustomValue.ACTION_OPEN_TXZ_VIEW);
        intentFilter.addAction(CustomValue.ACTION_GET_WEATHER);
        intentFilter.addAction(CustomValue.ACTION_OPEN_OR_CLOSE_TXZ);
        intentFilter.addAction(CustomValue.SYSTEM_WAKE_UP);
        intentFilter.addAction(CustomValue.SYSTEM_SLEEP);
        registerReceiver(mTxzBroadcastReceiver, intentFilter);
    }

    private void unregisterTXZReceiver() {
        if (mTxzBroadcastReceiver != null) {
            unregisterReceiver(mTxzBroadcastReceiver);
        }
    }

    public static class AlarmWeatherReceiver extends BroadcastReceiver {
        private WeakReference<SpeechRecognitionService> weakReference;
        private SpeechRecognitionService mSpeechRecognitionService;
        private AlarmManager manager;
        private PendingIntent pi;
        private Context mContext;

        public AlarmWeatherReceiver(SpeechRecognitionService service) {
            this.weakReference = new WeakReference<>(service);
            this.mSpeechRecognitionService = weakReference.get();
            manager = (AlarmManager) SpeechApplication.getInstance().getSystemService(Context.ALARM_SERVICE);
            Log.d(TAG, "AlarmWeatherReceiver: create");
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("AlarmWeatherReceiver", "AlarmWeatherReceiver onReceive: ");
            mContext = context;
            scheduleAlarms();
        }

        @SuppressLint("NewApi")
        private void scheduleAlarms() {
            TXZManagerTool.getWeatherInfo();
            long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
            if (pi != null) {
                manager.cancel(pi);
            }
            Intent i = new Intent(mContext, AlarmWeatherReceiver.class);
            i.setAction("great");
            pi = PendingIntent.getBroadcast(mContext, 0, i, 0);
            manager.setWindow(AlarmManager.ELAPSED_REALTIME, triggerAtTime, anHour, pi);
        }
    }


    @SuppressLint("NewApi")
    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
        Log.d(TAG, "onDestroy: SpeechRecognitionService");
        if (manager != null) {
            manager.cancel(pi);
        }
        unregisterTXZReceiver();
    }

}
