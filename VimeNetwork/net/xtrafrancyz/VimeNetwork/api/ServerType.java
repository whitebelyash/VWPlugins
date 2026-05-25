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
   LOBBY("LOBBY", "Лобби"),
   BUILD("BUILD", "Билд"),
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
   UNKNOWN("UNKNOWN", "UNKNOWN");

   private static final Map byId = new HashMap();
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
      return (ServerType)byId.getOrDefault(id.toUpperCase(), UNKNOWN);
   }

   static {
      for(ServerType serverType : values()) {
         ServerType old = (ServerType)byId.put(serverType.getId(), serverType);
         if (old != null) {
            throw new RuntimeException("Duplicate ServerType id " + old + " and " + serverType);
         }
      }

   }
}
