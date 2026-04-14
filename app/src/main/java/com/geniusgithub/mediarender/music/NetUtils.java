package com.geniusgithub.mediarender.music;

import android.graphics.drawable.Drawable;
import com.geniusgithub.mediarender.util.CommonLog;
import com.geniusgithub.mediarender.util.LogFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/* JADX INFO: loaded from: classes.dex */
public class NetUtils {
    private static final CommonLog log = LogFactory.createLog();

    public static Drawable requestDrawableByUri(String str) {
        Drawable drawableFromUri = null;
        if (str == null || str.length() == 0) {
            return null;
        }
        for (int i = 0; i < 3 && (drawableFromUri = getDrawableFromUri(str)) == null; i++) {
        }
        return drawableFromUri;
    }

    public static Drawable getDrawableFromUri(String str) {
        if (str == null || str.length() < 1) {
            return null;
        }
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            httpURLConnection.setRequestMethod("GET");
            InputStream inputStream = httpURLConnection.getInputStream();
            if (httpURLConnection.getResponseCode() != 200) {
                log.e("getDrawableFromUri.getResponseCode() = " + httpURLConnection.getResponseCode() + "\nuri :" + str + "is invalid!!!");
                inputStream.close();
                return null;
            }
            Drawable drawableCreateFromStream = Drawable.createFromStream(inputStream, "src");
            inputStream.close();
            return drawableCreateFromStream;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

