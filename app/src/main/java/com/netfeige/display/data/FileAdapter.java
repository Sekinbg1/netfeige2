package com.netfeige.display.data;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.netfeige.R;
import com.netfeige.common.Category;
import com.netfeige.common.FileInfo;
import com.netfeige.common.Global;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.ui.FileActivity;
import com.netfeige.display.ui.IpmsgActivity;
import com.netfeige.filemanager.ApkManager;
import com.netfeige.filemanager.Catalogue;
import java.io.File;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class FileAdapter extends BaseAdapter {
	private Context m_context;
	private LayoutInflater m_inflater;
	private IpmsgApplication m_ipmsgApp;
	public ArrayList<FileInfo> fileList = new ArrayList<>();
	private int m_nFileMaxWitch = 200;
	private int m_nFolderMaxWitch = 170;
	private FileInfo m_fileInfo = null;
	private final int VIEW_TYPE = 3;
	private final int TYPE_1 = 0;
	private final int TYPE_2 = 1;
	private final int TYPE_3 = 2;

	@Override // android.widget.Adapter
	public long getItemId(int i) {
		return i;
	}

	@Override // android.widget.BaseAdapter, android.widget.Adapter
	public int getViewTypeCount() {
		return 3;
	}

	public FileAdapter(Context context) {
		this.m_context = context;
		this.m_ipmsgApp = (IpmsgApplication) ((Activity) context).getApplication();
		this.m_inflater = LayoutInflater.from(this.m_context);
	}

	public ArrayList<FileInfo> getFileList() {
		return this.fileList;
	}

	public int getPosition(FileInfo fileInfo) {
		int i = 0;
		while (i < this.fileList.size()) {
			this.fileList.get(i);
			i++;
		}
		return i;
	}

	@Override // android.widget.Adapter
	public int getCount() {
		return this.fileList.size();
	}

	@Override // android.widget.Adapter
	public Object getItem(int i) {
		return this.fileList.get(i);
	}

	@Override // android.widget.BaseAdapter, android.widget.Adapter
	public int getItemViewType(int i) {
		String lowerCase = this.fileList.get(i).getName().toLowerCase();
		if (i == 0) {
			return 0;
		}
		return (lowerCase.endsWith(".jpg") || lowerCase.endsWith(".jpeg") || lowerCase.endsWith(".png") || lowerCase.endsWith(".bmp") || lowerCase.endsWith(".gif")) ? 2 : 1;
	}

	@Override // android.widget.Adapter
	public View getView(int i, View view, ViewGroup viewGroup) {
		viewHolder1 viewholder1;
		viewHolder2 viewholder2;
		viewHolder3 viewholder3;
		viewHolder3 viewholder32;
		viewHolder2 viewholder22;
		FileInfo fileInfo = this.fileList.get(i);
		int itemViewType = getItemViewType(i);
		viewHolder1 viewholder12 = null;
		if (view == null) {
			if (itemViewType == 0) {
				view = this.m_inflater.inflate(R.layout.fileitem2, viewGroup, false);
				viewHolder1 viewholder13 = new viewHolder1();
				viewholder13.textView = (TextView) view.findViewById(R.id.name_text_fileItem2);
				viewholder13.imageView = (ImageView) view.findViewById(R.id.file_img_fileItem2);
				view.setTag(viewholder13);
				viewholder32 = null;
				viewholder12 = viewholder13;
				viewholder22 = null;
			} else if (itemViewType == 1) {
				view = this.m_inflater.inflate(R.layout.sfileitem, viewGroup, false);
				viewholder22 = new viewHolder2();
				viewholder22.textVName = (TextView) view.findViewById(R.id.name_text_fileItem);
				viewholder22.textVSize = (TextView) view.findViewById(R.id.size_text_fileItem);
				viewholder22.textVCount = (TextView) view.findViewById(R.id.count_text_fileItem);
				viewholder22.textVTime = (TextView) view.findViewById(R.id.lasttime_text_fileItem);
				viewholder22.fileImg = (ImageView) view.findViewById(R.id.file_img_fileItem);
				viewholder22.fileSelectBox = (CheckBox) view.findViewById(R.id.file_select_box_fileitem);
				view.setTag(viewholder22);
				viewholder32 = null;
			} else if (itemViewType != 2) {
				viewholder22 = null;
				viewholder32 = null;
				viewholder12 = null;
			} else {
				view = this.m_inflater.inflate(R.layout.sfileitem3, viewGroup, false);
				viewHolder3 viewholder33 = new viewHolder3();
				viewholder33.textVName = (TextView) view.findViewById(R.id.name_text_fileItem);
				viewholder33.textVSize = (TextView) view.findViewById(R.id.size_text_fileItem);
				viewholder33.textVTime = (TextView) view.findViewById(R.id.lasttime_text_fileItem);
				viewholder33.fileImg = (ImageView) view.findViewById(R.id.file_img_fileItem);
				viewholder33.fileSelectBox = (CheckBox) view.findViewById(R.id.file_select_box_fileitem);
				view.setTag(viewholder33);
				viewholder32 = viewholder33;
				viewholder22 = null;
			}
			viewholder2 = viewholder22;
			viewholder3 = viewholder32;
			viewholder1 = viewholder12;
		} else {
			if (itemViewType == 0) {
				viewholder1 = (viewHolder1) view.getTag();
				viewholder2 = null;
				viewholder3 = null;
			} else if (itemViewType == 1) {
				viewholder2 = (viewHolder2) view.getTag();
				viewholder1 = null;
				viewholder3 = null;
			} else if (itemViewType != 2) {
				viewholder1 = null;
				viewholder2 = null;
				viewholder3 = null;
			} else {
				viewholder3 = (viewHolder3) view.getTag();
				viewholder1 = null;
				viewholder2 = null;
			}
			// viewholder3 = viewholder2; // Type mismatch removed
		}
		setLayoutValue(i, fileInfo, viewholder1, viewholder2, viewholder3, itemViewType);
		return view;
	}

	private void setLayoutValue(int i, FileInfo fileInfo, viewHolder1 viewholder1, viewHolder2 viewholder2, viewHolder3 viewholder3, int i2) {
		if (i2 == 0) {
			setFristTypeValue(viewholder1);
		} else if (i2 == 1) {
			setSecondTypeValue(i, fileInfo, viewholder2);
		} else {
			if (i2 != 2) {
				return;
			}
			setThirdTypeValue(i, fileInfo, viewholder3);
		}
	}

	private void setThirdTypeValue(int i, FileInfo fileInfo, viewHolder3 viewholder3) {
		AnonymousClass1 anonymousClass1 = null;
		if (fileInfo.getBitmap() != null) {
			viewholder3.fileImg.setImageBitmap(fileInfo.getBitmap());
			viewholder3.fileImg.setBackgroundColor(0);
		} else {
			viewholder3.fileImg.setImageBitmap(null);
			viewholder3.fileImg.setBackgroundResource(R.drawable.pic);
		}
		viewholder3.textVName.setText(fileInfo.getName());
		viewholder3.textVSize.setText(Public_Tools.getShortSize(fileInfo.getSize()));
		viewholder3.textVTime.setText(Public_Tools.lastModifiedTime(fileInfo.getLastTime()));
		viewholder3.fileSelectBox.setChecked(this.fileList.get(i).isCheck());
		viewholder3.fileSelectBox.setTag(i + "");
		viewholder3.fileSelectBox.setOnClickListener(new CheckBoxClickListener(this, anonymousClass1));
	}

	private void setSecondTypeValue(int i, FileInfo fileInfo, viewHolder2 viewholder2) {
		if (fileInfo.isFloder()) {
			setFolderType(fileInfo, viewholder2);
		} else {
			setFileType(fileInfo, viewholder2);
		}
		viewholder2.fileSelectBox.setChecked(this.fileList.get(i).isCheck());
		viewholder2.fileSelectBox.setTag(i + "");
		viewholder2.fileSelectBox.setOnClickListener(new CheckBoxClickListener(this, null));
	}

	private void setFileType(FileInfo fileInfo, viewHolder2 viewholder2) {
		if (Public_Tools.isApkFile(fileInfo.getPath())) {
			if (fileInfo.getDrawable() != null) {
				viewholder2.fileImg.setBackgroundDrawable(fileInfo.getDrawable());
			} else {
				viewholder2.fileImg.setBackgroundDrawable(this.m_ipmsgApp.getIconByExtentionName(fileInfo.name, true, true));
			}
			viewholder2.textVSize.setText(Public_Tools.getShortSize(fileInfo.getSize()));
			viewholder2.textVName.setText(ApkManager.getIApkManager(this.m_context).getAppName(fileInfo.getPath(), fileInfo.getName()));
			viewholder2.textVTime.setText(ApkManager.getIApkManager(this.m_context).getVersion(fileInfo.getPath()));
		} else {
			viewholder2.fileImg.setBackgroundDrawable(this.m_ipmsgApp.getIconByExtentionName(fileInfo.name, true, true));
			viewholder2.textVName.setText(fileInfo.getName());
			viewholder2.textVSize.setText(Public_Tools.getShortSize(fileInfo.getSize()));
			viewholder2.textVTime.setText(Public_Tools.lastModifiedTime(fileInfo.getLastTime()));
		}
		viewholder2.textVCount.setVisibility(8);
		viewholder2.textVName.setMaxWidth(Public_Tools.dip2px(this.m_context, this.m_nFileMaxWitch));
	}

	private void setFolderType(FileInfo fileInfo, viewHolder2 viewholder2) {
		viewholder2.textVSize.setText("");
		viewholder2.textVCount.setVisibility(0);
		viewholder2.textVName.setMaxWidth(Public_Tools.dip2px(this.m_context, this.m_nFolderMaxWitch));
		viewholder2.textVCount.setText("(" + getFolderCount(fileInfo.getPath()) + ")");
		try {
			if (Public_Tools.getDefaultDownloadPath().endsWith(fileInfo.path)) {
				viewholder2.fileImg.setImageBitmap(null);
				viewholder2.fileImg.setBackgroundResource(R.drawable.filer_feige);
			} else {
				viewholder2.fileImg.setImageBitmap(null);
				viewholder2.fileImg.setBackgroundResource(R.drawable.folder_icon);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			viewholder2.fileImg.setImageBitmap(null);
			viewholder2.fileImg.setBackgroundResource(R.drawable.folder_icon);
		}
		viewholder2.textVName.setText(fileInfo.getName());
		viewholder2.textVTime.setText(Public_Tools.lastModifiedTime(fileInfo.getLastTime()));
	}

	private void setFristTypeValue(viewHolder1 viewholder1) {
		viewholder1.textView.setText("上一级");
		viewholder1.textView.setTextColor(Color.rgb(43, 125, 204));
		viewholder1.imageView.setBackgroundResource(R.drawable.backforward2);
		Context context = this.m_context;
		if (context instanceof FileActivity) {
			Bundle extras = ((FileActivity) context).getIntent().getExtras();
			if (extras.getString("flag") == null || !extras.getString("flag").equals("myshare") || Catalogue.inFileActivityStack.size() > 1) {
				return;
			}
			viewholder1.textView.setTextColor(Color.rgb(128, 128, 128));
			viewholder1.imageView.setBackgroundResource(R.drawable.backforward2_disable);
		}
	}

	public FileInfo getFileInfo() {
		return this.m_fileInfo;
	}

	public void setFileInfo(FileInfo fileInfo) {
		if (fileInfo == null) {
			this.m_fileInfo = null;
		} else {
			this.m_fileInfo = fileInfo;
		}
	}

	class viewHolder1 {
		ImageView imageView;
		TextView textView;

		viewHolder1() {
		}
	}

	class viewHolder2 {
		ImageView fileImg;
		CheckBox fileSelectBox;
		TextView textVCount;
		TextView textVName;
		TextView textVSize;
		TextView textVTime;

		viewHolder2() {
		}
	}

	class viewHolder3 {
		ImageView fileImg;
		CheckBox fileSelectBox;
		TextView textVName;
		TextView textVSize;
		TextView textVTime;

		viewHolder3() {
		}
	}

	private class CheckBoxClickListener implements View.OnClickListener {
		private CheckBoxClickListener() {
		}

		/* synthetic */ CheckBoxClickListener(FileAdapter fileAdapter, AnonymousClass1 anonymousClass1) {
			this();
		}

		@Override // android.view.View.OnClickListener
		public void onClick(View view) {
			FileAdapter.this.choiceFile(view, Integer.parseInt(view.getTag().toString()));
		}
	}

	public void choiceFile(View view, int i) {
		if (Global.g_isInFileActivity) {
			choiceCheckFile(view, i);
			sendOrCancel();
			return;
		}
		mainCheckFile(view, i);
		if (Global.g_filePath.size() > 1) {
			Global.g_bMultipleChoice = true;
		} else {
			Global.g_bMultipleChoice = false;
		}
		showPopupSend();
	}

	private void showPopupSend() {
		Context context = this.m_context;
		if (context instanceof IpmsgActivity) {
			IpmsgActivity ipmsgActivity = (IpmsgActivity) context;
			if (Global.g_filePath.size() > 0) {
				ipmsgActivity.showSendPopupWindow();
				if (!IpmsgActivity.m_popupWindowSend.isShowing()) {
					IpmsgActivity.m_popupWindowSend.showAtLocation(ipmsgActivity.getViewFramel(), 80, 0, 0);
					ipmsgActivity.getHorizontalScrollView().scrollTo(ipmsgActivity.getHorizontalScrollView().getRight(), 0);
					ipmsgActivity.getHorizontalScrollView().smoothScrollTo(0, ipmsgActivity.getHorizontalScrollView().getHeight());
					if (ipmsgActivity.getPopupWindowPaste().isShowing()) {
						Global.g_bWaitPaste = 0;
						ipmsgActivity.getPopupWindowPaste().dismiss();
					}
					ipmsgActivity.getTextVNumber().setText(Global.g_filePath.size() + "");
					return;
				}
				ipmsgActivity.getTextVNumber().setText(Global.g_filePath.size() + "");
				return;
			}
			if (IpmsgActivity.m_popupWindowSend.isShowing()) {
				IpmsgActivity.m_popupWindowSend.dismiss();
			}
		}
	}

	private void sendOrCancel() {
		if (Global.g_chiocePaths.size() > 0) {
			FileActivity.s_btnSend.setTextColor(-1);
			FileActivity.s_btnSend.setEnabled(true);
			Global.g_bIsEnable = true;
		} else {
			FileActivity.s_btnSend.setTextColor(this.m_context.getResources().getColor(R.color.textcolor3));
			FileActivity.s_btnSend.setEnabled(false);
			Global.g_bIsEnable = true;
		}
	}

	private void choiceCheckFile(View view, int i) {
		if (Global.g_chiocePaths.size() >= 10) {
			choiceDy10(view, i);
		} else {
			choiceXy10(view, i);
		}
	}

	private void choiceXy10(View view, int i) {
		CheckBox checkBox = (CheckBox) view;
		checkBox.setChecked(!this.fileList.get(i).isCheck());
		this.fileList.get(i).setCheck(!this.fileList.get(i).isCheck());
		if (checkBox.isChecked()) {
			Global.g_chiocePaths.add(this.fileList.get(i).getPath());
		} else if (Global.g_chiocePaths.contains(this.fileList.get(i).getPath())) {
			Global.g_chiocePaths.remove(this.fileList.get(i).getPath());
		}
	}

	private void choiceDy10(View view, int i) {
		CheckBox checkBox = (CheckBox) view;
		if (!checkBox.isChecked()) {
			checkBox.setChecked(false);
			this.fileList.get(i).setCheck(false);
			if (Global.g_chiocePaths.contains(this.fileList.get(i).getPath())) {
				Global.g_chiocePaths.remove(this.fileList.get(i).getPath());
				return;
			}
			return;
		}
		checkBox.setChecked(false);
		Toast.makeText(this.m_context, R.string.max_check, 0).show();
	}

	private void mainCheckFile(View view, int i) {
		if (Global.g_filePath.size() >= 10) {
			mainDy10(view, i);
		} else {
			mainXy10(view, i);
		}
	}

	private void mainXy10(View view, int i) {
		CheckBox checkBox = (CheckBox) view;
		checkBox.setChecked(!this.fileList.get(i).isCheck());
		this.fileList.get(i).setCheck(!this.fileList.get(i).isCheck());
		if (checkBox.isChecked()) {
			Global.g_filePath.add(this.fileList.get(i).getPath());
		} else if (Global.g_filePath.contains(this.fileList.get(i).getPath())) {
			Global.g_filePath.remove(this.fileList.get(i).getPath());
		}
	}

	private void mainDy10(View view, int i) {
		CheckBox checkBox = (CheckBox) view;
		if (!checkBox.isChecked()) {
			checkBox.setChecked(false);
			this.fileList.get(i).setCheck(false);
			if (Global.g_filePath.contains(this.fileList.get(i).getPath())) {
				Global.g_filePath.remove(this.fileList.get(i).getPath());
				return;
			}
			return;
		}
		checkBox.setChecked(false);
		Toast.makeText(this.m_context, R.string.max_check, 0).show();
	}

	private int getFolderCount(String str) {
		int i;
		File[] fileArrListFiles = new File(str).listFiles();
		int i2 = 0;
		if (fileArrListFiles == null) {
			return 0;
		}
		int length = fileArrListFiles.length;
		int i3 = AnonymousClass1.$SwitchMap$com$netfeige$common$Category[Global.g_whatFolder.ordinal()];
		if (i3 == 1) {
			i = 0;
			while (i2 < length) {
				if (Public_Tools.isImageFile(fileArrListFiles[i2].getName()) && !fileArrListFiles[i2].isHidden()) {
					i++;
				}
				i2++;
			}
		} else if (i3 == 2) {
			i = 0;
			while (i2 < length) {
				if (Public_Tools.isAudioFile(fileArrListFiles[i2].getName()) && !fileArrListFiles[i2].isHidden()) {
					i++;
				}
				i2++;
			}
		} else if (i3 == 3) {
			i = 0;
			while (i2 < length) {
				if (Public_Tools.isVideoFile(fileArrListFiles[i2].getName()) && !fileArrListFiles[i2].isHidden()) {
					i++;
				}
				i2++;
			}
		} else if (i3 == 4) {
			i = 0;
			while (i2 < length) {
				if (Public_Tools.isDocumentFile(fileArrListFiles[i2].getName()) && !fileArrListFiles[i2].isHidden()) {
					i++;
				}
				i2++;
			}
		} else if (i3 != 5) {
			i = 0;
			while (i2 < length) {
				if (!fileArrListFiles[i2].isHidden()) {
					i++;
				}
				i2++;
			}
		} else {
			i = 0;
			while (i2 < length) {
				if (Public_Tools.isApkFile(fileArrListFiles[i2].getName()) && !fileArrListFiles[i2].isHidden()) {
					i++;
				}
				i2++;
			}
		}
		return i;
	}

	/* JADX INFO: renamed from: com.netfeige.display.data.FileAdapter$1, reason: invalid class name */
	static /* synthetic */ class AnonymousClass1 {
		static final /* synthetic */ int[] $SwitchMap$com$netfeige$common$Category;

		static {
			int[] iArr = new int[Category.values().length];
			$SwitchMap$com$netfeige$common$Category = iArr;
			try {
				iArr[Category.image.ordinal()] = 1;
			} catch (NoSuchFieldError unused) {
			}
			try {
				$SwitchMap$com$netfeige$common$Category[Category.audio.ordinal()] = 2;
			} catch (NoSuchFieldError unused2) {
			}
			try {
				$SwitchMap$com$netfeige$common$Category[Category.video.ordinal()] = 3;
			} catch (NoSuchFieldError unused3) {
			}
			try {
				$SwitchMap$com$netfeige$common$Category[Category.document.ordinal()] = 4;
			} catch (NoSuchFieldError unused4) {
			}
			try {
				$SwitchMap$com$netfeige$common$Category[Category.apk.ordinal()] = 5;
			} catch (NoSuchFieldError unused5) {
			}
		}
	}
}
