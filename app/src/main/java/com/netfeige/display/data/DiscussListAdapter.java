package com.netfeige.display.data;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.common.DiscussInfo;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class DiscussListAdapter extends ArrayAdapter<DiscussInfo> {
    private IpmsgApplication mMyApp;

    public DiscussListAdapter(Context context, int i, List<DiscussInfo> list) {
        super(context, i, list);
        this.mMyApp = (IpmsgApplication) ((Activity) context).getApplication();
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        int i2;
        DiscussInfo item = getItem(i);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.discusslistitem, (ViewGroup) null);
            viewHolder = new ViewHolder();
            viewHolder.imageVHead = (ImageView) view.findViewById(R.id.head_img_discusslistitem);
            viewHolder.fLayoutUnRead = (FrameLayout) view.findViewById(R.id.unread_flayout_discusslistitem);
            viewHolder.textFilePrompt = (TextView) view.findViewById(R.id.file_prompt_discusslistitem);
            viewHolder.unreadMsgImg = (ImageView) view.findViewById(R.id.unread_msg_img_discusslistitem);
            viewHolder.discussName = (TextView) view.findViewById(R.id.name_text_discusslistitem);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.discussName.setText(item.getStrName());
        viewHolder.fLayoutUnRead.setVisibility(8);
        viewHolder.unreadMsgImg.setVisibility(8);
        if (item.isBUnreadMsg()) {
            viewHolder.unreadMsgImg.setVisibility(0);
        }
        ArrayList<MsgRecord> arrayList = this.mMyApp.ipmsgService.fileMsgs.get(item.getStrId());
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
            viewHolder.fLayoutUnRead.setVisibility(0);
            viewHolder.textFilePrompt.setText(String.valueOf(i2));
        }
        return view;
    }

    private class ViewHolder {
        TextView discussName;
        FrameLayout fLayoutUnRead;
        ImageView imageVHead;
        TextView textFilePrompt;
        ImageView unreadMsgImg;

        private ViewHolder() {
        }
    }
}

