/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec.statemachine;

import org.apache.mina.filter.codec.statemachine.ConsumeToDynamicTerminatorDecodingState;

public abstract class ConsumeToLinearWhitespaceDecodingState
extends ConsumeToDynamicTerminatorDecodingState {
    @Override
    protected boolean isTerminator(byte b) {
        return b == 32 || b == 9;
    }
}

