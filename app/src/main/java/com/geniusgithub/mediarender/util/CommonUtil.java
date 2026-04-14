package com.geniusgithub.mediarender.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.view.WindowManager;
import android.widget.Toast;
import java.io.File;
import java.io.InputStream;

/* JADX INFO: loaded from: classes.dex */
public class CommonUtil {
    private static final CommonLog log = LogFactory.createLog();
    private static long m_lSysNetworkSpeedLastTs = 0;
    private static long m_lSystNetworkLastBytes = 0;
    private static float m_fSysNetowrkLastSpeed = 0.0f;

    public static class ViewSize {
        public int width = 0;
        public int height = 0;
    }

    public static boolean hasSDCard() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static String getRootFilePath(Context context) {
        if (hasSDCard()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
        }
        return context.getFilesDir().getPath() + File.separator;
    }

    public static boolean checkNetworkState(Context context) {
        NetworkInfo[] allNetworkInfo;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivityManager == null || (allNetworkInfo = connectivityManager.getAllNetworkInfo()) == null) {
            return false;
        }
        for (NetworkInfo networkInfo : allNetworkInfo) {
            if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        }
        return false;
    }

    public static String getLocalMacAddress(Context context) {
        String wifiMacAddress = getWifiMacAddress(context);
        if (wifiMacAddress != null && !wifiMacAddress.equals("00:00:00:00:00:00")) {
            return wifiMacAddress;
        }
        try {
            InputStream inputStream = new ProcessBuilder("busybox", "ifconfig").start().getInputStream();
            byte[] bArr = new byte[1024];
            StringBuffer stringBuffer = new StringBuffer();
            while (inputStream.read(bArr) > 0) {
                stringBuffer.append(new String(bArr));
            }
            int iIndexOf = stringBuffer.substring(0).indexOf("HWaddr ");
            return iIndexOf > 0 ? stringBuffer.substring(iIndexOf + 7).substring(0, 17) : "00:00:00:00:00:00";
        } catch (Exception e) {
            e.printStackTrace();
            return "00:00:00:00:00:00";
        }
    }

    public static String getWifiMacAddress(Context context) {
        return ((WifiManager) context.getSystemService("wifi")).getConnectionInfo().getMacAddress();
    }

    public static boolean openWifiBrocast(Context context) {
        WifiManager.MulticastLock multicastLockCreateMulticastLock = ((WifiManager) context.getSystemService("wifi")).createMulticastLock("MediaRender");
        if (multicastLockCreateMulticastLock == null) {
            return false;
        }
        multicastLockCreateMulticastLock.acquire();
        return true;
    }

    public static void setCurrentVolume(int i, Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService("audio");
        audioManager.setStreamVolume(3, (audioManager.getStreamMaxVolume(3) * i) / 100, 5);
        audioManager.setMode(-2);
    }

    public static void setVolumeMute(Context context) {
        ((AudioManager) context.getSystemService("audio")).setStreamMute(3, true);
    }

    public static void setVolumeUnmute(Context context) {
        ((AudioManager) context.getSystemService("audio")).setStreamMute(3, false);
    }

    public static void showToask(Context context, String str) {
        Toast.makeText(context, str, 0).show();
    }

    public static int getScreenWidth(Context context) {
        return ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getWidth();
    }

    public static int getScreenHeight(Context context) {
        return ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getHeight();
    }

    public static ViewSize getFitSize(Context context, MediaPlayer mediaPlayer) {
        double d;
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
        double d2 = videoWidth;
        Double.isNaN(d2);
        double d3 = videoHeight;
        Double.isNaN(d3);
        double d4 = (d2 * 1.0d) / d3;
        int screenWidth = getScreenWidth(context);
        int screenHeight = getScreenHeight(context);
        double d5 = screenWidth;
        Double.isNaN(d5);
        double d6 = d5 * 1.0d;
        double d7 = screenHeight;
        Double.isNaN(d7);
        if (d4 > d6 / d7) {
            Double.isNaN(d2);
            d = d6 / d2;
        } else {
            Double.isNaN(d7);
            Double.isNaN(d3);
            d = (d7 * 1.0d) / d3;
        }
        ViewSize viewSize = new ViewSize();
        Double.isNaN(d2);
        viewSize.width = (int) (d2 * d);
        Double.isNaN(d3);
        viewSize.height = (int) (d * d3);
        return viewSize;
    }

    public static boolean getWifiState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivityManager.getNetworkInfo(1).getState() != NetworkInfo.State.CONNECTED) {
            return false;
        }
        return NetworkInfo.State.CONNECTED != connectivityManager.getNetworkInfo(0).getState();
    }

    public static boolean getMobileState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivityManager.getNetworkInfo(1).getState() != NetworkInfo.State.CONNECTED) {
            return false;
        }
        return NetworkInfo.State.CONNECTED == connectivityManager.getNetworkInfo(0).getState();
    }

    public static float getSysNetworkDownloadSpeed() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        long totalRxBytes = TrafficStats.getTotalRxBytes();
        long j = jCurrentTimeMillis - m_lSysNetworkSpeedLastTs;
        long j2 = totalRxBytes - m_lSystNetworkLastBytes;
        if (j > 0) {
            m_fSysNetowrkLastSpeed = (j2 * 1.0f) / j;
        }
        m_lSysNetworkSpeedLastTs = jCurrentTimeMillis;
        m_lSystNetworkLastBytes = totalRxBytes;
        return m_fSysNetowrkLastSpeed;
    }
}

