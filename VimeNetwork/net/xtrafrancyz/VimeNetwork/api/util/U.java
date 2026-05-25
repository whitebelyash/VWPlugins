package net.xtrafrancyz.VimeNetwork.api.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.stream.Collectors;
import net.minecraft.server.v1_6_R3.Packet;
import net.minecraft.server.v1_6_R3.Packet205ClientCommand;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class U {
   private U() {
   }

   public static String plurals(int n, String form1, String form2, String form3) {
      if (n == 0) {
         return form3;
      } else {
         n = Math.abs(n) % 100;
         if (n > 10 && n < 20) {
            return form3;
         } else {
            n %= 10;
            if (n > 1 && n < 5) {
               return form2;
            } else {
               return n == 1 ? form1 : form3;
            }
         }
      }
   }

   public static String pluralsCoins(int coins) {
      return coins + plurals(coins, " коин", " коина", " коинов");
   }

   public static int limit(int min, int value, int max) {
      if (min > max) {
         throw new IllegalArgumentException("'min' must be less then 'max'");
      } else if (value < min) {
         return min;
      } else {
         return value > max ? max : value;
      }
   }

   public static long limit(long min, long value, long max) {
      if (min > max) {
         throw new IllegalArgumentException("'min' must be less then 'max'");
      } else if (value < min) {
         return min;
      } else {
         return value > max ? max : value;
      }
   }

   public static double avg(double a, double b) {
      return a < b ? a + (b - a) / (double)2.0F : b + (a - b) / (double)2.0F;
   }

   public static float avg(float a, float b) {
      return a < b ? a + (b - a) / 2.0F : b + (a - b) / 2.0F;
   }

   public static String formatFloat(float num, int pr) {
      StringBuilder sb = new StringBuilder();
      sb.append((int)num);
      sb.append('.');

      for(int i = 0; i < pr; ++i) {
         num *= 10.0F;
         sb.append((int)num % 10);
      }

      return sb.toString();
   }

   public static String trimToLength(String str, int length) {
      return str.length() > length ? str.substring(0, length) : str;
   }

   public static void respawnPlayer(Player player) {
      respawnPlayer(player, 15L);
   }

   public static void respawnPlayer(Player player, long delayTicks) {
      Bukkit.getScheduler().scheduleSyncDelayedTask(VNPlugin.instance(), () -> {
         if (VimeNetwork.isPlayerOnline(player)) {
            Packet205ClientCommand packet = new Packet205ClientCommand();
            packet.a = 1;
            ((CraftPlayer)player).getHandle().playerConnection.a(packet);
         }
      }, delayTicks);
   }

   public static void removeArrows(Player player) {
      ((CraftPlayer)player).getHandle().getDataWatcher().watch(9, (byte)0);
   }

   public static void msg(CommandSender cs, String... msg) {
      cs.sendMessage(colored(msg));
   }

   public static void msg(CommandSender cs, List msg) {
      for(String str : msg) {
         cs.sendMessage(colored(str));
      }

   }

   public static void bcast(String msg) {
      Bukkit.broadcastMessage(colored(msg));
   }

   public static String colored(String str) {
      return str == null ? null : ChatColor.translateAlternateColorCodes('&', str);
   }

   public static String[] colored(String... lines) {
      if (lines == null) {
         return null;
      } else {
         for(int i = 0; i < lines.length; ++i) {
            lines[i] = colored(lines[i]);
         }

         return lines;
      }
   }

   public static List colored(List lines) {
      ListIterator<String> it = lines.listIterator();

      while(it.hasNext()) {
         it.set(colored((String)it.next()));
      }

      return lines;
   }

   public static void sendPacket(Player player, Packet packet) {
      ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
   }

   public static Location parseLocation(World world, String loc) {
      try {
         String[] data = loc.split(",");
         Location bloc = new Location(world, Double.parseDouble(data[0].trim()), Double.parseDouble(data[1].trim()), Double.parseDouble(data[2].trim()));
         if (data.length > 3) {
            bloc.setYaw(Float.parseFloat(data[3].trim()));
         }

         if (data.length > 4) {
            bloc.setPitch(Float.parseFloat(data[4].trim()));
         }

         return bloc;
      } catch (Exception e) {
         VNPlugin.instance().getLogger().log(Level.SEVERE, (String)null, e);
         return new Location(world, (double)0.0F, (double)0.0F, (double)0.0F);
      }
   }

   public static List parseLocations(World world, List locs) {
      return (List)locs.stream().map((loc) -> parseLocation(world, loc)).collect(Collectors.toList());
   }

   public static List getNearbyPlayers(Location loc, double radius) {
      LinkedList<Player> list = new LinkedList();
      radius *= radius;

      for(Player player : Bukkit.getOnlinePlayers()) {
         if (player.getLocation().distanceSquared(loc) < radius) {
            list.add(player);
         }
      }

      return list;
   }

   public static Location center(Collection locs) {
      double x = (double)0.0F;
      double y = (double)0.0F;
      double z = (double)0.0F;
      World w = null;

      for(Location loc : locs) {
         if (w == null) {
            w = loc.getWorld();
         }

         x += loc.getX();
         y += loc.getY();
         z += loc.getZ();
      }

      return new Location(w, x / (double)locs.size(), y / (double)locs.size(), z / (double)locs.size());
   }

   public static String genBar(int length, float progress, char c, String background, String filled) {
      StringBuilder sb = new StringBuilder(length + 4);
      sb.append(colored(filled));
      boolean filled0 = false;

      for(int i = 0; i < length; ++i) {
         if (!filled0 && (float)length * progress <= (float)i) {
            sb.append(colored(background));
            filled0 = true;
         }

         sb.append(c);
      }

      return sb.toString();
   }
}
