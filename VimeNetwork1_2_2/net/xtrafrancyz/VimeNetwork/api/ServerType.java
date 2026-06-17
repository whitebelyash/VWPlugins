/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api;

import java.util.HashMap;
import java.util.Map;

public enum ServerType {
    SKY_WARS("SW", "SkyWars"),
    SKY_WARS_TEAM("SWT", "SkyWars Team"),
    BED_WARS("BW", "BedWars"),
    BED_WARS_HARD("BWH", "BedWars Hard"),
    BED_WARS_QUICK("BWQ", "BedWars Quick"),
    ANNIHILATION("ANN", "Annihilation"),
    LOBBY("LOBBY", "\u041b\u043e\u0431\u0431\u0438"),
    BUILD("BUILD", "\u0411\u0438\u043b\u0434"),
    GUN_GAME("GG", "GunGame"),
    DEATH_RUN("DR", "DeathRun"),
    BUILD_BATTLE("BB", "BuildBattle"),
    BLOCK_PARTY("BP", "BlockParty"),
    HUNGER_GAMES("HG", "HungerGames"),
    HUNGER_GAMES_LUCKY("HGL", "HungerGames Lucky"),
    KIT_PVP("KPVP", "KitPvP"),
    MOB_WARS("MW", "MobWars"),
    CLASH_POINT("CP", "ClashPoint"),
    CLASH_POINT_HARD("CPH", "ClashPoint Hard"),
    DUELS("DUELS", "\u0414\u0443\u044d\u043b\u0438"),
    UNKNOWN("UNKNOWN", "UNKNOWN");

    private static final Map<String, ServerType> byId;
    private String name;
    private String id;

    private ServerType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public static ServerType byId(String id) {
        return byId.getOrDefault(id.toUpperCase(), UNKNOWN);
    }

    static {
        byId = new HashMap<String, ServerType>();
        for (ServerType serverType : ServerType.values()) {
            ServerType old = byId.put(serverType.getId(), serverType);
            if (old == null) continue;
            throw new RuntimeException("Duplicate ServerType id " + (Object)((Object)old) + " and " + (Object)((Object)serverType));
        }
    }
}

