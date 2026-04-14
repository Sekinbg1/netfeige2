package com.geniusgithub.mediarender.music;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.view.ViewCompat;
import com.geniusgithub.mediarender.util.CommonLog;
import com.geniusgithub.mediarender.util.LogFactory;

/* JADX INFO: loaded from: classes.dex */
public class ImageUtils {
    private static final CommonLog log = LogFactory.createLog();

    public static Bitmap createRotateReflectedMap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(200.0f / bitmap.getWidth(), 200.0f / bitmap.getHeight());
        return createRotateImage(createReflectedImage(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true)));
    }

    public static Bitmap createRotateReflectedMap(Context context, Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        if (bitmap == null) {
            return null;
        }
        log.e("bitmap is not null");
        return createRotateReflectedMap(bitmap);
    }

    public static Bitmap createRotateImage(Bitmap bitmap) {
        Camera camera = new Camera();
        camera.save();
        camera.rotateY(10.0f);
        Matrix matrix = new Matrix();
        camera.getMatrix(matrix);
        camera.restore();
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap createReflectedImage(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(1.0f, -1.0f);
        int i = height / 2;
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(bitmap, 0, i, width, i, matrix, false);
        Bitmap bitmapCreateBitmap2 = Bitmap.createBitmap(width, i + height + 4, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap2);
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
        float f = height;
        float f2 = width;
        float f3 = height + 4;
        canvas.drawRect(0.0f, f, f2, f3, new Paint());
        canvas.drawBitmap(bitmapCreateBitmap, 0.0f, f3, (Paint) null);
        Paint paint = new Paint();
        paint.setShader(new LinearGradient(0.0f, bitmap.getHeight(), 0.0f, bitmapCreateBitmap2.getHeight(), 1895825407, ViewCompat.MEASURED_SIZE_MASK, Shader.TileMode.MIRROR));
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawRect(0.0f, f, f2, bitmapCreateBitmap2.getHeight(), paint);
        return bitmapCreateBitmap2;
    }
}

