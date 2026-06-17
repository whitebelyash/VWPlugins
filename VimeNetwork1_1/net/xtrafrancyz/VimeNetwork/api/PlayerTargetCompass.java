package net.xtrafrancyz.VimeNetwork.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.server.v1_6_R3.MathHelper;
import net.xtrafrancyz.VimeNetwork.api.event.ServiceItemClickedEvent;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class PlayerTargetCompass implements Listener {
   private Map updatable = new HashMap();
   private Function targetProvider;
   private Function playerNameProvider;

   public PlayerTargetCompass(Plugin plugin, Function targetProvider) {
      this.targetProvider = targetProvider;
      Bukkit.getPluginManager().registerEvents(this, plugin);
      Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::run, 20L, 20L);
      this.playerNameProvider = Player::getDisplayName;
   }

   public void setPlayerNameProvider(Function provider) {
      this.playerNameProvider = provider;
   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   private void onInteract(ServiceItemClickedEvent event) {
      if (event.getItem().getType() == Material.COMPASS) {
         if (event.getItem().hasItemMeta()) {
            ItemMeta im = event.getItem().getItemMeta();
            if (im.hasDisplayName() && im.getDisplayName().contains("лобби")) {
               return;
            }
         }

         event.setCancelled(true);
         UpdatableData data = (UpdatableData)this.updatable.get(event.getPlayer());
         if (data == null) {
            return;
         }

         if (data.nearest == null) {
            U.msg(event.getPlayer(), (String[])("&fВрагов больше не осталось..."));
         } else {
            int distance = (int)MathHelper.sqrt(data.distance);
            U.msg(event.getPlayer(), (String[])("&fБлижайший игрок: " + (String)this.playerNameProvider.apply(data.nearest) + "&r&f. Расстояние: &e" + distance + " &f" + U.plurals(distance, "блок", "блока", "блоков")));
         }
      }

   }

   private void run() {
      for(Map.Entry entry : this.updatable.entrySet()) {
         Player player = (Player)entry.getKey();
         UpdatableData data = (UpdatableData)entry.getValue();
         Location loc = player.getLocation();
         double min = (double)Float.MAX_VALUE;
         Player nearest = null;

         for(Player target : (Collection)this.targetProvider.apply(player)) {
            if (!target.equals(player)) {
               double distance = loc.distanceSquared(target.getLocation());
               if (distance < min) {
                  min = distance;
                  nearest = target;
               }
            }
         }

         data.nearest = nearest;
         data.distance = min;
         if (nearest != null) {
            player.setCompassTarget(nearest.getLocation());
         } else {
            player.setCompassTarget(loc);
         }
      }

   }

   public void addUpdatePlayer(Player player) {
      this.updatable.put(player, new UpdatableData());
   }

   public void removeUpdatePlayer(Player player) {
      this.updatable.remove(player);
   }

   private static class UpdatableData {
      Player nearest;
      double distance;

      private UpdatableData() {
      }
   }
}
