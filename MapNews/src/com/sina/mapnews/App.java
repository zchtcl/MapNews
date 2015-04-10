package com.sina.mapnews;

import com.baidu.mapapi.SDKInitializer;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Process;
import android.util.DisplayMetrics;

/**
 * 
 *
 * @author xuegang
 * @version Created: 2015年4月10日 上午10:27:37
 */
public class App extends Application {

    private static Context _ctx;
    private static String _packageName;
    private static int _versionCode;
    private static String _versionName;
    /** Global handler of main thread */
    private static Handler _handler;

    @Override
    public void onCreate() {
        super.onCreate();
        initGlobalConstant();
        SDKInitializer.initialize(this);
    }

    private void initGlobalConstant() {
        _ctx = getApplicationContext();
        _packageName = _ctx.getApplicationInfo().packageName;

        PackageManager pm = getPackageManager();
        try {
            _versionCode = pm.getPackageInfo(_packageName, 0).versionCode;
            _versionName = pm.getPackageInfo(_packageName, 0).versionName;
        } catch (NameNotFoundException e) {
            _versionName = "";
            e.printStackTrace();
        }

        _handler = new Handler();
    }

    public static Context getCtx() {
        return _ctx;
    }

    public static Resources getRes() {
        return _ctx.getResources();
    }

    public static DisplayMetrics getDisplayMetrics() {
        return _ctx.getResources().getDisplayMetrics();
    }

    public static Object getSysService(String name) {
        return _ctx.getSystemService(name);
    }

    public static String getAppPackageName() {
        return _packageName;
    }

    public static int getVersionCode() {
        return _versionCode;
    }

    public static String getVersionName() {
        return _versionName;
    }

    public static Handler getHandler() {
        return _handler;
    }

    public static void exit() {
        Process.killProcess(Process.myPid());
    }
}
