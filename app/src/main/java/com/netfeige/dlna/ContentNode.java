package com.netfeige.dlna;

import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.Item;

/* JADX INFO: loaded from: classes.dex */
public class ContentNode {
    private Container container;
    private String fullPath;
    private String id;
    private boolean isItem;
    private Item item;

    public ContentNode(String str, Container container) {
        this.id = str;
        this.container = container;
        this.fullPath = null;
        this.isItem = false;
    }

    public ContentNode(String str, Item item, String str2) {
        this.id = str;
        this.item = item;
        this.fullPath = str2;
        this.isItem = true;
    }

    public String getId() {
        return this.id;
    }

    public Container getContainer() {
        return this.container;
    }

    public Item getItem() {
        return this.item;
    }

    public String getFullPath() {
        String str;
        if (!this.isItem || (str = this.fullPath) == null) {
            return null;
        }
        return str;
    }

    public boolean isItem() {
        return this.isItem;
    }
}

