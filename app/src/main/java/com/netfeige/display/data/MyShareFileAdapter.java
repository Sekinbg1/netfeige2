package com.netfeige.display.data;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.common.Public_Tools;
import com.netfeige.common.ShareFiles;
import com.netfeige.display.ui.MyShareActivity;
import com.netfeige.filemanager.ApkManager;
import java.io.File;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class MyShareFileAdapter extends BaseAdapter {
	private Context m_context;
	private LayoutInflater m_inflater;
	private IpmsgApplication m_ipmsgApp;
	private ArrayList<ShareFiles> m_myShareFileList;
	private int m_nFileMaxWitch = 200;
	private int m_nFolderMaxWitch = 170;
	private final int TYPE_1 = 0;
	private final int TYPE_2 = 1;
	private final int TYPE_3 = 2;

	@Override // android.widget.Adapter
	public long getItemId(int i) {
		return 0L;
	}

	@Override // android.widget.BaseAdapter, android.widget.Adapter
	public int getViewTypeCount() {
		return 3;
	}

	public MyShareFileAdapter(Context context, ArrayList<ShareFiles> arrayList) {
		this.m_myShareFileList = new ArrayList<>();
		this.m_context = context;
		this.m_ipmsgApp = (IpmsgApplication) ((Activity) context).getApplication();
		this.m_myShareFileList = arrayList;
		this.m_inflater = LayoutInflater.from(this.m_context);
	}

	@Override // android.widget.Adapter
	public int getCount() {
		return this.m_myShareFileList.size();
	}

	@Override // android.widget.Adapter
	public Object getItem(int i) {
		return this.m_myShareFileList.get(i);
	}

	@Override // android.widget.BaseAdapter, android.widget.Adapter
	public int getItemViewType(int i) {
		String lowerCase = this.m_myShareFileList.get(i).getM_strName().toLowerCase();
		return (lowerCase.endsWith(".jpg") || lowerCase.endsWith(".jpeg") || lowerCase.endsWith(".png") || lowerCase.endsWith(".bmp") || lowerCase.endsWith(".gif")) ? 2 : 1;
	}

	@Override // android.widget.Adapter
	public View getView(int i, View view, ViewGroup viewGroup) {
		ShareFiles shareFiles = this.m_myShareFileList.get(i);
		int itemViewType = getItemViewType(i);
		if (view == null) {
			if (itemViewType == 0) {
				view = this.m_inflater.inflate(R.layout.fileitem2, viewGroup, false);
			} else if (itemViewType == 1) {
				view = this.m_inflater.inflate(R.layout.sfileitem, viewGroup, false);
			} else if (itemViewType == 2) {
				view = this.m_inflater.inflate(R.layout.sfileitem3, viewGroup, false);
			}
		}
		setLayoutValue(i, shareFiles, view, itemViewType);
		return view;
	}

	private void setLayoutValue(int i, ShareFiles shareFiles, View view, int i2) {
		if (i2 == 0) {
			setFristTypeValue(view);
		} else if (i2 == 1) {
			setSecondTypeValue(i, shareFiles, view);
		} else {
			if (i2 != 2) {
				return;
			}
			setThirdTypeValue(i, shareFiles, view);
		}
	}

	private void setFristTypeValue(View view) {
		((TextView) view.findViewById(R.id.name_text_fileItem2)).setText("上一级");
		((TextView) view.findViewById(R.id.name_text_fileItem2)).setTextColor(Color.rgb(43, 125, 204));
		((ImageView) view.findViewById(R.id.file_img_fileItem2)).setBackgroundResource(R.drawable.backforward2);
	}

	private void setSecondTypeValue(int i, ShareFiles shareFiles, View view) {
		if (new File(shareFiles.getM_strPath()).isDirectory()) {
			setFolderType(shareFiles, view);
		} else {
			setFileType(shareFiles, view);
		}
		if (this.m_myShareFileList.get(i).getM_strMACList().isEmpty()) {
			((TextView) view.findViewById(R.id.count_text_fileItem)).setText(this.m_context.getResources().getString(R.string.alluser));
		} else {
			((TextView) view.findViewById(R.id.count_text_fileItem)).setText(this.m_context.getResources().getString(R.string.specifyuser));
		}
		((TextView) view.findViewById(R.id.count_text_fileItem)).setTag(i + "");
		((TextView) view.findViewById(R.id.count_text_fileItem)).getPaint().setFlags(8);
		((TextView) view.findViewById(R.id.count_text_fileItem)).setTextColor(this.m_context.getResources().getColor(R.color.textcolor4));
		((TextView) view.findViewById(R.id.count_text_fileItem)).setOnClickListener(new ShareAreaClickListener());
		((CheckBox) view.findViewById(R.id.file_select_box_fileitem)).setChecked(this.m_myShareFileList.get(i).isM_bIsCheck());
		((CheckBox) view.findViewById(R.id.file_select_box_fileitem)).setTag(i + "");
		((CheckBox) view.findViewById(R.id.file_select_box_fileitem)).setOnClickListener(new CheckBoxClickListener());
	}

	private void setFileType(ShareFiles shareFiles, View view) {
		if (Public_Tools.isApkFile(shareFiles.getM_strPath())) {
			Drawable apkIcon = ApkManager.getIApkManager(this.m_context).getApkIcon(shareFiles.getM_strPath());
			if (apkIcon == null) {
				((ImageView) view.findViewById(R.id.file_img_fileItem)).setBackgroundDrawable(this.m_ipmsgApp.getIconByExtentionName(shareFiles.getM_strName(), true, true));
			} else {
				((ImageView) view.findViewById(R.id.file_img_fileItem)).setBackgroundDrawable(apkIcon);
			}
			((TextView) view.findViewById(R.id.size_text_fileItem)).setText(Public_Tools.getShortSize(shareFiles.getM_lSize()));
			((TextView) view.findViewById(R.id.name_text_fileItem)).setText(ApkManager.getIApkManager(this.m_context).getAppName(shareFiles.getM_strPath(), shareFiles.getM_strName()));
			((TextView) view.findViewById(R.id.lasttime_text_fileItem)).setText(ApkManager.getIApkManager(this.m_context).getVersion(shareFiles.getM_strPath()));
		} else {
			((ImageView) view.findViewById(R.id.file_img_fileItem)).setBackgroundDrawable(this.m_ipmsgApp.getIconByExtentionName(shareFiles.getM_strName(), true, true));
			((TextView) view.findViewById(R.id.name_text_fileItem)).setText(shareFiles.getM_strName());
			((TextView) view.findViewById(R.id.size_text_fileItem)).setText(Public_Tools.getShortSize(shareFiles.getM_lSize()));
			((TextView) view.findViewById(R.id.lasttime_text_fileItem)).setText(Public_Tools.lastModifiedTime(shareFiles.getM_lTime()));
		}
		((TextView) view.findViewById(R.id.name_text_fileItem)).setMaxWidth(Public_Tools.dip2px(this.m_context, this.m_nFileMaxWitch));
	}

	private void setFolderType(ShareFiles shareFiles, View view) {
		((TextView) view.findViewById(R.id.size_text_fileItem)).setText("");
		((TextView) view.findViewById(R.id.name_text_fileItem)).setMaxWidth(Public_Tools.dip2px(this.m_context, this.m_nFolderMaxWitch));
		File[] fileArrListFiles = new File(shareFiles.getM_strPath()).listFiles();
		((TextView) view.findViewById(R.id.count_text_fileItem)).setText("(" + (fileArrListFiles != null ? fileArrListFiles.length : 0) + ")");
		if (Public_Tools.getDefaultDownloadPath().endsWith(shareFiles.getM_strPath())) {
			((ImageView) view.findViewById(R.id.file_img_fileItem)).setImageBitmap(null);
			((ImageView) view.findViewById(R.id.file_img_fileItem)).setBackgroundResource(R.drawable.filer_feige);
		} else {
			((ImageView) view.findViewById(R.id.file_img_fileItem)).setImageBitmap(null);
			((ImageView) view.findViewById(R.id.file_img_fileItem)).setBackgroundResource(R.drawable.folder_icon);
		}
		((TextView) view.findViewById(R.id.name_text_fileItem)).setText(shareFiles.getM_strName());
		((TextView) view.findViewById(R.id.lasttime_text_fileItem)).setText(Public_Tools.lastModifiedTime(shareFiles.getM_lTime()));
	}

	private void setThirdTypeValue(int i, ShareFiles shareFiles, View view) {
		Bitmap bitmapDecodeBitmap = Public_Tools.decodeBitmap(shareFiles.getM_strPath(), 60, 60);
		if (bitmapDecodeBitmap == null) {
			((ImageView) view.findViewById(R.id.file_img_fileItem)).setImageBitmap(null);
			((ImageView) view.findViewById(R.id.file_img_fileItem)).setBackgroundResource(R.drawable.pic);
		} else {
			((ImageView) view.findViewById(R.id.file_img_fileItem)).setImageBitmap(bitmapDecodeBitmap);
			((ImageView) view.findViewById(R.id.file_img_fileItem)).setBackgroundColor(0);
		}
		if (this.m_myShareFileList.get(i).getM_strMACList().isEmpty()) {
			((TextView) view.findViewById(R.id.count_text_fileItem)).setText(this.m_context.getResources().getString(R.string.alluser));
		} else {
			((TextView) view.findViewById(R.id.count_text_fileItem)).setText(this.m_context.getResources().getString(R.string.specifyuser));
		}
		((TextView) view.findViewById(R.id.count_text_fileItem)).setTag(i + "");
		((TextView) view.findViewById(R.id.count_text_fileItem)).getPaint().setFlags(8);
		((TextView) view.findViewById(R.id.count_text_fileItem)).setTextColor(this.m_context.getResources().getColor(R.color.textcolor4));
		((TextView) view.findViewById(R.id.count_text_fileItem)).setOnClickListener(new ShareAreaClickListener());
		((TextView) view.findViewById(R.id.name_text_fileItem)).setText(shareFiles.getM_strName());
		((TextView) view.findViewById(R.id.size_text_fileItem)).setText(Public_Tools.getShortSize(shareFiles.getM_lSize()));
		((TextView) view.findViewById(R.id.lasttime_text_fileItem)).setText(Public_Tools.lastModifiedTime(shareFiles.getM_lTime()));
		((CheckBox) view.findViewById(R.id.file_select_box_fileitem)).setChecked(this.m_myShareFileList.get(i).isM_bIsCheck());
		((CheckBox) view.findViewById(R.id.file_select_box_fileitem)).setTag(i + "");
		((CheckBox) view.findViewById(R.id.file_select_box_fileitem)).setOnClickListener(new CheckBoxClickListener());
	}

	private class CheckBoxClickListener implements View.OnClickListener {
		private CheckBoxClickListener() {
		}

		@Override // android.view.View.OnClickListener
		public void onClick(View view) {
			MyShareFileAdapter.this.checkFileOperate(view, Integer.parseInt(view.getTag().toString()));
			((MyShareActivity) MyShareFileAdapter.this.m_context).showPopupOperate();
		}
	}

	private class ShareAreaClickListener implements View.OnClickListener {
		private ShareAreaClickListener() {
		}

		@Override // android.view.View.OnClickListener
		public void onClick(View view) {
			int i = Integer.parseInt(view.getTag().toString());
			((MyShareActivity) MyShareFileAdapter.this.m_context).m_waitModifyShareFile = (ShareFiles) MyShareFileAdapter.this.m_myShareFileList.get(i);
			((MyShareActivity) MyShareFileAdapter.this.m_context).shareAreaDispose();
		}
	}

	public void checkFileOperate(View view, int i) {
		CheckBox checkBox = (CheckBox) view;
		checkBox.setChecked(!this.m_myShareFileList.get(i).isM_bIsCheck());
		this.m_myShareFileList.get(i).setM_bIsCheck(!this.m_myShareFileList.get(i).isM_bIsCheck());
		if (checkBox.isChecked()) {
			((MyShareActivity) this.m_context).getM_selectedFilesList().add(this.m_myShareFileList.get(i));
		} else if (((MyShareActivity) this.m_context).getM_selectedFilesList().contains(this.m_myShareFileList.get(i))) {
			((MyShareActivity) this.m_context).getM_selectedFilesList().remove(this.m_myShareFileList.get(i));
		}
	}
}

