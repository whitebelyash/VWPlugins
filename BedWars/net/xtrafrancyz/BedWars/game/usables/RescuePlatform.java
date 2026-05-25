package net.xtrafrancyz.BedWars.game.usables;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.xtrafrancyz.BedWars.BedWars;
import net.xtrafrancyz.BedWars.game.BTexteria;
import net.xtrafrancyz.VimeNetwork.api.util.E;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.Particles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public class RescuePlatform implements Listener {
   public static final ItemStack ITEM;
   private final BedWars plugin;
   private final Set cooldown = new HashSet();

   public RescuePlatform(BedWars plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onInteract(PlayerInteractEvent event) {
      if (E.isRightClick(event) && event.hasItem() && event.getItem().getType() == ITEM.getType()) {
         if (this.cooldown.contains(event.getPlayer().getName())) {
            BTexteria.showCustomMessage(event.getPlayer(), "Перезарядка", -44205, 2000L);
            return;
         }

         if (event.getPlayer().getLocation().add((double)0.0F, (double)-1.0F, (double)0.0F).getBlock().getType() != Material.AIR) {
            return;
         }

         PlayerInventory inv = event.getPlayer().getInventory();
         ItemStack used = inv.getItemInHand();
         used.setAmount(used.getAmount() - 1);
         inv.setItem(inv.getHeldItemSlot(), used.getAmount() == 0 ? null : used);
         event.getPlayer().setFallDistance(0.0F);
         event.getPlayer().setVelocity(new Vector(0, 0, 0));
         Platform platform = new Platform();
         Location midLoc = event.getPlayer().getLocation().add((double)0.0F, (double)-1.0F, (double)0.0F);
         if (midLoc.getY() < (double)0.0F) {
            midLoc.setY((double)2.0F);
         }

         Block mid = midLoc.getBlock();

         for(BlockFace face : BlockFace.values()) {
            if (face != BlockFace.DOWN && face != BlockFace.UP) {
               Block placed = mid.getRelative(face);
               if (placed.getType() == Material.AIR) {
                  placed.setType(Material.GLASS);
                  platform.blocks.add(placed);
               }
            }
         }

         Location loc = midLoc.add((double)0.5F, 1.3, (double)0.5F);
         Location ploc = event.getPlayer().getLocation();
         loc.setPitch(ploc.getPitch());
         loc.setYaw(ploc.getYaw());
         event.getPlayer().teleport(loc);
         String name = event.getPlayer().getName();
         this.cooldown.add(name);
         Particles.CLOUD.play(loc, 2.0F, 0.2F, 2.0F, 0.0F, 100, new Player[0]);
         Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, platform, 200L);
         Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> this.cooldown.remove(name), 400L);
         BTexteria.showCustomTimer(event.getPlayer(), "Платформа &e{S}.{mm} с.", BTexteria.DEFAULT_BAR_COLOR, 10000L, false);
      }

   }

   static {
      ITEM = Items.name(Material.BLAZE_ROD, "&bСпасительная платформа", new String[]{"&7Спаси себя от падения!", "&7В течении &f10 секунд&7 вы будете", "&7стоять на стеклянной платформе."});
   }

   private class Platform implements Runnable {
      private List blocks;

      private Platform() {
         this.blocks = new ArrayList(20);
      }

      public void run() {
         this.blocks.stream().filter((block) -> block.getType() == Material.GLASS).forEach((block) -> block.setType(Material.AIR));
         this.blocks.clear();
      }
   }
}
