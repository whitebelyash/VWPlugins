/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.Commons.player.Rank
 *  net.xtrafrancyz.VimeNetwork.api.VimeNetwork
 *  net.xtrafrancyz.VimeNetwork.api.menu.ConfirmMenu
 *  net.xtrafrancyz.VimeNetwork.api.menu.IMenu
 *  net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer
 *  net.xtrafrancyz.VimeNetwork.api.util.Items
 *  net.xtrafrancyz.VimeNetwork.api.util.Items$Namer
 *  net.xtrafrancyz.VimeNetwork.api.util.Reflect
 *  net.xtrafrancyz.VimeNetwork.api.util.U
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.SkyWars.menu;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.SkyWars.PlayerInfo;
import net.xtrafrancyz.SkyWars.SkyWars;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.menu.ConfirmMenu;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.Reflect;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class MicroUpgradesMenu
implements IMenu {
    private static final List<Upgrade> UPGRADES = new ArrayList<Upgrade>();
    private Inventory inv;
    private PlayerInfo player;
    private NetworkPlayer networkPlayer;

    public MicroUpgradesMenu(PlayerInfo player) {
        this.player = player;
        this.networkPlayer = VimeNetwork.getPlayer((Player)player.player);
        this.inv = Bukkit.createInventory((InventoryHolder)this, (int)36, (String)"\u041c\u0435\u043d\u044e \u043c\u0438\u043a\u0440\u043e\u043f\u0440\u043e\u043a\u0430\u0447\u0435\u043a");
        UPGRADES.forEach(this::updateItem);
    }

    private void updateItem(Upgrade upgrade) {
        Level nextLevel;
        Level level;
        ItemStack is = upgrade.item.clone();
        ArrayList<String> lore = new ArrayList<String>(5);
        String name = upgrade.name;
        int levelNumber = upgrade.levelGetter.apply(this.player);
        if (levelNumber == -1) {
            level = null;
            nextLevel = upgrade.levels.get(0);
        } else if (levelNumber == -2) {
            levelNumber = upgrade.levels.size() - 1;
            level = upgrade.levels.get(levelNumber);
            nextLevel = null;
        } else {
            level = upgrade.levels.get(levelNumber);
            Level level2 = nextLevel = upgrade.levels.size() <= levelNumber + 1 ? null : upgrade.levels.get(levelNumber + 1);
        }
        if (level == null && nextLevel == null) {
            this.inv.setItem(upgrade.slot, Items.name((Material)Material.OBSIDIAN, (String)"&7\u0414\u0430\u0439\u0442\u0435 \u043c\u043d\u0435 \u0441\u043f\u043e\u043a\u043e\u0439\u043d\u043e \u0443\u043c\u0435\u0440\u0435\u0442\u044c...", (String[])new String[0]));
            return;
        }
        if (level == null) {
            name = "&6" + name;
            Collections.addAll(lore, nextLevel.description);
            lore.add("");
            if (upgrade.rank != Rank.PLAYER) {
                lore.add("&5\u0414\u043e\u0441\u0442\u0443\u043f\u043d\u043e \u0442\u043e\u043b\u044c\u043a\u043e \u0434\u043b\u044f " + upgrade.rank.getDisplayName() + "&5 \u0438 \u0432\u044b\u0448\u0435");
                lore.add("");
            }
            if (!this.networkPlayer.getRank().has(upgrade.rank)) {
                lore.add("&c\u0412\u0430\u043c \u043d\u0443\u0436\u0435\u043d " + upgrade.rank.getDisplayName() + "&c, \u0447\u0442\u043e\u0431\u044b \u043a\u0443\u043f\u0438\u0442\u044c");
                lore.add("&c\u044d\u0442\u0443 \u043f\u0440\u043e\u043a\u0430\u0447\u043a\u0443.");
            } else if (nextLevel.price <= this.networkPlayer.getCoins()) {
                lore.add("&a\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u043f\u043e\u043a\u0443\u043f\u043a\u0438");
                lore.add("&e\u0426\u0435\u043d\u0430: &a" + U.pluralsCoins((int)nextLevel.price));
            } else {
                lore.add("&6\u0414\u043b\u044f \u043f\u043e\u043a\u0443\u043f\u043a\u0438 \u043d\u0435\u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u043a\u043e\u0438\u043d\u043e\u0432");
                lore.add("&e\u0426\u0435\u043d\u0430: &c" + U.pluralsCoins((int)nextLevel.price));
            }
        } else if (nextLevel != null) {
            name = "&a" + name + " &l" + Items.Namer.ROMAN_NUMBERS[levelNumber];
            Collections.addAll(lore, level.description);
            lore.add("");
            lore.add("&9\u0421\u043b\u0435\u0434\u0443\u044e\u0449\u0438\u0439 \u0443\u0440\u043e\u0432\u0435\u043d\u044c:");
            Collections.addAll(lore, nextLevel.description);
            lore.add("");
            if (upgrade.rank != Rank.PLAYER) {
                lore.add("&5\u0414\u043e\u0441\u0442\u0443\u043f\u043d\u043e \u0442\u043e\u043b\u044c\u043a\u043e \u0434\u043b\u044f " + upgrade.rank.getDisplayName() + "&5 \u0438 \u0432\u044b\u0448\u0435");
                lore.add("");
            }
            if (!this.networkPlayer.getRank().has(upgrade.rank)) {
                lore.add("&c\u0412\u0430\u043c \u043d\u0443\u0436\u0435\u043d " + upgrade.rank.getDisplayName() + "&c, \u0447\u0442\u043e\u0431\u044b \u0443\u043b\u0443\u0447\u0448\u0438\u0442\u044c");
                lore.add("&c\u044d\u0442\u0443 \u043f\u0440\u043e\u043a\u0430\u0447\u043a\u0443.");
            } else if (nextLevel.price <= this.networkPlayer.getCoins()) {
                lore.add("&a\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u0443\u043b\u0443\u0447\u0448\u0435\u043d\u0438\u044f");
                lore.add("&e\u0426\u0435\u043d\u0430: &a" + U.pluralsCoins((int)nextLevel.price));
            } else {
                lore.add("&6\u0414\u043b\u044f \u0443\u043b\u0443\u0447\u0448\u0435\u043d\u0438\u044f \u043d\u0435\u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u043a\u043e\u0438\u043d\u043e\u0432");
                lore.add("&e\u0426\u0435\u043d\u0430: &c" + U.pluralsCoins((int)nextLevel.price));
            }
        } else {
            name = "&a" + name + " &l" + Items.Namer.ROMAN_NUMBERS[levelNumber];
            Collections.addAll(lore, level.description);
            lore.add("");
            if (upgrade.rank != Rank.PLAYER) {
                lore.add("&5\u0414\u043e\u0441\u0442\u0443\u043f\u043d\u043e \u0442\u043e\u043b\u044c\u043a\u043e \u0434\u043b\u044f " + upgrade.rank.getDisplayName() + "&5 \u0438 \u0432\u044b\u0448\u0435");
                lore.add("");
            }
            lore.add("&9\u041c\u0430\u043a\u0441\u0438\u043c\u0430\u043b\u044c\u043d\u044b\u0439 \u0443\u0440\u043e\u0432\u0435\u043d\u044c");
        }
        this.inv.setItem(upgrade.slot, Items.name((ItemStack)is, (String)name, lore));
    }

    public void onClick(ItemStack is, Player bukkitPlayer, int slot, ClickType clickType) {
        Upgrade upgrade = null;
        for (Upgrade up : UPGRADES) {
            if (up.slot != slot) continue;
            upgrade = up;
            break;
        }
        if (upgrade == null) {
            return;
        }
        if (!this.networkPlayer.getRank().has(upgrade.rank)) {
            return;
        }
        Level nextLevel = null;
        int levelNumber = upgrade.levelGetter.apply(this.player);
        if (levelNumber == -1) {
            nextLevel = upgrade.levels.get(0);
        } else if (levelNumber >= 0) {
            Level level = nextLevel = upgrade.levels.size() <= levelNumber + 1 ? null : upgrade.levels.get(levelNumber + 1);
        }
        if (nextLevel == null) {
            return;
        }
        if (nextLevel.price <= this.networkPlayer.getCoins()) {
            Level nextLevel0 = nextLevel;
            Upgrade upgrade0 = upgrade;
            ConfirmMenu confirm = new ConfirmMenu(this.inv, () -> {
                this.networkPlayer.takeCoins(nextLevel0.price);
                VimeNetwork.metrics().add("sw.buy.micro", nextLevel0.price);
                upgrade0.levelSetter.accept(this.player, nextLevel0);
                UPGRADES.forEach(this::updateItem);
                SkyWars.instance().repository.savePlayer(this.player);
            }, upgrade.name + " " + Items.Namer.ROMAN_NUMBERS[nextLevel.level]);
            confirm.setConfirmText("&a\u041f\u043e\u0434\u0442\u0432\u0435\u0440\u0434\u0438\u0442\u044c \u043f\u043e\u043a\u0443\u043f\u043a\u0443", new String[]{"", "&f" + upgrade.name + " &l" + Items.Namer.ROMAN_NUMBERS[nextLevel.level], "", "&f\u0426\u0435\u043d\u0430: &e" + U.pluralsCoins((int)nextLevel.price)});
            confirm.show(bukkitPlayer);
        } else {
            VimeNetwork.texteria().showInsufficientlyCoins(bukkitPlayer);
        }
    }

    public Inventory getInventory() {
        return this.inv;
    }

    static {
        UPGRADES.add(new Upgrade("\u0412\u043e\u0441\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u0438\u0435 \u0441\u0442\u0440\u0435\u043b", Material.ARROW, 10, "&7\u041f\u0440\u0438 \u0432\u044b\u0441\u0442\u0440\u0435\u043b\u0435 \u0441 \u0448\u0430\u043d\u0441\u043e\u043c &f{0}%&7 \u0441\u0442\u0440\u0435\u043b\u0430", "&7\u0432\u0435\u0440\u043d\u0435\u0442\u0441\u044f \u043a \u0432\u0430\u043c \u0432 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u044c.").addLevel(5000, 5).addLevel(10000, 10).addLevel(15000, 15).addLevel(20000, 20).addLevel(25000, 25).mapUpgradeFieldToLevelValueIndex("arrow", 0));
        UPGRADES.add(new Upgrade("\u0421\u0442\u0440\u0435\u043b\u0430 \u0431\u043b\u0435\u0439\u0437\u0430", Material.BLAZE_POWDER, 11, "&7\u041f\u0440\u0438 \u0432\u044b\u0441\u0442\u0440\u0435\u043b\u0435 \u0441 \u0448\u0430\u043d\u0441\u043e\u043c &f{0}%&7 \u0432\u0430\u0448\u0430", "&7\u0441\u0442\u0440\u0435\u043b\u0430 \u0431\u0443\u0434\u0435\u0442 \u0433\u043e\u0440\u044f\u0449\u0435\u0439.").addLevel(5000, 2).addLevel(10000, 4).addLevel(15000, 6).addLevel(20000, 8).addLevel(25000, 10).mapUpgradeFieldToLevelValueIndex("blazeArrow", 0));
        UPGRADES.add(new Upgrade("\u0414\u0436\u0430\u0433\u0433\u0435\u0440\u043d\u0430\u0443\u0442", Material.DIAMOND_SWORD, 12, "&7\u041f\u043e\u0441\u043b\u0435 \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u0430 \u0438\u0433\u0440\u043e\u043a\u0430 \u0432\u044b \u043f\u043e\u043b\u0443\u0447\u0438\u0442\u0435", "&7\u044d\u0444\u0444\u0435\u043a\u0442 \u0420\u0435\u0433\u0435\u043d\u0435\u0440\u0430\u0446\u0438\u0438 I \u043d\u0430 &f{0} \u0441\u0435\u043a.").addLevel(5000, 2).addLevel(10000, 4).addLevel(15000, 6).addLevel(20000, 8).addLevel(25000, 10).mapUpgradeFieldToLevelValueIndex("juggernaut", 0));
        UPGRADES.add(new Upgrade("\u0423\u0441\u043a\u043e\u0440\u0435\u043d\u0438\u0435", Material.GOLD_PICKAXE, 13, "&7\u041f\u0440\u0438 \u0441\u0442\u0430\u0440\u0442\u0435 \u0438\u0433\u0440\u044b \u0432\u044b \u043f\u043e\u043b\u0443\u0447\u0438\u0442\u0435 \u044d\u0444\u0444\u0435\u043a\u0442", "&7\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u0438 \u043b\u043e\u043c\u0430\u043d\u0438\u044f I \u043d\u0430 &f{0} \u0441\u0435\u043a.").addLevel(5000, 5).addLevel(10000, 10).addLevel(15000, 15).addLevel(20000, 20).addLevel(25000, 25).mapUpgradeFieldToLevelValueIndex("speedBoost", 0));
        UPGRADES.add(new Upgrade("\u0421\u043e\u043f\u0440\u043e\u0442\u0438\u0432\u043b\u0435\u043d\u0438\u0435 \u0443\u0440\u043e\u043d\u0443", Material.CHAINMAIL_CHESTPLATE, 14, "&7\u041f\u0440\u0438 \u0441\u0442\u0430\u0440\u0442\u0435 \u0438\u0433\u0440\u044b \u0432\u044b \u043f\u043e\u043b\u0443\u0447\u0438\u0442\u0435 \u044d\u0444\u0444\u0435\u043a\u0442", "&7\u0421\u043e\u043f\u0440\u043e\u0442\u0438\u0432\u043b\u0435\u043d\u0438\u044f \u0443\u0440\u043e\u043d\u0443 I \u043d\u0430 &f{0} \u0441\u0435\u043a.").addLevel(10000, 5).addLevel(15000, 10).addLevel(20000, 15).mapUpgradeFieldToLevelValueIndex("resistance", 0));
        UPGRADES.add(new Upgrade("\u0416\u0438\u0432\u0447\u0438\u043a", new ItemStack(Material.INK_SACK, 1, 1), 15, "&7\u0428\u0430\u043d\u0441 \u0432\u044b\u043f\u0430\u0434\u0435\u043d\u0438\u044f \u0434\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0433\u043e \u0441\u0435\u0440\u0434\u0435\u0447\u043a\u0430", "&7\u0438\u0437 \u0431\u043b\u043e\u043a\u0430 \u0440\u0435\u0434\u0441\u0442\u043e\u0443\u043d\u0430: &f{0}%&7.").addLevel(10000, 55).addLevel(15000, 60).addLevel(20000, 65).mapUpgradeFieldToLevelValueIndex("redstoneHeart", 0));
        UPGRADES.add(new Upgrade("\u042d\u043d\u0434\u0435\u0440\u043c\u0435\u043d", Material.ENDER_PEARL, 16, "&7\u0423\u043c\u0435\u043d\u044c\u0448\u0430\u0435\u0442 \u0443\u0440\u043e\u043d \u043e\u0442 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u044f \u0436\u0435\u043c\u0447\u0443\u0433\u0430", "&7\u044d\u043d\u0434\u0435\u0440\u0430 \u043d\u0430 &f{0}%&7.").addLevel(5000, 20).addLevel(10000, 40).addLevel(15000, 60).addLevel(20000, 80).addLevel(25000, 100).mapUpgradeFieldToLevelValueIndex("enderman", 0));
        UPGRADES.add(new Upgrade("\u0421\u0442\u0440\u043e\u0438\u0442\u0435\u043b\u044c", Material.COBBLESTONE, 19, "&7\u041f\u0440\u0438 \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043a\u0435 \u0431\u043b\u043e\u043a\u0430, \u0441 \u0448\u0430\u043d\u0441\u043e\u043c &f{0}%", "&7\u0432\u044b \u043c\u043e\u0436\u0435\u0442\u0435 \u0432\u0435\u0440\u043d\u0443\u0442\u044c \u043f\u043e\u0441\u0442\u0430\u0432\u043b\u0435\u043d\u043d\u044b\u0439 \u0431\u043b\u043e\u043a.").addLevel(5000, 5).addLevel(10000, 10).addLevel(15000, 15).addLevel(20000, 20).addLevel(25000, 25).mapUpgradeFieldToLevelValueIndex("builder", 0));
        UPGRADES.add(new Upgrade("\u0427\u0430\u0440\u043e\u0432\u0430\u043b\u044c\u0449\u0438\u043a", Material.EXP_BOTTLE, 20, "&7\u041f\u0440\u0438 \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u0435 \u0438\u0433\u0440\u043e\u043a\u0430 \u0432\u044b \u043f\u043e\u043b\u0443\u0447\u0430\u0435\u0442\u0435", "&f{0} {1}&7 \u043e\u043f\u044b\u0442\u0430.").setRank(Rank.VIP).addLevel(5000, 1, "\u0443\u0440\u043e\u0432\u0435\u043d\u044c").addLevel(10000, 2, "\u0443\u0440\u043e\u0432\u043d\u044f").addLevel(15000, 3, "\u0443\u0440\u043e\u0432\u043d\u044f").mapUpgradeFieldToLevelValueIndex("enchanter", 0));
        UPGRADES.add(new Upgrade("\u041f\u043e\u0436\u0438\u0440\u0430\u0442\u0435\u043b\u044c \u043f\u043b\u043e\u0442\u0438", Material.ROTTEN_FLESH, 21, "&7\u041f\u0440\u0438 \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u0435 \u0438\u0433\u0440\u043e\u043a\u0430 \u0432\u044b \u043f\u043e\u043b\u043d\u043e\u0441\u0442\u044c\u044e", "&7\u0432\u043e\u0441\u0441\u0442\u0430\u043d\u0430\u0432\u043b\u0438\u0432\u0430\u0435\u0442\u0435 \u0441\u0432\u043e\u0439 \u0433\u043e\u043b\u043e\u0434.").setRank(Rank.PREMIUM).addLevel(50000, 1).mapUpgradeFieldToLevelValueIndex("zombie", 0));
        UPGRADES.add(new Upgrade("\u0421\u0442\u0430\u0440\u0430\u0442\u0435\u043b\u044c", Material.GOLDEN_APPLE, 22, "&7\u041f\u0440\u0438 \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u0435 \u0438\u0433\u0440\u043e\u043a\u0430 \u0441 \u0448\u0430\u043d\u0441\u043e\u043c &f{0}%", "&7\u0432\u044b \u043f\u043e\u043b\u0443\u0447\u0430\u0435\u0442\u0435 \u0437\u043e\u043b\u043e\u0442\u043e\u0435 \u044f\u0431\u043b\u043e\u043a\u043e.").setRank(Rank.PREMIUM).addLevel(5000, 3).addLevel(10000, 7).addLevel(15000, 11).addLevel(20000, 15).addLevel(25000, 19).addLevel(30000, 23).addLevel(30000, 27).addLevel(30000, 30).mapUpgradeFieldToLevelValueIndex("goldenApple", 0));
    }

    private static final class Level {
        public int level;
        public int price;
        public String[] description;
        public Object[] descValues;

        public Level(int level, int price, String[] description, Object[] descValues) {
            this.level = level;
            this.price = price;
            this.descValues = descValues;
            this.description = Arrays.copyOf(description, description.length);
            for (int l = 0; l < this.description.length; ++l) {
                for (int i = 0; i < descValues.length; ++i) {
                    this.description[l] = this.description[l].replace("{" + i + "}", descValues[i].toString());
                }
            }
        }
    }

    private static class Upgrade {
        public String name;
        public ItemStack item;
        public int slot;
        public String[] description;
        public Function<PlayerInfo, Integer> levelGetter;
        public BiConsumer<PlayerInfo, Level> levelSetter;
        public List<Level> levels = new ArrayList<Level>();
        public Rank rank = Rank.PLAYER;

        public Upgrade(String name, Material type, int slot, String ... description) {
            this(name, new ItemStack(type), slot, description);
        }

        public Upgrade(String name, ItemStack item, int slot, String ... description) {
            this.name = name;
            this.item = item;
            this.slot = slot;
            this.description = description;
        }

        public Upgrade mapUpgradeFieldToLevelValueIndex(String fieldName, int index) {
            MethodHandle setter;
            MethodHandle getter;
            Field field = Reflect.findField(PlayerInfo.Upgrades.class, (String)fieldName);
            if (field == null) {
                throw new RuntimeException("Field " + fieldName + " not found in PlayerInfo.Upgrades class");
            }
            try {
                getter = MethodHandles.lookup().unreflectGetter(field);
                setter = MethodHandles.lookup().unreflectSetter(field);
            }
            catch (Exception e) {
                e.printStackTrace();
                return this;
            }
            this.levelGetter = player -> {
                int value = 0;
                try {
                    value = getter.invokeExact(player.upgrades);
                }
                catch (Throwable t) {
                    t.printStackTrace();
                }
                for (int i = 0; i < this.levels.size(); ++i) {
                    int lvlValue = (Integer)this.levels.get((int)i).descValues[index];
                    if (lvlValue == value) {
                        return i;
                    }
                    if (lvlValue <= value) continue;
                    return i - 1;
                }
                return -2;
            };
            this.levelSetter = (player, level) -> {
                try {
                    setter.invokeExact(player.upgrades, (Integer)level.descValues[index]);
                }
                catch (Throwable t) {
                    t.printStackTrace();
                }
            };
            return this;
        }

        public Upgrade setLevelGetter(Function<PlayerInfo, Integer> levelGetter) {
            this.levelGetter = levelGetter;
            return this;
        }

        public Upgrade setLevelSetter(BiConsumer<PlayerInfo, Level> levelSetter) {
            this.levelSetter = levelSetter;
            return this;
        }

        public Upgrade setRank(Rank rank) {
            this.rank = rank;
            return this;
        }

        public Upgrade addLevel(int price, Object ... descValues) {
            return this.addLevel(price, this.description, descValues);
        }

        public Upgrade addLevel(int price, String[] description, Object ... descValues) {
            this.levels.add(new Level(this.levels.size(), price, description, descValues));
            return this;
        }
    }
}

