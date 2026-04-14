package com.netfeige.display.data;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.common.HostInformation;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ChoiceUserAdpter extends ArrayAdapter<HostInformation> {
    private HostInformation m_HostInformation;
    private IpmsgApplication m_ipmsgApp;

    public ChoiceUserAdpter(Context context, int i, List<HostInformation> list) {
        super(context, i, list);
        this.m_HostInformation = null;
        this.m_ipmsgApp = (IpmsgApplication) ((Activity) context).getApplication();
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        this.m_HostInformation = getItem(i);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.choiceuseritem, (ViewGroup) null);
            viewHolder = new ViewHolder();
            viewHolder.choiceUser = (CheckBox) view.findViewById(R.id.checkb_choiceuseritem);
            viewHolder.userName = (TextView) view.findViewById(R.id.textv_username);
            viewHolder.imageVHead = (ImageView) view.findViewById(R.id.imagev_head);
            viewHolder.ipTextView = (TextView) view.findViewById(R.id.textv_userip);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.choiceUser.setChecked(this.m_HostInformation.isChoiced);
        viewHolder.userName.setText(this.m_HostInformation.pszUserName);
        viewHolder.ipTextView.setText(this.m_HostInformation.IpAddr.netAddr.getHostAddress());
        if (this.m_HostInformation.headImage != null) {
            try {
                viewHolder.imageVHead.setImageResource(this.m_ipmsgApp.g_headMap.get(this.m_HostInformation.headImage).intValue());
            } catch (Exception unused) {
                viewHolder.imageVHead.setImageResource(R.drawable.head_01);
            }
        } else {
            viewHolder.imageVHead.setImageResource(R.drawable.head_01);
        }
        return view;
    }

    private class ViewHolder {
        CheckBox choiceUser;
        ImageView imageVHead;
        TextView ipTextView;
        TextView userName;

        private ViewHolder() {
        }
    }
}

