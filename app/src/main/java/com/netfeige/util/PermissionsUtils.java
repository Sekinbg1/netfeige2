package com.netfeige.util;

import android.app.Activity;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import com.netfeige.dlna.ContentTree;

/* JADX INFO: loaded from: classes.dex */
public class PermissionsUtils {
    public static final int REQUESTCODE_PHONE = 17;
    private static PermissionsUtils instance;
    private String[] arr = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.REQUEST_INSTALL_PACKAGES"};
    private boolean isNeed = false;

    private PermissionsUtils() {
    }

    public static PermissionsUtils getInstance() {
        if (instance == null) {
            synchronized (PermissionsUtils.class) {
                if (instance == null) {
                    instance = new PermissionsUtils();
                }
            }
        }
        return instance;
    }

    public boolean requestPermissions(Activity activity, int i) {
        if (Build.VERSION.SDK_INT >= 23) {
            for (String str : this.arr) {
                if (ActivityCompat.checkSelfPermission(activity, str) != 0) {
                    this.isNeed = true;
                }
            }
            if (this.isNeed) {
                ActivityCompat.requestPermissions(activity, this.arr, i);
                this.isNeed = false;
                return true;
            }
            Log.i("res", ContentTree.AUDIO_ID);
        }
        return false;
    }
}

