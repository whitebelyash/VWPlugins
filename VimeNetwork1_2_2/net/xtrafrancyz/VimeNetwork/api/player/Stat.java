/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  gnu.trove.map.hash.TIntObjectHashMap
 */
package net.xtrafrancyz.VimeNetwork.api.player;

import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.LinkedList;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.StatAchievement;

public enum Stat {
    GOAL_COMPLETE(1),
    LOBBY_PAINTBALL(100),
    LOBBY_MELON(101),
    LOBBY_BALLOON(102),
    LOBBY_CHEST_COINS(103),
    SW_THROWN_PLAYERS(201),
    DR_MAUTI_WIN(301),
    DR_BONBON_WIN(302),
    DR_ORBIS_WIN(303),
    DR_SKYLANDS_WIN(304);

    private static final TIntObjectHashMap<Stat> byId;
    private int id;
    private List<StatAchievement> achievements;

    private Stat(int id) {
        this.id = id;
        this.achievements = new LinkedList<StatAchievement>();
    }

    public int getId() {
        return this.id;
    }

    public List<StatAchievement> getAchievements() {
        return this.achievements;
    }

    public String toString() {
        return this.name() + "(" + this.id + ")";
    }

    public static Stat byId(int id) {
        return (Stat)((Object)byId.get(id));
    }

    static {
        byId = new TIntObjectHashMap();
        for (Stat stat : Stat.values()) {
            Stat old = (Stat)((Object)byId.put(stat.getId(), (Object)stat));
            if (old == null) continue;
            throw new RuntimeException("Duplicate stat id " + (Object)((Object)old) + " and " + (Object)((Object)stat));
        }
    }
}

