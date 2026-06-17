/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.api.menu;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.util.Invs;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class ConfirmMenu
implements IMenu {
    private static final Set<Integer> CONFIRM_SLOTS = ImmutableSet.of((Object)10, (Object)11, (Object)12, (Object)19, (Object)20, (Object)21, (Object[])new Integer[]{28, 29, 30});
    private static final Set<Integer> CANCEL_SLOTS = ImmutableSet.of((Object)14, (Object)15, (Object)16, (Object)23, (Object)24, (Object)25, (Object[])new Integer[]{32, 33, 34});
    private Runnable callback;
    private Runnable cancelledCallback;
    private Inventory prev;
    private Inventory inv;
    private boolean confirmInited = false;
    private boolean cancelInited = false;
    private boolean backOnConfirm = true;

    public ConfirmMenu(Inventory prev, Runnable callback, String title) {
        this.callback = callback;
        this.prev = prev;
        this.inv = Bukkit.createInventory((InventoryHolder)this, (int)45, (String)title);
    }

    public void setConfirmText(String name, String ... lore) {
        this.confirmInited = true;
        ItemStack item = Items.name(Material.EMERALD_BLOCK, name, lore);
        CONFIRM_SLOTS.forEach(slot -> this.inv.setItem(slot.intValue(), item));
    }

    public void setCancelText(String name, String ... lore) {
        this.cancelInited = true;
        ItemStack item = Items.name(Material.REDSTONE_BLOCK, name, lore);
        CANCEL_SLOTS.forEach(slot -> this.inv.setItem(slot.intValue(), item));
    }

    public void setBackOnConfirm(boolean flag) {
        this.backOnConfirm = flag;
    }

    public void setCancelledCallback(Runnable callback) {
        this.cancelledCallback = callback;
    }

    @Override
    public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
        if (CONFIRM_SLOTS.contains(slot)) {
            this.callback.run();
            if (this.backOnConfirm) {
                if (this.prev != null) {
                    Invs.forceOpen((HumanEntity)player, this.prev);
                } else {
                    player.closeInventory();
                }
            }
        } else if (CANCEL_SLOTS.contains(slot)) {
            if (this.cancelledCallback != null) {
                this.cancelledCallback.run();
            } else if (this.prev != null) {
                Invs.forceOpen((HumanEntity)player, this.prev);
            } else {
                player.closeInventory();
            }
        }
    }

    public Inventory getInventory() {
        if (!this.confirmInited) {
            ItemStack confirm = Items.name(Material.EMERALD_BLOCK, "&aOK", new String[0]);
            CONFIRM_SLOTS.forEach(slot -> this.inv.setItem(slot.intValue(), confirm));
        }
        if (!this.cancelInited) {
            ItemStack cancel = Items.name(Material.REDSTONE_BLOCK, "&c\u041e\u0442\u043c\u0435\u043d\u0430", new String[0]);
            CANCEL_SLOTS.forEach(slot -> this.inv.setItem(slot.intValue(), cancel));
        }
        return this.inv;
    }
}

