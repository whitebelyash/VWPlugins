/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.api;

import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Def {
    public static final String SERVICE_LORE = "VimeWorld.ru";
    public static final ItemStack ITEM_GAME_SELECT = Items.menuTitle(Material.SLIME_BALL, "\u0412\u044b\u0431\u043e\u0440 \u0438\u0433\u0440\u044b", "VimeWorld.ru");
    public static final ItemStack ITEM_TEAM_SELECT = Items.menuTitle(Material.NAME_TAG, "\u0412\u044b\u0431\u043e\u0440 \u043a\u043e\u043c\u0430\u043d\u0434\u044b", "VimeWorld.ru");
    public static final ItemStack ITEM_TO_LOBBY = Items.menuTitle(Material.COMPASS, "\u0412\u0435\u0440\u043d\u0443\u0442\u044c\u0441\u044f \u0432 \u043b\u043e\u0431\u0431\u0438", "VimeWorld.ru");
    public static final ItemStack ITEM_TRAILS = Items.menuTitle(new ItemStack(Material.INK_SACK, 1, 11), "\u0412\u044b\u0431\u043e\u0440 \u0441\u043b\u0435\u0434\u0430", "VimeWorld.ru");
    public static final ItemStack ITEM_MICRO_UPGRADES = Items.menuTitle(Material.EYE_OF_ENDER, "\u041c\u0435\u043d\u044e \u043c\u0438\u043a\u0440\u043e\u043f\u0440\u043e\u043a\u0430\u0447\u0435\u043a", "VimeWorld.ru");
    public static final ItemStack ITEM_TARGET_COMPASS = Items.setLore(new ItemStack(Material.COMPASS), "VimeWorld.ru");
    public static final ItemStack ITEM_ACHIEVEMENTS = Items.menuTitle(new ItemStack(Material.ENCHANTED_BOOK), "\u0414\u043e\u0441\u0442\u0438\u0436\u0435\u043d\u0438\u044f", "VimeWorld.ru");
    public static final ItemStack ITEM_SETTINGS = Items.menuTitle(new ItemStack(Material.NETHER_STAR), "\u0418\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f \u043e \u0432\u0430\u0441", "VimeWorld.ru");

    private Def() {
    }

    public static ItemStack getSettingsItem(Player player) {
        return Def.getSettingsItem(VimeNetwork.getPlayer(player));
    }

    public static ItemStack getSettingsItem(NetworkPlayer player) {
        return Items.menuTitle(ITEM_SETTINGS.getType(), player.getPrefixedName(), SERVICE_LORE);
    }

    public static boolean isServiceItem(ItemStack is) {
        return SERVICE_LORE.equals(Items.getLore(is, -1));
    }
}

