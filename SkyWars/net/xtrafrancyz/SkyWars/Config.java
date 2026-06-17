/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.conf.Configuration
 *  net.xtrafrancyz.VimeNetwork.api.geom.Vec3f
 *  net.xtrafrancyz.VimeNetwork.api.geom.Vec3i
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.SkyWars;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.xtrafrancyz.SkyWars.Island;
import net.xtrafrancyz.SkyWars.PlayerInfo;
import net.xtrafrancyz.SkyWars.SkyWars;
import net.xtrafrancyz.VimeNetwork.api.conf.Configuration;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3f;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3i;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class Config {
    public static Type TYPE;
    public static World world;
    public static String mapName;
    public static Location lobby;
    public static double respawnY;
    public static List<Location> deathmatchSpawns;
    public static List<Island> islands;
    public static int islandPlayers;
    public static List<Location> basicChests;
    public static List<Location> middleChests;
    public static Location mysteryChest;
    public static Set<Location> protectedChests;
    public static boolean leaderboardEnabled;
    public static Vec3f leaderboardLocation;
    public static Vec3f leaderboardRotation;
    public static float leaderboardScale;
    public static boolean parkourEnabled;
    public static Vec3i parkourSign;

    public static void load() {
        Configuration config = new Configuration((Plugin)SkyWars.instance());
        world = config.getWorld("world");
        mapName = config.getString("name");
        lobby = config.getLocation(world, "lobby");
        respawnY = config.getDouble("respawnY");
        islandPlayers = config.getInt("islandPlayers");
        deathmatchSpawns = config.getLocationList(world, "deathmatchSpawns");
        char letterCounter = 'A';
        islands = new ArrayList<Island>();
        for (Configuration sec : config.getConfigList("islands")) {
            Island island = new Island();
            island.id = islands.size() + 1;
            island.spawns = sec.contains("spawn") ? Collections.singletonList(sec.getLocation(world, "spawn")) : sec.getLocationList(world, "spawns");
            island.clests = sec.getLocationList(world, "chests");
            island.players = new ArrayList<PlayerInfo>(islandPlayers);
            island.tag = String.valueOf(letterCounter);
            letterCounter = (char)(letterCounter + '\u0001');
            islands.add(island);
        }
        SkyWars.instance().getLogger().info("Loaded " + islands.size() + " islands");
        if (islands.size() > deathmatchSpawns.size()) {
            throw new IllegalArgumentException("Not enough deathmatchSpawns. Configured: " + deathmatchSpawns.size() + ". Needed: " + islands.size());
        }
        basicChests = config.getLocationList(world, "basicChests");
        middleChests = config.getLocationList(world, "middleChests");
        mysteryChest = config.getLocation(world, "mysteryChest");
        protectedChests = new HashSet<Location>();
        protectedChests.addAll(middleChests);
        protectedChests.addAll(basicChests);
        for (Island island : islands) {
            protectedChests.addAll(island.clests);
        }
        leaderboardEnabled = config.getBoolean("leaderboard.enabled");
        if (leaderboardEnabled) {
            leaderboardLocation = config.getVec3f("leaderboard.location");
            leaderboardRotation = config.getVec3f("leaderboard.rotation");
            leaderboardScale = config.getFloat("leaderboard.scale");
        }
        if (parkourEnabled = config.getBoolean("parkour.enabled")) {
            parkourSign = config.getVec3i("parkour.sign");
        }
        TYPE = islandPlayers == 1 ? Type.SOLO : Type.TEAM;
    }

    public static int getMaxPlayers() {
        return islandPlayers * islands.size();
    }

    public static enum Type {
        SOLO("sw"),
        TEAM("swt");

        String id;

        private Type(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }

        public String toString() {
            return this.id;
        }
    }
}

