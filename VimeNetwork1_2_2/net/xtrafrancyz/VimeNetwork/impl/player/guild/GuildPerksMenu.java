/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.impl.player.guild;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.xtrafrancyz.Commons.guild.GuildPerk;
import net.xtrafrancyz.Core.network.packet.Packet69Guild;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.menu.ConfirmMenu;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.player.guild.GuildMemberMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class GuildPerksMenu
implements IMenu {
    private static Map<GuildPerk, Integer> PERK_SLOTS = new EnumMap<GuildPerk, Integer>(GuildPerk.class);
    private final GuildMemberMenu parent;
    private final Inventory inv;

    public GuildPerksMenu(GuildMemberMenu parent) {
        this.parent = parent;
        this.inv = Bukkit.createInventory((InventoryHolder)this, (int)27, (String)"\u041f\u0435\u0440\u043a\u0438 \u0433\u0438\u043b\u044c\u0434\u0438\u0438");
        this.inv.setItem(4, Items.name(Material.BED, "&f\u2190 &e\u041d\u0430\u0437\u0430\u0434", new String[0]));
        this.genItem(GuildPerk.MEMBERS, new ItemStack(Material.SKULL_ITEM, 1, 3), Arrays.asList("&a\u0423\u0432\u0435\u043b\u0438\u0447\u0438\u0432\u0430\u0435\u0442 \u043b\u0438\u043c\u0438\u0442 \u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u0430", "&a \u0447\u043b\u0435\u043d\u043e\u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u0438 \u043d\u0430 &f5 \u0438\u0433\u0440\u043e\u043a\u043e\u0432&a."));
        this.genItem(GuildPerk.COINS, Material.GOLD_INGOT, Arrays.asList("&a\u0423\u0432\u0435\u043b\u0438\u0447\u0438\u0432\u0430\u0435\u0442 \u043b\u0438\u043c\u0438\u0442 \u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u0430", "&a \u043a\u043e\u0438\u043d\u043e\u0432, \u043a\u043e\u0442\u043e\u0440\u044b\u0435 \u043c\u043e\u0436\u043d\u043e \u0437\u0430 \u0434\u0435\u043d\u044c", "&a \u0432\u043a\u043b\u0430\u0434\u044b\u0432\u0430\u0442\u044c \u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u044e \u043d\u0430&f 5000&a."));
        this.genItem(GuildPerk.MOTD, Material.BOOK_AND_QUILL, Arrays.asList("&a\u041f\u043e\u0437\u0432\u043e\u043b\u044f\u0435\u0442 \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u0438\u0442\u044c \u043f\u0440\u0438\u0432\u0435\u0442\u0441\u0442\u0432\u0435\u043d\u043d\u043e\u0435", "&a \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435, \u043a\u043e\u0442\u043e\u0440\u043e\u0435 \u0431\u0443\u0434\u0435\u0442 \u043f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c\u0441\u044f", "&a \u0447\u043b\u0435\u043d\u0430\u043c \u0433\u0438\u043b\u044c\u0434\u0438\u0438 \u043f\u0440\u0438 \u0437\u0430\u0445\u043e\u0434\u0435 \u0432 \u0438\u0433\u0440\u0443."));
        this.genItem(GuildPerk.PARTY, Material.EYE_OF_ENDER, Arrays.asList("&a\u041f\u043e\u0437\u0432\u043e\u043b\u044f\u0435\u0442 \u0441\u043e\u0437\u0434\u0430\u0442\u044c \u0433\u0440\u0443\u043f\u043f\u0443 \u0438\u0437", "&a \u0438\u0433\u0440\u043e\u043a\u043e\u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u0438 \u043e\u043d\u043b\u0430\u0439\u043d."));
        this.genItem(GuildPerk.TAG, Material.NAME_TAG, Arrays.asList("&a\u041f\u043e\u0437\u0432\u043e\u043b\u044f\u0435\u0442 \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u0438\u0442\u044c \u0442\u0435\u0433 \u0433\u0438\u043b\u044c\u0434\u0438\u0438,", "&a \u043a\u043e\u0442\u043e\u0440\u044b\u0439 \u0431\u0443\u0434\u0435\u0442 \u043f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c\u0441\u044f \u043f\u0435\u0440\u0435\u0434", "&a \u043d\u0438\u043a\u043e\u043c."));
        this.genItem(GuildPerk.COLOR, new ItemStack(Material.INK_SACK, 1, 1), Arrays.asList("&a\u041f\u043e\u0437\u0432\u043e\u043b\u044f\u0435\u0442 \u0437\u0430\u0434\u0430\u0442\u044c \u0446\u0432\u0435\u0442 \u0433\u0438\u043b\u044c\u0434\u0438\u0438, \u0432", "&a \u043a\u043e\u0442\u043e\u0440\u044b\u0439 \u0431\u0443\u0434\u0435\u0442 \u043e\u043a\u0440\u0430\u0448\u0438\u0432\u0430\u0442\u044c\u0441\u044f \u0442\u0435\u0433 \u0438", "&a \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u0433\u0438\u043b\u044c\u0434\u0438\u0438."));
        this.genItem(GuildPerk.COINS_MULT, Material.GOLD_NUGGET, Arrays.asList("&a\u0423\u0432\u0435\u043b\u0438\u0447\u0438\u0432\u0430\u0435\u0442 \u043c\u043d\u043e\u0436\u0438\u0442\u0435\u043b\u044c \u043a\u043e\u0438\u043d\u043e\u0432", "&a \u0434\u043b\u044f \u0432\u0441\u0435\u0445 \u0447\u043b\u0435\u043d\u043e\u0432 \u043a\u043b\u0430\u043d\u0430 \u043d\u0430 &f0.1&a.", "&a\u041c\u0430\u043a\u0441\u0438\u043c\u0430\u043b\u044c\u043d\u0430\u044f \u043f\u0440\u043e\u043a\u0430\u0447\u043a\u0430&f + \u04451&a."));
    }

    private int getLevel(GuildPerk perk) {
        return this.parent.guild.perks.get((Object)perk);
    }

    private boolean hasUpgrade(GuildPerk perk) {
        return this.getLevel(perk) < perk.upgrades.length;
    }

    private void genItem(GuildPerk perk, Material type, List<String> lore) {
        this.genItem(perk, new ItemStack(type), lore);
    }

    private void genItem(GuildPerk perk, ItemStack is, List<String> lore) {
        lore = new ArrayList<String>(lore);
        lore.add("");
        if (this.hasUpgrade(perk)) {
            int level = this.getLevel(perk);
            lore.add("&e\u041d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c\u044b\u0439 \u0443\u0440\u043e\u0432\u0435\u043d\u044c \u0433\u0438\u043b\u044c\u0434\u0438\u0438:&f " + perk.upgrades[level].neededLevel);
            lore.add("&e\u0426\u0435\u043d\u0430 \u0443\u043b\u0443\u0447\u0448\u0435\u043d\u0438\u044f:&f " + U.pluralsCoins(perk.upgrades[level].price));
        } else {
            lore.add("&7\u041f\u043e\u043b\u043d\u043e\u0441\u0442\u044c\u044e \u0443\u043b\u0443\u0447\u0448\u0435\u043d\u043e");
        }
        this.inv.setItem(PERK_SLOTS.get((Object)perk).intValue(), Items.name(is, "&b" + perk.name, lore));
    }

    @Override
    public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
        if (slot == 4) {
            this.parent.show(player);
            return;
        }
        for (Map.Entry<GuildPerk, Integer> entry : PERK_SLOTS.entrySet()) {
            if (entry.getValue() != slot) continue;
            GuildPerk perk = entry.getKey();
            if (this.hasUpgrade(perk)) {
                ConfirmMenu menu = new ConfirmMenu(null, () -> VimeNetwork.core().sendPacket(new Packet69Guild(this.parent.player.getId(), Packet69Guild.Action.UPGRADE_PERK).put("perk", (byte)perk.ordinal())), "\u0423\u043b\u0443\u0447\u0448\u0435\u043d\u0438\u0435 \u043f\u0435\u0440\u043a\u0430");
                menu.setConfirmText("&a\u041a\u0443\u043f\u0438\u0442\u044c \u0443\u043b\u0443\u0447\u0448\u0435\u043d\u0438\u0435 \u043f\u0435\u0440\u043a\u0430", "&f" + perk.name, "&a \u0437\u0430", "&e" + U.pluralsCoins(perk.upgrades[this.getLevel((GuildPerk)perk)].price));
                menu.show(player);
            }
            return;
        }
    }

    public Inventory getInventory() {
        return this.inv;
    }

    static {
        PERK_SLOTS.put(GuildPerk.MEMBERS, 10);
        PERK_SLOTS.put(GuildPerk.COINS, 11);
        PERK_SLOTS.put(GuildPerk.PARTY, 12);
        PERK_SLOTS.put(GuildPerk.MOTD, 13);
        PERK_SLOTS.put(GuildPerk.TAG, 14);
        PERK_SLOTS.put(GuildPerk.COLOR, 15);
        PERK_SLOTS.put(GuildPerk.COINS_MULT, 16);
    }
}

