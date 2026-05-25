package net.xtrafrancyz.VimeNetwork.api.event;

import net.xtrafrancyz.Core.network.packet.Packet52CustomMessage;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CoreCustomMessageEvent extends Event implements Cancellable {
   private static final HandlerList HANDLERS = new HandlerList();
   private boolean cancelled = false;
   private Packet52CustomMessage packet;

   public CoreCustomMessageEvent(Packet52CustomMessage packet) {
      super(true);
      this.packet = packet;
   }

   public Packet52CustomMessage getPacket() {
      return this.packet;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean flag) {
      this.cancelled = flag;
   }

   public HandlerList getHandlers() {
      return HANDLERS;
   }

   public static HandlerList getHandlerList() {
      return HANDLERS;
   }
}
