/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.SimpleChannelInboundHandler
 *  net.minecraft.server.v1_6_R3.INetworkManager
 *  net.minecraft.server.v1_6_R3.Packet
 *  org.bukkit.Bukkit
 *  org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.player.PlayerJoinEvent
 */
package net.xtrafrancyz.VimeNetwork.packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
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
import net.xtrafrancyz.VimeNetwork.packet.PacketInjector;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class NettyPacketInjector
extends PacketInjector {
    public NettyPacketInjector() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.hook(player);
        }
    }

    private void hook(Player player) {
        INetworkManager networkManager = ((CraftPlayer)player).getHandle().playerConnection.networkManager;
        List queue = (List)Reflect.get(networkManager, "highPriorityQueue");
        if (!(queue instanceof ListWrapper)) {
            Reflect.set(networkManager, "highPriorityQueue", new ListWrapper(this, player, queue));
            Channel channel = (Channel)Reflect.get(networkManager, "channel");
            channel.pipeline().addAfter("decoder", "vimehook", (ChannelHandler)new NettyHook(this, player));
        }
    }

    private void unhook(Player player) {
        INetworkManager networkManager = ((CraftPlayer)player).getHandle().playerConnection.networkManager;
        List queue = (List)Reflect.get(networkManager, "highPriorityQueue");
        if (queue instanceof ListWrapper) {
            Reflect.set(networkManager, "highPriorityQueue", ((ListWrapper)queue).getHandle());
            Channel channel = (Channel)Reflect.get(networkManager, "channel");
            if (channel.pipeline().names().size() > 1) {
                channel.pipeline().remove("vimehook");
            }
        }
    }

    @EventHandler(priority=EventPriority.LOW)
    private void onPlayerJoin(PlayerJoinEvent event) {
        this.hook(event.getPlayer());
    }

    @EventHandler
    private void onPlayerUnload(PlayerUnloadEvent event) {
        this.unhook(event.getPlayer());
    }

    static class ListWrapper<E>
    implements List<E> {
        private final List<E> handle;
        private final Player owner;
        private final NettyPacketInjector injector;

        public ListWrapper(NettyPacketInjector injector, Player owner, List<E> handle) {
            this.handle = handle;
            this.owner = owner;
            this.injector = injector;
        }

        @Override
        public boolean add(E packet) {
            this.injector.handleOutgoingPacket((Packet)packet, this.owner);
            return this.handle.add(packet);
        }

        @Override
        public void add(int index, E packet) {
            this.injector.handleOutgoingPacket((Packet)packet, this.owner);
            this.handle.add(index, packet);
        }

        @Override
        public boolean addAll(Collection<? extends E> packetPile) {
            for (E packet : packetPile) {
                this.injector.handleOutgoingPacket((Packet)packet, this.owner);
            }
            return this.handle.addAll(packetPile);
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> packetPile) {
            for (E packet : packetPile) {
                this.injector.handleOutgoingPacket((Packet)packet, this.owner);
            }
            return this.handle.addAll(index, packetPile);
        }

        @Override
        public void clear() {
            this.handle.clear();
        }

        @Override
        public boolean contains(Object o) {
            return this.handle.contains(o);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return this.handle.containsAll(c);
        }

        @Override
        public E get(int index) {
            return this.handle.get(index);
        }

        public List<E> getHandle() {
            return this.handle;
        }

        @Override
        public int indexOf(Object o) {
            return this.handle.indexOf(o);
        }

        @Override
        public boolean isEmpty() {
            return this.handle.isEmpty();
        }

        @Override
        public Iterator<E> iterator() {
            return this.handle.iterator();
        }

        @Override
        public int lastIndexOf(Object o) {
            return this.handle.lastIndexOf(o);
        }

        @Override
        public ListIterator<E> listIterator() {
            return this.handle.listIterator();
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            return this.handle.listIterator(index);
        }

        @Override
        public E remove(int index) {
            return this.handle.remove(index);
        }

        @Override
        public boolean remove(Object o) {
            return this.handle.remove(o);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return this.handle.removeAll(c);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return this.handle.retainAll(c);
        }

        @Override
        public E set(int index, E packet) {
            this.injector.handleOutgoingPacket((Packet)packet, this.owner);
            return this.handle.set(index, packet);
        }

        @Override
        public int size() {
            return this.handle.size();
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            return this.handle.subList(fromIndex, toIndex);
        }

        @Override
        public Object[] toArray() {
            return this.handle.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return this.handle.toArray(a);
        }
    }

    public static class NettyHook
    extends SimpleChannelInboundHandler<Packet> {
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
}

