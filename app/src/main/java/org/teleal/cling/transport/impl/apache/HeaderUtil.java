package org.teleal.cling.transport.impl.apache;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.teleal.common.http.Headers;

/* JADX INFO: loaded from: classes.dex */
public class HeaderUtil {
    public static void add(HttpMessage httpMessage, Headers headers) {
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            Iterator<String> it = entry.getValue().iterator();
            while (it.hasNext()) {
                httpMessage.addHeader(entry.getKey(), it.next());
            }
        }
    }

    public static Headers get(HttpMessage httpMessage) {
        Headers headers = new Headers();
        for (Header header : httpMessage.getAllHeaders()) {
            headers.add(header.getName(), header.getValue());
        }
        return headers;
    }
}

