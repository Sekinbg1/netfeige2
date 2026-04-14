package org.teleal.common.mock.http;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletInputStream;

/* JADX INFO: loaded from: classes.dex */
public class DelegatingServletInputStream extends ServletInputStream {
    private final InputStream sourceStream;

    public DelegatingServletInputStream(InputStream inputStream) {
        this.sourceStream = inputStream;
    }

    public final InputStream getSourceStream() {
        return this.sourceStream;
    }

    public int read() throws IOException {
        return this.sourceStream.read();
    }

    public void close() throws IOException {
        super.close();
        this.sourceStream.close();
    }
}

