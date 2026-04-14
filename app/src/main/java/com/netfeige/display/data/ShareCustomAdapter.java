package com.netfeige.display.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.display.ui.dialog.SharePopupWindow;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ShareCustomAdapter extends BaseAdapter {
    private LayoutInflater m_inflater;
    private List<SharePopupWindow.AppInfo> m_shareAppInfos;
    private ViewHolder m_viewHolder = null;

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    public ShareCustomAdapter(Context context, List<SharePopupWindow.AppInfo> list) {
        this.m_inflater = LayoutInflater.from(context);
        this.m_shareAppInfos = list;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.m_shareAppInfos.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return this.m_shareAppInfos.get(i);
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        SharePopupWindow.AppInfo appInfo = this.m_shareAppInfos.get(i);
        if (view == null) {
            view = this.m_inflater.inflate(R.layout.popup_share_item, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder();
            this.m_viewHolder = viewHolder;
            viewHolder.shareName = (TextView) view.findViewById(R.id.share_item_name);
            this.m_viewHolder.shareIcon = (ImageView) view.findViewById(R.id.share_item_icon);
            view.setTag(this.m_viewHolder);
        } else {
            this.m_viewHolder = (ViewHolder) view.getTag();
        }
        this.m_viewHolder.shareIcon.setImageDrawable(appInfo.getAppIcon());
        if (appInfo.getAppName() == null) {
            this.m_viewHolder.shareName.setText(appInfo.getAppLauncherClassName());
        } else {
            this.m_viewHolder.shareName.setText(appInfo.getAppName());
        }
        return view;
    }

    class ViewHolder {
        ImageView shareIcon;
        TextView shareName;

        ViewHolder() {
        }
    }
}

