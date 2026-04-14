package com.netfeige.common;

/* JADX INFO: loaded from: classes.dex */
public class Public_Def {
    private static Public_Def m_Public_Def;

    public enum FileTransMode {
        FILETRANS_ORDER,
        FILETRANS_CONTINUE,
        FILETRANS_CON_CANCEL
    }

    public enum ListViewRefreshStatus {
        RELEASE_TO_REFRESH,
        PULL_TO_REFRESH,
        REFRESHING,
        DONE,
        LOADING
    }

    public enum ShareCheckFlag {
        SHARE_QUERY,
        SHARE_DOWNLOAD,
        SHARE_DOWNLOAD_SINGLE
    }

    public enum TransStatus {
        Trans_Ready,
        Trans_Sending,
        Trans_Recving,
        Trans_Done,
        Trans_SendFailed,
        Trans_Error,
        Trans_Rename
    }

    private Public_Def() {
    }

    public static Public_Def getInstance() {
        if (m_Public_Def == null) {
            m_Public_Def = new Public_Def();
        }
        return m_Public_Def;
    }

    public enum DownloadCmd {
        DOWNLOAD_ONLY(0),
        DOWNLOAD_OPEN(1),
        DOWNLOAD_SAVEAS(2);

        private int value;

        DownloadCmd(int i) {
            this.value = 0;
            this.value = i;
        }

        public static DownloadCmd valueOf(int i) {
            if (i == 0) {
                return DOWNLOAD_ONLY;
            }
            if (i == 1) {
                return DOWNLOAD_OPEN;
            }
            if (i != 2) {
                return null;
            }
            return DOWNLOAD_SAVEAS;
        }

        public int value() {
            return this.value;
        }
    }

    public class WifiConnectFailException extends Exception {
        public WifiConnectFailException() {
        }
    }

    public class SDCardNoAvailaleSizeException extends Exception {
        public SDCardNoAvailaleSizeException() {
        }
    }
}

