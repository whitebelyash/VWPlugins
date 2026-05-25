package net.xtrafrancyz.BedWars;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerInfo {
   public static final Map PLAYERS = new ConcurrentHashMap();
   boolean isLoaded;
   public final Player player;
   public final String username;
   public final Stats stats;
   public boolean hyperSpectator = false;
   public BWTeam team;
   public Location deathLocation;
   public long lastDeath = 0L;
   public int thrownPlayers = 0;
   public int thorUses = 0;
   public List bedBreakTimes = new ArrayList();
   public int spentGold = 0;
   public int clockUses = 0;

   private PlayerInfo(Player player) {
      this.player = player;
      this.username = player.getName();
      this.stats = new Stats();
      this.isLoaded = false;
      this.team = null;
      this.deathLocation = null;
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
      PlayerInfo pi = (PlayerInfo)PLAYERS.get(player);
      if (pi == null) {
         PLAYERS.put(player, pi = new PlayerInfo(Bukkit.getPlayerExact(player)));
      }

      return pi;
   }

   public static PlayerInfo get(Player player) {
      PlayerInfo pi = (PlayerInfo)PLAYERS.get(player.getName());
      if (pi == null) {
         PLAYERS.put(player.getName(), pi = new PlayerInfo(player));
      }

      return pi;
   }

   public static class Stats {
      public int kills = 0;
      public int deaths = 0;
      public int games = 0;
      public int wins = 0;
      public int bedBreaked = 0;

      void reset() {
         this.kills = 0;
         this.deaths = 0;
         this.games = 0;
         this.wins = 0;
         this.bedBreaked = 0;
      }
   }
}
