package org.teleal.cling.support.contentdirectory.ui;

import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.meta.Service;

/* JADX INFO: loaded from: classes.dex */
public interface ContentBrowseActionCallbackCreator {
    ActionCallback createContentBrowseActionCallback(Service service, TreeModel treeModel, TreeNode treeNode);
}

