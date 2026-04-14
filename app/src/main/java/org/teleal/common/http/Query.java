package org.teleal.common.http;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/* JADX INFO: loaded from: classes.dex */
public class Query {
    private Map<String, List<String>> parameters;
    private String qs;

    public static String parseParameters(Map<String, String[]> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            for (String str : entry.getValue()) {
                if (str != null && str.length() != 0) {
                    if (sb.length() > 0) {
                        sb.append("&");
                    }
                    sb.append(entry.getKey());
                    sb.append("=");
                    sb.append(str);
                }
            }
        }
        return sb.toString();
    }

    public Query() {
        this.qs = "";
        this.parameters = new TreeMap();
    }

    public Query(Map<String, String[]> map) {
        this(parseParameters(map));
    }

    public Query(URL url) {
        this(url.getQuery());
    }

    public Query(String str) {
        String strDecode;
        this.qs = "";
        this.parameters = new TreeMap();
        if (str == null) {
            return;
        }
        this.qs = str;
        for (String str2 : str.split("&")) {
            int iIndexOf = str2.indexOf(61);
            if (iIndexOf == -1) {
                strDecode = null;
            } else {
                try {
                    String strDecode2 = URLDecoder.decode(str2.substring(0, iIndexOf), "UTF-8");
                    strDecode = URLDecoder.decode(str2.substring(iIndexOf + 1, str2.length()), "UTF-8");
                    str2 = strDecode2;
                } catch (UnsupportedEncodingException unused) {
                    throw new IllegalStateException("Query string is not UTF-8");
                }
            }
            List<String> arrayList = this.parameters.get(str2);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.parameters.put(str2, arrayList);
            }
            arrayList.add(strDecode);
        }
    }

    public String get(String str) {
        List<String> list = this.parameters.get(str);
        return (list == null || list.size() == 0) ? "" : list.get(0);
    }

    public String[] getValues(String str) {
        List<String> list = this.parameters.get(str);
        if (list == null) {
            return null;
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    public Enumeration<String> getNames() {
        return Collections.enumeration(this.parameters.keySet());
    }

    public Map<String, String[]> getMap() {
        TreeMap treeMap = new TreeMap();
        for (Map.Entry<String, List<String>> entry : this.parameters.entrySet()) {
            List<String> value = entry.getValue();
            treeMap.put(entry.getKey(), value == null ? null : (String[]) value.toArray(new String[value.size()]));
        }
        return treeMap;
    }

    public boolean isEmpty() {
        return this.parameters.size() == 0;
    }

    public String toString() {
        return this.qs;
    }
}

