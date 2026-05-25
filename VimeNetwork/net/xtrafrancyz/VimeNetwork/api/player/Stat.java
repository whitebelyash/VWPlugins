package net.xtrafrancyz.VimeNetwork.api.player;

import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.LinkedList;
import java.util.List;

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

   private static final TIntObjectHashMap byId = new TIntObjectHashMap();
   private int id;
   private List achievements;

   private Stat(int id) {
      this.id = id;
      this.achievements = new LinkedList();
   }

   public int getId() {
      return this.id;
   }

   public List getAchievements() {
      return this.achievements;
   }

   public String toString() {
      return this.name() + "(" + this.id + ")";
   }

   public static Stat byId(int id) {
      return (Stat)byId.get(id);
   }

   static {
      for(Stat stat : values()) {
         Stat old = (Stat)byId.put(stat.getId(), stat);
         if (old != null) {
            throw new RuntimeException("Duplicate stat id " + old + " and " + stat);
         }
      }

   }
}
