package net.xtrafrancyz.VimeNetwork.listeners;

import net.xtrafrancyz.VimeNetwork.api.Def;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.event.ServiceItemClickedEvent;
import net.xtrafrancyz.VimeNetwork.api.menu.TrailMenu;
import net.xtrafrancyz.VimeNetwork.api.util.E;
import net.xtrafrancyz.VimeNetwork.impl.player.VAchievementMenu;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayerMenu;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ServiceItems implements Listener {
   @EventHandler
   public void onItemDrop(PlayerDropItemEvent event) {
      if (Def.isServiceItem(event.getItemDrop().getItemStack())) {
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onItemSpawn(ItemSpawnEvent event) {
      if (Def.isServiceItem(event.getEntity().getItemStack())) {
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onInteract(PlayerInteractEvent event) {
      if (event.hasItem() && Def.isServiceItem(event.getItem())) {
         Bukkit.getPluginManager().callEvent(new ServiceItemClickedEvent(event));
      }

   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onServiceItemClicked(ServiceItemClickedEvent event) {
      if (E.isRightClick(event)) {
         int id = event.getItem().getTypeId();
         if (id == Def.ITEM_TO_LOBBY.getTypeId()) {
            VimeNetwork.toLobby(event.getPlayer());
         } else if (id == Def.ITEM_TRAILS.getTypeId()) {
            (new TrailMenu(event.getPlayer())).show(event.getPlayer());
         } else if (id == Def.ITEM_ACHIEVEMENTS.getTypeId()) {
            (new VAchievementMenu(VPlayer.get(event.getPlayer()))).show(event.getPlayer());
         } else if (id == Def.ITEM_SETTINGS.getTypeId()) {
            (new VPlayerMenu(event.getPlayer())).show(event.getPlayer());
         }
      }

   }
}
