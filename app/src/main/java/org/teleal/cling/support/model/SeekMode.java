package org.teleal.cling.support.model;

import com.geniusgithub.mediarender.jni.PlatinumReflection;

/* JADX INFO: loaded from: classes.dex */
public enum SeekMode {
    TRACK_NR(PlatinumReflection.MEDIA_SEEK_TIME_TYPE_TRACK_NR),
    ABS_TIME("ABS_TIME"),
    REL_TIME(PlatinumReflection.MEDIA_SEEK_TIME_TYPE_REL_TIME),
    ABS_COUNT("ABS_COUNT"),
    REL_COUNT("REL_COUNT"),
    CHANNEL_FREQ("CHANNEL_FREQ"),
    TAPE_INDEX("TAPE-INDEX"),
    FRAME("FRAME");

    private String protocolString;

    SeekMode(String str) {
        this.protocolString = str;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.protocolString;
    }

    public static SeekMode valueOrExceptionOf(String str) throws IllegalArgumentException {
        for (SeekMode seekMode : values()) {
            if (seekMode.protocolString.equals(str)) {
                return seekMode;
            }
        }
        throw new IllegalArgumentException("Invalid seek mode string: " + str);
    }
}

