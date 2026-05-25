package net.xtrafrancyz.BedWars;

import java.util.ArrayList;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.conf.Configuration;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3f;
import net.xtrafrancyz.VimeNetwork.api.holo.TextTimerHologram;
import net.xtrafrancyz.VimeNetwork.api.score.Record;
import net.xtrafrancyz.VimeNetwork.api.score.SideScoreboard;
import net.xtrafrancyz.VimeNetwork.api.util.Rand;
import net.xtrafrancyz.VimeNetwork.api.util.Reflect;
import net.xtrafrancyz.bukkit.texteria.utils.ParsedTime;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BWTeam {
   public final String id;
   public final String[] names;
   public final List spawns;
   public final List bed;
   public final List bronzeSpawns;
   public final List ironSpawns;
   public final List villagers;
   public final ChatColor chatColor;
   public final Color color;
   public final short wool;
   public final Vec3f timerPos;
   public Record record;
   public final List players;
   public int slot = -1;
   public boolean bedBreaked = false;
   public long quickTTL = 0L;
   public TextTimerHologram timerHolo;
   public float gamePoints = 0.0F;

   public BWTeam(String id, Configuration config) {
      this.id = id;
      this.names = config.getString("names").split(",");

      for(int i = 0; i < this.names.length; ++i) {
         this.names[i] = this.names[i].trim();
      }

      this.spawns = config.getLocationList(Config.world, "spawns");
      this.bed = config.getLocationList(Config.world, "bed");
      this.bronzeSpawns = config.getLocationList(Config.world, "bronze-spawns");
      this.ironSpawns = config.getLocationList(Config.world, "iron-spawns");
      this.villagers = config.getLocationList(Config.world, "villagers");
      String colorStr = config.getString("color").toUpperCase();
      this.chatColor = ChatColor.valueOf(colorStr);
      switch (colorStr) {
         case "DARK_PURPLE":
            this.color = Color.PURPLE;
            break;
         case "LIGHT_PURPLE":
            this.color = Color.FUCHSIA;
            break;
         case "GOLD":
            this.color = Color.ORANGE;
            break;
         case "DARK_AQUA":
            this.color = Color.TEAL;
            break;
         default:
            Color color0 = (Color)Reflect.get(Color.class, colorStr);
            if (color0 == null) {
               this.color = Color.WHITE;
            } else {
               this.color = color0;
            }
      }

      this.wool = (short)config.getInt("wool");
      this.players = new ArrayList(Config.teamPlayers);
      this.timerPos = (new Vec3f((Location)this.bed.get(0))).add(new Vec3f((Location)this.bed.get(1))).divide(2.0F).add(0.5F, 1.0F, 0.5F);
   }

   public Location getSpawnLocation() {
      return (Location)Rand.of(this.spawns);
   }

   public void updateRecord() {
      if (Config.type == Config.Type.QUICK) {
         long ttl = this.quickTTL - System.currentTimeMillis();
         if (!this.bedBreaked && ttl > 0L) {
            ParsedTime time = new ParsedTime(ttl);
            String timestr = ChatColor.WHITE + this.toString(time.minutes, 2) + ":" + this.toString(time.seconds, 2) + this.chatColor;
            this.record.setName(this.chatColor + (this.bedBreaked ? "✕" : "✔") + " " + timestr + " " + this.names[2]);
         } else {
            this.record.setName(this.chatColor + (this.bedBreaked ? "✕" : "✔") + " " + ChatColor.DARK_RED + "00:00" + this.chatColor + " " + this.names[2]);
         }
      } else {
         this.record.setName(this.chatColor + (this.bedBreaked ? "✕" : "✔") + " " + this.names[2]);
      }

      this.record.setValue(this.players.size());
   }

   public void updateTimer() {
      if (this.bedBreaked) {
         if (this.timerHolo != null) {
            this.timerHolo.remove();
         }
      } else {
         if (this.timerHolo == null) {
            this.timerHolo = VimeNetwork.holograms().createTextTimer(this.timerPos, -1L, new String[]{"{MM}:{SS}"});
         }

         this.timerHolo.setTimerDuration(this.quickTTL - System.currentTimeMillis());
      }

   }

   private String toString(int num, int needed) {
      String str;
      for(str = num + ""; str.length() < needed; str = "0" + str) {
      }

      if (str.length() > needed) {
         str = str.substring(0, needed);
      }

      return str;
   }

   public void createRecord(SideScoreboard scoreboard) {
      this.record = scoreboard.create(this.chatColor + this.names[2]);
      this.updateRecord();
   }

   public Player[] getBukkitPlayers() {
      return (Player[])this.players.stream().map((p) -> p.player).toArray((x$0) -> new Player[x$0]);
   }

   public int hashCode() {
      return this.id.hashCode();
   }

   public boolean equals(Object obj) {
      return obj != null && obj instanceof BWTeam && ((BWTeam)obj).id.equals(this.id);
   }
}
