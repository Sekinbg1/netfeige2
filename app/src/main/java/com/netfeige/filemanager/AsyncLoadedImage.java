package com.netfeige.filemanager;

import android.content.Context;
import android.os.AsyncTask;
import com.netfeige.common.FileInfo;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.ui.FileActivity;
import com.netfeige.display.ui.IpmsgActivity;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class AsyncLoadedImage extends AsyncTask<Object, FileInfo, Object> {
    private ArrayList<FileInfo> m_arrayLImages = null;
    private Context m_context;
    private int m_nEnd;
    private int m_nFirst;

    @Override // android.os.AsyncTask
    protected void onPostExecute(Object obj) {
    }

    public AsyncLoadedImage(int i, int i2, Context context) {
        this.m_nFirst = i;
        this.m_nEnd = i2;
        this.m_context = context;
    }

    @Override // android.os.AsyncTask
    protected Object doInBackground(Object... objArr) {
        Context context = this.m_context;
        if (context instanceof FileActivity) {
            this.m_arrayLImages = FileActivity.s_choiceListView.getFileAdapter().getFileList();
        } else if (context instanceof IpmsgActivity) {
            this.m_arrayLImages = IpmsgActivity.s_fileListView.getFileAdapter().getFileList();
        }
        int size = this.m_arrayLImages.size();
        int i = this.m_nEnd;
        if (size >= i) {
            size = i;
        }
        for (int i2 = this.m_nFirst; i2 < size; i2++) {
            try {
                boolean zIsImageFile = Public_Tools.isImageFile(this.m_arrayLImages.get(i2).getName());
                boolean zIsApkFile = Public_Tools.isApkFile(this.m_arrayLImages.get(i2).getName());
                if (zIsImageFile && this.m_arrayLImages.get(i2).getBitmap() == null) {
                    this.m_arrayLImages.get(i2).setBitmap(Public_Tools.decodeBitmap(this.m_arrayLImages.get(i2).getPath(), 60, 60));
                    publishProgress(this.m_arrayLImages.get(i2));
                }
                if (zIsApkFile && this.m_arrayLImages.get(i2).getDrawable() == null) {
                    this.m_arrayLImages.get(i2).setDrawable(ApkManager.getIApkManager(this.m_context).getApkIcon(this.m_arrayLImages.get(i2).getPath()));
                    publishProgress(this.m_arrayLImages.get(i2));
                }
            } catch (Exception unused) {
            }
        }
        return null;
    }

    @Override // android.os.AsyncTask
    public void onProgressUpdate(FileInfo... fileInfoArr) {
        updateImage(fileInfoArr);
    }

    private void updateImage(FileInfo... fileInfoArr) {
        try {
            if (this.m_context instanceof FileActivity) {
                FileActivity.s_choiceListView.getFileAdapter().notifyDataSetChanged();
            } else if (this.m_context instanceof IpmsgActivity) {
                IpmsgActivity.s_fileListView.getFileAdapter().notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

