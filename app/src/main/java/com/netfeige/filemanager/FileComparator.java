package com.netfeige.filemanager;

import com.netfeige.common.FileInfo;
import com.netfeige.common.ImagePreview;
import com.netfeige.common.Music;
import java.util.Comparator;

/* JADX INFO: loaded from: classes.dex */
public class FileComparator {
    public static Comparator<FileInfo> g_nameDesComparator = new Comparator<FileInfo>() { // from class: com.netfeige.filemanager.FileComparator.1
        @Override // java.util.Comparator
        public int compare(FileInfo fileInfo, FileInfo fileInfo2) {
            return FileComparator.fileInfoCompareOnName(fileInfo, fileInfo2, -1);
        }
    };
    public static Comparator<FileInfo> g_nameAscComparator = new Comparator<FileInfo>() { // from class: com.netfeige.filemanager.FileComparator.2
        @Override // java.util.Comparator
        public int compare(FileInfo fileInfo, FileInfo fileInfo2) {
            return FileComparator.fileInfoCompareOnName(fileInfo, fileInfo2, 1);
        }
    };
    public static Comparator<FileInfo> g_lastTimeDesComparator = new Comparator<FileInfo>() { // from class: com.netfeige.filemanager.FileComparator.3
        @Override // java.util.Comparator
        public int compare(FileInfo fileInfo, FileInfo fileInfo2) {
            return FileComparator.fileInfoCompareOnLastTime(fileInfo, fileInfo2, -1, 1);
        }
    };
    public static Comparator<FileInfo> g_lastTimeAscComparator = new Comparator<FileInfo>() { // from class: com.netfeige.filemanager.FileComparator.4
        @Override // java.util.Comparator
        public int compare(FileInfo fileInfo, FileInfo fileInfo2) {
            return FileComparator.fileInfoCompareOnLastTime(fileInfo, fileInfo2, 1, -1);
        }
    };
    public static Comparator<Music> g_lastTimeDesComparatorForMusic = new Comparator<Music>() { // from class: com.netfeige.filemanager.FileComparator.5
        @Override // java.util.Comparator
        public int compare(Music music, Music music2) {
            return FileComparator.musicCompareOnLastTime(music, music2, -1, 1);
        }
    };
    public static Comparator<Music> g_lastTimeAscComparatorForMusic = new Comparator<Music>() { // from class: com.netfeige.filemanager.FileComparator.6
        @Override // java.util.Comparator
        public int compare(Music music, Music music2) {
            return FileComparator.musicCompareOnLastTime(music, music2, 1, -1);
        }
    };
    public static Comparator<Music> g_nameDesComparatorForMusic = new Comparator<Music>() { // from class: com.netfeige.filemanager.FileComparator.7
        @Override // java.util.Comparator
        public int compare(Music music, Music music2) {
            return FileComparator.musicCompareOnName(music, music2, -1);
        }
    };
    public static Comparator<Music> g_nameAscComparatorForMusic = new Comparator<Music>() { // from class: com.netfeige.filemanager.FileComparator.8
        @Override // java.util.Comparator
        public int compare(Music music, Music music2) {
            return FileComparator.musicCompareOnName(music, music2, 1);
        }
    };
    public static Comparator<ImagePreview> g_lastTimeDesComparatorForImage = new Comparator<ImagePreview>() { // from class: com.netfeige.filemanager.FileComparator.9
        @Override // java.util.Comparator
        public int compare(ImagePreview imagePreview, ImagePreview imagePreview2) {
            return FileComparator.imageCompareOnLastTime(imagePreview, imagePreview2, -1, 1);
        }
    };
    public static Comparator<ImagePreview> g_lastTimeAscComparatorForImage = new Comparator<ImagePreview>() { // from class: com.netfeige.filemanager.FileComparator.10
        @Override // java.util.Comparator
        public int compare(ImagePreview imagePreview, ImagePreview imagePreview2) {
            return FileComparator.imageCompareOnLastTime(imagePreview, imagePreview2, 1, -1);
        }
    };
    public static Comparator<ImagePreview> g_nameAscComparatorForImage = new Comparator<ImagePreview>() { // from class: com.netfeige.filemanager.FileComparator.11
        @Override // java.util.Comparator
        public int compare(ImagePreview imagePreview, ImagePreview imagePreview2) {
            return FileComparator.imageCompareOnName(imagePreview, imagePreview2, 1);
        }
    };
    public static Comparator<ImagePreview> g_nameDesComparatorForImage = new Comparator<ImagePreview>() { // from class: com.netfeige.filemanager.FileComparator.12
        @Override // java.util.Comparator
        public int compare(ImagePreview imagePreview, ImagePreview imagePreview2) {
            return FileComparator.imageCompareOnName(imagePreview, imagePreview2, -1);
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public static int fileInfoCompareOnName(FileInfo fileInfo, FileInfo fileInfo2, int i) {
        int iCompareToIgnoreCase;
        if (fileInfo == null || fileInfo2 == null) {
            return fileInfo == null ? -1 : 1;
        }
        if (fileInfo.getId().equals("-1") || fileInfo2.getId().equals("-1")) {
            return fileInfo.getId().equals("-1") ? -1 : 1;
        }
        if (fileInfo.isFloder() && fileInfo2.isFloder()) {
            iCompareToIgnoreCase = fileInfo.getName().compareToIgnoreCase(fileInfo2.getName());
        } else {
            boolean z = false;
            if (fileInfo.isFloder() && !fileInfo2.isFloder()) {
                return -1;
            }
            if (fileInfo2.isFloder() && !fileInfo.isFloder()) {
                z = true;
            }
            if (z) {
                return 1;
            }
            iCompareToIgnoreCase = fileInfo.getName().compareToIgnoreCase(fileInfo2.getName());
        }
        return i * iCompareToIgnoreCase;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int fileInfoCompareOnLastTime(FileInfo fileInfo, FileInfo fileInfo2, int i, int i2) {
        if (fileInfo == null || fileInfo2 == null) {
            return fileInfo == null ? -1 : 1;
        }
        if (fileInfo.getId().equals("-1") || fileInfo2.getId().equals("-1")) {
            return fileInfo.getId().equals("-1") ? -1 : 1;
        }
        if (fileInfo.isFloder() && fileInfo2.isFloder()) {
            long lastTime = fileInfo.getLastTime() - fileInfo2.getLastTime();
            if (lastTime > 0) {
                return i;
            }
            if (lastTime == 0) {
                return 0;
            }
            return i2;
        }
        if (fileInfo.isFloder() && !fileInfo2.isFloder()) {
            return -1;
        }
        if (fileInfo2.isFloder() && !fileInfo.isFloder()) {
            return 1;
        }
        long lastTime2 = fileInfo.getLastTime() - fileInfo2.getLastTime();
        if (lastTime2 > 0) {
            return i;
        }
        if (lastTime2 == 0) {
            return 0;
        }
        return i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int musicCompareOnLastTime(Music music, Music music2, int i, int i2) {
        if (music == null || music2 == null) {
            return music == null ? -1 : 1;
        }
        long lastTime = music.getLastTime() - music2.getLastTime();
        if (lastTime > 0) {
            return i;
        }
        if (lastTime == 0) {
            return 0;
        }
        return i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int musicCompareOnName(Music music, Music music2, int i) {
        if (music == null || music2 == null) {
            return music == null ? -1 : 1;
        }
        return i * music.getName().compareToIgnoreCase(music2.getName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int imageCompareOnLastTime(ImagePreview imagePreview, ImagePreview imagePreview2, int i, int i2) {
        if (imagePreview == null || imagePreview2 == null) {
            return imagePreview == null ? -1 : 1;
        }
        long m_lLastTime = imagePreview.getM_lLastTime() - imagePreview2.getM_lLastTime();
        if (m_lLastTime > 0) {
            return i;
        }
        if (m_lLastTime == 0) {
            return 0;
        }
        return i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int imageCompareOnName(ImagePreview imagePreview, ImagePreview imagePreview2, int i) {
        if (imagePreview == null || imagePreview2 == null) {
            return imagePreview == null ? -1 : 1;
        }
        return i * imagePreview.getM_strImageName().compareToIgnoreCase(imagePreview2.getM_strImageName());
    }
}

