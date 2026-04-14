package com.netfeige.display.data;

/* JADX INFO: loaded from: classes.dex */
public class User {
    private String host;
    private String ip;
    private String mac;
    private String name;
    private boolean online = true;

    public User(String str, String str2, String str3, String str4) {
        this.ip = "";
        this.host = "";
        this.mac = "";
        this.name = "";
        this.ip = str;
        this.host = str2;
        this.mac = str3;
        this.name = str4;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String str) {
        this.ip = str;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String str) {
        this.host = str;
    }

    public String getMac() {
        return this.mac;
    }

    public void setMac(String str) {
        this.mac = str;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public boolean isOnline() {
        return this.online;
    }

    public void setOnline(boolean z) {
        this.online = z;
    }
}

