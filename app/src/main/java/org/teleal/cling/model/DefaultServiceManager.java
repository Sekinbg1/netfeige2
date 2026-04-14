package org.teleal.cling.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.StateVariable;
import org.teleal.cling.model.state.StateVariableAccessor;
import org.teleal.cling.model.state.StateVariableValue;
import org.teleal.common.util.Exceptions;
import org.teleal.common.util.Reflections;

/* JADX INFO: loaded from: classes.dex */
public class DefaultServiceManager<T> implements ServiceManager<T> {
    private static Logger log = Logger.getLogger(DefaultServiceManager.class.getName());
    protected final ReentrantLock lock;
    protected PropertyChangeSupport propertyChangeSupport;
    protected final LocalService<T> service;
    protected final Class<T> serviceClass;
    protected T serviceImpl;

    protected int getLockTimeoutMillis() {
        return 500;
    }

    protected DefaultServiceManager(LocalService<T> localService) {
        this(localService, null);
    }

    public DefaultServiceManager(LocalService<T> localService, Class<T> cls) {
        this.lock = new ReentrantLock(true);
        this.service = localService;
        this.serviceClass = cls;
    }

    protected void lock() {
        try {
            if (this.lock.tryLock(getLockTimeoutMillis(), TimeUnit.MILLISECONDS)) {
                log.fine("Acquired lock");
                return;
            }
            throw new RuntimeException("Failed to acquire lock in milliseconds: " + getLockTimeoutMillis());
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to acquire lock:" + e);
        }
    }

    protected void unlock() {
        log.fine("Releasing lock");
        this.lock.unlock();
    }

    @Override // org.teleal.cling.model.ServiceManager
    public LocalService<T> getService() {
        return this.service;
    }

    @Override // org.teleal.cling.model.ServiceManager
    public T getImplementation() {
        lock();
        try {
            if (this.serviceImpl == null) {
                init();
            }
            return this.serviceImpl;
        } finally {
            unlock();
        }
    }

    @Override // org.teleal.cling.model.ServiceManager
    public PropertyChangeSupport getPropertyChangeSupport() {
        lock();
        try {
            if (this.propertyChangeSupport == null) {
                init();
            }
            return this.propertyChangeSupport;
        } finally {
            unlock();
        }
    }

    @Override // org.teleal.cling.model.ServiceManager
    public void execute(Command<T> command) throws Exception {
        lock();
        try {
            command.execute(this);
        } finally {
            unlock();
        }
    }

    @Override // org.teleal.cling.model.ServiceManager
    public Collection<StateVariableValue> readEventedStateVariableValues() throws Exception {
        lock();
        try {
            ArrayList arrayList = new ArrayList();
            for (StateVariable<LocalService> stateVariable : getService().getStateVariables()) {
                if (stateVariable.getEventDetails().isSendEvents()) {
                    StateVariableAccessor accessor = getService().getAccessor(stateVariable);
                    if (accessor == null) {
                        throw new IllegalStateException("No accessor for evented state variable");
                    }
                    arrayList.add(accessor.read(stateVariable, getImplementation()));
                }
            }
            return arrayList;
        } finally {
            unlock();
        }
    }

    protected void init() {
        log.fine("No service implementation instance available, initializing...");
        try {
            T tCreateServiceInstance = createServiceInstance();
            this.serviceImpl = tCreateServiceInstance;
            PropertyChangeSupport propertyChangeSupportCreatePropertyChangeSupport = createPropertyChangeSupport(tCreateServiceInstance);
            this.propertyChangeSupport = propertyChangeSupportCreatePropertyChangeSupport;
            propertyChangeSupportCreatePropertyChangeSupport.addPropertyChangeListener(createPropertyChangeListener(this.serviceImpl));
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize implementation: " + e, e);
        }
    }

    protected T createServiceInstance() throws Exception {
        Class<T> cls = this.serviceClass;
        if (cls == null) {
            throw new IllegalStateException("Subclass has to provide service class or override createServiceInstance()");
        }
        try {
            return cls.getConstructor(LocalService.class).newInstance(getService());
        } catch (NoSuchMethodException unused) {
            log.fine("Creating new service implementation instance with no-arg constructor: " + this.serviceClass.getName());
            return this.serviceClass.newInstance();
        }
    }

    protected PropertyChangeSupport createPropertyChangeSupport(T t) throws Exception {
        Method getterMethod = Reflections.getGetterMethod(t.getClass(), "propertyChangeSupport");
        if (getterMethod != null && PropertyChangeSupport.class.isAssignableFrom(getterMethod.getReturnType())) {
            log.fine("Service implementation instance offers PropertyChangeSupport, using that: " + t.getClass().getName());
            return (PropertyChangeSupport) getterMethod.invoke(t, new Object[0]);
        }
        log.fine("Creating new PropertyChangeSupport for service implementation: " + t.getClass().getName());
        return new PropertyChangeSupport(t);
    }

    protected PropertyChangeListener createPropertyChangeListener(T t) throws Exception {
        return new DefaultPropertyChangeListener();
    }

    public String toString() {
        return "(" + getClass().getSimpleName() + ") Implementation: " + this.serviceImpl;
    }

    protected class DefaultPropertyChangeListener implements PropertyChangeListener {
        protected DefaultPropertyChangeListener() {
        }

        @Override // java.beans.PropertyChangeListener
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            StateVariable<LocalService> stateVariable;
            DefaultServiceManager.log.finer("Property change event on local service: " + propertyChangeEvent.getPropertyName());
            if (propertyChangeEvent.getPropertyName().equals(ServiceManager.EVENTED_STATE_VARIABLES) || (stateVariable = DefaultServiceManager.this.getService().getStateVariable(propertyChangeEvent.getPropertyName())) == null || !stateVariable.getEventDetails().isSendEvents()) {
                return;
            }
            try {
                DefaultServiceManager.log.fine("Evented state variable value changed, reading state of service: " + stateVariable);
                DefaultServiceManager.this.getPropertyChangeSupport().firePropertyChange(ServiceManager.EVENTED_STATE_VARIABLES, (Object) null, DefaultServiceManager.this.readEventedStateVariableValues());
            } catch (Exception e) {
                DefaultServiceManager.log.severe("Error reading state of service after state variable update event: " + Exceptions.unwrap(e));
            }
        }
    }
}

