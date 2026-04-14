package org.teleal.common.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class Gfx {
	// Android compatible version - replaced AWT with Android graphics
	// This is a stub implementation as the original AWT code cannot run on Android
	public static byte[] resizeProportionally(Object imageIcon, String str, int i, int i2) throws IOException {
		// Android does not support java.awt or javax.swing
		// This method should be replaced with Android-specific image processing
		// using BitmapFactory and Bitmap.createScaledBitmap()
		throw new UnsupportedOperationException("AWT-based image processing is not supported on Android. Use Android Bitmap APIs instead.");
	}

	public static Object getScaledInstance(Object bufferedImage, int i, int i2, Object renderingHints, boolean b) {
		// Android does not support java.awt.image.BufferedImage
		// This method should be replaced with Android-specific image processing
		// using Bitmap.createScaledBitmap()
		throw new UnsupportedOperationException("AWT-based image processing is not supported on Android. Use Android Bitmap APIs instead.");
	}
}

