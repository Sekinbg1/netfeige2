package com.geniusgithub.mediarender.center;

import android.content.Context;
import android.content.Intent;
import com.geniusgithub.mediarender.service.MediaRenderService;
import com.geniusgithub.mediarender.util.CommonLog;
import com.geniusgithub.mediarender.util.LogFactory;
import com.netfeige.display.data.IpmsgApplication;

/* JADX INFO: loaded from: classes.dex */
public class MediaRenderProxy implements IBaseEngine {
    private static final CommonLog log = LogFactory.createLog();
    private static MediaRenderProxy mInstance;
    private Context mContext;

    private MediaRenderProxy(Context context) {
        this.mContext = context;
    }

    public static synchronized MediaRenderProxy getInstance() {
        if (mInstance == null) {
            mInstance = new MediaRenderProxy(IpmsgApplication.getInstance());
        }
        return mInstance;
    }

    @Override // com.geniusgithub.mediarender.center.IBaseEngine
    public boolean startEngine() {
        Intent intent = new Intent(this.mContext, (Class<?>) MediaRenderService.class);
        intent.setAction(MediaRenderService.START_RENDER_ENGINE);
        this.mContext.startService(intent);
        return true;
    }

    @Override // com.geniusgithub.mediarender.center.IBaseEngine
    public boolean stopEngine() {
        this.mContext.stopService(new Intent(this.mContext, (Class<?>) MediaRenderService.class));
        return true;
    }

    @Override // com.geniusgithub.mediarender.center.IBaseEngine
    public boolean restartEngine() {
        Intent intent = new Intent(this.mContext, (Class<?>) MediaRenderService.class);
        intent.setAction(MediaRenderService.RESTART_RENDER_ENGINE);
        this.mContext.startService(intent);
        return true;
    }
}

