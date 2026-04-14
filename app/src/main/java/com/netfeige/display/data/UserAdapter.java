package com.netfeige.display.data;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.common.HostInformation;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class UserAdapter extends ArrayAdapter<HostInformation> {
    private HostInformation m_HostInformation;
    private IpmsgApplication myApp;

    public UserAdapter(Context context, int i, List<HostInformation> list) {
        super(context, i, list);
        this.m_HostInformation = null;
        this.myApp = (IpmsgApplication) ((Activity) context).getApplication();
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        HostInformation item;
        ViewHolder viewHolder;
        int i2;
        try {
            item = getItem(i);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            item = null;
        } catch (Exception e2) {
            e2.printStackTrace();
            item = null;
        }
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.useritem, (ViewGroup) null);
            viewHolder = new ViewHolder();
            viewHolder.containerLinearL = (LinearLayout) view.findViewById(R.id.container_userItem);
            viewHolder.titleTextView = (TextView) view.findViewById(R.id.title_textView_userItem);
            viewHolder.ipTextView = (TextView) view.findViewById(R.id.ip_textView_userItem);
            viewHolder.headerImage = (ImageView) view.findViewById(R.id.header_image);
            viewHolder.unreadMsgBtn = (ImageView) view.findViewById(R.id.unread_msg_userItem);
            viewHolder.filePromptBtn = (TextView) view.findViewById(R.id.file_prompt_userItem);
            viewHolder.frameLUnRead = (FrameLayout) view.findViewById(R.id.framel_unread);
            viewHolder.dividerTextView = (TextView) view.findViewById(R.id.divider_text_userItem);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            HostInformation hostInformation = this.m_HostInformation;
            if (hostInformation == null || hostInformation != item) {
                view.setBackgroundColor(0);
            } else {
                view.setBackgroundColor(Color.rgb(240, 159, 23));
            }
        }
        if (item.headImage != null) {
            try {
                viewHolder.headerImage.setImageResource(this.myApp.g_headMap.get(item.headImage).intValue());
            } catch (Exception unused) {
                viewHolder.headerImage.setImageResource(R.drawable.head_01);
            }
        } else {
            viewHolder.headerImage.setImageResource(R.drawable.head_01);
        }
        if (this.myApp.g_iUserColumn == 4) {
            viewHolder.containerLinearL.setOrientation(1);
            viewHolder.containerLinearL.setGravity(17);
            viewHolder.headerImage.setPadding(0, 0, 0, 0);
            viewHolder.unreadMsgBtn.setPadding(0, 0, 0, 0);
            viewHolder.frameLUnRead.setPadding(0, 0, 0, 0);
            viewHolder.ipTextView.setVisibility(8);
            viewHolder.dividerTextView.setVisibility(8);
        } else {
            viewHolder.containerLinearL.setOrientation(0);
            viewHolder.containerLinearL.setGravity(19);
            viewHolder.headerImage.setPadding(this.myApp.g_iUserItemLeft, 0, 10, 4);
            viewHolder.unreadMsgBtn.setPadding(0, 0, 10, 4);
            viewHolder.frameLUnRead.setPadding(0, 0, 10, 4);
            viewHolder.ipTextView.setVisibility(0);
            viewHolder.dividerTextView.setVisibility(0);
        }
        viewHolder.titleTextView.setText(item.pszUserName);
        viewHolder.ipTextView.setText(item.IpAddr.netAddr.getHostAddress());
        if (item.unreadMsg) {
            viewHolder.unreadMsgBtn.setVisibility(0);
        } else {
            viewHolder.unreadMsgBtn.setVisibility(8);
        }
        ArrayList<MsgRecord> arrayList = this.myApp.ipmsgService.fileMsgs.get(item.strMacAddr);
        if (arrayList != null) {
            i2 = 0;
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                if (arrayList.get(i3).getFileId() != -1) {
                    i2++;
                }
            }
        } else {
            i2 = 0;
        }
        if (i2 > 0) {
            viewHolder.frameLUnRead.setVisibility(0);
            viewHolder.filePromptBtn.setText(String.valueOf(i2));
        } else {
            viewHolder.frameLUnRead.setVisibility(8);
        }
        return view;
    }

    public class ViewHolder {
        LinearLayout containerLinearL;
        TextView dividerTextView;
        TextView filePromptBtn;
        FrameLayout frameLUnRead;
        ImageView headerImage;
        TextView ipTextView;
        TextView titleTextView;
        ImageView unreadMsgBtn;

        public ViewHolder() {
        }
    }

    public HostInformation getHostInformation() {
        return this.m_HostInformation;
    }

    public void setHostInformation(HostInformation hostInformation) {
        if (hostInformation == null) {
            this.m_HostInformation = null;
        } else {
            this.m_HostInformation = hostInformation;
        }
    }
}

