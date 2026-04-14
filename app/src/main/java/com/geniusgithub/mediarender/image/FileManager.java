package com.geniusgithub.mediarender.image;

import android.content.Context;
import com.geniusgithub.mediarender.util.CommonUtil;
import com.netfeige.common.Public_MsgID;
import org.teleal.cling.model.ServiceReference;

/* JADX INFO: loaded from: classes.dex */
public class FileManager {
    public static String getSaveRootDir(Context context) {
        return CommonUtil.getRootFilePath(context) + "icons/";
    }

    public static String getSaveFullPath(String str, Context context) {
        return getSaveRootDir(context) + getFormatUri(str);
    }

    public static String getFormatUri(String str) {
        String strReplace = str.replace(ServiceReference.DELIMITER, "_").replace(Public_MsgID.PRO_SPACE, "").replace("?", "_").replace("%", "_");
        int length = strReplace.length();
        return length > 150 ? strReplace.substring(length - 150) : strReplace;
    }
}

