package com.lh.xiping;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private ComponentName adminReceiver;
    private PowerManager mPowerManager;
    private DevicePolicyManager policyManager;
    private PowerManager.WakeLock mWakeLock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adminReceiver = new ComponentName(MainActivity.this, ScreenOffAdminReceiver.class);
        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
        policyManager = (DevicePolicyManager) MainActivity.this.getSystemService(Context.DEVICE_POLICY_SERVICE);
        checkAndTurnOnDeviceManager(null);
    }


    @SuppressLint("InvalidWakeLockTag")
    private void starttest() {
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        mWakeLock.acquire();
        mWakeLock.release();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isOpen();
    }


    private void isOpen() {
        if (policyManager.isAdminActive(adminReceiver)) {//判断超级管理员是否激活
            showToast("设备已被激活");
        } else {
            showToast("设备没有被激活");

        }
    }


    /**
     * @param view 检测屏幕状态
     */
    public void checkScreen(View view) {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean screenOn = pm.isScreenOn();
        if (!screenOn) {//如果灭屏
            //相关操作
            showToast("屏幕是息屏");
        } else {
            showToast("屏幕是亮屏");

        }
    }


    /**
     * @param view 亮屏
     */
    @SuppressLint("InvalidWakeLockTag")
    public void checkScreenOn(View view) {
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
        mWakeLock.acquire();
        mWakeLock.release();
    }

    /**
     * @param view 熄屏
     */
    public void checkScreenOff(View view) {
        boolean admin = policyManager.isAdminActive(adminReceiver);
        if (admin) {
            policyManager.lockNow();
        } else {
            showToast("没有设备管理权限");
        }
    }

    /**
     * @param view 熄屏并延时亮屏
     */
    public void checkScreenOffAndDelayOn(View view) {
        boolean admin = policyManager.isAdminActive(adminReceiver);
        if (admin) {
            policyManager.lockNow();
//            handler.sendEmptyMessageDelayed(1, 3000);
        } else {
            showToast("没有设备管理权限");
        }
    }

    /**
     * @param view 检测并去激活设备管理器权限
     */
    public void checkAndTurnOnDeviceManager(View view) {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminReceiver);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "开启后就可以使用锁屏功能了...");//显示位置见图二
        startActivityForResult(intent, 0);

    }


    private void showToast(String Str) {
        Toast.makeText(this, Str, Toast.LENGTH_SHORT).show();
    }

}
