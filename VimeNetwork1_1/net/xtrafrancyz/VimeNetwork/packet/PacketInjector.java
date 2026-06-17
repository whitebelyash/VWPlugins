package net.xtrafrancyz.VimeNetwork.packet;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.server.v1_6_R3.Packet;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

public abstract class PacketInjector implements Listener {
   private List outgoingListeners = new LinkedList();
   private List ingoingListeners = new LinkedList();

   public void addOutgoingListener(Plugin plugin, Consumer listener) {
      this.removeOutgoingListener(listener);
      this.outgoingListeners.add(new OutgoingListenerInfo(plugin, listener));
   }

   public void addIngoingListener(Plugin plugin, Consumer listener) {
      this.removeIngoingListener(listener);
      this.ingoingListeners.add(new IngoingListenerInfo(plugin, listener));
   }

   public void removeOutgoingListener(Consumer listener) {
      Iterator<OutgoingListenerInfo> it = this.outgoingListeners.iterator();

      while(it.hasNext()) {
         if (((OutgoingListenerInfo)it.next()).listener == listener) {
            it.remove();
         }
      }

   }

   public void removeIngoingListener(Consumer listener) {
      Iterator<IngoingListenerInfo> it = this.ingoingListeners.iterator();

      while(it.hasNext()) {
         if (((IngoingListenerInfo)it.next()).listener == listener) {
            it.remove();
         }
      }

   }

   boolean handleOutgoingPacket(Packet obj, Player destination) {
      OutgoingPacketEvent event = new OutgoingPacketEvent(destination, obj);

      for(OutgoingListenerInfo listener : this.outgoingListeners) {
         try {
            listener.listener.accept(event);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      return event.isCancelled();
   }

   boolean handleIngoingPacket(Packet packet, Player sender) {
      IngoingPacketEvent event = new IngoingPacketEvent(sender, packet);

      for(IngoingListenerInfo listener : this.ingoingListeners) {
         try {
            listener.listener.accept(event);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      return event.isCancelled();
   }

   @EventHandler
   private void onPluginDisable(PluginDisableEvent event) {
      this.outgoingListeners.removeIf((info) -> info.plugin.equals(event.getPlugin()));
   }
}
