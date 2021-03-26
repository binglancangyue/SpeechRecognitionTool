package com.bixin.speechrecognitiontool.txz;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.bixin.speechrecognitiontool.SpeechApplication;
import com.bixin.speechrecognitiontool.mode.CustomValue;
import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZPowerManager;
import com.txznet.sdk.TXZResourceManager;
import com.txznet.sdk.TXZTtsManager;

import static android.content.ContentValues.TAG;


/**
 * Created by TownDream on 2020/7/31.
 * 注册免唤醒命令字
 */
public class AsrWakeUpInitManager {
    private static AsrWakeUpInitManager instance;
    private final String TAG = "AsrWakeUpInitManager";
    public static final String APP_BX_BT = "com.bixin.bluetooth";
    public static final String APP_E_GOD = "com.hdsc.edog";

    TXZAsrManager.AsrComplexSelectCallback asr;

    private AsrWakeUpInitManager() {
    }

    public static AsrWakeUpInitManager getInstance() {
        if (instance == null) {
            synchronized (AsrWakeUpInitManager.class) {
                if (instance == null)
                    instance = new AsrWakeUpInitManager();
            }
        }
        return instance;
    }

    /**
     * 注册同行者免唤醒指令
     */
    public void init() {
        Log.d(TAG, "init: 免唤醒初始化");
        asr = new TXZAsrManager.AsrComplexSelectCallback() {
            @Override
            public String getTaskId() {
                return "Asr_Commands";
            }

            @Override
            public boolean needAsrState() {
                return false;
            }

            @Override
            public void onCommandSelected(String type, String command) {
                super.onCommandSelected(type, command);
                TXZTtsManager.getInstance().speakText("滴");
                switch (type) {
                    case "LIGHT_DOWN":
                        TXZResourceManager.getInstance().speakTextOnRecordWin("已为您降低亮度",
                                true, null);
                        sendBroadCast("light.down");
                        break;
                    case "LIGHT_UP":
                        TXZResourceManager.getInstance().speakTextOnRecordWin("已为您增加亮度",
                                true, null);
                        sendBroadCast("light.up");
                        break;
                    case "LIGHT_MAX":
                        TXZResourceManager.getInstance().speakTextOnRecordWin("已调至最大亮度",
                                true, null);
                        sendBroadCast("light.max");
                        break;
                    case "LIGHT_MIN":
                        TXZResourceManager.getInstance().speakTextOnRecordWin("已调至最小亮度",
                                true, null);
                        sendBroadCast("light.min");
                        break;
                    case "WIFI_OPEN":
                        TXZResourceManager.getInstance().speakTextOnRecordWin("WIFI已打开",
                                true, null);
                        sendBroadCast("wifi.open");
                        break;
                    case "WIFI_CLOSE":
                        TXZResourceManager.getInstance().speakTextOnRecordWin("WIFI已关闭",
                                true, null);
                        sendBroadCast("wifi.close");
                        break;
                    case "BLUETOOTH_OPEN":
                        TXZResourceManager.getInstance().speakTextOnRecordWin("蓝牙已打开",
                                false, null);
//                        sendBroadCast("bxbluetooth.open");
                        launchAppByPackageName(APP_BX_BT);
                        break;
                    case "BLUETOOTH_CLOSE":
                        TXZResourceManager.getInstance().speakTextOnRecordWin("蓝牙已关闭",
                                true, null);
                        sendBroadCastToDVR("bt_close");
//                        sendBroadCast("bxbluetooth.close");
                        break;
                    case "VOLUME_UP":
                        TXZResourceManager.getInstance().speakTextOnRecordWin("已为您增加音量",
                                true, null);
                        sendBroadCast("volume.up");
                        break;
                    case "VOLUME_DOWN":
                        TXZResourceManager.getInstance().speakTextOnRecordWin("已为您降低音量",
                                true, null);
                        sendBroadCast("volume.down");
                        break;
                    case "VOLUME_MAX":
                        TXZResourceManager.getInstance().speakTextOnRecordWin("已调至最大音量",
                                true, null);
                        sendBroadCast("volume.max");
                        break;
                    case "VOLUME_MIN":
                        TXZResourceManager.getInstance().speakTextOnRecordWin("已调至最小音量",
                                true, null);
                        sendBroadCast("volume.min");
                        break;
                    case "VOLUME_MUTE":
                        TXZResourceManager.getInstance().speakTextOnRecordWin("已为您静音",
                                true, null);
                        sendBroadCast("volume.mute");
                        break;
                    case "FM_CLOSE":
                        TXZResourceManager.getInstance().speakTextOnRecordWin("FM已关闭",
                                true, null);
                        sendBroadCast("radio.close");
                        break;
                    case "FM_OPEN":
                        TXZResourceManager.getInstance().speakTextOnRecordWin("FM已打开",
                                false, null);
                        sendBroadCast("radio.open");
                        break;
                    case "LOOK_FRONT":
                        Log.d(TAG, "onCommandSelected LOOK_FRONT ");
                        sendBroadCastToDVR("look_front");
                        break;
                    case "LOOK_BACK":
                        Log.d(TAG, "onCommandSelected LOOK_BACK ");
                        sendBroadCastToDVR("look_back");
                        break;
                    case "E_DOG_OPEN":
                        Log.d(TAG, "onCommandSelected DOG_OPEN ");
                        launchAppByPackageName(APP_E_GOD);
                        break;
                    case "E_DOG_CLOSE":
                        Log.d(TAG, "onCommandSelected DOG_CLOSE ");
                        break;
                    case "DVR_CLOSE":
                        Log.d(TAG, "onCommandSelected DVR_CLOSE ");
                        sendBroadCastToDVR("dvr_close");
                        break;
                }
            }
        }.addCommand("LIGHT_DOWN", "降低亮度", "减小亮度")
                .addCommand("LIGHT_UP", "增加亮度", "提高亮度")
                .addCommand("LIGHT_MAX", "最大亮度")
                .addCommand("LIGHT_MIN", "最小亮度")
                .addCommand("VOLUME_MAX", "最大音量")
                .addCommand("VOLUME_MUTE", "关闭音量", "关闭音量")
                .addCommand("VOLUME_MIN", "最小音量")
                .addCommand("VOLUME_UP", "增加音量", "提高音量")
                .addCommand("VOLUME_DOWN", "减小音量", "降低音量")
                .addCommand("WIFI_CLOSE", "关闭WIFI")
                .addCommand("WIFI_OPEN", "打开WIFI")
                .addCommand("BLUETOOTH_OPEN", "打开蓝牙")
                .addCommand("BLUETOOTH_CLOSE", "关闭蓝牙")
                .addCommand("FM_CLOSE", "关闭FM")
                .addCommand("FM_OPEN", "打开FM", "开启FM")
                .addCommand("LOOK_FRONT", "看前面", "打开前录", "打开前摄")
                .addCommand("LOOK_BACK", "看后面", "打开后录", "打开后摄")
//                .addCommand("LOOK_DOUBLE_VIEW", "打开双屏预览")
                .addCommand("E_DOG_OPEN", "打开电子狗")
//                .addCommand("E_DOG_CLOSE", "关闭电子狗")
//                .addCommand("DVR_OPEN", "打开记录仪")
                .addCommand("DVR_CLOSE", "关闭记录仪");
        TXZAsrManager.getInstance().useWakeupAsAsr(asr);
    }

    public void recover() {
        TXZAsrManager.getInstance().recoverWakeupFromAsr("Asr_Commands");
    }

    private void sendBroadCast(String type) {
        Intent intent = new Intent(CustomValue.ACTION_TXZ_SEND);
        intent.putExtra("action", type);
        SpeechApplication.getInstance().sendBroadcast(intent);
    }

    private void sendBroadCastToDVR(String type) {
        Intent intent = new Intent(CustomValue.ACTION_SPEECH_TOOL_CMD);
        intent.putExtra("type", type);
        SpeechApplication.getInstance().sendBroadcast(intent);
    }

    /**
     * 根据包名启动应用
     *
     * @param packageName clicked app
     */
    private void launchAppByPackageName(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            Log.i(TAG, "package name is null!");
            return;
        }
        Intent launchIntent = SpeechApplication.getInstance().
                getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent == null) {
            Log.i(TAG, "app not install");
        } else {
            SpeechApplication.getInstance().startActivity(launchIntent);
        }
    }
}
