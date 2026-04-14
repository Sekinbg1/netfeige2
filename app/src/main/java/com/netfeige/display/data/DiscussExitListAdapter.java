package com.netfeige.display.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.netfeige.R;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class DiscussExitListAdapter extends ArrayAdapter<String> {
    public DiscussExitListAdapter(Context context, int i, List<String> list) {
        super(context, i, list);
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        String item = getItem(i);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.exitdiscusslistitem, (ViewGroup) null);
            viewHolder = new ViewHolder();
            viewHolder.content = (TextView) view.findViewById(R.id.tip_text_exitdiscusslistitem);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.content.setText(item);
        return view;
    }

    private class ViewHolder {
        TextView content;

        private ViewHolder() {
        }
    }
}

