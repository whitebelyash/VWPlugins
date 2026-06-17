/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.Material2
 *  net.xtrafrancyz.VimeNetwork.api.util.Items
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockBurnEvent
 *  org.bukkit.event.block.BlockFadeEvent
 *  org.bukkit.event.block.BlockFormEvent
 *  org.bukkit.event.block.BlockFromToEvent
 *  org.bukkit.event.block.BlockGrowEvent
 *  org.bukkit.event.block.BlockIgniteEvent
 *  org.bukkit.event.block.BlockSpreadEvent
 *  org.bukkit.event.block.LeavesDecayEvent
 *  org.bukkit.event.entity.CreatureSpawnEvent
 *  org.bukkit.event.entity.CreatureSpawnEvent$SpawnReason
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDeathEvent
 *  org.bukkit.event.entity.EntityShootBowEvent
 *  org.bukkit.event.entity.ItemSpawnEvent
 *  org.bukkit.event.inventory.CraftItemEvent
 *  org.bukkit.event.player.PlayerItemConsumeEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.ClashPoint;

import net.xtrafrancyz.ClashPoint.ClashPoint;
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
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class TotalDisabler
implements Listener {
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
                case WOOD_SWORD: {
                    item.setDurability((short)0);
                }
            }
        } else if (event.getEntity() instanceof Player) {
            ItemStack[] armor;
            for (ItemStack i : armor = ((Player)event.getEntity()).getInventory().getArmorContents()) {
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
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
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
        if (Material2.isSolid((Material)event.getBlock().getType())) {
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
            Bukkit.getScheduler().runTaskLaterAsynchronously((Plugin)ClashPoint.instance(), () -> event.getPlayer().setItemInHand(new ItemStack(Material.AIR)), 1L);
        }
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        ItemStack is = event.getEntity().getItemStack();
        switch (is.getType()) {
            case BED: 
            case OBSIDIAN: {
                event.setCancelled(true);
            }
        }
        if (is.hasItemMeta() && "\u041d\u0430\u0447\u0430\u043b\u044c\u043d\u044b\u0439 \u043f\u0440\u0435\u0434\u043c\u0435\u0442".equals(Items.getLore((ItemStack)is, (int)-1))) {
            event.setCancelled(true);
        }
    }
}

