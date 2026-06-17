/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.conf.Configuration
 *  net.xtrafrancyz.VimeNetwork.api.geom.Vec3f
 *  net.xtrafrancyz.VimeNetwork.api.geom.Vec3i
 *  net.xtrafrancyz.VimeNetwork.api.util.Items
 *  net.xtrafrancyz.VimeNetwork.api.util.U
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.World$Environment
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.ClashPoint;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.xtrafrancyz.ClashPoint.ClashPoint;
import net.xtrafrancyz.ClashPoint.object.CPTeam;
import net.xtrafrancyz.ClashPoint.object.ResourcePoint;
import net.xtrafrancyz.VimeNetwork.api.conf.Configuration;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3f;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3i;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class Config {
    public static final ItemStack IRON = Items.name((ItemStack)new ItemStack(Material.IRON_INGOT), (String)"&f\u0416\u0435\u043b\u0435\u0437\u043e", (String[])new String[0]);
    public static final ItemStack GOLD = Items.name((ItemStack)new ItemStack(Material.GOLD_INGOT), (String)"&e\u0417\u043e\u043b\u043e\u0442\u043e", (String[])new String[0]);
    public static final ItemStack DIAMOND = Items.name((ItemStack)new ItemStack(Material.DIAMOND), (String)"&b\u0410\u043b\u043c\u0430\u0437", (String[])new String[0]);
    public static final String START_LORE = "\u041d\u0430\u0447\u0430\u043b\u044c\u043d\u044b\u0439 \u043f\u0440\u0435\u0434\u043c\u0435\u0442";
    public static World world;
    public static String mapName;
    public static int ironFrequency;
    public static int goldFrequency;
    public static int diamondFrequency;
    public static int resourcePointsActivationFrequency;
    public static List<Location> goldSpawns;
    public static List<Location> diamondSpawns;
    public static Location lobby;
    public static Location middle;
    public static double teamDistance;
    public static int teamPlayers;
    public static List<CPTeam> teams;
    public static double respawnY;
    public static boolean leaderboardEnabled;
    public static Vec3f leaderboardLocation;
    public static Vec3f leaderboardRotation;
    public static float leaderboardScale;
    public static boolean parkourEnabled;
    public static Vec3i parkourSign;

    public static void load() {
        ClashPoint plugin = ClashPoint.instance();
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        Configuration config = new Configuration((Plugin)plugin);
        for (World w : Bukkit.getWorlds()) {
            if (w.getEnvironment() != World.Environment.NORMAL) continue;
            world = w;
            break;
        }
        if (world == null) {
            throw new RuntimeException("Can't find right world. Is the world has normal environment?");
        }
        mapName = config.getString("name");
        lobby = config.getLocation(world, "lobby");
        teamPlayers = config.getInt("teamPlayers");
        respawnY = config.getDouble("respawnY");
        ironFrequency = config.getInt("iron-frequency");
        goldFrequency = config.getInt("gold-frequency");
        diamondFrequency = config.getInt("diamond-frequency");
        resourcePointsActivationFrequency = config.getInt("resource-points-activation-frequency");
        goldSpawns = config.getLocationList(world, "gold-spawns");
        diamondSpawns = config.getLocationList(world, "diamond-spawns");
        Configuration tms = config.getSection("teams");
        teams = tms.getKeys(false).stream().map(k -> new CPTeam((String)k, tms.getSection(k))).collect(Collectors.toList());
        leaderboardEnabled = config.getBoolean("leaderboard.enabled");
        if (leaderboardEnabled) {
            leaderboardLocation = config.getVec3f("leaderboard.location");
            leaderboardRotation = config.getVec3f("leaderboard.rotation");
            leaderboardScale = config.getFloat("leaderboard.scale");
        }
        if (parkourEnabled = config.getBoolean("parkour.enabled")) {
            parkourSign = config.getVec3i("parkour.sign");
        }
        middle = U.center((Collection)teams.stream().flatMap(t -> t.originalResourcePoints.stream()).map(ResourcePoint::getLocation).collect(Collectors.toList()));
        teamDistance = middle.distance(Config.teams.get((int)0).spawns.get(0));
    }

    public static int getMaxPlayers() {
        return teamPlayers * teams.size();
    }
}

