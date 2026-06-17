/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.EntityItem
 *  net.minecraft.server.v1_6_R3.World
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.craftbukkit.v1_6_R3.CraftServer
 *  org.bukkit.craftbukkit.v1_6_R3.CraftWorld
 *  org.bukkit.craftbukkit.v1_6_R3.entity.CraftItem
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.event.player.PlayerDropItemEvent
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.listeners;

import net.minecraft.server.v1_6_R3.EntityItem;
import net.minecraft.server.v1_6_R3.World;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftItem;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class CancelDropItemFix
implements Listener {
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked().getType() != EntityType.PLAYER) {
            return;
        }
        if (VimeNetwork.features().CANCEL_DROP_ITEM_FIX.isEnabled() && event.getClick() == ClickType.DROP && !event.isCancelled()) {
            ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null) {
                return;
            }
            CraftItem item = new CraftItem((CraftServer)Bukkit.getServer(), new EntityItem((World)((CraftWorld)event.getWhoClicked().getWorld()).getHandle()));
            item.setItemStack(itemStack);
            WrappedPlayerDropItemEvent evt = new WrappedPlayerDropItemEvent((Player)event.getWhoClicked(), (Item)item);
            Bukkit.getPluginManager().callEvent((Event)evt);
            if (evt.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerDrop(PlayerDropItemEvent event) {
        if (event.isCancelled() && VimeNetwork.features().CANCEL_DROP_ITEM_FIX.isEnabled() && !(event instanceof WrappedPlayerDropItemEvent)) {
            Player player = event.getPlayer();
            if (player.getOpenInventory().getTopInventory().getType() != InventoryType.CRAFTING) {
                return;
            }
            ItemStack item = event.getItemDrop().getItemStack();
            ItemStack inHand = player.getInventory().getItemInHand();
            if (inHand.getType() != Material.AIR && (inHand.getType() != item.getType() || inHand.getDurability() != item.getDurability())) {
                return;
            }
            item.setAmount(inHand.getAmount() + 1);
            event.getItemDrop().remove();
            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), item);
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getType() == InventoryType.CRAFTING && VimeNetwork.features().CANCEL_DROP_ITEM_FIX.isEnabled()) {
            ItemStack[] contents = event.getInventory().getContents();
            for (int i = 1; i < 5; ++i) {
                if (contents[i] == null || contents[i].getType() == Material.AIR) continue;
                event.getPlayer().getInventory().addItem(new ItemStack[]{contents[i].clone()});
            }
            event.getInventory().clear();
        }
    }

    public static class WrappedPlayerDropItemEvent
    extends PlayerDropItemEvent {
        public WrappedPlayerDropItemEvent(Player player, Item drop) {
            super(player, drop);
        }
    }
}

