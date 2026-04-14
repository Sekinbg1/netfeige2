package org.teleal.cling.support.connectionmanager;

import java.beans.PropertyChangeSupport;
import java.util.logging.Logger;
import org.teleal.cling.binding.annotations.UpnpAction;
import org.teleal.cling.binding.annotations.UpnpInputArgument;
import org.teleal.cling.binding.annotations.UpnpOutputArgument;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.ServiceReference;
import org.teleal.cling.model.action.ActionException;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.ErrorCode;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.model.types.csv.CSV;
import org.teleal.cling.support.connectionmanager.callback.ConnectionComplete;
import org.teleal.cling.support.connectionmanager.callback.PrepareForConnection;
import org.teleal.cling.support.model.ConnectionInfo;
import org.teleal.cling.support.model.ProtocolInfo;
import org.teleal.cling.support.model.ProtocolInfos;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractPeeringConnectionManagerService extends ConnectionManagerService {
    private static final Logger log = Logger.getLogger(AbstractPeeringConnectionManagerService.class.getName());

    protected abstract void closeConnection(ConnectionInfo connectionInfo);

    protected abstract ConnectionInfo createConnection(int i, int i2, ServiceReference serviceReference, ConnectionInfo.Direction direction, ProtocolInfo protocolInfo) throws ActionException;

    protected abstract void peerFailure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str);

    protected AbstractPeeringConnectionManagerService(ConnectionInfo... connectionInfoArr) {
        super(connectionInfoArr);
    }

    protected AbstractPeeringConnectionManagerService(ProtocolInfos protocolInfos, ProtocolInfos protocolInfos2, ConnectionInfo... connectionInfoArr) {
        super(protocolInfos, protocolInfos2, connectionInfoArr);
    }

    protected AbstractPeeringConnectionManagerService(PropertyChangeSupport propertyChangeSupport, ProtocolInfos protocolInfos, ProtocolInfos protocolInfos2, ConnectionInfo... connectionInfoArr) {
        super(propertyChangeSupport, protocolInfos, protocolInfos2, connectionInfoArr);
    }

    protected synchronized int getNewConnectionId() {
        int iIntValue;
        iIntValue = -1;
        for (Integer num : this.activeConnections.keySet()) {
            if (num.intValue() > iIntValue) {
                iIntValue = num.intValue();
            }
        }
        return iIntValue + 1;
    }

    protected synchronized void storeConnection(ConnectionInfo connectionInfo) {
        CSV<UnsignedIntegerFourBytes> currentConnectionIDs = getCurrentConnectionIDs();
        this.activeConnections.put(Integer.valueOf(connectionInfo.getConnectionID()), connectionInfo);
        log.fine("Connection stored, firing event: " + connectionInfo.getConnectionID());
        getPropertyChangeSupport().firePropertyChange("CurrentConnectionIDs", currentConnectionIDs, getCurrentConnectionIDs());
    }

    protected synchronized void removeConnection(int i) {
        CSV<UnsignedIntegerFourBytes> currentConnectionIDs = getCurrentConnectionIDs();
        this.activeConnections.remove(Integer.valueOf(i));
        log.fine("Connection removed, firing event: " + i);
        getPropertyChangeSupport().firePropertyChange("CurrentConnectionIDs", currentConnectionIDs, getCurrentConnectionIDs());
    }

    @UpnpAction(out = {@UpnpOutputArgument(getterName = "getConnectionID", name = "ConnectionID", stateVariable = "A_ARG_TYPE_ConnectionID"), @UpnpOutputArgument(getterName = "getAvTransportID", name = "AVTransportID", stateVariable = "A_ARG_TYPE_AVTransportID"), @UpnpOutputArgument(getterName = "getRcsID", name = "RcsID", stateVariable = "A_ARG_TYPE_RcsID")})
    public synchronized ConnectionInfo prepareForConnection(@UpnpInputArgument(name = "RemoteProtocolInfo", stateVariable = "A_ARG_TYPE_ProtocolInfo") ProtocolInfo protocolInfo, @UpnpInputArgument(name = "PeerConnectionManager", stateVariable = "A_ARG_TYPE_ConnectionManager") ServiceReference serviceReference, @UpnpInputArgument(name = "PeerConnectionID", stateVariable = "A_ARG_TYPE_ConnectionID") int i, @UpnpInputArgument(name = "Direction", stateVariable = "A_ARG_TYPE_Direction") String str) throws ActionException {
        ConnectionInfo connectionInfoCreateConnection;
        int newConnectionId = getNewConnectionId();
        try {
            ConnectionInfo.Direction directionValueOf = ConnectionInfo.Direction.valueOf(str);
            log.fine("Preparing for connection with local new ID " + newConnectionId + " and peer connection ID: " + i);
            connectionInfoCreateConnection = createConnection(newConnectionId, i, serviceReference, directionValueOf, protocolInfo);
            storeConnection(connectionInfoCreateConnection);
        } catch (Exception unused) {
            throw new ConnectionManagerException(ErrorCode.ARGUMENT_VALUE_INVALID, "Unsupported direction: " + str);
        }
        return connectionInfoCreateConnection;
    }

    @UpnpAction
    public synchronized void connectionComplete(@UpnpInputArgument(name = "ConnectionID", stateVariable = "A_ARG_TYPE_ConnectionID") int i) throws ActionException {
        ConnectionInfo currentConnectionInfo = getCurrentConnectionInfo(i);
        log.fine("Closing connection ID " + i);
        closeConnection(currentConnectionInfo);
        removeConnection(i);
    }

    public synchronized int createConnectionWithPeer(ServiceReference serviceReference, ControlPoint controlPoint, final Service service, final ProtocolInfo protocolInfo, final ConnectionInfo.Direction direction) {
        final int newConnectionId;
        newConnectionId = getNewConnectionId();
        log.fine("Creating new connection ID " + newConnectionId + " with peer: " + service);
        final boolean[] zArr = new boolean[1];
        new PrepareForConnection(service, controlPoint, protocolInfo, serviceReference, newConnectionId, direction) { // from class: org.teleal.cling.support.connectionmanager.AbstractPeeringConnectionManagerService.1
            @Override // org.teleal.cling.support.connectionmanager.callback.PrepareForConnection
            public void received(ActionInvocation actionInvocation, int i, int i2, int i3) {
                AbstractPeeringConnectionManagerService.this.storeConnection(new ConnectionInfo(newConnectionId, i2, i3, protocolInfo, service.getReference(), i, direction.getOpposite(), ConnectionInfo.Status.OK));
            }

            @Override // org.teleal.cling.controlpoint.ActionCallback
            public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str) {
                AbstractPeeringConnectionManagerService.this.peerFailure(actionInvocation, upnpResponse, str);
                zArr[0] = true;
            }
        }.run();
        if (zArr[0]) {
            newConnectionId = -1;
        }
        return newConnectionId;
    }

    public synchronized void closeConnectionWithPeer(ControlPoint controlPoint, Service service, int i) throws ActionException {
        closeConnectionWithPeer(controlPoint, service, getCurrentConnectionInfo(i));
    }

    public synchronized void closeConnectionWithPeer(ControlPoint controlPoint, Service service, final ConnectionInfo connectionInfo) throws ActionException {
        log.fine("Closing connection ID " + connectionInfo.getConnectionID() + " with peer: " + service);
        new ConnectionComplete(service, controlPoint, connectionInfo.getPeerConnectionID()) { // from class: org.teleal.cling.support.connectionmanager.AbstractPeeringConnectionManagerService.2
            @Override // org.teleal.cling.controlpoint.ActionCallback
            public void success(ActionInvocation actionInvocation) {
                AbstractPeeringConnectionManagerService.this.removeConnection(connectionInfo.getConnectionID());
            }

            @Override // org.teleal.cling.controlpoint.ActionCallback
            public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str) {
                AbstractPeeringConnectionManagerService.this.peerFailure(actionInvocation, upnpResponse, str);
            }
        }.run();
    }
}

