package net.xtrafrancyz.BedWars;

import net.xtrafrancyz.VimeNetwork.api.Material2;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class TotalDisabler implements Listener {
   @EventHandler
   public void onCraft(CraftItemEvent event) {
      if (event.getWhoClicked() instanceof Player) {
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onEntityDeath(EntityDeathEvent event) {
      event.getDrops().clear();
   }

   @EventHandler
   public void noWeaponBreakDamage(EntityDamageByEntityEvent event) {
      if (event.getDamager() instanceof Player) {
         ItemStack item = ((Player)event.getDamager()).getItemInHand();
         switch (item.getType()) {
            case IRON_SWORD:
            case DIAMOND_SWORD:
            case GOLD_SWORD:
            case STONE_SWORD:
            case WOOD_SWORD:
               item.setDurability((short)0);
         }
      } else if (event.getEntity() instanceof Player) {
         ItemStack[] armor = ((Player)event.getEntity()).getInventory().getArmorContents();

         for(ItemStack i : armor) {
            i.setDurability((short)0);
         }

         ((Player)event.getEntity()).getInventory().setArmorContents(armor);
      }

   }

   @EventHandler
   public void noWeaponBreakDamage(EntityShootBowEvent event) {
      if (event.getEntity() instanceof Player) {
         event.getBow().setDurability((short)0);
      }

   }

   @EventHandler
   public void onCreatureSpawn(CreatureSpawnEvent event) {
      if (event.getSpawnReason() != SpawnReason.CUSTOM) {
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onBlockGrow(BlockGrowEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onBlockBurn(BlockBurnEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onBlockFade(BlockFadeEvent event) {
      if (Material2.isSolid(event.getBlock().getType())) {
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onBlockForm(BlockFormEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onBlockIgnite(BlockIgniteEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onBlockSpread(BlockSpreadEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onLeavesDecay(LeavesDecayEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onBlockFromTo(BlockFromToEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onItemConsume(PlayerItemConsumeEvent event) {
      if (event.getItem().getTypeId() == 373) {
         Bukkit.getScheduler().runTaskLaterAsynchronously(BedWars.instance(), () -> event.getPlayer().setItemInHand(new ItemStack(Material.AIR)), 1L);
      }

   }

   @EventHandler
   public void onItemSpawn(ItemSpawnEvent event) {
      ItemStack is = event.getEntity().getItemStack();
      switch (is.getType()) {
         case BED:
         case OBSIDIAN:
            event.setCancelled(true);
         default:
            if (Config.type == Config.Type.HARD && is.hasItemMeta() && "Начальный предмет".equals(Items.getLore(is, -1))) {
               event.setCancelled(true);
            }

      }
   }
}
