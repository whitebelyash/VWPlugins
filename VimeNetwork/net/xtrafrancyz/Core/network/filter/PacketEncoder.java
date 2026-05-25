package net.xtrafrancyz.Core.network.filter;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.packet.Packet;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class PacketEncoder extends ProtocolEncoderAdapter {
   public void encode(IoSession session, Object obj, ProtocolEncoderOutput out) throws Exception {
      Packet packet = (Packet)obj;
      IoBuffer buf = IoBuffer.allocate(1024);
      buf.setAutoExpand(true);
      buf.put(Magic.BEGIN_BYTES);
      buf.putShort((short)packet.getId());
      packet.write(new Buf(buf));
      buf.flip();
      out.write(buf);
   }
}
