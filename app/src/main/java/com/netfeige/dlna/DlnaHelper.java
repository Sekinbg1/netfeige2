package com.netfeige.dlna;

import com.netfeige.common.Public_MsgID;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.data.IpmsgApplication;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.support.avtransport.callback.Stop;

/* JADX INFO: loaded from: classes.dex */
public class DlnaHelper {
    private static final int m_port = 8192;
    DeviceType type = new UDADeviceType("MediaRenderer", 1);

    public static void stopPlay(Service service, IpmsgApplication ipmsgApplication) {
        if (service == null) {
            return;
        }
        try {
            ipmsgApplication.getControlPoint().execute(new Stop(service) { // from class: com.netfeige.dlna.DlnaHelper.1
                @Override // org.teleal.cling.support.avtransport.callback.Stop, org.teleal.cling.controlpoint.ActionCallback
                public void success(ActionInvocation actionInvocation) {
                    System.out.println("dlna:stop:success");
                    super.success(actionInvocation);
                }

                @Override // org.teleal.cling.controlpoint.ActionCallback
                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str) {
                    System.out.println("dlna:stop:failure");
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static String getHttpUri(String str) {
        if (str == null) {
            return null;
        }
        return "http:/" + Public_Tools.getLocalIpAddress() + Public_MsgID.PRO_SPACE + 8192 + str;
    }
}

