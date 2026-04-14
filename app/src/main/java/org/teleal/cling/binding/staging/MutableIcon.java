package org.teleal.cling.binding.staging;

import java.net.URI;
import org.teleal.cling.model.meta.Icon;

/* JADX INFO: loaded from: classes.dex */
public class MutableIcon {
    public int depth;
    public int height;
    public String mimeType;
    public URI uri;
    public int width;

    public Icon build() {
        return new Icon(this.mimeType, this.width, this.height, this.depth, this.uri);
    }
}

