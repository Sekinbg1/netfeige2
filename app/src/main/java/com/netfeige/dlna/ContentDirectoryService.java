package com.netfeige.dlna;

import android.util.Log;
import org.teleal.cling.support.contentdirectory.AbstractContentDirectoryService;
import org.teleal.cling.support.contentdirectory.ContentDirectoryErrorCode;
import org.teleal.cling.support.contentdirectory.ContentDirectoryException;
import org.teleal.cling.support.contentdirectory.DIDLParser;
import org.teleal.cling.support.model.BrowseFlag;
import org.teleal.cling.support.model.BrowseResult;
import org.teleal.cling.support.model.DIDLContent;
import org.teleal.cling.support.model.SortCriterion;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.Item;

/* JADX INFO: loaded from: classes.dex */
public class ContentDirectoryService extends AbstractContentDirectoryService {
    private static final String LOGTAG = "MediaServer-CDS";

    @Override // org.teleal.cling.support.contentdirectory.AbstractContentDirectoryService
    public BrowseResult browse(String str, BrowseFlag browseFlag, String str2, long j, long j2, SortCriterion[] sortCriterionArr) throws ContentDirectoryException {
        try {
            DIDLContent dIDLContent = new DIDLContent();
            ContentNode node = ContentTree.getNode(str);
            Log.v(LOGTAG, "someone's browsing id: " + str);
            if (node == null) {
                return new BrowseResult("", 0L, 0L);
            }
            if (node.isItem()) {
                dIDLContent.addItem(node.getItem());
                Log.v(LOGTAG, "returing item: " + node.getItem().getTitle());
                return new BrowseResult(new DIDLParser().generate(dIDLContent), 1L, 1L);
            }
            if (browseFlag == BrowseFlag.METADATA) {
                dIDLContent.addContainer(node.getContainer());
                Log.v(LOGTAG, "returning metadata of container: " + node.getContainer().getTitle());
                return new BrowseResult(new DIDLParser().generate(dIDLContent), 1L, 1L);
            }
            for (Container container : node.getContainer().getContainers()) {
                dIDLContent.addContainer(container);
                Log.v(LOGTAG, "getting child container: " + container.getTitle());
            }
            for (Item item : node.getContainer().getItems()) {
                dIDLContent.addItem(item);
                Log.v(LOGTAG, "getting child item: " + item.getTitle());
            }
            return new BrowseResult(new DIDLParser().generate(dIDLContent), node.getContainer().getChildCount().intValue(), node.getContainer().getChildCount().intValue());
        } catch (Exception e) {
            throw new ContentDirectoryException(ContentDirectoryErrorCode.CANNOT_PROCESS, e.toString());
        }
    }

    @Override // org.teleal.cling.support.contentdirectory.AbstractContentDirectoryService
    public BrowseResult search(String str, String str2, String str3, long j, long j2, SortCriterion[] sortCriterionArr) throws ContentDirectoryException {
        return super.search(str, str2, str3, j, j2, sortCriterionArr);
    }
}

