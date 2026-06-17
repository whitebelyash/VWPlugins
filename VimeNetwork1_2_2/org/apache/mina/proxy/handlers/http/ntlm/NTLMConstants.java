/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.proxy.handlers.http.ntlm;

public interface NTLMConstants {
    public static final byte[] NTLM_SIGNATURE = new byte[]{78, 84, 76, 77, 83, 83, 80, 0};
    public static final byte[] DEFAULT_OS_VERSION = new byte[]{5, 1, 40, 10, 0, 0, 0, 15};
    public static final int MESSAGE_TYPE_1 = 1;
    public static final int MESSAGE_TYPE_2 = 2;
    public static final int MESSAGE_TYPE_3 = 3;
    public static final int FLAG_NEGOTIATE_UNICODE = 1;
    public static final int FLAG_NEGOTIATE_OEM = 2;
    public static final int FLAG_REQUEST_SERVER_AUTH_REALM = 4;
    public static final int FLAG_NEGOTIATE_SIGN = 16;
    public static final int FLAG_NEGOTIATE_SEAL = 32;
    public static final int FLAG_NEGOTIATE_DATAGRAM_STYLE = 64;
    public static final int FLAG_NEGOTIATE_LAN_MANAGER_KEY = 128;
    public static final int FLAG_NEGOTIATE_NTLM = 512;
    public static final int FLAG_NEGOTIATE_ANONYMOUS = 2048;
    public static final int FLAG_NEGOTIATE_DOMAIN_SUPPLIED = 4096;
    public static final int FLAG_NEGOTIATE_WORKSTATION_SUPPLIED = 8192;
    public static final int FLAG_NEGOTIATE_LOCAL_CALL = 16384;
    public static final int FLAG_NEGOTIATE_ALWAYS_SIGN = 32768;
    public static final int FLAG_TARGET_TYPE_DOMAIN = 65536;
    public static final int FLAG_TARGET_TYPE_SERVER = 131072;
    public static final int FLAG_TARGET_TYPE_SHARE = 262144;
    public static final int FLAG_NEGOTIATE_NTLM2 = 524288;
    public static final int FLAG_NEGOTIATE_TARGET_INFO = 0x800000;
    public static final int FLAG_NEGOTIATE_128_BIT_ENCRYPTION = 0x20000000;
    public static final int FLAG_NEGOTIATE_KEY_EXCHANGE = 0x40000000;
    public static final int FLAG_NEGOTIATE_56_BIT_ENCRYPTION = Integer.MIN_VALUE;
    public static final int FLAG_UNIDENTIFIED_1 = 8;
    public static final int FLAG_UNIDENTIFIED_2 = 256;
    public static final int FLAG_UNIDENTIFIED_3 = 1024;
    public static final int FLAG_UNIDENTIFIED_4 = 0x100000;
    public static final int FLAG_UNIDENTIFIED_5 = 0x200000;
    public static final int FLAG_UNIDENTIFIED_6 = 0x400000;
    public static final int FLAG_UNIDENTIFIED_7 = 0x1000000;
    public static final int FLAG_UNIDENTIFIED_8 = 0x2000000;
    public static final int FLAG_UNIDENTIFIED_9 = 0x4000000;
    public static final int FLAG_UNIDENTIFIED_10 = 0x8000000;
    public static final int FLAG_UNIDENTIFIED_11 = 0x10000000;
    public static final int DEFAULT_FLAGS = 12291;
    public static final short TARGET_INFORMATION_SUBBLOCK_TERMINATOR_TYPE = 0;
    public static final short TARGET_INFORMATION_SUBBLOCK_SERVER_TYPE = 256;
    public static final short TARGET_INFORMATION_SUBBLOCK_DOMAIN_TYPE = 512;
    public static final short TARGET_INFORMATION_SUBBLOCK_FQDNS_HOSTNAME_TYPE = 768;
    public static final short TARGET_INFORMATION_SUBBLOCK_DNS_DOMAIN_NAME_TYPE = 1024;
    public static final short TARGET_INFORMATION_SUBBLOCK_PARENT_DNS_DOMAIN_NAME_TYPE = 1280;
}

