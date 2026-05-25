package net.xtrafrancyz.VimeNetwork.api.util;

import net.xtrafrancyz.VimeNetwork.api.event.ServiceItemClickedEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class E {
   private E() {
   }

   public static boolean isRightClick(PlayerInteractEvent event) {
      return event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK;
   }

   public static boolean isRightClick(ServiceItemClickedEvent event) {
      return event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK;
   }

   public static boolean isLeftClick(PlayerInteractEvent event) {
      return event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK;
   }

   public static boolean isLeftClick(ServiceItemClickedEvent event) {
      return event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK;
   }
}
