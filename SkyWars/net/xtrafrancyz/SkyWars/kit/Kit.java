/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.util.Items$Safe
 *  net.xtrafrancyz.VimeNetwork.api.util.U
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.SkyWars.kit;

import net.xtrafrancyz.SkyWars.PlayerInfo;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.inventory.ItemStack;

public abstract class Kit {
    static int slotCounter = 0;
    public final int slot = slotCounter++;
    public final String id;
    public final String name;
    public int price;
    private ItemStack menuItem = null;

    protected Kit(String id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public ItemStack getMenuItem(boolean isOpen, boolean canBuy) {
        ItemStack is = this.menuItem;
        if (is == null) {
            this.menuItem = is = this.getItem();
        }
        return Items.Safe.appendLore((ItemStack)is.clone(), (String[])new String[]{"&8----------------------------", isOpen ? "&a&o\u0414\u043e\u0441\u0442\u0443\u043f\u043d\u043e" : (canBuy ? "&a\u041a\u0443\u043f\u0438\u0442\u044c&8 | &6" : "&c") + this.getClosedMessage()});
    }

    public boolean isOpen(PlayerInfo player) {
        return player.kits.contains(this.id);
    }

    public String getClosedMessage() {
        return "\u0426\u0435\u043d\u0430 - " + U.pluralsCoins((int)this.price);
    }

    protected abstract ItemStack getItem();

    public abstract void equip(PlayerInfo var1);
}

