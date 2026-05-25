package net.xtrafrancyz.BedWars;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.conf.Configuration;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3f;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3i;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public class Config {
   public static Type type;
   public static final ItemStack BRONZE;
   public static final ItemStack IRON;
   public static final ItemStack GOLD;
   public static final ItemStack WATCH;
   public static final String START_LORE = "Начальный предмет";
   public static World world;
   public static String mapName;
   public static int bronzeFrequency;
   public static int ironFrequency;
   public static int goldFrequency;
   public static int watchFrequency;
   public static List goldSpawns;
   public static List watchSpawns;
   public static Location lobby;
   public static Location middle;
   public static double teamDistance;
   public static int teamPlayers;
   public static List teams;
   public static double respawnY;
   public static boolean leaderboardEnabled;
   public static Vec3f leaderboardLocation;
   public static Vec3f leaderboardRotation;
   public static float leaderboardScale;
   public static boolean parkourEnabled;
   public static Vec3i parkourSign;
   public static Location dropBronze;
   public static Location dropIron;
   public static Location dropGold;

   public static void load() {
      Configuration config = new Configuration(BedWars.instance());
      if (config.contains("type")) {
         type = Config.Type.valueOf(config.getString("type", "NORMAL").toUpperCase());
      } else {
         switch (VimeNetwork.lobby().getServerType()) {
            case BED_WARS_HARD:
               type = Config.Type.HARD;
               break;
            case BED_WARS_QUICK:
               type = Config.Type.QUICK;
               break;
            default:
               type = Config.Type.NORMAL;
         }
      }

      world = config.getWorld("world");
      mapName = config.getString("name");
      lobby = config.getLocation(world, "lobby");
      teamPlayers = config.getInt("teamPlayers");
      respawnY = config.getDouble("respawnY");
      leaderboardEnabled = config.getBoolean("leaderboard.enabled");
      if (leaderboardEnabled) {
         leaderboardLocation = config.getVec3f("leaderboard.location");
         leaderboardRotation = config.getVec3f("leaderboard.rotation");
         leaderboardScale = config.getFloat("leaderboard.scale");
      }

      parkourEnabled = config.getBoolean("parkour.enabled");
      if (parkourEnabled) {
         parkourSign = config.getVec3i("parkour.sign");
      }

      dropBronze = config.getLocation(world, "lobby-drop.bronze");
      dropIron = config.getLocation(world, "lobby-drop.iron");
      dropGold = config.getLocation(world, "lobby-drop.gold");
      bronzeFrequency = config.getInt("bronze-frequency");
      ironFrequency = config.getInt("iron-frequency");
      goldFrequency = config.getInt("gold-frequency");
      watchFrequency = config.getInt("watch-frequency", 20);
      if (type == Config.Type.HARD) {
         bronzeFrequency /= 2;
         ironFrequency /= 2;
         goldFrequency /= 2;
      }

      goldSpawns = config.getLocationList(world, "gold-spawns");
      watchSpawns = config.getLocationList(world, "watch-spawns");
      Configuration tms = config.getSection("teams");
      teams = (List)tms.getKeys(false).stream().map((k) -> new BWTeam(k, tms.getSection(k))).collect(Collectors.toList());
      middle = U.center((Collection)teams.stream().flatMap((t) -> t.bed.stream()).collect(Collectors.toList()));
      teamDistance = middle.distance((Location)((BWTeam)teams.get(0)).bed.get(0));
   }

   public static int getMaxPlayers() {
      return teamPlayers * teams.size();
   }

   static {
      BRONZE = Items.name(new ItemStack(Material.CLAY_BRICK), "&6Бронза", new String[0]);
      IRON = Items.name(new ItemStack(Material.IRON_INGOT), "&fЖелезо", new String[0]);
      GOLD = Items.name(new ItemStack(Material.GOLD_INGOT), "&eЗолото", new String[0]);
      WATCH = Items.name(new ItemStack(Material.WATCH), "&e+10 секунд &7(Нажмите правой кнопкой на кровать)", new String[]{"&7Нажмите правой кнопкой на кровать", "&7чтобы увеличить её время жизни"});
   }

   public static enum Type {
      NORMAL("bw"),
      HARD("bwh"),
      QUICK("bwq");

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
