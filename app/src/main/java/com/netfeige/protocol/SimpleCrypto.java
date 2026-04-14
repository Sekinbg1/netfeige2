package com.netfeige.protocol;

import android.util.Log;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/* JADX INFO: loaded from: classes.dex */
public class SimpleCrypto {
    private static final String HEX = "0123456789ABCDEF";
    private static byte[] _iv;
    private static byte[] _key;

    public static byte[] getKey() {
        if (_key == null) {
            _key = new byte[]{105, 110, 116, 87, 111, 114, 107, 57, 64, 35, 117, 77, 99, 97, 108, 108};
        }
        return _key;
    }

    public static void setKey(byte[] bArr) {
        _key = bArr;
    }

    public static void setIV(byte[] bArr) {
        _iv = bArr;
    }

    public static byte[] getIV() {
        if (_iv == null) {
            _iv = new byte[]{-1, -34, -128, 10, 6, 5, 8, -97, 11, 45, 87, 78, 34, 67, 93, 62};
        }
        return _iv;
    }

    public static byte[] encryptString(String str) throws Exception {
        return encrypt(str.getBytes());
    }

    public static String decryptToString(byte[] bArr) throws Exception {
        return new String(decrypt(bArr));
    }

    public static byte[] encrypt(byte[] bArr) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(getKey(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(1, secretKeySpec, new IvParameterSpec(getIV()));
        Log.i("mylog", "EN- cipher.blockSize:" + Integer.toString(cipher.getBlockSize()));
        Log.i("mylog", "EN- IV" + toHex(cipher.getIV()));
        return cipher.doFinal(bArr, 0, bArr.length);
    }

    public static byte[] decrypt(byte[] bArr) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(getKey(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(2, secretKeySpec, new IvParameterSpec(getIV()));
        Log.i("mylog", "DE- cipher.blockSize:" + Integer.toString(cipher.getBlockSize()));
        Log.i("mylog", "DE- IV" + toHex(cipher.getIV()));
        return cipher.doFinal(bArr, 0, bArr.length);
    }

    public static String toHex(String str) {
        return toHex(str.getBytes());
    }

    public static String fromHex(String str) {
        return new String(toByte(str));
    }

    public static byte[] toByte(String str) {
        int length = str.length() / 2;
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            bArr[i] = Integer.valueOf(str.substring(i2, i2 + 2), 16).byteValue();
        }
        return bArr;
    }

    public static String toHex(byte[] bArr) {
        if (bArr == null) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer(bArr.length * 2);
        for (byte b : bArr) {
            appendHex(stringBuffer, b);
        }
        return stringBuffer.toString();
    }

    private static void appendHex(StringBuffer stringBuffer, byte b) {
        stringBuffer.append(HEX.charAt((b >> 4) & 15));
        stringBuffer.append(HEX.charAt(b & dn.m));
    }
}

