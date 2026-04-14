package org.teleal.cling.support.messagebox.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.teleal.cling.support.messagebox.parser.MessageElement;

/* JADX INFO: loaded from: classes.dex */
public class DateTime implements ElementAppender {
    private final String date;
    private final String time;

    public DateTime() {
        this(getCurrentDate(), getCurrentTime());
    }

    public DateTime(String str, String str2) {
        this.date = str;
        this.time = str2;
    }

    public String getDate() {
        return this.date;
    }

    public String getTime() {
        return this.time;
    }

    @Override // org.teleal.cling.support.messagebox.model.ElementAppender
    public void appendMessageElements(MessageElement messageElement) {
        messageElement.createChild("Date").setContent(getDate());
        messageElement.createChild("Time").setContent(getTime());
    }

    public static String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    public static String getCurrentTime() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }
}

