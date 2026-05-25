package net.xtrafrancyz.VimeNetwork.api;

import java.util.function.Consumer;
import net.xtrafrancyz.Core.network.connector.CoreCallback;
import net.xtrafrancyz.Core.network.packet.Packet;
import net.xtrafrancyz.Core.network.packet.ResponsePacket;

public interface CoreBukkit {
   boolean isEnabled();

   boolean isConnected();

   void addHandler(Class var1, Consumer var2);

   void removeHandler(Class var1, Consumer var2);

   void addConnectListener(Runnable var1);

   void addDisconnectListener(Runnable var1);

   void sendPacket(Packet var1);

   void sendPacket(ResponsePacket var1, CoreCallback var2, long var3);

   void sendPacket(ResponsePacket var1, CoreCallback var2, long var3, Runnable var5);
}
