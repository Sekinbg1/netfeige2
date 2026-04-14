package com.netfeige.display.data;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.common.Global;
import com.netfeige.common.HostInformation;
import com.netfeige.display.ui.ChoicePrinterActivity;
import com.netfeige.service.IpmsgService;

/* JADX INFO: loaded from: classes.dex */
public class PrinterAdapter extends BaseAdapter {
    private ChoicePrinterActivity m_choicePrinterActivity;
    private LayoutInflater m_inflater;
    private IpmsgApplication m_ipmsgApp;
    private ViewHolder m_viewHolder = null;

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    public PrinterAdapter(Context context) {
        this.m_choicePrinterActivity = null;
        this.m_choicePrinterActivity = (ChoicePrinterActivity) context;
        this.m_inflater = LayoutInflater.from(context);
        this.m_ipmsgApp = (IpmsgApplication) ((Activity) context).getApplication();
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return IpmsgService.printerList.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return IpmsgService.printerList.get(i);
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        HostInformation hostInformation = IpmsgService.printerList.get(i);
        if (view == null) {
            view = this.m_inflater.inflate(R.layout.printitem, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder();
            this.m_viewHolder = viewHolder;
            viewHolder.userName = (TextView) view.findViewById(R.id.textview_name);
            this.m_viewHolder.printerName = (TextView) view.findViewById(R.id.textview_printername);
            this.m_viewHolder.full = (LinearLayout) view.findViewById(R.id.linearlfull);
            this.m_viewHolder.imageVHead = (ImageView) view.findViewById(R.id.imagev_head);
            view.setTag(this.m_viewHolder);
        } else {
            this.m_viewHolder = (ViewHolder) view.getTag();
        }
        if (hostInformation.pszUserName == null || "" == hostInformation.pszUserName) {
            this.m_viewHolder.userName.setText("Null");
        } else {
            this.m_viewHolder.userName.setText(hostInformation.pszUserName);
        }
        this.m_viewHolder.printerName.setText(hostInformation.strSharePrinter);
        this.m_viewHolder.full.setTag(i + "");
        this.m_viewHolder.full.setOnClickListener(new ClickListener());
        if (hostInformation.headImage != null) {
            try {
                this.m_viewHolder.imageVHead.setImageResource(this.m_ipmsgApp.g_headMap.get(hostInformation.headImage).intValue());
            } catch (Exception unused) {
                this.m_viewHolder.imageVHead.setImageResource(R.drawable.head_01);
            }
        } else {
            this.m_viewHolder.imageVHead.setImageResource(R.drawable.head_01);
        }
        return view;
    }

    class ViewHolder {
        LinearLayout full;
        ImageView imageVHead;
        TextView printerName;
        TextView userName;

        ViewHolder() {
        }
    }

    private class ClickListener implements View.OnClickListener {
        private ClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Global.g_hostInformation = IpmsgService.printerList.get(Integer.parseInt(view.getTag().toString()));
            PrinterAdapter.this.m_choicePrinterActivity.back();
        }
    }
}

