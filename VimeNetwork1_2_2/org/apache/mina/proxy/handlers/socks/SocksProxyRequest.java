/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.proxy.handlers.socks;

import java.net.InetSocketAddress;
import org.apache.mina.proxy.handlers.ProxyRequest;
import org.apache.mina.proxy.handlers.socks.SocksProxyConstants;

public class SocksProxyRequest
extends ProxyRequest {
    private byte protocolVersion;
    private byte commandCode;
    private String userName;
    private String password;
    private String host;
    private int port;
    private String serviceKerberosName;

    public SocksProxyRequest(byte protocolVersion, byte commandCode, InetSocketAddress endpointAddress, String userName) {
        super(endpointAddress);
        this.protocolVersion = protocolVersion;
        this.commandCode = commandCode;
        this.userName = userName;
    }

    public SocksProxyRequest(byte commandCode, String host, int port, String userName) {
        this.protocolVersion = (byte)4;
        this.commandCode = commandCode;
        this.userName = userName;
        this.host = host;
        this.port = port;
    }

    public byte[] getIpAddress() {
        if (this.getEndpointAddress() == null) {
            return SocksProxyConstants.FAKE_IP;
        }
        return this.getEndpointAddress().getAddress().getAddress();
    }

    public byte[] getPort() {
        byte[] port = new byte[2];
        int p = this.getEndpointAddress() == null ? this.port : this.getEndpointAddress().getPort();
        port[1] = (byte)p;
        port[0] = (byte)(p >> 8);
        return port;
    }

    public byte getCommandCode() {
        return this.commandCode;
    }

    public byte getProtocolVersion() {
        return this.protocolVersion;
    }

    public String getUserName() {
        return this.userName;
    }

    public final synchronized String getHost() {
        InetSocketAddress adr;
        if (this.host == null && (adr = this.getEndpointAddress()) != null && !adr.isUnresolved()) {
            this.host = this.getEndpointAddress().getHostName();
        }
        return this.host;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServiceKerberosName() {
        return this.serviceKerberosName;
    }

    public void setServiceKerberosName(String serviceKerberosName) {
        this.serviceKerberosName = serviceKerberosName;
    }
}

