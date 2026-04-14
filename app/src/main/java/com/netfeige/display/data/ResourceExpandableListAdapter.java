package com.netfeige.display.data;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import com.netfeige.R;
import com.netfeige.display.ui.IpmsgActivity;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class ResourceExpandableListAdapter extends SimpleExpandableListAdapter {
    private int[] m_childImgId;
    private Context m_context;

    public ResourceExpandableListAdapter(Context context, List<? extends Map<String, ?>> list, int i, String[] strArr, int[] iArr, List<? extends List<? extends Map<String, ?>>> list2, int i2, String[] strArr2, int[] iArr2, int[] iArr3) {
        super(context, list, i, strArr, iArr, list2, i2, strArr2, iArr2);
        this.m_context = context;
        this.m_childImgId = iArr3;
    }

    public ResourceExpandableListAdapter(Context context, List<? extends Map<String, ?>> list, int i, int i2, String[] strArr, int[] iArr, List<? extends List<? extends Map<String, ?>>> list2, int i3, String[] strArr2, int[] iArr2, int[] iArr3) {
        super(context, list, i, i2, strArr, iArr, list2, i3, strArr2, iArr2);
        this.m_context = context;
        this.m_childImgId = iArr3;
    }

    public ResourceExpandableListAdapter(Context context, List<? extends Map<String, ?>> list, int i, int i2, String[] strArr, int[] iArr, List<? extends List<? extends Map<String, ?>>> list2, int i3, int i4, String[] strArr2, int[] iArr2, int[] iArr3) {
        super(context, list, i, i2, strArr, iArr, list2, i3, i4, strArr2, iArr2);
        this.m_context = context;
        this.m_childImgId = iArr3;
    }

    @Override // android.widget.SimpleExpandableListAdapter, android.widget.ExpandableListAdapter
    public View getChildView(int i, int i2, boolean z, View view, ViewGroup viewGroup) {
        View childView = super.getChildView(i, i2, z, view, viewGroup);
        if (i == 0) {
            bindViewEx(childView, this.m_childImgId[i2]);
        }
        return childView;
    }

    @Override // android.widget.SimpleExpandableListAdapter, android.widget.ExpandableListAdapter
    public View getGroupView(int i, boolean z, View view, ViewGroup viewGroup) {
        View groupView = super.getGroupView(i, z, view, viewGroup);
        if (z) {
            ((ImageView) groupView.findViewById(R.id.arrow_img_grouplist_resource)).setImageResource(R.drawable.collapse);
        } else {
            ((ImageView) groupView.findViewById(R.id.arrow_img_grouplist_resource)).setImageResource(R.drawable.expansion);
        }
        return groupView;
    }

    private void bindViewEx(View view, int i) {
        ImageView imageView = (ImageView) view.findViewById(R.id.img_childlist_resource);
        if (imageView != null) {
            imageView.setImageResource(i);
        }
    }

    @Override // android.widget.BaseExpandableListAdapter, android.widget.ExpandableListAdapter
    public void onGroupCollapsed(int i) {
        if (i == 1) {
            ((GridView) ((IpmsgActivity) this.m_context).findViewById(R.id.users_gridView)).setVisibility(8);
        }
        super.onGroupCollapsed(i);
    }

    @Override // android.widget.BaseExpandableListAdapter, android.widget.ExpandableListAdapter
    public void onGroupExpanded(int i) {
        if (i == 1) {
            ((GridView) ((IpmsgActivity) this.m_context).findViewById(R.id.users_gridView)).setVisibility(0);
        }
        super.onGroupExpanded(i);
    }
}

