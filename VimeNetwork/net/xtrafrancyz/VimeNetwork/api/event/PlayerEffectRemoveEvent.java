package net.xtrafrancyz.VimeNetwork.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffectType;

public class PlayerEffectRemoveEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private final Player player;
   private PotionEffectType type;

   public PlayerEffectRemoveEvent(Player player, PotionEffectType type) {
      super(false);
      this.player = player;
      this.type = type;
   }

   public PotionEffectType getEffectType() {
      return this.type;
   }

   public Player getEntity() {
      return this.player;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
