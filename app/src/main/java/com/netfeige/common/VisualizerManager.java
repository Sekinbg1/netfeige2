package com.netfeige.common;

import android.media.audiofx.Visualizer;

/* JADX INFO: loaded from: classes.dex */
public class VisualizerManager {
    private Visualizer.OnDataCaptureListener m_onDataCaptureListener;
    private Visualizer m_visualizer;

    public VisualizerManager(Visualizer visualizer, Visualizer.OnDataCaptureListener onDataCaptureListener) {
        this.m_visualizer = visualizer;
        this.m_onDataCaptureListener = onDataCaptureListener;
    }

    public boolean reInitVisualizer(int i) {
        releaseVisualizer();
        int maxCaptureRate = Visualizer.getMaxCaptureRate();
        Visualizer visualizer = new Visualizer(i);
        this.m_visualizer = visualizer;
        visualizer.setCaptureSize(256);
        Visualizer.OnDataCaptureListener onDataCaptureListener = this.m_onDataCaptureListener;
        if (onDataCaptureListener != null) {
            this.m_visualizer.setDataCaptureListener(onDataCaptureListener, maxCaptureRate / 2, false, true);
        }
        return true;
    }

    public void releaseVisualizer() {
        Visualizer visualizer = this.m_visualizer;
        if (visualizer != null) {
            visualizer.setEnabled(false);
            this.m_visualizer.release();
            this.m_visualizer = null;
        }
    }

    public void enableVisualizer(boolean z) {
        Visualizer visualizer = this.m_visualizer;
        if (visualizer != null) {
            visualizer.setEnabled(z);
        }
    }
}

