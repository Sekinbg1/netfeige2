package com.geniusgithub.mediarender.util;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.teleal.cling.model.ServiceReference;

/* JADX INFO: loaded from: classes.dex */
public class FileHelper {
	private static final int FILE_BUFFER_SIZE = 51200;
	private static final CommonLog log = LogFactory.createLog();

	public static boolean fileIsExist(String str) {
		if (str != null && str.length() >= 1) {
			return new File(str).exists();
		}
		log.e("param invalid, filePath: " + str);
		return false;
	}

	public static InputStream readFile(String str) {
		if (str == null) {
			log.e("Invalid param. filePath: " + str);
			return null;
		}
		try {
			if (fileIsExist(str)) {
				return new FileInputStream(new File(str));
			}
			return null;
		} catch (Exception e) {
			log.e("Exception, ex: " + e.toString());
			return null;
		}
	}

	public static boolean createDirectory(String str) {
		if (str == null) {
			return false;
		}
		File file = new File(str);
		if (file.exists()) {
			return true;
		}
		file.mkdirs();
		return true;
	}

	public static boolean deleteDirectory(String str) {
		if (str == null) {
			log.e("Invalid param. filePath: " + str);
			return false;
		}
		File file = new File(str);
		if (!file.exists()) {
			return false;
		}
		if (file.isDirectory()) {
			File[] fileArrListFiles = file.listFiles();
			for (int i = 0; i < fileArrListFiles.length; i++) {
				if (fileArrListFiles[i].isDirectory()) {
					deleteDirectory(fileArrListFiles[i].getAbsolutePath());
				} else {
					fileArrListFiles[i].delete();
				}
			}
		}
		log.d("delete filePath: " + file.getAbsolutePath());
		file.delete();
		return true;
	}

	public static boolean writeFile(String str, InputStream inputStream) throws Throwable {
		if (str == null || str.length() < 1) {
			log.e("Invalid param. filePath: " + str);
			return false;
		}
		FileOutputStream fileOutputStream = null;
		try {
			File file = new File(str);
			if (file.exists()) {
				deleteDirectory(str);
			}
			String strSubstring = str.substring(0, str.lastIndexOf(ServiceReference.DELIMITER));
			boolean zCreateDirectory = createDirectory(strSubstring);
			if (!zCreateDirectory) {
				log.e("createDirectory fail path = " + strSubstring);
				return false;
			}
			file.createNewFile();
			if (!zCreateDirectory) {
				log.e("createNewFile fail filePath = " + str);
				return false;
			}
			FileOutputStream fileOutputStream2 = new FileOutputStream(file);
			try {
				byte[] bArr = new byte[1024];
				int i = inputStream.read(bArr);
				while (-1 != i) {
					fileOutputStream2.write(bArr, 0, i);
					i = inputStream.read(bArr);
				}
				fileOutputStream2.flush();
				try {
					fileOutputStream2.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			} catch (Exception e2) {
				fileOutputStream = fileOutputStream2;
				e2.printStackTrace();
				if (fileOutputStream != null) {
					try {
						fileOutputStream.close();
					} catch (Exception e3) {
						e3.printStackTrace();
					}
				}
				return false;
			} catch (Throwable th) {
				fileOutputStream = fileOutputStream2;
				if (fileOutputStream != null) {
					try {
						fileOutputStream.close();
					} catch (Exception e4) {
						e4.printStackTrace();
					}
				}
				throw th;
			}
		} catch (Exception e5) {
			return false;
		} catch (Throwable th2) {
			throw th2;
		}
	}

	public static boolean writeFile(String str, String str2) {
		return writeFile(str, str2, false);
	}

	public static boolean writeFile(String str, String str2, boolean z) {
		if (str == null || str2 == null || str.length() < 1 || str2.length() < 1) {
			log.e("Invalid param. filePath: " + str + ", fileContent: " + str2);
			return false;
		}
		try {
			File file = new File(str);
			if (!file.exists() && !file.createNewFile()) {
				return false;
			}
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, z));
			bufferedWriter.write(str2);
			bufferedWriter.flush();
			bufferedWriter.close();
			return true;
		} catch (IOException e) {
			log.e("writeFile ioe: " + e.toString());
			return false;
		}
	}

	public static long getFileSize(String str) {
		if (str == null) {
			log.e("Invalid param. filePath: " + str);
			return 0L;
		}
		File file = new File(str);
		if (file.exists()) {
			return file.length();
		}
		return 0L;
	}

	public static long getFileModifyTime(String str) {
		if (str == null) {
			log.e("Invalid param. filePath: " + str);
			return 0L;
		}
		File file = new File(str);
		if (file.exists()) {
			return file.lastModified();
		}
		return 0L;
	}

	public static boolean setFileModifyTime(String str, long j) {
		if (str == null) {
			log.e("Invalid param. filePath: " + str);
			return false;
		}
		File file = new File(str);
		if (file.exists()) {
			return file.setLastModified(j);
		}
		return false;
	}

	/* JADX WARN: Multi-variable type inference failed */
	/* JADX WARN: Removed duplicated region for block: B:86:0x011a A[EXC_TOP_SPLITTER, SYNTHETIC] */
	/* JADX WARN: Removed duplicated region for block: B:93:0x0121 A[EXC_TOP_SPLITTER, SYNTHETIC] */
	/* JADX WARN: Type inference failed for: r8v0, types: [java.lang.String] */
	/* JADX WARN: Type inference failed for: r8v1 */
	/* JADX WARN: Type inference failed for: r8v14 */
	/* JADX WARN: Type inference failed for: r8v15 */
	/* JADX WARN: Type inference failed for: r8v16 */
	/* JADX WARN: Type inference failed for: r8v17, types: [java.io.OutputStream] */
	/* JADX WARN: Type inference failed for: r8v2 */
	/* JADX WARN: Type inference failed for: r8v22, types: [java.io.FileOutputStream] */
	/* JADX WARN: Type inference failed for: r8v25 */
	/* JADX WARN: Type inference failed for: r8v3, types: [java.io.OutputStream] */
	/* JADX WARN: Type inference failed for: r8v4, types: [java.io.OutputStream] */
	/* JADX WARN: Type inference failed for: r8v5 */
	/* JADX WARN: Type inference failed for: r8v6 */
	/* JADX WARN: Type inference failed for: r8v7 */
	/* JADX WARN: Type inference failed for: r8v8 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
	public static boolean copyFile(android.content.ContentResolver r7, java.lang.String r8, java.lang.String r9) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 346
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
		throw new UnsupportedOperationException("Method not decompiled: com.geniusgithub.mediarender.util.FileHelper.copyFile(android.content.ContentResolver, java.lang.String, java.lang.String):boolean");
	}

	public static byte[] readAll(InputStream inputStream) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
		byte[] bArr = new byte[1024];
		int i = inputStream.read(bArr);
		while (-1 != i) {
			byteArrayOutputStream.write(bArr, 0, i);
			i = inputStream.read(bArr);
		}
		byteArrayOutputStream.flush();
		byteArrayOutputStream.close();
		return byteArrayOutputStream.toByteArray();
	}

	/* JADX WARN: Removed duplicated region for block: B:41:0x0058 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
	public static byte[] readFile(android.content.Context r4, android.net.Uri r5) {
        /*
            r0 = 0
            if (r4 == 0) goto L81
            if (r5 != 0) goto L7
            goto L81
        L7:
            java.lang.String r1 = r5.getScheme()
            java.lang.String r1 = r1.toLowerCase()
            java.lang.String r2 = "file"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L20
            java.lang.String r1 = r5.getPath()
            java.io.InputStream r1 = readFile(r1)
            goto L21
        L20:
            r1 = r0
        L21:
            android.content.ContentResolver r4 = r4.getContentResolver()     // Catch: java.lang.Throwable -> L39 java.lang.Exception -> L3b java.io.FileNotFoundException -> L5c
            java.io.InputStream r1 = r4.openInputStream(r5)     // Catch: java.lang.Throwable -> L39 java.lang.Exception -> L3b java.io.FileNotFoundException -> L5c
            if (r1 != 0) goto L31
            if (r1 == 0) goto L30
            r1.close()     // Catch: java.lang.Exception -> L30
        L30:
            return r0
        L31:
            byte[] r4 = readAll(r1)     // Catch: java.lang.Throwable -> L39 java.lang.Exception -> L3b java.io.FileNotFoundException -> L5c
            r1.close()     // Catch: java.lang.Throwable -> L39 java.lang.Exception -> L3b java.io.FileNotFoundException -> L5c
            return r4
        L39:
            r4 = move-exception
            goto L7b
        L3b:
            r4 = move-exception
            com.geniusgithub.mediarender.util.CommonLog r5 = com.geniusgithub.mediarender.util.FileHelper.log     // Catch: java.lang.Throwable -> L39
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L39
            r2.<init>()     // Catch: java.lang.Throwable -> L39
            java.lang.String r3 = "Exception, ex: "
            r2.append(r3)     // Catch: java.lang.Throwable -> L39
            java.lang.String r4 = r4.toString()     // Catch: java.lang.Throwable -> L39
            r2.append(r4)     // Catch: java.lang.Throwable -> L39
            java.lang.String r4 = r2.toString()     // Catch: java.lang.Throwable -> L39
            r5.e(r4)     // Catch: java.lang.Throwable -> L39
            if (r1 == 0) goto L7a
        L58:
            r1.close()     // Catch: java.lang.Exception -> L7a
            goto L7a
        L5c:
            r4 = move-exception
            com.geniusgithub.mediarender.util.CommonLog r5 = com.geniusgithub.mediarender.util.FileHelper.log     // Catch: java.lang.Throwable -> L39
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L39
            r2.<init>()     // Catch: java.lang.Throwable -> L39
            java.lang.String r3 = "FilNotFoundException, ex: "
            r2.append(r3)     // Catch: java.lang.Throwable -> L39
            java.lang.String r4 = r4.toString()     // Catch: java.lang.Throwable -> L39
            r2.append(r4)     // Catch: java.lang.Throwable -> L39
            java.lang.String r4 = r2.toString()     // Catch: java.lang.Throwable -> L39
            r5.e(r4)     // Catch: java.lang.Throwable -> L39
            if (r1 == 0) goto L7a
            goto L58
        L7a:
            return r0
        L7b:
            if (r1 == 0) goto L80
            r1.close()     // Catch: java.lang.Exception -> L80
        L80:
            throw r4
        L81:
            com.geniusgithub.mediarender.util.CommonLog r1 = com.geniusgithub.mediarender.util.FileHelper.log
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Invalid param. ctx: "
            r2.append(r3)
            r2.append(r4)
            java.lang.String r4 = ", uri: "
            r2.append(r4)
            r2.append(r5)
            java.lang.String r4 = r2.toString()
            r1.e(r4)
            return r0
        */
		throw new UnsupportedOperationException("Method not decompiled: com.geniusgithub.mediarender.util.FileHelper.readFile(android.content.Context, android.net.Uri):byte[]");
	}

	public static boolean writeFile(String str, byte[] bArr) throws Throwable {
		if (str == null || bArr == null) {
			log.e("Invalid param. filePath: " + str + ", content: " + bArr);
			return false;
		}
		FileOutputStream fileOutputStream = null;
		try {
			try {
				String strSubstring = str.substring(0, str.lastIndexOf(ServiceReference.DELIMITER));
				File file = new File(strSubstring);
				if (file.exists() && !file.isDirectory()) {
					file.delete();
				}
				File file2 = new File(str);
				if (file2.exists()) {
					if (file2.isDirectory()) {
						deleteDirectory(str);
					} else {
						file2.delete();
					}
				}
				File file3 = new File(strSubstring + File.separator);
				if (!file3.exists() && !file3.mkdirs()) {
					log.e("Can't make dirs, path=" + strSubstring);
				}
				FileOutputStream fileOutputStream2 = new FileOutputStream(str);
				try {
					fileOutputStream2.write(bArr);
					fileOutputStream2.flush();
					fileOutputStream2.close();
					file3.setLastModified(System.currentTimeMillis());
					return true;
				} catch (Exception e) {
					fileOutputStream = fileOutputStream2;
					log.e("Exception, ex: " + e.toString());
					if (fileOutputStream != null) {
						try {
							fileOutputStream.close();
						} catch (Exception unused) {
						}
					}
					return false;
				} catch (Throwable th) {
					fileOutputStream = fileOutputStream2;
					if (fileOutputStream != null) {
						try {
							fileOutputStream.close();
						} catch (Exception unused2) {
						}
					}
					throw th;
				}
			} catch (Exception e2) {
			}
		} catch (Throwable th2) {
		}
		return false;
	}

	public static boolean readZipFile(String str, StringBuffer stringBuffer) {
		try {
			ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(str));
			while (true) {
				ZipEntry nextEntry = zipInputStream.getNextEntry();
				if (nextEntry != null) {
					stringBuffer.append(nextEntry.getCrc() + ", size: " + nextEntry.getSize());
				} else {
					zipInputStream.close();
					return true;
				}
			}
		} catch (Exception e) {
			log.e("Exception: " + e.toString());
			return false;
		}
	}

	public static byte[] readGZipFile(String str) {
		if (!fileIsExist(str)) {
			return null;
		}
		log.i("zipFileName: " + str);
		try {
			FileInputStream fileInputStream = new FileInputStream(str);
			byte[] bArr = new byte[1024];
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			while (true) {
				int i = fileInputStream.read(bArr, 0, 1024);
				if (i != -1) {
					byteArrayOutputStream.write(bArr, 0, i);
				} else {
					return byteArrayOutputStream.toByteArray();
				}
			}
		} catch (Exception unused) {
			log.i("read zipRecorder file error");
			return null;
		}
	}

	public static boolean zipFile(String str, String str2, String str3) throws Throwable {
		boolean zDirToZip;
		if (str != null && !"".equals(str)) {
			File file = new File(str);
			if (file.exists() && file.isDirectory()) {
				String absolutePath = file.getAbsolutePath();
				ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(new File(str3)));
				File file2 = new File(file, str2);
				if (file2.isFile()) {
					zDirToZip = fileToZip(absolutePath, file2, zipOutputStream);
				} else {
					zDirToZip = dirToZip(absolutePath, file2, zipOutputStream);
				}
				zipOutputStream.close();
				return zDirToZip;
			}
		}
		return false;
	}

	public static boolean unZipFile(String str, String str2) throws Exception {
		File file = new File(str2);
		if (!file.exists()) {
			file.mkdirs();
		}
		ZipFile zipFile = new ZipFile(str);
		Enumeration<? extends ZipEntry> enumerationEntries = zipFile.entries();
		byte[] bArr = new byte[FILE_BUFFER_SIZE];
		log.i("unZipDir: " + str2);
		while (enumerationEntries.hasMoreElements()) {
			ZipEntry zipEntryNextElement = enumerationEntries.nextElement();
			if (zipEntryNextElement.isDirectory()) {
				File file2 = new File(str2 + ServiceReference.DELIMITER + zipEntryNextElement.getName());
				log.i("entry.isDirectory XXX " + file2.getPath());
				if (!file2.exists()) {
					file2.mkdirs();
				}
			} else {
				BufferedInputStream bufferedInputStream = new BufferedInputStream(zipFile.getInputStream(zipEntryNextElement));
				File file3 = new File(str2 + ServiceReference.DELIMITER + zipEntryNextElement.getName());
				if (file3.exists()) {
					file3.delete();
				}
				file3.createNewFile();
				RandomAccessFile randomAccessFile = new RandomAccessFile(file3, "rw");
				int i = 0;
				while (true) {
					int i2 = bufferedInputStream.read(bArr, 0, FILE_BUFFER_SIZE);
					if (i2 == -1) {
						break;
					}
					try {
						randomAccessFile.seek(i);
					} catch (Exception e) {
						log.e("exception, ex: " + e.toString());
					}
					randomAccessFile.write(bArr, 0, i2);
					i += i2;
				}
				file3.delete();
				randomAccessFile.close();
				bufferedInputStream.close();
			}
		}
		return true;
	}

	private static boolean fileToZip(String str, File file, ZipOutputStream zipOutputStream) throws Throwable {
		byte[] bArr = new byte[FILE_BUFFER_SIZE];
		FileInputStream fileInputStream = null;
		try {
			try {
				FileInputStream fileInputStream2 = new FileInputStream(file);
				try {
					zipOutputStream.putNextEntry(new ZipEntry(getEntryName(str, file)));
					while (true) {
						int i = fileInputStream2.read(bArr);
						if (i == -1) {
							break;
						}
						zipOutputStream.write(bArr, 0, i);
					}
					zipOutputStream.closeEntry();
					fileInputStream2.close();
					if (zipOutputStream != null) {
						zipOutputStream.closeEntry();
					}
					fileInputStream2.close();
					return true;
				} catch (IOException e) {
					fileInputStream = fileInputStream2;
					log.e("Exception, ex: " + e.toString());
					if (zipOutputStream != null) {
						zipOutputStream.closeEntry();
					}
					if (fileInputStream != null) {
						fileInputStream.close();
					}
					return false;
				} catch (Throwable th) {
					fileInputStream = fileInputStream2;
					if (zipOutputStream != null) {
						zipOutputStream.closeEntry();
					}
					if (fileInputStream != null) {
						fileInputStream.close();
					}
					throw th;
				}
			} catch (IOException e2) {
			}
		} catch (Throwable th2) {
		}
		return false;
	}

	private static boolean dirToZip(String str, File file, ZipOutputStream zipOutputStream) throws Throwable {
		if (!file.isDirectory()) {
			return false;
		}
		File[] fileArrListFiles = file.listFiles();
		if (fileArrListFiles.length == 0) {
			try {
				zipOutputStream.putNextEntry(new ZipEntry(getEntryName(str, file)));
				zipOutputStream.closeEntry();
			} catch (IOException e) {
				log.e("Exception, ex: " + e.toString());
			}
		}
		for (int i = 0; i < fileArrListFiles.length; i++) {
			if (fileArrListFiles[i].isFile()) {
				fileToZip(str, fileArrListFiles[i], zipOutputStream);
			} else {
				dirToZip(str, fileArrListFiles[i], zipOutputStream);
			}
		}
		return true;
	}

	private static String getEntryName(String str, File file) {
		if (!str.endsWith(File.separator)) {
			str = str + File.separator;
		}
		String absolutePath = file.getAbsolutePath();
		if (file.isDirectory()) {
			absolutePath = absolutePath + ServiceReference.DELIMITER;
		}
		return absolutePath.substring(absolutePath.indexOf(str) + str.length());
	}
}
