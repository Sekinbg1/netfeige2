package com.netfeige.display.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.netfeige.R;
import java.util.List;
import org.teleal.cling.model.meta.Device;

/* JADX INFO: loaded from: classes.dex */
public class ChoiceRemotePlayerAdpter extends ArrayAdapter<Device> {
    private Device m_device;

    public ChoiceRemotePlayerAdpter(Context context, int i, List<Device> list) {
        super(context, i, list);
        this.m_device = null;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        this.m_device = getItem(i);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.choiceremoteplayeritem, (ViewGroup) null);
            viewHolder = new ViewHolder();
            viewHolder.remotePlayerName = (TextView) view.findViewById(R.id.textv_remoteplayername);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.remotePlayerName.setText(this.m_device.getDetails().getFriendlyName());
        return view;
    }

    private class ViewHolder {
        TextView remotePlayerName;

        private ViewHolder() {
        }
    }
}

