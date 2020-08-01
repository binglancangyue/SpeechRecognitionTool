package com.bixin.speechrecognitiontool.txz;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.bixin.speechrecognitiontool.R;
import com.bixin.speechrecognitiontool.SpeechApplication;
import com.bixin.speechrecognitiontool.mode.CustomValue;
import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZNetDataProvider;
import com.txznet.sdk.TXZTtsManager;
import com.txznet.sdk.bean.WeatherData;

public class TXZVoiceControl {
    private static final String ACTION_TXZ_CUSTOM_COMMAND = "com.bixin.txz.command";

    public TXZVoiceControl() {
        customRegistration();
        customCommandListener();
    }

    /**
     * 注册唤醒状态语音命令
     *
     * @param commands 命令
     * @param data     命令标识
     */
    private void registerCommand(String[] commands, String data) {
        TXZAsrManager.getInstance().regCommand(commands, data);
    }

    /**
     * 定制同行者语音命令
     */
    private void customRegistration() {
        //Open
        registerCommand(new String[]{"打开应用管理", "打开应用管理器", "打开应用列表"},
                "CMD_OPEN_APP_MGT");
        registerCommand(new String[]{"打开文件管理", "打开文件管理器"}, "CMD_OPEN_FILE_MGT");
        registerCommand(new String[]{"打开记录仪", "打开行车记录仪"}, "CMD_OPEN_CAMERA_MGT");
        registerCommand(new String[]{"打开视频回放", "打开视频"}, "CMD_OPEN_V_PLAYBACK");
//       registerCommand(new String[]{"打开蓝牙"}, "CMD_OPEN_V_Bluetooth");
        registerCommand(new String[]{"打开电视家", "打开电视机"}, "CMD_OPEN_TV_HOME");
//        registerCommand(new String[]{"打开FM"}, "CMD_OPEN_FM");
        registerCommand(new String[]{"打开喜马拉雅"}, "CMD_OPEN_XIMALAYA");


        //Close
        registerCommand(new String[]{"关闭应用管理", "关闭应用管理器", "退出应用管理", "退出应用管理器"},
                "CMD_CLOSE_APP_MGT");
        registerCommand(new String[]{"退出电视家", "关闭电视家", "退出电视机", "关闭电视机"},
                "CMD_CLOSE_TV_HOME");
    }

    private void customCommandListener() {
        TXZAsrManager.getInstance().addCommandListener(new TXZAsrManager.CommandListener() {
            @Override
            public void onCommand(String cmd, String data) {
//                sendBroadcastToLauncher(data);
                startApp(data);
            }
        });
    }

    private void toSpeakText(String s) {
        TXZTtsManager.getInstance().speakText(s);
    }

    private void startApp(String data) {
        switch (data) {
            case "CMD_OPEN_APP_MGT":
//                openApp(null);
                startActivity();
                toSpeakText("应用管理已打开");
                break;
            case "CMD_OPEN_FILE_MGT":
                openApp(CustomValue.PACKAGE_NAME_FILE_MANAGER);
                toSpeakText("文件管理已打开");
                break;
            case "CMD_OPEN_CAMERA_MGT":
                openApp(CustomValue.PACKAGE_NAME_DVR);
                toSpeakText("记录仪已打开");
                break;
            case "CMD_OPEN_V_PLAYBACK":
                openApp(CustomValue.PACKAGE_NAME_ViDEO_PLAY_BACK);
                toSpeakText("视频回放已打开");
                break;
//                    case "CMD_OPEN_V_BLUETOOTH":
//                        openApp(CustomValue.PACKAGE_NAME_BLUETOOTH);
//                        toSpeakText("蓝牙已打开");
//                        break;
            case "CMD_OPEN_TV_HOME":
                openApp(CustomValue.PACKAGE_NAME_TV_HOME);
                break;
            case "CMD_OPEN_FM":
                openApp(CustomValue.PACKAGE_NAME_FM);
                break;
            case "CMD_OPEN_XIMALAYA":
                openApp(CustomValue.PACKAGE_NAME_XIMALAYA);
                break;
            case "CMD_CLOSE_TV_HOME":
                closeApp(CustomValue.PACKAGE_NAME_TV_HOME);
                break;
            default:
                break;
        }
    }



    private void sendBroadcastToLauncher(String command) {
        Intent intent = new Intent();
        intent.setAction(ACTION_TXZ_CUSTOM_COMMAND);
        intent.putExtra("key_type", command);
        SpeechApplication.getInstance().sendBroadcast(intent);
    }

    private void openApp(String packageName) {
        launchAppByPackageName(packageName);
    }

    private void closeApp(String packageName) {

    }

    /**
     * 根据包名启动应用
     *
     * @param packageName clicked app
     */
    private void launchAppByPackageName(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            Log.i("TAG", "package name is null!");
            return;
        }
        Context context = SpeechApplication.getInstance();
        Intent launchIntent =
                context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent == null) {
            ToastTool.showToast(R.string.app_not_install);
        } else {
            context.startActivity(launchIntent);
        }
    }

    private void startActivity() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(CustomValue.PACKAGE_NAME_LAUNCHER,
                CustomValue.PACKAGE_NAME_ACTIVITY));
        SpeechApplication.getInstance().startActivity(intent);
    }
}