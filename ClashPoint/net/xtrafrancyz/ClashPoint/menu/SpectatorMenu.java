/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.menu.IMenu
 *  net.xtrafrancyz.VimeNetwork.api.util.Items
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.SkullMeta
 */
package net.xtrafrancyz.ClashPoint.menu;

import java.util.List;
import java.util.stream.Collectors;
import net.xtrafrancyz.ClashPoint.object.PlayerInfo;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SpectatorMenu
implements IMenu {
    public static final ItemStack MENU_ITEM = Items.menuTitle((ItemStack)new ItemStack(Material.SKULL_ITEM, 1, 3), (String)"\u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044f \u043a \u0438\u0433\u0440\u043e\u043a\u0430\u043c", (String[])new String[0]);
    private Inventory inv = Bukkit.createInventory((InventoryHolder)this, (int)27, (String)"\u0412\u044b\u0431\u043e\u0440 \u0438\u0433\u0440\u043e\u043a\u0430");

    public void update() {
        this.inv.clear();
        int i = 0;
        List players = PlayerInfo.PLAYERS.values().stream().filter(p -> p.team != null).sorted((p1, p2) -> {
            int diff = p1.team.chatColor.getChar() - p2.team.chatColor.getChar();
            if (diff != 0) {
                return diff;
            }
            return p1.username.compareToIgnoreCase(p2.username);
        }).collect(Collectors.toList());
        for (PlayerInfo player : players) {
            this.inv.setItem(i++, Items.name((ItemStack)Items.head((String)player.username), (String)(player.team.chatColor + player.username), (String[])new String[]{"\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u0438"}));
        }
    }

    public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
        SkullMeta meta = (SkullMeta)is.getItemMeta();
        if (meta == null) {
            return;
        }
        Player target = Bukkit.getPlayerExact((String)meta.getOwner());
        if (target != null) {
            player.closeInventory();
            player.teleport((Entity)target);
        }
    }

    public Inventory getInventory() {
        return this.inv;
    }
}

