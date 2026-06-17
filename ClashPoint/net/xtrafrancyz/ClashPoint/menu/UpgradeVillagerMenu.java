/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.menu.ConfirmMenu
 *  net.xtrafrancyz.VimeNetwork.api.menu.IMenu
 *  net.xtrafrancyz.VimeNetwork.api.util.Items
 *  net.xtrafrancyz.VimeNetwork.api.util.U
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.ClashPoint.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.xtrafrancyz.ClashPoint.menu.shop.Currency;
import net.xtrafrancyz.ClashPoint.object.CPTeam;
import net.xtrafrancyz.ClashPoint.object.PlayerInfo;
import net.xtrafrancyz.ClashPoint.object.TeamPerk;
import net.xtrafrancyz.VimeNetwork.api.menu.ConfirmMenu;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class UpgradeVillagerMenu
implements IMenu {
    private static final Map<TeamPerk, List<Level>> UPGRADES = new HashMap<TeamPerk, List<Level>>();
    private Inventory inv = Bukkit.createInventory((InventoryHolder)this, (int)9, (String)"\u0423\u043b\u0443\u0447\u0448\u0435\u043d\u0438\u044f \u0431\u0430\u0437\u044b");
    private CPTeam team;

    public UpgradeVillagerMenu(PlayerInfo player) {
        this.team = player.team;
        this.update();
    }

    private void update() {
        int index = 0;
        for (TeamPerk perk : TeamPerk.values()) {
            int level = this.team.getPerkLevel(perk);
            List<Level> upgrades = UPGRADES.get((Object)perk);
            ArrayList<String> lore = new ArrayList<String>();
            if (level == upgrades.size()) {
                lore.addAll(upgrades.get((int)(level - 1)).text);
                lore.add("");
                lore.add("&e\u041f\u043e\u043b\u043d\u043e\u0441\u0442\u044c\u044e \u043f\u0440\u043e\u043a\u0430\u0447\u0430\u043d\u043e");
            } else {
                Level upgrade = upgrades.get(level);
                lore.addAll(upgrade.text);
                lore.add("");
                lore.add("&d\u0426\u0435\u043d\u0430: " + Currency.DIAMOND.color + upgrade.price + " " + U.plurals((int)upgrade.price, (String)Currency.DIAMOND.form1, (String)Currency.DIAMOND.form2, (String)Currency.DIAMOND.form3));
            }
            this.inv.setItem(index++, Items.name((ItemStack)perk.is, (String)("&b&l" + perk.name), lore));
        }
    }

    public void onClick(ItemStack is, Player bukkitPlayer, int slot, ClickType clickType) {
        TeamPerk[] values = TeamPerk.values();
        if (slot >= 0 && slot < values.length) {
            List<Level> upgrades;
            TeamPerk perk = values[slot];
            int level = this.team.getPerkLevel(perk);
            if (level >= (upgrades = UPGRADES.get((Object)perk)).size()) {
                return;
            }
            Level upgrade = upgrades.get(level);
            PlayerInfo player = PlayerInfo.get(bukkitPlayer);
            if (player.countResources(Currency.DIAMOND.material) < upgrade.price) {
                U.msg((CommandSender)bukkitPlayer, (String[])new String[]{"&c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432 \u0434\u043b\u044f \u043f\u043e\u043a\u0443\u043f\u043a\u0438"});
                return;
            }
            ConfirmMenu menu = new ConfirmMenu(this.inv, () -> {
                if (this.team.getPerkLevel(perk) != level) {
                    return;
                }
                player.takeResources(Currency.DIAMOND.material, upgrade.price);
                this.team.upgradePerk(perk);
                this.update();
            }, "\u041f\u043e\u0434\u0442\u0432\u0435\u0440\u0436\u0434\u0435\u043d\u0438\u0435 \u0443\u043b\u0443\u0447\u0448\u0435\u043d\u0438\u044f");
            menu.setBackOnConfirm(true);
            menu.show(bukkitPlayer);
        }
    }

    public Inventory getInventory() {
        return this.inv;
    }

    static {
        UPGRADES.put(TeamPerk.PERSONAL_CHEST_CAPACITY, Arrays.asList(new Level(3, "&7\u0423\u0432\u0435\u043b\u0438\u0447\u0438\u0432\u0430\u0435\u0442 \u0432\u043c\u0435\u0441\u0442\u0438\u043c\u043e\u0441\u0442\u044c \u043f\u0435\u0440\u0441\u043e\u043d\u0430\u043b\u044c\u043d\u043e\u0433\u043e", "&7\u0441\u0443\u043d\u0434\u0443\u043a\u0430 \u0434\u043e &f18 \u0441\u043b\u043e\u0442\u043e\u0432"), new Level(7, "&7\u0423\u0432\u0435\u043b\u0438\u0447\u0438\u0432\u0430\u0435\u0442 \u0432\u043c\u0435\u0441\u0442\u0438\u043c\u043e\u0441\u0442\u044c \u043f\u0435\u0440\u0441\u043e\u043d\u0430\u043b\u044c\u043d\u043e\u0433\u043e", "&7\u0441\u0443\u043d\u0434\u0443\u043a\u0430 \u0434\u043e &f27 \u0441\u043b\u043e\u0442\u043e\u0432")));
        UPGRADES.put(TeamPerk.RP_REGENERATION, Arrays.asList(new Level(2, "&7\u0412\u043a\u043b\u044e\u0447\u0430\u0435\u0442 \u0440\u0435\u0433\u0435\u043d\u0435\u0440\u0430\u0446\u0438\u044e \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u044f", "&7\u0442\u043e\u0447\u0435\u043a \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432: &f1 \u0445\u043f/\u0441\u0435\u043a"), new Level(5, "&7\u0423\u0432\u0435\u043b\u0438\u0447\u0438\u0432\u0430\u0435\u0442 \u0440\u0435\u0433\u0435\u043d\u0435\u0440\u0430\u0446\u0438\u044e \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u044f", "&7\u0442\u043e\u0447\u0435\u043a \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432: &f3 \u0445\u043f/\u0441\u0435\u043a")));
        UPGRADES.put(TeamPerk.RP_HEALTH, Arrays.asList(new Level(2, "&7\u0423\u0432\u0435\u043b\u0438\u0447\u0438\u0432\u0430\u0435\u0442 \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u0435 \u0442\u043e\u0447\u0435\u043a", "&7\u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432 \u0434\u043e &f75"), new Level(5, "&7\u0423\u0432\u0435\u043b\u0438\u0447\u0438\u0432\u0430\u0435\u0442 \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u0435 \u0442\u043e\u0447\u0435\u043a", "&7\u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432 \u0434\u043e &f100")));
        UPGRADES.put(TeamPerk.RP_RATE, Arrays.asList(new Level(10, "&7\u0423\u0432\u0435\u043b\u0438\u0447\u0438\u0432\u0430\u0435\u0442 \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0433\u0435\u043d\u0435\u0440\u0430\u0446\u0438\u0438", "&7\u0436\u0435\u043b\u0435\u0437\u0430 \u043d\u0430 &f25%"), new Level(20, "&7\u0423\u0432\u0435\u043b\u0438\u0447\u0438\u0432\u0430\u0435\u0442 \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0433\u0435\u043d\u0435\u0440\u0430\u0446\u0438\u0438", "&7\u0436\u0435\u043b\u0435\u0437\u0430 \u043d\u0430 &f50%")));
        UPGRADES.put(TeamPerk.RP_DEBUFF, Arrays.asList(new Level(5, "&7\u041d\u0430\u043a\u043b\u0430\u0434\u044b\u0432\u0430\u0435\u0442 \u043d\u0430 \u0432\u0440\u0430\u0433\u043e\u0432 \u0432\u043e\u0437\u043b\u0435", "&7\u0442\u043e\u0447\u0435\u043a \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432 \u044d\u0444\u0444\u0435\u043a\u0442\u044b:", "&9  \u0421\u043b\u0430\u0431\u043e\u0441\u0442\u044c I"), new Level(5, "&7\u041d\u0430\u043a\u043b\u0430\u0434\u044b\u0432\u0430\u0435\u0442 \u043d\u0430 \u0432\u0440\u0430\u0433\u043e\u0432 \u0432\u043e\u0437\u043b\u0435", "&7\u0442\u043e\u0447\u0435\u043a \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432 \u044d\u0444\u0444\u0435\u043a\u0442\u044b:", "&9  \u0421\u043b\u0430\u0431\u043e\u0441\u0442\u044c I", "&9  \u041c\u0435\u0434\u043b\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c I"), new Level(10, "&7\u041d\u0430\u043a\u043b\u0430\u0434\u044b\u0432\u0430\u0435\u0442 \u043d\u0430 \u0432\u0440\u0430\u0433\u043e\u0432 \u0432\u043e\u0437\u043b\u0435", "&7\u0442\u043e\u0447\u0435\u043a \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432 \u044d\u0444\u0444\u0435\u043a\u0442\u044b:", "&9  \u0421\u043b\u0430\u0431\u043e\u0441\u0442\u044c II", "&9  \u041c\u0435\u0434\u043b\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c I"), new Level(10, "&7\u041d\u0430\u043a\u043b\u0430\u0434\u044b\u0432\u0430\u0435\u0442 \u043d\u0430 \u0432\u0440\u0430\u0433\u043e\u0432 \u0432\u043e\u0437\u043b\u0435", "&7\u0442\u043e\u0447\u0435\u043a \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432 \u044d\u0444\u0444\u0435\u043a\u0442\u044b:", "&9  \u0421\u043b\u0430\u0431\u043e\u0441\u0442\u044c II", "&9  \u041c\u0435\u0434\u043b\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c II")));
        UPGRADES.put(TeamPerk.RP_THORNS, Arrays.asList(new Level(5, "&7\u0422\u043e\u0447\u043a\u0430 \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432 \u0431\u0443\u0434\u0435\u0442 \u0432\u043e\u0437\u0432\u0440\u0430\u0449\u0430\u0442\u044c", "&f20%&7 \u0443\u0440\u043e\u043d\u0430 \u043d\u0430\u043f\u0430\u0434\u0430\u044e\u0449\u0435\u043c\u0443"), new Level(10, "&7\u0422\u043e\u0447\u043a\u0430 \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432 \u0431\u0443\u0434\u0435\u0442 \u0432\u043e\u0437\u0432\u0440\u0430\u0449\u0430\u0442\u044c", "&f40%&7 \u0443\u0440\u043e\u043d\u0430 \u043d\u0430\u043f\u0430\u0434\u0430\u044e\u0449\u0435\u043c\u0443"), new Level(15, "&7\u0422\u043e\u0447\u043a\u0430 \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432 \u0431\u0443\u0434\u0435\u0442 \u0432\u043e\u0437\u0432\u0440\u0430\u0449\u0430\u0442\u044c", "&f60%&7 \u0443\u0440\u043e\u043d\u0430 \u043d\u0430\u043f\u0430\u0434\u0430\u044e\u0449\u0435\u043c\u0443")));
    }

    private static class Level {
        public int price;
        public List<String> text;

        public Level(int price, String ... text) {
            this.price = price;
            this.text = Arrays.asList(text);
        }
    }
}

