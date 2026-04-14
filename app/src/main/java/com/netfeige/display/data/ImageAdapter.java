package com.netfeige.display.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import com.netfeige.common.ImagePreview;
import com.netfeige.display.ui.AlbumImageView;
import com.netfeige.display.ui.ImagePreviewActivity;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class ImageAdapter extends BaseAdapter {
    public static ArrayList<ImagePreview> s_imageList = new ArrayList<>();
    private Context m_context;

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    public ImageAdapter(Context context) {
        this.m_context = context;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return s_imageList.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return s_imageList.get(i);
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        AlbumImageView albumImageView;
        Bitmap m_imageBitmap = s_imageList.get(i).getM_imageBitmap();
        if (m_imageBitmap != null) {
            albumImageView = new AlbumImageView(this.m_context, m_imageBitmap.getWidth(), m_imageBitmap.getHeight());
            albumImageView.setImageBitmap(null);
            albumImageView.setImageBitmap(m_imageBitmap);
            albumImageView.setSrc(s_imageList.get(i).getM_strImagePath());
        } else {
            Bitmap bitmap = ImagePreviewActivity.s_bmDamage;
            albumImageView = new AlbumImageView(this.m_context, bitmap.getWidth(), bitmap.getHeight());
        }
        albumImageView.setLayoutParams(new Gallery.LayoutParams(-1, -1));
        return albumImageView;
    }

    public ArrayList<ImagePreview> getM_imageList() {
        return s_imageList;
    }
}

