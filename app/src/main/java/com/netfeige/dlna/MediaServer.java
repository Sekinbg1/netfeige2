package com.netfeige.dlna;

import android.os.Build;
import android.util.Log;
import com.netfeige.common.Public_MsgID;
import java.io.IOException;
import java.net.InetAddress;
import org.teleal.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.teleal.cling.model.DefaultServiceManager;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.meta.DeviceDetails;
import org.teleal.cling.model.meta.DeviceIdentity;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.ManufacturerDetails;
import org.teleal.cling.model.meta.ModelDetails;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.model.types.UDN;

/* JADX INFO: loaded from: classes.dex */
public class MediaServer {
    private static final String LOGTAG = "IpMsg-MediaServer";
    private static final String m_deviceType = "MediaServer";
    private static InetAddress m_localAddress = null;
    private static final int m_port = 8192;
    private static final int m_version = 1;
    private HttpServer m_httpServer;
    private LocalDevice m_localDevice;
    private UDN udn = UDN.uniqueSystemIdentifier(LOGTAG);

    public MediaServer(InetAddress inetAddress) throws ValidationException {
        UDADeviceType uDADeviceType = new UDADeviceType(m_deviceType, 1);
        DeviceDetails deviceDetails = new DeviceDetails(Build.MODEL, new ManufacturerDetails(Build.MANUFACTURER), new ModelDetails("IpMsg", "IpMsg MediaServer for Android", "v1"));
        LocalService localService = new AnnotationLocalServiceBinder().read(ContentDirectoryService.class);
        localService.setManager(new DefaultServiceManager(localService, ContentDirectoryService.class));
        this.m_localDevice = new LocalDevice(new DeviceIdentity(this.udn), uDADeviceType, deviceDetails, localService);
        m_localAddress = inetAddress;
        Log.v(LOGTAG, "MediaServer device created: ");
        Log.v(LOGTAG, "friendly name: " + deviceDetails.getFriendlyName());
        Log.v(LOGTAG, "manufacturer: " + deviceDetails.getManufacturerDetails().getManufacturer());
        Log.v(LOGTAG, "model: " + deviceDetails.getModelDetails().getModelName());
        try {
            this.m_httpServer = new HttpServer(8192);
        } catch (IOException e) {
            System.err.println("Couldn't start server:\n" + e);
            System.exit(-1);
        }
        Log.v(LOGTAG, "Started Http Server on port 8192");
    }

    public LocalDevice getDevice() {
        return this.m_localDevice;
    }

    public String getAddress() {
        return m_localAddress.getHostAddress() + Public_MsgID.PRO_SPACE + 8192;
    }

    public HttpServer getHttpServer() {
        return this.m_httpServer;
    }
}

