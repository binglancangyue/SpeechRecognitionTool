package com.bixin.speechrecognitiontool;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bixin.speechrecognitiontool.mode.CustomValue;
import com.bixin.speechrecognitiontool.txz.TXZBroadcastReceiver;
import com.bixin.speechrecognitiontool.txz.TXZManagerTool;

/**
 * @author Altair
 * @date :2020.03.23 上午 10:35
 * @description: 注册语音广播监听
 */
@SuppressLint("Registered")
public class SpeechRecognitionService extends Service {
    public static final String CHANNEL_ID_STRING = "service_01";
    private static final String TAG = "SpeechRecognitionService";
    private TXZBroadcastReceiver mTxzBroadcastReceiver;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("LongLogTag")
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "SpeechRecognitionService onCreate: ");
        TXZManagerTool.iniTxz();
        mTxzBroadcastReceiver = new TXZBroadcastReceiver();
        registerTXZReceiver();
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
        return super.onStartCommand(intent, flags, startId);
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
        registerReceiver(mTxzBroadcastReceiver, intentFilter);
    }

    private void unregisterTXZReceiver() {
        if (mTxzBroadcastReceiver != null) {
            unregisterReceiver(mTxzBroadcastReceiver);
        }
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
        unregisterTXZReceiver();
    }

}
