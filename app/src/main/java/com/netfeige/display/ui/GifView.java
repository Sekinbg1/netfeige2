package com.netfeige.display.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import com.netfeige.R;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/* JADX INFO: loaded from: classes.dex */
public class GifView extends View {
    private Movie gifMovie;
    private long lStartTime;

    public GifView(Context context, AttributeSet attributeSet) {
        int resourceId;
        super(context, attributeSet);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.GifView);
        int length = typedArrayObtainStyledAttributes.length();
        for (int i = 0; i < length; i++) {
            if (typedArrayObtainStyledAttributes.getIndex(i) == 0 && (resourceId = typedArrayObtainStyledAttributes.getResourceId(0, 0)) != 0) {
                setSrc(resourceId);
            }
        }
        typedArrayObtainStyledAttributes.recycle();
    }

    public void setSrc(int i) {
        this.gifMovie = Movie.decodeStream(getResources().openRawResource(i));
    }

    public void setSrc(String str) {
        BufferedInputStream bufferedInputStream;
        BufferedInputStream bufferedInputStream2 = null;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(new File(str)), 16384);
            try {
                bufferedInputStream.mark(16384);
            } catch (FileNotFoundException e) {
                Exception e = null;
                e = e;
                bufferedInputStream2 = bufferedInputStream;
                e.printStackTrace();
                bufferedInputStream = bufferedInputStream2;
            }
        } catch (FileNotFoundException e2) {
            Exception e = null;
            e = e2;
        }
        this.gifMovie = Movie.decodeStream(bufferedInputStream);
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        long jUptimeMillis = SystemClock.uptimeMillis();
        if (this.lStartTime == 0) {
            this.lStartTime = jUptimeMillis;
        }
        Movie movie = this.gifMovie;
        if (movie != null) {
            int iDuration = movie.duration();
            if (iDuration == 0) {
                iDuration = 1000;
            }
            this.gifMovie.setTime((int) ((jUptimeMillis - this.lStartTime) % ((long) iDuration)));
            this.gifMovie.draw(canvas, (getWidth() - this.gifMovie.width()) / 2, getHeight() - this.gifMovie.height());
            invalidate();
        }
    }
}

