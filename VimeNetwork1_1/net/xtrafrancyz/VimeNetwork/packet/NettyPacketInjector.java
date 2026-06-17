package net.xtrafrancyz.VimeNetwork.packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import net.minecraft.server.v1_6_R3.INetworkManager;
import net.minecraft.server.v1_6_R3.Packet;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerUnloadEvent;
import net.xtrafrancyz.VimeNetwork.api.util.Reflect;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class NettyPacketInjector extends PacketInjector {
   public NettyPacketInjector() {
      for(Player player : Bukkit.getOnlinePlayers()) {
         this.hook(player);
      }

   }

   private void hook(Player player) {
      INetworkManager networkManager = ((CraftPlayer)player).getHandle().playerConnection.networkManager;
      List<?> queue = (List)Reflect.get((Object)networkManager, "highPriorityQueue");
      if (!(queue instanceof ListWrapper)) {
         Reflect.set((Object)networkManager, "highPriorityQueue", new ListWrapper(this, player, queue));
         Channel channel = (Channel)Reflect.get((Object)networkManager, "channel");
         channel.pipeline().addAfter("decoder", "vimehook", new NettyHook(this, player));
      }

   }

   private void unhook(Player player) {
      INetworkManager networkManager = ((CraftPlayer)player).getHandle().playerConnection.networkManager;
      List queue = (List)Reflect.get((Object)networkManager, "highPriorityQueue");
      if (queue instanceof ListWrapper) {
         Reflect.set((Object)networkManager, "highPriorityQueue", ((ListWrapper)queue).getHandle());
         Channel channel = (Channel)Reflect.get((Object)networkManager, "channel");
         if (channel.pipeline().names().size() > 1) {
            channel.pipeline().remove("vimehook");
         }
      }

   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   private void onPlayerJoin(PlayerJoinEvent event) {
      this.hook(event.getPlayer());
   }

   @EventHandler
   private void onPlayerUnload(PlayerUnloadEvent event) {
      this.unhook(event.getPlayer());
   }

   public static class NettyHook extends SimpleChannelInboundHandler {
      private final NettyPacketInjector injector;
      private final Player player;

      public NettyHook(NettyPacketInjector injector, Player player) {
         this.injector = injector;
         this.player = player;
      }

      public boolean acceptInboundMessage(Object msg) throws Exception {
         return this.injector.handleIngoingPacket((Packet)msg, this.player);
      }

      protected void channelRead0(ChannelHandlerContext context, Packet packet) throws Exception {
      }
   }

   static class ListWrapper implements List {
      private final List handle;
      private final Player owner;
      private final NettyPacketInjector injector;

      public ListWrapper(NettyPacketInjector injector, Player owner, List handle) {
         this.handle = handle;
         this.owner = owner;
         this.injector = injector;
      }

      public boolean add(Object packet) {
         this.injector.handleOutgoingPacket((Packet)packet, this.owner);
         return this.handle.add(packet);
      }

      public void add(int index, Object packet) {
         this.injector.handleOutgoingPacket((Packet)packet, this.owner);
         this.handle.add(index, packet);
      }

      public boolean addAll(Collection packetPile) {
         for(Object packet : packetPile) {
            this.injector.handleOutgoingPacket((Packet)packet, this.owner);
         }

         return this.handle.addAll(packetPile);
      }

      public boolean addAll(int index, Collection packetPile) {
         for(Object packet : packetPile) {
            this.injector.handleOutgoingPacket((Packet)packet, this.owner);
         }

         return this.handle.addAll(index, packetPile);
      }

      public void clear() {
         this.handle.clear();
      }

      public boolean contains(Object o) {
         return this.handle.contains(o);
      }

      public boolean containsAll(Collection c) {
         return this.handle.containsAll(c);
      }

      public Object get(int index) {
         return this.handle.get(index);
      }

      public List getHandle() {
         return this.handle;
      }

      public int indexOf(Object o) {
         return this.handle.indexOf(o);
      }

      public boolean isEmpty() {
         return this.handle.isEmpty();
      }

      public Iterator iterator() {
         return this.handle.iterator();
      }

      public int lastIndexOf(Object o) {
         return this.handle.lastIndexOf(o);
      }

      public ListIterator listIterator() {
         return this.handle.listIterator();
      }

      public ListIterator listIterator(int index) {
         return this.handle.listIterator(index);
      }

      public Object remove(int index) {
         return this.handle.remove(index);
      }

      public boolean remove(Object o) {
         return this.handle.remove(o);
      }

      public boolean removeAll(Collection c) {
         return this.handle.removeAll(c);
      }

      public boolean retainAll(Collection c) {
         return this.handle.retainAll(c);
      }

      public Object set(int index, Object packet) {
         this.injector.handleOutgoingPacket((Packet)packet, this.owner);
         return this.handle.set(index, packet);
      }

      public int size() {
         return this.handle.size();
      }

      public List subList(int fromIndex, int toIndex) {
         return this.handle.subList(fromIndex, toIndex);
      }

      public Object[] toArray() {
         return this.handle.toArray();
      }

      public Object[] toArray(Object[] a) {
         return this.handle.toArray(a);
      }
   }
}
