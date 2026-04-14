package com.netfeige.display.data;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class WiFiAdpter extends ArrayAdapter<ScanResult> {
    private LayoutInflater m_inflater;

    public WiFiAdpter(Context context, int i, List<ScanResult> list) {
        super(context, i, list);
        this.m_inflater = LayoutInflater.from(context);
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x00e9 A[Catch: NullPointerException -> 0x0107, TryCatch #0 {NullPointerException -> 0x0107, blocks: (B:8:0x007b, B:10:0x0081, B:12:0x009f, B:14:0x00b4, B:15:0x00e9, B:17:0x00fb, B:18:0x0101), top: B:24:0x007b }] */
    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public android.view.View getView(int r8, android.view.View r9, android.view.ViewGroup r10) {
        /*
            Method dump skipped, instruction units count: 268
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.netfeige.display.data.WiFiAdpter.getView(int, android.view.View, android.view.ViewGroup):android.view.View");
    }

    public final class WiFiViewHolder {
        public LinearLayout linearLConnectOk;
        public ProgressBar progressBConnecting;
        public TextView textConnect;
        public TextView textVName;

        public WiFiViewHolder() {
        }
    }
}

