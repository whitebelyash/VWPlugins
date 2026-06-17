/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.executor;

import java.util.EventListener;
import org.apache.mina.core.session.IoEvent;

public interface IoEventQueueHandler
extends EventListener {
    public static final IoEventQueueHandler NOOP = new IoEventQueueHandler(){

        @Override
        public boolean accept(Object source, IoEvent event) {
            return true;
        }

        @Override
        public void offered(Object source, IoEvent event) {
        }

        @Override
        public void polled(Object source, IoEvent event) {
        }
    };

    public boolean accept(Object var1, IoEvent var2);

    public void offered(Object var1, IoEvent var2);

    public void polled(Object var1, IoEvent var2);
}

