package net.xtrafrancyz.VimeNetwork.api.event;

import net.xtrafrancyz.Core.CoreByteMap;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerLoadedEvent extends PlayerEvent {
   private static final HandlerList HANDLERS = new HandlerList();
   private NetworkPlayer player;
   private CoreByteMap switchData;

   public PlayerLoadedEvent(NetworkPlayer player, CoreByteMap switchData) {
      super(player.getBukkitPlayer());
      this.player = player;
      this.switchData = switchData;
   }

   public NetworkPlayer getNetworkPlayer() {
      return this.player;
   }

   public CoreByteMap getSwitchData() {
      return this.switchData;
   }

   public HandlerList getHandlers() {
      return HANDLERS;
   }

   public static HandlerList getHandlerList() {
      return HANDLERS;
   }
}
