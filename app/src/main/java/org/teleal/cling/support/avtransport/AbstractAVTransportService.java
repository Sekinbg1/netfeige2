package org.teleal.cling.support.avtransport;

import com.netfeige.dlna.ContentTree;
import java.beans.PropertyChangeSupport;
import org.teleal.cling.binding.annotations.UpnpAction;
import org.teleal.cling.binding.annotations.UpnpInputArgument;
import org.teleal.cling.binding.annotations.UpnpOutputArgument;
import org.teleal.cling.binding.annotations.UpnpService;
import org.teleal.cling.binding.annotations.UpnpServiceId;
import org.teleal.cling.binding.annotations.UpnpServiceType;
import org.teleal.cling.binding.annotations.UpnpStateVariable;
import org.teleal.cling.binding.annotations.UpnpStateVariables;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.teleal.cling.support.lastchange.LastChange;
import org.teleal.cling.support.model.DeviceCapabilities;
import org.teleal.cling.support.model.MediaInfo;
import org.teleal.cling.support.model.PlayMode;
import org.teleal.cling.support.model.PositionInfo;
import org.teleal.cling.support.model.RecordMediumWriteStatus;
import org.teleal.cling.support.model.RecordQualityMode;
import org.teleal.cling.support.model.SeekMode;
import org.teleal.cling.support.model.StorageMedium;
import org.teleal.cling.support.model.TransportInfo;
import org.teleal.cling.support.model.TransportSettings;
import org.teleal.cling.support.model.TransportState;
import org.teleal.cling.support.model.TransportStatus;

/* JADX INFO: loaded from: classes.dex */
@UpnpService(serviceId = @UpnpServiceId("AVTransport"), serviceType = @UpnpServiceType(value = "AVTransport", version = 1), stringConvertibleTypes = {LastChange.class})
@UpnpStateVariables({@UpnpStateVariable(allowedValuesEnum = TransportState.class, name = "TransportState", sendEvents = false), @UpnpStateVariable(allowedValuesEnum = TransportStatus.class, name = "TransportStatus", sendEvents = false), @UpnpStateVariable(allowedValuesEnum = StorageMedium.class, defaultValue = "NONE", name = "PlaybackStorageMedium", sendEvents = false), @UpnpStateVariable(allowedValuesEnum = StorageMedium.class, defaultValue = "NOT_IMPLEMENTED", name = "RecordStorageMedium", sendEvents = false), @UpnpStateVariable(datatype = "string", defaultValue = "NETWORK", name = "PossiblePlaybackStorageMedia", sendEvents = false), @UpnpStateVariable(datatype = "string", defaultValue = "NOT_IMPLEMENTED", name = "PossibleRecordStorageMedia", sendEvents = false), @UpnpStateVariable(allowedValuesEnum = PlayMode.class, defaultValue = "NORMAL", name = "CurrentPlayMode", sendEvents = false), @UpnpStateVariable(datatype = "string", defaultValue = ContentTree.VIDEO_ID, name = "TransportPlaySpeed", sendEvents = false), @UpnpStateVariable(allowedValuesEnum = RecordMediumWriteStatus.class, defaultValue = "NOT_IMPLEMENTED", name = "RecordMediumWriteStatus", sendEvents = false), @UpnpStateVariable(allowedValuesEnum = RecordQualityMode.class, defaultValue = "NOT_IMPLEMENTED", name = "CurrentRecordQualityMode", sendEvents = false), @UpnpStateVariable(datatype = "string", defaultValue = "NOT_IMPLEMENTED", name = "PossibleRecordQualityModes", sendEvents = false), @UpnpStateVariable(datatype = "ui4", defaultValue = ContentTree.ROOT_ID, name = "NumberOfTracks", sendEvents = false), @UpnpStateVariable(datatype = "ui4", defaultValue = ContentTree.ROOT_ID, name = "CurrentTrack", sendEvents = false), @UpnpStateVariable(datatype = "string", name = "CurrentTrackDuration", sendEvents = false), @UpnpStateVariable(datatype = "string", defaultValue = "00:00:00", name = "CurrentMediaDuration", sendEvents = false), @UpnpStateVariable(datatype = "string", defaultValue = "NOT_IMPLEMENTED", name = "CurrentTrackMetaData", sendEvents = false), @UpnpStateVariable(datatype = "string", name = "CurrentTrackURI", sendEvents = false), @UpnpStateVariable(datatype = "string", name = "AVTransportURI", sendEvents = false), @UpnpStateVariable(datatype = "string", defaultValue = "NOT_IMPLEMENTED", name = "AVTransportURIMetaData", sendEvents = false), @UpnpStateVariable(datatype = "string", defaultValue = "NOT_IMPLEMENTED", name = "NextAVTransportURI", sendEvents = false), @UpnpStateVariable(datatype = "string", defaultValue = "NOT_IMPLEMENTED", name = "NextAVTransportURIMetaData", sendEvents = false), @UpnpStateVariable(datatype = "string", name = "RelativeTimePosition", sendEvents = false), @UpnpStateVariable(datatype = "string", name = "AbsoluteTimePosition", sendEvents = false), @UpnpStateVariable(datatype = "i4", defaultValue = "2147483647", name = "RelativeCounterPosition", sendEvents = false), @UpnpStateVariable(datatype = "i4", defaultValue = "2147483647", name = "AbsoluteCounterPosition", sendEvents = false), @UpnpStateVariable(datatype = "string", name = "CurrentTransportActions", sendEvents = false), @UpnpStateVariable(allowedValuesEnum = SeekMode.class, name = "A_ARG_TYPE_SeekMode", sendEvents = false), @UpnpStateVariable(datatype = "string", name = "A_ARG_TYPE_SeekTarget", sendEvents = false), @UpnpStateVariable(datatype = "ui4", name = "A_ARG_TYPE_InstanceID", sendEvents = false)})
public abstract class AbstractAVTransportService {

    @UpnpStateVariable(eventMaximumRateMilliseconds = 200)
    private final LastChange lastChange;
    protected final PropertyChangeSupport propertyChangeSupport;

    @UpnpAction(out = {@UpnpOutputArgument(name = "Actions")})
    public abstract String getCurrentTransportActions(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes unsignedIntegerFourBytes) throws AVTransportException;

    @UpnpAction(out = {@UpnpOutputArgument(getterName = "getPlayMediaString", name = "PlayMedia", stateVariable = "PossiblePlaybackStorageMedia"), @UpnpOutputArgument(getterName = "getRecMediaString", name = "RecMedia", stateVariable = "PossibleRecordStorageMedia"), @UpnpOutputArgument(getterName = "getRecQualityModesString", name = "RecQualityModes", stateVariable = "PossibleRecordQualityModes")})
    public abstract DeviceCapabilities getDeviceCapabilities(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes unsignedIntegerFourBytes) throws AVTransportException;

    @UpnpAction(out = {@UpnpOutputArgument(getterName = "getNumberOfTracks", name = "NrTracks", stateVariable = "NumberOfTracks"), @UpnpOutputArgument(getterName = "getMediaDuration", name = "MediaDuration", stateVariable = "CurrentMediaDuration"), @UpnpOutputArgument(getterName = "getCurrentURI", name = "CurrentURI", stateVariable = "AVTransportURI"), @UpnpOutputArgument(getterName = "getCurrentURIMetaData", name = "CurrentURIMetaData", stateVariable = "AVTransportURIMetaData"), @UpnpOutputArgument(getterName = "getNextURI", name = "NextURI", stateVariable = "NextAVTransportURI"), @UpnpOutputArgument(getterName = "getNextURIMetaData", name = "NextURIMetaData", stateVariable = "NextAVTransportURIMetaData"), @UpnpOutputArgument(getterName = "getPlayMedium", name = "PlayMedium", stateVariable = "PlaybackStorageMedium"), @UpnpOutputArgument(getterName = "getRecordMedium", name = "RecordMedium", stateVariable = "RecordStorageMedium"), @UpnpOutputArgument(getterName = "getWriteStatus", name = "WriteStatus", stateVariable = "RecordMediumWriteStatus")})
    public abstract MediaInfo getMediaInfo(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes unsignedIntegerFourBytes) throws AVTransportException;

    @UpnpAction(out = {@UpnpOutputArgument(getterName = "getTrack", name = "Track", stateVariable = "CurrentTrack"), @UpnpOutputArgument(getterName = "getTrackDuration", name = "TrackDuration", stateVariable = "CurrentTrackDuration"), @UpnpOutputArgument(getterName = "getTrackMetaData", name = "TrackMetaData", stateVariable = "CurrentTrackMetaData"), @UpnpOutputArgument(getterName = "getTrackURI", name = "TrackURI", stateVariable = "CurrentTrackURI"), @UpnpOutputArgument(getterName = "getRelTime", name = "RelTime", stateVariable = "RelativeTimePosition"), @UpnpOutputArgument(getterName = "getAbsTime", name = "AbsTime", stateVariable = "AbsoluteTimePosition"), @UpnpOutputArgument(getterName = "getRelCount", name = "RelCount", stateVariable = "RelativeCounterPosition"), @UpnpOutputArgument(getterName = "getAbsCount", name = "AbsCount", stateVariable = "AbsoluteCounterPosition")})
    public abstract PositionInfo getPositionInfo(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes unsignedIntegerFourBytes) throws AVTransportException;

    @UpnpAction(out = {@UpnpOutputArgument(getterName = "getCurrentTransportState", name = "CurrentTransportState", stateVariable = "TransportState"), @UpnpOutputArgument(getterName = "getCurrentTransportStatus", name = "CurrentTransportStatus", stateVariable = "TransportStatus"), @UpnpOutputArgument(getterName = "getCurrentSpeed", name = "CurrentSpeed", stateVariable = "TransportPlaySpeed")})
    public abstract TransportInfo getTransportInfo(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes unsignedIntegerFourBytes) throws AVTransportException;

    @UpnpAction(out = {@UpnpOutputArgument(getterName = "getPlayMode", name = "PlayMode", stateVariable = "CurrentPlayMode"), @UpnpOutputArgument(getterName = "getRecQualityMode", name = "RecQualityMode", stateVariable = "CurrentRecordQualityMode")})
    public abstract TransportSettings getTransportSettings(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes unsignedIntegerFourBytes) throws AVTransportException;

    @UpnpAction
    public abstract void next(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes unsignedIntegerFourBytes) throws AVTransportException;

    @UpnpAction
    public abstract void pause(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes unsignedIntegerFourBytes) throws AVTransportException;

    @UpnpAction
    public abstract void play(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes unsignedIntegerFourBytes, @UpnpInputArgument(name = "Speed", stateVariable = "TransportPlaySpeed") String str) throws AVTransportException;

    @UpnpAction
    public abstract void previous(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes unsignedIntegerFourBytes) throws AVTransportException;

    @UpnpAction
    public abstract void record(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes unsignedIntegerFourBytes) throws AVTransportException;

    @UpnpAction
    public abstract void seek(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes unsignedIntegerFourBytes, @UpnpInputArgument(name = "Unit", stateVariable = "A_ARG_TYPE_SeekMode") String str, @UpnpInputArgument(name = "Target", stateVariable = "A_ARG_TYPE_SeekTarget") String str2) throws AVTransportException;

    @UpnpAction
    public abstract void setAVTransportURI(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes unsignedIntegerFourBytes, @UpnpInputArgument(name = "CurrentURI", stateVariable = "AVTransportURI") String str, @UpnpInputArgument(name = "CurrentURIMetaData", stateVariable = "AVTransportURIMetaData") String str2) throws AVTransportException;

    @UpnpAction
    public abstract void setNextAVTransportURI(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes unsignedIntegerFourBytes, @UpnpInputArgument(name = "NextURI", stateVariable = "AVTransportURI") String str, @UpnpInputArgument(name = "NextURIMetaData", stateVariable = "AVTransportURIMetaData") String str2) throws AVTransportException;

    @UpnpAction
    public abstract void setPlayMode(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes unsignedIntegerFourBytes, @UpnpInputArgument(name = "NewPlayMode", stateVariable = "CurrentPlayMode") String str) throws AVTransportException;

    @UpnpAction
    public abstract void setRecordQualityMode(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes unsignedIntegerFourBytes, @UpnpInputArgument(name = "NewRecordQualityMode", stateVariable = "CurrentRecordQualityMode") String str) throws AVTransportException;

    @UpnpAction
    public abstract void stop(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes unsignedIntegerFourBytes) throws AVTransportException;

    protected AbstractAVTransportService() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.lastChange = new LastChange(new AVTransportLastChangeParser());
    }

    protected AbstractAVTransportService(LastChange lastChange) {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.lastChange = lastChange;
    }

    protected AbstractAVTransportService(PropertyChangeSupport propertyChangeSupport) {
        this.propertyChangeSupport = propertyChangeSupport;
        this.lastChange = new LastChange(new AVTransportLastChangeParser());
    }

    protected AbstractAVTransportService(PropertyChangeSupport propertyChangeSupport, LastChange lastChange) {
        this.propertyChangeSupport = propertyChangeSupport;
        this.lastChange = lastChange;
    }

    public LastChange getLastChange() {
        return this.lastChange;
    }

    public void fireLastChange() {
        getLastChange().fire(getPropertyChangeSupport());
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return this.propertyChangeSupport;
    }

    public static UnsignedIntegerFourBytes getDefaultInstanceID() {
        return new UnsignedIntegerFourBytes(0L);
    }
}

