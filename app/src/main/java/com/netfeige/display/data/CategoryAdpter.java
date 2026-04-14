package com.netfeige.display.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.netfeige.R;
import com.netfeige.common.Category;
import com.netfeige.common.Global;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.ui.FileListView;
import com.netfeige.display.ui.IpmsgActivity;
import com.netfeige.dlna.ContentTree;
import com.netfeige.enums.FileAccessAuth;
import com.netfeige.filemanager.Catalogue;
import com.netfeige.filemanager.FileManager;
import com.netfeige.kits.DataConfig;
import com.netfeige.kits.IDataConfig;
import com.netfeige.util.SDCardUtil;
import java.io.File;
import java.util.ArrayList;
import org.teleal.cling.model.ServiceReference;

/* JADX INFO: loaded from: classes.dex */
public class CategoryAdpter extends BaseAdapter {
    private LinearLayout m_LinearLIntroduction;
    private Context m_context;
    private IDataConfig m_iDataConfig;
    private LayoutInflater m_inflater;
    private IpmsgActivity m_ipmsgActivity;
    private String m_strCheckCompress;
    private FileListView m_fileListView = null;
    private final int m_nInCatalogue = 2;
    private int offsetHeight = 50;
    private int m_nFirst = 0;
    private int m_nCurrentCount = 9;

    private void feigeGuidePage() {
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return 1;
    }

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return null;
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return 0L;
    }

    public CategoryAdpter(Context context) {
        this.m_iDataConfig = null;
        this.m_context = context;
        this.m_inflater = LayoutInflater.from(context);
        IpmsgActivity ipmsgActivity = (IpmsgActivity) context;
        this.m_ipmsgActivity = ipmsgActivity;
        this.m_iDataConfig = DataConfig.getInstance(ipmsgActivity.getApplicationContext());
    }

    public final class ListItemView {
        public Button btn_filer_apk;
        public Button btn_filer_audio;
        public Button btn_filer_document;
        public Button btn_filer_feige;
        public Button btn_filer_img;
        public Button btn_filer_memory;
        public Button btn_filer_sdcard;
        public Button btn_filer_video;
        public TextView textV_filer_apk;
        public TextView textV_filer_audio;
        public TextView textV_filer_document;
        public TextView textV_filer_feige;
        public TextView textV_filer_img;
        public TextView textV_filer_memory;
        public TextView textV_filer_sdcard;
        public TextView textV_filer_video;

        public ListItemView() {
        }
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        ListItemView listItemView;
        if (view == null) {
            listItemView = new ListItemView();
            view = this.m_inflater.inflate(R.layout.categoryfileitem, (ViewGroup) null);
            listItemView.btn_filer_img = (Button) view.findViewById(R.id.btn_filer_img);
            listItemView.textV_filer_img = (TextView) view.findViewById(R.id.textV_filer_img);
            listItemView.btn_filer_audio = (Button) view.findViewById(R.id.btn_filer_audio);
            listItemView.textV_filer_audio = (TextView) view.findViewById(R.id.textV_filer_audio);
            listItemView.btn_filer_video = (Button) view.findViewById(R.id.btn_filer_video);
            listItemView.textV_filer_video = (TextView) view.findViewById(R.id.textV_filer_video);
            listItemView.btn_filer_document = (Button) view.findViewById(R.id.btn_filer_document);
            listItemView.textV_filer_document = (TextView) view.findViewById(R.id.textV_filer_document);
            listItemView.btn_filer_apk = (Button) view.findViewById(R.id.btn_filer_apk);
            listItemView.textV_filer_apk = (TextView) view.findViewById(R.id.textV_filer_apk);
            listItemView.btn_filer_memory = (Button) view.findViewById(R.id.btn_filer_memory);
            listItemView.textV_filer_memory = (TextView) view.findViewById(R.id.textV_filer_memory);
            listItemView.btn_filer_feige = (Button) view.findViewById(R.id.btn_filer_feige);
            listItemView.textV_filer_feige = (TextView) view.findViewById(R.id.textV_filer_feige);
            listItemView.btn_filer_sdcard = (Button) view.findViewById(R.id.btn_filer_sdcard);
            listItemView.textV_filer_sdcard = (TextView) view.findViewById(R.id.textV_filer_sdcard);
            view.setTag(listItemView);
        } else {
            listItemView = (ListItemView) view.getTag();
        }
        listItemView.btn_filer_img.setBackgroundResource(R.drawable.filer_img);
        listItemView.textV_filer_img.setText(R.string.filer_img);
        listItemView.btn_filer_img.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.data.CategoryAdpter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                CategoryAdpter.this.clickCategory(true, Category.image, FileManager.m_imageFolderList, "img/");
            }
        });
        listItemView.btn_filer_audio.setBackgroundResource(R.drawable.filer_audio);
        listItemView.textV_filer_audio.setText(R.string.filer_audio);
        listItemView.btn_filer_audio.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.data.CategoryAdpter.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                CategoryAdpter.this.clickCategory(false, Category.audio, FileManager.m_audioFolderList, "audio/");
            }
        });
        listItemView.btn_filer_video.setBackgroundResource(R.drawable.filer_video);
        listItemView.textV_filer_video.setText(R.string.filer_video);
        listItemView.btn_filer_video.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.data.CategoryAdpter.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                CategoryAdpter.this.clickCategory(false, Category.video, FileManager.m_videoFolderList, "video/");
            }
        });
        listItemView.btn_filer_document.setBackgroundResource(R.drawable.filer_document);
        listItemView.textV_filer_document.setText(R.string.filer_document);
        listItemView.btn_filer_document.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.data.CategoryAdpter.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                CategoryAdpter.this.clickCategory(false, Category.document, FileManager.m_documentFolderList, "document/");
            }
        });
        listItemView.btn_filer_apk.setBackgroundResource(R.drawable.filer_apk);
        listItemView.textV_filer_apk.setText(R.string.filer_apk);
        listItemView.btn_filer_apk.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.data.CategoryAdpter.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                CategoryAdpter.this.clickCategory(true, Category.apk, FileManager.m_apkFolderList, "apk/");
            }
        });
        listItemView.btn_filer_feige.setBackgroundResource(R.drawable.filer_feige);
        listItemView.textV_filer_feige.setText(R.string.filer_feige);
        listItemView.btn_filer_feige.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.data.CategoryAdpter.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) throws Throwable {
                CategoryAdpter.this.clickNoCompress(Category.feige, Public_Tools.getDefaultDownloadPath(), "feige/");
            }
        });
        listItemView.btn_filer_memory.setBackgroundResource(R.drawable.filer_memory);
        listItemView.textV_filer_memory.setText(R.string.filer_memory);
        listItemView.btn_filer_memory.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.data.CategoryAdpter.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                CategoryAdpter.this.clickNoCompress(Category.memory, ServiceReference.DELIMITER, ServiceReference.DELIMITER);
            }
        });
        listItemView.btn_filer_sdcard.setBackgroundResource(R.drawable.filer_sdcard);
        listItemView.textV_filer_sdcard.setText(R.string.filer_sdcard);
        listItemView.btn_filer_sdcard.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.data.CategoryAdpter.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                CategoryAdpter.this.clickNoCompress(Category.sdcard, null, "sdcard/");
            }
        });
        return view;
    }

    private void afterFileLoaded(String str) {
        Public_Tools.fileCompare(this.m_fileListView.getFileAdapter().getFileList());
        FileListView fileListView = this.m_fileListView;
        fileListView.setAdapter((ListAdapter) fileListView.getFileAdapter());
        Global.g_listViewCurrentAdpter = 2;
        Catalogue.categoryStack.push(str);
        recoverPosition(str);
        this.m_fileListView.getFileAdapter().notifyDataSetChanged();
    }

    private void recoverPosition(String str) {
        if (this.m_ipmsgActivity.getHashMapKeepPosition().containsKey(str)) {
            int iIntValue = this.m_ipmsgActivity.getHashMapKeepPosition().get(str).intValue();
            this.m_fileListView.setSelection(iIntValue);
            this.m_nFirst = iIntValue;
            this.m_fileListView.loadingImage(iIntValue, this.m_nCurrentCount + iIntValue, this.m_context);
            return;
        }
        this.m_nFirst = 0;
        this.m_fileListView.loadingImage(0, this.m_nCurrentCount + 0, this.m_context);
    }

    private void startCompress() {
        FileManager.getIFileManager(this.m_context).compressFolder();
        Global.g_nCompressed = 1;
        this.m_ipmsgActivity.showPregressBar();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clickCategory(boolean z, Category category, ArrayList<String> arrayList, String str) {
        feigeGuidePage();
        this.m_fileListView = IpmsgActivity.s_fileListView;
        Global.g_bInRoot = false;
        btnShow();
        Global.g_isAsyncLoadedImage = z;
        this.m_fileListView.getFileAdapter().getFileList().clear();
        Global.g_bInCategoryDir = false;
        Public_Tools.backForward(this.m_fileListView.getFileAdapter().getFileList());
        this.m_ipmsgActivity.getM_btnDragbar().setText(str);
        Global.g_whatFolder = category;
        String strRead = this.m_iDataConfig.Read(28);
        this.m_strCheckCompress = strRead;
        if (strRead == ContentTree.ROOT_ID) {
            if (Global.g_nCompressed != 1) {
                startCompress();
            } else {
                loadFolder2(arrayList, this.m_fileListView);
                this.m_ipmsgActivity.showPregressBar();
            }
        } else {
            if (Global.g_nBrowseMethod == 1) {
                loadFolder(arrayList, this.m_fileListView);
            } else if (Global.g_nBrowseMethod == 0) {
                this.m_ipmsgActivity.allBrowse(arrayList, category, this.m_fileListView);
            }
            if (Global.g_nCompressed == 1) {
                this.m_ipmsgActivity.showPregressBar();
            }
        }
        afterFileLoaded(str);
    }

    private void loadFolder(ArrayList<String> arrayList, FileListView fileListView) {
        for (int i = 0; i < arrayList.size(); i++) {
            Public_Tools.path2FileInfoForMian(arrayList.get(i), fileListView);
        }
    }

    private void loadFolder2(ArrayList<String> arrayList, FileListView fileListView) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (Global.g_nBrowseMethod == 0) {
                File file = new File(arrayList.get(i));
                if (file.exists() && file.isDirectory()) {
                    File[] fileArrListFiles = file.listFiles();
                    for (int i2 = 0; i2 < fileArrListFiles.length; i2++) {
                        this.m_ipmsgActivity.specialFilling(Global.g_whatFolder, fileArrListFiles, i2, fileListView);
                    }
                }
            } else {
                Public_Tools.path2FileInfoForMian(arrayList.get(i), fileListView);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clickNoCompress(Category category, String str, String str2) {
        if (str2.equals("sdcard/") && SDCardUtil.getSDCardInfo(this.m_context).isEmpty()) {
            Toast.makeText(this.m_context, "无SD卡！", 0).show();
            return;
        }
        this.m_fileListView = IpmsgActivity.s_fileListView;
        Global.g_bInRoot = false;
        btnShow();
        Global.g_whatFolder = category;
        Global.g_listViewCurrentAdpter = 2;
        feigeGuidePage();
        FileListView fileListView = this.m_fileListView;
        fileListView.setAdapter((ListAdapter) fileListView.getFileAdapter());
        try {
            this.m_fileListView.moveToCategory(str, str2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.m_fileListView.setCurrentCategoryPath(str);
        if (Global.g_bWaitPaste != 1 || str == null) {
            return;
        }
        File file = new File(str);
        if (file.isDirectory() && (Public_Tools.authority(file) == FileAccessAuth.W_OK || Public_Tools.authority(file) == FileAccessAuth.RW_OK)) {
            this.m_ipmsgActivity.showPopupWindowPaste(true);
        } else {
            this.m_ipmsgActivity.showPopupWindowPaste(false);
        }
    }

    private void btnShow() {
        if (this.m_fileListView.getHeight() >= (this.m_ipmsgActivity.getM_rectWindowArea().height() - this.offsetHeight) - this.m_ipmsgActivity.getLinearLDragBar().getHeight()) {
            this.m_ipmsgActivity.getBtnMore().setVisibility(0);
            this.m_ipmsgActivity.getBtnParentFolder().setVisibility(0);
        }
    }
}

