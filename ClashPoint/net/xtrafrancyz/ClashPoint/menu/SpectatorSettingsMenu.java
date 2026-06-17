/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.menu.IMenu
 *  net.xtrafrancyz.VimeNetwork.api.util.Items
 *  net.xtrafrancyz.VimeNetwork.api.util.U
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.ClashPoint.menu;

import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class SpectatorSettingsMenu
implements IMenu {
    public static final ItemStack MENU_ITEM = Items.menuTitle((ItemStack)new ItemStack(Material.REDSTONE_COMPARATOR), (String)"\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 \u0440\u0435\u0436\u0438\u043c\u0430 \u043d\u0430\u0431\u043b\u044e\u0434\u0430\u0442\u0435\u043b\u044f", (String[])new String[0]);
    private static final Material[] BOOTS = new Material[]{Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.GOLD_BOOTS, Material.DIAMOND_BOOTS};
    private final Inventory inv;

    public SpectatorSettingsMenu(Player player) {
        int level = SpectatorSettingsMenu.getSpeedLevel(player);
        this.inv = Bukkit.createInventory((InventoryHolder)this, (int)27);
        for (int i = 1; i <= 5; ++i) {
            ItemStack is = level == i ? Items.glow((ItemStack)Items.name((Material)BOOTS[i - 1], (String)("&a&l\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u043f\u043e\u043b\u0435\u0442\u0430 " + i), (String[])new String[]{"&7\u0412\u044b\u0431\u0440\u0430\u043d\u043e"})) : Items.name((Material)BOOTS[i - 1], (String)("&b&l\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u043f\u043e\u043b\u0435\u0442\u0430 " + i), (String[])new String[]{"\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u0432\u044b\u0431\u043e\u0440\u0430"});
            this.inv.setItem(10 + i, is);
        }
    }

    public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
        if ((slot -= 10) >= 1 && slot <= 5) {
            U.msg((CommandSender)player, (String[])new String[]{"\u0412\u0430\u043c \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u0430 \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u044c &a" + slot});
            player.setFlySpeed(0.1f + 0.05f * (float)(slot - 1));
            player.closeInventory();
        }
    }

    private static int getSpeedLevel(Player player) {
        float speed = player.getFlySpeed();
        int i = 0;
        while (!(speed <= 0.101f + 0.05f * (float)i)) {
            ++i;
        }
        return i + 1;
    }

    public Inventory getInventory() {
        return this.inv;
    }
}

