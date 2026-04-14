package com.netfeige.display.data;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.common.HostInformation;
import com.netfeige.common.Public_Tools;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class SelectedMemListAdapter extends ArrayAdapter<HostInformation> {
    private boolean mBIsManager;
    private List<String> mListMemberExisted;

    public SelectedMemListAdapter(Context context, int i, List<HostInformation> list, List<String> list2, boolean z) {
        super(context, i, list);
        this.mListMemberExisted = list2;
        this.mBIsManager = z;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        HostInformation item = getItem(i);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.selectedmemberitem, (ViewGroup) null);
            viewHolder = new ViewHolder();
            viewHolder.imageVHead = (ImageView) view.findViewById(R.id.head_img_selectedmemberitem);
            viewHolder.userName = (TextView) view.findViewById(R.id.name_text_selectedmemberitem);
            viewHolder.ipTextView = (TextView) view.findViewById(R.id.ip_text_selectedmemberitem);
            viewHolder.imgDelete = (ImageView) view.findViewById(R.id.delete_img_selectedmemberitem);
            viewHolder.imgDelete.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.data.SelectedMemListAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    SelectedMemListAdapter.this.remove((HostInformation) view2.getTag());
                    SelectedMemListAdapter.this.notifyDataSetChanged();
                }
            });
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.imgDelete.setTag(item);
        int i2 = 0;
        viewHolder.imgDelete.setVisibility(0);
        viewHolder.userName.setText(item.pszUserName);
        viewHolder.ipTextView.setText(item.IpAddr.netAddr.getHostAddress());
        if (item.headImage != null) {
            try {
                viewHolder.imageVHead.setImageResource(((IpmsgApplication) ((Activity) getContext()).getApplication()).g_headMap.get(item.headImage).intValue());
            } catch (Exception unused) {
                viewHolder.imageVHead.setImageResource(R.drawable.head_01);
            }
        } else {
            viewHolder.imageVHead.setImageResource(R.drawable.head_01);
        }
        if (!this.mBIsManager) {
            while (true) {
                if (i2 >= this.mListMemberExisted.size()) {
                    break;
                }
                if (this.mListMemberExisted.get(i2).equals(item.strMacAddr)) {
                    viewHolder.imgDelete.setVisibility(8);
                    break;
                }
                i2++;
            }
        } else if (Public_Tools.getLocalMacAddress().equals(item.strMacAddr)) {
            viewHolder.imgDelete.setVisibility(8);
        }
        return view;
    }

    private class ViewHolder {
        ImageView imageVHead;
        ImageView imgDelete;
        TextView ipTextView;
        TextView userName;

        private ViewHolder() {
        }
    }
}

