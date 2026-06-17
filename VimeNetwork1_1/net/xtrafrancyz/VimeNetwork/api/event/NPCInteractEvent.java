package net.xtrafrancyz.VimeNetwork.api.event;

import net.xtrafrancyz.VimeNetwork.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NPCInteractEvent extends Event {
   private static final HandlerList HANDLERS = new HandlerList();
   private Player player;
   private NPC npc;
   private Action action;

   public NPCInteractEvent(Player player, NPC npc, Action action) {
      this.player = player;
      this.npc = npc;
      this.action = action;
   }

   public NPC getNpc() {
      return this.npc;
   }

   public Player getPlayer() {
      return this.player;
   }

   public Action getAction() {
      return this.action;
   }

   public HandlerList getHandlers() {
      return HANDLERS;
   }

   public static HandlerList getHandlerList() {
      return HANDLERS;
   }

   public static enum Action {
      RIGHT_CLICK,
      LEFT_CLICK;
   }
}
