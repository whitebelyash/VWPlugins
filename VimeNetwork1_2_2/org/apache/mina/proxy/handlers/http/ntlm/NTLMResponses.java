/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.proxy.handlers.http.ntlm;

import java.security.Key;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class NTLMResponses {
    public static final byte[] LM_HASH_MAGIC_CONSTANT = new byte[]{75, 71, 83, 33, 64, 35, 36, 37};

    public static byte[] getLMResponse(String password, byte[] challenge) throws Exception {
        byte[] lmHash = NTLMResponses.lmHash(password);
        return NTLMResponses.lmResponse(lmHash, challenge);
    }

    public static byte[] getNTLMResponse(String password, byte[] challenge) throws Exception {
        byte[] ntlmHash = NTLMResponses.ntlmHash(password);
        return NTLMResponses.lmResponse(ntlmHash, challenge);
    }

    public static byte[] getNTLMv2Response(String target, String user, String password, byte[] targetInformation, byte[] challenge, byte[] clientNonce) throws Exception {
        return NTLMResponses.getNTLMv2Response(target, user, password, targetInformation, challenge, clientNonce, System.currentTimeMillis());
    }

    public static byte[] getNTLMv2Response(String target, String user, String password, byte[] targetInformation, byte[] challenge, byte[] clientNonce, long time) throws Exception {
        byte[] ntlmv2Hash = NTLMResponses.ntlmv2Hash(target, user, password);
        byte[] blob = NTLMResponses.createBlob(targetInformation, clientNonce, time);
        return NTLMResponses.lmv2Response(ntlmv2Hash, blob, challenge);
    }

    public static byte[] getLMv2Response(String target, String user, String password, byte[] challenge, byte[] clientNonce) throws Exception {
        byte[] ntlmv2Hash = NTLMResponses.ntlmv2Hash(target, user, password);
        return NTLMResponses.lmv2Response(ntlmv2Hash, clientNonce, challenge);
    }

    public static byte[] getNTLM2SessionResponse(String password, byte[] challenge, byte[] clientNonce) throws Exception {
        byte[] ntlmHash = NTLMResponses.ntlmHash(password);
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(challenge);
        md5.update(clientNonce);
        byte[] sessionHash = new byte[8];
        System.arraycopy(md5.digest(), 0, sessionHash, 0, 8);
        return NTLMResponses.lmResponse(ntlmHash, sessionHash);
    }

    private static byte[] lmHash(String password) throws Exception {
        byte[] oemPassword = password.toUpperCase().getBytes("US-ASCII");
        int length = Math.min(oemPassword.length, 14);
        byte[] keyBytes = new byte[14];
        System.arraycopy(oemPassword, 0, keyBytes, 0, length);
        Key lowKey = NTLMResponses.createDESKey(keyBytes, 0);
        Key highKey = NTLMResponses.createDESKey(keyBytes, 7);
        Cipher des = Cipher.getInstance("DES/ECB/NoPadding");
        des.init(1, lowKey);
        byte[] lowHash = des.doFinal(LM_HASH_MAGIC_CONSTANT);
        des.init(1, highKey);
        byte[] highHash = des.doFinal(LM_HASH_MAGIC_CONSTANT);
        byte[] lmHash = new byte[16];
        System.arraycopy(lowHash, 0, lmHash, 0, 8);
        System.arraycopy(highHash, 0, lmHash, 8, 8);
        return lmHash;
    }

    private static byte[] ntlmHash(String password) throws Exception {
        byte[] unicodePassword = password.getBytes("UnicodeLittleUnmarked");
        MessageDigest md4 = MessageDigest.getInstance("MD4");
        return md4.digest(unicodePassword);
    }

    private static byte[] ntlmv2Hash(String target, String user, String password) throws Exception {
        byte[] ntlmHash = NTLMResponses.ntlmHash(password);
        String identity = user.toUpperCase() + target;
        return NTLMResponses.hmacMD5(identity.getBytes("UnicodeLittleUnmarked"), ntlmHash);
    }

    private static byte[] lmResponse(byte[] hash, byte[] challenge) throws Exception {
        byte[] keyBytes = new byte[21];
        System.arraycopy(hash, 0, keyBytes, 0, 16);
        Key lowKey = NTLMResponses.createDESKey(keyBytes, 0);
        Key middleKey = NTLMResponses.createDESKey(keyBytes, 7);
        Key highKey = NTLMResponses.createDESKey(keyBytes, 14);
        Cipher des = Cipher.getInstance("DES/ECB/NoPadding");
        des.init(1, lowKey);
        byte[] lowResponse = des.doFinal(challenge);
        des.init(1, middleKey);
        byte[] middleResponse = des.doFinal(challenge);
        des.init(1, highKey);
        byte[] highResponse = des.doFinal(challenge);
        byte[] lmResponse = new byte[24];
        System.arraycopy(lowResponse, 0, lmResponse, 0, 8);
        System.arraycopy(middleResponse, 0, lmResponse, 8, 8);
        System.arraycopy(highResponse, 0, lmResponse, 16, 8);
        return lmResponse;
    }

    private static byte[] lmv2Response(byte[] hash, byte[] clientData, byte[] challenge) throws Exception {
        byte[] data = new byte[challenge.length + clientData.length];
        System.arraycopy(challenge, 0, data, 0, challenge.length);
        System.arraycopy(clientData, 0, data, challenge.length, clientData.length);
        byte[] mac = NTLMResponses.hmacMD5(data, hash);
        byte[] lmv2Response = new byte[mac.length + clientData.length];
        System.arraycopy(mac, 0, lmv2Response, 0, mac.length);
        System.arraycopy(clientData, 0, lmv2Response, mac.length, clientData.length);
        return lmv2Response;
    }

    private static byte[] createBlob(byte[] targetInformation, byte[] clientNonce, long time) {
        byte[] blobSignature = new byte[]{1, 1, 0, 0};
        byte[] reserved = new byte[]{0, 0, 0, 0};
        byte[] unknown1 = new byte[]{0, 0, 0, 0};
        byte[] unknown2 = new byte[]{0, 0, 0, 0};
        time += 11644473600000L;
        time *= 10000L;
        byte[] timestamp = new byte[8];
        for (int i = 0; i < 8; ++i) {
            timestamp[i] = (byte)time;
            time >>>= 8;
        }
        byte[] blob = new byte[blobSignature.length + reserved.length + timestamp.length + clientNonce.length + unknown1.length + targetInformation.length + unknown2.length];
        int offset = 0;
        System.arraycopy(blobSignature, 0, blob, offset, blobSignature.length);
        System.arraycopy(reserved, 0, blob, offset += blobSignature.length, reserved.length);
        System.arraycopy(timestamp, 0, blob, offset += reserved.length, timestamp.length);
        System.arraycopy(clientNonce, 0, blob, offset += timestamp.length, clientNonce.length);
        System.arraycopy(unknown1, 0, blob, offset += clientNonce.length, unknown1.length);
        System.arraycopy(targetInformation, 0, blob, offset += unknown1.length, targetInformation.length);
        System.arraycopy(unknown2, 0, blob, offset += targetInformation.length, unknown2.length);
        return blob;
    }

    public static byte[] hmacMD5(byte[] data, byte[] key) throws Exception {
        byte[] ipad = new byte[64];
        byte[] opad = new byte[64];
        for (int i = 0; i < 64; ++i) {
            if (i < key.length) {
                ipad[i] = (byte)(key[i] ^ 0x36);
                opad[i] = (byte)(key[i] ^ 0x5C);
                continue;
            }
            ipad[i] = 54;
            opad[i] = 92;
        }
        byte[] content = new byte[data.length + 64];
        System.arraycopy(ipad, 0, content, 0, 64);
        System.arraycopy(data, 0, content, 64, data.length);
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        data = md5.digest(content);
        content = new byte[data.length + 64];
        System.arraycopy(opad, 0, content, 0, 64);
        System.arraycopy(data, 0, content, 64, data.length);
        return md5.digest(content);
    }

    private static Key createDESKey(byte[] bytes, int offset) {
        byte[] keyBytes = new byte[7];
        System.arraycopy(bytes, offset, keyBytes, 0, 7);
        byte[] material = new byte[]{keyBytes[0], (byte)(keyBytes[0] << 7 | (keyBytes[1] & 0xFF) >>> 1), (byte)(keyBytes[1] << 6 | (keyBytes[2] & 0xFF) >>> 2), (byte)(keyBytes[2] << 5 | (keyBytes[3] & 0xFF) >>> 3), (byte)(keyBytes[3] << 4 | (keyBytes[4] & 0xFF) >>> 4), (byte)(keyBytes[4] << 3 | (keyBytes[5] & 0xFF) >>> 5), (byte)(keyBytes[5] << 2 | (keyBytes[6] & 0xFF) >>> 6), (byte)(keyBytes[6] << 1)};
        NTLMResponses.oddParity(material);
        return new SecretKeySpec(material, "DES");
    }

    private static void oddParity(byte[] bytes) {
        for (int i = 0; i < bytes.length; ++i) {
            boolean needsParity;
            byte b = bytes[i];
            boolean bl = needsParity = ((b >>> 7 ^ b >>> 6 ^ b >>> 5 ^ b >>> 4 ^ b >>> 3 ^ b >>> 2 ^ b >>> 1) & 1) == 0;
            if (needsParity) {
                int n = i;
                bytes[n] = (byte)(bytes[n] | 1);
                continue;
            }
            int n = i;
            bytes[n] = (byte)(bytes[n] & 0xFFFFFFFE);
        }
    }
}

