package org.teleal.cling.model.meta;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.teleal.cling.model.Namespace;
import org.teleal.cling.model.Validatable;
import org.teleal.cling.model.ValidationError;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.DeviceIdentity;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.profile.ControlPointInfo;
import org.teleal.cling.model.resource.Resource;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.ServiceId;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.model.types.UDN;

/* JADX INFO: loaded from: classes.dex */
public abstract class Device<DI extends DeviceIdentity, D extends Device, S extends Service> implements Validatable {
	private static final Logger log = Logger.getLogger(Device.class.getName());
	private final DeviceDetails details;
	protected final D[] embeddedDevices;
	private final Icon[] icons;
	private final DI identity;
	private D parentDevice;
	protected final S[] services;
	private final DeviceType type;
	private final UDAVersion version;

	public abstract Resource[] discoverResources(Namespace namespace);

	public abstract D findDevice(UDN udn);

	public abstract D[] getEmbeddedDevices();

	public abstract D getRoot();

	public abstract S[] getServices();

	public abstract D newInstance(UDN udn, UDAVersion uDAVersion, DeviceType deviceType, DeviceDetails deviceDetails, Icon[] iconArr, S[] sArr, List<D> list) throws ValidationException;

	public abstract S newInstance(ServiceType serviceType, ServiceId serviceId, URI uri, URI uri2, URI uri3, Action<S>[] actionArr, StateVariable<S>[] stateVariableArr) throws ValidationException;

	public abstract S[] newServiceArray(int i);

	public abstract D[] toDeviceArray(Collection<D> collection);

	public abstract S[] toServiceArray(Collection<S> collection);

	public Device(DI di) throws ValidationException {
		this(di, null, null, null, null, null);
	}

	public Device(DI di, DeviceType deviceType, DeviceDetails deviceDetails, Icon[] iconArr, S[] sArr) throws ValidationException {
		this(di, null, deviceType, deviceDetails, iconArr, sArr, null);
	}

	public Device(DI di, DeviceType deviceType, DeviceDetails deviceDetails, Icon[] iconArr, S[] sArr, D[] dArr) throws ValidationException {
		this(di, null, deviceType, deviceDetails, iconArr, sArr, dArr);
	}

	public Device(DI di, UDAVersion uDAVersion, DeviceType deviceType, DeviceDetails deviceDetails, Icon[] iconArr, S[] sArr, D[] dArr) throws ValidationException {
		boolean z;
		boolean z2;
		this.identity = di;
		this.version = uDAVersion == null ? new UDAVersion() : uDAVersion;
		this.type = deviceType;
		this.details = deviceDetails;
		boolean z3 = true;
		if (iconArr != null) {
			z = true;
			for (Icon icon : iconArr) {
				if (icon != null) {
					icon.setDevice(this);
					z = false;
				}
			}
		} else {
			z = true;
		}
		this.icons = (iconArr == null || z) ? new Icon[0] : iconArr;
		if (sArr != null) {
			z2 = true;
			for (S s : sArr) {
				if (s != null) {
					s.setDevice(this);
					z2 = false;
				}
			}
		} else {
			z2 = true;
		}
		this.services = (sArr == null || z2) ? null : sArr;
		if (dArr != null) {
			for (D d : dArr) {
				if (d != null) {
					d.setParentDevice(this);
					z3 = false;
				}
			}
		}
		this.embeddedDevices = (dArr == null || z3) ? null : dArr;
		List<ValidationError> listValidate = validate();
		if (listValidate.size() > 0) {
			if (log.isLoggable(Level.FINEST)) {
				Iterator<ValidationError> it = listValidate.iterator();
				while (it.hasNext()) {
					log.finest(it.next().toString());
				}
			}
			throw new ValidationException("Validation of device graph failed, call getErrors() on exception", listValidate);
		}
	}

	public DI getIdentity() {
		return this.identity;
	}

	public UDAVersion getVersion() {
		return this.version;
	}

	public DeviceType getType() {
		return this.type;
	}

	public DeviceDetails getDetails() {
		return this.details;
	}

	public DeviceDetails getDetails(ControlPointInfo controlPointInfo) {
		return getDetails();
	}

	public Icon[] getIcons() {
		return this.icons;
	}

	public boolean hasIcons() {
		return getIcons() != null && getIcons().length > 0;
	}

	public boolean hasServices() {
		return getServices() != null && getServices().length > 0;
	}

	public boolean hasEmbeddedDevices() {
		return getEmbeddedDevices() != null && getEmbeddedDevices().length > 0;
	}

	public D getParentDevice() {
		return this.parentDevice;
	}

	void setParentDevice(D d) {
		if (this.parentDevice != null) {
			throw new IllegalStateException("Final value has been set already, model is immutable");
		}
		this.parentDevice = d;
	}

	public boolean isRoot() {
		return getParentDevice() == null;
	}

	public D[] findEmbeddedDevices() {
		Collection<D> devices = findEmbeddedDevices((D) this);
		return toDeviceArray(devices);
	}

	public D[] findDevices(DeviceType deviceType) {
		Collection<D> devices = find(deviceType, (D) this);
		return toDeviceArray(devices);
	}

	public D[] findDevices(ServiceType serviceType) {
		Collection<D> devices = find(serviceType, (D) this);
		return toDeviceArray(devices);
	}

	public Icon[] findIcons() {
		ArrayList arrayList = new ArrayList();
		if (hasIcons()) {
			arrayList.addAll(Arrays.asList(getIcons()));
		}
		for (Device device : findEmbeddedDevices()) {
			if (device.hasIcons()) {
				arrayList.addAll(Arrays.asList(device.getIcons()));
			}
		}
		return (Icon[]) arrayList.toArray(new Icon[arrayList.size()]);
	}

	public S[] findServices() {
		Collection<S> services = findServices(null, null, (D) this);
		return toServiceArray(services);
	}

	public S[] findServices(ServiceType serviceType) {
		Collection<S> services = findServices(serviceType, null, (D) this);
		return toServiceArray(services);
	}

	protected D find(UDN udn, D d) {
		if (d.getIdentity().getUdn().equals(udn)) {
			return d;
		}
		if (!d.hasEmbeddedDevices()) {
			return null;
		}
		for (Device device : d.getEmbeddedDevices()) {
			D d2 = find(udn, (D) device);
			if (d2 != null) {
				return d2;
			}
		}
		return null;
	}

	protected Collection<D> findEmbeddedDevices(D d) {
		HashSet hashSet = new HashSet();
		if (!d.isRoot()) {
			hashSet.add(d);
		}
		if (d.hasEmbeddedDevices()) {
			for (Device device : d.getEmbeddedDevices()) {
				hashSet.addAll(findEmbeddedDevices((D) device));
			}
		}
		return hashSet;
	}

	protected Collection<D> find(DeviceType deviceType, D d) {
		HashSet hashSet = new HashSet();
		if (d.getType() != null && d.getType().implementsVersion(deviceType)) {
			hashSet.add(d);
		}
		if (d.hasEmbeddedDevices()) {
			for (Device device : d.getEmbeddedDevices()) {
				hashSet.addAll(find(deviceType, (D) device));
			}
		}
		return hashSet;
	}

	protected Collection<D> find(ServiceType serviceType, D d) {
		Collection<S> collectionFindServices = findServices(serviceType, null, d);
		HashSet hashSet = new HashSet();
		Iterator<S> it = collectionFindServices.iterator();
		while (it.hasNext()) {
			hashSet.add(it.next().getDevice());
		}
		return hashSet;
	}

	protected Collection<S> findServices(ServiceType serviceType, ServiceId serviceId, D d) {
		HashSet hashSet = new HashSet();
		if (d.hasServices()) {
			for (Service service : d.getServices()) {
				if (isMatch(service, serviceType, serviceId)) {
					hashSet.add(service);
				}
			}
		}
		Collection<D> collectionFindEmbeddedDevices = findEmbeddedDevices(d);
		if (collectionFindEmbeddedDevices != null) {
			for (D d2 : collectionFindEmbeddedDevices) {
				if (d2.hasServices()) {
					for (Service service2 : d2.getServices()) {
						if (isMatch(service2, serviceType, serviceId)) {
							hashSet.add(service2);
						}
					}
				}
			}
		}
		return hashSet;
	}

	public S findService(ServiceId serviceId) {
		Collection<S> collectionFindServices = findServices(null, serviceId, (D) this);
		if (collectionFindServices.size() == 1) {
			return collectionFindServices.iterator().next();
		}
		return null;
	}

	public S findService(ServiceType serviceType) {
		Collection<S> collectionFindServices = findServices(serviceType, null, (D) this);
		if (collectionFindServices.size() > 0) {
			return collectionFindServices.iterator().next();
		}
		return null;
	}

	public ServiceType[] findServiceTypes() {
		Collection<S> collectionFindServices = findServices(null, null, (D) this);
		HashSet hashSet = new HashSet();
		Iterator<S> it = collectionFindServices.iterator();
		while (it.hasNext()) {
			hashSet.add(it.next().getServiceType());
		}
		return (ServiceType[]) hashSet.toArray(new ServiceType[hashSet.size()]);
	}

	private boolean isMatch(Service service, ServiceType serviceType, ServiceId serviceId) {
		return (serviceType == null || service.getServiceType().implementsVersion(serviceType)) && (serviceId == null || service.getServiceId().equals(serviceId));
	}

	public boolean isFullyHydrated() {
		for (Service service : findServices()) {
			if (service.hasStateVariables()) {
				return true;
			}
		}
		return false;
	}

	public String getDisplayString() {
		String modelNumber;
		String str;
		String strTrim = null;
		String str2 = "";
		if (getDetails() == null || getDetails().getModelDetails() == null) {
			modelNumber = null;
		} else {
			ModelDetails modelDetails = getDetails().getModelDetails();
			if (modelDetails.getModelName() != null) {
				strTrim = (modelDetails.getModelNumber() == null || !modelDetails.getModelName().endsWith(modelDetails.getModelNumber())) ? modelDetails.getModelName() : modelDetails.getModelName().substring(0, modelDetails.getModelName().length() - modelDetails.getModelNumber().length());
			}
			if (strTrim != null) {
				modelNumber = (modelDetails.getModelNumber() == null || strTrim.startsWith(modelDetails.getModelNumber())) ? "" : modelDetails.getModelNumber();
			} else {
				modelNumber = modelDetails.getModelNumber();
			}
		}
		StringBuilder sb = new StringBuilder();
		if (getDetails() != null && getDetails().getManufacturerDetails() != null) {
			if (strTrim != null && getDetails().getManufacturerDetails().getManufacturer() != null) {
				if (strTrim.startsWith(getDetails().getManufacturerDetails().getManufacturer())) {
					strTrim = strTrim.substring(getDetails().getManufacturerDetails().getManufacturer().length());
				}
				strTrim = strTrim.trim();
			}
			if (getDetails().getManufacturerDetails().getManufacturer() != null) {
				sb.append(getDetails().getManufacturerDetails().getManufacturer());
			}
		}
		if (strTrim == null || strTrim.length() <= 0) {
			str = "";
		} else {
			str = " " + strTrim;
		}
		sb.append(str);
		if (modelNumber != null && modelNumber.length() > 0) {
			str2 = " " + modelNumber.trim();
		}
		sb.append(str2);
		return sb.toString();
	}

	@Override // org.teleal.cling.model.Validatable
	public List<ValidationError> validate() {
		ArrayList arrayList = new ArrayList();
		if (getType() != null) {
			arrayList.addAll(getVersion().validate());
			if (getDetails() != null) {
				arrayList.addAll(getDetails().validate());
			}
			if (hasIcons()) {
				for (Icon icon : getIcons()) {
					if (icon != null) {
						arrayList.addAll(icon.validate());
					}
				}
			}
			if (hasServices()) {
				for (Service service : getServices()) {
					if (service != null) {
						arrayList.addAll(service.validate());
					}
				}
			}
			if (hasEmbeddedDevices()) {
				for (Device device : getEmbeddedDevices()) {
					if (device != null) {
						arrayList.addAll(device.validate());
					}
				}
			}
		}
		return arrayList;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		return obj != null && getClass() == obj.getClass() && this.identity.equals(((Device) obj).identity);
	}

	public int hashCode() {
		return this.identity.hashCode();
	}

	public String toString() {
		return "(" + getClass().getSimpleName() + ") Identity: " + getIdentity().toString() + ", Root: " + isRoot();
	}
}

