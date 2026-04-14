package org.teleal.cling.support.contentdirectory.callback;

import java.util.logging.Logger;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionException;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.ErrorCode;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.support.contentdirectory.DIDLParser;
import org.teleal.cling.support.model.BrowseFlag;
import org.teleal.cling.support.model.BrowseResult;
import org.teleal.cling.support.model.DIDLContent;
import org.teleal.cling.support.model.SortCriterion;

/* JADX INFO: loaded from: classes.dex */
public abstract class Browse extends ActionCallback {
    public static final String CAPS_WILDCARD = "*";
    private static Logger log = Logger.getLogger(Browse.class.getName());

    public long getDefaultMaxResults() {
        return 999L;
    }

    public abstract void received(ActionInvocation actionInvocation, DIDLContent dIDLContent);

    public boolean receivedRaw(ActionInvocation actionInvocation, BrowseResult browseResult) {
        return true;
    }

    public abstract void updateStatus(Status status);

    public enum Status {
        NO_CONTENT("No Content"),
        LOADING("Loading..."),
        OK("OK");

        private String defaultMessage;

        Status(String str) {
            this.defaultMessage = str;
        }

        public String getDefaultMessage() {
            return this.defaultMessage;
        }
    }

    public Browse(Service service, String str, BrowseFlag browseFlag) {
        this(service, str, browseFlag, "*", 0L, null, new SortCriterion[0]);
    }

    public Browse(Service service, String str, BrowseFlag browseFlag, String str2, long j, Long l, SortCriterion... sortCriterionArr) {
        super(new ActionInvocation(service.getAction("Browse")));
        log.fine("Creating browse action for object ID: " + str);
        getActionInvocation().setInput("ObjectID", str);
        getActionInvocation().setInput("BrowseFlag", browseFlag.toString());
        getActionInvocation().setInput("Filter", str2);
        getActionInvocation().setInput("StartingIndex", new UnsignedIntegerFourBytes(j));
        getActionInvocation().setInput("RequestedCount", new UnsignedIntegerFourBytes(l == null ? getDefaultMaxResults() : l.longValue()));
        getActionInvocation().setInput("SortCriteria", SortCriterion.toString(sortCriterionArr));
    }

    @Override // org.teleal.cling.controlpoint.ActionCallback, java.lang.Runnable
    public void run() {
        updateStatus(Status.LOADING);
        super.run();
    }

    @Override // org.teleal.cling.controlpoint.ActionCallback
    public void success(ActionInvocation actionInvocation) {
        log.fine("Successful browse action, reading output argument values");
        BrowseResult browseResult = new BrowseResult(actionInvocation.getOutput("Result").getValue().toString(), (UnsignedIntegerFourBytes) actionInvocation.getOutput("NumberReturned").getValue(), (UnsignedIntegerFourBytes) actionInvocation.getOutput("TotalMatches").getValue(), (UnsignedIntegerFourBytes) actionInvocation.getOutput("UpdateID").getValue());
        if (receivedRaw(actionInvocation, browseResult) && browseResult.getCountLong() > 0 && browseResult.getResult().length() > 0) {
            try {
                received(actionInvocation, new DIDLParser().parse(browseResult.getResult()));
                updateStatus(Status.OK);
                return;
            } catch (Exception e) {
                actionInvocation.setFailure(new ActionException(ErrorCode.ACTION_FAILED, "Can't parse DIDL XML response: " + e, e));
                failure(actionInvocation, null);
                return;
            }
        }
        received(actionInvocation, new DIDLContent());
        updateStatus(Status.NO_CONTENT);
    }
}

