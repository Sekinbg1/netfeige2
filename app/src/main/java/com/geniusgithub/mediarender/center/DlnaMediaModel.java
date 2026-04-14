package com.geniusgithub.mediarender.center;

/* JADX INFO: loaded from: classes.dex */
public class DlnaMediaModel {
    private String uri = "";
    private String title = "";
    private String artist = "";
    private String album = "";
    private String albumiconuri = "";
    private String objectclass = "";

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        if (str == null) {
            str = "";
        }
        this.title = str;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String str) {
        if (str == null) {
            str = "";
        }
        this.artist = str;
    }

    public void setAlbum(String str) {
        if (str == null) {
            str = "";
        }
        this.album = str;
    }

    public String getAlbum() {
        return this.album;
    }

    public void setObjectClass(String str) {
        if (str == null) {
            str = "";
        }
        this.objectclass = str;
    }

    public String getObjectClass() {
        return this.objectclass;
    }

    public void setUrl(String str) {
        if (str == null) {
            str = "";
        }
        this.uri = str;
    }

    public String getUrl() {
        return this.uri;
    }

    public String getAlbumUri() {
        return this.albumiconuri;
    }

    public void setAlbumUri(String str) {
        if (str == null) {
            str = "";
        }
        this.albumiconuri = str;
    }
}

