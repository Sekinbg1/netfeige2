package org.teleal.cling.support.model;

import com.netfeige.dlna.ContentTree;
import java.util.Map;
import org.teleal.cling.model.action.ActionArgumentValue;

/* JADX INFO: loaded from: classes.dex */
public class TransportInfo {
    private String currentSpeed;
    private TransportState currentTransportState;
    private TransportStatus currentTransportStatus;

    public TransportInfo() {
        this.currentTransportState = TransportState.NO_MEDIA_PRESENT;
        this.currentTransportStatus = TransportStatus.OK;
        this.currentSpeed = ContentTree.VIDEO_ID;
    }

    public TransportInfo(Map<String, ActionArgumentValue> map) {
        this(TransportState.valueOrCustomOf((String) map.get("CurrentTransportState").getValue()), TransportStatus.valueOrCustomOf((String) map.get("CurrentTransportStatus").getValue()), (String) map.get("CurrentSpeed").getValue());
    }

    public TransportInfo(TransportState transportState) {
        this.currentTransportState = TransportState.NO_MEDIA_PRESENT;
        this.currentTransportStatus = TransportStatus.OK;
        this.currentSpeed = ContentTree.VIDEO_ID;
        this.currentTransportState = transportState;
    }

    public TransportInfo(TransportState transportState, String str) {
        this.currentTransportState = TransportState.NO_MEDIA_PRESENT;
        this.currentTransportStatus = TransportStatus.OK;
        this.currentSpeed = ContentTree.VIDEO_ID;
        this.currentTransportState = transportState;
        this.currentSpeed = str;
    }

    public TransportInfo(TransportState transportState, TransportStatus transportStatus) {
        this.currentTransportState = TransportState.NO_MEDIA_PRESENT;
        this.currentTransportStatus = TransportStatus.OK;
        this.currentSpeed = ContentTree.VIDEO_ID;
        this.currentTransportState = transportState;
        this.currentTransportStatus = transportStatus;
    }

    public TransportInfo(TransportState transportState, TransportStatus transportStatus, String str) {
        this.currentTransportState = TransportState.NO_MEDIA_PRESENT;
        this.currentTransportStatus = TransportStatus.OK;
        this.currentSpeed = ContentTree.VIDEO_ID;
        this.currentTransportState = transportState;
        this.currentTransportStatus = transportStatus;
        this.currentSpeed = str;
    }

    public TransportState getCurrentTransportState() {
        return this.currentTransportState;
    }

    public TransportStatus getCurrentTransportStatus() {
        return this.currentTransportStatus;
    }

    public String getCurrentSpeed() {
        return this.currentSpeed;
    }
}

