/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.bukkit.texteria.utils.ParsedTime
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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.xtrafrancyz.VimeNetwork.api.menu.ConfirmMenu;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.goals.Goal;
import net.xtrafrancyz.VimeNetwork.api.util.Invs;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.bukkit.texteria.utils.ParsedTime;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class GoalsInventory
implements IMenu {
    private static final ItemStack HELP_COMMON = Items.glow(Items.name(Material.PAPER, "&a\u0427\u0442\u043e \u044d\u0442\u043e?", "&f \u0417\u0434\u0435\u0441\u044c \u043f\u043e\u043a\u0430\u0437\u0430\u043d\u044b \u0430\u043a\u0442\u0438\u0432\u043d\u044b\u0435 \u0437\u0430\u0434\u0430\u043d\u0438\u044f", "&f\u0438 \u0441\u0442\u0435\u043f\u0435\u043d\u044c \u0438\u0445 \u0432\u044b\u043f\u043e\u043b\u043d\u0435\u043d\u0438\u044f."));
    private static final ItemStack HELP_OBTAIN = Items.glow(Items.name(Material.PAPER, "&a\u041a\u0430\u043a \u043f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u0437\u0430\u0434\u0430\u043d\u0438\u044f?", "&f \u0417\u0430\u0434\u0430\u043d\u0438\u044f \u0432\u044b\u0434\u0430\u044e\u0442\u0441\u044f \u0432 \u043b\u043e\u0431\u0431\u0438,", "&e2 \u0437\u0430\u0434\u0430\u043d\u0438\u044f&f \u043a\u0430\u0436\u0434\u044b\u0435 &e24 \u0447\u0430\u0441\u0430."));
    private static final ItemStack HELP_CANCEL = Items.glow(Items.name(Material.PAPER, "&a\u0417\u0430\u0447\u0435\u043c \u0438\u0445 \u043e\u0442\u043c\u0435\u043d\u044f\u0442\u044c?", "&f \u0422\u043e\u043b\u044c\u043a\u043e \u0434\u043b\u044f \u0442\u043e\u0433\u043e \u0447\u0442\u043e\u0431\u044b", "&f\u043d\u0435 \u043f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u043b\u043e\u0441\u044c \u043e\u043f\u043e\u0432\u0435\u0449\u0435\u043d\u0438\u0435", "&f\u043e\u0431 \u0430\u043a\u0442\u0438\u0432\u043d\u044b\u0445 \u0437\u0430\u0434\u0430\u043d\u0438\u044f\u0445.", "", "&f \u0412\u0437\u044f\u0442\u044c \u043d\u043e\u0432\u043e\u0435, \u0432\u043c\u0435\u0441\u0442\u043e \u0441\u0442\u0430\u0440\u043e\u0433\u043e,", "&f\u043d\u0435 \u043f\u043e\u043b\u0443\u0447\u0438\u0442\u0441\u044f."));
    private static final ItemStack CANCEL_ITEM = Items.name(new ItemStack(Material.INK_SACK, 1, 1), "&c\u041e\u0442\u043c\u0435\u043d\u0438\u0442\u044c \u0437\u0430\u0434\u0430\u043d\u0438\u0435", "&f\u0426\u0435\u043d\u0430: &c500 \u043a\u043e\u0438\u043d\u043e\u0432");
    private final Inventory inv;
    private final NetworkPlayer player;

    public GoalsInventory(NetworkPlayer player) {
        this.player = player;
        int lines = (player.getGoals().getActiveGoals().size() + player.getGoals().getCustomGoals().size() - 1) / 9 + 1;
        this.inv = Bukkit.createInventory((InventoryHolder)this, (int)(27 * lines), (String)"\u0417\u0430\u0434\u0430\u043d\u0438\u044f");
        this.inv.setItem(3, HELP_COMMON.clone());
        this.inv.setItem(4, HELP_OBTAIN.clone());
        this.inv.setItem(5, HELP_CANCEL.clone());
        this.update();
    }

    public void update() {
        String s;
        Object parsed;
        List<String> rewardText;
        AbstractList lore;
        List<String> text;
        Goal goal;
        Map.Entry<String, Goal> entry;
        long time = System.currentTimeMillis();
        Iterator<Map.Entry<String, Goal>> it = this.player.getGoals().getActiveGoals().entrySet().iterator();
        int i = 9;
        while (it.hasNext()) {
            entry = it.next();
            goal = entry.getValue();
            text = goal.getText(true);
            lore = new ArrayList<String>(text.size() + 4);
            if (text.size() > 1) {
                lore.addAll(text.subList(1, text.size()));
            }
            lore.add("");
            lore.add("&e\u0412\u044b\u043f\u043e\u043b\u043d\u0435\u043d\u043e: &f" + goal.getProgress() + "/" + goal.getGoal());
            rewardText = goal.getRewardText();
            if (rewardText != null) {
                lore.add("&e\u041d\u0430\u0433\u0440\u0430\u0434\u0430:");
                for (String line : rewardText) {
                    lore.add("  " + line);
                }
            }
            parsed = new ParsedTime(goal.finishTime * 1000L - time);
            s = "";
            if (((ParsedTime)parsed).days > 0) {
                s = ((ParsedTime)parsed).days + " \u0434. ";
            }
            s = ((ParsedTime)parsed).hours > 0 ? s + ((ParsedTime)parsed).hours + " \u0447." : s + ((ParsedTime)parsed).minutes + " \u043c.";
            lore.add("&e\u041e\u0441\u0442\u0430\u043b\u043e\u0441\u044c: &f" + s);
            this.inv.setItem(i, Items.name(goal.getItem(), text.get(0), (List<String>)lore));
            this.inv.setItem(i + 9, Items.nbt(CANCEL_ITEM).setString("goal", entry.getKey()).build());
            if (i % 9 == 8) {
                i += 18;
            }
            ++i;
        }
        it = this.player.getGoals().getCustomGoals().entrySet().iterator();
        i = 1;
        while (it.hasNext()) {
            entry = it.next();
            goal = entry.getValue();
            text = goal.getText(true);
            lore = new LinkedList();
            if (text.size() > 1) {
                lore.addAll(text.subList(1, text.size()));
            }
            lore.add("");
            lore.add("&e\u0412\u044b\u043f\u043e\u043b\u043d\u0435\u043d\u043e: &f" + goal.getProgress() + "/" + goal.getGoal());
            rewardText = goal.getRewardText();
            if (rewardText != null) {
                lore.add("&e\u041d\u0430\u0433\u0440\u0430\u0434\u0430:");
                for (String line : rewardText) {
                    lore.add("  " + line);
                }
            }
            if (goal.finishTime != -1L) {
                parsed = new ParsedTime(goal.finishTime * 1000L - time);
                s = "";
                if (((ParsedTime)parsed).days > 0) {
                    s = ((ParsedTime)parsed).days + " \u0434. ";
                }
                s = ((ParsedTime)parsed).hours > 0 ? s + ((ParsedTime)parsed).hours + " \u0447." : s + ((ParsedTime)parsed).minutes + " \u043c.";
                lore.add("&e\u041e\u0441\u0442\u0430\u043b\u043e\u0441\u044c: &f" + s);
            }
            this.inv.setItem(this.inv.getSize() - 9 - i, Items.name(goal.getItem(), text.get(0), (List<String>)lore));
            ++i;
        }
    }

    @Override
    public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
        String id;
        if (is.getTypeId() == CANCEL_ITEM.getTypeId() && !(id = Items.nbt(is).getString("goal")).isEmpty() && this.player.getCoins() >= 500) {
            ConfirmMenu menu = new ConfirmMenu(this.inv, () -> {
                this.player.getGoals().remove(id);
                this.player.takeCoins(500);
                this.inv.setItem(slot, null);
                this.inv.setItem(slot - 9, null);
            }, "\u041e\u0442\u043c\u0435\u043d\u0430 \u0437\u0430\u0434\u0430\u043d\u0438\u044f");
            menu.setConfirmText("&c\u041e\u0442\u043c\u0435\u043d\u0438\u0442\u044c \u0437\u0430\u0434\u0430\u043d\u0438\u0435", "&f\u0426\u0435\u043d\u0430: &c500 \u043a\u043e\u0438\u043d\u043e\u0432");
            Invs.forceOpen((HumanEntity)player, menu);
        }
    }

    public Inventory getInventory() {
        return this.inv;
    }
}

