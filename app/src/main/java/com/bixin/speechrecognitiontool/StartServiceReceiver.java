package com.bixin.speechrecognitiontool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("StartServiceReceiver", "StartServiceReceiver onReceive: ");
        context.startService(new Intent(context, SpeechRecognitionService.class));
    }
}
