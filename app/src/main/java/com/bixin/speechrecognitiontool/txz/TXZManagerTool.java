package com.bixin.speechrecognitiontool.txz;

import android.content.Context;
import android.util.Log;

import com.bixin.speechrecognitiontool.R;
import com.bixin.speechrecognitiontool.SpeechApplication;
import com.txznet.sdk.TXZConfigManager;

/**
 * @author Altair
 * @date :2020.03.23 上午 10:38
 * @description:
 */
public class TXZManagerTool {
    private static final String TAG = "TXZTool";

    public static void iniTxz() {
        Context context = SpeechApplication.getInstance();
        //  获取接入分配的appId和appToken
        String appId = context.getResources().getString(
                R.string.txz_sdk_init_app_id);
        String appToken = context.getResources().getString(
                R.string.txz_sdk_init_app_token);
        //  设置初始化参数
        TXZConfigManager.InitParam mInitParam = new TXZConfigManager.InitParam(appId, appToken);
        //  可以设置自己的客户ID，同行者后台协助计数/鉴权
        // mInitParam.setAppCustomId("ABCDEFG");
        //  可以设置自己的硬件唯一标识码
        // mInitParam.setUUID("0123456789");

        //  设置识别和tts引擎类型
        mInitParam.setAsrType(TXZConfigManager.AsrEngineType.ASR_YUNZHISHENG).setTtsType(
                TXZConfigManager.TtsEngineType.TTS_YUNZHISHENG);
        //  设置唤醒词
        String[] wakeupKeywords = context.getResources().getStringArray(
                R.array.txz_sdk_init_wakeup_keywords);
        mInitParam.setWakeupKeywordsNew(wakeupKeywords);
        TXZConfigManager.getInstance().initialize(context, mInitParam,
                new TXZConfigManager.InitListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "TXZ onSuccess: ");
                        TXZVoiceControl txzVoiceControl = new TXZVoiceControl();
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.d(TAG, "TXZ onError: ");
                    }
                }, null);
    }

}
