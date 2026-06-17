/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.executor;

import org.apache.mina.core.session.IoEvent;

public interface IoEventSizeEstimator {
    public int estimateSize(IoEvent var1);
}

