/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.VimeNetwork
 *  net.xtrafrancyz.VimeNetwork.api.menu.ConfirmMenu
 *  net.xtrafrancyz.VimeNetwork.api.menu.IMenu
 *  net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer
 *  net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement
 *  net.xtrafrancyz.VimeNetwork.api.util.Invs
 *  net.xtrafrancyz.VimeNetwork.api.util.Items
 *  net.xtrafrancyz.VimeNetwork.api.util.Items$Safe
 *  net.xtrafrancyz.VimeNetwork.api.util.U
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.SkyWars.menu;

import java.util.List;
import net.xtrafrancyz.SkyWars.PlayerInfo;
import net.xtrafrancyz.SkyWars.SkyWars;
import net.xtrafrancyz.SkyWars.kit.Kit;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.menu.ConfirmMenu;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.util.Invs;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class KitSelectMenu
implements IMenu {
    public static final ItemStack MENU_ITEM = Items.menuTitle((Material)Material.EMERALD, (String)"\u0412\u044b\u0431\u043e\u0440 \u0441\u0442\u0430\u0440\u0442\u043e\u0432\u043e\u0433\u043e \u043d\u0430\u0431\u043e\u0440\u0430", (String[])new String[]{"VimeWorld.ru"});
    private Inventory inv;

    public KitSelectMenu(Player player) {
        this(PlayerInfo.get(player));
    }

    public KitSelectMenu(PlayerInfo player) {
        Kit selected = SkyWars.instance().kits.kits.get(player.kit);
        this.inv = Bukkit.createInventory((InventoryHolder)this, (int)36, (String)("\u0412\u044b\u0431\u043e\u0440 \u043d\u0430\u0431\u043e\u0440\u0430" + (selected == null ? "" : " - " + selected.name)));
        int coins = VimeNetwork.getPlayer((String)player.username).getCoins();
        for (Kit kit : SkyWars.instance().kits.kits.values()) {
            this.updateKit(kit, player, coins);
        }
    }

    private void updateKit(Kit kit, PlayerInfo player, int coins) {
        String displayName;
        boolean isOpen = kit.isOpen(player);
        ItemStack is = kit.getMenuItem(isOpen, kit.price > 0 && coins >= kit.price);
        if (kit.id.equals(player.kit)) {
            displayName = ChatColor.GREEN + kit.name + " | \u0412\u044b\u0431\u0440\u0430\u043d\u043e";
            List lore = is.getItemMeta().getLore();
            lore.set(lore.size() - 1, "&a\u0412\u044b\u0431\u0440\u0430\u043d\u043e");
            is = Items.Safe.lore((ItemStack)is, (List)lore);
        } else {
            displayName = isOpen ? ChatColor.DARK_GREEN + kit.name : (kit.price > 0 && coins >= kit.price ? ChatColor.BLUE + kit.name : ChatColor.GRAY + kit.name);
        }
        is = Items.Safe.name((ItemStack)is, (String)displayName);
        this.inv.setItem(kit.slot, is);
    }

    public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
        for (Kit kit : SkyWars.instance().kits.kits.values()) {
            if (kit.slot != slot) continue;
            PlayerInfo pi = PlayerInfo.get(player);
            if (kit.isOpen(pi)) {
                player.closeInventory();
                pi.kit = kit.id;
                U.msg((CommandSender)player, (String[])new String[]{"\u0412\u044b \u0432\u044b\u0431\u0440\u0430\u043b\u0438 \u043d\u0430\u0431\u043e\u0440: &b" + kit.name});
                break;
            }
            if (kit.price <= VimeNetwork.getPlayer((Player)player).getCoins()) {
                ConfirmMenu menu = new ConfirmMenu(this.inv, () -> {
                    NetworkPlayer networkPlayer = VimeNetwork.getPlayer((Player)player);
                    networkPlayer.takeCoins(kit.price);
                    networkPlayer.getAchievements().complete(Achievement.SW_FIRST_KIT);
                    SkyWars.instance().repository.query("INSERT INTO sw_kits (userid, kit) VALUES (" + networkPlayer.getId() + ", '" + kit.id + "')");
                    pi.kits.add(kit.id);
                    pi.kit = kit.id;
                    this.updateKit(kit, pi, networkPlayer.getCoins());
                    VimeNetwork.metrics().add("sw.buy.kit", kit.price);
                }, kit.name);
                menu.setConfirmText("&a\u041a\u0443\u043f\u0438\u0442\u044c \u043d\u0430\u0431\u043e\u0440", new String[]{"&f\u0426\u0435\u043d\u0430: &e" + U.pluralsCoins((int)kit.price)});
                Invs.forceOpen((HumanEntity)player, (InventoryHolder)menu);
                break;
            }
            VimeNetwork.texteria().showInsufficientlyCoins(player);
            break;
        }
    }

    public Inventory getInventory() {
        return this.inv;
    }
}

