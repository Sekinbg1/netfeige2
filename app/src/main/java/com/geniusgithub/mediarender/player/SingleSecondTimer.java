package com.geniusgithub.mediarender.player;

import android.content.Context;

/* JADX INFO: loaded from: classes.dex */
public class SingleSecondTimer extends AbstractTimer {
    public SingleSecondTimer(Context context) {
        super(context);
        setTimeInterval(1000);
    }
}

