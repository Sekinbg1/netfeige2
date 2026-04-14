package org.teleal.common.mock.http;

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletOutputStream;

/* JADX INFO: loaded from: classes.dex */
public class DelegatingServletOutputStream extends ServletOutputStream {
	private final OutputStream targetStream;

	public DelegatingServletOutputStream(OutputStream outputStream) {
		this.targetStream = outputStream;
	}

	public final OutputStream getTargetStream() {
		return this.targetStream;
	}

	public void write(int i) throws IOException {
		this.targetStream.write(i);
	}

	public void flush() throws IOException {
		super.flush();
		this.targetStream.flush();
	}

	public void close() throws IOException {
		super.close();
		this.targetStream.close();
	}

	@Override
	public void setWriteListener(javax.servlet.WriteListener writeListener) {
		// Stub implementation
	}

	@Override
	public boolean isReady() {
		return true;
	}
}
