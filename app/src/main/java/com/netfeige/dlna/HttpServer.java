package com.netfeige.dlna;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;
import org.teleal.cling.model.ServiceReference;

/* JADX INFO: loaded from: classes.dex */
public class HttpServer {
    public static final String HTTP_BADREQUEST = "400 Bad Request";
    public static final String HTTP_FORBIDDEN = "403 Forbidden";
    public static final String HTTP_INTERNALERROR = "500 Internal Server Error";
    public static final String HTTP_NOTFOUND = "404 Not Found";
    public static final String HTTP_NOTIMPLEMENTED = "501 Not Implemented";
    public static final String HTTP_OK = "200 OK";
    public static final String HTTP_PARTIALCONTENT = "206 Partial Content";
    public static final String HTTP_RANGE_NOT_SATISFIABLE = "416 Requested Range Not Satisfiable";
    public static final String HTTP_REDIRECT = "301 Moved Permanently";
    private static final String LICENCE = "Copyright (C) 2001,2005-2011 by Jarno Elonen <elonen@iki.fi>\nand Copyright (C) 2010 by Konstantinos Togias <info@ktogias.gr>\n\nRedistribution and use in source and binary forms, with or without\nmodification, are permitted provided that the following conditions\nare met:\n\nRedistributions of source code must retain the above copyright notice,\nthis list of conditions and the following disclaimer. Redistributions in\nbinary form must reproduce the above copyright notice, this list of\nconditions and the following disclaimer in the documentation and/or other\nmaterials provided with the distribution. The name of the author may not\nbe used to endorse or promote products derived from this software without\nspecific prior written permission. \n \nTHIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR\nIMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES\nOF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.\nIN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,\nINCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT\nNOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,\nDATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY\nTHEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT\n(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE\nOF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.";
    public static final String MIME_DEFAULT_BINARY = "application/octet-stream";
    public static final String MIME_HTML = "text/html";
    public static final String MIME_PLAINTEXT = "text/plain";
    public static final String MIME_XML = "text/xml";
    private static SimpleDateFormat gmtFrmt;
    private static Hashtable theMimeTypes = new Hashtable();
    private File myRootDir = new File(ServiceReference.DELIMITER);
    private final ServerSocket myServerSocket;
    private int myTcpPort;
    private Thread myThread;

    public Response serve(String str, String str2, Properties properties, Properties properties2, Properties properties3) {
        System.out.println(str2 + " '" + str + "' ");
        Enumeration<?> enumerationPropertyNames = properties.propertyNames();
        while (enumerationPropertyNames.hasMoreElements()) {
            String str3 = (String) enumerationPropertyNames.nextElement();
            System.out.println("  HDR: '" + str3 + "' = '" + properties.getProperty(str3) + "'");
        }
        Enumeration<?> enumerationPropertyNames2 = properties2.propertyNames();
        while (enumerationPropertyNames2.hasMoreElements()) {
            String str4 = (String) enumerationPropertyNames2.nextElement();
            System.out.println("  PRM: '" + str4 + "' = '" + properties2.getProperty(str4) + "'");
        }
        Enumeration<?> enumerationPropertyNames3 = properties3.propertyNames();
        while (enumerationPropertyNames3.hasMoreElements()) {
            String str5 = (String) enumerationPropertyNames3.nextElement();
            System.out.println("  UPLOADED: '" + str5 + "' = '" + properties3.getProperty(str5) + "'");
        }
        String strDecode = URLDecoder.decode(str.replaceFirst(ServiceReference.DELIMITER, ""));
        String fullPath = null;
        if (ContentTree.hasNode(strDecode)) {
            ContentNode node = ContentTree.getNode(strDecode);
            if (node.isItem()) {
                fullPath = node.getFullPath();
            }
        }
        if (fullPath != null) {
            str = fullPath;
        }
        return serveFile(str, properties, this.myRootDir, false);
    }

    public class Response {
        public InputStream data;
        public Properties header;
        public String mimeType;
        public String status;

        public Response() {
            this.header = new Properties();
            this.status = HttpServer.HTTP_OK;
        }

        public Response(String str, String str2, InputStream inputStream) {
            this.header = new Properties();
            this.status = str;
            this.mimeType = str2;
            this.data = inputStream;
        }

        public Response(String str, String str2, String str3) {
            this.header = new Properties();
            this.status = str;
            this.mimeType = str2;
            try {
                this.data = new ByteArrayInputStream(str3.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        public void addHeader(String str, String str2) {
            this.header.put(str, str2);
        }
    }

    public HttpServer(int i) throws IOException {
        this.myTcpPort = i;
        this.myServerSocket = new ServerSocket(this.myTcpPort);
        Thread thread = new Thread(new Runnable() { // from class: com.netfeige.dlna.HttpServer.1
            @Override // java.lang.Runnable
            public void run() {
                while (true) {
                    try {
                        HttpServer.this.new HTTPSession(HttpServer.this.myServerSocket.accept());
                    } catch (IOException unused) {
                        return;
                    }
                }
            }
        });
        this.myThread = thread;
        thread.setDaemon(true);
        this.myThread.start();
    }

    public void stop() {
        try {
            this.myServerSocket.close();
            this.myThread.join();
        } catch (IOException | InterruptedException unused) {
        }
    }

    private class HTTPSession implements Runnable {
        private Socket mySocket;

        public HTTPSession(Socket socket) {
            this.mySocket = socket;
            Thread thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }

        @Override // java.lang.Runnable
        public void run() throws InterruptedException {
            byte[] bArr;
            int i;
            long j;
            boolean z;
            int i2;
            byte[] bArr2;
            try {
                InputStream inputStream = this.mySocket.getInputStream();
                if (inputStream != null && (i = inputStream.read((bArr = new byte[8192]), 0, 8192)) > 0) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bArr, 0, i)));
                    Properties properties = new Properties();
                    Properties properties2 = new Properties();
                    Properties properties3 = new Properties();
                    Properties properties4 = new Properties();
                    decodeHeader(bufferedReader, properties, properties2, properties3);
                    String property = properties.getProperty("method");
                    String property2 = properties.getProperty("uri");
                    String property3 = properties3.getProperty("content-length");
                    if (property3 != null) {
                        try {
                            j = Integer.parseInt(property3);
                        } catch (NumberFormatException unused) {
                            j = Long.MAX_VALUE;
                        }
                    } else {
                        j = Long.MAX_VALUE;
                    }
                    int i3 = 0;
                    while (true) {
                        if (i3 >= i) {
                            z = false;
                            break;
                        }
                        if (bArr[i3] == 13) {
                            i3++;
                            if (bArr[i3] == 10) {
                                i3++;
                                if (bArr[i3] == 13) {
                                    i3++;
                                    if (bArr[i3] == 10) {
                                        z = true;
                                        break;
                                    }
                                } else {
                                    continue;
                                }
                            } else {
                                continue;
                            }
                        }
                        i3++;
                    }
                    int i4 = i3 + 1;
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    if (i4 < i) {
                        byteArrayOutputStream.write(bArr, i4, i - i4);
                    }
                    if (i4 < i) {
                        i2 = i;
                        j -= (long) ((i - i4) + 1);
                    } else {
                        i2 = i;
                        if (!z || j == Long.MAX_VALUE) {
                            j = 0;
                        }
                    }
                    int i5 = 512;
                    byte[] bArr3 = new byte[512];
                    while (i2 >= 0 && j > 0) {
                        int i6 = inputStream.read(bArr3, 0, i5);
                        byte[] bArr4 = bArr3;
                        j -= (long) i6;
                        if (i6 > 0) {
                            bArr2 = bArr4;
                            byteArrayOutputStream.write(bArr2, 0, i6);
                        } else {
                            bArr2 = bArr4;
                        }
                        bArr3 = bArr2;
                        i2 = i6;
                        i5 = 512;
                    }
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(byteArray)));
                    if (property.equalsIgnoreCase("POST")) {
                        StringTokenizer stringTokenizer = new StringTokenizer(properties3.getProperty("content-type"), "; ");
                        String str = "";
                        if ((stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "").equalsIgnoreCase("multipart/form-data")) {
                            if (!stringTokenizer.hasMoreTokens()) {
                                sendError(HttpServer.HTTP_BADREQUEST, "BAD REQUEST: Content type is multipart/form-data but boundary missing. Usage: GET /example/file.html");
                            }
                            StringTokenizer stringTokenizer2 = new StringTokenizer(stringTokenizer.nextToken(), "=");
                            if (stringTokenizer2.countTokens() != 2) {
                                sendError(HttpServer.HTTP_BADREQUEST, "BAD REQUEST: Content type is multipart/form-data but boundary syntax error. Usage: GET /example/file.html");
                            }
                            stringTokenizer2.nextToken();
                            decodeMultipartData(stringTokenizer2.nextToken(), byteArray, bufferedReader2, properties2, properties4);
                        } else {
                            char[] cArr = new char[512];
                            for (int i7 = bufferedReader2.read(cArr); i7 >= 0 && !str.endsWith("\r\n"); i7 = bufferedReader2.read(cArr)) {
                                str = str + String.valueOf(cArr, 0, i7);
                            }
                            decodeParms(str.trim(), properties2);
                        }
                    }
                    Response responseServe = HttpServer.this.serve(property2, property, properties3, properties2, properties4);
                    if (responseServe == null) {
                        sendError(HttpServer.HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: Serve() returned a null response.");
                    } else {
                        sendResponse(responseServe.status, responseServe.mimeType, responseServe.header, responseServe.data);
                    }
                    bufferedReader2.close();
                    inputStream.close();
                }
            } catch (IOException e) {
                sendError(HttpServer.HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: IOException: " + e.getMessage());
            } catch (Throwable unused2) {
            }
        }

        private void decodeHeader(BufferedReader bufferedReader, Properties properties, Properties properties2, Properties properties3) throws InterruptedException {
            String strDecodePercent;
            try {
                String line = bufferedReader.readLine();
                if (line == null) {
                    return;
                }
                StringTokenizer stringTokenizer = new StringTokenizer(line);
                if (!stringTokenizer.hasMoreTokens()) {
                    sendError(HttpServer.HTTP_BADREQUEST, "BAD REQUEST: Syntax error. Usage: GET /example/file.html");
                }
                properties.put("method", stringTokenizer.nextToken());
                if (!stringTokenizer.hasMoreTokens()) {
                    sendError(HttpServer.HTTP_BADREQUEST, "BAD REQUEST: Missing URI. Usage: GET /example/file.html");
                }
                String strDecode = URLDecoder.decode(stringTokenizer.nextToken(), "UTF-8");
                int iIndexOf = strDecode.indexOf(63);
                if (iIndexOf >= 0) {
                    decodeParms(strDecode.substring(iIndexOf + 1), properties2);
                    strDecodePercent = decodePercent(strDecode.substring(0, iIndexOf));
                } else {
                    strDecodePercent = decodePercent(strDecode);
                }
                if (stringTokenizer.hasMoreTokens()) {
                    String line2 = bufferedReader.readLine();
                    while (line2 != null && line2.trim().length() > 0) {
                        int iIndexOf2 = line2.indexOf(58);
                        if (iIndexOf2 >= 0) {
                            properties3.put(line2.substring(0, iIndexOf2).trim().toLowerCase(), line2.substring(iIndexOf2 + 1).trim());
                        }
                        line2 = bufferedReader.readLine();
                    }
                }
                properties.put("uri", strDecodePercent);
            } catch (IOException e) {
                sendError(HttpServer.HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: IOException: " + e.getMessage());
            }
        }

        private void decodeMultipartData(String str, byte[] bArr, BufferedReader bufferedReader, Properties properties, Properties properties2) throws InterruptedException {
            String line;
            Properties properties3;
            try {
                int[] boundaryPositions = getBoundaryPositions(bArr, str.getBytes());
                int i = 1;
                for (String line2 = bufferedReader.readLine(); line2 != null; line2 = line) {
                    if (line2.indexOf(str) == -1) {
                        sendError(HttpServer.HTTP_BADREQUEST, "BAD REQUEST: Content type is multipart/form-data but next chunk does not start with boundary. Usage: GET /example/file.html");
                    }
                    i++;
                    Properties properties4 = new Properties();
                    line = bufferedReader.readLine();
                    while (line != null && line.trim().length() > 0) {
                        int iIndexOf = line.indexOf(58);
                        if (iIndexOf != -1) {
                            properties4.put(line.substring(0, iIndexOf).trim().toLowerCase(), line.substring(iIndexOf + 1).trim());
                        }
                        line = bufferedReader.readLine();
                    }
                    if (line != null) {
                        String property = properties4.getProperty("content-disposition");
                        if (property == null) {
                            sendError(HttpServer.HTTP_BADREQUEST, "BAD REQUEST: Content type is multipart/form-data but no content-disposition info found. Usage: GET /example/file.html");
                        }
                        StringTokenizer stringTokenizer = new StringTokenizer(property, "; ");
                        Properties properties5 = new Properties();
                        while (stringTokenizer.hasMoreTokens()) {
                            String strNextToken = stringTokenizer.nextToken();
                            int iIndexOf2 = strNextToken.indexOf(61);
                            if (iIndexOf2 != -1) {
                                properties5.put(strNextToken.substring(0, iIndexOf2).trim().toLowerCase(), strNextToken.substring(iIndexOf2 + 1).trim());
                            }
                        }
                        String property2 = properties5.getProperty("name");
                        String strSubstring = property2.substring(1, property2.length() - 1);
                        String strSubstring2 = "";
                        if (properties4.getProperty("content-type") == null) {
                            while (line != null && line.indexOf(str) == -1) {
                                line = bufferedReader.readLine();
                                if (line != null) {
                                    int iIndexOf3 = line.indexOf(str);
                                    strSubstring2 = iIndexOf3 == -1 ? strSubstring2 + line : strSubstring2 + line.substring(0, iIndexOf3 - 2);
                                }
                            }
                            properties3 = properties;
                        } else {
                            if (i > boundaryPositions.length) {
                                sendError(HttpServer.HTTP_INTERNALERROR, "Error processing request");
                            }
                            properties2.put(strSubstring, saveTmpFile(bArr, stripMultipartHeaders(bArr, boundaryPositions[i - 2]), (boundaryPositions[i - 1] - r5) - 4));
                            String property3 = properties5.getProperty("filename");
                            strSubstring2 = property3.substring(1, property3.length() - 1);
                            do {
                                line = bufferedReader.readLine();
                                if (line == null) {
                                    break;
                                }
                            } while (line.indexOf(str) == -1);
                            properties3 = properties;
                        }
                        properties3.put(strSubstring, strSubstring2);
                    }
                }
            } catch (IOException e) {
                sendError(HttpServer.HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: IOException: " + e.getMessage());
            }
        }

        public int[] getBoundaryPositions(byte[] bArr, byte[] bArr2) {
            Vector vector = new Vector();
            int i = 0;
            int i2 = 0;
            int i3 = -1;
            while (i < bArr.length) {
                if (bArr[i] == bArr2[i2]) {
                    if (i2 == 0) {
                        i3 = i;
                    }
                    i2++;
                    if (i2 == bArr2.length) {
                        vector.addElement(new Integer(i3));
                    } else {
                        i++;
                    }
                } else {
                    i -= i2;
                }
                i2 = 0;
                i3 = -1;
                i++;
            }
            int size = vector.size();
            int[] iArr = new int[size];
            for (int i4 = 0; i4 < size; i4++) {
                iArr[i4] = ((Integer) vector.elementAt(i4)).intValue();
            }
            return iArr;
        }

        private String saveTmpFile(byte[] bArr, int i, int i2) {
            if (i2 <= 0) {
                return "";
            }
            try {
                File fileCreateTempFile = File.createTempFile("NanoHTTPD", "", new File(System.getProperty("java.io.tmpdir")));
                FileOutputStream fileOutputStream = new FileOutputStream(fileCreateTempFile);
                fileOutputStream.write(bArr, i, i2);
                fileOutputStream.close();
                return fileCreateTempFile.getAbsolutePath();
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return "";
            }
        }

        private int stripMultipartHeaders(byte[] bArr, int i) {
            while (i < bArr.length) {
                if (bArr[i] == 13) {
                    i++;
                    if (bArr[i] == 10) {
                        i++;
                        if (bArr[i] == 13) {
                            i++;
                            if (bArr[i] == 10) {
                                break;
                            }
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }
                }
                i++;
            }
            return i + 1;
        }

        private String decodePercent(String str) throws InterruptedException {
            try {
                StringBuffer stringBuffer = new StringBuffer();
                int i = 0;
                while (i < str.length()) {
                    char cCharAt = str.charAt(i);
                    if (cCharAt == '%') {
                        stringBuffer.append((char) Integer.parseInt(str.substring(i + 1, i + 3), 16));
                        i += 2;
                    } else if (cCharAt == '+') {
                        stringBuffer.append(' ');
                    } else {
                        stringBuffer.append(cCharAt);
                    }
                    i++;
                }
                return stringBuffer.toString();
            } catch (Exception unused) {
                sendError(HttpServer.HTTP_BADREQUEST, "BAD REQUEST: Bad percent-encoding.");
                return null;
            }
        }

        private void decodeParms(String str, Properties properties) throws InterruptedException {
            if (str == null) {
                return;
            }
            StringTokenizer stringTokenizer = new StringTokenizer(str, "&");
            while (stringTokenizer.hasMoreTokens()) {
                String strNextToken = stringTokenizer.nextToken();
                int iIndexOf = strNextToken.indexOf(61);
                if (iIndexOf >= 0) {
                    properties.put(decodePercent(strNextToken.substring(0, iIndexOf)).trim(), decodePercent(strNextToken.substring(iIndexOf + 1)));
                }
            }
        }

        private void sendError(String str, String str2) throws InterruptedException {
            sendResponse(str, HttpServer.MIME_PLAINTEXT, null, new ByteArrayInputStream(str2.getBytes()));
            throw new InterruptedException();
        }

        private void sendResponse(String str, String str2, Properties properties, InputStream inputStream) {
            try {
                try {
                    if (str == null) {
                        throw new Error("sendResponse(): Status can't be null.");
                    }
                    OutputStream outputStream = this.mySocket.getOutputStream();
                    PrintWriter printWriter = new PrintWriter(outputStream);
                    printWriter.print("HTTP/1.0 " + str + " \r\n");
                    if (str2 != null) {
                        printWriter.print("Content-Type: " + str2 + "\r\n");
                    }
                    if (properties == null || properties.getProperty("Date") == null) {
                        printWriter.print("Date: " + HttpServer.gmtFrmt.format(new Date()) + "\r\n");
                    }
                    if (properties != null) {
                        Enumeration enumerationKeys = properties.keys();
                        while (enumerationKeys.hasMoreElements()) {
                            String str3 = (String) enumerationKeys.nextElement();
                            printWriter.print(str3 + ": " + properties.getProperty(str3) + "\r\n");
                        }
                    }
                    printWriter.print("\r\n");
                    printWriter.flush();
                    if (inputStream != null) {
                        int iAvailable = inputStream.available();
                        byte[] bArr = new byte[2048];
                        while (iAvailable > 0) {
                            int i = inputStream.read(bArr, 0, iAvailable > 2048 ? 2048 : iAvailable);
                            if (i <= 0) {
                                break;
                            }
                            outputStream.write(bArr, 0, i);
                            iAvailable -= i;
                        }
                    }
                    outputStream.flush();
                    outputStream.close();
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Throwable unused) {
                }
            } catch (IOException unused2) {
                this.mySocket.close();
            }
        }
    }

    private String encodeUri(String str) {
        StringTokenizer stringTokenizer = new StringTokenizer(str, "/ ", true);
        String str2 = "";
        while (stringTokenizer.hasMoreTokens()) {
            String strNextToken = stringTokenizer.nextToken();
            if (strNextToken.equals(ServiceReference.DELIMITER)) {
                str2 = str2 + ServiceReference.DELIMITER;
            } else if (strNextToken.equals(" ")) {
                str2 = str2 + "%20";
            } else {
                str2 = str2 + URLEncoder.encode(strNextToken);
            }
        }
        return str2;
    }

    /* JADX WARN: Removed duplicated region for block: B:111:0x03cf A[Catch: IOException -> 0x03f2, TRY_LEAVE, TryCatch #0 {IOException -> 0x03f2, blocks: (B:110:0x0392, B:111:0x03cf), top: B:122:0x0346 }] */
    /* JADX WARN: Removed duplicated region for block: B:72:0x02d7 A[PHI: r2 r9
  0x02d7: PHI (r2v5 com.netfeige.dlna.HttpServer$Response) = 
  (r2v4 com.netfeige.dlna.HttpServer$Response)
  (r2v4 com.netfeige.dlna.HttpServer$Response)
  (r2v16 com.netfeige.dlna.HttpServer$Response)
  (r2v16 com.netfeige.dlna.HttpServer$Response)
  (r2v16 com.netfeige.dlna.HttpServer$Response)
 binds: [B:25:0x0073, B:27:0x0079, B:31:0x00bc, B:37:0x00f0, B:34:0x00cb] A[DONT_GENERATE, DONT_INLINE]
  0x02d7: PHI (r9v1 java.io.File) = (r9v0 java.io.File), (r9v0 java.io.File), (r9v0 java.io.File), (r9v4 java.io.File), (r9v5 java.io.File) binds: [B:25:0x0073, B:27:0x0079, B:31:0x00bc, B:37:0x00f0, B:34:0x00cb] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public com.netfeige.dlna.HttpServer.Response serveFile(java.lang.String r28, java.util.Properties r29, java.io.File r30, boolean r31) {
        /*
            Method dump skipped, instruction units count: 1031
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.netfeige.dlna.HttpServer.serveFile(java.lang.String, java.util.Properties, java.io.File, boolean):com.netfeige.dlna.HttpServer$Response");
    }

    static {
        StringTokenizer stringTokenizer = new StringTokenizer("css\t\ttext/css js\t\t\ttext/javascript htm\t\ttext/html html\t\ttext/html txt\t\ttext/plain asc\t\ttext/plain gif\t\timage/gif jpg\t\timage/jpeg jpeg\t\timage/jpeg png\t\timage/png mp3\t\taudio/mpeg m3u\t\taudio/mpeg-url pdf\t\tapplication/pdf doc\t\tapplication/msword ogg\t\tapplication/x-ogg zip\t\tapplication/octet-stream exe\t\tapplication/octet-stream class\t\tapplication/octet-stream ");
        while (stringTokenizer.hasMoreTokens()) {
            theMimeTypes.put(stringTokenizer.nextToken(), stringTokenizer.nextToken());
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        gmtFrmt = simpleDateFormat;
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
}

