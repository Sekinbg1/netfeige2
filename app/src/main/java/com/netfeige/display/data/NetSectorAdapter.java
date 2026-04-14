package com.netfeige.display.data;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.display.ui.NetSectorActivity;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class NetSectorAdapter extends ArrayAdapter<String> {
    private IpmsgApplication m_myApp;

    public NetSectorAdapter(Context context, int i) {
        super(context, i);
        this.m_myApp = (IpmsgApplication) ((NetSectorActivity) context).getApplication();
    }

    public NetSectorAdapter(Context context, int i, int i2) {
        super(context, i, i2);
        this.m_myApp = (IpmsgApplication) ((NetSectorActivity) context).getApplication();
    }

    public NetSectorAdapter(Context context, int i, String[] strArr) {
        super(context, i, strArr);
        this.m_myApp = (IpmsgApplication) ((NetSectorActivity) context).getApplication();
    }

    public NetSectorAdapter(Context context, int i, List<String> list) {
        super(context, i, list);
        this.m_myApp = (IpmsgApplication) ((NetSectorActivity) context).getApplication();
    }

    public NetSectorAdapter(Context context, int i, int i2, String[] strArr) {
        super(context, i, i2, strArr);
        this.m_myApp = (IpmsgApplication) ((NetSectorActivity) context).getApplication();
    }

    public NetSectorAdapter(Context context, int i, int i2, List<String> list) {
        super(context, i, i2, list);
        this.m_myApp = (IpmsgApplication) ((NetSectorActivity) context).getApplication();
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = super.getView(i, view, viewGroup);
        }
        ((TextView) view.findViewById(R.id.name_text_netsectoritem)).setText(getItem(i));
        ImageView imageView = (ImageView) view.findViewById(R.id.delete_image_netsectoritem);
        imageView.setTag(getItem(i));
        imageView.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.data.NetSectorAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                NetSectorAdapter.this.m_myApp.g_listNetSectors.remove((String) view2.getTag());
                NetSectorAdapter.this.notifyDataSetChanged();
            }
        });
        return view;
    }
}

