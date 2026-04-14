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
public class WTAdapter extends ArrayAdapter<ScanResult> {
    private LayoutInflater m_inflater;

    public WTAdapter(Context context, int i, List<ScanResult> list) {
        super(context, i, list);
        this.m_inflater = LayoutInflater.from(context);
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x00d2 A[Catch: NullPointerException -> 0x00ee, TryCatch #0 {NullPointerException -> 0x00ee, blocks: (B:8:0x0079, B:10:0x007f, B:12:0x008b, B:14:0x009e, B:15:0x00d2, B:17:0x00e2, B:18:0x00e8), top: B:24:0x0079 }] */
    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public android.view.View getView(int r6, android.view.View r7, android.view.ViewGroup r8) {
        /*
            Method dump skipped, instruction units count: 243
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.netfeige.display.data.WTAdapter.getView(int, android.view.View, android.view.ViewGroup):android.view.View");
    }

    public final class ViewHolder {
        public LinearLayout linearLConnectOk;
        public ProgressBar progressBConnecting;
        public TextView textConnect;
        public TextView textVName;

        public ViewHolder() {
        }
    }
}

