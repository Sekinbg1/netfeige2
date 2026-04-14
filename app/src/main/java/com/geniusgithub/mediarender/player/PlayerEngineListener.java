package com.geniusgithub.mediarender.player;

import com.geniusgithub.mediarender.center.DlnaMediaModel;

/* JADX INFO: loaded from: classes.dex */
public interface PlayerEngineListener {
    void onTrackPause(DlnaMediaModel dlnaMediaModel);

    void onTrackPlay(DlnaMediaModel dlnaMediaModel);

    void onTrackPlayComplete(DlnaMediaModel dlnaMediaModel);

    void onTrackPrepareComplete(DlnaMediaModel dlnaMediaModel);

    void onTrackPrepareSync(DlnaMediaModel dlnaMediaModel);

    void onTrackStop(DlnaMediaModel dlnaMediaModel);

    void onTrackStreamError(DlnaMediaModel dlnaMediaModel);
}

