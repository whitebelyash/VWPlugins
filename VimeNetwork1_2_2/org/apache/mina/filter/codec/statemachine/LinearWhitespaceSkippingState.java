/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec.statemachine;

import org.apache.mina.filter.codec.statemachine.SkippingState;

public abstract class LinearWhitespaceSkippingState
extends SkippingState {
    @Override
    protected boolean canSkip(byte b) {
        return b == 32 || b == 9;
    }
}

