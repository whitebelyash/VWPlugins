/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.conf.Configuration
 *  net.xtrafrancyz.VimeNetwork.api.score.Record
 *  net.xtrafrancyz.VimeNetwork.api.score.SideScoreboard
 *  net.xtrafrancyz.VimeNetwork.api.util.Invs
 *  net.xtrafrancyz.VimeNetwork.api.util.Rand
 *  net.xtrafrancyz.VimeNetwork.api.util.Reflect
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Color
 *  org.bukkit.Location
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 */
package net.xtrafrancyz.ClashPoint.object;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.xtrafrancyz.ClashPoint.Config;
import net.xtrafrancyz.ClashPoint.object.PlayerInfo;
import net.xtrafrancyz.ClashPoint.object.ResourcePoint;
import net.xtrafrancyz.ClashPoint.object.TeamPerk;
import net.xtrafrancyz.VimeNetwork.api.conf.Configuration;
import net.xtrafrancyz.VimeNetwork.api.score.Record;
import net.xtrafrancyz.VimeNetwork.api.score.SideScoreboard;
import net.xtrafrancyz.VimeNetwork.api.util.Invs;
import net.xtrafrancyz.VimeNetwork.api.util.Rand;
import net.xtrafrancyz.VimeNetwork.api.util.Reflect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CPTeam {
    public final String id;
    public final String[] names;
    public final List<Location> spawns;
    public final List<Location> traders;
    public final List<Location> upgraders;
    public final List<Location> personalChests;
    public final ChatColor chatColor;
    public final Color color;
    public final short wool;
    public Record record;
    public final List<PlayerInfo> players;
    public int slot = -1;
    public float gamePoints = 0.0f;
    private EnumMap<TeamPerk, Integer> perks = new EnumMap(TeamPerk.class);
    public final Set<ResourcePoint> originalResourcePoints;
    private Set<ResourcePoint> resourcePoints;
    public int leavers = 0;

    public CPTeam(String id, Configuration config) {
        this.id = id;
        this.names = config.getString("names").split(",");
        for (int i = 0; i < this.names.length; ++i) {
            this.names[i] = this.names[i].trim();
        }
        this.spawns = config.getLocationList(Config.world, "spawns");
        this.originalResourcePoints = config.getLocationList(Config.world, "resource-points").stream().map(l -> new ResourcePoint(this, (Location)l)).collect(Collectors.toSet());
        this.traders = config.getLocationList(Config.world, "traders");
        this.upgraders = config.getLocationList(Config.world, "upgraders");
        this.personalChests = config.getLocationList(Config.world, "personal-chests");
        String colorStr = config.getString("color").toUpperCase();
        this.chatColor = ChatColor.valueOf((String)colorStr);
        switch (colorStr) {
            case "DARK_PURPLE": {
                this.color = Color.PURPLE;
                break;
            }
            case "LIGHT_PURPLE": {
                this.color = Color.FUCHSIA;
                break;
            }
            case "GOLD": {
                this.color = Color.ORANGE;
                break;
            }
            case "DARK_AQUA": {
                this.color = Color.TEAL;
                break;
            }
            default: {
                Color color0 = (Color)Reflect.get(Color.class, (String)colorStr);
                this.color = color0 == null ? Color.WHITE : color0;
            }
        }
        this.wool = (short)config.getInt("wool");
        this.players = new ArrayList<PlayerInfo>(Config.teamPlayers);
        this.init();
    }

    public void init() {
        this.resourcePoints = new HashSet<ResourcePoint>(this.originalResourcePoints);
    }

    public void wipe() {
        this.originalResourcePoints.forEach(ResourcePoint::invalidate);
        this.resourcePoints.clear();
    }

    public void onResourcePointDestroyed(ResourcePoint rp) {
        this.resourcePoints.remove(rp);
    }

    public Set<ResourcePoint> getResourcePoints() {
        return this.resourcePoints;
    }

    public int getPerkLevel(TeamPerk perk) {
        return this.perks.getOrDefault((Object)perk, 0);
    }

    public void upgradePerk(TeamPerk perk) {
        int level = this.getPerkLevel(perk) + 1;
        this.perks.put(perk, level);
        switch (perk) {
            case RP_HEALTH: {
                int health = 50;
                if (level == 1) {
                    health = 75;
                } else if (level == 2) {
                    health = 100;
                }
                for (ResourcePoint rp : this.originalResourcePoints) {
                    int diff = health - rp.maxHealth;
                    rp.maxHealth = health;
                    rp.health += (float)diff;
                    rp.updateHolo();
                }
                break;
            }
            case PERSONAL_CHEST_CAPACITY: {
                int newSize = 9 + level * 9;
                for (PlayerInfo player : this.players) {
                    Inventory newInv = Bukkit.createInventory((InventoryHolder)player.player, (int)newSize, (String)"\u041f\u0435\u0440\u0441\u043e\u043d\u0430\u043b\u044c\u043d\u044b\u0439 \u0441\u0443\u043d\u0434\u0443\u043a");
                    newInv.setContents(player.personalInventory.getContents());
                    for (HumanEntity viewer : new ArrayList(player.personalInventory.getViewers())) {
                        Invs.forceOpen((HumanEntity)viewer, (Inventory)newInv);
                    }
                    player.personalInventory = newInv;
                }
                break;
            }
        }
    }

    public Location getSpawnLocation() {
        return (Location)Rand.of(this.spawns);
    }

    public void updateRecord() {
        int alive = this.resourcePoints.size();
        this.record.setName(this.chatColor + (alive == 0 ? "\u2715" : alive + " \u2714") + " " + this.names[2]);
        this.record.setValue(this.players.size());
    }

    public void createRecord(SideScoreboard scoreboard) {
        this.record = scoreboard.create(this.chatColor + this.names[2]);
        this.updateRecord();
    }

    public Player[] getBukkitPlayers() {
        return (Player[])this.players.stream().map(p -> p.player).toArray(Player[]::new);
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public boolean equals(Object obj) {
        return obj != null && obj instanceof CPTeam && ((CPTeam)obj).id.equals(this.id);
    }
}

