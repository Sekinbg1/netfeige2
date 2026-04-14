package org.teleal.cling.controlpoint;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.teleal.cling.model.gena.CancelReason;
import org.teleal.cling.model.gena.GENASubscription;
import org.teleal.cling.model.gena.LocalGENASubscription;
import org.teleal.cling.model.gena.RemoteGENASubscription;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.RemoteService;
import org.teleal.cling.model.meta.Service;
import org.teleal.common.util.Exceptions;

/* JADX INFO: loaded from: classes.dex */
public abstract class SubscriptionCallback implements Runnable {
	protected static Logger log = Logger.getLogger(SubscriptionCallback.class.getName());
	private ControlPoint controlPoint;
	protected final Integer requestedDurationSeconds;
	protected final Service service;
	private GENASubscription subscription;

	protected abstract void ended(GENASubscription gENASubscription, CancelReason cancelReason, UpnpResponse upnpResponse);

	protected abstract void established(GENASubscription gENASubscription);

	protected abstract void eventReceived(GENASubscription gENASubscription);

	protected abstract void eventsMissed(GENASubscription gENASubscription, int i);

	protected abstract void failed(GENASubscription gENASubscription, UpnpResponse upnpResponse, Exception exc, String str);

	protected SubscriptionCallback(Service service) {
		this.service = service;
		this.requestedDurationSeconds = 1800;
	}

	protected SubscriptionCallback(Service service, int i) {
		this.service = service;
		this.requestedDurationSeconds = Integer.valueOf(i);
	}

	public Service getService() {
		return this.service;
	}

	public synchronized ControlPoint getControlPoint() {
		return this.controlPoint;
	}

	public synchronized void setControlPoint(ControlPoint controlPoint) {
		this.controlPoint = controlPoint;
	}

	public synchronized GENASubscription getSubscription() {
		return this.subscription;
	}

	public synchronized void setSubscription(GENASubscription gENASubscription) {
		this.subscription = gENASubscription;
	}

	@Override // java.lang.Runnable
	public synchronized void run() {
		if (getControlPoint() == null) {
			throw new IllegalStateException("Callback must be executed through ControlPoint");
		}
		if (getService() instanceof LocalService) {
			establishLocalSubscription((LocalService) this.service);
		} else if (getService() instanceof RemoteService) {
			establishRemoteSubscription((RemoteService) this.service);
		}
	}

	private void establishLocalSubscription(LocalService localService) {
		LocalGENASubscription localGENASubscription;
		if (getControlPoint().getRegistry().getLocalDevice(localService.getDevice().getIdentity().getUdn(), false) == null) {
			log.fine("Local device service is currently not registered, failing subscription immediately");
			failed(null, null, new IllegalStateException("Local device is not registered"));
			return;
		}
		try {
			localGENASubscription = new LocalGENASubscription(localService, Integer.valueOf(Integer.MAX_VALUE), Collections.EMPTY_LIST) { // from class: org.teleal.cling.controlpoint.SubscriptionCallback.1
				public void failed(Exception exc) {
					synchronized (SubscriptionCallback.this) {
						SubscriptionCallback.this.setSubscription(null);
						SubscriptionCallback.this.failed(null, null, exc);
					}
				}

				@Override // org.teleal.cling.model.gena.GENASubscription
				public void established() {
					synchronized (SubscriptionCallback.this) {
						SubscriptionCallback.this.setSubscription(this);
						SubscriptionCallback.this.established(this);
					}
				}

				@Override // org.teleal.cling.model.gena.LocalGENASubscription
				public void ended(CancelReason cancelReason) {
					synchronized (SubscriptionCallback.this) {
						SubscriptionCallback.this.setSubscription(null);
						SubscriptionCallback.this.ended(this, cancelReason, null);
					}
				}

				@Override // org.teleal.cling.model.gena.GENASubscription
				public void eventReceived() {
					synchronized (SubscriptionCallback.this) {
						SubscriptionCallback.log.fine("Local service state updated, notifying callback, sequence is: " + getCurrentSequence());
						SubscriptionCallback.this.eventReceived(this);
						incrementSequence();
					}
				}
			};
			try {
				log.fine("Local device service is currently registered, also registering subscription");
				getControlPoint().getRegistry().addLocalSubscription(localGENASubscription);
				log.fine("Notifying subscription callback of local subscription availablity");
				localGENASubscription.establish();
				log.fine("Simulating first initial event for local subscription callback, sequence: " + localGENASubscription.getCurrentSequence());
				eventReceived(localGENASubscription);
				localGENASubscription.incrementSequence();
				log.fine("Starting to monitor state changes of local service");
				localGENASubscription.registerOnService();
			} catch (Exception e) {
				log.fine("Local callback creation failed: " + e.toString());
				log.log(Level.FINE, "Exception root cause: ", Exceptions.unwrap(e));
				if (localGENASubscription != null) {
					getControlPoint().getRegistry().removeLocalSubscription(localGENASubscription);
				}
				failed(localGENASubscription, null, e);
			}
		} catch (Exception e2) {
			log.fine("Local subscription creation failed: " + e2.toString());
			log.log(Level.FINE, "Exception root cause: ", Exceptions.unwrap(e2));
			failed(null, null, e2);
		}
	}

	private void establishRemoteSubscription(RemoteService remoteService) {
		getControlPoint().getProtocolFactory().createSendingSubscribe(new RemoteGENASubscription(remoteService, this.requestedDurationSeconds.intValue()) { // from class: org.teleal.cling.controlpoint.SubscriptionCallback.2
			@Override // org.teleal.cling.model.gena.RemoteGENASubscription
			public void failed(UpnpResponse upnpResponse) {
				synchronized (SubscriptionCallback.this) {
					SubscriptionCallback.this.setSubscription(null);
					SubscriptionCallback.this.failed(this, upnpResponse, null);
				}
			}

			@Override // org.teleal.cling.model.gena.GENASubscription
			public void established() {
				synchronized (SubscriptionCallback.this) {
					SubscriptionCallback.this.setSubscription(this);
					SubscriptionCallback.this.established(this);
				}
			}

			@Override // org.teleal.cling.model.gena.RemoteGENASubscription
			public void ended(CancelReason cancelReason, UpnpResponse upnpResponse) {
				synchronized (SubscriptionCallback.this) {
					SubscriptionCallback.this.setSubscription(null);
					SubscriptionCallback.this.ended(this, cancelReason, upnpResponse);
				}
			}

			@Override // org.teleal.cling.model.gena.GENASubscription
			public void eventReceived() {
				synchronized (SubscriptionCallback.this) {
					SubscriptionCallback.this.eventReceived(this);
				}
			}

			@Override // org.teleal.cling.model.gena.RemoteGENASubscription
			public void eventsMissed(int i) {
				synchronized (SubscriptionCallback.this) {
					SubscriptionCallback.this.eventsMissed(this, i);
				}
			}
		}).run();
	}

	public synchronized void end() {
		if (this.subscription == null) {
			return;
		}
		if (this.subscription instanceof LocalGENASubscription) {
			endLocalSubscription((LocalGENASubscription) this.subscription);
		} else if (this.subscription instanceof RemoteGENASubscription) {
			endRemoteSubscription((RemoteGENASubscription) this.subscription);
		}
	}

	private void endLocalSubscription(LocalGENASubscription localGENASubscription) {
		log.fine("Removing local subscription and ending it in callback: " + localGENASubscription);
		getControlPoint().getRegistry().removeLocalSubscription(localGENASubscription);
		localGENASubscription.end(null);
	}

	private void endRemoteSubscription(RemoteGENASubscription remoteGENASubscription) {
		log.fine("Ending remote subscription: " + remoteGENASubscription);
		getControlPoint().getConfiguration().getSyncProtocolExecutor().execute(getControlPoint().getProtocolFactory().createSendingUnsubscribe(remoteGENASubscription));
	}

	protected void failed(GENASubscription gENASubscription, UpnpResponse upnpResponse, Exception exc) {
		failed(gENASubscription, upnpResponse, exc, createDefaultFailureMessage(upnpResponse, exc));
	}

	public static String createDefaultFailureMessage(UpnpResponse upnpResponse, Exception exc) {
		if (upnpResponse != null) {
			return "Subscription failed:  HTTP response was: " + upnpResponse.getResponseDetails();
		}
		if (exc != null) {
			return "Subscription failed:  Exception occured: " + exc;
		}
		return "Subscription failed:  No response received.";
	}

	public String toString() {
		return "(SubscriptionCallback) " + getService();
	}
}
