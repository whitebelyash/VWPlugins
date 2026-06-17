/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.menu.IMenu
 *  net.xtrafrancyz.VimeNetwork.api.util.Items
 *  net.xtrafrancyz.bukkit.texteria.utils.Visibility
 *  net.xtrafrancyz.bukkit.texteria.utils.Visibility$Always
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.ClashPoint.menu.shop;

import net.xtrafrancyz.ClashPoint.game.CPTexteria;
import net.xtrafrancyz.ClashPoint.menu.shop.ShopCategory;
import net.xtrafrancyz.ClashPoint.menu.shop.ShopMenu;
import net.xtrafrancyz.ClashPoint.object.PlayerInfo;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.bukkit.texteria.utils.Visibility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class CategoryMenu
implements IMenu {
    private static final ItemStack BACK_ITEM = Items.name((Material)Material.BED, (String)"&f\u2190 &e\u041d\u0430\u0437\u0430\u0434", (String[])new String[0]);
    private final ShopMenu parent;
    private final Inventory inv;
    private final ShopCategory category;

    public CategoryMenu(ShopMenu parent, ShopCategory category) {
        this.parent = parent;
        this.category = category;
        int size = category.items.size() + 9;
        size = size % 9 != 0 ? (size / 9 + 1) * 9 : size;
        this.inv = Bukkit.createInventory((InventoryHolder)this, (int)size, (String)category.name);
        int index = 0;
        for (ShopMenu.Item item : category.items) {
            this.inv.setItem(index++, Items.appendLore((ItemStack)item.is.clone(), (String[])new String[]{"&d\u0426\u0435\u043d\u0430: " + item.price.currency.color + item.price.getReadable()}));
        }
        this.inv.setItem(size - 5, BACK_ITEM);
    }

    public void onClick(ItemStack is, Player bukkitPlayer, int slot, ClickType clickType) {
        if (slot == this.inv.getSize() - 5) {
            this.parent.show(bukkitPlayer);
            return;
        }
        if (slot >= 0 && slot < this.category.items.size()) {
            PlayerInfo player = PlayerInfo.get(bukkitPlayer);
            ShopMenu.Item item = this.category.items.get(slot);
            int balance = player.countResources(item.price.currency.material);
            if (balance < item.price.amount) {
                String message = "\u0412\u0430\u043c \u043d\u0435 \u0445\u0432\u0430\u0442\u0430\u0435\u0442 " + new ShopMenu.Price(item.price.currency, item.price.amount - balance).getReadable();
                CPTexteria.showCustomMessage(bukkitPlayer, message, -44205, 2000L, (Visibility)new Visibility.Always());
                return;
            }
            if (item.action.apply(bukkitPlayer).booleanValue()) {
                player.takeResources(item.price.currency.material, item.price.amount);
            }
        }
    }

    public Inventory getInventory() {
        return this.inv;
    }
}

