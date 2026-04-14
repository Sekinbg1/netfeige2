package org.teleal.common.xhtml;

import com.netfeige.common.Public_MsgID;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class Option {
    private String key;
    private String[] values;

    public Option(String str, String[] strArr) {
        this.key = str;
        this.values = strArr;
    }

    public static Option[] fromString(String str) {
        if (str == null || str.length() == 0) {
            return new Option[0];
        }
        ArrayList arrayList = new ArrayList();
        try {
            for (String str2 : str.split(";")) {
                String[] strArrSplit = str2.trim().split(Public_MsgID.PRO_SPACE);
                String strTrim = strArrSplit[0].trim();
                String[] strArrSplit2 = strArrSplit[1].split(",");
                for (int i = 0; i < strArrSplit2.length; i++) {
                    strArrSplit2[i] = strArrSplit2[i].trim();
                }
                arrayList.add(new Option(strTrim, strArrSplit2));
            }
            return (Option[]) arrayList.toArray(new Option[arrayList.size()]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Can't parse options string: " + str, e);
        }
    }

    public String getKey() {
        return this.key;
    }

    public String[] getValues() {
        return this.values;
    }

    public boolean isTrue() {
        return getValues().length == 1 && getValues()[0].toLowerCase().equals("true");
    }

    public boolean isFalse() {
        return getValues().length == 1 && getValues()[0].toLowerCase().equals("false");
    }

    public String getFirstValue() {
        return getValues()[0];
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getKey());
        sb.append(": ");
        Iterator it = Arrays.asList(getValues()).iterator();
        while (it.hasNext()) {
            sb.append((String) it.next());
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Option option = (Option) obj;
        return this.key.equals(option.key) && Arrays.equals(this.values, option.values);
    }

    public int hashCode() {
        return (this.key.hashCode() * 31) + Arrays.hashCode(this.values);
    }
}

