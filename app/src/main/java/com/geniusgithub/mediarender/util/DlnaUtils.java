package com.geniusgithub.mediarender.util;

import android.content.Context;
import com.geniusgithub.mediarender.center.DlnaMediaModel;
import com.geniusgithub.mediarender.datastore.LocalConfigSharePreference;
import com.geniusgithub.mediarender.jni.PlatinumReflection;
import com.netfeige.common.Public_MsgID;
import com.netfeige.dlna.ContentTree;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public class DlnaUtils {
    public static final String DLNA_OBJECTCLASS_MUSICID = "object.item.audioItem";
    public static final String DLNA_OBJECTCLASS_PHOTOID = "object.item.imageItem";
    public static final String DLNA_OBJECTCLASS_VIDEOID = "object.item.videoItem";
    private static final CommonLog log = LogFactory.createLog();

    public static boolean setDevName(Context context, String str) {
        return LocalConfigSharePreference.commintDevName(context, str);
    }

    public static String getDevName(Context context) {
        return LocalConfigSharePreference.getDevName(context);
    }

    public static String creat12BitUUID(Context context) {
        String strReplace = CommonUtil.getLocalMacAddress(context).replace(Public_MsgID.PRO_SPACE, "").replace(".", "");
        return strReplace.length() != 12 ? "123456789abc" : strReplace;
    }

    public static int parseSeekTime(String str) throws Exception {
        String[] strArrSplit = str.split("=");
        if (2 != strArrSplit.length) {
            return 0;
        }
        String str2 = strArrSplit[0];
        String str3 = strArrSplit[1];
        if (PlatinumReflection.MEDIA_SEEK_TIME_TYPE_REL_TIME.equals(str2)) {
            return convertSeekRelTimeToMs(str3);
        }
        log.e("timetype = " + str2 + ", position = " + str3);
        return 0;
    }

    public static int convertSeekRelTimeToMs(String str) {
        int i;
        String[] strArrSplit = str.split(Public_MsgID.PRO_SPACE);
        int i2 = 0;
        if (3 != strArrSplit.length || !isNumeric(strArrSplit[0])) {
            return 0;
        }
        int i3 = Integer.parseInt(strArrSplit[0]);
        if (!isNumeric(strArrSplit[1])) {
            return 0;
        }
        int i4 = Integer.parseInt(strArrSplit[1]);
        String[] strArrSplit2 = strArrSplit[2].split("\\.");
        if (2 == strArrSplit2.length) {
            if (!isNumeric(strArrSplit2[0])) {
                return 0;
            }
            int i5 = Integer.parseInt(strArrSplit2[0]);
            if (!isNumeric(strArrSplit2[1])) {
                return 0;
            }
            i = Integer.parseInt(strArrSplit2[1]);
            i2 = i5;
        } else {
            if (1 == strArrSplit2.length) {
                if (!isNumeric(strArrSplit2[0])) {
                    return 0;
                }
                i2 = Integer.parseInt(strArrSplit2[0]);
            }
            i = 0;
        }
        return (i3 * 3600000) + (i4 * 60000) + (i2 * 1000) + i;
    }

    public static boolean isNumeric(String str) {
        return !"".equals(str) && Pattern.compile("[0-9]*").matcher(str).matches();
    }

    public static String formatTimeFromMSInt(int i) {
        String hunToStr;
        String hunToStr2;
        if (i >= 3600000) {
            int i2 = i / 3600000;
            hunToStr = formatHunToStr(i2);
            i -= i2 * 3600000;
        } else {
            hunToStr = "00";
        }
        if (i >= 60000) {
            int i3 = i / 60000;
            hunToStr2 = formatHunToStr(i3);
            i -= i3 * 60000;
        } else {
            hunToStr2 = "00";
        }
        return hunToStr + Public_MsgID.PRO_SPACE + hunToStr2 + Public_MsgID.PRO_SPACE + (i >= 1000 ? formatHunToStr(i / 1000) : "00");
    }

    private static String formatHunToStr(int i) {
        int i2 = i % 100;
        if (i2 > 9) {
            return "" + i2;
        }
        return ContentTree.ROOT_ID + i2;
    }

    public static String formateTime(long j) {
        int i = (int) (j / 1000);
        int i2 = i % 60;
        int i3 = i / 60;
        return i3 >= 60 ? String.format("%02d:%02d:%02d", Integer.valueOf(i3 / 60), Integer.valueOf(i3 % 60), Integer.valueOf(i2)) : String.format("%02d:%02d", Integer.valueOf(i3), Integer.valueOf(i2));
    }

    public static boolean isAudioItem(DlnaMediaModel dlnaMediaModel) {
        return dlnaMediaModel.getObjectClass().contains(DLNA_OBJECTCLASS_MUSICID);
    }

    public static boolean isVideoItem(DlnaMediaModel dlnaMediaModel) {
        return dlnaMediaModel.getObjectClass().contains(DLNA_OBJECTCLASS_VIDEOID);
    }

    public static boolean isImageItem(DlnaMediaModel dlnaMediaModel) {
        return dlnaMediaModel.getObjectClass().contains(DLNA_OBJECTCLASS_PHOTOID);
    }

    public static boolean isImageFile(String str) {
        try {
            String lowerCase = str.toLowerCase();
            if (!lowerCase.endsWith(".jpeg") && !lowerCase.endsWith(".JPEG") && !lowerCase.endsWith(".jpg") && !lowerCase.endsWith(".JPG") && !lowerCase.endsWith(".png") && !lowerCase.endsWith(".PNG") && !lowerCase.endsWith(".bmp") && !lowerCase.endsWith(".BMP") && !lowerCase.endsWith(".gif")) {
                if (!lowerCase.endsWith(".GIF")) {
                    return false;
                }
            }
            return true;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public static boolean isAudioFile(String str) {
        try {
            String lowerCase = str.toLowerCase();
            if (!lowerCase.endsWith(".mp3") && !lowerCase.endsWith(".wma") && !lowerCase.endsWith(".MP3")) {
                if (!lowerCase.endsWith(".WMA")) {
                    return false;
                }
            }
            return true;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public static boolean isVideoFile(String str) {
        try {
            String lowerCase = str.toLowerCase();
            if (!lowerCase.endsWith(".mp4") && !lowerCase.endsWith(".rmvb") && !lowerCase.endsWith(".3gp") && !lowerCase.endsWith(".avi") && !lowerCase.endsWith(".wmv") && !lowerCase.endsWith(".MP4") && !lowerCase.endsWith(".RMVB") && !lowerCase.endsWith(".3GP") && !lowerCase.endsWith(".AVI")) {
                if (!lowerCase.endsWith(".WMV")) {
                    return false;
                }
            }
            return true;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }
}

