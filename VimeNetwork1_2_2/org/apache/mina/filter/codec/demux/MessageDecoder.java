/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec.demux;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

public interface MessageDecoder {
    public static final MessageDecoderResult OK = MessageDecoderResult.OK;
    public static final MessageDecoderResult NEED_DATA = MessageDecoderResult.NEED_DATA;
    public static final MessageDecoderResult NOT_OK = MessageDecoderResult.NOT_OK;

    public MessageDecoderResult decodable(IoSession var1, IoBuffer var2);

    public MessageDecoderResult decode(IoSession var1, IoBuffer var2, ProtocolDecoderOutput var3) throws Exception;

    public void finishDecode(IoSession var1, ProtocolDecoderOutput var2) throws Exception;
}

