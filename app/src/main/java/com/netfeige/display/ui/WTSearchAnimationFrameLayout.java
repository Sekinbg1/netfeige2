package com.netfeige.display.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.netfeige.R;
import java.lang.ref.SoftReference;

/* JADX INFO: loaded from: classes.dex */
public class WTSearchAnimationFrameLayout extends FrameLayout {
    private SoftReference<Bitmap> m_bitmapRipple;
    private ImageView[] m_imageVRadars;

    public WTSearchAnimationFrameLayout(Context context) {
        super(context);
        init();
    }

    public WTSearchAnimationFrameLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public WTSearchAnimationFrameLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void loadRadarBitmap() {
        try {
            this.m_bitmapRipple = new SoftReference<>(BitmapFactory.decodeStream(getContext().getResources().openRawResource(R.drawable.searchwifiap)));
        } catch (Exception e) {
            Log.e("WTSearchAnimationFrameLayout", Log.getStackTraceString(e));
        } catch (OutOfMemoryError e2) {
            Log.e("WTSearchAnimationFrameLayout", Log.getStackTraceString(e2));
            System.gc();
        }
    }

    private void init() {
        loadRadarBitmap();
        this.m_imageVRadars = new ImageView[3];
        LayoutInflater.from(getContext()).inflate(R.layout.wt_search_device_anima, this);
        this.m_imageVRadars[0] = (ImageView) findViewById(R.id.radar_ray_1);
        this.m_imageVRadars[1] = (ImageView) findViewById(R.id.radar_ray_2);
        this.m_imageVRadars[2] = (ImageView) findViewById(R.id.radar_ray_3);
    }

    public final void startRadarAnimation() {
        if (this.m_bitmapRipple == null) {
            loadRadarBitmap();
        }
        int i = 0;
        while (true) {
            ImageView[] imageViewArr = this.m_imageVRadars;
            if (i >= imageViewArr.length) {
                return;
            }
            ImageView imageView = imageViewArr[i];
            imageView.setImageBitmap(this.m_bitmapRipple.get());
            imageView.setVisibility(0);
            long j = ((long) i) * 333;
            if (imageView.getAnimation() != null) {
                imageView.getAnimation().start();
            } else {
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 14.0f, 1.0f, 14.0f, 1, 0.5f, 1, 0.5f);
                scaleAnimation.setRepeatCount(-1);
                AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.2f);
                AnimationSet animationSet = new AnimationSet(true);
                animationSet.addAnimation(scaleAnimation);
                animationSet.addAnimation(alphaAnimation);
                animationSet.setDuration(1000L);
                animationSet.setFillEnabled(true);
                animationSet.setFillBefore(true);
                animationSet.setStartOffset(j);
                animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
                animationSet.setAnimationListener(new WTSearchAnimationHandler(this, imageView));
                imageView.setAnimation(animationSet);
                imageView.startAnimation(animationSet);
                i++;
            }
        }
    }

    public final void reset() {
        Bitmap bitmap;
        int i = 0;
        while (true) {
            ImageView[] imageViewArr = this.m_imageVRadars;
            if (i >= imageViewArr.length) {
                break;
            }
            ImageView imageView = imageViewArr[i];
            imageView.setImageBitmap(null);
            imageView.setVisibility(8);
            imageView.clearAnimation();
            i++;
        }
        SoftReference<Bitmap> softReference = this.m_bitmapRipple;
        if (softReference != null && (bitmap = softReference.get()) != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        this.m_bitmapRipple = null;
    }

    final class WTSearchAnimationHandler implements Animation.AnimationListener {
        private ImageView m_imageVRadar;

        @Override // android.view.animation.Animation.AnimationListener
        public final void onAnimationStart(Animation animation) {
        }

        public WTSearchAnimationHandler(WTSearchAnimationFrameLayout wTSearchAnimationFrameLayout, ImageView imageView) {
            this.m_imageVRadar = imageView;
        }

        @Override // android.view.animation.Animation.AnimationListener
        public final void onAnimationEnd(Animation animation) {
            this.m_imageVRadar.setVisibility(8);
        }

        @Override // android.view.animation.Animation.AnimationListener
        public final void onAnimationRepeat(Animation animation) {
            animation.setStartOffset(0L);
        }
    }
}

