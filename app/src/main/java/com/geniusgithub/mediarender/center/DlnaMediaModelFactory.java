package com.geniusgithub.mediarender.center;

import android.content.Intent;
import com.geniusgithub.mediarender.util.CommonLog;
import com.geniusgithub.mediarender.util.LogFactory;
import java.io.ByteArrayInputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: classes.dex */
public class DlnaMediaModelFactory {
    public static final String PARAM_GET_ALBUM = "param_metadata_album";
    public static final String PARAM_GET_ALBUMICONURI = "param_metadata_album_uri";
    public static final String PARAM_GET_ARTIST = "param_metadata_artist";
    public static final String PARAM_GET_OBJECT_CLASS = "param_metadata_object_class";
    public static final String PARAM_GET_TITLE = "param_metadata_title";
    public static final String PARAM_GET_URL = "param_metadata__url";
    private static final CommonLog log = LogFactory.createLog();

    public static void pushMediaModelToIntent(Intent intent, DlnaMediaModel dlnaMediaModel) {
        intent.putExtra(PARAM_GET_URL, dlnaMediaModel.getUrl());
        intent.putExtra(PARAM_GET_OBJECT_CLASS, dlnaMediaModel.getObjectClass());
        intent.putExtra(PARAM_GET_TITLE, dlnaMediaModel.getTitle());
        intent.putExtra(PARAM_GET_ARTIST, dlnaMediaModel.getArtist());
        intent.putExtra(PARAM_GET_ALBUM, dlnaMediaModel.getAlbum());
        intent.putExtra(PARAM_GET_ALBUMICONURI, dlnaMediaModel.getAlbumUri());
    }

    public static DlnaMediaModel createFromIntent(Intent intent) {
        DlnaMediaModel dlnaMediaModel = new DlnaMediaModel();
        dlnaMediaModel.setUrl(intent.getStringExtra(PARAM_GET_URL));
        dlnaMediaModel.setObjectClass(intent.getStringExtra(PARAM_GET_OBJECT_CLASS));
        dlnaMediaModel.setTitle(intent.getStringExtra(PARAM_GET_TITLE));
        dlnaMediaModel.setArtist(intent.getStringExtra(PARAM_GET_ARTIST));
        dlnaMediaModel.setAlbum(intent.getStringExtra(PARAM_GET_ALBUM));
        dlnaMediaModel.setAlbumUri(intent.getStringExtra(PARAM_GET_ALBUMICONURI));
        return dlnaMediaModel;
    }

    public static DlnaMediaModel createFromMetaData(String str) {
        DlnaMediaModel dlnaMediaModel = new DlnaMediaModel();
        DocumentBuilderFactory documentBuilderFactoryNewInstance = DocumentBuilderFactory.newInstance();
        if (str.contains("&") && !str.contains("&amp;")) {
            str = str.replace("&", "&amp;");
        }
        try {
            Document document = documentBuilderFactoryNewInstance.newDocumentBuilder().parse(new ByteArrayInputStream(str.getBytes("UTF-8")));
            dlnaMediaModel.setObjectClass(getElementValue(document, "upnp:class"));
            dlnaMediaModel.setTitle(getElementValue(document, "dc:title"));
            dlnaMediaModel.setAlbum(getElementValue(document, "upnp:album"));
            dlnaMediaModel.setArtist(getElementValue(document, "upnp:artist"));
            dlnaMediaModel.setAlbumUri(getElementValue(document, "upnp:albumArtURI"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dlnaMediaModel;
    }

    private static String getElementValue(Document document, String str) {
        NodeList elementsByTagName = document.getElementsByTagName(str);
        for (int i = 0; i < elementsByTagName.getLength(); i++) {
            NodeList childNodes = elementsByTagName.item(i).getChildNodes();
            if (childNodes.getLength() != 0) {
                return childNodes.item(0).getNodeValue();
            }
        }
        return "";
    }
}

