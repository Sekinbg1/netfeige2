package com.netfeige.display.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.common.InviteDiscussInfo;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class InviteDiscussListAdapter extends ArrayAdapter<InviteDiscussInfo> {
    private OnInviteClickListener mInviteClickListener;

    public interface OnInviteClickListener {
        void onReceive(View view);

        void onRefuse(View view);
    }

    public InviteDiscussListAdapter(Context context, int i, List<InviteDiscussInfo> list, OnInviteClickListener onInviteClickListener) {
        super(context, i, list);
        this.mInviteClickListener = null;
        this.mInviteClickListener = onInviteClickListener;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        InviteDiscussInfo item = getItem(i);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.invitediscusslistitem, (ViewGroup) null);
            viewHolder = new ViewHolder();
            viewHolder.content = (TextView) view.findViewById(R.id.content_text_invitediscusslistitem);
            viewHolder.receiveBtn = (Button) view.findViewById(R.id.receive_btn_invitediscusslistitem);
            viewHolder.receiveBtn.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.data.InviteDiscussListAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    if (InviteDiscussListAdapter.this.mInviteClickListener != null) {
                        InviteDiscussListAdapter.this.mInviteClickListener.onReceive(view2);
                    }
                }
            });
            viewHolder.refuseBtn = (Button) view.findViewById(R.id.refuse_btn_invitediscusslistitem);
            viewHolder.refuseBtn.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.data.InviteDiscussListAdapter.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    if (InviteDiscussListAdapter.this.mInviteClickListener != null) {
                        InviteDiscussListAdapter.this.mInviteClickListener.onRefuse(view2);
                    }
                }
            });
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.content.setText(item.getHostInformation().pszUserName + "邀请你加入讨论组：" + item.getDiscussInfo().getStrName());
        viewHolder.receiveBtn.setTag(item);
        viewHolder.refuseBtn.setTag(item);
        return view;
    }

    private class ViewHolder {
        TextView content;
        Button receiveBtn;
        Button refuseBtn;

        private ViewHolder() {
        }
    }
}

