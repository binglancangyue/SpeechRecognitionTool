package com.bixin.speechrecognitiontool;

import android.util.Log;

import androidx.multidex.MultiDexApplication;

/**
 * @author Altair
 * @date :2020.03.23 上午 10:36
 * @description:
 */
public class SpeechApplication extends MultiDexApplication {
    private static SpeechApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        Log.d("SpeechApplication", "SpeechApplication onCreate: ");
//        TXZManagerTool.iniTxz();
    }

    public static SpeechApplication getInstance() {
        return mApplication;
    }
}
