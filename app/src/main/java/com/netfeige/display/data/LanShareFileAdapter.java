package com.netfeige.display.data;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.common.LanSharedItem;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.ui.LanShareActivity;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class LanShareFileAdapter extends BaseAdapter {
	private final int TYPE_1 = 0;
	private final int TYPE_2 = 1;
	private final int TYPE_3 = 2;
	private Context m_context;
	private LayoutInflater m_inflater;
	private IpmsgApplication m_ipmsgApp;
	private ArrayList<LanSharedItem> m_lanShareFileList;

	@Override // android.widget.Adapter
	public long getItemId(int i) {
		return 0L;
	}

	@Override // android.widget.BaseAdapter, android.widget.Adapter
	public int getViewTypeCount() {
		return 3;
	}

	public LanShareFileAdapter(Context context, ArrayList<LanSharedItem> arrayList) {
		this.m_lanShareFileList = new ArrayList<>();
		this.m_context = context;
		this.m_ipmsgApp = (IpmsgApplication) ((Activity) context).getApplication();
		this.m_lanShareFileList = arrayList;
		this.m_inflater = LayoutInflater.from(this.m_context);
	}

	@Override // android.widget.Adapter
	public int getCount() {
		return this.m_lanShareFileList.size();
	}

	@Override // android.widget.Adapter
	public Object getItem(int i) {
		return this.m_lanShareFileList.get(i);
	}

	@Override // android.widget.BaseAdapter, android.widget.Adapter
	public int getItemViewType(int i) {
		String lowerCase = this.m_lanShareFileList.get(i).getM_strName().toLowerCase();
		if (i == 0) {
			return 0;
		}
		return (lowerCase.endsWith(".jpg") || lowerCase.endsWith(".jpeg") || lowerCase.endsWith(".png") || lowerCase.endsWith(".bmp") || lowerCase.endsWith(".gif")) ? 2 : 1;
	}

	@Override // android.widget.Adapter
	public View getView(int i, View view, ViewGroup viewGroup) {
		LanSharedItem lanSharedItem = this.m_lanShareFileList.get(i);
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
		setLayoutValue(i, lanSharedItem, view, itemViewType);
		return view;
	}

	private void setLayoutValue(int i, LanSharedItem lanSharedItem, View view, int i2) {
		if (i2 == 0) {
			setFristTypeValue(view);
		} else if (i2 == 1) {
			setSecondTypeValue(i, lanSharedItem, view);
		} else {
			if (i2 != 2) {
				return;
			}
			setThirdTypeValue(i, lanSharedItem, view);
		}
	}

	private void setFristTypeValue(View view) {
		((TextView) view.findViewById(R.id.name_text_fileItem2)).setText("上一级");
		((TextView) view.findViewById(R.id.name_text_fileItem2)).setTextColor(Color.rgb(43, 125, 204));
		((ImageView) view.findViewById(R.id.file_img_fileItem2)).setBackgroundResource(R.drawable.backforward2);
	}

	private void setSecondTypeValue(int i, LanSharedItem lanSharedItem, View view) {
		if (lanSharedItem.getM_iType() == 1) {
			setFolderType(lanSharedItem, view);
		} else {
			setFileType(lanSharedItem, view);
		}
		((TextView) view.findViewById(R.id.count_text_fileItem)).setText(this.m_lanShareFileList.get(i).getM_strFrom());
		((TextView) view.findViewById(R.id.count_text_fileItem)).setTag(this.m_lanShareFileList.get(i).getM_strFromMac());
		((TextView) view.findViewById(R.id.count_text_fileItem)).getPaint().setFlags(8);
		((TextView) view.findViewById(R.id.count_text_fileItem)).setTextColor(this.m_context.getResources().getColor(R.color.textcolor4));
		((TextView) view.findViewById(R.id.count_text_fileItem)).setOnClickListener(new FromTextVClickListener());
		TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(100, -2);
		layoutParams.column = 1;
		layoutParams.leftMargin = 6;
		layoutParams.gravity = 19;
		((TextView) view.findViewById(R.id.name_text_fileItem)).setLayoutParams(layoutParams);
		((CheckBox) view.findViewById(R.id.file_select_box_fileitem)).setChecked(this.m_lanShareFileList.get(i).isM_bIsCheck());
		((CheckBox) view.findViewById(R.id.file_select_box_fileitem)).setTag(i + "");
		((CheckBox) view.findViewById(R.id.file_select_box_fileitem)).setOnClickListener(new CheckBoxClickListener());
	}

	private void setFileType(LanSharedItem lanSharedItem, View view) {
		((ImageView) view.findViewById(R.id.file_img_fileItem)).setBackgroundDrawable(this.m_ipmsgApp.getIconByExtentionName(lanSharedItem.getM_strName(), true, true));
		((TextView) view.findViewById(R.id.name_text_fileItem)).setText(lanSharedItem.getM_strName());
		((TextView) view.findViewById(R.id.size_text_fileItem)).setText(Public_Tools.getShortSize(lanSharedItem.getM_lSize()));
		((TextView) view.findViewById(R.id.lasttime_text_fileItem)).setText(Public_Tools.lastModifiedTime(lanSharedItem.getM_lTime()));
	}

	private void setFolderType(LanSharedItem lanSharedItem, View view) {
		((TextView) view.findViewById(R.id.size_text_fileItem)).setText(Public_Tools.getShortSize(lanSharedItem.getM_lSize()));
		((ImageView) view.findViewById(R.id.file_img_fileItem)).setImageBitmap(null);
		((ImageView) view.findViewById(R.id.file_img_fileItem)).setBackgroundResource(R.drawable.folder_icon);
		((TextView) view.findViewById(R.id.name_text_fileItem)).setText(lanSharedItem.getM_strName());
		((TextView) view.findViewById(R.id.lasttime_text_fileItem)).setText(Public_Tools.lastModifiedTime(lanSharedItem.getM_lTime()));
	}

	private void setThirdTypeValue(int i, LanSharedItem lanSharedItem, View view) {
		((ImageView) view.findViewById(R.id.file_img_fileItem)).setImageBitmap(null);
		((ImageView) view.findViewById(R.id.file_img_fileItem)).setBackgroundResource(R.drawable.pic);
		((TextView) view.findViewById(R.id.name_text_fileItem)).setText(lanSharedItem.getM_strName());
		((TextView) view.findViewById(R.id.size_text_fileItem)).setText(Public_Tools.getShortSize(lanSharedItem.getM_lSize()));
		((TextView) view.findViewById(R.id.lasttime_text_fileItem)).setText(Public_Tools.lastModifiedTime(lanSharedItem.getM_lTime()));
		((TextView) view.findViewById(R.id.count_text_fileItem)).setText(this.m_lanShareFileList.get(i).getM_strFrom());
		((TextView) view.findViewById(R.id.count_text_fileItem)).setTag(this.m_lanShareFileList.get(i).getM_strFromMac());
		((TextView) view.findViewById(R.id.count_text_fileItem)).setOnClickListener(new FromTextVClickListener());
		((TextView) view.findViewById(R.id.count_text_fileItem)).getPaint().setFlags(8);
		((TextView) view.findViewById(R.id.count_text_fileItem)).setTextColor(this.m_context.getResources().getColor(R.color.textcolor4));
		TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(100, -2);
		layoutParams.column = 1;
		layoutParams.leftMargin = 6;
		layoutParams.gravity = 19;
		((TextView) view.findViewById(R.id.name_text_fileItem)).setLayoutParams(layoutParams);
		((CheckBox) view.findViewById(R.id.file_select_box_fileitem)).setChecked(this.m_lanShareFileList.get(i).isM_bIsCheck());
		((CheckBox) view.findViewById(R.id.file_select_box_fileitem)).setTag(i + "");
		((CheckBox) view.findViewById(R.id.file_select_box_fileitem)).setOnClickListener(new CheckBoxClickListener());
	}

	private class FromTextVClickListener implements View.OnClickListener {
		private FromTextVClickListener() {
		}

		@Override // android.view.View.OnClickListener
		public void onClick(View view) {
			((LanShareActivity) LanShareFileAdapter.this.m_context).locationDestHostSubQuery(view.getTag().toString());
		}
	}

	private class CheckBoxClickListener implements View.OnClickListener {
		private CheckBoxClickListener() {
		}

		@Override // android.view.View.OnClickListener
		public void onClick(View view) {
			LanShareFileAdapter.this.checkFileOperate(view, Integer.parseInt(view.getTag().toString()));
			((LanShareActivity) LanShareFileAdapter.this.m_context).showPopupOperate();
		}
	}

	public void checkFileOperate(View view, int i) {
		CheckBox checkBox = (CheckBox) view;
		checkBox.setChecked(!this.m_lanShareFileList.get(i).isM_bIsCheck());
		this.m_lanShareFileList.get(i).setM_bIsCheck(!this.m_lanShareFileList.get(i).isM_bIsCheck());
		if (checkBox.isChecked()) {
			((LanShareActivity) this.m_context).getM_selectedFilesList().add(this.m_lanShareFileList.get(i));
		} else if (((LanShareActivity) this.m_context).getM_selectedFilesList().contains(this.m_lanShareFileList.get(i))) {
			((LanShareActivity) this.m_context).getM_selectedFilesList().remove(this.m_lanShareFileList.get(i));
		}
	}
}

