package org.teleal.cling.support.contentdirectory.ui;

import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.support.contentdirectory.callback.Browse;
import org.teleal.cling.support.model.container.Container;

/* JADX INFO: loaded from: classes.dex */
public abstract class ContentTree implements ContentBrowseActionCallbackCreator {
    protected final Container rootContainer;
    protected final TreeNode rootNode;
    protected final TreeModel treeModel;

    public abstract void failure(String str);

    public ContentTree(ControlPoint controlPoint, Service service) {
        this.rootContainer = createRootContainer(service);
        this.rootNode = new TreeNode(this.rootContainer) {
            @Override
            public boolean isLeaf() {
                return false;
            }
        };
        this.treeModel = new TreeModel(this.rootNode);
        controlPoint.execute(createContentBrowseActionCallback(service, treeModel, getRootNode()));
    }

    public Container getRootContainer() {
        return this.rootContainer;
    }

    public TreeNode getRootNode() {
        return this.rootNode;
    }

    public TreeModel getTreeModel() {
        return treeModel;
    }

    protected Container createRootContainer(Service service) {
        Container container = new Container();
        container.setId(com.netfeige.dlna.ContentTree.ROOT_ID);
        container.setTitle("Content Directory on " + service.getDevice().getDisplayString());
        return container;
    }

    protected ContentTreeExpandListener createContainerTreeExpandListener(ControlPoint controlPoint, Service service, TreeModel treeModel) {
        return new ContentTreeExpandListener(controlPoint, service, treeModel, this);
    }

    @Override // org.teleal.cling.support.contentdirectory.ui.ContentBrowseActionCallbackCreator
    public ActionCallback createContentBrowseActionCallback(Service service, TreeModel treeModel, TreeNode treeNode) {
        return new ContentBrowseActionCallback(service, treeModel, treeNode) {
            @Override // org.teleal.cling.support.contentdirectory.ui.ContentBrowseActionCallback
            public void updateStatusUI(Browse.Status status, TreeNode treeNode, TreeModel treeModel) {
                ContentTree.this.updateStatus(status, treeNode, treeModel);
            }

            @Override // org.teleal.cling.support.contentdirectory.ui.ContentBrowseActionCallback
            public void failureUI(String str) {
                ContentTree.this.failure(str);
            }
        };
    }

    /* JADX INFO: renamed from: org.teleal.cling.support.contentdirectory.ui.ContentTree$3, reason: invalid class name */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$org$teleal$cling$support$contentdirectory$callback$Browse$Status;

        static {
            int[] iArr = new int[Browse.Status.values().length];
            $SwitchMap$org$teleal$cling$support$contentdirectory$callback$Browse$Status = iArr;
            try {
                iArr[Browse.Status.LOADING.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$org$teleal$cling$support$contentdirectory$callback$Browse$Status[Browse.Status.NO_CONTENT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    public void updateStatus(Browse.Status status, TreeNode treeNode, TreeModel treeModel) {
        int i = AnonymousClass3.$SwitchMap$org$teleal$cling$support$contentdirectory$callback$Browse$Status[status.ordinal()];
        if (i == 1 || i == 2) {
            treeNode.removeAllChildren();
            TreeNode messageNode = new TreeNode(status.getDefaultMessage());
            treeModel.insertNodeInto(messageNode, treeNode, treeNode.getChildCount() <= 0 ? 0 : treeNode.getChildCount());
            treeModel.nodeStructureChanged(treeNode);
        }
    }
}

