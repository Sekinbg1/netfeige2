package org.teleal.cling.support.contentdirectory.ui;

import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.Item;

/* JADX INFO: loaded from: classes.dex */
public class ContentTreeCellRenderer {

    public String getNodeText(TreeNode treeNode) {
        Object userObject = treeNode.getUserObject();
        if (userObject instanceof Container) {
            return ((Container) userObject).getTitle();
        } else if (userObject instanceof Item) {
            Item item = (Item) userObject;
            return item.getTitle();
        } else if (userObject instanceof String) {
            return (String) userObject;
        }
        return "";
    }

    public boolean isContainer(TreeNode treeNode) {
        Object userObject = treeNode.getUserObject();
        return userObject instanceof Container;
    }

    public boolean isItem(TreeNode treeNode) {
        Object userObject = treeNode.getUserObject();
        return userObject instanceof Item;
    }

    public Container getContainer(TreeNode treeNode) {
        Object userObject = treeNode.getUserObject();
        return userObject instanceof Container ? (Container) userObject : null;
    }

    public Item getItem(TreeNode treeNode) {
        Object userObject = treeNode.getUserObject();
        return userObject instanceof Item ? (Item) userObject : null;
    }
}

