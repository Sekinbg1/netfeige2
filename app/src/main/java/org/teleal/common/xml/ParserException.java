package org.teleal.common.xml;

import com.netfeige.common.Public_MsgID;
import org.xml.sax.SAXParseException;

/* JADX INFO: loaded from: classes.dex */
public class ParserException extends Exception {
    public ParserException() {
    }

    public ParserException(String str) {
        super(str);
    }

    public ParserException(String str, Throwable th) {
        super(str, th);
    }

    public ParserException(Throwable th) {
        super(th);
    }

    public ParserException(SAXParseException sAXParseException) {
        super("(Line/Column: " + sAXParseException.getLineNumber() + Public_MsgID.PRO_SPACE + sAXParseException.getColumnNumber() + ") " + sAXParseException.getMessage());
    }
}

