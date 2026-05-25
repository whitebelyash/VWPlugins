package net.xtrafrancyz.VimeNetwork.api.event;

import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLeaveEvent extends Event {
   private static final HandlerList HANDLERS = new HandlerList();
   private final boolean isKick;
   private final NetworkPlayer player;
   private String message;

   public PlayerLeaveEvent(NetworkPlayer player, String message, boolean isKick) {
      this.player = player;
      this.isKick = isKick;
      this.message = message;
   }

   public Player getPlayer() {
      return this.player.getBukkitPlayer();
   }

   public NetworkPlayer getNetworkPlayer() {
      return this.player;
   }

   public String getLeaveMessage() {
      return this.message;
   }

   public void setLeaveMessage(String message) {
      this.message = message;
   }

   public boolean isKick() {
      return this.isKick;
   }

   public HandlerList getHandlers() {
      return HANDLERS;
   }

   public static HandlerList getHandlerList() {
      return HANDLERS;
   }
}
