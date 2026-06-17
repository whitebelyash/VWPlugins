/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  gnu.trove.map.TIntObjectMap
 *  gnu.trove.map.hash.TIntObjectHashMap
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.impl.player;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.xtrafrancyz.Commons.Leveling;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.menu.ConfirmMenu;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.player.Multiplier;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.OwnedMultiplier;
import net.xtrafrancyz.VimeNetwork.api.player.Stat;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.player.treasure.TreasureType;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.player.LevelingRewards;
import net.xtrafrancyz.VimeNetwork.impl.player.VAchievementMenu;
import net.xtrafrancyz.VimeNetwork.impl.player.VArrowTrailMenu;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class VPlayerMenu
implements IMenu {
    private static final int SLOT_ACHIEVEMENTS = 20;
    private static final int SLOT_GOALS = 22;
    private static final int SLOT_ARROW_TRAIL = 24;
    private static final int SLOT_RANK = 1;
    private static final int SLOT_COINS = 3;
    private static final int SLOT_LEVELING = 5;
    private static final int SLOT_TREASURES = 7;
    private static final int SLOT_SETTINGS = 41;
    private static final int SLOT_MULTIPLIERS = 39;
    public final VPlayer player;
    public final Inventory inv;

    public VPlayerMenu(Player player) {
        this(VimeNetwork.getPlayer(player));
    }

    public VPlayerMenu(NetworkPlayer player) {
        this.player = (VPlayer)player;
        this.inv = Bukkit.createInventory((InventoryHolder)this, (int)45, (String)player.getName());
        this.inv.setItem(1, Items.name(Material.DIAMOND, "&2\u0412\u0430\u0448 \u0441\u0442\u0430\u0442\u0443\u0441:", player.getRank().getDisplayName()));
        this.inv.setItem(3, Items.name(Material.GOLD_NUGGET, "&2\u0423 \u0432\u0430\u0441 \u043d\u0430 \u0440\u0443\u043a\u0430\u0445:", "&e" + U.pluralsCoins(player.getCoins())));
        int expToNextLevel = Leveling.getExpToNextLevel(player.getLevel());
        float progress = (float)player.getPartialExp() / (float)expToNextLevel;
        this.inv.setItem(5, Items.name(Material.EXP_BOTTLE, "&b&l\u0423\u0440\u043e\u0432\u0435\u043d\u044c", "&e\u0422\u0435\u043a\u0443\u0449\u0438\u0439 \u0443\u0440\u043e\u0432\u0435\u043d\u044c: &f" + player.getLevel() + " (" + (int)(progress * 100.0f) + "%)", "&e\u041f\u0440\u043e\u0433\u0440\u0435\u0441\u0441: &f" + player.getPartialExp() + "&e/&f" + expToNextLevel, "&f[" + U.genBar(48, progress, '|', "&7", "&a") + "&f]", "", "&a\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u043f\u0440\u043e\u0441\u043c\u043e\u0442\u0440\u0430 \u043d\u0430\u0433\u0440\u0430\u0434"));
        this.inv.setItem(7, Items.name(Material.STORAGE_MINECART, "&2\u0421\u043e\u043a\u0440\u043e\u0432\u0438\u0449\u043d\u0438\u0446\u0430: &e", TreasureType.BASIC.name + "&f: " + player.getTreasures().get(TreasureType.BASIC), TreasureType.ANCIENT.name + "&f: " + player.getTreasures().get(TreasureType.ANCIENT), TreasureType.MYTHICAL.name + "&f: " + player.getTreasures().get(TreasureType.MYTHICAL)));
        int completed = player.getAchievements().getCompletedCount();
        int total = Achievement.getAchievements().size();
        this.inv.setItem(20, Items.name(Material.ENCHANTED_BOOK, "&b&l\u0414\u043e\u0441\u0442\u0438\u0436\u0435\u043d\u0438\u044f", "&f\u0412\u044b\u043f\u043e\u043b\u043d\u0435\u043d\u043e: &a" + completed + "&7/" + total + " (" + Math.round(100 * completed / total) + "%)", "", "&a\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u043f\u0440\u043e\u0441\u043c\u043e\u0442\u0440\u0430"));
        this.inv.setItem(22, Items.name(Material.PAPER, "&b&l\u0417\u0430\u0434\u0430\u043d\u0438\u044f", "&f\u0410\u043a\u0442\u0438\u0432\u043d\u043e: &a" + player.getGoals().getActiveGoals().size(), "&f\u0412\u044b\u043f\u043e\u043b\u043d\u0435\u043d\u043e: &a" + player.getStats().get(Stat.GOAL_COMPLETE), "", "&a\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u043f\u0440\u043e\u0441\u043c\u043e\u0442\u0440\u0430"));
        this.inv.setItem(24, Items.name(Material.ARROW, "&b&l\u0421\u043b\u0435\u0434 \u0441\u0442\u0440\u0435\u043b\u044b", player.getArrowTrail() == null ? "&f\u041d\u0435 \u0432\u044b\u0431\u0440\u0430\u043d\u043e" : "&f\u0412\u044b\u0431\u0440\u0430\u043d\u043e: &a" + player.getArrowTrail().getName(), "", "&a\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u0432\u044b\u0431\u043e\u0440\u0430"));
        this.inv.setItem(41, Items.name(Material.REDSTONE_COMPARATOR, "&b&l\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438", "", "&a\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u0432\u044b\u0431\u043e\u0440\u0430"));
        int rmult = player.getMultipliers().getRankMultiplier();
        int emult = player.getMultipliers().getExtraMultiplier();
        float gmult = player.getMultipliers().getGuildMultiplier();
        float tmult = player.getMultipliers().getCurrentMultiplier();
        this.inv.setItem(39, Items.name(Material.GOLD_INGOT, "&b&l\u041c\u043d\u043e\u0436\u0438\u0442\u0435\u043b\u044c \u043a\u043e\u0438\u043d\u043e\u0432", "&f\u041c\u043d\u043e\u0436\u0438\u0442\u0435\u043b\u044c \u0441\u0442\u0430\u0442\u0443\u0441\u0430: " + (rmult == 1 ? "x1" : "&ax" + rmult + " (+" + (rmult - 1) * 100 + "%)"), "&f\u0412\u0440\u0435\u043c\u0435\u043d\u043d\u044b\u0439 \u0431\u043e\u043d\u0443\u0441: " + (emult == 0 ? "&7\u043d\u0435\u0442" : "&ax" + (emult + 1) + " (+" + emult * 100 + "%)"), "&f\u0411\u043e\u043d\u0443\u0441 \u0433\u0438\u043b\u044c\u0434\u0438\u0438: " + (gmult == 0.0f ? "&7\u043d\u0435\u0442" : "&ax" + (gmult + 1.0f) + " (+" + Math.round(gmult * 100.0f) + "%)"), "", "&f\u041e\u0431\u0449\u0438\u0439: &ex" + player.getMultipliers().getFormattedMultiplier() + " (+" + Math.round((tmult - 1.0f) * 100.0f) + "%)", "", "&a\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u043f\u0440\u043e\u0441\u043c\u043e\u0442\u0440\u0430 \u0434\u043e\u0441\u0442\u0443\u043f\u043d\u044b\u0445 \u043c\u043d\u043e\u0436\u0438\u0442\u0435\u043b\u0435\u0439"));
    }

    @Override
    public void onClick(ItemStack is, Player bukkitPlayer, int slot, ClickType clickType) {
        switch (slot) {
            case 5: {
                new LevelingMenu(this.player, this).show(bukkitPlayer);
                break;
            }
            case 20: {
                new VAchievementMenu(this.player).show(bukkitPlayer);
                break;
            }
            case 22: {
                this.player.getGoals().openInventory();
                break;
            }
            case 24: {
                new VArrowTrailMenu(this.inv, this.player).show(bukkitPlayer);
                break;
            }
            case 41: {
                new SettingsMenu(this.player, this).show(bukkitPlayer);
                break;
            }
            case 39: {
                new MultipliersMenu(this.player, this).show(bukkitPlayer);
            }
        }
    }

    public Inventory getInventory() {
        return this.inv;
    }

    static class FlagTogglable
    extends Togglable {
        protected int flag;

        FlagTogglable(int slot, ItemStack is, int flag) {
            super(slot, is);
            this.flag = flag;
        }

        @Override
        boolean isEnabled(VPlayer player) {
            return player.settings.get(this.flag);
        }

        @Override
        boolean toggle(VPlayer player) {
            boolean f = !player.settings.get(this.flag);
            player.settings.set(this.flag, f);
            return f;
        }
    }

    static abstract class Togglable {
        ItemStack is;
        int slot;

        Togglable(int slot, ItemStack is) {
            this.slot = slot;
            this.is = is;
        }

        abstract boolean toggle(VPlayer var1);

        abstract boolean isEnabled(VPlayer var1);

        public int hashCode() {
            return this.slot;
        }

        public boolean equals(Object obj) {
            return obj == this;
        }
    }

    private static class SettingsMenu
    implements IMenu {
        private final Inventory inv;
        private final VPlayerMenu parent;
        private final VPlayer player;
        private static final List<Togglable> TOGGLABLES = Arrays.asList(new FlagTogglable(9, Items.name(Material.BOOK_AND_QUILL, "&b&l\u041b\u0438\u0447\u043d\u044b\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f", "&7 \u0415\u0441\u043b\u0438 \u0432\u044b\u043a\u043b\u044e\u0447\u0438\u0442\u044c, \u0442\u043e \u0447\u0435\u0440\u0435\u0437 \u043b\u0438\u0447\u043d\u044b\u0435", "&7\u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f \u0432\u044b \u0441\u043c\u043e\u0436\u0435\u0442\u0435 \u043e\u0431\u0449\u0430\u0442\u044c\u0441\u044f \u0442\u043e\u043b\u044c\u043a\u043e", "&7\u0441\u043e \u0441\u0432\u043e\u0438\u043c\u0438 \u0434\u0440\u0443\u0437\u044c\u044f\u043c\u0438.", "&7\u0414\u0440\u0443\u0433\u0438\u0435 \u0438\u0433\u0440\u043e\u043a\u0438 \u043d\u0435 \u0441\u043c\u043e\u0433\u0443\u0442 \u043d\u0430\u043f\u0438\u0441\u0430\u0442\u044c \u0432\u0430\u043c."), 1), new FlagTogglable(11, Items.name(new ItemStack(Material.SKULL_ITEM, 1, 3), "&b&l\u041f\u0440\u0438\u0433\u043b\u0430\u0448\u0435\u043d\u0438\u044f \u0432 \u0433\u0440\u0443\u043f\u043f\u0443", "&7 \u0415\u0441\u043b\u0438 \u0432\u044b\u043a\u043b\u044e\u0447\u0438\u0442\u044c, \u0442\u043e \u0432\u0430\u043c \u043d\u0435", "&7\u0431\u0443\u0434\u0443\u0442 \u043f\u0440\u0438\u0445\u043e\u0434\u0438\u0442\u044c \u043f\u0440\u0438\u0433\u043b\u0430\u0448\u0435\u043d\u0438\u044f,", "&7\u043e\u0434\u043d\u0430\u043a\u043e \u0432\u0441\u0451 \u0440\u0430\u0432\u043d\u043e \u043c\u043e\u0436\u043d\u043e \u0431\u0443\u0434\u0435\u0442", "&7\u043f\u0440\u0438\u0441\u043e\u0435\u0434\u0438\u043d\u0438\u0442\u044c\u0441\u044f \u043a \u0433\u0440\u0443\u043f\u043f\u0435", "&7\u0441 \u043f\u043e\u043c\u043e\u0449\u044c\u044e \u043a\u043e\u043c\u0430\u043d\u0434\u044b", "&f/party join <\u043d\u0438\u043a \u0438\u0433\u0440\u043e\u043a\u0430>"), 0), new FlagTogglable(13, Items.name(Material.BEACON, "&b&l\u041f\u0440\u0438\u0433\u043b\u0430\u0448\u0435\u043d\u0438\u044f \u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u044e", "&7 \u0415\u0441\u043b\u0438 \u0432\u044b\u043a\u043b\u044e\u0447\u0438\u0442\u044c, \u0442\u043e \u0432\u0430\u043c \u043d\u0435", "&7\u0431\u0443\u0434\u0443\u0442 \u043f\u0440\u0438\u0445\u043e\u0434\u0438\u0442\u044c \u043f\u0440\u0438\u0433\u043b\u0430\u0448\u0435\u043d\u0438\u044f,", "&7\u043e\u0434\u043d\u0430\u043a\u043e \u0432\u0441\u0451 \u0440\u0430\u0432\u043d\u043e \u043c\u043e\u0436\u043d\u043e \u0431\u0443\u0434\u0435\u0442", "&7\u0432\u0441\u0442\u0443\u043f\u0438\u0442\u044c \u043a \u0433\u0438\u043b\u044c\u0434\u0438\u044e \u0441 \u043f\u043e\u043c\u043e\u0449\u044c\u044e", "&7\u043a\u043e\u043c\u0430\u043d\u0434\u044b:", "&f/guild accept <\u043d\u0438\u043a \u0438\u0433\u0440\u043e\u043a\u0430>"), 7), new FlagTogglable(15, Items.name(Material.PAPER, "&b&l\u041d\u0430\u043f\u043e\u043c\u0438\u043d\u0430\u043d\u0438\u0435 \u043e \u0437\u0430\u0434\u0430\u043d\u0438\u044f\u0445", "&7 \u0415\u0441\u043b\u0438 \u0432\u044b\u043a\u043b\u044e\u0447\u0438\u0442\u044c, \u0442\u043e \u043e\u043f\u043e\u0432\u0435\u0449\u0435\u043d\u0438\u0435:", "&f\u0410\u043a\u0442\u0438\u0432\u043d\u043e \u0437\u0430\u0434\u0430\u043d\u0438\u0439: &a&l_", "&7\u043d\u0435 \u0431\u0443\u0434\u0435\u0442 \u043f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c\u0441\u044f."), 2), new FlagTogglable(17, Items.name(Material.EMERALD, "&b&l\u041e\u043f\u043e\u0432\u0435\u0449\u0435\u043d\u0438\u044f \u043e \u0441\u0442\u0440\u0438\u043c\u0430\u0445", "&7 \u0415\u0441\u043b\u0438 \u0432\u044b\u043a\u043b\u044e\u0447\u0438\u0442\u044c, \u0442\u043e \u0432\u044b \u043d\u0435", "&7\u0431\u0443\u0434\u0435\u0442\u0435 \u0432\u0438\u0434\u0435\u0442\u044c \u043e\u043f\u043e\u0432\u0435\u0449\u0435\u043d\u0438\u044f \u043e\u0431", "&7\u0438\u0434\u0443\u0449\u0438\u0445 \u0441\u0442\u0440\u0438\u043c\u0430\u0445 \u0432 \u0447\u0430\u0442\u0435 \u0438\u043b\u0438", "&7\u0432 \u0432\u0435\u0440\u0445\u043d\u0435\u043c \u043f\u0440\u0430\u0432\u043e\u043c \u0443\u0433\u043b\u0443 \u044d\u043a\u0440\u0430\u043d\u0430."), 3), new FlagTogglable(38, Items.name(new ItemStack(Material.SKULL_ITEM, 1, 2), "&b&l\u0417\u0430\u044f\u0432\u043a\u0438 \u0432 \u0434\u0440\u0443\u0437\u044c\u044f", "&7 \u0415\u0441\u043b\u0438 \u0432\u044b\u043a\u043b\u044e\u0447\u0438\u0442\u044c, \u0442\u043e \u0443 \u0432\u0430\u0441 \u043d\u0435", "&7\u0431\u0443\u0434\u0435\u0442 \u043e\u043f\u043e\u0432\u0435\u0449\u0435\u043d\u0438\u044f \u043e \u0437\u0430\u044f\u0432\u043a\u0430\u0445,", "&7\u043e\u0434\u043d\u0430\u043a\u043e \u0432\u0441\u0451 \u0440\u0430\u0432\u043d\u043e \u043c\u043e\u0436\u043d\u043e \u0431\u0443\u0434\u0435\u0442", "&7\u043f\u0440\u0438\u043d\u044f\u0442\u044c \u0437\u0430\u044f\u0432\u043a\u0443 \u0441 \u043f\u043e\u043c\u043e\u0449\u044c\u044e \u043a\u043e\u043c\u0430\u043d\u0434\u044b", "&f/friend accept <\u043d\u0438\u043a \u0438\u0433\u0440\u043e\u043a\u0430>"), 4), new FlagTogglable(40, Items.name(new ItemStack(Material.MONSTER_EGG, 1, 50), "&b&l\u041e\u043f\u043e\u0432\u0435\u0449\u0435\u043d\u0438\u044f \u043e\u0442 \u0434\u0440\u0443\u0437\u0435\u0439", "&7 \u0415\u0441\u043b\u0438 \u0432\u044b\u043a\u043b\u044e\u0447\u0438\u0442\u044c, \u0442\u043e \u0432\u0430\u043c \u043d\u0435", "&7\u0431\u0443\u0434\u0443\u0442 \u043f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c\u0441\u044f \u043e\u043f\u043e\u0432\u0435\u0449\u0435\u043d\u0438\u044f", "&7\u043e \u0442\u043e\u043c \u0447\u0442\u043e \u0432\u0430\u0448 \u0434\u0440\u0443\u0433 &f\u0437\u0430\u0448\u0435\u043b&7 \u0438\u043b\u0438", "&f\u0432\u044b\u0448\u0435\u043b&7 \u0438\u0437 \u0438\u0433\u0440\u044b"), 5), new FlagTogglable(42, Items.name(new ItemStack(Material.EMPTY_MAP), "&b&l\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u0434\u0440\u0443\u0437\u044c\u044f\u043c \u0432\u0430\u0448 \u0442\u043e\u0447\u043d\u044b\u0439 \u0441\u0435\u0440\u0432\u0435\u0440", "&6 \u0414\u0430\u043d\u043d\u0430\u044f \u0444\u0443\u043d\u043a\u0446\u0438\u044f \u0434\u043e\u0441\u0442\u0443\u043f\u043d\u0430 \u0442\u043e\u043b\u044c\u043a\u043e \u0434\u043b\u044f &aVIP", "&6\u0438\u0433\u0440\u043e\u043a\u043e\u0432 \u0438\u043b\u0438 \u0432\u044b\u0448\u0435.", "", "&7 \u0415\u0441\u043b\u0438 \u0432\u043a\u043b\u044e\u0447\u0438\u0442\u044c, \u0442\u043e \u0432\u0430\u0448\u0438 \u0434\u0440\u0443\u0437\u044c\u044f \u0431\u0443\u0434\u0443\u0442", "&7\u0442\u043e\u0447\u043d\u043e \u0432\u0438\u0434\u0435\u0442\u044c \u0441\u0435\u0440\u0432\u0435\u0440, \u043d\u0430 \u043a\u043e\u0442\u043e\u0440\u043e\u043c \u0432\u044b", "&7\u043d\u0430\u0445\u043e\u0434\u0438\u0442\u0435\u0441\u044c. \u041d\u0430\u043f\u0440\u0438\u043c\u0435\u0440:&f \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u0441\u044f \u0432 LOBBY_1", "&7 \u0418\u043d\u0430\u0447\u0435 \u0432\u0430\u0448\u0438 \u0434\u0440\u0443\u0437\u044c\u044f \u0431\u0443\u0434\u0443\u0442 \u0432\u0438\u0434\u0435\u0442\u044c \u0442\u043e\u043b\u044c\u043a\u043e", "&7\u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u0438\u0433\u0440\u044b/\u043b\u043e\u0431\u0431\u0438.", "&7 \u041d\u0430\u043f\u0440\u0438\u043c\u0435\u0440:&f \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u0441\u044f \u0432 \u041b\u043e\u0431\u0431\u0438"), 6){

            @Override
            boolean toggle(VPlayer player) {
                if (!player.rank.has(Rank.VIP)) {
                    return player.settings.get(this.flag);
                }
                return super.toggle(player);
            }
        });
        private static final ItemStack ENABLED_ITEM = Items.name(new ItemStack(Material.INK_SACK, 1, 10), "&a\u0412\u043a\u043b\u044e\u0447\u0435\u043d\u043e", "\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u0432\u044b\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u044f");
        private static final ItemStack DISABLED_ITEM = Items.name(new ItemStack(Material.INK_SACK, 1, 8), "&c\u0412\u044b\u043a\u043b\u044e\u0447\u0435\u043d\u043e", "\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u0432\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u044f");

        public SettingsMenu(VPlayer player, VPlayerMenu parent) {
            this.player = player;
            this.parent = parent;
            this.inv = Bukkit.createInventory((InventoryHolder)this, (int)54, (String)"\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438");
            this.inv.setItem(4, Items.name(Material.BED, "&f\u2190 &e\u041d\u0430\u0437\u0430\u0434", new String[0]));
            this.updateTogglables();
        }

        public void updateTogglables() {
            for (Togglable togglable : TOGGLABLES) {
                this.inv.setItem(togglable.slot, togglable.is.clone());
                this.inv.setItem(togglable.slot + 9, togglable.isEnabled(this.player) ? ENABLED_ITEM.clone() : DISABLED_ITEM.clone());
            }
        }

        @Override
        public void onClick(ItemStack is, Player bukkitPlayer, int slot, ClickType clickType) {
            if (slot == 4) {
                this.parent.show(bukkitPlayer);
                return;
            }
            Togglable togglable = null;
            for (Togglable t : TOGGLABLES) {
                if (t.slot != slot && t.slot != slot - 9) continue;
                togglable = t;
                break;
            }
            if (togglable != null) {
                togglable.toggle(this.player);
                this.updateTogglables();
            }
        }

        public Inventory getInventory() {
            return this.inv;
        }
    }

    private static class MultipliersMenu
    implements IMenu {
        private final Inventory inv;
        private final VPlayerMenu parent;
        private final VPlayer player;
        private final TIntObjectMap<Multiplier> mapping;

        public MultipliersMenu(VPlayer player, VPlayerMenu parent) {
            this.parent = parent;
            this.player = player;
            this.mapping = new TIntObjectHashMap();
            this.inv = Bukkit.createInventory((InventoryHolder)this, (int)54, (String)"\u041c\u043d\u043e\u0436\u0438\u0442\u0435\u043b\u0438 \u043a\u043e\u0438\u043d\u043e\u0432");
            this.inv.setItem(4, Items.name(Material.BED, "&f\u2190 &e\u041d\u0430\u0437\u0430\u0434", new String[0]));
            this.update();
        }

        private void update() {
            for (int i = 19; i < this.mapping.size(); ++i) {
                this.inv.setItem(i, null);
            }
            this.mapping.clear();
            List<OwnedMultiplier> list = this.player.getMultipliers().list();
            list.sort((a, b) -> {
                int mult = a.getMultiplier().getMultiplier() - b.getMultiplier().getMultiplier();
                if (mult != 0) {
                    return mult;
                }
                return a.getMultiplier().getDuration() - b.getMultiplier().getDuration();
            });
            int slot = 9;
            int lastMultiplier = 0;
            for (OwnedMultiplier owned : list) {
                if (lastMultiplier != owned.getMultiplier().getMultiplier()) {
                    slot += 9;
                    slot /= 9;
                    slot *= 9;
                    lastMultiplier = owned.getMultiplier().getMultiplier();
                }
                ItemStack is = Items.name(new ItemStack(Material.GOLD_INGOT, owned.getAmount()), "&f\u041c\u043d\u043e\u0436\u0438\u0442\u0435\u043b\u044c " + owned.getMultiplier().getText("&e", "&f"), "&f\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e: &a" + owned.getAmount(), "", "&a\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u0430\u043a\u0442\u0438\u0432\u0430\u0446\u0438\u0438");
                this.mapping.put(slot, (Object)owned.getMultiplier());
                this.inv.setItem(slot++, is);
            }
        }

        @Override
        public void onClick(ItemStack is, Player bukkitPlayer, int slot, ClickType clickType) {
            if (slot == 4) {
                this.parent.show(bukkitPlayer);
                return;
            }
            Multiplier mult = (Multiplier)this.mapping.get(slot);
            if (mult != null) {
                ConfirmMenu menu = new ConfirmMenu(this.inv, () -> {
                    this.player.getMultipliers().activate(mult);
                    this.update();
                    U.msg((CommandSender)bukkitPlayer, "&a\u041c\u043d\u043e\u0436\u0438\u0442\u0435\u043b\u044c " + mult.getText("&e", "&a") + " \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0430\u043a\u0442\u0438\u0432\u0438\u0440\u043e\u0432\u0430\u043d");
                }, "\u041f\u043e\u0434\u0442\u0432\u0435\u0440\u0436\u0434\u0435\u043d\u0438\u0435 \u0430\u043a\u0442\u0438\u0432\u0430\u0446\u0438\u0438 x" + mult.getMultiplier());
                menu.setBackOnConfirm(true);
                menu.setConfirmText("&a\u0410\u043a\u0442\u0438\u0432\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u043c\u043d\u043e\u0436\u0438\u0442\u0435\u043b\u044c", "&a\u043a\u043e\u0438\u043d\u043e\u0432 " + mult.getText("&e", "&a"), "", "&c \u0415\u0441\u043b\u0438 \u0432 \u0434\u0430\u043d\u043d\u044b\u0439 \u043c\u043e\u043c\u0435\u043d\u0442 \u0443 \u0432\u0430\u0441", "&c\u0430\u043a\u0442\u0438\u0432\u0438\u0440\u043e\u0432\u0430\u043d \u0434\u0440\u0443\u0433\u043e\u0439 \u043c\u043d\u043e\u0436\u0438\u0442\u0435\u043b\u044c,", "&c\u0442\u043e \u043e\u043d \u0431\u0443\u0434\u0435\u0442 \u0443\u0434\u0430\u043b\u0451\u043d");
                menu.show(bukkitPlayer);
            }
        }

        public Inventory getInventory() {
            return this.inv;
        }
    }

    private static class LevelingMenu
    implements IMenu {
        private static final int[] SLOTS = new int[]{0, 1, 2, 11, 20, 19, 18, 27, 36, 45, 46, 47, 48, 39, 30, 31, 32, 41, 50, 51, 52, 53, 44, 35, 26, 25, 24, 15, 6, 7, 8};
        private final Inventory inv;
        private final VPlayerMenu parent;
        private final VPlayer player;
        private int page = 0;
        private boolean hasNextPage;

        public LevelingMenu(VPlayer player, VPlayerMenu parent) {
            this.parent = parent;
            this.player = player;
            this.inv = Bukkit.createInventory((InventoryHolder)this, (int)54, (String)"\u0423\u0440\u043e\u0432\u0435\u043d\u044c VimeWorld");
            this.inv.setItem(4, Items.name(Material.BED, "&f\u2190 &e\u041d\u0430\u0437\u0430\u0434", new String[0]));
            int expToNextLevel = Leveling.getExpToNextLevel(player.getLevel());
            float progress = (float)player.getPartialExp() / (float)expToNextLevel;
            this.inv.setItem(13, Items.name(Material.WRITTEN_BOOK, "&e\u0422\u0435\u043a\u0443\u0449\u0438\u0439 \u0443\u0440\u043e\u0432\u0435\u043d\u044c: &f" + player.getLevel() + " (" + (int)(progress * 100.0f) + "%)", "&e\u041f\u0440\u043e\u0433\u0440\u0435\u0441\u0441: &f" + player.getPartialExp() + "&e/&f" + expToNextLevel, "&f[" + U.genBar(48, progress, '|', "&7", "&a") + "&f]"));
            this.update();
        }

        private void update() {
            int i;
            for (int slot : SLOTS) {
                this.inv.setItem(slot, null);
            }
            this.hasNextPage = true;
            int rewardTaken = this.getRewardTaken();
            ArrayList<String> lore = new ArrayList<String>();
            int max = this.page > 0 ? SLOTS.length - 1 : SLOTS.length;
            int level = LevelingMenu.getPageStartLevel(this.page);
            int n = i = this.page > 0 ? 1 : 0;
            while (i < max) {
                ItemStack is;
                if (LevelingRewards.REWARDS.size() <= ++level) {
                    this.hasNextPage = false;
                    break;
                }
                lore.addAll(LevelingRewards.REWARDS.get(level).getText());
                lore.add("");
                if (level <= this.player.getLevel()) {
                    if (level > rewardTaken) {
                        is = new ItemStack(Material.GOLD_BLOCK);
                        lore.add("&a\u041d\u0430\u0436\u043c\u0438\u0442\u0435, \u0447\u0442\u043e\u0431\u044b \u0437\u0430\u0431\u0440\u0430\u0442\u044c \u043d\u0430\u0433\u0440\u0430\u0434\u0443");
                    } else {
                        is = new ItemStack(Material.IRON_BLOCK);
                        lore.add("&7\u0412\u044b \u0443\u0436\u0435 \u043f\u043e\u043b\u0443\u0447\u0438\u043b\u0438 \u043d\u0430\u0433\u0440\u0430\u0434\u0443");
                    }
                } else {
                    is = new ItemStack(Material.COAL_BLOCK);
                    lore.add("&c\u0412\u0430\u0448 \u0443\u0440\u043e\u0432\u0435\u043d\u044c \u0441\u043b\u0438\u0448\u043a\u043e\u043c \u043c\u0430\u043b");
                }
                this.inv.setItem(SLOTS[i], Items.name(is, "&d&l\u041d\u0430\u0433\u0440\u0430\u0434\u0430 \u0437\u0430 " + level + " \u0443\u0440\u043e\u0432\u0435\u043d\u044c", lore));
                lore.clear();
                ++i;
            }
            if (this.page > 0) {
                this.inv.setItem(SLOTS[0], Items.name(Material.SIGN, "&f\u2190 &e\u041f\u0440\u0435\u0434\u044b\u0434\u0443\u0449\u0430\u044f \u0441\u0442\u0440\u0430\u043d\u0438\u0446\u0430", "&f\u0423\u0440\u043e\u0432\u043d\u0438: &e" + (LevelingMenu.getPageStartLevel(this.page - 1) + 1) + "&f...&e" + LevelingMenu.getPageStartLevel(this.page)));
            }
            if (this.hasNextPage) {
                if (level + 1 >= LevelingRewards.REWARDS.size()) {
                    this.inv.setItem(SLOTS[SLOTS.length - 1], Items.name(Material.PAPER, "&e\u041d\u0430\u0433\u0440\u0430\u0434\u044b \u043a\u043e\u043d\u0447\u0438\u043b\u0438\u0441\u044c", "&f\u041d\u043e \u0432\u044b \u0432\u0441\u0435 \u0440\u0430\u0432\u043d\u043e \u0441\u043c\u043e\u0436\u0435\u0442\u0435 \u0434\u0430\u043b\u044c\u0448\u0435", "&f\u043f\u0440\u043e\u043a\u0430\u0447\u0438\u0432\u0430\u0442\u044c \u0441\u0432\u043e\u0439 \u0443\u0440\u043e\u0432\u0435\u043d\u044c"));
                    this.hasNextPage = false;
                } else {
                    this.inv.setItem(SLOTS[SLOTS.length - 1], Items.name(Material.SIGN, "&e\u0421\u043b\u0435\u0434\u0443\u044e\u0449\u0430\u044f \u0441\u0442\u0440\u0430\u043d\u0438\u0446\u0430 &f\u2192", "&f\u0423\u0440\u043e\u0432\u043d\u0438: &e" + (level + 1) + "&f...&e" + LevelingMenu.getPageStartLevel(this.page + 2)));
                }
            }
        }

        private static int getPageStartLevel(int page) {
            if (page > 1) {
                return SLOTS.length - 1 + (page - 1) * (SLOTS.length - 2);
            }
            if (page == 1) {
                return SLOTS.length - 1;
            }
            return 0;
        }

        private int getRewardTaken() {
            String val = this.player.getMeta("lvl-reward");
            if (val != null) {
                try {
                    return Integer.parseInt(val);
                }
                catch (Exception ex) {
                    this.player.removeMeta("lvl-reward");
                }
            }
            return 0;
        }

        @Override
        public void onClick(ItemStack is, Player bukkitPlayer, int slot, ClickType clickType) {
            if (slot == 4) {
                this.parent.show(bukkitPlayer);
                return;
            }
            if (is != null) {
                if (this.page > 0 && slot == SLOTS[0]) {
                    --this.page;
                    this.update();
                    return;
                }
                if (this.hasNextPage && slot == SLOTS[SLOTS.length - 1]) {
                    ++this.page;
                    this.update();
                    return;
                }
                int levelClicked = -1;
                for (int i = 0; i < SLOTS.length; ++i) {
                    if (SLOTS[i] != slot) continue;
                    levelClicked = i;
                    break;
                }
                if (levelClicked == -1) {
                    return;
                }
                if ((levelClicked += LevelingMenu.getPageStartLevel(this.page) + (this.page > 0 ? 0 : 1)) > this.player.getLevel()) {
                    return;
                }
                if (levelClicked <= 0 || levelClicked >= LevelingRewards.REWARDS.size()) {
                    return;
                }
                int lastRewardTaken = this.getRewardTaken();
                if (levelClicked <= lastRewardTaken) {
                    return;
                }
                if (lastRewardTaken - levelClicked != -1) {
                    U.msg((CommandSender)bukkitPlayer, "&c\u041f\u043e\u0436\u0430\u043b\u0443\u0439\u0441\u0442\u0430, \u0437\u0430\u0431\u0438\u0440\u0430\u0439\u0442\u0435 \u043d\u0430\u0433\u0440\u0430\u0434\u044b \u043f\u043e \u043f\u043e\u0440\u044f\u0434\u043a\u0443...");
                    return;
                }
                LevelingRewards.LevelingReward reward = LevelingRewards.REWARDS.get(levelClicked);
                reward.accept(this.player);
                ArrayList<String> lore = new ArrayList<String>();
                lore.addAll(reward.getText());
                lore.add("");
                lore.add("&e&l\u041d\u0430\u0433\u0440\u0430\u0434\u0430 \u043f\u043e\u043b\u0443\u0447\u0435\u043d\u0430!");
                this.inv.setItem(slot, Items.name(Material.IRON_BLOCK, "&d&l\u041d\u0430\u0433\u0440\u0430\u0434\u0430 \u0437\u0430 " + levelClicked + " \u0443\u0440\u043e\u0432\u0435\u043d\u044c", lore));
                this.player.setMeta("lvl-reward", String.valueOf(levelClicked));
            }
        }

        public Inventory getInventory() {
            return this.inv;
        }
    }
}

