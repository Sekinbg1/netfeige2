package com.netfeige.util;

import android.content.Context;
import android.os.Build;
import android.os.storage.StorageManager;
import com.netfeige.common.SDCardInfo;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class SDCardUtil {
    public static String SDCARD_EXTERNAL = "external";
    public static String SDCARD_INTERNAL = "internal";

    /* JADX WARN: Removed duplicated region for block: B:25:0x00ae  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.util.ArrayList<com.netfeige.common.SDCardInfo> getSDCardInfoBelow14() {
        /*
            Method dump skipped, instruction units count: 211
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.netfeige.util.SDCardUtil.getSDCardInfoBelow14():java.util.ArrayList");
    }

    public static ArrayList<SDCardInfo> getSDCardInfoAbove14(Context context) {
        String[] strArr;
        ArrayList<SDCardInfo> arrayList = new ArrayList<>();
        try {
            StorageManager storageManager = (StorageManager) context.getSystemService("storage");
            strArr = (String[]) storageManager.getClass().getMethod("getVolumePaths", new Class[0]).invoke(storageManager, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
            strArr = null;
        }
        if (strArr != null && strArr.length > 0) {
            for (int i = 0; i < strArr.length; i++) {
                String str = strArr[i];
                if (checkSDCardMount14(context, str)) {
                    SDCardInfo sDCardInfo = new SDCardInfo();
                    sDCardInfo.setLabel("sdcard" + i);
                    sDCardInfo.setMountPoint(str);
                    sDCardInfo.setMounted(checkSDCardMount14(context, str));
                    arrayList.add(sDCardInfo);
                }
            }
        }
        return arrayList;
    }

    public static ArrayList<SDCardInfo> getSDCardInfo(Context context) {
        if (Build.VERSION.SDK_INT < 14) {
            return getSDCardInfoBelow14();
        }
        return getSDCardInfoAbove14(context);
    }

    protected static boolean checkSDCardMount14(Context context, String str) {
        if (str == null) {
            return false;
        }
        StorageManager storageManager = (StorageManager) context.getSystemService("storage");
        try {
            return "mounted".equals((String) storageManager.getClass().getMethod("getVolumeState", String.class).invoke(storageManager, str));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

