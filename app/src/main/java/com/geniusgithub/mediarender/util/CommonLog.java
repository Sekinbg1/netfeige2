package com.geniusgithub.mediarender.util;

import android.util.Log;
import com.netfeige.common.Public_MsgID;

/* JADX INFO: loaded from: classes.dex */
public class CommonLog {
    public static boolean isDebug = true;
    public static int logLevel = 2;
    private String tag;

    public CommonLog() {
        this.tag = "CommonLog";
    }

    public CommonLog(String str) {
        this.tag = "CommonLog";
        this.tag = str;
    }

    public void setTag(String str) {
        this.tag = str;
    }

    private String getFunctionName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace == null) {
            return null;
        }
        for (StackTraceElement stackTraceElement : stackTrace) {
            if (!stackTraceElement.isNativeMethod() && !stackTraceElement.getClassName().equals(Thread.class.getName()) && !stackTraceElement.getClassName().equals(getClass().getName())) {
                return "[" + Thread.currentThread().getId() + ": " + stackTraceElement.getFileName() + Public_MsgID.PRO_SPACE + stackTraceElement.getLineNumber() + "]";
            }
        }
        return null;
    }

    public void info(Object obj) {
        String string;
        if (logLevel <= 4) {
            String functionName = getFunctionName();
            if (functionName == null) {
                string = obj.toString();
            } else {
                string = functionName + " - " + obj;
            }
            Log.i(this.tag, string);
        }
    }

    public void i(Object obj) {
        if (isDebug) {
            info(obj);
        }
    }

    public void verbose(Object obj) {
        String string;
        if (logLevel <= 2) {
            String functionName = getFunctionName();
            if (functionName == null) {
                string = obj.toString();
            } else {
                string = functionName + " - " + obj;
            }
            Log.v(this.tag, string);
        }
    }

    public void v(Object obj) {
        if (isDebug) {
            verbose(obj);
        }
    }

    public void warn(Object obj) {
        String string;
        if (logLevel <= 5) {
            String functionName = getFunctionName();
            if (functionName == null) {
                string = obj.toString();
            } else {
                string = functionName + " - " + obj;
            }
            Log.w(this.tag, string);
        }
    }

    public void w(Object obj) {
        if (isDebug) {
            warn(obj);
        }
    }

    public void error(Object obj) {
        String string;
        if (logLevel <= 6) {
            String functionName = getFunctionName();
            if (functionName == null) {
                string = obj.toString();
            } else {
                string = functionName + " - " + obj;
            }
            Log.e(this.tag, string);
        }
    }

    public void error(Exception exc) {
        if (logLevel <= 6) {
            StringBuffer stringBuffer = new StringBuffer();
            String functionName = getFunctionName();
            StackTraceElement[] stackTrace = exc.getStackTrace();
            if (functionName != null) {
                stringBuffer.append(functionName + " - " + exc + "\r\n");
            } else {
                stringBuffer.append(exc + "\r\n");
            }
            if (stackTrace != null && stackTrace.length > 0) {
                for (StackTraceElement stackTraceElement : stackTrace) {
                    if (stackTraceElement != null) {
                        stringBuffer.append("[ " + stackTraceElement.getFileName() + Public_MsgID.PRO_SPACE + stackTraceElement.getLineNumber() + " ]\r\n");
                    }
                }
            }
            Log.e(this.tag, stringBuffer.toString());
        }
    }

    public void e(Object obj) {
        if (isDebug) {
            error(obj);
        }
    }

    public void e(Exception exc) {
        if (isDebug) {
            error(exc);
        }
    }

    public void debug(Object obj) {
        String string;
        if (logLevel <= 3) {
            String functionName = getFunctionName();
            if (functionName == null) {
                string = obj.toString();
            } else {
                string = functionName + " - " + obj;
            }
            Log.d(this.tag, string);
        }
    }

    public void d(Object obj) {
        if (isDebug) {
            debug(obj);
        }
    }
}

