/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.filter;

import java.nio.BufferUnderflowException;
import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.filter.Magic;
import net.xtrafrancyz.Core.network.packet.Packet;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class PacketDecoder
extends CumulativeProtocolDecoder {
    Packet lastSuccessPacket = null;

    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        int start = in.position();
        try {
            short id;
            Packet.PacketData data;
            int index = 0;
            do {
                byte get;
                if ((get = in.get()) == Magic.BEGIN_BYTES[index]) {
                    ++index;
                    continue;
                }
                if (index == 0) continue;
                index = get == Magic.BEGIN_BYTES[0] ? 1 : 0;
            } while (index != Magic.BEGIN_LENGTH);
            int skipped = in.position() - start - Magic.BEGIN_LENGTH;
            if (skipped > 0) {
                System.out.println("[Core Decoder] Skipped " + skipped + " bytes. Last packet: " + this.lastSuccessPacket);
            }
            if ((data = Packet.idToPacket.get(id = in.getShort())) == null) {
                System.err.println("Wrong packet from " + session.getRemoteAddress() + ", packet id " + id);
                return true;
            }
            Packet packet = data.create();
            packet.read(new Buf(in));
            out.write(packet);
            this.lastSuccessPacket = packet;
            return true;
        }
        catch (BufferUnderflowException | ProtocolDecoderException ignored) {
            in.position(start);
            return false;
        }
    }
}

