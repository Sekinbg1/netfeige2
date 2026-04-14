package org.teleal.cling.support.contentdirectory.ui;

import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.meta.Service;

/* JADX INFO: loaded from: classes.dex */
public class ContentTreeExpandListener {
    protected final ContentBrowseActionCallbackCreator actionCreator;
    protected final ControlPoint controlPoint;
    protected final Service service;
    protected final TreeModel treeModel;

    public ContentTreeExpandListener(ControlPoint controlPoint, Service service, TreeModel treeModel, ContentBrowseActionCallbackCreator contentBrowseActionCallbackCreator) {
        this.controlPoint = controlPoint;
        this.service = service;
        this.treeModel = treeModel;
        this.actionCreator = contentBrowseActionCallbackCreator;
    }

    public void onNodeExpand(TreeNode treeNode) {
        treeNode.removeAllChildren();
        this.treeModel.nodeStructureChanged(treeNode);
        this.controlPoint.execute(this.actionCreator.createContentBrowseActionCallback(this.service, this.treeModel, treeNode));
    }
}

