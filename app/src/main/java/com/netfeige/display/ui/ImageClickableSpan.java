package com.netfeige.display.ui;

import android.text.style.ClickableSpan;
import android.view.View;
import com.netfeige.common.Global;
import com.netfeige.common.ImagePreview;
import com.netfeige.display.data.ImageAdapter;
import com.netfeige.filemanager.FileManager;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class ImageClickableSpan extends ClickableSpan {
    private String m_strFileFullPath;

    public ImageClickableSpan(String str) {
        this.m_strFileFullPath = str;
    }

    @Override // android.text.style.ClickableSpan
    public void onClick(View view) {
        File file = new File(this.m_strFileFullPath);
        Global.g_bIsClearImageList = true;
        ImageAdapter.s_imageList.clear();
        ImageAdapter.s_imageList.add(new ImagePreview(0, file.getName(), file.getAbsolutePath(), null));
        FileManager.getIFileManager(view.getContext()).openFile(file);
    }
}

