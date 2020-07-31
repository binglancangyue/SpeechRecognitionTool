package com.bixin.speechrecognitiontool.txz;

import android.content.Context;
import android.util.Log;

import com.bixin.speechrecognitiontool.R;
import com.bixin.speechrecognitiontool.SpeechApplication;
import com.txznet.sdk.TXZConfigManager;

import static com.txznet.sdk.TXZConfigManager.FloatToolType.FLOAT_NONE;

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

        // 掩藏语音按钮
        mInitParam.setFloatToolType(FLOAT_NONE);
//        //  可以按需要设置自己的对话模式
//        mInitParam.setAsrMode(TXZConfigManager.AsrMode.ASR_MODE_CHAT);
//        //  设置识别模式，默认自动模式即可
//        mInitParam.setAsrServiceMode(TXZConfigManager.AsrServiceMode.ASR_SVR_MODE_AUTO);
//        //  设置是否允许启用服务号
//        mInitParam.setEnableServiceContact(true);
//        //  设置开启回音消除模式
//        mInitParam.setFilterNoiseType(1);
        // 是否启用免唤醒词功能
        mInitParam.setInstantAsrEnabled(true);
//        //  设置识别和tts引擎类型
//        mInitParam.setAsrType(TXZConfigManager.AsrEngineType.ASR_YUNZHISHENG).setTtsType(
//                TXZConfigManager.TtsEngineType.TTS_YUNZHISHENG);
        //  设置唤醒词
        String[] wakeupKeywords = context.getResources().getStringArray(
                R.array.txz_sdk_init_wakeup_keywords);
        mInitParam.setWakeupKeywordsNew(wakeupKeywords);
        TXZConfigManager.getInstance().initialize(context, mInitParam,
                new TXZConfigManager.InitListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "TXZ onSuccess: ");
                        AsrWakeUpInitManager.getInstance().init();
                        TXZVoiceControl txzVoiceControl = new TXZVoiceControl();
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.d(TAG, "TXZ onError: ");
                    }
                }, null);
    }

}
