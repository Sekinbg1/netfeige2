package org.teleal.common.http;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class Headers implements Map<String, List<String>> {
    static final byte CR = 13;
    static final byte LF = 10;
    final Map<String, List<String>> map = new HashMap(32);

    public Headers() {
    }

    public Headers(Map<String, List<String>> map) {
        putAll(map);
    }

    public Headers(ByteArrayInputStream byteArrayInputStream) {
        Headers headers = new Headers();
        String line = readLine(byteArrayInputStream);
        if (line.length() != 0) {
            String str = null;
            do {
                char cCharAt = line.charAt(0);
                if (str != null && (cCharAt == ' ' || cCharAt == '\t')) {
                    List<String> list = headers.get((Object) str);
                    int size = list.size() - 1;
                    list.set(size, list.get(size) + line.trim());
                } else {
                    String[] strArrSplitHeader = splitHeader(line);
                    headers.add(strArrSplitHeader[0], strArrSplitHeader[1]);
                    str = strArrSplitHeader[0];
                }
                line = readLine(byteArrayInputStream);
            } while (line.length() != 0);
        }
        putAll(headers);
    }

    @Override // java.util.Map
    public int size() {
        return this.map.size();
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override // java.util.Map
    public boolean containsKey(Object obj) {
        return obj != null && (obj instanceof String) && this.map.containsKey(normalize((String) obj));
    }

    @Override // java.util.Map
    public boolean containsValue(Object obj) {
        return this.map.containsValue(obj);
    }

    @Override // java.util.Map
    public List<String> get(Object obj) {
        return this.map.get(normalize((String) obj));
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.Map
    public List<String> put(String str, List<String> list) {
        return this.map.put(normalize(str), list);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.Map
    public List<String> remove(Object obj) {
        return this.map.remove(normalize((String) obj));
    }

    @Override // java.util.Map
    public void putAll(Map<? extends String, ? extends List<String>> map) {
        for (Map.Entry<? extends String, ? extends List<String>> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override // java.util.Map
    public void clear() {
        this.map.clear();
    }

    @Override // java.util.Map
    public Set<String> keySet() {
        return this.map.keySet();
    }

    @Override // java.util.Map
    public Collection<List<String>> values() {
        return this.map.values();
    }

    @Override // java.util.Map
    public Set<Map.Entry<String, List<String>>> entrySet() {
        return this.map.entrySet();
    }

    @Override // java.util.Map
    public boolean equals(Object obj) {
        return this.map.equals(obj);
    }

    @Override // java.util.Map
    public int hashCode() {
        return this.map.hashCode();
    }

    public String getFirstHeader(String str) {
        List<String> list = this.map.get(normalize(str));
        if (list == null || list.size() <= 0) {
            return null;
        }
        return list.get(0);
    }

    public void add(String str, String str2) {
        String strNormalize = normalize(str);
        List<String> linkedList = this.map.get(strNormalize);
        if (linkedList == null) {
            linkedList = new LinkedList<>();
            this.map.put(strNormalize, linkedList);
        }
        linkedList.add(str2);
    }

    public void set(String str, String str2) {
        LinkedList linkedList = new LinkedList();
        linkedList.add(str2);
        put(str, (List<String>) linkedList);
    }

    private String normalize(String str) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return str;
        }
        char[] charArray = str.toCharArray();
        if (charArray[0] >= 'a' && charArray[0] <= 'z') {
            charArray[0] = (char) (charArray[0] - ' ');
        }
        for (int i = 1; i < str.length(); i++) {
            if (charArray[i] >= 'A' && charArray[i] <= 'Z') {
                charArray[i] = (char) (charArray[i] + ' ');
            }
        }
        return new String(charArray);
    }

    public static String readLine(ByteArrayInputStream byteArrayInputStream) {
        StringBuilder sb = new StringBuilder(64);
        while (true) {
            int i = byteArrayInputStream.read();
            if (i == -1) {
                break;
            }
            char c = (char) i;
            if (c == '\r') {
                if (((char) byteArrayInputStream.read()) == '\n') {
                    break;
                }
                sb.append(c);
            } else {
                if (c == '\n') {
                    break;
                }
                sb.append(c);
            }
        }
        return sb.toString();
    }

    protected String[] splitHeader(String str) {
        char cCharAt;
        int iFindNonWhitespace = findNonWhitespace(str, 0);
        int i = iFindNonWhitespace;
        while (i < str.length() && (cCharAt = str.charAt(i)) != ':' && !Character.isWhitespace(cCharAt)) {
            i++;
        }
        int i2 = i;
        while (true) {
            if (i2 >= str.length()) {
                break;
            }
            if (str.charAt(i2) == ':') {
                i2++;
                break;
            }
            i2++;
        }
        int iFindNonWhitespace2 = findNonWhitespace(str, i2);
        int iFindEndOfString = findEndOfString(str);
        String[] strArr = new String[2];
        strArr[0] = str.substring(iFindNonWhitespace, i);
        strArr[1] = (str.length() < iFindNonWhitespace2 || str.length() < iFindEndOfString || iFindNonWhitespace2 >= iFindEndOfString) ? null : str.substring(iFindNonWhitespace2, iFindEndOfString);
        return strArr;
    }

    protected int findNonWhitespace(String str, int i) {
        while (i < str.length() && Character.isWhitespace(str.charAt(i))) {
            i++;
        }
        return i;
    }

    protected int findEndOfString(String str) {
        int length = str.length();
        while (length > 0 && Character.isWhitespace(str.charAt(length - 1))) {
            length--;
        }
        return length;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : entrySet()) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(entry.getKey());
            sb2.append(": ");
            Iterator<String> it = entry.getValue().iterator();
            while (it.hasNext()) {
                sb2.append(it.next());
                sb2.append(",");
            }
            sb2.delete(sb2.length() - 1, sb2.length());
            sb.append((CharSequence) sb2);
            sb.append("\r\n");
        }
        return sb.toString();
    }
}

