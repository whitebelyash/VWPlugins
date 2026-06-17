/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.VimeNetwork
 *  net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement
 *  net.xtrafrancyz.VimeNetwork.api.util.Invs
 *  net.xtrafrancyz.bukkit.texteria.Texteria2D
 *  net.xtrafrancyz.bukkit.texteria.elements.Element
 *  net.xtrafrancyz.bukkit.texteria.elements.Image
 *  net.xtrafrancyz.bukkit.texteria.elements.Rectangle
 *  net.xtrafrancyz.bukkit.texteria.elements.Text
 *  net.xtrafrancyz.bukkit.texteria.utils.Attachment
 *  net.xtrafrancyz.bukkit.texteria.utils.IntColor
 *  net.xtrafrancyz.bukkit.texteria.utils.Position
 *  net.xtrafrancyz.bukkit.texteria.utils.Visibility
 *  net.xtrafrancyz.bukkit.texteria.utils.Visibility$Always
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 */
package net.xtrafrancyz.ClashPoint.object;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.xtrafrancyz.ClashPoint.object.CPTeam;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.util.Invs;
import net.xtrafrancyz.bukkit.texteria.Texteria2D;
import net.xtrafrancyz.bukkit.texteria.elements.Element;
import net.xtrafrancyz.bukkit.texteria.elements.Image;
import net.xtrafrancyz.bukkit.texteria.elements.Rectangle;
import net.xtrafrancyz.bukkit.texteria.elements.Text;
import net.xtrafrancyz.bukkit.texteria.utils.Attachment;
import net.xtrafrancyz.bukkit.texteria.utils.IntColor;
import net.xtrafrancyz.bukkit.texteria.utils.Position;
import net.xtrafrancyz.bukkit.texteria.utils.Visibility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class PlayerInfo {
    public static final Map<String, PlayerInfo> PLAYERS = new ConcurrentHashMap<String, PlayerInfo>();
    public boolean isLoaded;
    public final Player player;
    public final String username;
    public final Stats stats;
    public boolean hyperSpectator = false;
    public CPTeam team;
    public Location deathLocation;
    public long lastDeath = 0L;
    public int thrownPlayers = 0;
    public int resourcePointsBroken = 0;
    public Inventory personalInventory = null;
    public int cachedIron = 0;
    public int cachedGold = 0;
    public int cachedDiamond = 0;
    public int spentGold = 0;

    private PlayerInfo(Player player) {
        this.player = player;
        this.username = player.getName();
        this.stats = new Stats();
        this.isLoaded = false;
        this.team = null;
        this.deathLocation = null;
    }

    public void updateResourceBar() {
        Texteria2D.add((Visibility)new Visibility.Always(), (Element[])new Element[]{((Rectangle)((Rectangle)new Image("cpr.iron", 16, "file:texteria/iron.png").setFade(0)).setPosition(Position.BOTTOM)).setOffset(102, 2), ((Text)((Text)new Text("cpr.iron.text", new String[]{String.valueOf(this.cachedIron)}).setFade(0)).setOffset(1, 0)).setBackground(IntColor.setAlpha((int)-16777216, (int)80)).setAttachment(new Attachment("cpr.iron", Position.RIGHT)), ((Rectangle)((Rectangle)new Image("cpr.gold", 16, "file:texteria/gold.png").setFade(0)).setPosition(Position.BOTTOM)).setOffset(102, 17), ((Text)((Text)new Text("cpr.gold.text", new String[]{String.valueOf(this.cachedGold)}).setFade(0)).setOffset(1, 0)).setBackground(IntColor.setAlpha((int)-16777216, (int)80)).setAttachment(new Attachment("cpr.gold", Position.RIGHT)), ((Rectangle)((Rectangle)new Image("cpr.diam", 16, "file:texteria/diamond.png").setFade(0)).setPosition(Position.BOTTOM)).setOffset(102, 33), ((Text)((Text)new Text("cpr.diam.text", new String[]{String.valueOf(this.cachedDiamond)}).setFade(0)).setOffset(1, 1)).setBackground(IntColor.setAlpha((int)-16777216, (int)80)).setAttachment(new Attachment("cpr.diam", Position.RIGHT))}, (Player[])new Player[]{this.player});
    }

    public int countResources(Material type) {
        if (type == Material.IRON_INGOT) {
            return this.cachedIron;
        }
        if (type == Material.GOLD_INGOT) {
            return this.cachedGold;
        }
        if (type == Material.DIAMOND) {
            return this.cachedDiamond;
        }
        return 0;
    }

    public void takeResources(Material type, int amount) {
        int remaining = Invs.take((Inventory)this.player.getInventory(), (Material)type, (int)amount);
        Invs.take((Inventory)this.personalInventory, (Material)type, (int)remaining);
        if (type == Material.IRON_INGOT) {
            this.cachedIron -= amount;
        } else if (type == Material.GOLD_INGOT) {
            this.cachedGold -= amount;
            this.spentGold += amount;
            if (this.spentGold >= 64) {
                VimeNetwork.getPlayer((String)this.username).getAchievements().complete(Achievement.CP_SHOPPING);
            }
        } else if (type == Material.DIAMOND) {
            this.cachedDiamond -= amount;
        }
        this.updateResourceBar();
    }

    public String toString() {
        return this.username;
    }

    public int hashCode() {
        return this.username.hashCode();
    }

    public boolean equals(Object obj) {
        return obj instanceof PlayerInfo && this.username.equals(((PlayerInfo)obj).username);
    }

    public static PlayerInfo get(String player) {
        PlayerInfo pi = PLAYERS.get(player);
        if (pi == null) {
            pi = PLAYERS.computeIfAbsent(player, name -> new PlayerInfo(Bukkit.getPlayerExact((String)name)));
        }
        return pi;
    }

    public static PlayerInfo get(Player player) {
        PlayerInfo pi = PLAYERS.get(player.getName());
        if (pi == null) {
            pi = PLAYERS.computeIfAbsent(player.getName(), name -> new PlayerInfo(player));
        }
        return pi;
    }

    public static class Stats {
        public int kills = 0;
        public int deaths = 0;
        public int games = 0;
        public int wins = 0;
        public int resourcePointsBreaked = 0;

        public void reset() {
            this.kills = 0;
            this.deaths = 0;
            this.games = 0;
            this.wins = 0;
            this.resourcePointsBreaked = 0;
        }
    }
}

