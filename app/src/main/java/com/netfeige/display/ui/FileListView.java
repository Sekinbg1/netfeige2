package com.netfeige.display.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.netfeige.R;
import com.netfeige.common.Category;
import com.netfeige.common.FileInfo;
import com.netfeige.common.Global;
import com.netfeige.common.HostInformation;
import com.netfeige.common.ImagePreview;
import com.netfeige.common.Music;
import com.netfeige.common.Public_Tools;
import com.netfeige.common.SDCardInfo;
import com.netfeige.display.data.CategoryAdpter;
import com.netfeige.display.data.FileAdapter;
import com.netfeige.display.data.ImageAdapter;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.ui.IpmsgActivity;
import com.netfeige.display.ui.dialog.DeleteDialog;
import com.netfeige.dlna.ContentTree;
import com.netfeige.enums.FileAccessAuth;
import com.netfeige.filemanager.AsyncLoadedImage;
import com.netfeige.filemanager.Catalogue;
import com.netfeige.filemanager.FileManager;
import com.netfeige.util.SDCardUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Vector;
import org.teleal.cling.model.ServiceReference;

/* JADX INFO: loaded from: classes.dex */
public class FileListView extends ListView implements AdapterView.OnItemClickListener {
	private static final int m_nBitmapHeight = 60;
	private static final int m_nBitmapWitch = 60;
	private static final int m_nOffsetOnDrag = 80;
	public static Vector<String> m_vecLimitedApk;
	public static Vector<String> m_vecLimitedAudio;
	public static Vector<String> m_vecLimitedDocument;
	public static Vector<String> m_vecLimitedImage;
	public static Vector<String> m_vecLimitedVideo;
	private boolean m_bIsFilerDown;
	private boolean m_bIsInDeleteArea;
	public Bitmap m_bitmapDrag;
	private CategoryAdpter m_categoryAdpter;
	private Context m_context;
	private Dialog m_dialogDeleteNotify;
	private final float m_fFlingDistance;
	private final float m_fFlingHeight;
	private FileAdapter m_fileAdapter;
	private GestureDetector m_gestureDetector;
	private ImageView m_imageVDrag;
	private ImageView m_imageV_deleteFile;
	private IpmsgActivity m_ipmsgActivity;
	private int m_nCurrentCount;
	public int m_nCurrentFileIndex;
	private final int m_nDragndropBackgroundColor;
	private int m_nFilerDownHeight;
	private int m_nFirst;
	private int m_nGragX;
	private int m_nGragY;
	private final int m_nHeightOff;
	private final int m_nInCatalogue;
	private final int m_nOffsetX;
	private final int m_nOffsetY;
	private int m_nStartX;
	private int m_nStartY;
	private final int m_nWidthOff;
	private Rect m_rectTemp;
	private String m_strCurrentCategoryPath;
	private final String[] m_strLimitedApk;
	private final String[] m_strLimitedAudio;
	private final String[] m_strLimitedDocument;
	private final String[] m_strLimitedImage;
	private final String[] m_strLimitedVideo;
	private WindowManager m_windowManager;
	private WindowManager.LayoutParams m_windowParams;

	@Override // android.widget.AdapterView.OnItemClickListener
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
	}

	public FileListView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		this.m_context = null;
		this.m_strCurrentCategoryPath = ServiceReference.DELIMITER;
		this.m_nCurrentFileIndex = -1;
		this.m_nGragX = 0;
		this.m_nGragY = 0;
		this.m_nDragndropBackgroundColor = 0;
		this.m_rectTemp = new Rect();
		this.m_nInCatalogue = 2;
		this.m_nFirst = 0;
		this.m_nCurrentCount = 9;
		this.m_fFlingDistance = 50.0f;
		this.m_fFlingHeight = 50.0f;
		this.m_nOffsetX = 66;
		this.m_nOffsetY = 80;
		this.m_strLimitedImage = new String[]{"jpg", "jpeg", "png", "bmp", "gif", "JPG", "JPEG", "PNG", "BMP", "GIF"};
		this.m_strLimitedAudio = new String[]{"mp3", "wma", "MP3", "WMA"};
		this.m_strLimitedVideo = new String[]{"mp4", "rmvb", "3gp", "avi", "MP4", "RMVB", "3GP", "AVI", "wmv", "WMV"};
		this.m_strLimitedDocument = new String[]{"txt", "TXT", "doc", "DOC", "docx", "DOCX", "xls", "XLS", "xlsx", "XLSX", "ppt", "PPT", "pps", "PPS", "pptx", "PPTX", "pdf", "PDF"};
		this.m_strLimitedApk = new String[]{"apk", "APK"};
		this.m_nFilerDownHeight = R.styleable.AppCompatTheme_windowNoTitle;
		this.m_bIsFilerDown = false;
		this.m_nWidthOff = 75;
		this.m_nHeightOff = 350;
		this.m_imageV_deleteFile = null;
		this.m_bIsInDeleteArea = false;
		this.m_context = context;
		this.m_fileAdapter = new FileAdapter(context);
		CategoryAdpter categoryAdpter = new CategoryAdpter(context);
		this.m_categoryAdpter = categoryAdpter;
		setAdapter((ListAdapter) categoryAdpter);
		setOnItemClickListener(this);
		setLongClickable(true);
		this.m_gestureDetector = new GestureDetector(context, new ListViewGestureListener());
		setOnTouchListener(new View.OnTouchListener() { // from class: com.netfeige.display.ui.FileListView.1
			@Override // android.view.View.OnTouchListener
			public boolean onTouch(View view, MotionEvent motionEvent) {
				return FileListView.this.m_gestureDetector.onTouchEvent(motionEvent);
			}
		});
		initLimitedVector();
		this.m_ipmsgActivity = (IpmsgActivity) context;
		this.m_nFilerDownHeight = Public_Tools.dip2px(this.m_context, this.m_nFilerDownHeight);
	}

	private void initLimitedVector() {
		Vector<String> vector = new Vector<>();
		m_vecLimitedImage = vector;
		forAddCategory(this.m_strLimitedImage, vector);
		Vector<String> vector2 = new Vector<>();
		m_vecLimitedAudio = vector2;
		forAddCategory(this.m_strLimitedAudio, vector2);
		Vector<String> vector3 = new Vector<>();
		m_vecLimitedVideo = vector3;
		forAddCategory(this.m_strLimitedVideo, vector3);
		Vector<String> vector4 = new Vector<>();
		m_vecLimitedDocument = vector4;
		forAddCategory(this.m_strLimitedDocument, vector4);
		Vector<String> vector5 = new Vector<>();
		m_vecLimitedApk = vector5;
		forAddCategory(this.m_strLimitedApk, vector5);
	}

	private void forAddCategory(String[] strArr, Vector<String> vector) {
		for (String str : strArr) {
			vector.add(str);
		}
	}

	public String getCurrentCategoryPath() {
		return this.m_strCurrentCategoryPath;
	}

	public void setCurrentCategoryPath(String str) {
		this.m_strCurrentCategoryPath = str;
	}

	public FileAdapter getFileAdapter() {
		return this.m_fileAdapter;
	}

	public CategoryAdpter getCategoryAdpter() {
		return this.m_categoryAdpter;
	}

	public int getStartX() {
		return this.m_nStartX;
	}

	public int getStartY() {
		return this.m_nStartY;
	}

	public boolean moveBack() throws Exception {
		Global.g_isAsyncLoadedImage = true;
		if (Global.g_nCompressed == 1) {
			this.m_ipmsgActivity.getM_compress_progressBar().setVisibility(8);
		}
		moveBackShowCatalogue();
		return true;
	}

	private void moveBackShowCatalogue() throws Exception {
		goneText();
		if (Catalogue.categoryStack.isEmpty()) {
			return;
		}
		String strPop = Catalogue.categoryStack.pop();
		this.m_ipmsgActivity.getHashMapKeepPosition().put(strPop, Integer.valueOf(IpmsgActivity.s_nCurrentposition));
		if (strPop.equals("img/")) {
			backSet();
			return;
		}
		if (strPop.equals("audio/")) {
			backSet();
			return;
		}
		if (strPop.equals("video/")) {
			backSet();
			return;
		}
		if (strPop.equals("apk/")) {
			backSet();
			return;
		}
		if (strPop.equals("document/")) {
			backSet();
			return;
		}
		if (strPop.equals(ServiceReference.DELIMITER)) {
			backSet();
			return;
		}
		if (strPop.equals("feige/")) {
			backSet();
		} else if (strPop.equals("sdcard/")) {
			backSet();
		} else {
			moveBackCategory(Catalogue.categoryStack.peek());
		}
	}

	public void backSet() {
		setAdapter((ListAdapter) this.m_categoryAdpter);
		Global.g_whatFolder = null;
		this.m_ipmsgActivity.getM_btnDragbar().setText("");
		Global.g_filePath.clear();
		if (IpmsgActivity.m_popupWindowSend.isShowing()) {
			IpmsgActivity.m_popupWindowSend.dismiss();
		}
		Global.g_bInRoot = true;
		if (this.m_ipmsgActivity.getDragEnum() == IpmsgActivity.DragEnum.TOP) {
			this.m_ipmsgActivity.getBtnMore().setVisibility(8);
			this.m_ipmsgActivity.getBtnParentFolder().setVisibility(8);
		}
		if (Global.g_bWaitPaste == 1 && this.m_ipmsgActivity.getPopupWindowPaste().isShowing()) {
			this.m_ipmsgActivity.getPopupWindowPaste().dismiss();
		}
		if (Global.g_bWaitPaste == 0) {
			Global.g_pastePaths.clear();
		}
	}

	public void backToot() {
		backSet();
		goneText();
		Catalogue.categoryStack.clear();
	}

	public void goneText() {
		Global.g_bIsInEmptyDir = false;
		this.m_ipmsgActivity.getTextVContent().setVisibility(8);
	}

	public void loadingImage(int i, int i2, Context context) {
		new AsyncLoadedImage(i, i2, context).execute(new Object[0]);
	}

	public void moveToCategory(String str) throws Exception {
		moveToBasic(str, null);
		this.m_ipmsgActivity.getM_btnDragbar().setText(Public_Tools.subductionPath(this.m_strCurrentCategoryPath));
		if (!Catalogue.categoryStack.isEmpty()) {
			this.m_ipmsgActivity.getHashMapKeepPosition().put(Catalogue.categoryStack.peek(), Integer.valueOf(IpmsgActivity.s_nCurrentposition));
		}
		Catalogue.categoryStack.push(this.m_strCurrentCategoryPath);
	}

	public void reLoadCategory(String str) throws Exception {
		int length;
		File[] fileArrListFiles = new File(str).listFiles();
		this.m_fileAdapter.getFileList().clear();
		ImageAdapter.s_imageList.clear();
		IpmsgApplication.g_arrMusicList.clear();
		IpmsgApplication.g_arrVideoList.clear();
		Public_Tools.backForward(this.m_fileAdapter.getFileList());
		if (fileArrListFiles != null && (length = fileArrListFiles.length) > 0) {
			AddDataInCategory(fileArrListFiles, length);
		}
		Public_Tools.fileCompare(getFileAdapter().getFileList());
		Public_Tools.musicCompare(IpmsgApplication.g_arrMusicList);
		Public_Tools.musicCompare(IpmsgApplication.g_arrVideoList);
		Public_Tools.imageCompare(ImageAdapter.s_imageList);
		this.m_fileAdapter.notifyDataSetChanged();
	}

	public void moveToCategory(String str, String str2) throws Exception {
		moveToBasic(str, str2);
		if (Global.g_whatFolder == Category.feige) {
			this.m_ipmsgActivity.getM_btnDragbar().setText("FeigeDownload/");
		} else {
			this.m_ipmsgActivity.getM_btnDragbar().setText(str2);
		}
		Catalogue.categoryStack.push(str2);
	}

	private void moveToBasic(String str, String str2) {
		Global.g_bInCategoryDir = true;
		if (Global.g_nCompressed == 1) {
			this.m_ipmsgActivity.getM_compress_progressBar().setVisibility(8);
		}
		if (Global.g_bWaitPaste == 0) {
			Global.g_pastePaths.clear();
		}
		Global.g_filePath.clear();
		if (IpmsgActivity.m_popupWindowSend.isShowing()) {
			IpmsgActivity.m_popupWindowSend.dismiss();
		}
		if (str == null && str2 != null && str2.equals("sdcard/")) {
			enterSDTopDir();
			return;
		}
		this.m_strCurrentCategoryPath = str;
		if (!str.endsWith(File.separator)) {
			this.m_strCurrentCategoryPath += File.separator;
		}
		try {
			if (this.m_strCurrentCategoryPath.equals(Public_Tools.getDefaultDownloadPath() + ServiceReference.DELIMITER)) {
				Global.g_bInFeige = true;
			} else {
				Global.g_bInFeige = false;
			}
		} catch (Throwable e) {
			e.printStackTrace();
			Global.g_bInFeige = false;
		}
		addDataForCategory(new File(this.m_strCurrentCategoryPath), this.m_strCurrentCategoryPath, str2);
	}

	private void enterSDTopDir() {
		if (Global.g_bWaitPaste == 1 && this.m_ipmsgActivity.getPopupWindowPaste().isShowing()) {
			this.m_ipmsgActivity.getPopupWindowPaste().dismiss();
		}
		Global.g_bInFeige = false;
		this.m_fileAdapter.getFileList().clear();
		ImageAdapter.s_imageList.clear();
		IpmsgApplication.g_arrMusicList.clear();
		IpmsgApplication.g_arrVideoList.clear();
		Public_Tools.backForward(this.m_fileAdapter.getFileList());
		ArrayList<SDCardInfo> sDCardInfo = SDCardUtil.getSDCardInfo(this.m_context);
		for (int i = 0; i < sDCardInfo.size(); i++) {
			if (sDCardInfo.get(i).isMounted()) {
				File file = new File(sDCardInfo.get(i).getMountPoint());
				FileInfo fileInfo = new FileInfo();
				fileInfo.setFloder(file.isDirectory());
				fileInfo.setName(file.getName());
				fileInfo.setPath(file.getAbsolutePath());
				fileInfo.setSize(file.length());
				fileInfo.setLastTime(file.lastModified());
				this.m_fileAdapter.getFileList().add(fileInfo);
			}
		}
		this.m_fileAdapter.notifyDataSetChanged();
	}

	private void moveBackCategory(String str) throws Exception {
		Global.g_filePath.clear();
		if (IpmsgActivity.m_popupWindowSend.isShowing()) {
			IpmsgActivity.m_popupWindowSend.dismiss();
		}
		this.m_strCurrentCategoryPath = str;
		this.m_ipmsgActivity.getM_btnDragbar().setText(Public_Tools.subductionPath(str));
		String str2;
		try {
			str2 = Public_Tools.getDefaultDownloadPath() + ServiceReference.DELIMITER;
		} catch (Throwable e) {
			e.printStackTrace();
			str2 = "";
		}
		if (!this.m_strCurrentCategoryPath.endsWith(File.separator)) {
			this.m_strCurrentCategoryPath += File.separator;
		}
		if (this.m_strCurrentCategoryPath.equals("img/")) {
			backCategory(FileManager.m_imageFolderList, "img/");
			return;
		}
		if (this.m_strCurrentCategoryPath.equals("audio/")) {
			backCategory(FileManager.m_audioFolderList, "audio/");
			return;
		}
		if (this.m_strCurrentCategoryPath.equals("video/")) {
			backCategory(FileManager.m_videoFolderList, "video/");
			return;
		}
		if (this.m_strCurrentCategoryPath.equals("document/")) {
			backCategory(FileManager.m_documentFolderList, "document/");
			return;
		}
		if (this.m_strCurrentCategoryPath.equals("apk/")) {
			backCategory(FileManager.m_apkFolderList, "apk/");
			return;
		}
		if (this.m_strCurrentCategoryPath.equals("feige/")) {
			File file = new File(str2);
			this.m_ipmsgActivity.getM_btnDragbar().setText("FeigeDownload/");
			addDataForCategory(file, this.m_strCurrentCategoryPath, null);
		} else {
			if (this.m_strCurrentCategoryPath.equals("sdcard/")) {
				enterSDTopDir();
				return;
			}
			if (this.m_strCurrentCategoryPath.equals(str2)) {
				Global.g_bInFeige = true;
			} else {
				Global.g_bInFeige = false;
			}
			addDataForCategory(new File(this.m_strCurrentCategoryPath), this.m_strCurrentCategoryPath, null);
		}
	}

	private void backCategory(ArrayList<String> arrayList, String str) {
		if (Global.g_bWaitPaste == 1 && this.m_ipmsgActivity.getPopupWindowPaste().isShowing()) {
			this.m_ipmsgActivity.getPopupWindowPaste().dismiss();
		}
		Global.g_bInCategoryDir = false;
		if (Global.g_nCompressed == 1) {
			this.m_ipmsgActivity.showPregressBar();
		}
		getFileAdapter().getFileList().clear();
		Public_Tools.backForward(getFileAdapter().getFileList());
		int size = arrayList.size();
		for (int i = 0; i < size; i++) {
			FileInfo fileInfo = new FileInfo();
			String str2 = arrayList.get(i);
			String folderName = Public_Tools.getFolderName(str2);
			fileInfo.setPath(str2);
			fileInfo.setName(folderName);
			fileInfo.setFloder(true);
			fileInfo.setLastTime(new File(str2).lastModified());
			this.m_fileAdapter.getFileList().add(fileInfo);
			Public_Tools.fileCompare(this.m_fileAdapter.getFileList());
		}
		setAdapter((ListAdapter) this.m_fileAdapter);
		recoverPosition(str);
		getFileAdapter().notifyDataSetChanged();
	}

	private void recoverPosition(String str, String str2) {
		if (str2 != null) {
			recoverPosition(str2);
		} else {
			recoverPosition(str);
		}
	}

	private void recoverPosition(String str) {
		if (this.m_ipmsgActivity.getHashMapKeepPosition().containsKey(str)) {
			int iIntValue = this.m_ipmsgActivity.getHashMapKeepPosition().get(str).intValue();
			setSelection(iIntValue);
			if (Global.g_isAsyncLoadedImage) {
				this.m_nFirst = iIntValue;
				loadingImage(iIntValue, this.m_nCurrentCount + iIntValue, this.m_context);
				return;
			}
			return;
		}
		if (Global.g_isAsyncLoadedImage) {
			this.m_nFirst = 0;
			loadingImage(0, this.m_nCurrentCount + 0, this.m_context);
		}
	}

	private void addData(File file, String str, String str2) {
		File[] fileArrListFiles = file.listFiles();
		this.m_fileAdapter.getFileList().clear();
		ImageAdapter.s_imageList.clear();
		IpmsgApplication.g_arrMusicList.clear();
		IpmsgApplication.g_arrVideoList.clear();
		Public_Tools.backForward(this.m_fileAdapter.getFileList());
		if (fileArrListFiles != null && fileArrListFiles.length > 0) {
			AddDataInCategory(fileArrListFiles, fileArrListFiles.length);
		}
		if (this.m_fileAdapter.getFileList().size() <= 1) {
			showText();
		}
		setAdapter((ListAdapter) this.m_fileAdapter);
		recoverPosition(str, str2);
		this.m_fileAdapter.notifyDataSetChanged();
	}

	private void AddDataInCategory(File[] fileArr, int i) {
		for (int i2 = 0; i2 < i; i2++) {
			try {
				String name = fileArr[i2].getName();
				if (Global.g_whatFolder != null) {
					int i3 = AnonymousClass2.$SwitchMap$com$netfeige$common$Category[Global.g_whatFolder.ordinal()];
					if (i3 != 1) {
						if (i3 != 2) {
							if (i3 != 3) {
								if (i3 != 4) {
									if (i3 == 5) {
										if (Public_Tools.isApkFile(name)) {
											inFor(fileArr, i2, name);
										}
									} else {
										inFor(fileArr, i2, name);
									}
								} else if (Public_Tools.isDocumentFile(name)) {
									inFor(fileArr, i2, name);
								}
							} else if (Public_Tools.isVideoFile(name)) {
								inFor(fileArr, i2, name);
							}
						} else if (Public_Tools.isAudioFile(name)) {
							inFor(fileArr, i2, name);
						}
					} else if (Public_Tools.isImageFile(name)) {
						inFor(fileArr, i2, name);
					}
				} else {
					inFor(fileArr, i2, name);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

	/* JADX INFO: renamed from: com.netfeige.display.ui.FileListView$2, reason: invalid class name */
	static /* synthetic */ class AnonymousClass2 {
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

	private void inFor(File[] fileArr, int i, String str) {
		if ((fileArr[i].isFile() || !FileManager.hasDot(fileArr[i].getName())) && !fileArr[i].isHidden()) {
			String absolutePath = fileArr[i].getAbsolutePath();
			long jLastModified = fileArr[i].lastModified();
			FileInfo fileInfo = new FileInfo();
			setFileInfo(fileArr, i, str, absolutePath, jLastModified, fileInfo);
			if (Public_Tools.isImageFile(str)) {
				loadImage(str, absolutePath, jLastModified);
			} else if (Public_Tools.isAudioFile(str)) {
				loadMedia(str, absolutePath, jLastModified, IpmsgApplication.g_arrMusicList);
			} else if (Public_Tools.isVideoFile(str)) {
				loadMedia(str, absolutePath, jLastModified, IpmsgApplication.g_arrVideoList);
			}
			this.m_fileAdapter.getFileList().add(fileInfo);
		}
	}

	private void setFileInfo(File[] fileArr, int i, String str, String str2, long j, FileInfo fileInfo) {
		fileInfo.setFloder(fileArr[i].isDirectory());
		fileInfo.setName(str);
		fileInfo.setPath(str2);
		fileInfo.setSize(fileArr[i].length());
		fileInfo.setLastTime(j);
	}

	public void loadImage(String str, String str2, long j) {
		ImageAdapter.s_imageList.add(new ImagePreview(0, str, str2, null, j));
	}

	private void loadMedia(String str, String str2, long j, ArrayList<Music> arrayList) {
		arrayList.add(new Music(0, str, str2, -1L, j));
	}

	private void addDataForCategory(File file, String str, String str2) {
		if (Global.g_whatFolder == Category.feige || Global.g_whatFolder == Category.memory || Global.g_whatFolder == Category.sdcard) {
			if ((Public_Tools.authority(file) == FileAccessAuth.W_OK || Public_Tools.authority(file) == FileAccessAuth.RW_OK) && Global.g_bWaitPaste == 1) {
				this.m_ipmsgActivity.showPopupWindowPaste(true);
			} else {
				this.m_ipmsgActivity.showPopupWindowPaste(false);
			}
		}
		addData(file, str, str2);
		Public_Tools.fileCompare(getFileAdapter().getFileList());
		Public_Tools.musicCompare(IpmsgApplication.g_arrMusicList);
		Public_Tools.musicCompare(IpmsgApplication.g_arrVideoList);
		Public_Tools.imageCompare(ImageAdapter.s_imageList);
	}

	private void showText() {
		int iDip2px = Public_Tools.dip2px(this.m_context, 75.0f);
		int iDip2px2 = Public_Tools.dip2px(this.m_context, 350.0f);
		if (getTop() < 510) {
			Global.g_bIsInEmptyDir = true;
			this.m_ipmsgActivity.getTextVContent().setVisibility(0);
			this.m_ipmsgActivity.getTextVContent().setPadding(iDip2px, iDip2px2, 0, 0);
			this.m_ipmsgActivity.getTextVContent().bringToFront();
		}
	}

	@Override // android.widget.AbsListView, android.view.View
	public boolean onTouchEvent(MotionEvent motionEvent) {
		if (Global.g_listViewCurrentAdpter == 2 && this.m_imageVDrag != null) {
			this.m_imageV_deleteFile = (ImageView) this.m_ipmsgActivity.findViewById(R.id.delete_file_main);
			HostInformation hostInformation = Global.g_hostInfo;
			Vector<String> vector = Global.g_filePath;
			FileInfo fileInfo = this.m_fileAdapter.fileList.get(this.m_nCurrentFileIndex);
			int action = motionEvent.getAction();
			this.m_nStartX = ((int) motionEvent.getRawX()) - 66;
			this.m_nStartY = ((int) motionEvent.getRawY()) - 80;
			if (action == 1) {
				this.m_imageV_deleteFile.setVisibility(8);
				if (this.m_bIsInDeleteArea) {
					inDeleteArea(vector, fileInfo);
				} else {
					dragSendFiles(this.m_ipmsgActivity, hostInformation, vector, fileInfo, this.m_bIsFilerDown);
				}
				stopDragging();
			} else if (action == 2) {
				ontouchMove(this.m_ipmsgActivity);
				if (((int) motionEvent.getRawY()) < this.m_nFilerDownHeight && this.m_ipmsgActivity.getDragEnum() == IpmsgActivity.DragEnum.TOP) {
					this.m_ipmsgActivity.setDragEnum(IpmsgActivity.DragEnum.MIDDLE_UP);
					this.m_ipmsgActivity.getDragProcess().start();
					this.m_bIsFilerDown = true;
				}
				if (IpmsgActivity.m_popupWindowSend.isShowing()) {
					IpmsgActivity.m_popupWindowSend.dismiss();
				}
			} else if (action == 3) {
				stopDragging();
			}
			return true;
		}
		return super.onTouchEvent(motionEvent);
	}

	private void inDeleteArea(Vector<String> vector, FileInfo fileInfo) {
		if (((IpmsgApplication) ((Activity) this.m_context).getApplication()).g_strDelFilePrompt.equals(ContentTree.VIDEO_ID)) {
			DeleteDialog deleteDialog = new DeleteDialog(this.m_context, fileInfo);
			this.m_dialogDeleteNotify = deleteDialog;
			deleteDialog.show();
		} else {
			if (vector.size() > 0) {
				if (isInner(this.m_fileAdapter.fileList.get(this.m_nCurrentFileIndex).path, vector)) {
					deleteFileInfo(vector);
					Global.g_filePath.clear();
					if (IpmsgActivity.m_popupWindowSend.isShowing()) {
						IpmsgActivity.m_popupWindowSend.dismiss();
						return;
					}
					return;
				}
				deleteFile(fileInfo);
				return;
			}
			deleteFile(fileInfo);
		}
	}

	private void implementOnMove(IpmsgActivity ipmsgActivity, int i, int i2, int i3, int i4, float f, int i5, int i6) {
		dragView(i5, i6);
		if (i5 > i3 && i5 < i4 && i6 > i && i6 < i2) {
			this.m_imageV_deleteFile.setBackgroundResource(R.drawable.delete3);
			this.m_imageV_deleteFile.setVisibility(0);
			this.m_bIsInDeleteArea = true;
		} else {
			if (i6 > this.m_nGragY + (Global.g_nDensityDpi * R.styleable.AppCompatTheme_viewInflaterClass)) {
				this.m_imageV_deleteFile.setBackgroundResource(R.drawable.delete_main);
				this.m_imageV_deleteFile.setVisibility(0);
			} else {
				this.m_imageV_deleteFile.setVisibility(8);
			}
			this.m_bIsInDeleteArea = false;
		}
	}

	private void ontouchMove(IpmsgActivity ipmsgActivity) {
		if (Global.g_hostInfo == null) {
			ipmsgActivity.cancelHighlightDisplay();
			View gridViewItem = ipmsgActivity.getGridViewItem((this.m_nStartX + 66) - 80, (this.m_nStartY + 80) - 80);
			if (gridViewItem != null) {
				gridViewItem.setBackgroundColor(Color.rgb(240, 159, 23));
			}
		}
		implementOnMove(ipmsgActivity, ipmsgActivity.getM_rectWindowArea().bottom - 200, ipmsgActivity.getM_rectWindowArea().bottom, 80, (ipmsgActivity.getM_rectWindowArea().width() / 2) + 80, ipmsgActivity.getStartY(), (this.m_nStartX + 66) - 80, (this.m_nStartY + 80) - 80);
	}

	private void dragSendFiles(IpmsgActivity ipmsgActivity, HostInformation hostInformation, Vector<String> vector, FileInfo fileInfo, boolean z) {
		if (hostInformation == null) {
			if (vector.size() > 0) {
				sendFiles(ipmsgActivity, vector, z);
				return;
			} else {
				sendFile(ipmsgActivity, fileInfo, z);
				return;
			}
		}
		if (vector.size() > 0) {
			sendFilesHasHost(ipmsgActivity, hostInformation, vector, z);
		} else {
			sendFileHasHost(ipmsgActivity, hostInformation, fileInfo, z);
		}
	}

	private void sendFileHasHost(IpmsgActivity ipmsgActivity, HostInformation hostInformation, FileInfo fileInfo, boolean z) {
		if (Global.g_whatFolder != null) {
			int i = AnonymousClass2.$SwitchMap$com$netfeige$common$Category[Global.g_whatFolder.ordinal()];
			if (i == 1) {
				implementHaveHostInfo(ipmsgActivity, hostInformation, fileInfo, m_vecLimitedImage, z);
				return;
			}
			if (i == 2) {
				implementHaveHostInfo(ipmsgActivity, hostInformation, fileInfo, m_vecLimitedAudio, z);
				return;
			}
			if (i == 3) {
				implementHaveHostInfo(ipmsgActivity, hostInformation, fileInfo, m_vecLimitedVideo, z);
				return;
			}
			if (i == 4) {
				implementHaveHostInfo(ipmsgActivity, hostInformation, fileInfo, m_vecLimitedDocument, z);
			} else if (i == 5) {
				implementHaveHostInfo(ipmsgActivity, hostInformation, fileInfo, m_vecLimitedApk, z);
			} else {
				implementHaveHostInfo(ipmsgActivity, hostInformation, fileInfo, (Vector<String>) null, z);
			}
		}
	}

	private void sendFilesHasHost(IpmsgActivity ipmsgActivity, HostInformation hostInformation, Vector<String> vector, boolean z) {
		if (Global.g_whatFolder != null) {
			int i = AnonymousClass2.$SwitchMap$com$netfeige$common$Category[Global.g_whatFolder.ordinal()];
			if (i == 1) {
				implementHaveHostInfo(ipmsgActivity, hostInformation, vector, m_vecLimitedImage, z);
				return;
			}
			if (i == 2) {
				implementHaveHostInfo(ipmsgActivity, hostInformation, vector, m_vecLimitedAudio, z);
				return;
			}
			if (i == 3) {
				implementHaveHostInfo(ipmsgActivity, hostInformation, vector, m_vecLimitedVideo, z);
				return;
			}
			if (i == 4) {
				implementHaveHostInfo(ipmsgActivity, hostInformation, vector, m_vecLimitedDocument, z);
			} else if (i == 5) {
				implementHaveHostInfo(ipmsgActivity, hostInformation, vector, m_vecLimitedApk, z);
			} else {
				implementHaveHostInfo(ipmsgActivity, hostInformation, vector, (Vector<String>) null, z);
			}
		}
	}

	private void sendFile(IpmsgActivity ipmsgActivity, FileInfo fileInfo, boolean z) {
		if (Global.g_whatFolder != null) {
			int i = AnonymousClass2.$SwitchMap$com$netfeige$common$Category[Global.g_whatFolder.ordinal()];
			if (i == 1) {
				implementNoHostInfo(ipmsgActivity, fileInfo, m_vecLimitedImage, z);
				return;
			}
			if (i == 2) {
				implementNoHostInfo(ipmsgActivity, fileInfo, m_vecLimitedAudio, z);
				return;
			}
			if (i == 3) {
				implementNoHostInfo(ipmsgActivity, fileInfo, m_vecLimitedVideo, z);
				return;
			}
			if (i == 4) {
				implementNoHostInfo(ipmsgActivity, fileInfo, m_vecLimitedDocument, z);
			} else if (i == 5) {
				implementNoHostInfo(ipmsgActivity, fileInfo, m_vecLimitedApk, z);
			} else {
				implementNoHostInfo(ipmsgActivity, fileInfo, (Vector<String>) null, z);
			}
		}
	}

	private void sendFiles(IpmsgActivity ipmsgActivity, Vector<String> vector, boolean z) {
		if (Global.g_whatFolder != null) {
			int i = AnonymousClass2.$SwitchMap$com$netfeige$common$Category[Global.g_whatFolder.ordinal()];
			if (i == 1) {
				implementNoHostInfo(ipmsgActivity, vector, m_vecLimitedImage, z);
				return;
			}
			if (i == 2) {
				implementNoHostInfo(ipmsgActivity, vector, m_vecLimitedAudio, z);
				return;
			}
			if (i == 3) {
				implementNoHostInfo(ipmsgActivity, vector, m_vecLimitedVideo, z);
				return;
			}
			if (i == 4) {
				implementNoHostInfo(ipmsgActivity, vector, m_vecLimitedDocument, z);
			} else if (i == 5) {
				implementNoHostInfo(ipmsgActivity, vector, m_vecLimitedApk, z);
			} else {
				implementNoHostInfo(ipmsgActivity, vector, (Vector<String>) null, z);
			}
		}
	}

	public boolean isInner(String str, Vector<String> vector) {
		boolean zEquals = false;
		for (int i = 0; i < vector.size() && !(zEquals = vector.get(i).equals(str)); i++) {
		}
		return zEquals;
	}

	private void implementHaveHostInfo(IpmsgActivity ipmsgActivity, HostInformation hostInformation, Vector<String> vector, Vector<String> vector2, boolean z) {
		try {
			boolean zIsInner = isInner(this.m_fileAdapter.fileList.get(this.m_nCurrentFileIndex).path, vector);
			if (this.m_nStartY <= (getTop() - ipmsgActivity.m_btnDragbar.getHeight()) - 25 && zIsInner) {
				ipmsgActivity.dragAnimation(hostInformation);
				try {
					Thread.sleep(1L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				ipmsgActivity.fileDragLocation(this.m_nStartY + 80, hostInformation, vector, vector2);
				for (int i = 0; i < getFileAdapter().getFileList().size(); i++) {
					getFileAdapter().getFileList().get(i).setCheck(false);
				}
				getFileAdapter().notifyDataSetChanged();
				Global.g_filePath.clear();
				if (IpmsgActivity.m_popupWindowSend.isShowing()) {
					IpmsgActivity.m_popupWindowSend.dismiss();
					return;
				}
				return;
			}
			ipmsgActivity.backAnimation(this.m_fileAdapter.fileList.get(this.m_nCurrentFileIndex).path, this.m_nGragX, this.m_nGragY, this.m_nStartX, this.m_nStartY, z);
		} catch (NullPointerException e2) {
			e2.printStackTrace();
		} catch (Exception e3) {
			e3.printStackTrace();
		}
	}

	private void implementHaveHostInfo(IpmsgActivity ipmsgActivity, HostInformation hostInformation, FileInfo fileInfo, Vector<String> vector, boolean z) {
		try {
			if (this.m_nStartY <= (getTop() - ipmsgActivity.m_btnDragbar.getHeight()) - 25) {
				ipmsgActivity.dragAnimation(hostInformation);
				try {
					Thread.sleep(1L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				ipmsgActivity.fileDragLocation(this.m_nStartY + 80, hostInformation, this.m_fileAdapter.fileList.get(this.m_nCurrentFileIndex).path, vector);
				return;
			}
			ipmsgActivity.backAnimation(fileInfo.getPath(), this.m_nGragX, this.m_nGragY, this.m_nStartX, this.m_nStartY, z);
		} catch (NullPointerException e2) {
			e2.printStackTrace();
		} catch (Exception e3) {
			e3.printStackTrace();
		}
	}

	private void implementNoHostInfo(IpmsgActivity ipmsgActivity, Vector<String> vector, Vector<String> vector2, boolean z) {
		try {
			if (!(isInner(this.m_fileAdapter.fileList.get(this.m_nCurrentFileIndex).path, vector) ? ipmsgActivity.fileDragLocation((this.m_nStartX + 66) - 80, (this.m_nStartY + 80) - 80, vector, vector2) : false)) {
				ipmsgActivity.backAnimation(this.m_fileAdapter.fileList.get(this.m_nCurrentFileIndex).path, this.m_nGragX, this.m_nGragY, this.m_nStartX, this.m_nStartY, z);
				return;
			}
			for (int i = 0; i < getFileAdapter().getFileList().size(); i++) {
				getFileAdapter().getFileList().get(i).setCheck(false);
			}
			getFileAdapter().notifyDataSetChanged();
			Global.g_filePath.clear();
			if (IpmsgActivity.m_popupWindowSend.isShowing()) {
				IpmsgActivity.m_popupWindowSend.dismiss();
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	private void implementNoHostInfo(IpmsgActivity ipmsgActivity, FileInfo fileInfo, Vector<String> vector, boolean z) {
		try {
			if (ipmsgActivity.fileDragLocation((this.m_nStartX + 66) - 80, (this.m_nStartY + 80) - 80, this.m_fileAdapter.fileList.get(this.m_nCurrentFileIndex).path, vector)) {
				return;
			}
			ipmsgActivity.backAnimation(fileInfo.getPath(), this.m_nGragX, this.m_nGragY, this.m_nStartX, this.m_nStartY, z);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	public void deleteFileInfo(FileInfo fileInfo) {
		try {
			if (FileManager.getIFileManager(this.m_context).deleteFile(new File(fileInfo.path))) {
				this.m_fileAdapter.fileList.remove(fileInfo);
				Public_Tools.isContain(fileInfo.getPath());
				this.m_fileAdapter.notifyDataSetChanged();
			} else {
				Toast.makeText(this.m_context, R.string.no_delete, 0).show();
			}
		} catch (Exception unused) {
			Toast.makeText(this.m_context, R.string.no_delete, 0).show();
		}
	}

	public void deleteFile(FileInfo fileInfo) {
		try {
			if (fileInfo.isFloder) {
				deleteFolder(fileInfo);
			} else {
				deleteFileInfo(fileInfo);
			}
			if (Global.g_pastePaths.contains(fileInfo.getPath())) {
				Global.g_pastePaths.remove(fileInfo.getPath());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void deleteFolder(FileInfo fileInfo) {
		if (Global.g_whatFolder != null) {
			int i = AnonymousClass2.$SwitchMap$com$netfeige$common$Category[Global.g_whatFolder.ordinal()];
			if (i == 1) {
				deleteImage(fileInfo);
				return;
			}
			if (i == 2) {
				deleteAudio(fileInfo);
				return;
			}
			if (i == 3) {
				deleteVideo(fileInfo);
				return;
			}
			if (i == 4) {
				deleteDocument(fileInfo);
			} else if (i == 5) {
				deleteApk(fileInfo);
			} else {
				deleteFileInfo(fileInfo);
			}
		}
	}

	private void deleteImage(FileInfo fileInfo) {
		File file = new File(fileInfo.path);
		File[] fileArrListFiles = file.listFiles();
		int i = 0;
		for (int i2 = 0; i2 < fileArrListFiles.length; i2++) {
			if (Public_Tools.isImageFile(fileArrListFiles[i2].getName())) {
				FileManager.getIFileManager(this.m_context).deleteFile(fileArrListFiles[i2]);
				i++;
			}
		}
		if (i == fileArrListFiles.length) {
			FileManager.getIFileManager(this.m_context).deleteFile(file);
		}
		getFileAdapter().getFileList().remove(fileInfo);
		getFileAdapter().notifyDataSetChanged();
		FileManager.m_imageFolderList.remove(fileInfo.getPath());
		Global.g_bIsUpdateSQLite = true;
	}

	private void deleteAudio(FileInfo fileInfo) {
		File file = new File(fileInfo.path);
		File[] fileArrListFiles = file.listFiles();
		int i = 0;
		for (int i2 = 0; i2 < fileArrListFiles.length; i2++) {
			if (Public_Tools.isAudioFile(fileArrListFiles[i2].getName())) {
				FileManager.getIFileManager(this.m_context).deleteFile(fileArrListFiles[i2]);
				i++;
			}
		}
		if (i == fileArrListFiles.length) {
			FileManager.getIFileManager(this.m_context).deleteFile(file);
		}
		getFileAdapter().getFileList().remove(fileInfo);
		getFileAdapter().notifyDataSetChanged();
		FileManager.m_audioFolderList.remove(fileInfo.getPath());
		Global.g_bIsUpdateSQLite = true;
	}

	private void deleteVideo(FileInfo fileInfo) {
		File file = new File(fileInfo.path);
		File[] fileArrListFiles = file.listFiles();
		int i = 0;
		for (int i2 = 0; i2 < fileArrListFiles.length; i2++) {
			if (Public_Tools.isVideoFile(fileArrListFiles[i2].getName())) {
				FileManager.getIFileManager(this.m_context).deleteFile(fileArrListFiles[i2]);
				i++;
			}
		}
		if (i == fileArrListFiles.length) {
			FileManager.getIFileManager(this.m_context).deleteFile(file);
		}
		getFileAdapter().getFileList().remove(fileInfo);
		getFileAdapter().notifyDataSetChanged();
		FileManager.m_videoFolderList.remove(fileInfo.getPath());
		Global.g_bIsUpdateSQLite = true;
	}

	private void deleteDocument(FileInfo fileInfo) {
		File file = new File(fileInfo.path);
		File[] fileArrListFiles = file.listFiles();
		int i = 0;
		for (int i2 = 0; i2 < fileArrListFiles.length; i2++) {
			if (Public_Tools.isDocumentFile(fileArrListFiles[i2].getName())) {
				FileManager.getIFileManager(this.m_context).deleteFile(fileArrListFiles[i2]);
				i++;
			}
		}
		if (i == fileArrListFiles.length) {
			FileManager.getIFileManager(this.m_context).deleteFile(file);
		}
		getFileAdapter().getFileList().remove(fileInfo);
		getFileAdapter().notifyDataSetChanged();
		FileManager.m_documentFolderList.remove(fileInfo.getPath());
		Global.g_bIsUpdateSQLite = true;
	}

	private void deleteApk(FileInfo fileInfo) {
		File file = new File(fileInfo.path);
		File[] fileArrListFiles = file.listFiles();
		int i = 0;
		for (int i2 = 0; i2 < fileArrListFiles.length; i2++) {
			if (Public_Tools.isApkFile(fileArrListFiles[i2].getName())) {
				FileManager.getIFileManager(this.m_context).deleteFile(fileArrListFiles[i2]);
				i++;
			}
		}
		if (i == fileArrListFiles.length) {
			FileManager.getIFileManager(this.m_context).deleteFile(file);
		}
		getFileAdapter().getFileList().remove(fileInfo);
		getFileAdapter().notifyDataSetChanged();
		FileManager.m_apkFolderList.remove(fileInfo.getPath());
		Global.g_bIsUpdateSQLite = true;
	}

	public void deleteFileInfo(Vector<String> vector) {
		for (int i = 0; i < vector.size(); i++) {
			int i2 = 0;
			while (true) {
				try {
					if (i2 >= getFileAdapter().getFileList().size()) {
						i2 = -1;
						break;
					} else if (vector.get(i).equals(getFileAdapter().getFileList().get(i2).getPath())) {
						break;
					} else {
						i2++;
					}
				} catch (Exception unused) {
					Toast.makeText(this.m_context, R.string.no_delete, 0).show();
				}
			}
			deleteFile(i2 != -1 ? getFileAdapter().getFileList().get(i2) : null);
		}
	}

	@Override // android.widget.AbsListView, android.view.ViewGroup
	public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
		int i = getposition(motionEvent.getRawX(), motionEvent.getRawY());
		Public_Tools.limited(getFileAdapter().getFileList(), i);
		if (i != 0 && Global.g_listViewCurrentAdpter == 2 && motionEvent.getAction() == 0) {
			try {
				int x = (int) motionEvent.getX();
				int iPointToPosition = pointToPosition(x, (int) motionEvent.getY());
				this.m_nCurrentFileIndex = iPointToPosition;
				if (iPointToPosition != -1) {
					View viewFindViewById = ((ViewGroup) getChildAt(iPointToPosition - getFirstVisiblePosition())).findViewById(R.id.file_img_fileItem);
					Rect rect = this.m_rectTemp;
					rect.left = viewFindViewById.getLeft();
					rect.right = viewFindViewById.getRight();
					rect.top = viewFindViewById.getTop();
					rect.bottom = viewFindViewById.getBottom();
					if (rect.left < x && x < rect.right) {
						viewFindViewById.setDrawingCacheEnabled(true);
						Bitmap bitmapCreateScaledBitmap = Bitmap.createScaledBitmap(Bitmap.createBitmap(viewFindViewById.getDrawingCache()), Public_Tools.dip2px(this.m_context, 60.0f), Public_Tools.dip2px(this.m_context, 60.0f), true);
						viewFindViewById.setDrawingCacheEnabled(false);
						startDragging(bitmapCreateScaledBitmap, (int) motionEvent.getRawX(), (int) motionEvent.getRawY(), viewFindViewById.getWidth(), viewFindViewById.getHeight());
						double rawX = motionEvent.getRawX();
						double width = viewFindViewById.getWidth();
						Double.isNaN(width);
						Double.isNaN(rawX);
						this.m_nGragX = (int) (rawX - ((width * 1.2d) / 2.0d));
						double rawY = motionEvent.getRawY();
						double height = viewFindViewById.getHeight();
						Double.isNaN(height);
						Double.isNaN(rawY);
						this.m_nGragY = (int) (rawY - (height * 1.2d));
						return false;
					}
					this.m_imageVDrag = null;
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return super.onInterceptTouchEvent(motionEvent);
	}

	/* JADX INFO: Access modifiers changed from: private */
	public int getposition(float f, float f2) {
		int[] iArr = new int[2];
		int i = -1;
		for (int i2 = 0; i2 < getChildCount(); i2++) {
			getChildAt(i2).getLocationInWindow(iArr);
			if (f >= iArr[0] && f <= this.getWidth() + iArr[0] && f2 >= iArr[1] && f2 <= this.getHeight() + iArr[1]) {
				i = i2;
			}
		}
		return i;
	}

	private void startDragging(Bitmap bitmap, int i, int i2, int i3, int i4) {
		stopDragging();
		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
		this.m_windowParams = layoutParams;
		layoutParams.gravity = 51;
		WindowManager.LayoutParams layoutParams2 = this.m_windowParams;
		double d = i3;
		Double.isNaN(d);
		layoutParams2.x = i - ((int) (d * 1.2d));
		WindowManager.LayoutParams layoutParams3 = this.m_windowParams;
		double d2 = i4;
		Double.isNaN(d2);
		layoutParams3.y = i2 - ((int) (d2 * 1.2d));
		this.m_windowParams.height = -2;
		this.m_windowParams.width = -2;
		this.m_windowParams.flags = 408;
		this.m_windowParams.format = -3;
		this.m_windowParams.windowAnimations = 0;
		ImageView imageView = new ImageView(getContext());
		boolean zIsInner = isInner(this.m_fileAdapter.fileList.get(this.m_nCurrentFileIndex).path, Global.g_filePath);
		if (Global.g_filePath.size() <= 1 || !zIsInner) {
			imageView.setBackgroundColor(0);
			imageView.setImageBitmap(bitmap);
		}
		this.m_bitmapDrag = bitmap;
		WindowManager windowManager = (WindowManager) getContext().getSystemService("window");
		this.m_windowManager = windowManager;
		windowManager.addView(imageView, this.m_windowParams);
		this.m_imageVDrag = imageView;
	}

	private void stopDragging() {
		if (this.m_imageVDrag != null) {
			((WindowManager) getContext().getSystemService("window")).removeView(this.m_imageVDrag);
			this.m_imageVDrag.setImageDrawable(null);
			this.m_imageVDrag = null;
		}
		ImageView imageView = this.m_imageV_deleteFile;
		if (imageView != null) {
			imageView.setVisibility(8);
		}
		this.m_bIsInDeleteArea = false;
		this.m_bIsFilerDown = false;
	}

	private void dragView(int i, int i2) {
		this.m_windowParams.alpha = 1.0f;
		this.m_windowParams.x = i;
		this.m_windowParams.y = i2;
		boolean zIsInner = isInner(this.m_fileAdapter.fileList.get(this.m_nCurrentFileIndex).path, Global.g_filePath);
		if (Global.g_filePath.size() > 1 && zIsInner) {
			this.m_imageVDrag.setBackgroundResource(R.drawable.sendfiles);
			this.m_imageVDrag.setLayoutParams(new AbsListView.LayoutParams(Public_Tools.dip2px(this.m_context, 60.0f), Public_Tools.dip2px(this.m_context, 60.0f)));
		}
		this.m_windowManager.updateViewLayout(this.m_imageVDrag, this.m_windowParams);
	}

	class FileComparer implements Comparator<FileInfo> {
		FileComparer() {
		}

		@Override // java.util.Comparator
		public int compare(FileInfo fileInfo, FileInfo fileInfo2) {
			return fileInfo.name.compareTo(fileInfo2.name);
		}
	}

	private class ListViewGestureListener extends GestureDetector.SimpleOnGestureListener {
		private ListViewGestureListener() {
		}

		@Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
		public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
			float rawX = motionEvent.getRawX();
			float rawY = motionEvent.getRawY();
			int i = FileListView.this.getposition(rawX, rawY);
			if (Global.g_isInFileActivity || Global.g_listViewCurrentAdpter != 2) {
				return false;
			}
			if (i != 0 || IpmsgActivity.s_nCurrentposition != 0) {
				View viewItem = FileListView.this.m_ipmsgActivity.getViewItem(rawX, rawY);
				if (viewItem != null) {
					if (Global.g_filePath.size() > 0) {
						FileListView.this.m_fileAdapter.choiceFile(viewItem.findViewById(R.id.file_select_box_fileitem), FileListView.this.getPositionForView(viewItem));
						return false;
					}
					FileListView fileListView = FileListView.this;
					fileListView.clickInIpmsgActivity(viewItem, fileListView.m_ipmsgActivity);
					return false;
				}
				try {
					FileListView.this.moveBack();
					return false;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			try {
				FileListView.this.moveBack();
				return false;
			} catch (Exception e2) {
				e2.printStackTrace();
				return false;
			}
		}

		@Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
		public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
			try {
				try {
					if (Global.g_listViewCurrentAdpter != 2 || motionEvent.getX() - motionEvent2.getX() <= 50.0f || Math.abs(motionEvent2.getY() - motionEvent.getY()) >= 50.0f) {
						return false;
					}
					try {
						FileListView.this.moveBack();
						return false;
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				} catch (NullPointerException e2) {
					e2.printStackTrace();
					return false;
				}
			} catch (Exception e3) {
				e3.printStackTrace();
				return false;
			}
		}
	}

	/* JADX INFO: Access modifiers changed from: private */
	public void clickInIpmsgActivity(View view, IpmsgActivity ipmsgActivity) {
		if (view == null) {
			return;
		}
		FileInfo fileInfo = this.m_fileAdapter.fileList.get(IpmsgActivity.s_fileListView.getPositionForView(view));
		try {
			stopDragging();
			if (fileInfo.isFloder()) {
				String path = fileInfo.getPath();
				this.m_strCurrentCategoryPath = path;
				if (!path.endsWith(File.separator)) {
					this.m_strCurrentCategoryPath += File.separator;
				}
				moveToCategory(this.m_strCurrentCategoryPath);
				return;
			}
			FileManager.getIFileManager(this.m_context).openFile(new File(fileInfo.getPath()));
		} catch (ActivityNotFoundException unused) {
			Context context = this.m_context;
			Public_Tools.showToast(context, context.getResources().getString(R.string.activity_not_found), 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Bitmap getmDragBitmap() {
		return this.m_bitmapDrag;
	}

	public void setIsFilerDown(boolean z) {
		this.m_bIsFilerDown = z;
	}
}
