package org.teleal.cling.support.contentdirectory.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import org.teleal.cling.model.action.ActionException;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.ErrorCode;
import org.teleal.cling.support.contentdirectory.callback.Browse;
import org.teleal.cling.support.model.BrowseFlag;
import org.teleal.cling.support.model.DIDLContent;
import org.teleal.cling.support.model.SortCriterion;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.Item;

/* JADX INFO: loaded from: classes.dex */
public abstract class ContentBrowseActionCallback extends Browse {
    private static Logger log = Logger.getLogger(ContentBrowseActionCallback.class.getName());

    public abstract void failureUI(String str);






    @Override // org.teleal.cling.support.contentdirectory.callback.Browse
    public void received(ActionInvocation actionInvocation, DIDLContent dIDLContent) {
        log.fine("Received browse action DIDL descriptor, creating tree nodes");
        final ArrayList<Container> containers = new ArrayList<>();
        final ArrayList<Item> items = new ArrayList<>();
        try {
            Iterator<Container> it = dIDLContent.getContainers().iterator();
            while (it.hasNext()) {
                containers.add(it.next());
            }
            Iterator<Item> it2 = dIDLContent.getItems().iterator();
            while (it2.hasNext()) {
                items.add(it2.next());
            }
            updateUI(containers, items);
        } catch (Exception e) {
            log.fine("Creating DIDL tree nodes failed: " + e);
            actionInvocation.setFailure(new ActionException(ErrorCode.ACTION_FAILED, "Can't create tree child nodes: " + e, e));
            failure(actionInvocation, null);
        }
    }

    @Override // org.teleal.cling.support.contentdirectory.callback.Browse
    public void updateStatus(final Browse.Status status) {
        updateStatusUI(status);
    }

    @Override // org.teleal.cling.controlpoint.ActionCallback
    public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, final String str) {
        failureUI(str);
    }

    protected abstract void updateUI(List<Container> containers, List<Item> items);

    protected abstract void updateStatusUI(Browse.Status status);



}

