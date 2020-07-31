package com.bixin.speechrecognitiontool.txz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bixin.speechrecognitiontool.SpeechApplication;
import com.bixin.speechrecognitiontool.mode.CustomValue;
import com.txznet.sdk.TXZAsrManager;

/**
 * @author Altair
 * @date :2020.03.23 上午 11:19
 * @description:
 */
public class TXZBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "TXZBroadcastReceiver";
    private SettingsFunctionTool settingsFunctionTool;

    public TXZBroadcastReceiver() {
        this.settingsFunctionTool = new SettingsFunctionTool();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals(CustomValue.ACTION_TXZ_SEND)) {
            String actionString = intent.getStringExtra("action");
            Log.d(TAG, "onReceive:actionString " + actionString);
            switch (actionString) {
                case "light.up":
                    sendToActivity(1, 20);
                    break;
                case "light.down":
                    sendToActivity(1, -20);
                    break;
                case "light.max":
                    sendToActivity(1, 100);
                    break;
                case "light.min":
                    sendToActivity(1, 0);
                    break;
                case "volume.up":
                    sendToActivity(2, 3);
                    break;
                case "volume.down":
                    sendToActivity(2, -3);
                    break;
                case "volume.max":
                    sendToActivity(2, 15);
                    break;
                case "volume.min":
                    sendToActivity(2, 1);
                    break;
                case "volume.mute":
                    boolean isMute = intent.getBooleanExtra("mute", true);
                    Log.d(TAG, "onReceive: " + isMute);
                    if (isMute) {
                        sendToActivity(2, 0);
                    }
                    break;
                case "wifi.open":
                    sendToActivity(3, 1);
                    break;
                case "wifi.close":
                    sendToActivity(3, 0);
                    break;
                case "screen.close":
                    sendToActivity(4, 0);
                    break;
                case "screen.open":
                    sendToActivity(4, 1);
                    break;
                case "go.home":
                    sendToActivity(5, 0);
                case "bluetooth.open":
                    sendToActivity(6, 1);
                    break;
                case "bluetooth.close":
                    sendToActivity(6, 0);
                    break;
                case "radio.open":
                    sendToActivity(7, 1);
                    break;
                case "radio.close":
                    sendToActivity(7, 0);
                    break;
            }
        }
        if (action.equals(CustomValue.ACTION_OPEN_TXZ_VIEW)) {
            openTXZView();
        }
    }

    //    private void sendToActivity(int type, int value) {
//        Intent intent = new Intent();
//        intent.setAction(CustomValue.ACTION_SEND_TO_SYSTEM_UI);
//        Bundle bundle = new Bundle();
//        bundle.putInt("key_type", type);
//        bundle.putInt("key_value", value);
//        intent.putExtras(bundle);
//        if (Build.VERSION.SDK_INT >= 27) {
//            intent.setComponent(new ComponentName("com.android.systemui",
//                    "com.android.systemui.activity.TXZBroadcastReceiver"));
//        }
//        SpeechApplication.getInstance().sendBroadcast(intent);
//    }
    private void sendToActivity(int type, int value) {
        switch (type) {
            case 1:
                settingsFunctionTool.updateBrightness(value);
            case 2:
                settingsFunctionTool.updateVolume(value);
                break;
            case 3:
                settingsFunctionTool.setWifiStatue(value);
                break;
            case 4:
                if (value == 1) {//open

                } else {//close

                }
                break;
            case 5:
                Intent intentGoHome = new Intent();
                intentGoHome.setAction(Intent.ACTION_MAIN);// "android.intent.action.MAIN"
                intentGoHome.addCategory(Intent.CATEGORY_HOME); //"android.intent.category
                // .HOME"
                SpeechApplication.getInstance().startActivity(intentGoHome);
                break;
            case 6:
                if (settingsFunctionTool.isBlueToothEnable()) {
                    if (value != 1) {
                        settingsFunctionTool.openOrCloseBT(false);
                    }
                } else {
                    settingsFunctionTool.openOrCloseBT(true);
                }
                break;
            case 7:
                if (value == 1) {
                    settingsFunctionTool.openOrCloseFM(true);
                } else {
                    settingsFunctionTool.openOrCloseFM(false);
                }
                break;
        }
    }

    public void openTXZView() {
        Intent it = new Intent("com.txznet.txz.config.change");
        it.putExtra("type", "screen");
        it.putExtra("x", 0);
        it.putExtra("y", 0);
//        it.putExtra("width", 1024);
//        it.putExtra("height", 517);
        SpeechApplication.getInstance().sendBroadcast(it);
        TXZAsrManager.getInstance().triggerRecordButton();
    }

}
