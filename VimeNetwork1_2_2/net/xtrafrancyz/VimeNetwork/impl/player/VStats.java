/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.impl.player;

import java.util.EnumMap;
import net.xtrafrancyz.Core.network.packet.Packet1PlayerInfo;
import net.xtrafrancyz.Core.network.packet.Packet9PlayerStatChange;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.Stat;
import net.xtrafrancyz.VimeNetwork.api.player.Stats;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.StatAchievement;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;

public class VStats
implements Stats {
    private final EnumMap<Stat, Integer> stats = new EnumMap(Stat.class);
    private final VPlayer player;

    public VStats(VPlayer player) {
        this.player = player;
    }

    public void load(Packet1PlayerInfo packet) {
        for (int[] stat : packet.stats) {
            this.stats.put(Stat.byId(stat[0]), stat[1]);
        }
    }

    @Override
    public int get(Stat stat) {
        return this.stats.getOrDefault((Object)stat, 0);
    }

    @Override
    public int increment(Stat stat) {
        return this.increment(stat, 1);
    }

    @Override
    public int increment(Stat stat, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount < 0");
        }
        int val = this.get(stat);
        this.stats.put(stat, val += amount);
        for (StatAchievement achievement : stat.getAchievements()) {
            if (achievement.getNeeded() > val) continue;
            this.player.getAchievements().complete(achievement);
        }
        VimeNetwork.core().sendPacket(new Packet9PlayerStatChange(this.player.id, stat.getId(), amount));
        return val;
    }
}

