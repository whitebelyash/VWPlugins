/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.proxy.handlers.http.digest;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import javax.security.sasl.AuthenticationException;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.proxy.session.ProxyIoSession;
import org.apache.mina.proxy.utils.ByteUtilities;
import org.apache.mina.proxy.utils.StringUtilities;

public class DigestUtilities {
    public static final String SESSION_HA1 = DigestUtilities.class + ".SessionHA1";
    private static MessageDigest md5;
    public static final String[] SUPPORTED_QOPS;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public static String computeResponseValue(IoSession session, HashMap<String, String> map, String method, String pwd, String charsetName, String body) throws AuthenticationException, UnsupportedEncodingException {
        Object hEntity;
        MessageDigest messageDigest;
        byte[] hA1;
        StringBuilder sb;
        boolean isMD5Sess = "md5-sess".equalsIgnoreCase(StringUtilities.getDirectiveValue(map, "algorithm", false));
        if (!isMD5Sess || session.getAttribute(SESSION_HA1) == null) {
            Object prehA1;
            sb = new StringBuilder();
            sb.append(StringUtilities.stringTo8859_1(StringUtilities.getDirectiveValue(map, "username", true))).append(':');
            String realm = StringUtilities.stringTo8859_1(StringUtilities.getDirectiveValue(map, "realm", false));
            if (realm != null) {
                sb.append(realm);
            }
            sb.append(':').append(pwd);
            if (isMD5Sess) {
                MessageDigest messageDigest2 = md5;
                // MONITORENTER : messageDigest2
                md5.reset();
                prehA1 = md5.digest(sb.toString().getBytes(charsetName));
                // MONITOREXIT : messageDigest2
                sb = new StringBuilder();
                sb.append(ByteUtilities.asHex((byte[])prehA1));
                sb.append(':').append(StringUtilities.stringTo8859_1(StringUtilities.getDirectiveValue(map, "nonce", true)));
                sb.append(':').append(StringUtilities.stringTo8859_1(StringUtilities.getDirectiveValue(map, "cnonce", true)));
                messageDigest2 = md5;
                // MONITORENTER : messageDigest2
                md5.reset();
                hA1 = md5.digest(sb.toString().getBytes(charsetName));
                // MONITOREXIT : messageDigest2
                session.setAttribute(SESSION_HA1, hA1);
            } else {
                prehA1 = md5;
                // MONITORENTER : prehA1
                md5.reset();
                hA1 = md5.digest(sb.toString().getBytes(charsetName));
                // MONITOREXIT : prehA1
            }
        } else {
            hA1 = (byte[])session.getAttribute(SESSION_HA1);
        }
        sb = new StringBuilder(method);
        sb.append(':');
        sb.append(StringUtilities.getDirectiveValue(map, "uri", false));
        String qop = StringUtilities.getDirectiveValue(map, "qop", false);
        if ("auth-int".equalsIgnoreCase(qop)) {
            ProxyIoSession proxyIoSession = (ProxyIoSession)session.getAttribute(ProxyIoSession.PROXY_SESSION);
            messageDigest = md5;
            // MONITORENTER : messageDigest
            md5.reset();
            hEntity = md5.digest(body.getBytes(proxyIoSession.getCharsetName()));
            // MONITOREXIT : messageDigest
            sb.append(':').append(hEntity);
        }
        hEntity = md5;
        // MONITORENTER : hEntity
        md5.reset();
        byte[] hA2 = md5.digest(sb.toString().getBytes(charsetName));
        // MONITOREXIT : hEntity
        sb = new StringBuilder();
        sb.append(ByteUtilities.asHex(hA1));
        sb.append(':').append(StringUtilities.getDirectiveValue(map, "nonce", true));
        sb.append(":00000001:");
        sb.append(StringUtilities.getDirectiveValue(map, "cnonce", true));
        sb.append(':').append(qop).append(':');
        sb.append(ByteUtilities.asHex(hA2));
        messageDigest = md5;
        // MONITORENTER : messageDigest
        md5.reset();
        byte[] hFinal = md5.digest(sb.toString().getBytes(charsetName));
        // MONITOREXIT : messageDigest
        return ByteUtilities.asHex(hFinal);
    }

    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        SUPPORTED_QOPS = new String[]{"auth", "auth-int"};
    }
}

