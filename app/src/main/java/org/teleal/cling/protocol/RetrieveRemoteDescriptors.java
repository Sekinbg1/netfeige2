package org.teleal.cling.protocol;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.binding.xml.DescriptorBindingException;
import org.teleal.cling.model.ValidationError;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.UpnpRequest;
import org.teleal.cling.model.meta.Icon;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.RemoteDeviceIdentity;
import org.teleal.cling.model.meta.RemoteService;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.registry.RegistrationException;
import org.teleal.common.util.Exceptions;

/* JADX INFO: loaded from: classes.dex */
public class RetrieveRemoteDescriptors implements Runnable {
	private RemoteDevice rd;
	private final UpnpService upnpService;
	private static final Logger log = Logger.getLogger(RetrieveRemoteDescriptors.class.getName());
	private static final Set<URL> activeRetrievals = new CopyOnWriteArraySet();

	public RetrieveRemoteDescriptors(UpnpService upnpService, RemoteDevice remoteDevice) {
		this.upnpService = upnpService;
		this.rd = remoteDevice;
	}

	public UpnpService getUpnpService() {
		return this.upnpService;
	}

	@Override // java.lang.Runnable
	public void run() {
		Exception e = null;
		URL descriptorURL = this.rd.getIdentity().getDescriptorURL();
		if (activeRetrievals.contains(descriptorURL)) {
			log.finer("Exiting early, active retrieval for URL already in progress: " + descriptorURL);
			return;
		}
		if (getUpnpService().getRegistry().getRemoteDevice(this.rd.getIdentity().getUdn(), true) != null) {
			log.finer("Exiting early, already discovered: " + descriptorURL);
			return;
		}
		try {
			activeRetrievals.add(descriptorURL);
			describe();
		} finally {
			activeRetrievals.remove(descriptorURL);
		}
	}

	protected void describe() {
		StreamRequestMessage streamRequestMessage = new StreamRequestMessage(UpnpRequest.Method.GET, this.rd.getIdentity().getDescriptorURL());
		log.fine("Sending device descriptor retrieval message: " + streamRequestMessage);
		StreamResponseMessage streamResponseMessageSend = getUpnpService().getRouter().send(streamRequestMessage);
		if (streamResponseMessageSend == null) {
			log.warning("Device descriptor retrieval failed, no response: " + this.rd.getIdentity().getDescriptorURL());
			return;
		}
		if (streamResponseMessageSend.getOperation().isFailed()) {
			log.warning("Device descriptor retrieval failed: " + this.rd.getIdentity().getDescriptorURL() + ", " + streamResponseMessageSend.getOperation().getResponseDetails());
			return;
		}
		if (!streamResponseMessageSend.isContentTypeTextUDA()) {
			log.warning("Received device descriptor without or with invalid Content-Type: " + this.rd.getIdentity().getDescriptorURL());
		}
		log.fine("Received root device descriptor: " + streamResponseMessageSend);
		describe(streamResponseMessageSend.getBodyString());
	}

	protected void describe(String str) {
		RemoteDevice remoteDevice;
		RemoteDevice remoteDevice2 = null;
		Exception exception = null;
		try {
			remoteDevice = (RemoteDevice) getUpnpService().getConfiguration().getDeviceDescriptorBinderUDA10().describe(this.rd, str);
		} catch (DescriptorBindingException e) {
			exception = e;
			remoteDevice = null;
		} catch (ValidationException e2) {
			exception = e2;
			remoteDevice = null;
		} catch (RegistrationException e3) {
			exception = e3;
			remoteDevice = null;
		}
		try {
			log.fine("Remote device described (without services) notifying listeners: " + remoteDevice);
			boolean zNotifyDiscoveryStart = getUpnpService().getRegistry().notifyDiscoveryStart(remoteDevice);
			log.fine("Hydrating described device's services: " + remoteDevice);
			RemoteDevice remoteDeviceDescribeServices = describeServices(remoteDevice);
			if (remoteDeviceDescribeServices == null) {
				log.warning("Device service description failed: " + this.rd);
				if (zNotifyDiscoveryStart) {
					getUpnpService().getRegistry().notifyDiscoveryFailure(remoteDevice, new DescriptorBindingException("Device service description failed: " + this.rd));
					return;
				}
				return;
			}
			log.fine("Adding fully hydrated remote device to registry: " + remoteDeviceDescribeServices);
			getUpnpService().getRegistry().addDevice(remoteDeviceDescribeServices);
		} catch (DescriptorBindingException e4) {
			exception = e4;
			remoteDevice2 = remoteDevice;
			log.warning("Could not hydrate device or its services from descriptor: " + this.rd);
			log.warning("Cause was: " + Exceptions.unwrap(exception));
			if (remoteDevice2 == null || 0 == 0) {
				return;
			}
			getUpnpService().getRegistry().notifyDiscoveryFailure(remoteDevice2, exception);
		} catch (ValidationException e5) {
			exception = e5;
			remoteDevice2 = remoteDevice;
			log.warning("Could not validate device model: " + this.rd);
			Iterator<ValidationError> it = ((ValidationException)exception).getErrors().iterator();
			while (it.hasNext()) {
				log.warning(it.next().toString());
			}
			if (remoteDevice2 == null || 0 == 0) {
				return;
			}
			getUpnpService().getRegistry().notifyDiscoveryFailure(remoteDevice2, exception);
		} catch (RegistrationException e6) {
			exception = e6;
			remoteDevice2 = remoteDevice;
			log.warning("Adding hydrated device to registry failed: " + this.rd);
			log.warning("Cause was: " + exception.toString());
			if (remoteDevice2 == null || 0 == 0) {
				return;
			}
			getUpnpService().getRegistry().notifyDiscoveryFailure(remoteDevice2, exception);
		}
	}

	protected RemoteDevice describeServices(RemoteDevice remoteDevice) throws ValidationException, DescriptorBindingException {
		ArrayList arrayList = new ArrayList();
		if (remoteDevice.hasServices()) {
			Iterator<RemoteService> it = filterExclusiveServices(remoteDevice.getServices()).iterator();
			while (it.hasNext()) {
				RemoteService remoteServiceDescribeService = describeService(it.next());
				if (remoteServiceDescribeService == null) {
					return null;
				}
				arrayList.add(remoteServiceDescribeService);
			}
		}
		List<RemoteDevice> arrayList2 = new ArrayList<>();
		if (remoteDevice.hasEmbeddedDevices()) {
			for (RemoteDevice remoteDevice2 : remoteDevice.getEmbeddedDevices()) {
				if (remoteDevice2 != null) {
					RemoteDevice remoteDeviceDescribeServices = describeServices(remoteDevice2);
					if (remoteDeviceDescribeServices == null) {
						return null;
					}
					arrayList2.add(remoteDeviceDescribeServices);
				}
			}
		}
		Icon[] iconArr = new Icon[remoteDevice.getIcons().length];
		for (int i = 0; i < remoteDevice.getIcons().length; i++) {
			iconArr[i] = remoteDevice.getIcons()[i].deepCopy();
		}
		return remoteDevice.newInstance(((RemoteDeviceIdentity) remoteDevice.getIdentity()).getUdn(), remoteDevice.getVersion(), remoteDevice.getType(), remoteDevice.getDetails(), iconArr, remoteDevice.toServiceArray((Collection<RemoteService>) arrayList), arrayList2);
	}

	protected RemoteService describeService(RemoteService remoteService) throws ValidationException, DescriptorBindingException {
		URL urlNormalizeURI = remoteService.getDevice().normalizeURI(remoteService.getDescriptorURI());
		StreamRequestMessage streamRequestMessage = new StreamRequestMessage(UpnpRequest.Method.GET, urlNormalizeURI);
		log.fine("Sending service descriptor retrieval message: " + streamRequestMessage);
		StreamResponseMessage streamResponseMessageSend = getUpnpService().getRouter().send(streamRequestMessage);
		if (streamResponseMessageSend == null) {
			log.warning("Could not retrieve service descriptor: " + remoteService);
			return null;
		}
		if (streamResponseMessageSend.getOperation().isFailed()) {
			log.warning("Service descriptor retrieval failed: " + urlNormalizeURI + ", " + streamResponseMessageSend.getOperation().getResponseDetails());
			return null;
		}
		if (!streamResponseMessageSend.isContentTypeTextUDA()) {
			log.warning("Received service descriptor without or with invalid Content-Type: " + urlNormalizeURI);
		}
		String bodyString = streamResponseMessageSend.getBodyString();
		if (bodyString == null || bodyString.length() == 0) {
			log.warning("Received empty descriptor:" + urlNormalizeURI);
			return null;
		}
		log.fine("Received service descriptor, hydrating service model: " + streamResponseMessageSend);
		return (RemoteService) getUpnpService().getConfiguration().getServiceDescriptorBinderUDA10().describe(remoteService, streamResponseMessageSend.getBodyString());
	}

	protected List<RemoteService> filterExclusiveServices(RemoteService[] remoteServiceArr) {
		ServiceType[] exclusiveServiceTypes = getUpnpService().getConfiguration().getExclusiveServiceTypes();
		if (exclusiveServiceTypes == null || exclusiveServiceTypes.length == 0) {
			return Arrays.asList(remoteServiceArr);
		}
		ArrayList arrayList = new ArrayList();
		for (RemoteService remoteService : remoteServiceArr) {
			for (ServiceType serviceType : exclusiveServiceTypes) {
				if (remoteService.getServiceType().implementsVersion(serviceType)) {
					log.fine("Including exlusive service: " + remoteService);
					arrayList.add(remoteService);
				} else {
					log.fine("Excluding unwanted service: " + serviceType);
				}
			}
		}
		return arrayList;
	}
}
