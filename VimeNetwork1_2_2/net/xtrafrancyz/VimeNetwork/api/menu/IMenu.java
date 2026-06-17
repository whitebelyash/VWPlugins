/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.api.menu;

import net.xtrafrancyz.VimeNetwork.api.util.Invs;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public interface IMenu
extends InventoryHolder {
    public void onClick(ItemStack var1, Player var2, int var3, ClickType var4);

    default public void show(Player player) {
        Invs.forceOpen((HumanEntity)player, this.getInventory());
    }
}

