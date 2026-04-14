package org.teleal.cling.model.meta;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.teleal.cling.model.Namespace;
import org.teleal.cling.model.ServiceReference;
import org.teleal.cling.model.ValidationError;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.profile.ControlPointInfo;
import org.teleal.cling.model.profile.DeviceDetailsProvider;
import org.teleal.cling.model.resource.DeviceDescriptorResource;
import org.teleal.cling.model.resource.IconResource;
import org.teleal.cling.model.resource.Resource;
import org.teleal.cling.model.resource.ServiceControlResource;
import org.teleal.cling.model.resource.ServiceDescriptorResource;
import org.teleal.cling.model.resource.ServiceEventSubscriptionResource;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.ServiceId;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.model.types.UDN;

/* JADX INFO: loaded from: classes.dex */
public class LocalDevice extends Device<DeviceIdentity, LocalDevice, LocalService> {
	private final DeviceDetailsProvider deviceDetailsProvider;





	public LocalDevice(DeviceIdentity deviceIdentity) throws ValidationException {
		super(deviceIdentity);
		this.deviceDetailsProvider = null;
	}

	public LocalDevice(DeviceIdentity deviceIdentity, DeviceType deviceType, DeviceDetails deviceDetails, LocalService localService) throws ValidationException {
		super(deviceIdentity, deviceType, deviceDetails, null, new LocalService[]{localService});
		this.deviceDetailsProvider = null;
	}

	public LocalDevice(DeviceIdentity deviceIdentity, DeviceType deviceType, DeviceDetailsProvider deviceDetailsProvider, LocalService localService) throws ValidationException {
		super(deviceIdentity, deviceType, null, null, new LocalService[]{localService});
		this.deviceDetailsProvider = deviceDetailsProvider;
	}

	public LocalDevice(DeviceIdentity deviceIdentity, DeviceType deviceType, DeviceDetailsProvider deviceDetailsProvider, LocalService localService, LocalDevice localDevice) throws ValidationException {
		super(deviceIdentity, deviceType, null, null, new LocalService[]{localService}, new LocalDevice[]{localDevice});
		this.deviceDetailsProvider = deviceDetailsProvider;
	}

	public LocalDevice(DeviceIdentity deviceIdentity, DeviceType deviceType, DeviceDetails deviceDetails, LocalService localService, LocalDevice localDevice) throws ValidationException {
		super(deviceIdentity, deviceType, deviceDetails, null, new LocalService[]{localService}, new LocalDevice[]{localDevice});
		this.deviceDetailsProvider = null;
	}

	public LocalDevice(DeviceIdentity deviceIdentity, DeviceType deviceType, DeviceDetails deviceDetails, LocalService[] localServiceArr) throws ValidationException {
		super(deviceIdentity, deviceType, deviceDetails, null, localServiceArr);
		this.deviceDetailsProvider = null;
	}

	public LocalDevice(DeviceIdentity deviceIdentity, DeviceType deviceType, DeviceDetails deviceDetails, LocalService[] localServiceArr, LocalDevice[] localDeviceArr) throws ValidationException {
		super(deviceIdentity, deviceType, deviceDetails, null, localServiceArr, localDeviceArr);
		this.deviceDetailsProvider = null;
	}

	public LocalDevice(DeviceIdentity deviceIdentity, DeviceType deviceType, DeviceDetails deviceDetails, Icon icon, LocalService localService) throws ValidationException {
		super(deviceIdentity, deviceType, deviceDetails, new Icon[]{icon}, new LocalService[]{localService});
		this.deviceDetailsProvider = null;
	}

	public LocalDevice(DeviceIdentity deviceIdentity, DeviceType deviceType, DeviceDetails deviceDetails, Icon icon, LocalService localService, LocalDevice localDevice) throws ValidationException {
		super(deviceIdentity, deviceType, deviceDetails, new Icon[]{icon}, new LocalService[]{localService}, new LocalDevice[]{localDevice});
		this.deviceDetailsProvider = null;
	}

	public LocalDevice(DeviceIdentity deviceIdentity, DeviceType deviceType, DeviceDetails deviceDetails, Icon icon, LocalService[] localServiceArr) throws ValidationException {
		super(deviceIdentity, deviceType, deviceDetails, new Icon[]{icon}, localServiceArr);
		this.deviceDetailsProvider = null;
	}

	public LocalDevice(DeviceIdentity deviceIdentity, DeviceType deviceType, DeviceDetailsProvider deviceDetailsProvider, Icon icon, LocalService[] localServiceArr) throws ValidationException {
		super(deviceIdentity, deviceType, null, new Icon[]{icon}, localServiceArr);
		this.deviceDetailsProvider = deviceDetailsProvider;
	}

	public LocalDevice(DeviceIdentity deviceIdentity, DeviceType deviceType, DeviceDetails deviceDetails, Icon icon, LocalService[] localServiceArr, LocalDevice[] localDeviceArr) throws ValidationException {
		super(deviceIdentity, deviceType, deviceDetails, new Icon[]{icon}, localServiceArr, localDeviceArr);
		this.deviceDetailsProvider = null;
	}

	public LocalDevice(DeviceIdentity deviceIdentity, DeviceType deviceType, DeviceDetails deviceDetails, Icon[] iconArr, LocalService localService) throws ValidationException {
		super(deviceIdentity, deviceType, deviceDetails, iconArr, new LocalService[]{localService});
		this.deviceDetailsProvider = null;
	}

	public LocalDevice(DeviceIdentity deviceIdentity, DeviceType deviceType, DeviceDetails deviceDetails, Icon[] iconArr, LocalService localService, LocalDevice localDevice) throws ValidationException {
		super(deviceIdentity, deviceType, deviceDetails, iconArr, new LocalService[]{localService}, new LocalDevice[]{localDevice});
		this.deviceDetailsProvider = null;
	}

	public LocalDevice(DeviceIdentity deviceIdentity, DeviceType deviceType, DeviceDetailsProvider deviceDetailsProvider, Icon[] iconArr, LocalService localService, LocalDevice localDevice) throws ValidationException {
		super(deviceIdentity, deviceType, null, iconArr, new LocalService[]{localService}, new LocalDevice[]{localDevice});
		this.deviceDetailsProvider = deviceDetailsProvider;
	}

	public LocalDevice(DeviceIdentity deviceIdentity, DeviceType deviceType, DeviceDetails deviceDetails, Icon[] iconArr, LocalService[] localServiceArr) throws ValidationException {
		super(deviceIdentity, deviceType, deviceDetails, iconArr, localServiceArr);
		this.deviceDetailsProvider = null;
	}

	public LocalDevice(DeviceIdentity deviceIdentity, DeviceType deviceType, DeviceDetails deviceDetails, Icon[] iconArr, LocalService[] localServiceArr, LocalDevice[] localDeviceArr) throws ValidationException {
		super(deviceIdentity, deviceType, deviceDetails, iconArr, localServiceArr, localDeviceArr);
		this.deviceDetailsProvider = null;
	}

	public LocalDevice(DeviceIdentity deviceIdentity, UDAVersion uDAVersion, DeviceType deviceType, DeviceDetails deviceDetails, Icon[] iconArr, LocalService[] localServiceArr, LocalDevice[] localDeviceArr) throws ValidationException {
		super(deviceIdentity, uDAVersion, deviceType, deviceDetails, iconArr, localServiceArr, localDeviceArr);
		this.deviceDetailsProvider = null;
	}

	public LocalDevice(DeviceIdentity deviceIdentity, UDAVersion uDAVersion, DeviceType deviceType, DeviceDetailsProvider deviceDetailsProvider, Icon[] iconArr, LocalService[] localServiceArr, LocalDevice[] localDeviceArr) throws ValidationException {
		super(deviceIdentity, uDAVersion, deviceType, null, iconArr, localServiceArr, localDeviceArr);
		this.deviceDetailsProvider = deviceDetailsProvider;
	}

	@Override // org.teleal.cling.model.meta.Device
	public DeviceDetails getDetails(ControlPointInfo controlPointInfo) {
		DeviceDetailsProvider deviceDetailsProvider = this.deviceDetailsProvider;
		if (deviceDetailsProvider != null) {
			return deviceDetailsProvider.provide(controlPointInfo);
		}
		return getDetails();
	}

	@Override // org.teleal.cling.model.meta.Device
	public LocalService[] getServices() {
		return this.services != null ? (LocalService[]) this.services : new LocalService[0];
	}

	@Override // org.teleal.cling.model.meta.Device
	public LocalDevice[] getEmbeddedDevices() {
		return this.embeddedDevices != null ? (LocalDevice[]) this.embeddedDevices : new LocalDevice[0];
	}

	@Override // org.teleal.cling.model.meta.Device
	public LocalDevice newInstance(UDN udn, UDAVersion uDAVersion, DeviceType deviceType, DeviceDetails deviceDetails, Icon[] iconArr, LocalService[] localServiceArr, List<LocalDevice> list) throws ValidationException {
		return new LocalDevice(new DeviceIdentity(udn, getIdentity().getMaxAgeSeconds()), uDAVersion, deviceType, deviceDetails, iconArr, localServiceArr, list.size() > 0 ? (LocalDevice[]) list.toArray(new LocalDevice[list.size()]) : null);
	}

	@Override // org.teleal.cling.model.meta.Device
	public LocalService[] newServiceArray(int i) {
		return new LocalService[i];
	}

	@Override // org.teleal.cling.model.meta.Device, org.teleal.cling.model.Validatable
	public List<ValidationError> validate() {
		ArrayList arrayList = new ArrayList();
		arrayList.addAll(super.validate());
		if (hasIcons()) {
			for (Icon icon : getIcons()) {
				if (icon.getUri().isAbsolute()) {
					arrayList.add(new ValidationError(getClass(), "icons", "Local icon URI can not be absolute: " + icon.getUri()));
				}
				if (icon.getUri().toString().contains("../")) {
					arrayList.add(new ValidationError(getClass(), "icons", "Local icon URI must not contain '../': " + icon.getUri()));
				}
				if (icon.getUri().toString().startsWith(ServiceReference.DELIMITER)) {
					arrayList.add(new ValidationError(getClass(), "icons", "Local icon URI must not start with '/': " + icon.getUri()));
				}
			}
		}
		return arrayList;
	}

	@Override // org.teleal.cling.model.meta.Device
	public Resource[] discoverResources(Namespace namespace) {
		ArrayList arrayList = new ArrayList();
		if (isRoot()) {
			arrayList.add(new DeviceDescriptorResource(namespace.getDescriptorPath(this), this));
		}
		for (LocalService localService : getServices()) {
			arrayList.add(new ServiceDescriptorResource(namespace.getDescriptorPath(localService), localService));
			arrayList.add(new ServiceControlResource(namespace.getControlPath(localService), localService));
			arrayList.add(new ServiceEventSubscriptionResource(namespace.getEventSubscriptionPath(localService), localService));
		}
		for (Icon icon : getIcons()) {
			arrayList.add(new IconResource(namespace.prefixIfRelative(this, icon.getUri()), icon));
		}
		if (hasEmbeddedDevices()) {
			for (LocalDevice localDevice : getEmbeddedDevices()) {
				arrayList.addAll(Arrays.asList(localDevice.discoverResources(namespace)));
			}
		}
		return (Resource[]) arrayList.toArray(new Resource[arrayList.size()]);
	}

	@Override // org.teleal.cling.model.meta.Device
	public LocalDevice getRoot() {
		if (isRoot()) {
			return this;
		}
		LocalDevice parentDevice = this;
		while (parentDevice.getParentDevice() != null) {
			parentDevice = parentDevice.getParentDevice();
		}
		return parentDevice;
	}

	@Override // org.teleal.cling.model.meta.Device
	public LocalDevice findDevice(UDN udn) {
		return find(udn, this);
	}

	@Override // org.teleal.cling.model.meta.Device
	public LocalDevice[] toDeviceArray(Collection<LocalDevice> collection) {
		return collection != null ? (LocalDevice[]) collection.toArray(new LocalDevice[collection.size()]) : new LocalDevice[0];
	}

	@Override // org.teleal.cling.model.meta.Device
	public LocalService[] toServiceArray(Collection<LocalService> collection) {
		return collection != null ? (LocalService[]) collection.toArray(new LocalService[collection.size()]) : new LocalService[0];
	}

	@Override // org.teleal.cling.model.meta.Device
	public LocalService newInstance(ServiceType serviceType, ServiceId serviceId, URI uri, URI uri2, URI uri3, Action<LocalService>[] actionArr, StateVariable<LocalService>[] stateVariableArr) throws ValidationException {
		return new LocalService(serviceType, serviceId, actionArr, stateVariableArr);
	}
}

