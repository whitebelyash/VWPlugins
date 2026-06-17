/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.player;

import java.time.LocalDate;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;

public class DailyMetaValue {
    private NetworkPlayer player;
    private String key;

    public DailyMetaValue(NetworkPlayer player, String key) {
        this.player = player;
        this.key = key;
    }

    public String getValue() {
        String meta = this.player.getMeta(this.key);
        if (meta == null) {
            return null;
        }
        String[] split = meta.split(";");
        if (split[0].equals(DailyMetaValue.getCurrentDay())) {
            return split[1];
        }
        this.player.removeMeta(this.key);
        return null;
    }

    public void setValue(String value) {
        this.player.setMeta(this.key, DailyMetaValue.getCurrentDay() + ";" + value);
    }

    private static String getCurrentDay() {
        return String.valueOf(LocalDate.now(VimeNetwork.TZ_MOSCOW).getDayOfYear());
    }
}

