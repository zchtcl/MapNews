package com.sina.mapnews.util;

import java.util.List;
import java.util.UUID;

import com.sina.mapnews.App;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

/**
 * 系统接口的Facade类
 * 
 * @Description
 * @author snow
 * @version Created: 2014年11月26日 下午9:37:49
 */
public class SystemFacade {
    /**
     * 获取当前进程的进程名。
     * 
     * @return
     *
     * @author snow
     * @version Created: 2014年11月26日 下午9:54:09
     */
    private static String getCurProcessName() {
        ActivityManager am = (ActivityManager) App.getSysService(Context.ACTIVITY_SERVICE);
        if (null == am) {
            return null;
        }

        List<RunningAppProcessInfo> apps = am.getRunningAppProcesses();
        if (null == apps || apps.size() == 0) {
            return null;
        }

        int pid = Process.myPid();
        for (RunningAppProcessInfo info : apps) {
            if (null != info && info.pid == pid) {
                return info.processName;
            }
        }

        return null;
    }

    /**
     * 判断当前进程是否为Application主进程。
     * 
     * @return
     *
     * @author snow
     * @version Created: 2014年11月26日 下午9:55:56
     */
    public static boolean isMainProcess() {
        return App.getAppPackageName().equals(getCurProcessName());
    }

    private static NetworkInfo getNetworkInfo() {
        ConnectivityManager cm = (ConnectivityManager) App
                .getSysService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * Check whether network is available
     * 
     * @return
     *
     * @author snow
     * @version Created: 2014年11月27日 下午9:07:07
     */
    public static boolean isNetworkAvailable() {
        NetworkInfo ni = getNetworkInfo();
        if (null == ni) {
            return false;
        }

        return ni.isAvailable() && ni.isConnectedOrConnecting();
    }

    /**
     * Check whether WiFi is connected
     * 
     * @return
     *
     * @author snow
     * @version Created: 2014年11月27日 下午9:06:52
     */
    public static boolean isWiFiAvailable() {
        NetworkInfo ni = getNetworkInfo();
        if (null == ni || !ni.isAvailable() || !ni.isConnectedOrConnecting()) {
            return false;
        }

        return ni.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * Check whether mobile is connected
     * 
     * @return
     *
     * @author snow
     * @version Created: 2014年11月27日 下午9:06:24
     */
    public static boolean isMobileAvailable() {
        NetworkInfo ni = getNetworkInfo();
        if (null == ni || !ni.isAvailable() || !ni.isConnectedOrConnecting()) {
            return false;
        }

        return ni.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    /**
     * Check whether airplone mode is on
     * 
     * @return
     *
     * @author xuegang
     * @version Created: 2015年1月12日 下午5:11:53
     */
    @SuppressWarnings("deprecation")
    public static boolean isAirPlaneModeOn() {
        return Settings.System.getInt(App.getCtx().getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) != 0;
    }

    private static String DEVICE_ID = null;

    /**
     * Get the unique device ID, the IMEI for GSM and the MEID or ESN for CDMA
     * <p>
     * Empty String on error
     * </p>
     * 
     * @return
     *
     * @author snow
     * @version Created: 2014年11月27日 下午9:24:02
     */
    public static String getDeviceId() {
        if (null == DEVICE_ID) {
            TelephonyManager tm = (TelephonyManager) App.getSysService(Context.TELEPHONY_SERVICE);
            DEVICE_ID = tm.getDeviceId();
            if (null == DEVICE_ID) {
                DEVICE_ID = "";
            }
        }

        return DEVICE_ID;
    }

    private static String SIM_SERIAL_NUMBER = null;

    /**
     * Get the Serial number of Sim card
     * <p>
     * Empty String on error
     * </p>
     * 
     * @return
     *
     * @author xuegang
     * @version Created: 2015年1月12日 下午4:53:35
     */
    public static String getSimSerialNumber() {
        if (null == SIM_SERIAL_NUMBER) {
            TelephonyManager tm = (TelephonyManager) App.getSysService(Context.TELEPHONY_SERVICE);
            SIM_SERIAL_NUMBER = tm.getSimSerialNumber();
            if (null == SIM_SERIAL_NUMBER) {
                SIM_SERIAL_NUMBER = "";
            }
        }

        return SIM_SERIAL_NUMBER;
    }

    private static String ANDROID_ID = null;

    /**
     * Get android id
     * <p>
     * Empty String on error
     * </p>
     * 
     * @return
     *
     * @author xuegang
     * @version Created: 2015年1月12日 下午5:05:06
     */
    public static String getAndroidId() {
        if (null == ANDROID_ID) {
            ANDROID_ID = Settings.Secure.getString(App.getCtx().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            if (null == ANDROID_ID) {
                ANDROID_ID = "";
            }
        }

        return ANDROID_ID;
    }

    private static String MAC_ADDRESS = null;

    /**
     * Get Mac address
     * <p>
     * Empty String on error
     * </p>
     * 
     * @return
     *
     * @author snow
     * @version Created: 2014年11月27日 下午9:25:23
     */
    public static String getMacAddress() {
        if (null == MAC_ADDRESS) {
            WifiManager wm = (WifiManager) App.getSysService(Context.WIFI_SERVICE);
            WifiInfo wi = wm.getConnectionInfo();
            MAC_ADDRESS = null == wi ? "" : wi.getMacAddress();
            if (null == MAC_ADDRESS) {
                MAC_ADDRESS = "";
            }
        }

        return MAC_ADDRESS;
    }

    private static DisplayMetrics DISPLAY_METRICS = null;

    /**
     * Get DisplayMetrics of this screen
     * 
     * @return
     *
     * @author xuegang
     * @version Created: 2015年3月12日 下午2:02:33
     */
    public static DisplayMetrics getDisplayMetrics() {
        if (null == DISPLAY_METRICS) {
            DISPLAY_METRICS = App.getDisplayMetrics();
        }

        return DISPLAY_METRICS;
    }

    /**
     * Get width of screen in pixel
     * 
     * @return
     *
     * @author snow
     * @version Created: 2014年11月27日 下午9:32:54
     */
    public static int getScreenWidth() {
        if (null == DISPLAY_METRICS) {
            DISPLAY_METRICS = App.getDisplayMetrics();
        }

        return DISPLAY_METRICS.widthPixels;
    }

    /**
     * Get height of screen in pixel
     * 
     * @return
     *
     * @author snow
     * @version Created: 2014年11月27日 下午9:33:06
     */
    public static int getScreenHeight() {
        if (null == DISPLAY_METRICS) {
            DISPLAY_METRICS = App.getDisplayMetrics();
        }

        return DISPLAY_METRICS.heightPixels;
    }

    /**
     * Get density of screen
     * 
     * @return
     *
     * @author snow
     * @version Created: 2014年11月27日 下午9:33:30
     */
    public static float getScreenDensity() {
        if (null == DISPLAY_METRICS) {
            DISPLAY_METRICS = App.getDisplayMetrics();
        }

        return DISPLAY_METRICS.density;
    }

    /**
     * Get densityDpi of screen
     * 
     * @return
     *
     * @author snow
     * @version Created: 2014年11月27日 下午9:33:45
     */
    public static int getScreenDensityDpi() {
        if (null == DISPLAY_METRICS) {
            DISPLAY_METRICS = App.getDisplayMetrics();
        }

        return DISPLAY_METRICS.densityDpi;
    }

    /**
     * Get the available size of data directory of this application
     * 
     * @return
     *
     * @author xuegang
     * @version Created: 2014年12月4日 上午10:52:47
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static long getAvailDataSize() {
        ApplicationInfo info = App.getCtx().getApplicationInfo();
        if (null == info) {
            return 0L;
        }

        StatFs stat = new StatFs(info.dataDir);
        long blockSize = 0L;
        long availBlocks = 0L;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            availBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            availBlocks = stat.getAvailableBlocks();
        }
        return availBlocks * blockSize;
    }

    /**
     * Check whether external storage is available
     * 
     * @return
     *
     * @author xuegang
     * @version Created: 2014年12月18日 下午5:07:01
     */
    public static boolean isExternalStorageAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * Convert dip value to pixel
     * 
     * @param dp
     * @return
     *
     * @author xuegang
     * @version Created: 2015年1月12日 下午4:40:27
     */
    public static int dip2px(float dp) {
        if (null == DISPLAY_METRICS) {
            DISPLAY_METRICS = App.getDisplayMetrics();
        }

        return (int) (dp * DISPLAY_METRICS.density + 0.5);
    }

    /**
     * Convert pixel value to dip
     * 
     * @param px
     * @return
     *
     * @author xuegang
     * @version Created: 2015年1月12日 下午4:43:43
     */
    public static int px2dip(float px) {
        if (null == DISPLAY_METRICS) {
            DISPLAY_METRICS = App.getDisplayMetrics();
        }

        return (int) (px / DISPLAY_METRICS.density + 0.5);
    }

    /**
     * Convert sp value to pixel
     * 
     * @param sp
     * @return
     *
     * @author xuegang
     * @version Created: 2015年1月12日 下午4:45:29
     */
    public static int sp2px(float sp) {
        if (null == DISPLAY_METRICS) {
            DISPLAY_METRICS = App.getDisplayMetrics();
        }

        return (int) (sp * DISPLAY_METRICS.scaledDensity + 0.5);
    }

    /**
     * Convert px value to sp
     * 
     * @param px
     * @return
     *
     * @author xuegang
     * @version Created: 2015年1月12日 下午4:45:48
     */
    public static int px2sp(float px) {
        if (null == DISPLAY_METRICS) {
            DISPLAY_METRICS = App.getDisplayMetrics();
        }

        return (int) (px / DISPLAY_METRICS.scaledDensity + 0.5);
    }

    /**
     * Generate a UUID based on the system information
     * 
     * @return
     *
     * @author xuegang
     * @version Created: 2015年1月12日 下午5:02:20
     */
    public static String generateUUID() {
        String androidId = getAndroidId();
        String deviceId = getDeviceId();
        String serialNumber = getSimSerialNumber();

        long mostSigBits = androidId.hashCode();
        long leastSigBits = deviceId.hashCode() << 32 | serialNumber.hashCode();

        return new UUID(mostSigBits, leastSigBits).toString();
    }

    /**
     * Check current thread is Ui thread or not
     * 
     * @return
     *
     * @author snow
     * @version Created: 2015年3月14日 下午5:58:50
     */
    public static boolean isUiThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    /**
     * Print the stack trace from the point where this method has been called.
     * 
     * @param detailMessage
     *
     * @author snow
     * @version Created: 2015年3月14日 下午6:00:37
     */
    public static void printStackTrace(String detailMessage) {
        Throwable t = new Throwable(detailMessage);
        t.fillInStackTrace();
        t.printStackTrace();
    }

    /**
     * 检查App是否具有指定权限
     * 
     * @param permission
     * @return
     *
     * @author xuegang
     * @version Created: 2015年3月30日 下午2:27:35
     */
    public static boolean checkPermission(String permission) {
        PackageManager pm = App.getCtx().getPackageManager();
        return PackageManager.PERMISSION_GRANTED == pm.checkPermission(permission,
                App.getAppPackageName());
    }
}
