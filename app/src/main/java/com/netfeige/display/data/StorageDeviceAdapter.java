package com.netfeige.display.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.common.Public_Tools;
import com.netfeige.common.StorageDevice;
import java.io.File;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class StorageDeviceAdapter extends ArrayAdapter<StorageDevice> {
    private LayoutInflater m_inflater;

    public StorageDeviceAdapter(Context context, int i, List<StorageDevice> list) {
        super(context, i, list);
        this.m_inflater = LayoutInflater.from(context);
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = this.m_inflater.inflate(R.layout.storagedeviceitem, viewGroup, false);
        }
        ((TextView) view.findViewById(R.id.name_text_storagedeviceitem)).setText(getItem(i).getStrDeviceName());
        File file = new File(getItem(i).getStrMountPoint());
        long usableSpace = file.getUsableSpace();
        long totalSpace = file.getTotalSpace();
        getItem(i).setlUsableSpace(usableSpace);
        ((ProgressBar) view.findViewById(R.id.space_progress_storagedeviceitem)).setProgress((int) (((totalSpace - usableSpace) * 100) / totalSpace));
        ((TextView) view.findViewById(R.id.usablespace_text_storagedeviceitem)).setText(Public_Tools.getShortSize(usableSpace) + "可用 /" + Public_Tools.getShortSize(totalSpace));
        ((CheckBox) view.findViewById(R.id.device_box_storagedeviceitem)).setChecked(getItem(i).isChecked());
        return view;
    }
}

