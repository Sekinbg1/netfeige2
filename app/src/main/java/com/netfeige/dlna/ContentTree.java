package com.netfeige.dlna;

import java.util.HashMap;
import org.teleal.cling.support.model.WriteStatus;
import org.teleal.cling.support.model.container.Container;

/* JADX INFO: loaded from: classes.dex */
public class ContentTree {
    public static final String AUDIO_ID = "2";
    public static final String AUDIO_PREFIX = "audio-item-";
    public static final String IMAGE_ID = "3";
    public static final String IMAGE_PREFIX = "image-item-";
    public static final String ROOT_ID = "0";
    public static final String VIDEO_ID = "1";
    public static final String VIDEO_PREFIX = "video-item-";
    private static HashMap<String, ContentNode> contentMap = new HashMap<>();
    private static ContentNode rootNode = createRootNode();

    protected static ContentNode createRootNode() {
        Container container = new Container();
        container.setId(ROOT_ID);
        container.setParentID("-1");
        container.setTitle("GNaP MediaServer root directory");
        container.setCreator("GNaP Media Server");
        container.setRestricted(true);
        container.setSearchable(true);
        container.setWriteStatus(WriteStatus.NOT_WRITABLE);
        container.setChildCount(0);
        ContentNode contentNode = new ContentNode(ROOT_ID, container);
        contentMap.put(ROOT_ID, contentNode);
        return contentNode;
    }

    public static ContentNode getRootNode() {
        return rootNode;
    }

    public static ContentNode getNode(String str) {
        if (contentMap.containsKey(str)) {
            return contentMap.get(str);
        }
        return null;
    }

    public static boolean hasNode(String str) {
        return contentMap.containsKey(str);
    }

    public static void addNode(String str, ContentNode contentNode) {
        contentMap.put(str, contentNode);
    }
}

