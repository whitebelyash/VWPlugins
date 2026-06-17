/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  gnu.trove.iterator.TIntObjectIterator
 *  gnu.trove.list.linked.TIntLinkedList
 *  gnu.trove.map.hash.TIntObjectHashMap
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.impl.player;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.linked.TIntLinkedList;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.logging.Level;
import net.xtrafrancyz.Core.network.packet.Packet5PlayerCoinsChange;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.VTexteria;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public class VCoins {
    private final VNPlugin plugin;
    private volatile boolean waiting = false;

    public VCoins(VNPlugin plugin) {
        this.plugin = plugin;
        this.waiting = true;
        Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)plugin, this::flush, 200L, 200L);
        Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)plugin, () -> {
            long time = System.currentTimeMillis();
            for (VPlayer pi : VPlayer.PLAYERS.values()) {
                if (!pi.multipliers.isActivated() || pi.multipliers.getExtraEndTime() >= time) continue;
                pi.multipliers.deactivate();
                VTexteria.showCoins(pi);
            }
        }, 100L, 100L);
    }

    private void flush() {
        this.waiting = false;
        int total = 0;
        TIntObjectHashMap map = new TIntObjectHashMap();
        for (VPlayer player : VPlayer.PLAYERS.values()) {
            if (player.coinsAddBuffer == 0) continue;
            total += player.coinsAddBuffer;
            TIntLinkedList list = (TIntLinkedList)map.get(player.coinsAddBuffer);
            if (list == null) {
                list = new TIntLinkedList();
                map.put(player.coinsAddBuffer, (Object)list);
            }
            list.add(player.id);
            player.coinsAddBuffer = 0;
        }
        VimeNetwork.metrics().add("coins.added", total);
        this.waiting = true;
        TIntObjectIterator it = map.iterator();
        while (it.hasNext()) {
            it.advance();
            if (this.plugin.core.isEnabled()) {
                this.plugin.core.sendPacket(new Packet5PlayerCoinsChange(((TIntLinkedList)it.value()).toArray(), it.key()));
                continue;
            }
            String ids = ((TIntLinkedList)it.value()).toString();
            this.plugin.mysql.query("UPDATE users SET coins=coins+" + it.key() + " WHERE id IN (" + ids.substring(1, ids.length() - 1) + ")");
        }
        map.clear();
    }

    public void saveNow(VPlayer player) {
        if (!this.waiting) {
            return;
        }
        if (player.coinsAddBuffer > 0) {
            int amount = player.coinsAddBuffer;
            player.coinsAddBuffer = 0;
            if (this.plugin.core.isEnabled()) {
                this.plugin.core.sendPacket(new Packet5PlayerCoinsChange(player.id, amount));
            } else {
                this.plugin.mysql.query("UPDATE users SET coins=coins+" + amount + " WHERE id = " + player.id);
            }
        }
    }

    public int addCoins(VPlayer player, int amount, boolean simulate) {
        if (player == null || amount < 1) {
            return -1;
        }
        try {
            if (!simulate) {
                if (this.waiting) {
                    player.coinsAddBuffer += amount;
                } else if (this.plugin.core.isEnabled()) {
                    this.plugin.core.sendPacket(new Packet5PlayerCoinsChange(player.id, amount));
                } else {
                    this.plugin.mysql.query("UPDATE users SET coins=coins+" + amount + " WHERE id = " + player.id);
                }
                player.coins += amount;
            }
            player.player.sendMessage(ChatColor.YELLOW + "\u0414\u043e\u0431\u0430\u0432\u043b\u0435\u043d\u043e \u043a\u043e\u0438\u043d\u043e\u0432: " + amount);
            if (player.coins >= 20000) {
                player.getAchievements().complete(Achievement.GLOBAL_COINS_20000);
            }
            if (player.coins >= 100000) {
                player.getAchievements().complete(Achievement.GLOBAL_COINS_100000);
            }
            VTexteria.showCoins(player);
            VTexteria.showCoinsChange(player, amount);
            return amount;
        }
        catch (Exception e) {
            this.plugin.getLogger().log(Level.WARNING, null, e);
            return -1;
        }
    }

    public void takeCoins(VPlayer player, int amount, boolean simulate) {
        if (amount < 1) {
            return;
        }
        if (!simulate) {
            player.coins -= amount;
            if (this.plugin.core.isEnabled()) {
                this.plugin.core.sendPacket(new Packet5PlayerCoinsChange(player.id, -amount));
            } else {
                this.plugin.mysql.query("UPDATE users SET coins=coins-" + amount + " WHERE id = " + player.id);
            }
        }
        VTexteria.showCoins(player);
        VTexteria.showCoinsChange(player, -amount);
    }

    public void finish() {
        this.flush();
        this.waiting = false;
    }
}

