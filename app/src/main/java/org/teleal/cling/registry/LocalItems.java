package org.teleal.cling.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Logger;
import org.teleal.cling.model.gena.CancelReason;
import org.teleal.cling.model.gena.GENASubscription;
import org.teleal.cling.model.gena.LocalGENASubscription;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.resource.Resource;
import org.teleal.cling.model.types.UDN;
import org.teleal.cling.protocol.async.SendingNotificationByebye;

/* JADX INFO: loaded from: classes.dex */
class LocalItems extends RegistryItems<LocalDevice, LocalGENASubscription> {
	private static Logger log = Logger.getLogger(Registry.class.getName());
	protected Random randomGenerator;

	LocalItems(RegistryImpl registryImpl) {
		super(registryImpl);
		this.randomGenerator = new Random();
	}

	/* JADX INFO: Access modifiers changed from: package-private */
	/* JADX WARN: Type inference incomplete: some casts might be missing */
	@Override // org.teleal.cling.registry.RegistryItems
	public void add(LocalDevice localDevice) throws RegistrationException {
		if (this.registry.getDevice(localDevice.getIdentity().getUdn(), false) != null) {
			log.fine("Ignoring addition, device already registered: " + localDevice);
			return;
		}
		log.fine("Adding local device to registry: " + localDevice);
		for (Resource resource : getResources(localDevice)) {
			if (this.registry.getResource(resource.getPathQuery()) != null) {
				throw new RegistrationException("URI namespace conflict with already registered resource: " + resource);
			}
			this.registry.addResource(resource);
			log.fine("Registered resource: " + resource);
		}
		log.fine("Adding item to registry with expiration in seconds: " + localDevice.getIdentity().getMaxAgeSeconds());
		RegistryItem<UDN, LocalDevice> registryItem = new RegistryItem<>(localDevice.getIdentity().getUdn(), localDevice, localDevice.getIdentity().getMaxAgeSeconds().intValue());
		this.deviceItems.add(registryItem);
		log.fine("Registered local device: " + registryItem);
		advertiseAlive(localDevice);
		Iterator<RegistryListener> it = this.registry.getListeners().iterator();
		while (it.hasNext()) {
			it.next().localDeviceAdded(this.registry, localDevice);
		}
	}

	@Override // org.teleal.cling.registry.RegistryItems
	Collection<LocalDevice> get() {
		HashSet hashSet = new HashSet();
		Iterator it = this.deviceItems.iterator();
		while (it.hasNext()) {
			hashSet.add(((RegistryItem) it.next()).getItem());
		}
		return Collections.unmodifiableCollection(hashSet);
	}

	/* JADX INFO: Access modifiers changed from: package-private */
	@Override // org.teleal.cling.registry.RegistryItems
	public boolean remove(LocalDevice localDevice) throws RegistrationException {
		return remove(localDevice, false);
	}

	boolean remove(final LocalDevice localDevice, boolean z) throws RegistrationException {
		LocalDevice localDevice2 = get(localDevice.getIdentity().getUdn(), true);
		if (localDevice2 == null) {
			return false;
		}
		log.fine("Removing local device from registry: " + localDevice);
		this.deviceItems.remove(new RegistryItem(localDevice.getIdentity().getUdn()));
		for (Resource resource : getResources(localDevice)) {
			if (this.registry.removeResource(resource)) {
				log.fine("Unregistered resource: " + resource);
			}
		}
		Iterator it = this.subscriptionItems.iterator();
		while (it.hasNext()) {
			final RegistryItem registryItem = (RegistryItem) it.next();
			if (((LocalGENASubscription) registryItem.getItem()).getService().getDevice().getIdentity().getUdn().equals(localDevice2.getIdentity().getUdn())) {
				log.fine("Removing incoming subscription: " + ((String) registryItem.getKey()));
				it.remove();
				if (!z) {
					this.registry.getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.registry.LocalItems.1
						@Override // java.lang.Runnable
						public void run() {
							((LocalGENASubscription) registryItem.getItem()).end(CancelReason.DEVICE_WAS_REMOVED);
						}
					});
				}
			}
		}
		advertiseByebye(localDevice, !z);
		if (!z) {
			for (final RegistryListener registryListener : this.registry.getListeners()) {
				this.registry.getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.registry.LocalItems.2
					@Override // java.lang.Runnable
					public void run() {
						registryListener.localDeviceRemoved(LocalItems.this.registry, localDevice);
					}
				});
			}
		}
		return true;
	}

	@Override // org.teleal.cling.registry.RegistryItems
	void removeAll() {
		removeAll(false);
	}

	void removeAll(boolean z) {
		for (LocalDevice localDevice : (LocalDevice[]) get().toArray(new LocalDevice[get().size()])) {
			remove(localDevice, z);
		}
	}

	@Override // org.teleal.cling.registry.RegistryItems
	void maintain() {
		if (this.deviceItems.isEmpty()) {
			return;
		}
		HashSet<RegistryItem> hashSet = new HashSet();
		Iterator it = this.deviceItems.iterator();
		while (it.hasNext()) {
			RegistryItem registryItem = (RegistryItem) it.next();
			if (registryItem.getExpirationDetails().hasExpired(true)) {
				log.finer("Local item has expired: " + registryItem);
				hashSet.add(registryItem);
			}
		}
		for (RegistryItem registryItem2 : hashSet) {
			log.fine("Refreshing local device advertisement: " + registryItem2.getItem());
			advertiseAlive((LocalDevice) registryItem2.getItem());
			registryItem2.getExpirationDetails().stampLastRefresh();
		}
		HashSet<RegistryItem> hashSet2 = new HashSet();
		Iterator it2 = this.subscriptionItems.iterator();
		while (it2.hasNext()) {
			RegistryItem registryItem3 = (RegistryItem) it2.next();
			if (registryItem3.getExpirationDetails().hasExpired(false)) {
				hashSet2.add(registryItem3);
			}
		}
		for (RegistryItem registryItem4 : hashSet2) {
			log.fine("Removing expired: " + registryItem4);
			removeSubscription((LocalGENASubscription) registryItem4.getItem());
			((LocalGENASubscription) registryItem4.getItem()).end(CancelReason.EXPIRED);
		}
	}

	@Override // org.teleal.cling.registry.RegistryItems
	void shutdown() {
		log.fine("Clearing all registered subscriptions to local devices during shutdown");
		this.subscriptionItems.clear();
		log.fine("Removing all local devices from registry during shutdown");
		removeAll(true);
	}

	protected void advertiseAlive(final LocalDevice localDevice) {
		this.registry.executeAsyncProtocol(new Runnable() { // from class: org.teleal.cling.registry.LocalItems.3
			@Override // java.lang.Runnable
			public void run() {
				try {
					LocalItems.log.finer("Sleeping some milliseconds to avoid flooding the network with ALIVE msgs");
					Thread.sleep(LocalItems.this.randomGenerator.nextInt(100));
				} catch (InterruptedException e) {
					LocalItems.log.severe("Background execution interrupted: " + e.getMessage());
				}
				LocalItems.this.registry.getProtocolFactory().createSendingNotificationAlive(localDevice).run();
			}
		});
	}

	protected void advertiseByebye(LocalDevice localDevice, boolean z) {
		SendingNotificationByebye sendingNotificationByebyeCreateSendingNotificationByebye = this.registry.getProtocolFactory().createSendingNotificationByebye(localDevice);
		if (z) {
			this.registry.executeAsyncProtocol(sendingNotificationByebyeCreateSendingNotificationByebye);
		} else {
			sendingNotificationByebyeCreateSendingNotificationByebye.run();
		}
	}
}
