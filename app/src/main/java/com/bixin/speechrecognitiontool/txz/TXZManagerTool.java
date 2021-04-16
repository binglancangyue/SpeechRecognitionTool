package com.bixin.speechrecognitiontool.txz;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSONObject;
import com.bixin.speechrecognitiontool.R;
import com.bixin.speechrecognitiontool.SpeechApplication;
import com.bixin.speechrecognitiontool.mode.CustomValue;
import com.bixin.speechrecognitiontool.mode.bean.WeatherBean;
import com.txznet.sdk.TXZConfigManager;
import com.txznet.sdk.TXZNavManager;
import com.txznet.sdk.TXZNetDataProvider;
import com.txznet.sdk.bean.WeatherData;

import java.util.HashMap;
import java.util.Map;

import static com.txznet.sdk.TXZConfigManager.FloatToolType.FLOAT_NONE;

/**
 * @author Altair
 * @date :2020.03.23 上午 10:38
 * @description:
 */
public class TXZManagerTool {
    private static final String TAG = "TXZTool";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void iniTxz() {
        Log.d(TAG, "iniTxz: ");
        Context context = SpeechApplication.getInstance();
        //  获取接入分配的appId和appToken
        final String appId;
        final String appToken;
        if (CustomValue.USER_ID_TYPE == 0) {
            appId = context.getResources().getString(
                    R.string.txz_sdk_init_app_id_old);
            appToken = context.getResources().getString(
                    R.string.txz_sdk_init_app_token_old);
        } else if (CustomValue.USER_ID_TYPE == 2) {
            appId = context.getResources().getString(
                    R.string.txz_sdk_init_app_id_free);
            appToken = context.getResources().getString(
                    R.string.txz_sdk_init_app_token_free);
        } else {
            appId = context.getResources().getString(
                    R.string.txz_sdk_init_app_id);
            appToken = context.getResources().getString(
                    R.string.txz_sdk_init_app_token);
        }
        //  设置初始化参数
        TXZConfigManager.InitParam mInitParam = new TXZConfigManager.InitParam(appId, appToken);
        //  可以设置自己的客户ID，同行者后台协助计数/鉴权
        // mInitParam.setAppCustomId("ABCDEFG");
        //  可以设置自己的硬件唯一标识码
//         mInitParam.setUUID(getIMEIforO());

        // 掩藏语音按钮
        mInitParam.setFloatToolType(TXZConfigManager.FloatToolType.FLOAT_NONE);
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
        if (CustomValue.USER_ID_TYPE == 1) {
            //TODO 设置UUID 需配合最新收费id使用
            mInitParam.setUUID("565405388");
        }
//        TXZNavManager.getInstance().setNavDefaultTool(TXZNavManager.NavToolType.NAV_TOOL_KAILIDE_NAV);
        TXZConfigManager.getInstance().initialize(context, mInitParam,
                new TXZConfigManager.InitListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "TXZ onSuccess: ");
                        AsrWakeUpInitManager.getInstance().init();
                        TXZVoiceControl txzVoiceControl = new TXZVoiceControl();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(10000);
                                    getWeatherInfo();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.d(TAG, "TXZ onError:code " + i + " s:" + s);
                    }
                }, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getIMEIforO() {
        TelephonyManager tm = ((TelephonyManager) SpeechApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE));
        String imei1 = tm.getImei(0);
        Log.d(TAG, "getIMEIforO: " + imei1);
        return imei1;
    }

    public static void getWeatherInfo() {
        TXZNetDataProvider.getInstance().getWeatherInfo(new TXZNetDataProvider.NetDataCallback<WeatherData>() {
            @Override
            public void onResult(WeatherData weatherData) {
                WeatherData.WeatherDay weatherDays = weatherData.weatherDays[0];
                WeatherBean weatherBean = new WeatherBean();
                weatherBean.setCityName(weatherData.cityName);
                weatherBean.setCurrentTemperature(weatherDays.currentTemperature);
                weatherBean.setWeather(weatherDays.weather);
                String json = JSONObject.toJSONString(weatherBean);
                sendToLauncher(json);
                Log.d(TAG, "getWeatherInfo onResult: " + json);
            }

            @Override
            public void onError(int i) {
                Log.e(TAG, "onError code : " + i);
            }
        });
    }

    private static void sendToLauncher(String weather) {
        Intent intent = new Intent();
        intent.setAction(CustomValue.ACTION_UPDATE_WEATHER);
//        intent.setComponent(new ComponentName("com.bixin.launcher_t20",
//                "com.bixin.launcher_t20.model.receiver.WeatherReceiver"));
        intent.putExtra("weatherInfo", weather);
        SpeechApplication.getInstance().sendBroadcast(intent);
    }

}
