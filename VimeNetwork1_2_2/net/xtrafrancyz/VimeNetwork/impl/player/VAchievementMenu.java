/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.impl.player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.CompletedAchievement;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.StatAchievement;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.WinAchievement;
import net.xtrafrancyz.VimeNetwork.api.util.Invs;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.player.VAchievements;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class VAchievementMenu
implements IMenu {
    private static final short BACK_SLOT = 4;
    private static final ItemStack BACK_ITEM = Items.name(Material.BED, "&f\u2190 &e\u041d\u0430\u0437\u0430\u0434", new String[0]);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
    private Inventory menu;
    private VPlayer player;

    public VAchievementMenu(VPlayer player) {
        this.player = player;
        this.menu = Bukkit.createInventory((InventoryHolder)this, (int)54, (String)"\u0414\u043e\u0441\u0442\u0438\u0436\u0435\u043d\u0438\u044f");
        for (Achievement.Group group : Achievement.Group.values()) {
            if (group.getSlot() == -1) continue;
            ItemStack is = group.getItemStack().clone();
            LinkedList<String> lore = new LinkedList<String>();
            int count = this.getCompletedCount(group);
            int total = group.getAchievements().size();
            int percent = Math.round((float)(100 * count) / (float)total);
            lore.add("&7\u041f\u0440\u043e\u0433\u0440\u0435\u0441\u0441: &f" + count + "&7/&f" + total + "&7 (" + (percent == 100 ? "&a" : "") + percent + "%&7)");
            lore.add("");
            lore.add("&a\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u043f\u0440\u043e\u0441\u043c\u043e\u0442\u0440\u0430");
            is = Items.name(is, "&b&l" + group.getName(), lore);
            this.menu.setItem(group.getSlot(), is);
        }
    }

    private int getCompletedCount(Achievement.Group group) {
        int count = 0;
        VAchievements a = this.player.getAchievements();
        for (Achievement achievement : group.getAchievements()) {
            if (!a.isCompleted(achievement)) continue;
            ++count;
        }
        return count;
    }

    @Override
    public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
        for (Achievement.Group group : Achievement.Group.values()) {
            if (group.getSlot() != slot) continue;
            new Category(group).show(player);
            return;
        }
    }

    public Inventory getInventory() {
        return this.menu;
    }

    private class Category
    implements IMenu {
        private Inventory inv;

        public Category(Achievement.Group group) {
            this.inv = Bukkit.createInventory((InventoryHolder)this, (int)54, (String)group.getName());
            this.inv.setItem(4, BACK_ITEM.clone());
            int common = 9;
            int win = 38;
            for (Achievement a : group.getAchievements()) {
                if (a.isHidden() && !VAchievementMenu.this.player.getAchievements().isCompleted(a)) continue;
                if (a instanceof WinAchievement) {
                    this.inv.setItem(win++, this.getItem(a));
                    continue;
                }
                this.inv.setItem(common++, this.getItem(a));
            }
        }

        private ItemStack getItem(Achievement a) {
            Material type;
            String name;
            boolean complete = false;
            CompletedAchievement ca = VAchievementMenu.this.player.getAchievements().getCompletedAchievement(a);
            if (ca != null) {
                name = "&a" + a.getName();
                complete = true;
                type = Material.DIAMOND;
            } else {
                type = Material.COAL;
                name = a.getGroup() == Achievement.Group.SECRET ? "&7???" : "&c" + a.getName();
            }
            LinkedList<String> lore = new LinkedList<String>();
            if (!complete && a.getGroup() == Achievement.Group.SECRET) {
                lore.add("&7???");
            } else {
                for (String line : a.getDescription()) {
                    lore.add("&f" + line);
                }
                lore.add("");
                lore.add("&7\u041d\u0430\u0433\u0440\u0430\u0434\u0430: &e" + U.pluralsCoins(a.getReward()));
                if (a instanceof StatAchievement) {
                    StatAchievement sa = (StatAchievement)a;
                    int progress = VAchievementMenu.this.player.getStats().get(sa.getStat());
                    lore.add("&7\u041f\u0440\u043e\u0433\u0440\u0435\u0441\u0441: &f" + progress + "&7/&f" + sa.getNeeded() + " &7(" + (progress >= sa.getNeeded() ? "&a" : "") + Math.min(100, Math.round(100.0f * (float)progress / (float)sa.getNeeded())) + "%&7)");
                }
                if (complete) {
                    lore.add("&7\u0414\u0430\u0442\u0430 \u0432\u044b\u043f\u043e\u043b\u043d\u0435\u043d\u0438\u044f: &a" + DATE_FORMAT.format(new Date((long)ca.getTimestamp() * 1000L)));
                }
            }
            return Items.name(type, name, lore);
        }

        @Override
        public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
            if (slot == 4) {
                Invs.forceOpen((HumanEntity)player, VAchievementMenu.this.menu);
            }
        }

        public Inventory getInventory() {
            return this.inv;
        }
    }
}

