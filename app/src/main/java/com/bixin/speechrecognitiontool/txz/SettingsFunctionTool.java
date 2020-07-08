package com.bixin.speechrecognitiontool.txz;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.car.ContextDef;
import android.car.FmManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.bixin.speechrecognitiontool.SpeechApplication;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.media.AudioManager.STREAM_MUSIC;

/**
 * @author Altair
 * @date :2019.10.25 上午 09:43
 * @description: 设置弹窗功能工具类
 */
public class SettingsFunctionTool {
    private Context mContext;
    private LocationManager locationManager;
    private AudioManager audioManager;
    private final float baseValue = 2.55f;//0-255
    private BluetoothAdapter bluetoothAdapter;
    private FmManager mFmManager;
    private static final String TAG = "SettingsFunctionTool";
    private WifiManager mWifiManager;

    public SettingsFunctionTool(Context mContext) {
        this.mContext = mContext;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mWifiManager = (WifiManager) mContext.getApplicationContext().
                getSystemService(Context.WIFI_SERVICE);
    }

    public SettingsFunctionTool() {
        this.mContext = SpeechApplication.getInstance();
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mFmManager = (FmManager) getSystemService(mContext, ContextDef.FM_SERVICE);
        mWifiManager = (WifiManager) mContext.getApplicationContext().
                getSystemService(Context.WIFI_SERVICE);
    }

    /*private final ContentObserver mGpsMonitor = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            boolean enabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
    };

    public boolean checkGpsIsOpen() {
        boolean isGpsOpen;
        isGpsOpen = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isGpsOpen;
    }

    public void openOrCloseGPS(boolean open) {
        if (open) {
            mContext.getContentResolver()
                    .registerContentObserver(Settings.Secure.getUriFor(Settings.System
                    .LOCATION_PROVIDERS_ALLOWED),
                            false, mGpsMonitor);
        } else {
            mContext.getContentResolver().unregisterContentObserver(mGpsMonitor);
        }
    }*/

    //打开或者关闭gps
    @SuppressLint("ObsoleteSdkInt")
    public void openGPS(boolean open) {
        if (Build.VERSION.SDK_INT < 19) {
            Settings.Secure.setLocationProviderEnabled(mContext.getContentResolver(),
                    LocationManager.GPS_PROVIDER, open);
        } else {
            if (!open) {
                Settings.Secure.putInt(mContext.getContentResolver(),
                        Settings.Secure.LOCATION_MODE,
                        android.provider.Settings.Secure.LOCATION_MODE_OFF);
            } else {
                Settings.Secure.putInt(mContext.getContentResolver(),
                        Settings.Secure.LOCATION_MODE,
                        android.provider.Settings.Secure.LOCATION_MODE_BATTERY_SAVING);
            }
        }
    }

    //判断gps是否处于打开状态
    @SuppressLint("ObsoleteSdkInt")
    public boolean isGpsOpen() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } else {
            int state = Settings.Secure.getInt(mContext.getContentResolver(),
                    Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
            if (state == Settings.Secure.LOCATION_MODE_OFF) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * 获得屏幕亮度值
     *
     * @return 系统屏幕亮度值
     */
    public int getScreenBrightness() {
        ContentResolver contentResolver = mContext.getContentResolver();
        int defValue = 125;
        return Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, defValue);
    }

    /**
     * 获得屏百分比制幕亮度值
     *
     * @return 百分比值
     */
    public int getScreenBrightnessPercentageValue() {
        double value = (int) (getScreenBrightness() / baseValue);
        return (int) Math.floor(value);
    }

    /**
     * 将百分比值转成亮度值
     *
     * @return
     */
    public int percentageValueChangeToBrightness(int value) {
        return (int) (value * baseValue);
    }

    /**
     * 关闭光感，设置手动调节背光模式
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC 自动调节屏幕亮度模式值为1
     * SCREEN_BRIGHTNESS_MODE_MANUAL 手动调节屏幕亮度模式值为0
     **/
    private void setScreenManualMode() {
        ContentResolver contentResolver = mContext.getContentResolver();
        int mode;
        try {
            mode = Settings.System.getInt(contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "setScreenManualMode Exception: " + e.toString());
        }
    }

    /**
     * 系统值 0-255
     * 修改Setting 中屏幕亮度值
     * 修改Setting的值需要动态申请权限<uses-permission
     * android:name="android.permission.WRITE_SETTINGS"/>
     **/

    public void modifyScreenBrightness(int brightnessValue) {
        // 首先需要设置为手动调节屏幕亮度模式
        setScreenManualMode();
        ContentResolver contentResolver = mContext.getContentResolver();
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightnessValue);
    }

    /**
     * 根据seeBar progress 转换后屏幕亮度
     *
     * @param progress seekBar progress
     */
    public void progressChangeToBrightness(int progress) {
        int brightnessValue = (int) Math.ceil(progress * baseValue);
        Log.d(TAG, "progressChangeToBrightness: brightnessValue " + brightnessValue);
        try {
            modifyScreenBrightness(brightnessValue);
        } catch (Exception e) {
            Log.e(TAG, "progressChangeToBrightness Exception: " + e.getMessage());
        }
    }

    /**
     * 获取开启静音(音量设为0)的权限
     */
    private void getDoNotDisturb() {
        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(android.provider.Settings
                    .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            mContext.startActivity(intent);
        }
    }

    /**
     * 根据类型获取最大音量
     *
     * @param type 声音类型
     * @return 系统音量值
     */
    public int getMaxValue(int type) {
        return audioManager.getStreamMaxVolume(type);
    }

    /**
     * 获取系统当前音量
     *
     * @return 系统当前音量
     */
    public int getCurrentVolume() {
        return audioManager.getStreamVolume(STREAM_MUSIC);
    }

    public void setVolume(int volume) {
        audioManager.setStreamVolume(STREAM_MUSIC, volume,
                AudioManager.FLAG_PLAY_SOUND);
    }

    /**
     * 蓝牙是否开启
     *
     * @return true or false
     */
    public boolean isBlueToothEnable() {
        return bluetoothAdapter.isEnabled();
    }

    /**
     * 开启或关闭蓝牙
     *
     * @param isOpen true or false
     */
    public void openOrCloseBT(boolean isOpen) {
        if (isOpen) {
            if (!isBlueToothEnable()) {
                bluetoothAdapter.enable();
            }
//            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            mContext.startActivity(intent);
        } else {
            bluetoothAdapter.disable();
        }
    }


    private static Object getSystemService(Context context, String name) {
        return context.getSystemService(name);
    }

    public void openOrCloseFM(boolean isOpen) {
        if (isOpen) {
            Log.d(TAG, "openOrCloseFM: on");
            if (!getFMStatus()) {
                mFmManager.turnOn();
            }
        } else {
            Log.d(TAG, "openOrCloseFM: off");
            mFmManager.turnOff();
        }
    }

    public boolean getFMStatus() {
        int status = mFmManager.getState();
        if (status == 0) {
            return false;
        } else {
            return true;
        }
    }

    public int getFMState() {
        int status = mFmManager.getState();
        return status;
    }


    /**
     * 设置息屏或屏保时间
     * 管理器方式
     *
     * @param time 时间值
     */
    public void setTime(int time) {
        Settings.System.putInt(mContext.getContentResolver(),
                android.provider.Settings.System.SCREEN_OFF_TIMEOUT, time);
        Uri uri = Settings.System
                .getUriFor(Settings.System.SCREEN_OFF_TIMEOUT);
        mContext.getContentResolver().notifyChange(uri, null);
    }

    /**
     * 获取当前系统的息屏或屏保时间
     * 管理器方式
     *
     * @return 时间
     */
    public int getScreenOutTime() {
        try {
            int time = Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT) / 1000;
            Log.d(TAG, "instance initializer: " + time);
            if (time <= 60) {
                return 1;
            } else if (time <= 300) {
                return 2;
            } else if (time <= 1800) {
                return 3;
            } else {
                return 4;
            }
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "getScreenOutTime: error " + e.getMessage());
        }
        return 1;
    }

    /**
     * 移动网络开关
     */
    public void toggleMobileData(boolean enabled) {
        ConnectivityManager conMgr =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        Class<?> conMgrClass; // ConnectivityManager类
        Field iConMgrField; // ConnectivityManager类中的字段
        Object iConMgr; // IConnectivityManager类的引用
        Class<?> iConMgrClass; // IConnectivityManager类
        Method setMobileDataEnabledMethod; // setMobileDataEnabled方法
        try {
            // 取得ConnectivityManager类
            conMgrClass = Class.forName(conMgr.getClass().getName());
            // 取得ConnectivityManager类中的对象mService
            iConMgrField = conMgrClass.getDeclaredField("mService");
            // 设置mService可访问
            iConMgrField.setAccessible(true);
            // 取得mService的实例化类IConnectivityManager
            iConMgr = iConMgrField.get(conMgr);
            // 取得IConnectivityManager类
            iConMgrClass = Class.forName(iConMgr.getClass().getName());
            // 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
            setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled",
                    Boolean.TYPE);
            // 设置setMobileDataEnabled方法可访问
            setMobileDataEnabledMethod.setAccessible(true);
            // 调用setMobileDataEnabled方法
            setMobileDataEnabledMethod.invoke(iConMgr, enabled);
        } catch (ClassNotFoundException | NoSuchFieldException | SecurityException
                | NoSuchMethodException | IllegalArgumentException | IllegalAccessException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回手机移动数据的状态
     *
     * @param arg 默认填null
     * @return true 连接 false 未连接
     */
    public boolean getMobileDataState(Object[] arg) {
        try {
            ConnectivityManager mConnectivityManager =
                    (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            Class ownerClass = mConnectivityManager.getClass();
            Class[] argsClass = null;
            if (arg != null) {
                argsClass = new Class[1];
                argsClass[0] = arg.getClass();
            }
            Method method = ownerClass.getMethod("getMobileDataEnabled", argsClass);
            Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);
            return isOpen;
        } catch (Exception e) {
            return false;
        }
    }

    public int updateBrightness(int value) {
        int currentBrightness = getScreenBrightnessPercentageValue();
        final int maxValue = 100;
        currentBrightness += value;
        if (currentBrightness < 0) {
            currentBrightness = 0;
        }
        if (currentBrightness > maxValue) {
            currentBrightness = maxValue;
        }
        progressChangeToBrightness(currentBrightness);
        return currentBrightness;
    }

    public int updateVolume(int value) {
        final int maxValue = 15;
        int currentVolume = getCurrentVolume();

        currentVolume = currentVolume + value;
        if (currentVolume < 0) {
            currentVolume = 0;
        }
        if (currentVolume > maxValue) {
            currentVolume = maxValue;
        }
        setVolume(currentVolume);
        return currentVolume;
    }

    /**
     * wifi是否打开
     *
     * @return open or close
     */
    public boolean isWifiEnable() {
        boolean isEnable = false;
        if (mWifiManager != null) {
            Log.d("WifiTool", "isWifiEnable: " + mWifiManager.isWifiEnabled());
            if (mWifiManager.isWifiEnabled()) {
                isEnable = true;
            }
        }
        return isEnable;
    }

    public void setWifiStatue(int type) {
        if (type == 0) {
            closeWifi();
        } else {
            openWifi();
        }
    }

    /**
     * 打开WiFi
     */
    public void openWifi() {
        if (mWifiManager != null && !isWifiEnable()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭WiFi
     */
    public void closeWifi() {
        if (mWifiManager != null && isWifiEnable()) {
            mWifiManager.disconnect();
            mWifiManager.setWifiEnabled(false);
        }
    }

}
