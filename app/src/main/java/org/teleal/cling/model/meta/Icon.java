package org.teleal.cling.model.meta;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.teleal.cling.model.Validatable;
import org.teleal.cling.model.ValidationError;
import org.teleal.cling.model.types.BinHexDatatype;
import org.teleal.common.io.IO;
import org.teleal.common.util.ByteArray;
import org.teleal.common.util.MimeType;
import org.teleal.common.util.URIUtil;

/* JADX INFO: loaded from: classes.dex */
public class Icon implements Validatable {
    private static final Logger log = Logger.getLogger(StateVariable.class.getName());
    private final byte[] data;
    private final int depth;
    private Device device;
    private final int height;
    private final MimeType mimeType;
    private final URI uri;
    private final int width;

    public Icon(String str, int i, int i2, int i3, String str2) throws IllegalArgumentException {
        this(str, i, i2, i3, URI.create(str2), "");
    }

    public Icon(String str, int i, int i2, int i3, URI uri) {
        this(str, i, i2, i3, uri, "");
    }

    public Icon(String str, int i, int i2, int i3, URI uri, String str2) {
        this(str, i, i2, i3, uri, (str2 == null || str2.equals("")) ? null : ByteArray.toPrimitive(new BinHexDatatype().valueOf(str2)));
    }

    public Icon(String str, int i, int i2, int i3, URL url) throws IOException {
        this(str, i, i2, i3, new File(URIUtil.toURI(url)));
    }

    public Icon(String str, int i, int i2, int i3, URI uri, InputStream inputStream) throws IOException {
        this(str, i, i2, i3, uri, IO.readBytes(inputStream));
    }

    public Icon(String str, int i, int i2, int i3, File file) throws IOException {
        this(str, i, i2, i3, URI.create(file.getName()), IO.readBytes(file));
    }

    public Icon(String str, int i, int i2, int i3, URI uri, byte[] bArr) {
        this((str == null || str.length() <= 0) ? null : MimeType.valueOf(str), i, i2, i3, uri, bArr);
    }

    public Icon(MimeType mimeType, int i, int i2, int i3, URI uri, byte[] bArr) {
        this.mimeType = mimeType;
        this.width = i;
        this.height = i2;
        this.depth = i3;
        this.uri = uri;
        this.data = bArr;
    }

    public MimeType getMimeType() {
        return this.mimeType;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getDepth() {
        return this.depth;
    }

    public URI getUri() {
        return this.uri;
    }

    public byte[] getData() {
        return this.data;
    }

    public Device getDevice() {
        return this.device;
    }

    void setDevice(Device device) {
        if (this.device != null) {
            throw new IllegalStateException("Final value has been set already, model is immutable");
        }
        this.device = device;
    }

    @Override // org.teleal.cling.model.Validatable
    public List<ValidationError> validate() {
        ArrayList arrayList = new ArrayList();
        if (getMimeType() == null) {
            log.warning("UPnP specification violation of: " + getDevice());
            log.warning("Invalid icon, missing mime type: " + this);
        }
        if (getWidth() == 0) {
            log.warning("UPnP specification violation of: " + getDevice());
            log.warning("Invalid icon, missing width: " + this);
        }
        if (getHeight() == 0) {
            log.warning("UPnP specification violation of: " + getDevice());
            log.warning("Invalid icon, missing height: " + this);
        }
        if (getDepth() == 0) {
            log.warning("UPnP specification violation of: " + getDevice());
            log.warning("Invalid icon, missing bitmap depth: " + this);
        }
        if (getUri() == null) {
            arrayList.add(new ValidationError(getClass(), "uri", "URL is required"));
        }
        try {
            if (getUri().toURL() == null) {
                throw new MalformedURLException();
            }
        } catch (IllegalArgumentException unused) {
        } catch (MalformedURLException e) {
            arrayList.add(new ValidationError(getClass(), "uri", "URL must be valid: " + e.getMessage()));
        }
        return arrayList;
    }

    public Icon deepCopy() {
        return new Icon(getMimeType(), getWidth(), getHeight(), getDepth(), getUri(), getData());
    }

    public String toString() {
        return "Icon(" + getWidth() + "x" + getHeight() + ", " + getMimeType() + ") " + getUri();
    }
}

