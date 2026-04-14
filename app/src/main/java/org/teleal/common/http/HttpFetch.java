package org.teleal.common.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import org.teleal.common.io.IO;

/* JADX INFO: loaded from: classes.dex */
public class HttpFetch {

	public interface RepresentationFactory<E> {
		Representation<E> createRepresentation(URLConnection uRLConnection, InputStream inputStream) throws IOException;
	}

	public static Representation<byte[]> fetchBinary(URL url) throws Throwable {
		return fetchBinary(url, 500, 500);
	}

	public static Representation<byte[]> fetchBinary(URL url, int i, int i2) throws Throwable {
		return fetch(url, i, i2, new RepresentationFactory<byte[]>() { // from class: org.teleal.common.http.HttpFetch.1
			@Override // org.teleal.common.http.HttpFetch.RepresentationFactory
			public Representation<byte[]> createRepresentation(URLConnection uRLConnection, InputStream inputStream) throws IOException {
				return new Representation<>(uRLConnection, IO.readBytes(inputStream));
			}
		});
	}

	public static Representation<String> fetchString(URL url, int i, int i2) throws Throwable {
		return fetch(url, i, i2, new RepresentationFactory<String>() { // from class: org.teleal.common.http.HttpFetch.2
			@Override // org.teleal.common.http.HttpFetch.RepresentationFactory
			public Representation<String> createRepresentation(URLConnection uRLConnection, InputStream inputStream) throws IOException {
				return new Representation<>(uRLConnection, IO.readLines(inputStream));
			}
		});
	}

	public static <E> Representation<E> fetch(URL url, int i, int i2, RepresentationFactory<E> representationFactory) throws Throwable {
		return fetch(url, "GET", i, i2, representationFactory);
	}

	/* JADX WARN: Removed duplicated region for block: B:21:0x004a  */
	/*
		Code decompiled incorrectly, please refer to instructions dump.
		To view partially-correct code enable 'Show inconsistent code' option in preferences
	*/
	public static <E> org.teleal.common.http.Representation<E> fetch(java.net.URL r2, java.lang.String r3, int r4, int r5, org.teleal.common.http.HttpFetch.RepresentationFactory<E> r6) throws java.lang.Throwable {
		/*
			r0 = 0
			java.net.URLConnection r2 = r2.openConnection()     // Catch: java.lang.Throwable -> L23 java.io.IOException -> L25
			java.net.HttpURLConnection r2 = (java.net.HttpURLConnection) r2     // Catch: java.lang.Throwable -> L23 java.io.IOException -> L25
			r2.setRequestMethod(r3)     // Catch: java.io.IOException -> L1e java.lang.Throwable -> L23
			r2.setConnectTimeout(r4)     // Catch: java.io.IOException -> L1e java.lang.Throwable -> L23
			r2.setReadTimeout(r5)     // Catch: java.io.IOException -> L1e java.lang.Throwable -> L23
			java.io.InputStream r0 = r2.getInputStream()     // Catch: java.io.IOException -> L1e java.lang.Throwable -> L23
			org.teleal.common.http.Representation r2 = r6.createRepresentation(r2, r0)     // Catch: java.io.IOException -> L1e java.lang.Throwable -> L23
			if (r0 == 0) goto L1d
			r0.close()
		L1d:
			return r2
		L1e:
			r3 = move-exception
			r1 = r0
			r0 = r2
			r2 = r1
			goto L27
		L23:
			r2 = move-exception
			goto L48
		L25:
			r3 = move-exception
			r2 = r0
		L27:
			if (r0 == 0) goto L44
			int r3 = r0.getResponseCode()     // Catch: java.lang.Throwable -> L45
			java.io.IOException r4 = new java.io.IOException     // Catch: java.lang.Throwable -> L45
			java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L45
			r5.<init>()     // Catch: java.lang.Throwable -> L45
			java.lang.String r6 = "Fetching resource failed, returned status code: "
			r5.append(r6)     // Catch: java.lang.Throwable -> L45
			r5.append(r3)     // Catch: java.lang.Throwable -> L45
			java.lang.String r3 = r5.toString()     // Catch: java.lang.Throwable -> L45
			r4.<init>(r3)     // Catch: java.lang.Throwable -> L45
			throw r4     // Catch: java.lang.Throwable -> L45
		L44:
			throw r3     // Catch: java.lang.Throwable -> L45
		L45:
			r3 = move-exception
			r0 = r2
			r2 = r3
		L48:
			if (r0 == 0) goto L4d
			r0.close()
		L4d:
			throw r2
		*/
		throw new UnsupportedOperationException("Method not decompiled: org.teleal.common.http.HttpFetch.fetch(java.net.URL, java.lang.String, int, int, org.teleal.common.http.HttpFetch$RepresentationFactory):org.teleal.common.http.Representation");
	}

	public static void validate(URL url) throws Throwable {
		fetch(url, "HEAD", 500, 500, new RepresentationFactory() { // from class: org.teleal.common.http.HttpFetch.3
			@Override // org.teleal.common.http.HttpFetch.RepresentationFactory
			public Representation createRepresentation(URLConnection uRLConnection, InputStream inputStream) throws IOException {
				return new Representation(uRLConnection, null);
			}
		});
	}
}
