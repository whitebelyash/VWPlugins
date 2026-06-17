/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.filterchain;

import org.apache.mina.core.filterchain.IoFilterChain;

public interface IoFilterChainBuilder {
    public static final IoFilterChainBuilder NOOP = new IoFilterChainBuilder(){

        @Override
        public void buildFilterChain(IoFilterChain chain) throws Exception {
        }

        public String toString() {
            return "NOOP";
        }
    };

    public void buildFilterChain(IoFilterChain var1) throws Exception;
}

