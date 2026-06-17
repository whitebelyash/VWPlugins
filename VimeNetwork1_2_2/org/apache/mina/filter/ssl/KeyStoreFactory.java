/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.ssl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

public class KeyStoreFactory {
    private String type = "JKS";
    private String provider = null;
    private char[] password = null;
    private byte[] data = null;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public KeyStore newInstance() throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException, IOException {
        if (this.data == null) {
            throw new IllegalStateException("data property is not set.");
        }
        KeyStore ks = this.provider == null ? KeyStore.getInstance(this.type) : KeyStore.getInstance(this.type, this.provider);
        ByteArrayInputStream is = new ByteArrayInputStream(this.data);
        try {
            ks.load(is, this.password);
        }
        finally {
            try {
                ((InputStream)is).close();
            }
            catch (IOException iOException) {}
        }
        return ks;
    }

    public void setType(String type) {
        if (type == null) {
            throw new IllegalArgumentException("type");
        }
        this.type = type;
    }

    public void setPassword(String password) {
        this.password = (char[])(password != null ? password.toCharArray() : null);
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setData(byte[] data) {
        byte[] copy = new byte[data.length];
        System.arraycopy(data, 0, copy, 0, data.length);
        this.data = copy;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void setData(InputStream dataStream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            int data;
            while ((data = dataStream.read()) >= 0) {
                out.write(data);
            }
            this.setData(out.toByteArray());
        }
        finally {
            try {
                dataStream.close();
            }
            catch (IOException iOException) {}
        }
    }

    public void setDataFile(File dataFile) throws IOException {
        this.setData(new BufferedInputStream(new FileInputStream(dataFile)));
    }

    public void setDataUrl(URL dataUrl) throws IOException {
        this.setData(dataUrl.openStream());
    }
}

