package com.netfeige.common;

import android.graphics.Bitmap;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.ViewCompat;
import java.io.InputStream;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class GifOpenHelper {
	protected static final int MaxStackSize = 4096;
	public static final int STATUS_FORMAT_ERROR = 1;
	public static final int STATUS_OK = 0;
	public static final int STATUS_OPEN_ERROR = 2;
	protected int[] act;
	protected int bgColor;
	protected int bgIndex;
	protected int frameCount;
	protected Vector<GifFrame> frames;
	protected int[] gct;
	protected boolean gctFlag;
	protected int gctSize;
	protected int height;
	protected int ih;
	protected Bitmap image;
	protected InputStream in;
	protected boolean interlace;
	protected int iw;
	protected int ix;
	protected int iy;
	protected int lastBgColor;
	protected Bitmap lastImage;
	protected int[] lct;
	protected boolean lctFlag;
	protected int lctSize;
	protected int lrh;
	protected int lrw;
	protected int lrx;
	protected int lry;
	protected int pixelAspect;
	protected byte[] pixelStack;
	protected byte[] pixels;
	protected short[] prefix;
	protected int status;
	protected byte[] suffix;
	protected int transIndex;
	protected int width;
	protected int loopCount = 1;
	protected int frameindex = 0;
	protected byte[] block = new byte[256];
	protected int blockSize = 0;
	protected int dispose = 0;
	protected int lastDispose = 0;
	protected boolean transparency = false;
	protected int delay = 0;

	class GifFrame {
		public int delay;
		public Bitmap image;

		public GifFrame(Bitmap bitmap, int i) {
			this.image = bitmap;
			this.delay = i;
		}
	}

	public int getFrameindex() {
		return this.frameindex;
	}

	public void setFrameindex(int i) {
		this.frameindex = i;
		int size = this.frames.size() - 1;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeigh() {
		return this.height;
	}

	public int getDelay(int i) {
		this.delay = -1;
		if (i >= 0 && i < this.frameCount) {
			this.delay = this.frames.elementAt(i).delay;
		}
		return this.delay;
	}

	public int getFrameCount() {
		return this.frameCount;
	}

	public Bitmap getImage() {
		return getFrame(0);
	}

	public int getLoopCount() {
		return this.loopCount;
	}

	protected void setPixels() {
		int i;
		int[] iArr = new int[this.width * this.height];
		int i2 = this.lastDispose;
		int i3 = 0;
		if (i2 > 0) {
			if (i2 == 3) {
				int i4 = this.frameCount - 2;
				if (i4 > 0) {
					this.lastImage = getFrame(i4 - 1);
				} else {
					this.lastImage = null;
				}
			}
			Bitmap bitmap = this.lastImage;
			if (bitmap != null) {
				int i5 = this.width;
				bitmap.getPixels(iArr, 0, i5, 0, 0, i5, this.height);
				if (this.lastDispose == 2) {
					int i6 = !this.transparency ? this.lastBgColor : 0;
					for (int i7 = 0; i7 < this.lrh; i7++) {
						int i8 = ((this.lry + i7) * this.width) + this.lrx;
						int i9 = this.lrw + i8;
						while (i8 < i9) {
							iArr[i8] = i6;
							i8++;
						}
					}
				}
			}
		}
		int i10 = 8;
		int i11 = 0;
		int i12 = 1;
		while (true) {
			int i13 = this.ih;
			if (i3 < i13) {
				if (this.interlace) {
					if (i11 >= i13) {
						i12++;
						if (i12 == 2) {
							i11 = 4;
						} else if (i12 == 3) {
							i10 = 4;
							i11 = 2;
						} else if (i12 == 4) {
							i10 = 2;
							i11 = 1;
						}
					}
					i = i11 + i10;
				} else {
					i = i11;
					i11 = i3;
				}
				int i14 = i11 + this.iy;
				if (i14 < this.height) {
					int i15 = this.width;
					int i16 = i14 * i15;
					int i17 = this.ix + i16;
					int i18 = this.iw + i17;
					if (i16 + i15 < i18) {
						i18 = i16 + i15;
					}
					int i19 = this.iw * i3;
					while (i17 < i18) {
						int i20 = i19 + 1;
						int i21 = this.act[this.pixels[i19] & 255];
						if (i21 != 0) {
							iArr[i17] = i21;
						}
						i17++;
						i19 = i20;
					}
				}
				i3++;
				i11 = i;
			} else {
				this.image = Bitmap.createBitmap(iArr, this.width, this.height, Bitmap.Config.ARGB_4444);
				return;
			}
		}
	}

	public Bitmap getFrame(int i) {
		if (i < 0 || i >= this.frameCount) {
			return null;
		}
		return this.frames.elementAt(i).image;
	}

	public Bitmap nextBitmap() {
		int i = this.frameindex + 1;
		this.frameindex = i;
		if (i > this.frames.size() - 1) {
			this.frameindex = 0;
		}
		return this.frames.elementAt(this.frameindex).image;
	}

	public int nextDelay() {
		return this.frames.elementAt(this.frameindex).delay;
	}

	public int read(InputStream inputStream) {
		init();
		if (inputStream != null) {
			this.in = inputStream;
			readHeader();
			if (!err()) {
				readContents();
				if (this.frameCount < 0) {
					this.status = 1;
				}
			}
		} else {
			this.status = 2;
		}
		try {
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.status;
	}

	/* JADX WARN: Multi-variable type inference failed */
	/* JADX WARN: Type inference failed for: r2v20 */
	/* JADX WARN: Type inference failed for: r2v21 */
	/* JADX WARN: Type inference failed for: r2v22 */
	/* JADX WARN: Type inference failed for: r2v25, types: [short] */
	/* JADX WARN: Type inference failed for: r2v27 */
	protected void decodeImageData() {
		int i;
		int i2;
		int i3;
		int i4;
		int i5;
		short s;
		int i6 = this.iw * this.ih;
		byte[] bArr = this.pixels;
		if (bArr == null || bArr.length < i6) {
			this.pixels = new byte[i6];
		}
		if (this.prefix == null) {
			this.prefix = new short[4096];
		}
		if (this.suffix == null) {
			this.suffix = new byte[4096];
		}
		if (this.pixelStack == null) {
			this.pixelStack = new byte[FragmentTransaction.TRANSIT_FRAGMENT_OPEN];
		}
		int i7 = read();
		int i8 = 1 << i7;
		int i9 = i8 + 1;
		int i10 = i8 + 2;
		int i11 = i7 + 1;
		int i12 = (1 << i11) - 1;
		for (int i13 = 0; i13 < i8; i13++) {
			this.prefix[i13] = 0;
			this.suffix[i13] = (byte) i13;
		}
		int i14 = i11;
		int i15 = i10;
		int i16 = i12;
		int i17 = -1;
		int i18 = 0;
		int i19 = 0;
		int i20 = 0;
		int i21 = 0;
		int block = 0;
		int i22 = 0;
		int i23 = 0;
		int i24 = 0;
		i = i11;
		i2 = i9;
		i3 = i8;
		i4 = 0;
		i5 = 0;
		s = 0;
		while (i18 < i6) {
			if (i19 != 0) {
				i = i11;
				i2 = i9;
				int i25 = i23;
				i3 = i8;
				i4 = i25;
			} else if (i20 < i14) {
				if (block == 0) {
					block = readBlock();
					if (block <= 0) {
						break;
					} else {
						i22 = 0;
					}
				}
				i21 += (this.block[i22] & 255) << i20;
				i20 += 8;
				i22++;
				block--;
			} else {
				int i26 = i21 & i16;
				i21 >>= i14;
				i20 -= i14;
				if (i26 > i15 || i26 == i9) {
					break;
				}
				if (i26 == i8) {
					i14 = i11;
					i15 = i10;
					i16 = i12;
					i17 = -1;
				} else if (i17 == -1) {
					this.pixelStack[i19] = this.suffix[i26];
					i17 = i26;
					i23 = i17;
					i19++;
					i11 = i11;
				} else {
					i = i11;
					if (i26 == i15) {
						i5 = i26;
						this.pixelStack[i19] = (byte) i23;
						s = (short) i17;
						i19++;
					} else {
						i5 = i26;
						s = (short) i5;
					}
					while (s > i8) {
						this.pixelStack[i19] = this.suffix[s];
						s = this.prefix[s];
						i19++;
						i8 = i8;
					}
					i3 = i8;
					byte[] bArr2 = this.suffix;
					i4 = bArr2[s] & 255;
					if (i15 >= 4096) {
						break;
					}
					int i27 = i19 + 1;
					i2 = i9;
					byte b = (byte) i4;
					this.pixelStack[i19] = b;
					this.prefix[i15] = (short) i17;
					bArr2[i15] = b;
					i15++;
					if ((i15 & i16) == 0 && i15 < 4096) {
						i14++;
						i16 += i15;
					}
					i19 = i27;
					i17 = i5;
				}
			}
			i19--;
			this.pixels[i24] = this.pixelStack[i19];
			i18++;
			i24++;
			i8 = i3;
			i9 = i2;
			i23 = i4;
			i11 = i;
		}
		for (int i28 = i24; i28 < i6; i28++) {
			this.pixels[i28] = 0;
		}
	}

	protected boolean err() {
		return this.status != 0;
	}

	public void init() {
		this.status = 0;
		this.frameCount = 0;
		this.frames = new Vector<>();
		this.gct = null;
		this.lct = null;
	}

	protected int read() {
		try {
			return this.in.read();
		} catch (Exception unused) {
			this.status = 1;
			return 0;
		}
	}

	protected int readBlock() {
		int i = read();
		this.blockSize = i;
		int i2 = 0;
		if (i > 0) {
			while (i2 < this.blockSize) {
				try {
					int i3 = this.in.read(this.block, i2, this.blockSize - i2);
					if (i3 == -1) {
						break;
					}
					i2 += i3;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (i2 < this.blockSize) {
				this.status = 1;
			}
		}
		return i2;
	}

	protected int[] readColorTable(int i) {
		int i2;
		int i3 = i * 3;
		byte[] bArr = new byte[i3];
		try {
			i2 = this.in.read(bArr);
		} catch (Exception e) {
			e.printStackTrace();
			i2 = 0;
		}
		if (i2 < i3) {
			this.status = 1;
			return null;
		}
		int[] iArr = new int[256];
		int i4 = 0;
		for (int i5 = 0; i5 < i; i5++) {
			int i6 = i4 + 1;
			int i7 = bArr[i4] & 255;
			int i8 = i6 + 1;
			int i9 = bArr[i6] & 255;
			int i10 = i8 + 1;
			iArr[i5] = (i7 << 16) | ViewCompat.MEASURED_STATE_MASK | (i9 << 8) | (bArr[i8] & 255);
			i4 = i10;
		}
		return iArr;
	}

	protected void readContents() {
		boolean z = false;
		while (!z && !err()) {
			int i = read();
			if (i != 0) {
				if (i == 33) {
					int i2 = read();
					if (i2 == 249) {
						readGraphicControlExt();
					} else if (i2 == 255) {
						readBlock();
						String str = "";
						for (int i3 = 0; i3 < 11; i3++) {
							str = str + ((char) this.block[i3]);
						}
						if (str.equals("NETSCAPE2.0")) {
							readNetscapeExt();
						} else {
							skip();
						}
					} else {
						skip();
					}
				} else if (i == 44) {
					readImage();
				} else if (i != 59) {
					this.status = 1;
				} else {
					z = true;
				}
			}
		}
	}

	protected void readGraphicControlExt() {
		read();
		int i = read();
		int i2 = (i & 28) >> 2;
		this.dispose = i2;
		if (i2 == 0) {
			this.dispose = 1;
		}
		this.transparency = (i & 1) != 0;
		this.delay = readShort() * 10;
		this.transIndex = read();
		read();
	}

	protected void readHeader() {
		String str = "";
		for (int i = 0; i < 6; i++) {
			str = str + ((char) read());
		}
		if (!str.startsWith("GIF")) {
			this.status = 1;
			return;
		}
		readLSD();
		if (!this.gctFlag || err()) {
			return;
		}
		int[] colorTable = readColorTable(this.gctSize);
		this.gct = colorTable;
		this.bgColor = colorTable[this.bgIndex];
	}

	protected void readImage() {
		this.ix = readShort();
		this.iy = readShort();
		this.iw = readShort();
		this.ih = readShort();
		int i = read();
		int i2 = 0;
		this.lctFlag = (i & 128) != 0;
		this.interlace = (i & 64) != 0;
		int i3 = 2 << (i & 7);
		this.lctSize = i3;
		if (this.lctFlag) {
			int[] colorTable = readColorTable(i3);
			this.lct = colorTable;
			this.act = colorTable;
		} else {
			this.act = this.gct;
			if (this.bgIndex == this.transIndex) {
				this.bgColor = 0;
			}
		}
		if (this.transparency) {
			int[] iArr = this.act;
			int i4 = this.transIndex;
			int i5 = iArr[i4];
			iArr[i4] = 0;
			i2 = i5;
		}
		if (this.act == null) {
			this.status = 1;
		}
		if (err()) {
			return;
		}
		decodeImageData();
		skip();
		if (err()) {
			return;
		}
		this.frameCount++;
		this.image = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_4444);
		setPixels();
		this.frames.addElement(new GifFrame(this.image, this.delay));
		if (this.transparency) {
			this.act[this.transIndex] = i2;
		}
		resetFrame();
	}

	protected void readLSD() {
		this.width = readShort();
		this.height = readShort();
		int i = read();
		this.gctFlag = (i & 128) != 0;
		this.gctSize = 2 << (i & 7);
		this.bgIndex = read();
		this.pixelAspect = read();
	}

	protected void readNetscapeExt() {
		do {
			readBlock();
			byte[] bArr = this.block;
			if (bArr[0] == 1) {
				this.loopCount = ((bArr[2] & 255) << 8) | (bArr[1] & 255);
			}
			if (this.blockSize <= 0) {
				return;
			}
		} while (!err());
	}

	protected int readShort() {
		return read() | (read() << 8);
	}

	protected void resetFrame() {
		this.lastDispose = this.dispose;
		this.lrx = this.ix;
		this.lry = this.iy;
		this.lrw = this.iw;
		this.lrh = this.ih;
		this.lastImage = this.image;
		this.lastBgColor = this.bgColor;
		this.dispose = 0;
		this.transparency = false;
		this.delay = 0;
		this.lct = null;
	}

	protected void skip() {
		do {
			readBlock();
			if (this.blockSize <= 0) {
				return;
			}
		} while (!err());
	}
}
