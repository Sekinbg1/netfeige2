package com.netfeige.common;

import android.content.Context;
import android.media.MediaPlayer;

/* JADX INFO: loaded from: classes.dex */
public class SoundPlayer {
    int resId = -1;
    MediaPlayer player = null;

    public void play(Context context, int i, boolean z) {
        if (context == null) {
            return;
        }
        if (this.resId != i) {
            this.resId = i;
            MediaPlayer mediaPlayer = this.player;
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    this.player.stop();
                }
                this.player.release();
                this.player = null;
            }
            this.player = MediaPlayer.create(context, i);
        }
        if (this.player.isPlaying()) {
            this.player.pause();
        }
        this.player.setLooping(z);
        this.player.seekTo(0);
        this.player.start();
    }

    public void stop() {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }
}

