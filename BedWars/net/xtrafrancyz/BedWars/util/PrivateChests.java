package net.xtrafrancyz.BedWars.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.xtrafrancyz.BedWars.BWTeam;
import net.xtrafrancyz.BedWars.PlayerInfo;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3i;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class PrivateChests {
   private final Map privateChests = new HashMap();
   private final Map teamEnderChests = new HashMap();

   public boolean canOpen(PlayerInfo player, Location block) {
      BWTeam team = (BWTeam)this.privateChests.get(new Vec3i(block));
      return team == null || team.equals(player.team);
   }

   public void addProtection(Location block, BWTeam team) {
      this.privateChests.put(new Vec3i(block), team);
   }

   public void removeProtection(Location block) {
      this.privateChests.remove(new Vec3i(block));
   }

   public void removeProtection(BWTeam team) {
      Iterator<Map.Entry<Vec3i, BWTeam>> it = this.privateChests.entrySet().iterator();

      while(it.hasNext()) {
         if (((BWTeam)((Map.Entry)it.next()).getValue()).equals(team)) {
            it.remove();
         }
      }

   }

   public void openTeamEnderChest(PlayerInfo player) {
      Inventory inv = (Inventory)this.teamEnderChests.get(player.team);
      if (inv == null) {
         inv = Bukkit.createInventory(new FakeInventoryHolder(), 27, "Командный сундук");
         this.teamEnderChests.put(player.team, inv);
      }

      player.player.openInventory(inv);
   }

   private static class FakeInventoryHolder implements InventoryHolder {
      private FakeInventoryHolder() {
      }

      public Inventory getInventory() {
         return null;
      }
   }
}
