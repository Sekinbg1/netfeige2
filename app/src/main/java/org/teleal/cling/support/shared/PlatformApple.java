package org.teleal.cling.support.shared;

import android.os.Build;
import android.util.Log;
import org.teleal.common.androidfwk.Controller;

public class PlatformApple {
    private static final String TAG = "PlatformApple";

    public static void setup(Controller controller, String appName) {
        if (Build.MANUFACTURER.equalsIgnoreCase("Apple")) {
            Log.d(TAG, "Running on Apple device");
            setupAppleSpecific(appName);
        }
    }
    
    private static void setupAppleSpecific(String appName) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", appName);
    }
}

