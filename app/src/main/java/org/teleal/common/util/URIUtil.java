package org.teleal.common.util;

import com.netfeige.R;
import com.netfeige.common.Public_MsgID;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.BitSet;
import org.teleal.cling.model.ServiceReference;

/* JADX INFO: loaded from: classes.dex */
public class URIUtil {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final BitSet ALPHA;
    public static final BitSet ALPHANUM;
    public static final BitSet DIGIT;
    public static final BitSet FRAGMENT;
    public static final BitSet GEN_DELIMS;
    public static final BitSet LOW_ALPHA;
    public static final BitSet PATH_PARAM_NAME;
    public static final BitSet PATH_PARAM_VALUE;
    public static final BitSet PATH_SEGMENT;
    public static final BitSet PCHAR;
    public static final BitSet QUERY;
    public static final BitSet RESERVED;
    public static final BitSet SUB_DELIMS;
    public static final BitSet UNRESERVED;
    public static final BitSet UP_ALPHA;

    public static URI createAbsoluteURI(URI uri, String str) throws IllegalArgumentException {
        return createAbsoluteURI(uri, URI.create(str));
    }

    public static URI createAbsoluteURI(URI uri, URI uri2) throws IllegalArgumentException {
        if (uri == null && !uri2.isAbsolute()) {
            throw new IllegalArgumentException("Base URI is null and given URI is not absolute");
        }
        if (uri == null && uri2.isAbsolute()) {
            return uri2;
        }
        if (uri.getPath().length() == 0) {
            try {
                uri = new URI(uri.getScheme(), uri.getAuthority(), ServiceReference.DELIMITER, uri.getQuery(), uri.getFragment());
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
        return uri.resolve(uri2);
    }

    public static URL createAbsoluteURL(URL url, String str) throws IllegalArgumentException {
        return createAbsoluteURL(url, URI.create(str));
    }

    public static URL createAbsoluteURL(URL url, URI uri) throws IllegalArgumentException {
        if (url == null && !uri.isAbsolute()) {
            throw new IllegalArgumentException("Base URL is null and given URI is not absolute");
        }
        if (url == null && uri.isAbsolute()) {
            try {
                return uri.toURL();
            } catch (Exception unused) {
                throw new IllegalArgumentException("Base URL was null and given URI can't be converted to URL");
            }
        }
        try {
            return createAbsoluteURI(url.toURI(), uri).toURL();
        } catch (Exception e) {
            throw new IllegalArgumentException("Base URL is not an URI, or can't create absolute URI (null?), or absolute URI can not be converted to URL", e);
        }
    }

    public static URL createAbsoluteURL(URI uri, URI uri2) throws IllegalArgumentException {
        try {
            return createAbsoluteURI(uri, uri2).toURL();
        } catch (Exception e) {
            throw new IllegalArgumentException("Absolute URI can not be converted to URL", e);
        }
    }

    public static URL createAbsoluteURL(InetAddress inetAddress, int i, URI uri) throws IllegalArgumentException {
        try {
            if (inetAddress instanceof Inet6Address) {
                return createAbsoluteURL(new URL("http://[" + inetAddress.getHostAddress() + "]:" + i), uri);
            }
            if (inetAddress instanceof Inet4Address) {
                return createAbsoluteURL(new URL("http://" + inetAddress.getHostAddress() + Public_MsgID.PRO_SPACE + i), uri);
            }
            throw new IllegalArgumentException("InetAddress is neither IPv4 nor IPv6: " + inetAddress);
        } catch (Exception e) {
            throw new IllegalArgumentException("Address, port, and URI can not be converted to URL", e);
        }
    }

    public static URI createRelativePathURI(URI uri) {
        assertRelativeURI("Given", uri);
        String string = uri.normalize().toString();
        while (true) {
            int iIndexOf = string.indexOf("../");
            if (iIndexOf == -1) {
                break;
            }
            string = string.substring(0, iIndexOf) + string.substring(iIndexOf + 3);
        }
        while (string.startsWith(ServiceReference.DELIMITER)) {
            string = string.substring(1);
        }
        return URI.create(string);
    }

    public static URI createRelativeURI(URI uri, URI uri2) {
        return uri.relativize(uri2);
    }

    public static URI createRelativeURI(URL url, URL url2) throws IllegalArgumentException {
        try {
            return createRelativeURI(url.toURI(), url2.toURI());
        } catch (Exception e) {
            throw new IllegalArgumentException("Can't convert base or full URL to URI", e);
        }
    }

    public static URI createRelativeURI(URI uri, URL url) throws IllegalArgumentException {
        try {
            return createRelativeURI(uri, url.toURI());
        } catch (Exception e) {
            throw new IllegalArgumentException("Can't convert full URL to URI", e);
        }
    }

    public static URI createRelativeURI(URL url, URI uri) throws IllegalArgumentException {
        try {
            return createRelativeURI(url.toURI(), uri);
        } catch (Exception e) {
            throw new IllegalArgumentException("Can't convert base URL to URI", e);
        }
    }

    public static boolean isAbsoluteURI(String str) {
        return URI.create(str).isAbsolute();
    }

    public static void assertRelativeURI(String str, URI uri) {
        if (uri.isAbsolute()) {
            throw new IllegalArgumentException(str + " URI must be relative, without scheme and authority");
        }
    }

    public static URL toURL(URI uri) {
        if (uri == null) {
            return null;
        }
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static URI toURI(URL url) {
        if (url == null) {
            return null;
        }
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static String percentEncode(String str) {
        if (str == null) {
            return "";
        }
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String percentDecode(String str) {
        if (str == null) {
            return "";
        }
        try {
            return URLDecoder.decode(str, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static {
        BitSet bitSet = new BitSet();
        GEN_DELIMS = bitSet;
        bitSet.set(58);
        GEN_DELIMS.set(47);
        GEN_DELIMS.set(63);
        GEN_DELIMS.set(35);
        GEN_DELIMS.set(91);
        GEN_DELIMS.set(93);
        GEN_DELIMS.set(64);
        BitSet bitSet2 = new BitSet();
        SUB_DELIMS = bitSet2;
        bitSet2.set(33);
        SUB_DELIMS.set(36);
        SUB_DELIMS.set(38);
        SUB_DELIMS.set(39);
        SUB_DELIMS.set(40);
        SUB_DELIMS.set(41);
        SUB_DELIMS.set(42);
        SUB_DELIMS.set(43);
        SUB_DELIMS.set(44);
        SUB_DELIMS.set(59);
        SUB_DELIMS.set(61);
        BitSet bitSet3 = new BitSet();
        RESERVED = bitSet3;
        bitSet3.or(GEN_DELIMS);
        RESERVED.or(SUB_DELIMS);
        BitSet bitSet4 = new BitSet();
        LOW_ALPHA = bitSet4;
        bitSet4.set(97);
        LOW_ALPHA.set(98);
        LOW_ALPHA.set(99);
        LOW_ALPHA.set(100);
        LOW_ALPHA.set(101);
        LOW_ALPHA.set(102);
        LOW_ALPHA.set(103);
        LOW_ALPHA.set(104);
        LOW_ALPHA.set(105);
        LOW_ALPHA.set(106);
        LOW_ALPHA.set(107);
        LOW_ALPHA.set(108);
        LOW_ALPHA.set(109);
        LOW_ALPHA.set(R.styleable.AppCompatTheme_viewInflaterClass);
        LOW_ALPHA.set(R.styleable.AppCompatTheme_windowActionBar);
        LOW_ALPHA.set(112);
        LOW_ALPHA.set(113);
        LOW_ALPHA.set(114);
        LOW_ALPHA.set(115);
        LOW_ALPHA.set(R.styleable.AppCompatTheme_windowFixedWidthMajor);
        LOW_ALPHA.set(R.styleable.AppCompatTheme_windowFixedWidthMinor);
        LOW_ALPHA.set(R.styleable.AppCompatTheme_windowMinWidthMajor);
        LOW_ALPHA.set(R.styleable.AppCompatTheme_windowMinWidthMinor);
        LOW_ALPHA.set(R.styleable.AppCompatTheme_windowNoTitle);
        LOW_ALPHA.set(121);
        LOW_ALPHA.set(122);
        BitSet bitSet5 = new BitSet();
        UP_ALPHA = bitSet5;
        bitSet5.set(65);
        UP_ALPHA.set(66);
        UP_ALPHA.set(67);
        UP_ALPHA.set(68);
        UP_ALPHA.set(69);
        UP_ALPHA.set(70);
        UP_ALPHA.set(71);
        UP_ALPHA.set(72);
        UP_ALPHA.set(73);
        UP_ALPHA.set(74);
        UP_ALPHA.set(75);
        UP_ALPHA.set(76);
        UP_ALPHA.set(77);
        UP_ALPHA.set(78);
        UP_ALPHA.set(79);
        UP_ALPHA.set(80);
        UP_ALPHA.set(81);
        UP_ALPHA.set(82);
        UP_ALPHA.set(83);
        UP_ALPHA.set(84);
        UP_ALPHA.set(85);
        UP_ALPHA.set(86);
        UP_ALPHA.set(87);
        UP_ALPHA.set(88);
        UP_ALPHA.set(89);
        UP_ALPHA.set(90);
        BitSet bitSet6 = new BitSet();
        ALPHA = bitSet6;
        bitSet6.or(LOW_ALPHA);
        ALPHA.or(UP_ALPHA);
        BitSet bitSet7 = new BitSet();
        DIGIT = bitSet7;
        bitSet7.set(48);
        DIGIT.set(49);
        DIGIT.set(50);
        DIGIT.set(51);
        DIGIT.set(52);
        DIGIT.set(53);
        DIGIT.set(54);
        DIGIT.set(55);
        DIGIT.set(56);
        DIGIT.set(57);
        BitSet bitSet8 = new BitSet();
        ALPHANUM = bitSet8;
        bitSet8.or(ALPHA);
        ALPHANUM.or(DIGIT);
        BitSet bitSet9 = new BitSet();
        UNRESERVED = bitSet9;
        bitSet9.or(ALPHA);
        UNRESERVED.or(DIGIT);
        UNRESERVED.set(45);
        UNRESERVED.set(46);
        UNRESERVED.set(95);
        UNRESERVED.set(126);
        BitSet bitSet10 = new BitSet();
        PCHAR = bitSet10;
        bitSet10.or(UNRESERVED);
        PCHAR.or(SUB_DELIMS);
        PCHAR.set(58);
        PCHAR.set(64);
        BitSet bitSet11 = new BitSet();
        PATH_SEGMENT = bitSet11;
        bitSet11.or(PCHAR);
        PATH_SEGMENT.clear(59);
        BitSet bitSet12 = new BitSet();
        PATH_PARAM_NAME = bitSet12;
        bitSet12.or(PCHAR);
        PATH_PARAM_NAME.clear(59);
        PATH_PARAM_NAME.clear(61);
        BitSet bitSet13 = new BitSet();
        PATH_PARAM_VALUE = bitSet13;
        bitSet13.or(PCHAR);
        PATH_PARAM_VALUE.clear(59);
        BitSet bitSet14 = new BitSet();
        QUERY = bitSet14;
        bitSet14.or(PCHAR);
        QUERY.set(47);
        QUERY.set(63);
        QUERY.clear(61);
        QUERY.clear(38);
        QUERY.clear(43);
        BitSet bitSet15 = new BitSet();
        FRAGMENT = bitSet15;
        bitSet15.or(PCHAR);
        FRAGMENT.set(47);
        FRAGMENT.set(63);
    }

    public static String encodePathParamName(String str) {
        try {
            return encodePart(str, "UTF-8", PATH_PARAM_NAME);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodePathParamValue(String str) {
        try {
            return encodePart(str, "UTF-8", PATH_PARAM_VALUE);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodeQueryNameOrValue(String str) {
        try {
            return encodePart(str, "UTF-8", QUERY);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodeQueryNameOrValueNoParen(String str) {
        try {
            return encodePart(str, "UTF-8", QUERY).replace("(", "").replace(")", "");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodePathSegment(String str) {
        try {
            return encodePart(str, "UTF-8", PATH_SEGMENT);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodePart(String str, String str2, BitSet bitSet) throws UnsupportedEncodingException {
        if (str == null) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer(str.length() * 3);
        for (char c : str.toCharArray()) {
            if (bitSet.get(c)) {
                stringBuffer.append(c);
            } else {
                for (byte b : String.valueOf(c).getBytes(str2)) {
                    stringBuffer.append(String.format("%%%1$02X", Integer.valueOf(b & 255)));
                }
            }
        }
        return stringBuffer.toString();
    }
}

