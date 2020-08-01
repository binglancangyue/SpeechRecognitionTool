package com.bixin.speechrecognitiontool.txz;

import android.content.Intent;
import android.util.Log;

import com.bixin.speechrecognitiontool.SpeechApplication;
import com.bixin.speechrecognitiontool.mode.CustomValue;
import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZResourceManager;
import com.txznet.sdk.TXZTtsManager;


/**
 * Created by TownDream on 2020/7/31.
 * 注册免唤醒命令字
 */
public class AsrWakeUpInitManager {

    private static AsrWakeUpInitManager instance;
    private String TAG = "AsrWakeUpInitManager";

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

    public void init() {
        Log.d("txz", "init: 免唤醒初始化");
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
                switch (type) {
                    case "LIGHT_DOWN":
                        TXZTtsManager.getInstance().speakText("滴");
                        TXZResourceManager.getInstance().speakTextOnRecordWin("已为您降低亮度",
                                true, null);
                        sendBroadCast("light.down");
                        break;
                    case "LIGHT_UP":
                        TXZTtsManager.getInstance().speakText("滴");
                        TXZResourceManager.getInstance().speakTextOnRecordWin("已为您增加亮度",
                                true, null);
                        sendBroadCast("light.up");
                        break;
                    case "LIGHT_MAX":
                        TXZTtsManager.getInstance().speakText("滴");
                        TXZResourceManager.getInstance().speakTextOnRecordWin("已调至最大亮度",
                                true, null);
                        sendBroadCast("light.max");
                        break;
                    case "LIGHT_MIN":
                        TXZTtsManager.getInstance().speakText("滴");
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
                                true, null);
                        sendBroadCast("bluetooth.open");
                        break;
                    case "BLUETOOTH_CLOSE":
                        TXZResourceManager.getInstance().speakTextOnRecordWin("蓝牙已关闭",
                                true, null);
                        sendBroadCast("bluetooth.close");
                        break;
                }
            }
        }.addCommand("LIGHT_DOWN", "降低亮度", "减小亮度")
                .addCommand("LIGHT_UP", "增加亮度", "提高亮度")
                .addCommand("LIGHT_MAX", "最大亮度")
                .addCommand("LIGHT_MIN", "最小亮度")
                .addCommand("WIFI_CLOSE", "关闭WIFI")
                .addCommand("WIFI_OPEN", "打开WIFI")
                .addCommand("BLUETOOTH_OPEN", "打开蓝牙")
                .addCommand("BLUETOOTH_CLOSE", "关闭蓝牙");
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

}
