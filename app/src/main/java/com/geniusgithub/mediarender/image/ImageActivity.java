package com.geniusgithub.mediarender.image;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.geniusgithub.mediarender.center.DlnaMediaModel;
import com.geniusgithub.mediarender.center.DlnaMediaModelFactory;
import com.geniusgithub.mediarender.center.MediaControlBrocastFactory;
import com.geniusgithub.mediarender.image.DownLoadHelper;
import com.geniusgithub.mediarender.util.CommonLog;
import com.geniusgithub.mediarender.util.CommonUtil;
import com.geniusgithub.mediarender.util.FileHelper;
import com.geniusgithub.mediarender.util.LogFactory;
import com.netfeige.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/* JADX INFO: loaded from: classes.dex */
public class ImageActivity extends Activity implements MediaControlBrocastFactory.IMediaControlListener, DownLoadHelper.IDownLoadCallback {
    private static final int EXIT_ACTIVITY = 2;
    private static final int EXIT_DELAY_TIME = 2000;
    private static final int REFRESH_SPEED = 1;
    private static final CommonLog log = LogFactory.createLog();
    private DelCacheFileManager mDelCacheFileManager;
    private DownLoadHelper mDownLoadHelper;
    private Handler mHandler;
    private MediaControlBrocastFactory mMediaControlBorcastFactor;
    private UIManager mUIManager;
    private int mScreenWidth = 0;
    private int mScreenHeight = 0;
    private DlnaMediaModel mMediaInfo = new DlnaMediaModel();

    @Override // com.geniusgithub.mediarender.center.MediaControlBrocastFactory.IMediaControlListener
    public void onPauseCommand() {
    }

    @Override // com.geniusgithub.mediarender.center.MediaControlBrocastFactory.IMediaControlListener
    public void onPlayCommand() {
    }

    @Override // com.geniusgithub.mediarender.center.MediaControlBrocastFactory.IMediaControlListener
    public void onSeekCommand(int i) {
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        log.e("onCreate");
        getWindow().addFlags(128);
        setContentView(R.layout.image_player_layout);
        initView();
        initData();
        refreshIntent(getIntent());
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        log.e("onDestroy");
        this.mMediaControlBorcastFactor.unregister();
        this.mDownLoadHelper.unInit();
        this.mDelCacheFileManager.start(FileManager.getSaveRootDir(this));
        super.onDestroy();
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        refreshIntent(intent);
    }

    private void initView() {
        this.mUIManager = new UIManager();
    }

    private void initData() {
        this.mScreenWidth = CommonUtil.getScreenWidth(this);
        this.mScreenHeight = CommonUtil.getScreenHeight(this);
        MediaControlBrocastFactory mediaControlBrocastFactory = new MediaControlBrocastFactory(this);
        this.mMediaControlBorcastFactor = mediaControlBrocastFactory;
        mediaControlBrocastFactory.register(this);
        DownLoadHelper downLoadHelper = new DownLoadHelper();
        this.mDownLoadHelper = downLoadHelper;
        downLoadHelper.init();
        this.mDelCacheFileManager = new DelCacheFileManager();
        this.mHandler = new Handler() { // from class: com.geniusgithub.mediarender.image.ImageActivity.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what != 2) {
                    return;
                }
                ImageActivity.this.finish();
            }
        };
    }

    private void refreshIntent(Intent intent) {
        removeExitMessage();
        if (intent != null) {
            this.mMediaInfo = DlnaMediaModelFactory.createFromIntent(intent);
        }
        String url = this.mMediaInfo.getUrl();
        String saveFullPath = FileManager.getSaveFullPath(url, this);
        if (saveFullPath == null || saveFullPath.length() < 1) {
            return;
        }
        this.mUIManager.showProgress(true);
        this.mDownLoadHelper.syncDownLoadFile(url, saveFullPath, this);
    }

    private void removeExitMessage() {
        this.mHandler.removeMessages(2);
    }

    private void delayToExit() {
        removeExitMessage();
        this.mHandler.sendEmptyMessageDelayed(2, 2000L);
    }

    class UIManager {
        public ImageView mImageView;
        public boolean mIsScalBitmap = false;
        public View mLoadView;
        public Bitmap recycleBitmap;

        public UIManager() {
            initView();
        }

        private void initView() {
            this.mImageView = (ImageView) ImageActivity.this.findViewById(R.id.imageview);
            this.mLoadView = ImageActivity.this.findViewById(R.id.show_load_progress);
        }

        public void setBitmap(Bitmap bitmap) {
            Bitmap bitmap2 = this.recycleBitmap;
            if (bitmap2 != null && !bitmap2.isRecycled()) {
                this.mImageView.setImageBitmap(null);
                this.recycleBitmap.recycle();
                this.recycleBitmap = null;
            }
            if (this.mIsScalBitmap) {
                this.mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } else {
                this.mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
            this.recycleBitmap = bitmap;
            this.mImageView.setImageBitmap(bitmap);
        }

        public boolean isLoadViewShow() {
            return this.mLoadView.getVisibility() == 0;
        }

        public void showProgress(boolean z) {
            if (z) {
                this.mLoadView.setVisibility(0);
            } else {
                this.mLoadView.setVisibility(8);
            }
        }

        public void showLoadFailTip() {
            showToask(R.string.load_image_fail);
        }

        public void showParseFailTip() {
            showToask(R.string.parse_image_fail);
        }

        private void showToask(int i) {
            Toast.makeText(ImageActivity.this, i, 0).show();
        }
    }

    @Override // com.geniusgithub.mediarender.image.DownLoadHelper.IDownLoadCallback
    public void downLoadResult(boolean z, String str) {
        onTransDelLoadResult(z, str);
    }

    private void onTransDelLoadResult(final boolean z, final String str) {
        runOnUiThread(new Runnable() { // from class: com.geniusgithub.mediarender.image.ImageActivity.2
            @Override // java.lang.Runnable
            public void run() {
                ImageActivity.this.mUIManager.showProgress(false);
                if (!z) {
                    ImageActivity.this.mUIManager.showLoadFailTip();
                    return;
                }
                Bitmap bitmapDecodeOptionsFile = ImageActivity.this.decodeOptionsFile(str);
                if (bitmapDecodeOptionsFile == null) {
                    ImageActivity.this.mUIManager.showParseFailTip();
                } else {
                    ImageActivity.this.mUIManager.setBitmap(bitmapDecodeOptionsFile);
                }
            }
        });
    }

    public Bitmap decodeOptionsFile(String str) {
        int i;
        Bitmap bitmapDecodeStream;
        try {
            File file = new File(str);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, options);
            int i2 = options.outWidth;
            int i3 = options.outHeight;
            if (i2 <= this.mScreenWidth && i3 <= this.mScreenHeight) {
                this.mUIManager.mIsScalBitmap = false;
                i = 1;
            } else {
                double d = i2;
                Double.isNaN(d);
                double d2 = d * 1.0d;
                double d3 = this.mScreenWidth;
                Double.isNaN(d3);
                double d4 = d2 / d3;
                double d5 = i3;
                Double.isNaN(d5);
                double d6 = d5 * 1.0d;
                double d7 = this.mScreenHeight;
                Double.isNaN(d7);
                double d8 = d6 / d7;
                if (d4 <= d8) {
                    d4 = d8;
                }
                i = (int) (d4 + 0.5d);
                this.mUIManager.mIsScalBitmap = true;
            }
            if (i == 1) {
                bitmapDecodeStream = BitmapFactory.decodeStream(new FileInputStream(file));
                if (bitmapDecodeStream != null) {
                    log.e("scale = 1 bitmap.size = " + (bitmapDecodeStream.getRowBytes() * bitmapDecodeStream.getHeight()));
                }
            } else {
                BitmapFactory.Options options2 = new BitmapFactory.Options();
                options2.inSampleSize = i;
                bitmapDecodeStream = BitmapFactory.decodeStream(new FileInputStream(file), null, options2);
                if (bitmapDecodeStream != null) {
                    log.e("scale = " + options2.inSampleSize + " bitmap.size = " + (bitmapDecodeStream.getRowBytes() * bitmapDecodeStream.getHeight()));
                }
            }
            return bitmapDecodeStream;
        } catch (FileNotFoundException e) {
            log.e("fileNotFoundException, e: " + e.toString());
            return null;
        }
    }

    class DelCacheFileManager implements Runnable {
        private String mFilePath;
        private Thread mThread;

        public DelCacheFileManager() {
        }

        @Override // java.lang.Runnable
        public void run() {
            long jCurrentTimeMillis = System.currentTimeMillis();
            ImageActivity.log.e("DelCacheFileManager run...");
            try {
                FileHelper.deleteDirectory(this.mFilePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            long jCurrentTimeMillis2 = System.currentTimeMillis() - jCurrentTimeMillis;
            ImageActivity.log.e("DelCacheFileManager del over, cost time = " + jCurrentTimeMillis2);
        }

        public boolean start(String str) {
            Thread thread = this.mThread;
            if (thread != null && thread.isAlive()) {
                return false;
            }
            this.mFilePath = str;
            Thread thread2 = new Thread(this);
            this.mThread = thread2;
            thread2.start();
            return true;
        }
    }

    @Override // com.geniusgithub.mediarender.center.MediaControlBrocastFactory.IMediaControlListener
    public void onStopCommand() {
        log.e("onStopCommand");
        delayToExit();
    }
}

