package org.teleal.cling.model.message.header;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class CallbackHeader extends UpnpHeader<List<URL>> {
    public CallbackHeader() {
        setValue(new ArrayList());
    }

    public CallbackHeader(List<URL> list) {
        this();
        getValue().addAll(list);
    }

    public CallbackHeader(URL url) {
        this();
        getValue().add(url);
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public void setString(String str) throws InvalidHeaderException {
        if (str.length() == 0) {
            return;
        }
        if (!str.contains("<") || !str.contains(">")) {
            throw new InvalidHeaderException("URLs not in brackets: " + str);
        }
        String strReplaceAll = str.replaceAll("<", "");
        String[] strArrSplit = strReplaceAll.split(">");
        try {
            ArrayList arrayList = new ArrayList();
            for (String str2 : strArrSplit) {
                String strTrim = str2.trim();
                if (!strTrim.startsWith("http://")) {
                    throw new InvalidHeaderException("Can't parse non-http callback URL: " + strTrim);
                }
                arrayList.add(new URL(strTrim));
            }
            setValue(arrayList);
        } catch (MalformedURLException e) {
            throw new InvalidHeaderException("Can't parse callback URLs from '" + strReplaceAll + "': " + e);
        }
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public String getString() {
        StringBuilder sb = new StringBuilder();
        for (URL url : getValue()) {
            sb.append("<");
            sb.append(url.toString());
            sb.append(">");
        }
        return sb.toString();
    }
}

