/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringEscapeUtils
 */
package net.xtrafrancyz.VimeNetwork.tasks;

import java.util.Collection;
import java.util.Map;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.impl.player.MysqlPlayer;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import org.apache.commons.lang.StringEscapeUtils;

public class PlayerMetaSaver
implements Runnable {
    private final VNPlugin plugin;

    public PlayerMetaSaver(VNPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        long time = System.currentTimeMillis();
        for (MysqlPlayer player : this.getAllPlayers()) {
            for (Map.Entry<String, MysqlPlayer.MetaValue> entry : player.meta.entrySet()) {
                if (entry.getValue().saved || time - entry.getValue().changed <= 5000L) continue;
                this.save(player, entry);
            }
        }
    }

    private Collection<MysqlPlayer> getAllPlayers() {
        return VPlayer.PLAYERS.values();
    }

    public void saveNow(MysqlPlayer player) {
        for (Map.Entry<String, MysqlPlayer.MetaValue> entry : player.meta.entrySet()) {
            if (entry.getValue().saved) continue;
            this.save(player, entry);
        }
    }

    public void finish() {
        this.getAllPlayers().forEach(this::saveNow);
    }

    private void save(MysqlPlayer player, Map.Entry<String, MysqlPlayer.MetaValue> entry) {
        MysqlPlayer.MetaValue value = entry.getValue();
        if (value.value == null) {
            if (value.prev != null) {
                this.plugin.mysql.query("DELETE FROM `users_meta` WHERE `userid` = " + player.id + " AND `key` = '" + entry.getKey() + "'");
            }
            player.meta.remove(entry.getKey());
        } else {
            String escaped = StringEscapeUtils.escapeSql((String)value.value);
            if (value.prev == null) {
                this.plugin.mysql.query("INSERT INTO `users_meta` (`userid`, `key`, `value`) VALUES (" + player.id + ", '" + entry.getKey() + "', '" + escaped + "')");
            } else if (!value.value.equals(value.prev)) {
                this.plugin.mysql.query("UPDATE `users_meta` SET `value` = '" + escaped + "' WHERE userid = " + player.id + " AND `key` = '" + entry.getKey() + "'");
            }
            value.prev = value.value;
            value.saved = true;
        }
    }
}

