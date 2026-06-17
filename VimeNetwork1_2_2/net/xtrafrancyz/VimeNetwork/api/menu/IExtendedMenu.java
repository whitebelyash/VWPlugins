/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.InventoryHolder
 */
package net.xtrafrancyz.VimeNetwork.api.menu;

import net.xtrafrancyz.VimeNetwork.api.util.Invs;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public interface IExtendedMenu
extends InventoryHolder {
    default public void show(Player player) {
        Invs.forceOpen((HumanEntity)player, this.getInventory());
    }

    public void onClick(Player var1, InventoryClickEvent var2);
}

