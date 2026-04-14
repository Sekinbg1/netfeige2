package com.netfeige.display.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.netfeige.R;
import com.netfeige.common.Global;
import com.netfeige.common.ImagePreview;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.data.ImageAdapter;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.ui.dialog.LastImageDialog;
import com.netfeige.display.ui.dialog.ShowImageDeleteDialog;
import com.netfeige.dlna.ContentTree;
import com.netfeige.dlna.DlnaHelper;
import com.netfeige.filemanager.FileManager;
import com.netfeige.kits.DataConfig;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.support.avtransport.callback.Play;
import org.teleal.cling.support.avtransport.callback.SetAVTransportURI;

/* JADX INFO: loaded from: classes.dex */
public class ImagePreviewActivity extends Activity implements View.OnTouchListener {
    public static boolean g_bIsAVTransport = false;
    public static boolean g_bIsSuc = false;
    public static TextView g_textVScreeninteraction = null;
    public static TextView g_textVScreeninteraction2 = null;
    public static Bitmap s_bmDamage = null;
    public static int s_nCurrentImageIndex = -1;
    public static int s_nScreenHeight;
    public static int s_nScreenWidth;
    private Context m_context;
    private IpmsgApplication m_ipmsgApp;
    private LastImageDialog m_lastImageDialog;
    private LinearLayout m_linearLDelete;
    private LinearLayout m_linearLDelete2;
    private LinearLayout m_linearLPopupSend;
    private LinearLayout m_linearLPrint;
    private LinearLayout m_linearLScreeninteraction;
    private LinearLayout m_linearLScreeninteraction2;
    private LinearLayout m_linearLSend;
    private LinearLayout m_linearLShare;
    private int m_nCurrentIndexBak;
    private int m_nPopX;
    private int m_nPopY;
    private PopupWindow m_popupWindow;
    private PopupWindow m_popupWindow2;
    private ShowImageDeleteDialog m_showImageDeleteDialog;
    private ImageGallery m_imageGallery = null;
    private Button m_btnBack = null;
    private TextView m_textVShowName = null;
    private Button m_btnPopup = null;
    private LinearLayout m_linearLImagePreview = null;
    private ImageAdapter m_imageAdapter = null;
    private ImagePreview m_imagePreview = null;
    private boolean m_bIsExecute = false;
    private float m_fBeforeLenght = 0.0f;
    private float m_fAfterLenght = 0.0f;
    private boolean m_bIsScale = false;
    private float m_fCurrentScale = 1.0f;
    private LayoutInflater m_inflater = null;
    private final int m_nPopUpX = 5;
    private final int m_nPopUpY = 50;
    private int m_nNum = 0;
    private int m_nNum1 = 0;

    static /* synthetic */ int access$2208(ImagePreviewActivity imagePreviewActivity) {
        int i = imagePreviewActivity.m_nNum1;
        imagePreviewActivity.m_nNum1 = i + 1;
        return i;
    }

    static /* synthetic */ int access$2508(ImagePreviewActivity imagePreviewActivity) {
        int i = imagePreviewActivity.m_nNum;
        imagePreviewActivity.m_nNum = i + 1;
        return i;
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.imagepreview);
        this.m_context = this;
        this.m_inflater = (LayoutInflater) getSystemService("layout_inflater");
        this.m_ipmsgApp = (IpmsgApplication) getApplication();
        InputStream inputStreamOpenRawResource = getResources().openRawResource(R.drawable.damage);
        s_bmDamage = BitmapFactory.decodeStream(inputStreamOpenRawResource);
        try {
            inputStreamOpenRawResource.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bundle extras = getIntent().getExtras();
        String string = extras.getString("filePath");
        g_bIsAVTransport = extras.getBoolean("AVTransport");
        Bitmap bitmapDecodeBitmap2 = Public_Tools.decodeBitmap2(string, -1, -1);
        if (bitmapDecodeBitmap2 == null) {
            bitmapDecodeBitmap2 = s_bmDamage;
        }
        s_nCurrentImageIndex = seekPosition(string);
        try {
            ImageAdapter.s_imageList.get(s_nCurrentImageIndex).setM_imageBitmap(bitmapDecodeBitmap2);
            sendMediaFile(string);
            loadLeftImages();
            loadRightImages();
            initControl();
            s_nScreenWidth = getWindow().getWindowManager().getDefaultDisplay().getWidth();
            s_nScreenHeight = getWindow().getWindowManager().getDefaultDisplay().getHeight() - this.m_linearLImagePreview.getHeight();
            this.m_nPopX = Public_Tools.dip2px(this, 5.0f);
            this.m_nPopY = Public_Tools.dip2px(this, 50.0f);
            if (g_bIsAVTransport) {
                this.m_btnPopup.setVisibility(4);
            } else {
                initPopupWindow();
                initPopupWindow2();
            }
            this.m_showImageDeleteDialog = new ShowImageDeleteDialog(this);
            this.m_lastImageDialog = new LastImageDialog(this);
        } catch (IndexOutOfBoundsException unused) {
            onBackPressed();
        } catch (Exception unused2) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendMediaFile(String str) {
        String httpUri;
        if (!g_bIsAVTransport || (httpUri = DlnaHelper.getHttpUri(str)) == null || ChoiceRemotePlayerActivity.g_serviceAVTransport == null) {
            return;
        }
        DlnaHelper.stopPlay(ChoiceRemotePlayerActivity.g_serviceAVTransport, this.m_ipmsgApp);
        setPlayUri(ChoiceRemotePlayerActivity.g_serviceAVTransport, httpUri);
    }

    private void initControl() {
        ImageGallery imageGallery = (ImageGallery) findViewById(R.id.image_preview);
        this.m_imageGallery = imageGallery;
        imageGallery.setVerticalFadingEdgeEnabled(false);
        this.m_imageGallery.setHorizontalFadingEdgeEnabled(false);
        ImageAdapter imageAdapter = new ImageAdapter(this);
        this.m_imageAdapter = imageAdapter;
        this.m_imageGallery.setAdapter((SpinnerAdapter) imageAdapter);
        this.m_imageGallery.setSelection(s_nCurrentImageIndex);
        this.m_textVShowName = (TextView) findViewById(R.id.path_text_txt);
        this.m_imageGallery.setOnItemSelectedListener(new ImageGalleryOnItemSelectedListener());
        Button button = (Button) findViewById(R.id.back_btn);
        this.m_btnBack = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.ImagePreviewActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ImagePreviewActivity.this.onBackPressed();
            }
        });
        Button button2 = (Button) findViewById(R.id.popup_btn);
        this.m_btnPopup = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.ImagePreviewActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                View viewFindViewById = ImagePreviewActivity.this.findViewById(R.id.top_imagepreview_layout);
                if (Global.g_isInFileActivity) {
                    ImagePreviewActivity.this.m_popupWindow2.showAtLocation(viewFindViewById, 53, ImagePreviewActivity.this.m_nPopX, ImagePreviewActivity.this.m_nPopY);
                } else {
                    ImagePreviewActivity.this.m_popupWindow.showAtLocation(viewFindViewById, 53, ImagePreviewActivity.this.m_nPopX, ImagePreviewActivity.this.m_nPopY);
                }
            }
        });
        this.m_linearLImagePreview = (LinearLayout) findViewById(R.id.top_imagepreview_layout);
        toFront();
    }

    private class ImageGalleryOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onNothingSelected(AdapterView<?> adapterView) {
        }

        private ImageGalleryOnItemSelectedListener() {
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
            ImagePreviewActivity.s_nCurrentImageIndex = i;
            ImagePreviewActivity imagePreviewActivity = ImagePreviewActivity.this;
            imagePreviewActivity.m_imagePreview = (ImagePreview) imagePreviewActivity.m_imageAdapter.getItem(ImagePreviewActivity.s_nCurrentImageIndex);
            ImagePreviewActivity.this.m_textVShowName.setText(ImagePreviewActivity.this.m_imagePreview.getM_strImageName());
            if (ImageAdapter.s_imageList.size() > 2) {
                ImagePreviewActivity.this.loadLeftImages();
                ImagePreviewActivity.this.loadRightImages();
            }
            if (ImagePreviewActivity.g_bIsAVTransport) {
                ImagePreviewActivity.this.sendMediaFile(ImageAdapter.s_imageList.get(ImagePreviewActivity.s_nCurrentImageIndex).getM_strImagePath());
            }
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        if (Global.g_bInChoiceRemote) {
            if (Global.g_bInImageFromRemote) {
                s_nCurrentImageIndex = this.m_nCurrentIndexBak;
                Global.g_bInChoiceRemote = false;
                g_bIsAVTransport = false;
                if (ImageAdapter.s_imageList.size() > 2) {
                    loadLeftImages();
                    loadRightImages();
                }
            } else {
                Global.g_bInImageFromRemote = true;
            }
        }
        this.m_ipmsgApp.currentActivity = this;
        if (this.m_ipmsgApp.g_bBackRuning) {
            this.m_ipmsgApp.g_bBackRuning = false;
            ((NotificationManager) getSystemService("notification")).cancel(IpmsgApplication.MAIN_NOTIFICATION_ID);
            if (this.m_ipmsgApp.g_strMsgNotification.equals(ContentTree.VIDEO_ID)) {
                ((NotificationManager) getSystemService("notification")).cancel(IpmsgApplication.MSG_NOTIFICATION_ID);
            }
        }
        super.onResume();
// Umeng removed:         // Umeng removed: MobclickAgent.onResume(this);
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
// Umeng removed:         // Umeng removed: MobclickAgent.onPause(this);
    }

    @Override // android.app.Activity
    protected void onStop() {
        if (this.m_ipmsgApp.currentActivity == this) {
            this.m_ipmsgApp.g_bBackRuning = true;
            Public_Tools.showNotification(this.m_ipmsgApp, getString(R.string.app_name), DataConfig.getInstance(this).Read(0), R.drawable.ic_launcher);
        }
        super.onStop();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        s_bmDamage.recycle();
        super.onDestroy();
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        stopRemotePlay();
        super.onBackPressed();
    }

    private void stopRemotePlay() {
        if (!g_bIsAVTransport || ChoiceRemotePlayerActivity.g_serviceAVTransport == null) {
            return;
        }
        DlnaHelper.stopPlay(ChoiceRemotePlayerActivity.g_serviceAVTransport, this.m_ipmsgApp);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 4) {
            stopRemotePlay();
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction() & 255;
        if (action != 2) {
            if (action == 5) {
                float fSpacing = spacing(motionEvent);
                this.m_fBeforeLenght = fSpacing;
                if (fSpacing > 5.0f) {
                    this.m_bIsScale = true;
                }
            } else if (action == 6) {
                this.m_bIsScale = false;
            }
        } else if (this.m_bIsScale) {
            float fSpacing2 = spacing(motionEvent);
            this.m_fAfterLenght = fSpacing2;
            if (fSpacing2 >= 5.0f) {
                float f = fSpacing2 - this.m_fBeforeLenght;
                if (f != 0.0f) {
                    if (Math.abs(f) > 5.0f) {
                        float f2 = f / 854.0f;
                        float f3 = this.m_fCurrentScale;
                        ScaleAnimation scaleAnimation = new ScaleAnimation(f3, f3 + f2, f3, f3 + f2, 1, 0.5f, 1, 0.5f);
                        scaleAnimation.setDuration(100L);
                        scaleAnimation.setFillAfter(true);
                        scaleAnimation.setFillEnabled(true);
                        this.m_fCurrentScale += f2;
                        View selectedView = this.m_imageGallery.getSelectedView();
                        float f4 = this.m_fCurrentScale;
                        selectedView.setLayoutParams(new Gallery.LayoutParams((int) (480.0f * f4), (int) (f4 * 854.0f)));
                        this.m_fBeforeLenght = this.m_fAfterLenght;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private float spacing(MotionEvent motionEvent) {
        float x = motionEvent.getX(0) - motionEvent.getX(1);
        float y = motionEvent.getY(0) - motionEvent.getY(1);
        return (float) Math.sqrt((x * x) + (y * y));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadRightImages() {
        Bitmap m_imageBitmap;
        if (s_nCurrentImageIndex < ImageAdapter.s_imageList.size() - 1) {
            Bitmap m_imageBitmap2 = ImageAdapter.s_imageList.get(s_nCurrentImageIndex + 1).getM_imageBitmap();
            if (m_imageBitmap2 == null) {
                Bitmap bitmapDecodeBitmap2 = Public_Tools.decodeBitmap2(ImageAdapter.s_imageList.get(s_nCurrentImageIndex + 1).getM_strImagePath(), -1, -1);
                if (bitmapDecodeBitmap2 == null) {
                    bitmapDecodeBitmap2 = s_bmDamage;
                }
                ImageAdapter.s_imageList.get(s_nCurrentImageIndex + 1).setM_imageBitmap(bitmapDecodeBitmap2);
            } else if (m_imageBitmap2.isRecycled()) {
                String m_strImagePath = ImageAdapter.s_imageList.get(s_nCurrentImageIndex + 1).getM_strImagePath();
                Bitmap bitmapDecodeBitmap22 = Public_Tools.decodeBitmap2(m_strImagePath, -1, -1);
                if (bitmapDecodeBitmap22 == null) {
                    bitmapDecodeBitmap22 = s_bmDamage;
                }
                ImageAdapter.s_imageList.set(s_nCurrentImageIndex + 1, new ImagePreview(0, ImageAdapter.s_imageList.get(s_nCurrentImageIndex + 1).getM_strImageName(), m_strImagePath, bitmapDecodeBitmap22, ImageAdapter.s_imageList.get(s_nCurrentImageIndex + 1).getM_lLastTime()));
            }
            if (s_nCurrentImageIndex - 2 < 0 || (m_imageBitmap = ImageAdapter.s_imageList.get(s_nCurrentImageIndex - 2).getM_imageBitmap()) == null || m_imageBitmap.isRecycled() || m_imageBitmap == s_bmDamage) {
                return;
            }
            m_imageBitmap.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadLeftImages() {
        Bitmap m_imageBitmap;
        int size = ImageAdapter.s_imageList.size();
        if (s_nCurrentImageIndex > 0) {
            Bitmap m_imageBitmap2 = ImageAdapter.s_imageList.get(s_nCurrentImageIndex - 1).getM_imageBitmap();
            if (m_imageBitmap2 == null) {
                Bitmap bitmapDecodeBitmap2 = Public_Tools.decodeBitmap2(ImageAdapter.s_imageList.get(s_nCurrentImageIndex - 1).getM_strImagePath(), -1, -1);
                if (bitmapDecodeBitmap2 == null) {
                    bitmapDecodeBitmap2 = s_bmDamage;
                }
                ImageAdapter.s_imageList.get(s_nCurrentImageIndex - 1).setM_imageBitmap(bitmapDecodeBitmap2);
            } else if (m_imageBitmap2.isRecycled()) {
                String m_strImagePath = ImageAdapter.s_imageList.get(s_nCurrentImageIndex - 1).getM_strImagePath();
                String m_strImageName = ImageAdapter.s_imageList.get(s_nCurrentImageIndex - 1).getM_strImageName();
                long m_lLastTime = ImageAdapter.s_imageList.get(s_nCurrentImageIndex - 1).getM_lLastTime();
                Bitmap bitmapDecodeBitmap22 = Public_Tools.decodeBitmap2(m_strImagePath, -1, -1);
                if (bitmapDecodeBitmap22 == null) {
                    bitmapDecodeBitmap22 = s_bmDamage;
                }
                ImageAdapter.s_imageList.set(s_nCurrentImageIndex - 1, new ImagePreview(0, m_strImageName, m_strImagePath, bitmapDecodeBitmap22, m_lLastTime));
            }
            if (s_nCurrentImageIndex + 2 > size - 1 || (m_imageBitmap = ImageAdapter.s_imageList.get(s_nCurrentImageIndex + 2).getM_imageBitmap()) == null || m_imageBitmap.isRecycled() || m_imageBitmap == s_bmDamage) {
                return;
            }
            m_imageBitmap.recycle();
        }
    }

    public ImageAdapter getM_imageAdapter() {
        return this.m_imageAdapter;
    }

    public void deleteImage(String str) {
        int positionOnPath;
        try {
            if (FileManager.getIFileManager(this).deleteFile(new File(str))) {
                String m_strImagePath = ImageAdapter.s_imageList.get(s_nCurrentImageIndex).getM_strImagePath();
                if (Global.g_isInFileActivity) {
                    positionOnPath = Public_Tools.getPositionOnPath(FileActivity.s_choiceListView.getFileAdapter().getFileList(), m_strImagePath);
                } else {
                    positionOnPath = Public_Tools.getPositionOnPath(IpmsgActivity.s_fileListView.getFileAdapter().getFileList(), m_strImagePath);
                }
                if (positionOnPath != -1) {
                    if (Global.g_isInFileActivity) {
                        FileActivity.s_choiceListView.getFileAdapter().getFileList().remove(positionOnPath);
                        FileActivity.s_choiceListView.getFileAdapter().notifyDataSetChanged();
                    } else {
                        IpmsgActivity.s_fileListView.getFileAdapter().getFileList().remove(positionOnPath);
                        IpmsgActivity.s_fileListView.getFileAdapter().notifyDataSetChanged();
                    }
                }
                ImageAdapter.s_imageList.get(s_nCurrentImageIndex).getM_imageBitmap().recycle();
                ImageAdapter.s_imageList.remove(s_nCurrentImageIndex);
                this.m_imageAdapter.notifyDataSetChanged();
                int size = ImageAdapter.s_imageList.size();
                if (size == 0) {
                    this.m_lastImageDialog.show();
                    return;
                }
                if (size == 1) {
                    this.m_textVShowName.setText(ImageAdapter.s_imageList.get(0).getM_strImageName());
                    return;
                }
                if (s_nCurrentImageIndex == 0) {
                    if (s_nCurrentImageIndex + 1 < size) {
                        this.m_textVShowName.setText(ImageAdapter.s_imageList.get(s_nCurrentImageIndex).getM_strImageName());
                    } else {
                        this.m_textVShowName.setText("");
                    }
                    loadRightImages();
                    return;
                }
                if (s_nCurrentImageIndex == size - 1) {
                    if (s_nCurrentImageIndex - 1 >= 0) {
                        this.m_textVShowName.setText(ImageAdapter.s_imageList.get(s_nCurrentImageIndex - 1).getM_strImageName());
                    } else {
                        this.m_textVShowName.setText("");
                    }
                    loadLeftImages();
                    return;
                }
                this.m_textVShowName.setText(ImageAdapter.s_imageList.get(s_nCurrentImageIndex).getM_strImageName());
                loadRightImages();
                return;
            }
            Toast.makeText(this, R.string.not_delete_notify, 0).show();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private int seekPosition(String str) {
        for (int i = 0; i < ImageAdapter.s_imageList.size(); i++) {
            if (str.equals(ImageAdapter.s_imageList.get(i).getM_strImagePath())) {
                return i;
            }
        }
        return -1;
    }

    public void singleTapup() {
        if (this.m_bIsExecute) {
            this.m_btnBack.setVisibility(0);
            this.m_textVShowName.setVisibility(0);
            if (g_bIsAVTransport) {
                this.m_btnPopup.setVisibility(4);
            } else {
                this.m_btnPopup.setVisibility(0);
            }
            this.m_linearLImagePreview.setVisibility(0);
            toFront();
            this.m_bIsExecute = false;
            return;
        }
        this.m_btnBack.setVisibility(8);
        this.m_textVShowName.setVisibility(8);
        this.m_btnPopup.setVisibility(8);
        this.m_linearLImagePreview.setVisibility(8);
        this.m_bIsExecute = true;
    }

    private void toFront() {
        this.m_btnBack.bringToFront();
        this.m_textVShowName.bringToFront();
        this.m_btnPopup.bringToFront();
        this.m_linearLImagePreview.bringToFront();
    }

    private void initPopupWindow2() {
        View viewInflate = this.m_inflater.inflate(R.layout.popupimagepreview2, (ViewGroup) null);
        PopupWindow popupWindow = new PopupWindow(viewInflate, -2, -2);
        this.m_popupWindow2 = popupWindow;
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup2));
        this.m_popupWindow2.setFocusable(true);
        this.m_popupWindow2.setOutsideTouchable(true);
        this.m_popupWindow2.update();
        LinearLayout linearLayout = (LinearLayout) viewInflate.findViewById(R.id.linearl_delete);
        this.m_linearLDelete2 = linearLayout;
        linearLayout.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.ImagePreviewActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (((IpmsgApplication) ((Activity) ImagePreviewActivity.this.m_context).getApplication()).g_strDelFilePrompt.equals(ContentTree.VIDEO_ID)) {
                    ImagePreviewActivity.this.m_showImageDeleteDialog.show();
                } else {
                    if (ImagePreviewActivity.s_nCurrentImageIndex >= 0) {
                        ImagePreviewActivity imagePreviewActivity = ImagePreviewActivity.this;
                        imagePreviewActivity.setImagePreview((ImagePreview) imagePreviewActivity.getM_imageAdapter().getItem(ImagePreviewActivity.s_nCurrentImageIndex));
                    }
                    ImagePreviewActivity imagePreviewActivity2 = ImagePreviewActivity.this;
                    imagePreviewActivity2.deleteImage(imagePreviewActivity2.getImagePreview().getM_strImagePath());
                }
                ImagePreviewActivity.this.m_popupWindow2.dismiss();
            }
        });
        LinearLayout linearLayout2 = (LinearLayout) viewInflate.findViewById(R.id.linearl_send);
        this.m_linearLSend = linearLayout2;
        linearLayout2.setOnClickListener(new LinearSendClickListener());
        LinearLayout linearLayout3 = (LinearLayout) viewInflate.findViewById(R.id.linearl_screeninteraction2);
        this.m_linearLScreeninteraction2 = linearLayout3;
        linearLayout3.setOnClickListener(new LinearScreeninteractionClickListener2());
        g_textVScreeninteraction2 = (TextView) viewInflate.findViewById(R.id.textv_screeninteraction2);
    }

    private class LinearSendClickListener implements View.OnClickListener {
        private LinearSendClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            try {
                ImagePreviewActivity.this.m_ipmsgApp.waitSendfiles.clear();
                ImagePreviewActivity.this.m_ipmsgApp.waitSendfiles.add(ImageAdapter.s_imageList.get(ImagePreviewActivity.s_nCurrentImageIndex).getM_strImagePath());
                ImagePreviewActivity.this.m_ipmsgApp.ipmsgService.m_DataSource.m_Protocol.sendFile(null, ImagePreviewActivity.this.m_ipmsgApp.waitSendfiles, IpmsgApplication.g_currentHostInfo, ImagePreviewActivity.this.m_ipmsgApp.ipmsgService, null, 0);
                Toast.makeText(ImagePreviewActivity.this.m_context, R.string.notifysendout, 0).show();
                ImagePreviewActivity.this.m_popupWindow2.dismiss();
                ImagePreviewActivity.this.m_ipmsgApp.waitSendfiles.clear();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private class LinearScreeninteractionClickListener2 implements View.OnClickListener {
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
        }

        private LinearScreeninteractionClickListener2() {
        }
    }

    private void initPopupWindow() {
        View viewInflate = this.m_inflater.inflate(R.layout.popupimagepreview, (ViewGroup) null);
        PopupWindow popupWindow = new PopupWindow(viewInflate, -2, -2);
        this.m_popupWindow = popupWindow;
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup3));
        this.m_popupWindow.setFocusable(true);
        this.m_popupWindow.setOutsideTouchable(true);
        this.m_popupWindow.update();
        LinearLayout linearLayout = (LinearLayout) viewInflate.findViewById(R.id.linearl_send);
        this.m_linearLPopupSend = linearLayout;
        linearLayout.setOnClickListener(new PopUpWindowSendOnClickListener());
        LinearLayout linearLayout2 = (LinearLayout) viewInflate.findViewById(R.id.linearl_print);
        this.m_linearLPrint = linearLayout2;
        linearLayout2.setOnClickListener(new PopUpWindowPrintOnClickListener());
        LinearLayout linearLayout3 = (LinearLayout) viewInflate.findViewById(R.id.linearl_share);
        this.m_linearLShare = linearLayout3;
        linearLayout3.setOnClickListener(new PopUpWindowShareOnClickListener());
        LinearLayout linearLayout4 = (LinearLayout) viewInflate.findViewById(R.id.linearl_screeninteract);
        this.m_linearLScreeninteraction = linearLayout4;
        linearLayout4.setOnClickListener(new LinearScreeninteractionClickListener());
        g_textVScreeninteraction = (TextView) viewInflate.findViewById(R.id.textv_screeninteract);
        LinearLayout linearLayout5 = (LinearLayout) viewInflate.findViewById(R.id.linearl_delete);
        this.m_linearLDelete = linearLayout5;
        linearLayout5.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.ImagePreviewActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (((IpmsgApplication) ((Activity) ImagePreviewActivity.this.m_context).getApplication()).g_strDelFilePrompt.equals(ContentTree.VIDEO_ID)) {
                    ImagePreviewActivity.this.m_showImageDeleteDialog.show();
                } else {
                    if (ImagePreviewActivity.s_nCurrentImageIndex >= 0) {
                        ImagePreviewActivity imagePreviewActivity = ImagePreviewActivity.this;
                        imagePreviewActivity.setImagePreview((ImagePreview) imagePreviewActivity.getM_imageAdapter().getItem(ImagePreviewActivity.s_nCurrentImageIndex));
                    }
                    ImagePreviewActivity imagePreviewActivity2 = ImagePreviewActivity.this;
                    imagePreviewActivity2.deleteImage(imagePreviewActivity2.getImagePreview().getM_strImagePath());
                }
                ImagePreviewActivity.this.m_popupWindow.dismiss();
            }
        });
    }

    private class LinearScreeninteractionClickListener implements View.OnClickListener {
        private LinearScreeninteractionClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            ImagePreviewActivity.this.m_popupWindow.dismiss();
            ImagePreviewActivity.this.m_nCurrentIndexBak = ImagePreviewActivity.s_nCurrentImageIndex;
            String m_strImagePath = ImageAdapter.s_imageList.get(ImagePreviewActivity.s_nCurrentImageIndex).getM_strImagePath();
            Intent intent = new Intent(ImagePreviewActivity.this, (Class<?>) ChoiceRemotePlayerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("filePath", m_strImagePath);
            intent.putExtras(bundle);
            ImagePreviewActivity.this.startActivity(intent);
        }
    }

    private class PopUpWindowSendOnClickListener implements View.OnClickListener {
        private PopUpWindowSendOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Intent intent = new Intent(ImagePreviewActivity.this, (Class<?>) ChoiceUserActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("filePath", ImageAdapter.s_imageList.get(ImagePreviewActivity.s_nCurrentImageIndex).getM_strImagePath());
            bundle.putString("activity", "ImagePreviewActivity");
            intent.putExtras(bundle);
            ImagePreviewActivity.this.startActivity(intent);
            ImagePreviewActivity.this.m_popupWindow.dismiss();
        }
    }

    private class PopUpWindowPrintOnClickListener implements View.OnClickListener {
        private PopUpWindowPrintOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            ImagePreviewActivity.this.print(ImageAdapter.s_imageList.get(ImagePreviewActivity.s_nCurrentImageIndex).getM_strImagePath());
            ImagePreviewActivity.this.m_popupWindow.dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void print(String str) {
        Intent intent = new Intent();
        intent.setClass(this, PrintActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("filePath", str);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private class PopUpWindowShareOnClickListener implements View.OnClickListener {
        private PopUpWindowShareOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Public_Tools.share(ImagePreviewActivity.this, ImageAdapter.s_imageList.get(ImagePreviewActivity.s_nCurrentImageIndex).getM_strImagePath());
            ImagePreviewActivity.this.m_popupWindow.dismiss();
        }
    }

    public ImagePreview getImagePreview() {
        return this.m_imagePreview;
    }

    public void setImagePreview(ImagePreview imagePreview) {
        this.m_imagePreview = imagePreview;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPlayUri(final Service service, final String str) {
        g_bIsSuc = false;
        try {
            this.m_ipmsgApp.getControlPoint().execute(new SetAVTransportURI(service, str, "NO METADATA") { // from class: com.netfeige.display.ui.ImagePreviewActivity.5
                @Override // org.teleal.cling.support.avtransport.callback.SetAVTransportURI, org.teleal.cling.controlpoint.ActionCallback
                public void success(ActionInvocation actionInvocation) {
                    ImagePreviewActivity.this.m_nNum1 = 0;
                    System.out.println("dlna+SetAVTransportURI : success");
                    ImagePreviewActivity.this.playMedia(service);
                    super.success(actionInvocation);
                }

                @Override // org.teleal.cling.controlpoint.ActionCallback
                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str2) {
                    System.out.println("dlnaSetAVTransportURI :" + ImagePreviewActivity.this.m_nNum1 + " failure");
                    if (ImagePreviewActivity.this.m_nNum1 < Global.g_nNumber) {
                        ImagePreviewActivity.this.setPlayUri(service, str);
                        ImagePreviewActivity.access$2208(ImagePreviewActivity.this);
                    } else {
                        ImagePreviewActivity.this.m_nNum1 = 0;
                        System.out.println("dlna无法设置路径");
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playMedia(final Service service) {
        try {
            this.m_ipmsgApp.getControlPoint().execute(new Play(service) { // from class: com.netfeige.display.ui.ImagePreviewActivity.6
                @Override // org.teleal.cling.support.avtransport.callback.Play, org.teleal.cling.controlpoint.ActionCallback
                public void success(ActionInvocation actionInvocation) {
                    ImagePreviewActivity.this.m_nNum = 0;
                    ImagePreviewActivity.g_bIsSuc = true;
                    System.out.println("dlnaPlay : success");
                    super.success(actionInvocation);
                }

                @Override // org.teleal.cling.controlpoint.ActionCallback
                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str) {
                    System.out.println("dlnaPlay :" + ImagePreviewActivity.this.m_nNum + " failure");
                    if (ImagePreviewActivity.this.m_nNum < Global.g_nNumber) {
                        ImagePreviewActivity.this.playMedia(service);
                        ImagePreviewActivity.access$2508(ImagePreviewActivity.this);
                    } else {
                        ImagePreviewActivity.this.m_nNum = 0;
                        System.out.println("dlna:无法播放");
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}

