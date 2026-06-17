/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.util;

import org.apache.mina.util.DefaultExceptionMonitor;

public abstract class ExceptionMonitor {
    private static ExceptionMonitor instance = new DefaultExceptionMonitor();

    public static ExceptionMonitor getInstance() {
        return instance;
    }

    public static void setInstance(ExceptionMonitor monitor) {
        if (monitor == null) {
            monitor = new DefaultExceptionMonitor();
        }
        instance = monitor;
    }

    public abstract void exceptionCaught(Throwable var1);
}

