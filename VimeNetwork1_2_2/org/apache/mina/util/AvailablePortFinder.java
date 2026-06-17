/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.util;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

public class AvailablePortFinder {
    public static final int MIN_PORT_NUMBER = 1;
    public static final int MAX_PORT_NUMBER = 49151;

    private AvailablePortFinder() {
    }

    public static Set<Integer> getAvailablePorts() {
        return AvailablePortFinder.getAvailablePorts(1, 49151);
    }

    public static int getNextAvailable() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(0);
            int port = serverSocket.getLocalPort();
            serverSocket.close();
            return port;
        }
        catch (IOException ioe) {
            throw new NoSuchElementException(ioe.getMessage());
        }
    }

    public static int getNextAvailable(int fromPort) {
        if (fromPort < 1 || fromPort > 49151) {
            throw new IllegalArgumentException("Invalid start port: " + fromPort);
        }
        for (int i = fromPort; i <= 49151; ++i) {
            if (!AvailablePortFinder.available(i)) continue;
            return i;
        }
        throw new NoSuchElementException("Could not find an available port above " + fromPort);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean available(int port) {
        if (port < 1 || port > 49151) {
            throw new IllegalArgumentException("Invalid start port: " + port);
        }
        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            boolean bl = true;
            return bl;
        }
        catch (IOException iOException) {
        }
        finally {
            if (ds != null) {
                ds.close();
            }
            if (ss != null) {
                try {
                    ss.close();
                }
                catch (IOException iOException) {}
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Set<Integer> getAvailablePorts(int fromPort, int toPort) {
        if (fromPort < 1 || toPort > 49151 || fromPort > toPort) {
            throw new IllegalArgumentException("Invalid port range: " + fromPort + " ~ " + toPort);
        }
        TreeSet<Integer> result = new TreeSet<Integer>();
        for (int i = fromPort; i <= toPort; ++i) {
            ServerSocket s = null;
            try {
                s = new ServerSocket(i);
                result.add(i);
                continue;
            }
            catch (IOException iOException) {
                continue;
            }
            finally {
                if (s != null) {
                    try {
                        s.close();
                    }
                    catch (IOException iOException) {}
                }
            }
        }
        return result;
    }
}

