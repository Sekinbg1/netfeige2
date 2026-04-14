package com.geniusgithub.mediarender.music;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import com.netfeige.R;

/* JADX INFO: loaded from: classes.dex */
public class VisualizerView extends View {
    private byte[] mBytes;
    private Paint mForePaint;
    private float[] mPoints;
    private Rect mRect;
    private int mSpectrumNum;

    public VisualizerView(Context context) {
        super(context);
        this.mRect = new Rect();
        this.mForePaint = new Paint();
        this.mSpectrumNum = 64;
        init();
    }

    public VisualizerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mRect = new Rect();
        this.mForePaint = new Paint();
        this.mSpectrumNum = 64;
        init();
    }

    private void init() {
        this.mBytes = null;
        this.mForePaint.setStrokeWidth(3.0f);
        this.mForePaint.setAntiAlias(true);
        this.mForePaint.setColor(getResources().getColor(R.color.visualize_fx));
    }

    public void updateVisualizer(byte[] bArr) {
        byte[] bArr2 = new byte[(bArr.length / 2) + 1];
        bArr2[0] = (byte) Math.abs((int) bArr[0]);
        int i = 2;
        for (int i2 = 1; i2 < this.mSpectrumNum; i2++) {
            bArr2[i2] = (byte) Math.hypot(bArr[i], bArr[i + 1]);
            i += 2;
        }
        this.mBytes = bArr2;
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        byte[] bArr = this.mBytes;
        if (bArr == null) {
            return;
        }
        float[] fArr = this.mPoints;
        if (fArr == null || fArr.length < bArr.length * 4) {
            this.mPoints = new float[this.mBytes.length * 4];
        }
        this.mRect.set(0, 0, getWidth(), getHeight());
        int iWidth = this.mRect.width() / this.mSpectrumNum;
        int iHeight = this.mRect.height();
        for (int i = 0; i < this.mSpectrumNum; i++) {
            byte[] bArr2 = this.mBytes;
            if (bArr2[i] < 0) {
                bArr2[i] = 127;
            }
            float[] fArr2 = this.mPoints;
            int i2 = i * 4;
            float f = (iWidth * i) + (iWidth / 2);
            fArr2[i2] = f;
            fArr2[i2 + 1] = iHeight;
            fArr2[i2 + 2] = f;
            fArr2[i2 + 3] = iHeight - (this.mBytes[i] * 2);
        }
        canvas.drawLines(this.mPoints, this.mForePaint);
    }
}

