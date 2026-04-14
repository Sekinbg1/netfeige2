package org.teleal.cling.model.action;

import org.teleal.cling.model.types.ErrorCode;

/* JADX INFO: loaded from: classes.dex */
public class ActionException extends Exception {
	private int errorCode;

	public ActionException(int i, String str) {
		super(str);
		this.errorCode = i;
	}

	public ActionException(int i, String str, Throwable th) {
		super(str, th);
		this.errorCode = i;
	}

	public ActionException(ErrorCode errorCode) {
		this(errorCode.getCode(), errorCode.getDescription());
	}

	public ActionException(ErrorCode errorCode, String str) {
		this(errorCode, str, true);
	}

	/* JADX WARN: Illegal instructions before constructor call */
	public ActionException(ErrorCode errorCode, String str, boolean z) {
		super(z ? errorCode.getDescription() + ". " + str + "." : str);
		this.errorCode = errorCode.getCode();
	}

	public ActionException(ErrorCode errorCode, String str, Throwable th) {
		this(errorCode.getCode(), errorCode.getDescription() + ". " + str + ".", th);
	}

	public int getErrorCode() {
		return this.errorCode;
	}
}

