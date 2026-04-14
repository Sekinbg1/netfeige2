package org.teleal.cling.support.avtransport.impl;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.teleal.cling.model.ModelUtil;
import org.teleal.cling.model.types.ErrorCode;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.support.avtransport.AVTransportErrorCode;
import org.teleal.cling.support.avtransport.AVTransportException;
import org.teleal.cling.support.avtransport.AbstractAVTransportService;
import org.teleal.cling.support.avtransport.impl.state.AbstractState;
import org.teleal.cling.support.lastchange.LastChange;
import org.teleal.cling.support.model.AVTransport;
import org.teleal.cling.support.model.DeviceCapabilities;
import org.teleal.cling.support.model.MediaInfo;
import org.teleal.cling.support.model.PlayMode;
import org.teleal.cling.support.model.PositionInfo;
import org.teleal.cling.support.model.RecordQualityMode;
import org.teleal.cling.support.model.SeekMode;
import org.teleal.cling.support.model.StorageMedium;
import org.teleal.cling.support.model.TransportInfo;
import org.teleal.cling.support.model.TransportSettings;
import org.teleal.common.statemachine.StateMachineBuilder;
import org.teleal.common.statemachine.TransitionException;

/* JADX INFO: loaded from: classes.dex */
public class AVTransportService<T extends AVTransport> extends AbstractAVTransportService {
	private static final Logger log = Logger.getLogger(AVTransportService.class.getName());
	final Class<? extends AbstractState> initialState;
	final Class<? extends AVTransportStateMachine> stateMachineDefinition;
	private final Map<Long, AVTransportStateMachine> stateMachines;
	final Class<? extends AVTransport> transportClass;

	public AVTransportService(Class<? extends AVTransportStateMachine> cls, Class<? extends AbstractState> cls2) {
		this(cls, cls2, (Class) AVTransport.class);
	}

	/* JADX WARN: Multi-variable type inference failed */
	public AVTransportService(Class<? extends AVTransportStateMachine> cls, Class<? extends AbstractState> cls2, Class<T> cls3) {
		this.stateMachines = new ConcurrentHashMap();
		this.stateMachineDefinition = cls;
		this.initialState = cls2;
		this.transportClass = cls3;
	}

	@Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
	public void setAVTransportURI(UnsignedIntegerFourBytes unsignedIntegerFourBytes, String str, String str2) throws AVTransportException {
		try {
			try {
				findStateMachine(unsignedIntegerFourBytes, true).setTransportURI(new URI(str), str2);
			} catch (TransitionException e) {
				throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, e.getMessage());
			}
		} catch (Exception unused) {
			throw new AVTransportException(ErrorCode.INVALID_ARGS, "CurrentURI can not be null or malformed");
		}
	}

	@Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
	public void setNextAVTransportURI(UnsignedIntegerFourBytes unsignedIntegerFourBytes, String str, String str2) throws AVTransportException {
		try {
			try {
				findStateMachine(unsignedIntegerFourBytes, true).setNextTransportURI(new URI(str), str2);
			} catch (TransitionException e) {
				throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, e.getMessage());
			}
		} catch (Exception unused) {
			throw new AVTransportException(ErrorCode.INVALID_ARGS, "NextURI can not be null or malformed");
		}
	}

	@Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
	public void setPlayMode(UnsignedIntegerFourBytes unsignedIntegerFourBytes, String str) throws AVTransportException {
		AVTransport transport = findStateMachine(unsignedIntegerFourBytes).getCurrentState().getTransport();
		try {
			transport.setTransportSettings(new TransportSettings(PlayMode.valueOf(str), transport.getTransportSettings().getRecQualityMode()));
		} catch (IllegalArgumentException unused) {
			throw new AVTransportException(AVTransportErrorCode.PLAYMODE_NOT_SUPPORTED, "Unsupported play mode: " + str);
		}
	}

	@Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
	public void setRecordQualityMode(UnsignedIntegerFourBytes unsignedIntegerFourBytes, String str) throws AVTransportException {
		AVTransport transport = findStateMachine(unsignedIntegerFourBytes).getCurrentState().getTransport();
		try {
			transport.setTransportSettings(new TransportSettings(transport.getTransportSettings().getPlayMode(), RecordQualityMode.valueOrExceptionOf(str)));
		} catch (IllegalArgumentException unused) {
			throw new AVTransportException(AVTransportErrorCode.RECORDQUALITYMODE_NOT_SUPPORTED, "Unsupported record quality mode: " + str);
		}
	}

	@Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
	public MediaInfo getMediaInfo(UnsignedIntegerFourBytes unsignedIntegerFourBytes) throws AVTransportException {
		return findStateMachine(unsignedIntegerFourBytes).getCurrentState().getTransport().getMediaInfo();
	}

	@Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
	public TransportInfo getTransportInfo(UnsignedIntegerFourBytes unsignedIntegerFourBytes) throws AVTransportException {
		return findStateMachine(unsignedIntegerFourBytes).getCurrentState().getTransport().getTransportInfo();
	}

	@Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
	public PositionInfo getPositionInfo(UnsignedIntegerFourBytes unsignedIntegerFourBytes) throws AVTransportException {
		return findStateMachine(unsignedIntegerFourBytes).getCurrentState().getTransport().getPositionInfo();
	}

	@Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
	public DeviceCapabilities getDeviceCapabilities(UnsignedIntegerFourBytes unsignedIntegerFourBytes) throws AVTransportException {
		return findStateMachine(unsignedIntegerFourBytes).getCurrentState().getTransport().getDeviceCapabilities();
	}

	@Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
	public TransportSettings getTransportSettings(UnsignedIntegerFourBytes unsignedIntegerFourBytes) throws AVTransportException {
		return findStateMachine(unsignedIntegerFourBytes).getCurrentState().getTransport().getTransportSettings();
	}

	@Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
	public String getCurrentTransportActions(UnsignedIntegerFourBytes unsignedIntegerFourBytes) throws AVTransportException {
		try {
			return ModelUtil.toCommaSeparatedList(findStateMachine(unsignedIntegerFourBytes).getCurrentState().getCurrentTransportActions());
		} catch (TransitionException unused) {
			return "";
		}
	}

	@Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
	public void stop(UnsignedIntegerFourBytes unsignedIntegerFourBytes) throws AVTransportException {
		try {
			findStateMachine(unsignedIntegerFourBytes).stop();
		} catch (TransitionException e) {
			throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, e.getMessage());
		}
	}

	@Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
	public void play(UnsignedIntegerFourBytes unsignedIntegerFourBytes, String str) throws AVTransportException {
		try {
			findStateMachine(unsignedIntegerFourBytes).play(str);
		} catch (TransitionException e) {
			throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, e.getMessage());
		}
	}

	@Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
	public void pause(UnsignedIntegerFourBytes unsignedIntegerFourBytes) throws AVTransportException {
		try {
			findStateMachine(unsignedIntegerFourBytes).pause();
		} catch (TransitionException e) {
			throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, e.getMessage());
		}
	}

	@Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
	public void record(UnsignedIntegerFourBytes unsignedIntegerFourBytes) throws AVTransportException {
		try {
			findStateMachine(unsignedIntegerFourBytes).record();
		} catch (TransitionException e) {
			throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, e.getMessage());
		}
	}

	@Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
	public void seek(UnsignedIntegerFourBytes unsignedIntegerFourBytes, String str, String str2) throws AVTransportException {
		try {
			try {
				findStateMachine(unsignedIntegerFourBytes).seek(SeekMode.valueOrExceptionOf(str), str2);
			} catch (TransitionException e) {
				throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, e.getMessage());
			}
		} catch (IllegalArgumentException unused) {
			throw new AVTransportException(AVTransportErrorCode.SEEKMODE_NOT_SUPPORTED, "Unsupported seek mode: " + str);
		}
	}

	@Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
	public void next(UnsignedIntegerFourBytes unsignedIntegerFourBytes) throws AVTransportException {
		try {
			findStateMachine(unsignedIntegerFourBytes).next();
		} catch (TransitionException e) {
			throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, e.getMessage());
		}
	}

	@Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
	public void previous(UnsignedIntegerFourBytes unsignedIntegerFourBytes) throws AVTransportException {
		try {
			findStateMachine(unsignedIntegerFourBytes).previous();
		} catch (TransitionException e) {
			throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, e.getMessage());
		}
	}

	protected AVTransportStateMachine findStateMachine(UnsignedIntegerFourBytes unsignedIntegerFourBytes) throws AVTransportException {
		return findStateMachine(unsignedIntegerFourBytes, true);
	}

	protected AVTransportStateMachine findStateMachine(UnsignedIntegerFourBytes unsignedIntegerFourBytes, boolean z) throws AVTransportException {
		AVTransportStateMachine aVTransportStateMachineCreateStateMachine;
		synchronized (this.stateMachines) {
			long jLongValue = unsignedIntegerFourBytes.getValue().longValue();
			aVTransportStateMachineCreateStateMachine = this.stateMachines.get(Long.valueOf(jLongValue));
			if (aVTransportStateMachineCreateStateMachine == null && jLongValue == 0 && z) {
				log.fine("Creating default transport instance with ID '0'");
				aVTransportStateMachineCreateStateMachine = createStateMachine(unsignedIntegerFourBytes);
				this.stateMachines.put(Long.valueOf(jLongValue), aVTransportStateMachineCreateStateMachine);
			} else if (aVTransportStateMachineCreateStateMachine == null) {
				throw new AVTransportException(AVTransportErrorCode.INVALID_INSTANCE_ID);
			}
			log.fine("Found transport control with ID '" + jLongValue + "'");
		}
		return aVTransportStateMachineCreateStateMachine;
	}

	protected AVTransportStateMachine createStateMachine(UnsignedIntegerFourBytes unsignedIntegerFourBytes) {
		return (AVTransportStateMachine) StateMachineBuilder.build(this.stateMachineDefinition, this.initialState, new Class[]{this.transportClass}, new Object[]{createTransport(unsignedIntegerFourBytes, getLastChange())});
	}

	protected AVTransport createTransport(UnsignedIntegerFourBytes unsignedIntegerFourBytes, LastChange lastChange) {
		return new AVTransport(unsignedIntegerFourBytes, lastChange, StorageMedium.NETWORK);
	}
}
