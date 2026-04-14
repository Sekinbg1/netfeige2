package com.netfeige.display.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.graphics.RectF;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.ImageView;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/* JADX INFO: loaded from: classes.dex */
public class AlbumImageView extends ImageView {
	static final float SCALE_RATE = 1.25f;
	private static final String TAG = "ImageViewTouchBase";
	float _dy;
	protected Handler mHandler;
	protected Bitmap m_bitmapImage;
	private final float[] m_fMatrixValues;
	float m_fMaxZoom;
	float m_fMinZoom;
	private float m_fScaleRate;
	private Movie m_gifMovie;
	private long m_lStartTime;
	protected Matrix m_matrixBase;
	private final Matrix m_matrixDisplay;
	protected Matrix m_matrixSupp;
	private int m_nImageHeight;
	private int m_nImageWidth;
	int m_nThisHeight;
	int m_nThisWidth;

	public AlbumImageView(Context context, int i, int i2) {
		super(context);
		this.m_gifMovie = null;
		this.m_matrixBase = new Matrix();
		this.m_matrixSupp = new Matrix();
		this.m_matrixDisplay = new Matrix();
		this.m_fMatrixValues = new float[9];
		this.m_bitmapImage = null;
		this.m_nThisWidth = -1;
		this.m_nThisHeight = -1;
		this.m_fMaxZoom = 3.0f;
		this.mHandler = new Handler();
		this._dy = 0.0f;
		this.m_nImageHeight = i2;
		this.m_nImageWidth = i;
		init();
	}

	public AlbumImageView(Context context, AttributeSet attributeSet, int i, int i2) {
		super(context, attributeSet);
		this.m_gifMovie = null;
		this.m_matrixBase = new Matrix();
		this.m_matrixSupp = new Matrix();
		this.m_matrixDisplay = new Matrix();
		this.m_fMatrixValues = new float[9];
		this.m_bitmapImage = null;
		this.m_nThisWidth = -1;
		this.m_nThisHeight = -1;
		this.m_fMaxZoom = 3.0f;
		this.mHandler = new Handler();
		this._dy = 0.0f;
		this.m_nImageHeight = i2;
		this.m_nImageWidth = i;
		init();
	}

	public void setSrc(String str) {
		BufferedInputStream bufferedInputStream = null;
		try {
			bufferedInputStream = new BufferedInputStream(new FileInputStream(new File(str)), 16384);
			bufferedInputStream.mark(16384);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.m_gifMovie = Movie.decodeStream(bufferedInputStream);
	}

	private void arithScaleRate() {
		this.m_fScaleRate = Math.min(ImagePreviewActivity.s_nScreenWidth / this.m_nImageWidth, ImagePreviewActivity.s_nScreenHeight / this.m_nImageHeight);
	}

	public float getScaleRate() {
		return this.m_fScaleRate;
	}

	public int getImageWidth() {
		return this.m_nImageWidth;
	}

	public void setImageWidth(int i) {
		this.m_nImageWidth = i;
	}

	public int getImageHeight() {
		return this.m_nImageHeight;
	}

	public void setImageHeight(int i) {
		this.m_nImageHeight = i;
	}

	@Override // android.view.View, android.view.KeyEvent.Callback
	public boolean onKeyDown(int i, KeyEvent keyEvent) {
		if (i == 4 && keyEvent.getRepeatCount() == 0) {
			keyEvent.startTracking();
			return true;
		}
		return super.onKeyDown(i, keyEvent);
	}

	@Override // android.view.View, android.view.KeyEvent.Callback
	public boolean onKeyUp(int i, KeyEvent keyEvent) {
		if (i == 4 && keyEvent.isTracking() && !keyEvent.isCanceled() && getScale() > 1.0f) {
			zoomTo(1.0f);
			return true;
		}
		return super.onKeyUp(i, keyEvent);
	}

	@Override // android.widget.ImageView
	public void setImageBitmap(Bitmap bitmap) {
		super.setImageBitmap(bitmap);
		this.m_bitmapImage = bitmap;
		arithScaleRate();
		zoomTo(this.m_fScaleRate, ImagePreviewActivity.s_nScreenWidth / 2.0f, ImagePreviewActivity.s_nScreenHeight / 2.0f);
		layoutToCenter();
	}

	protected void center(boolean z, boolean z2) {
		float f = 0.0f;
		float f2 = 0.0f;
		float height = 0.0f;
		float f3 = 0.0f;
		if (this.m_bitmapImage == null) {
			return;
		}
		Matrix imageViewMatrix = getImageViewMatrix();
		float f4 = 0.0f;
		RectF rectF = new RectF(0.0f, 0.0f, this.m_bitmapImage.getWidth(), this.m_bitmapImage.getHeight());
		imageViewMatrix.mapRect(rectF);
		float fHeight = rectF.height();
		float fWidth = rectF.width();
		if (z2) {
			float height2 = getHeight();
			if (fHeight < height2) {
				height = (height2 - fHeight) / 2.0f;
				f3 = rectF.top;
			} else if (rectF.top > 0.0f) {
				f = -rectF.top;
			} else {
				if (rectF.bottom < height2) {
					height = getHeight();
					f3 = rectF.bottom;
				} else {
					f = 0.0f;
				}
			}
			f = height - f3;
		} else {
			f = 0.0f;
		}
		if (z) {
			float width = getWidth();
			if (fWidth < width) {
				width = (width - fWidth) / 2.0f;
				f2 = rectF.left;
			} else if (rectF.left > 0.0f) {
				f4 = -rectF.left;
			} else if (rectF.right < width) {
				f2 = rectF.right;
			} else {
				f4 = 0.0f;
			}
			f4 = width - f2;
		}
		postTranslate(f4, f);
		setImageMatrix(getImageViewMatrix());
	}

	private void init() {
		setScaleType(ImageView.ScaleType.MATRIX);
	}

	public void layoutToCenter() {
		float scale = this.m_nImageWidth * getScale();
		float f = ImagePreviewActivity.s_nScreenWidth - scale;
		float scale2 = ImagePreviewActivity.s_nScreenHeight - (this.m_nImageHeight * getScale());
		postTranslate(f > 0.0f ? f / 2.0f : 0.0f, scale2 > 0.0f ? scale2 / 2.0f : 0.0f);
		setImageMatrix(getImageViewMatrix());
	}

	protected float getValue(Matrix matrix, int i) {
		matrix.getValues(this.m_fMatrixValues);
		this.m_fMinZoom = (ImagePreviewActivity.s_nScreenWidth / 2.0f) / this.m_nImageWidth;
		return this.m_fMatrixValues[i];
	}

	protected float getScale(Matrix matrix) {
		return getValue(matrix, 0);
	}

	protected float getScale() {
		return getScale(this.m_matrixSupp);
	}

	protected Matrix getImageViewMatrix() {
		this.m_matrixDisplay.set(this.m_matrixBase);
		this.m_matrixDisplay.postConcat(this.m_matrixSupp);
		return this.m_matrixDisplay;
	}

	protected float maxZoom() {
		if (this.m_bitmapImage == null) {
			return 1.0f;
		}
		return Math.max(this.getWidth() / this.m_nThisWidth, this.m_bitmapImage.getHeight() / this.m_nThisHeight) * 4.0f;
	}

	/* JADX WARN: Removed duplicated region for block: B:4:0x0006 A[PHI: r0
0x0006: PHI (r0v4 float) = (r0v0 float), (r0v1 float) binds: [B:3:0x0004, B:6:0x000c] A[DONT_GENERATE, DONT_INLINE]] */
	/*
		Code decompiled incorrectly, please refer to instructions dump.
		To view partially-correct code enable 'Show inconsistent code' option in preferences
	*/
	protected void zoomTo(float r3, float r4, float r5) {
		/*
			r2 = this;
			float r0 = r2.m_fMaxZoom
			int r1 = (r3 > r0 ? 1 : (r3 == r0 ? 0 : -1))
			if (r1 <= 0) goto L8
		L6:
			r3 = r0
			goto Lf
		L8:
			float r0 = r2.m_fMinZoom
			int r1 = (r3 > r0 ? 1 : (r3 == r0 ? 0 : -1))
			if (r1 >= 0) goto Lf
			goto L6
		Lf:
			float r0 = r2.getScale()
			float r3 = r3 / r0
			android.graphics.Matrix r0 = r2.m_matrixSupp
			r0.postScale(r3, r3, r4, r5)
			android.graphics.Matrix r3 = r2.getImageViewMatrix()
			r2.setImageMatrix(r3)
			r3 = 1
			r2.center(r3, r3)
			return
		*/
		throw new UnsupportedOperationException("Method not decompiled: com.netfeige.display.ui.AlbumImageView.zoomTo(float, float, float):void");
	}

	protected void zoomTo(float f, final float f2, final float f3, final float f4) {
		final float scale = (f - getScale()) / f4;
		final float scale2 = getScale();
		final long jCurrentTimeMillis = System.currentTimeMillis();
		this.mHandler.post(new Runnable() { // from class: com.netfeige.display.ui.AlbumImageView.1
			@Override // java.lang.Runnable
			public void run() {
				float fMin = Math.min(f4, System.currentTimeMillis() - jCurrentTimeMillis);
				AlbumImageView.this.zoomTo(scale2 + (scale * fMin), f2, f3);
				if (fMin < f4) {
					AlbumImageView.this.mHandler.post(this);
				}
			}
		});
	}

	protected void zoomTo(float f) {
		zoomTo(f, getWidth() / 2.0f, getHeight() / 2.0f);
	}

	protected void zoomToPoint(float f, float f2, float f3) {
		float width = getWidth() / 2.0f;
		float height = getHeight() / 2.0f;
		panBy(width - f2, height - f3);
		zoomTo(f, width, height);
	}

	protected void zoomIn() {
		zoomIn(SCALE_RATE);
	}

	protected void zoomOut() {
		zoomOut(SCALE_RATE);
	}

	protected void zoomIn(float f) {
		if (getScale() < this.m_fMaxZoom && getScale() > this.m_fMinZoom && this.m_bitmapImage != null) {
			this.m_matrixSupp.postScale(f, f, getWidth() / 2.0f, getHeight() / 2.0f);
			setImageMatrix(getImageViewMatrix());
		}
	}

	protected void zoomOut(float f) {
		if (this.m_bitmapImage == null) {
			return;
		}
		float width = getWidth() / 2.0f;
		float height = getHeight() / 2.0f;
		Matrix matrix = new Matrix(this.m_matrixSupp);
		float f2 = 1.0f / f;
		matrix.postScale(f2, f2, width, height);
		if (getScale(matrix) < 1.0f) {
			this.m_matrixSupp.setScale(1.0f, 1.0f, width, height);
		} else {
			this.m_matrixSupp.postScale(f2, f2, width, height);
		}
		setImageMatrix(getImageViewMatrix());
		center(true, true);
	}

	public void postTranslate(float f, float f2) {
		this.m_matrixSupp.postTranslate(f, f2);
		setImageMatrix(getImageViewMatrix());
	}

	protected void postTranslateDur(float f, final float f2) {
		this._dy = 0.0f;
		final float f3 = f / f2;
		final long jCurrentTimeMillis = System.currentTimeMillis();
		this.mHandler.post(new Runnable() { // from class: com.netfeige.display.ui.AlbumImageView.2
			@Override // java.lang.Runnable
			public void run() {
				float fMin = Math.min(f2, System.currentTimeMillis() - jCurrentTimeMillis);
				AlbumImageView albumImageView = AlbumImageView.this;
				albumImageView.postTranslate(0.0f, (f3 * fMin) - albumImageView._dy);
				AlbumImageView.this._dy = f3 * fMin;
				if (fMin < f2) {
					AlbumImageView.this.mHandler.post(this);
				}
			}
		});
	}

	protected void panBy(float f, float f2) {
		postTranslate(f, f2);
		setImageMatrix(getImageViewMatrix());
	}

	@Override // android.widget.ImageView, android.view.View
	protected void onDraw(Canvas canvas) {
		if (this.m_gifMovie == null) {
			try {
				if (this.m_nImageWidth * getScale() > ImagePreviewActivity.s_nScreenWidth) {
					center(false, true);
				} else {
					center(true, true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			super.onDraw(canvas);
			return;
		}
		long jUptimeMillis = SystemClock.uptimeMillis();
		if (this.m_lStartTime == 0) {
			this.m_lStartTime = jUptimeMillis;
		}
		Movie movie = this.m_gifMovie;
		if (movie != null) {
			int iDuration = movie.duration();
			if (iDuration == 0) {
				iDuration = 1000;
			}
			this.m_gifMovie.setTime((int) ((jUptimeMillis - this.m_lStartTime) % ((long) iDuration)));
			this.m_gifMovie.draw(canvas, (getWidth() - this.m_gifMovie.width()) / 2, (getHeight() - this.m_gifMovie.height()) / 2);
			invalidate();
		}
	}
}
