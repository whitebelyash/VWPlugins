/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 *  org.bukkit.potion.PotionEffectType
 */
package net.xtrafrancyz.SkyWars;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.xtrafrancyz.SkyWars.Island;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class PlayerInfo {
    public static final Map<String, PlayerInfo> PLAYERS = new ConcurrentHashMap<String, PlayerInfo>();
    private int hash = 0;
    public final Player player;
    public final String username;
    public final Stats stats;
    public final Upgrades upgrades;
    public final Set<String> kits;
    public String kit;
    public boolean isLoaded = false;
    public boolean hyperSpectator = false;
    public Island island = null;
    public Location deathLocation;
    public int kills = 0;
    public boolean chestOpened = false;
    public PotionEffectType blockEffect = null;
    public int blockEffectTask = -1;

    private PlayerInfo(Player player) {
        this.username = player.getName();
        this.player = player;
        this.stats = new Stats();
        this.upgrades = new Upgrades();
        this.kits = new HashSet<String>();
    }

    public static PlayerInfo get(String player) {
        PlayerInfo pi = PLAYERS.get(player);
        if (pi == null) {
            pi = new PlayerInfo(Bukkit.getPlayerExact((String)player));
            PLAYERS.put(player, pi);
        }
        return pi;
    }

    public static PlayerInfo get(Player player) {
        PlayerInfo pi = PLAYERS.get(player.getName());
        if (pi == null) {
            pi = new PlayerInfo(player);
            PLAYERS.put(player.getName(), pi);
        }
        return pi;
    }

    public String toString() {
        return "PlayerInfo{name='" + this.username + "'}";
    }

    public int hashCode() {
        if (this.hash == 0) {
            this.hash = this.username.toLowerCase().hashCode();
        }
        return this.hash;
    }

    public boolean equals(Object obj) {
        return this == obj;
    }

    public static class Upgrades {
        public int arrow = 0;
        public int blazeArrow = 0;
        public int juggernaut = 0;
        public int speedBoost = 0;
        public int resistance = 0;
        public int redstoneHeart = 50;
        public int enderman = 0;
        public int builder = 0;
        public int zombie = 0;
        public int enchanter = 0;
        public int goldenApple = 0;
    }

    public static class Stats {
        public int games = 0;
        public int wins = 0;
        public int kills = 0;
        public int deaths = 0;
        public int arrowsFired = 0;
        public int blocksPlaced = 0;
        public int blocksBroken = 0;
        public int winStreak = 0;
        public int highestWinStreak = 0;
    }
}

