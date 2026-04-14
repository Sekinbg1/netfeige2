package com.netfeige.display.ui.dialog;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import com.netfeige.R;
import com.netfeige.common.Global;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.data.ShareCustomAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class SharePopupWindow extends PopupWindow {
    private static final String m_strShareName = "分享";
    private Context m_context;
    private PackageManager m_packageManager;

    public SharePopupWindow(Context context) {
        super(context);
        this.m_packageManager = null;
        this.m_context = context;
        this.m_packageManager = context.getPackageManager();
        View viewInflate = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.popup_share, (ViewGroup) null);
        setContentView(viewInflate);
        setWidth(Public_Tools.dip2px(context, 300.0f));
        setHeight(-2);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        ListView listView = (ListView) viewInflate.findViewById(R.id.share_list);
        final ShareCustomAdapter shareCustomAdapter = new ShareCustomAdapter(context, getShareAppList());
        listView.setSelection(0);
        listView.setAdapter((ListAdapter) shareCustomAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.netfeige.display.ui.dialog.SharePopupWindow.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                Intent intent = new Intent("android.intent.action.SEND");
                AppInfo appInfo = (AppInfo) shareCustomAdapter.getItem(i);
                intent.setComponent(new ComponentName(appInfo.getAppPkgName(), appInfo.getAppLauncherClassName()));
                intent.setType("*/*");
                File file = new File(Global.g_strSharePath);
                intent.putExtra("android.intent.extra.SUBJECT", SharePopupWindow.m_strShareName);
                intent.putExtra("android.intent.extra.TEXT", file.getName());
                intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(file));
                SharePopupWindow.this.m_context.startActivity(intent);
                SharePopupWindow.this.dismiss();
            }
        });
    }

    public List<ResolveInfo> getShareApps(Context context) {
        new ArrayList();
        Intent intent = new Intent("android.intent.action.SEND", (Uri) null);
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("*/*");
        return this.m_packageManager.queryIntentActivities(intent, 0);
    }

    private List<AppInfo> getShareAppList() {
        ArrayList arrayList = new ArrayList();
        List<ResolveInfo> shareApps = getShareApps(this.m_context);
        if (shareApps == null) {
            return null;
        }
        for (ResolveInfo resolveInfo : shareApps) {
            AppInfo appInfo = new AppInfo();
            appInfo.setAppPkgName(resolveInfo.activityInfo.packageName);
            appInfo.setAppLauncherClassName(resolveInfo.activityInfo.name);
            appInfo.setAppName(resolveInfo.loadLabel(this.m_packageManager).toString());
            appInfo.setAppIcon(resolveInfo.loadIcon(this.m_packageManager));
            arrayList.add(appInfo);
        }
        return arrayList;
    }

    public class AppInfo {
        private Drawable appIcon;
        private String appLauncherClassName;
        private String appName;
        private String appPkgName;

        public AppInfo() {
        }

        public String getAppPkgName() {
            return this.appPkgName;
        }

        public void setAppPkgName(String str) {
            this.appPkgName = str;
        }

        public String getAppLauncherClassName() {
            return this.appLauncherClassName;
        }

        public void setAppLauncherClassName(String str) {
            this.appLauncherClassName = str;
        }

        public String getAppName() {
            return this.appName;
        }

        public void setAppName(String str) {
            this.appName = str;
        }

        public Drawable getAppIcon() {
            return this.appIcon;
        }

        public void setAppIcon(Drawable drawable) {
            this.appIcon = drawable;
        }
    }
}

