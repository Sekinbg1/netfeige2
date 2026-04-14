package com.netfeige.display.ui;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Gallery;

/* JADX INFO: loaded from: classes.dex */
public class ImageGallery extends Gallery {
    private AlbumImageView m_albumImageView;
    private Context m_context;
    private float m_fScale;
    private GestureDetector m_gestureScanner;

    public ImageGallery(Context context) {
        super(context);
        this.m_fScale = 0.0f;
    }

    public ImageGallery(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.m_fScale = 0.0f;
    }

    public ImageGallery(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.m_fScale = 0.0f;
        this.m_context = context;
        this.m_gestureScanner = new GestureDetector(new MySimpleGesture());
        setOnTouchListener(new ImageGalleryOnTouchListener());
    }

    private class ImageGalleryOnTouchListener implements View.OnTouchListener {
        float baseValue;
        float originalScale;

        private ImageGalleryOnTouchListener() {
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            View selectedView = ImageGallery.this.getSelectedView();
            if (selectedView instanceof AlbumImageView) {
                ImageGallery.this.m_albumImageView = (AlbumImageView) selectedView;
                if (ImageGallery.this.m_fScale == 0.0f) {
                    ImageGallery imageGallery = ImageGallery.this;
                    imageGallery.m_fScale = imageGallery.m_albumImageView.getScale();
                }
                if (motionEvent.getAction() == 0) {
                    this.baseValue = 0.0f;
                    this.originalScale = ImageGallery.this.m_albumImageView.getScale();
                }
                if (motionEvent.getAction() == 2 && motionEvent.getPointerCount() == 2) {
                    float x = motionEvent.getX(0) - motionEvent.getX(1);
                    float y = motionEvent.getY(0) - motionEvent.getY(1);
                    float fSqrt = (float) Math.sqrt((x * x) + (y * y));
                    float f = this.baseValue;
                    if (f == 0.0f) {
                        this.baseValue = fSqrt;
                    } else {
                        ImageGallery.this.m_albumImageView.zoomTo(this.originalScale * (fSqrt / f), x + motionEvent.getX(1), y + motionEvent.getY(1));
                    }
                }
            }
            return false;
        }
    }

    @Override // android.widget.Gallery, android.view.GestureDetector.OnGestureListener
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        if (ImagePreviewActivity.g_bIsAVTransport) {
            if (!ImagePreviewActivity.g_bIsSuc) {
                return false;
            }
            moveOrSkip(motionEvent, motionEvent2, f, f2);
            return false;
        }
        moveOrSkip(motionEvent, motionEvent2, f, f2);
        return false;
    }

    private void moveOrSkip(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        View selectedView = getSelectedView();
        try {
            if (selectedView instanceof AlbumImageView) {
                AlbumImageView albumImageView = (AlbumImageView) selectedView;
                this.m_albumImageView = albumImageView;
                float[] fArr = new float[9];
                albumImageView.getImageMatrix().getValues(fArr);
                float scale = this.m_albumImageView.getScale() * this.m_albumImageView.getImageWidth();
                float scale2 = this.m_albumImageView.getScale() * this.m_albumImageView.getImageHeight();
                if (((int) scale) <= ImagePreviewActivity.s_nScreenWidth && ((int) scale2) <= ImagePreviewActivity.s_nScreenHeight) {
                    super.onScroll(motionEvent, motionEvent2, f, f2);
                } else {
                    float f3 = fArr[2];
                    float f4 = scale + f3;
                    Rect rect = new Rect();
                    this.m_albumImageView.getGlobalVisibleRect(rect);
                    if (f > 0.0f) {
                        if (rect.left > 0 || f4 < ImagePreviewActivity.s_nScreenWidth) {
                            super.onScroll(motionEvent, motionEvent2, f, f2);
                        } else {
                            this.m_albumImageView.postTranslate(-f, -f2);
                        }
                    } else if (f < 0.0f) {
                        if (rect.right < ImagePreviewActivity.s_nScreenWidth || f3 > 0.0f) {
                            super.onScroll(motionEvent, motionEvent2, f, f2);
                        } else {
                            this.m_albumImageView.postTranslate(-f, -f2);
                        }
                    }
                }
            } else {
                super.onScroll(motionEvent, motionEvent2, f, f2);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    @Override // android.widget.Gallery, android.view.GestureDetector.OnGestureListener
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        onKeyDown(isScrollingLeft(motionEvent, motionEvent2) ? 21 : 22, null);
        return true;
    }

    private boolean isScrollingLeft(MotionEvent motionEvent, MotionEvent motionEvent2) {
        return motionEvent2.getX() > motionEvent.getX();
    }

    @Override // android.widget.Gallery, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        this.m_gestureScanner.onTouchEvent(motionEvent);
        if (motionEvent.getAction() == 1) {
            View selectedView = getSelectedView();
            if (selectedView instanceof AlbumImageView) {
                AlbumImageView albumImageView = (AlbumImageView) selectedView;
                this.m_albumImageView = albumImageView;
                float scale = albumImageView.getScale() * this.m_albumImageView.getImageWidth();
                float scale2 = this.m_albumImageView.getScale() * this.m_albumImageView.getImageHeight();
                if (((int) scale) > ImagePreviewActivity.s_nScreenWidth || ((int) scale2) > ImagePreviewActivity.s_nScreenHeight) {
                    float[] fArr = new float[9];
                    this.m_albumImageView.getImageMatrix().getValues(fArr);
                    float f = fArr[5];
                    float f2 = scale2 + f;
                    if (f > 0.0f) {
                        this.m_albumImageView.postTranslateDur(-f, 200.0f);
                    }
                    Log.i("manga", "bottom:" + f2);
                    if (f2 < ImagePreviewActivity.s_nScreenHeight) {
                        this.m_albumImageView.postTranslateDur(ImagePreviewActivity.s_nScreenHeight - f2, 200.0f);
                    }
                }
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    private class MySimpleGesture extends GestureDetector.SimpleOnGestureListener {
        private MySimpleGesture() {
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            if (ImageGallery.this.m_context instanceof ImagePreviewActivity) {
                ((ImagePreviewActivity) ImageGallery.this.m_context).singleTapup();
            }
            return super.onSingleTapConfirmed(motionEvent);
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
        public boolean onDoubleTap(MotionEvent motionEvent) {
            if (ImageGallery.this.m_albumImageView.getScale() > ImageGallery.this.m_albumImageView.getScaleRate()) {
                ImageGallery.this.m_albumImageView.zoomTo(ImageGallery.this.m_albumImageView.getScaleRate(), ImagePreviewActivity.s_nScreenWidth / 2.0f, ImagePreviewActivity.s_nScreenHeight / 2.0f);
            } else {
                ImageGallery.this.m_albumImageView.zoomTo(3.1f);
            }
            return super.onDoubleTap(motionEvent);
        }
    }
}

