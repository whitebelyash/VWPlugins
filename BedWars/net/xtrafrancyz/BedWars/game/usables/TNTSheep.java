package net.xtrafrancyz.BedWars.game.usables;

import net.minecraft.server.v1_6_R3.EntityTNTPrimed;
import net.xtrafrancyz.BedWars.BedWars;
import net.xtrafrancyz.BedWars.Config;
import net.xtrafrancyz.BedWars.game.entity.TNTSheepEntity;
import net.xtrafrancyz.VimeNetwork.api.entity.NMSEntityUtils;
import net.xtrafrancyz.VimeNetwork.api.util.E;
import net.xtrafrancyz.VimeNetwork.api.util.Reflect;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftTNTPrimed;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public class TNTSheep implements Listener {
   private final BedWars plugin;

   public TNTSheep(BedWars plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onInteract(PlayerInteractEvent event) {
      if (E.isRightClick(event) && event.hasBlock() && event.hasItem() && event.getItem().getType() == Material.MONSTER_EGG && event.getItem().getDurability() == 91) {
         event.setCancelled(true);
         double min = Double.MAX_VALUE;
         Player target = null;
         Location sheepLoc = event.getClickedBlock().getLocation().add((double)0.5F, (double)1.0F, (double)0.5F);

         for(Entity en : event.getPlayer().getNearbyEntities((double)15.0F, (double)15.0F, (double)15.0F)) {
            if (en.getType() == EntityType.PLAYER) {
               double d = en.getLocation().distanceSquared(sheepLoc);
               if (d < min) {
                  min = d;
                  target = (Player)en;
               }
            }
         }

         if (target == null) {
            U.msg(event.getPlayer(), new String[]{"&cПоблизости нет ни одного вражеского игрока"});
            return;
         }

         PlayerInventory inv = event.getPlayer().getInventory();
         ItemStack used = inv.getItemInHand();
         used.setAmount(used.getAmount() - 1);
         inv.setItem(inv.getHeldItemSlot(), used.getAmount() == 0 ? null : used);
         TNTSheepEntity nmsSheep = new TNTSheepEntity(NMSEntityUtils.getNMSWorld(Config.world), event.getPlayer(), target);
         Sheep sheep = (Sheep)NMSEntityUtils.spawn(nmsSheep, sheepLoc);
         EntityTNTPrimed nmsTnt = new EntityTNTPrimed(NMSEntityUtils.getNMSWorld(Config.world));
         nmsTnt.fuseTicks = 100;
         nmsTnt.isIncendiary = false;
         TNTPrimed tnt = (TNTPrimed)NMSEntityUtils.spawn(nmsTnt, sheepLoc);
         Reflect.set(((CraftTNTPrimed)tnt).getHandle(), "source", ((CraftPlayer)event.getPlayer()).getHandle());
         nmsTnt.setPassengerOf(nmsSheep);
         Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            nmsTnt.fuseTicks = 40;
            nmsTnt.isIncendiary = true;
            nmsTnt.setPassengerOf((net.minecraft.server.v1_6_R3.Entity)null);
            Vector vec = sheep.getVelocity();
            vec.setY(0.2);
            tnt.setVelocity(vec);
            sheep.remove();
         }, 60L);
      }

   }

   @EventHandler
   public void onExplosionPrime(ExplosionPrimeEvent event) {
      if (event.getEntity() instanceof TNTPrimed) {
         event.setCancelled(true);
         Entity source = ((TNTPrimed)event.getEntity()).getSource();
         if (source instanceof Player) {
            Location loc = event.getEntity().getLocation();
            NMSEntityUtils.getNMSWorld(Config.world).createExplosion(((CraftEntity)source).getHandle(), loc.getX(), loc.getY(), loc.getZ(), event.getRadius(), false, false);
         }
      }

   }
}
