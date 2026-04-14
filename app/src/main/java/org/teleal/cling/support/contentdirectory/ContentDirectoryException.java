package org.teleal.cling.support.contentdirectory;

import org.teleal.cling.model.action.ActionException;
import org.teleal.cling.model.types.ErrorCode;

/* JADX INFO: loaded from: classes.dex */
public class ContentDirectoryException extends ActionException {
    public ContentDirectoryException(int i, String str) {
        super(i, str);
    }

    public ContentDirectoryException(int i, String str, Throwable th) {
        super(i, str, th);
    }

    public ContentDirectoryException(ErrorCode errorCode, String str) {
        super(errorCode, str);
    }

    public ContentDirectoryException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ContentDirectoryException(ContentDirectoryErrorCode contentDirectoryErrorCode, String str) {
        super(contentDirectoryErrorCode.getCode(), contentDirectoryErrorCode.getDescription() + ". " + str + ".");
    }

    public ContentDirectoryException(ContentDirectoryErrorCode contentDirectoryErrorCode) {
        super(contentDirectoryErrorCode.getCode(), contentDirectoryErrorCode.getDescription());
    }
}

