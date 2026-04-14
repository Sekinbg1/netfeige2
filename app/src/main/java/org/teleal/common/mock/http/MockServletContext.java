package org.teleal.common.mock.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.teleal.cling.model.ServiceReference;

/* JADX INFO: loaded from: classes.dex */
public class MockServletContext implements ServletContext {
    private File webInfClassesRoot;
    private File webInfRoot;
    private File webappRoot;
    private Map<String, String> initParameters = new HashMap();
    private Map<String, Object> attributes = new HashMap();

    private void processContextParameters(URL url) {
    }

    public ServletContext getContext(String str) {
        return this;
    }

    public String getContextPath() {
        return null;
    }

    public int getMajorVersion() {
        return 2;
    }

    public String getMimeType(String str) {
        return null;
    }

    public int getMinorVersion() {
        return 4;
    }

    public String getServerInfo() {
        return null;
    }

    public String getServletContextName() {
        return "Mock";
    }

    public Enumeration getServletNames() {
        return null;
    }

    public Enumeration getServlets() {
        return null;
    }

    public void log(Exception exc, String str) {
    }

    public void log(String str) {
    }

    public void log(String str, Throwable th) {
    }

    public MockServletContext() {
        try {
            URL resource = getClass().getResource("/WEB-INF/web.xml");
            if (resource != null) {
                File parentFile = new File(resource.toURI()).getParentFile();
                this.webInfRoot = parentFile;
                if (parentFile != null) {
                    this.webInfClassesRoot = new File(this.webInfRoot.getParentFile().getPath() + "/classes");
                    this.webappRoot = this.webInfRoot.getParentFile();
                }
                processContextParameters(resource);
                return;
            }
            this.webappRoot = new File(getClass().getResource("/.").toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> getInitParameters() {
        return this.initParameters;
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public Set getResourcePaths(String str) {
        try {
            Enumeration<URL> resources = getClass().getClassLoader().getResources("WEB-INF");
            HashSet hashSet = new HashSet();
            while (resources.hasMoreElements()) {
                File parentFile = new File(resources.nextElement().getPath()).getParentFile();
                File[] fileArrListFiles = new File(parentFile.getPath() + str).listFiles();
                if (fileArrListFiles != null) {
                    addPaths(hashSet, fileArrListFiles, parentFile.getPath());
                }
            }
            return hashSet;
        } catch (IOException e) {
            throw new RuntimeException("Error finding webroot.", e);
        }
    }

    private static void addPaths(Set<String> set, File[] fileArr, String str) {
        for (File file : fileArr) {
            String strReplace = file.getPath().substring(str.length()).replace('\\', '/');
            if (file.isDirectory()) {
                set.add(strReplace + ServiceReference.DELIMITER);
            } else {
                set.add(strReplace);
            }
        }
    }

    public URL getResource(String str) throws MalformedURLException {
        File file = getFile(str, this.webappRoot);
        if (file == null) {
            file = getFile(str, this.webInfRoot);
        }
        if (file == null) {
            file = getFile(str, this.webInfClassesRoot);
        }
        if (file != null) {
            return file.toURI().toURL();
        }
        return null;
    }

    private static File getFile(String str, File file) {
        if (file == null) {
            return null;
        }
        if (str.startsWith(ServiceReference.DELIMITER)) {
            str = str.substring(1);
        }
        File file2 = new File(file, str);
        if (file2.exists()) {
            return file2;
        }
        return null;
    }

    public InputStream getResourceAsStream(String str) {
        return getClass().getResourceAsStream(str);
    }

    public RequestDispatcher getRequestDispatcher(String str) {
        throw new UnsupportedOperationException();
    }

    public RequestDispatcher getNamedDispatcher(String str) {
        throw new UnsupportedOperationException();
    }

    public Servlet getServlet(String str) throws ServletException {
        throw new UnsupportedOperationException();
    }

    public String getRealPath(String str) {
        if (this.webappRoot == null) {
            return str;
        }
        return this.webappRoot.getAbsolutePath() + str;
    }

    public String getInitParameter(String str) {
        return this.initParameters.get(str);
    }

    public Enumeration getInitParameterNames() {
        return new IteratorEnumeration(this.initParameters.keySet().iterator());
    }

    public Object getAttribute(String str) {
        return this.attributes.get(str);
    }

    public Enumeration getAttributeNames() {
        return new IteratorEnumeration(this.attributes.keySet().iterator());
    }

    public void setAttribute(String str, Object obj) {
        if (obj == null) {
            this.attributes.remove(obj);
        } else {
            this.attributes.put(str, obj);
        }
    }

    public void removeAttribute(String str) {
        this.attributes.remove(str);
    }
}

