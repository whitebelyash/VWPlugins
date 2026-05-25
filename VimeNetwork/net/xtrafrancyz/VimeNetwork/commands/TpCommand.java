package net.xtrafrancyz.VimeNetwork.commands;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.List;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.VimeNetwork.api.ServerType;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.Permission;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.util.Spectators;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      NetworkPlayer networkPlayer = VimeNetwork.getPlayer(sender.getName());
      if (VimeNetwork.lobby().getServerType() == ServerType.BUILD) {
         if (!networkPlayer.getRank().has(sender, Permission.BUILDER)) {
            return true;
         }
      } else {
         if (!networkPlayer.getRank().has(sender, Permission.VANISH)) {
            return true;
         }

         if (!networkPlayer.getRank().has(Rank.CHIEF) && !Spectators.instance().contains((Player)sender)) {
            U.msg(sender, T.error("VimeWorld", "Вы можете использовать телепорт только в режиме наблюдателя (/vanish)"));
            return true;
         }
      }

      if (args.length == 1) {
         if (args[0].contains(",")) {
            this.teleport(sender, args[0].split(","));
         } else {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target != null) {
               this.teleport(sender, target);
            } else {
               U.msg(sender, "&cИгрок &f" + args[0] + "&c не найден");
            }
         }

         return true;
      } else if (args.length == 2 && networkPlayer.getRank().has(Rank.CHIEF)) {
         Player player1 = Bukkit.getPlayerExact(args[0]);
         if (player1 == null) {
            U.msg(sender, "&cИгрок &f" + args[0] + "&c не найден");
            return true;
         } else {
            Player player2 = Bukkit.getPlayerExact(args[1]);
            if (player2 == null) {
               U.msg(sender, "&cИгрок &f" + args[1] + "&c не найден");
               return true;
            } else {
               this.teleport(player1, (Player)player2);
               return true;
            }
         }
      } else if (args.length > 2 && !args[0].contains(",")) {
         this.teleport(sender, args);
         return true;
      } else {
         this.teleport(sender, Joiner.on("").join(args).split(","));
         return true;
      }
   }

   private void help(CommandSender sender) {
      List<String> list = new ArrayList();
      list.add("&cИспользование:");
      list.add("&e/tp &7<игрок>&f: Телепортация к игроку");
      if (VimeNetwork.hasRank(sender, Rank.CHIEF, false)) {
         list.add("&e/tp &7<игрок 1> <игрок 2>&f: Телепортация первого игрока ко второму");
      }

      list.add("&e/tp &7<x> <y> <z> [yaw] [pitch]&f: Телепортация на координаты");
      U.msg(sender, list);
   }

   private void teleport(CommandSender sender, String[] loc) {
      if (loc.length < 3) {
         this.help(sender);
      } else {
         try {
            Location parsed = new Location(((Player)sender).getWorld(), Double.parseDouble(loc[0].trim()), Double.parseDouble(loc[1].trim()), Double.parseDouble(loc[2].trim()));
            if (loc.length > 3) {
               parsed.setYaw(Float.parseFloat(loc[3].trim()) + 1.0E-4F);
            }

            if (loc.length > 4) {
               parsed.setPitch(Float.parseFloat(loc[4].trim()) + 1.0E-4F);
            }

            if (parsed.getX() % (double)1.0F < 1.0E-4 && parsed.getZ() % (double)1.0F < 1.0E-4) {
               parsed.setX(parsed.getX() + (double)0.5F);
               parsed.setZ(parsed.getZ() + (double)0.5F);
            }

            this.teleport(sender, parsed);
         } catch (Exception var4) {
            this.help(sender);
         }

      }
   }

   private void teleport(CommandSender sender, Player entity) {
      this.teleport(sender, entity.getLocation());
      U.msg(sender, "&aВы телепортированы к игроку " + entity.getName());
   }

   private void teleport(CommandSender sender, Location loc) {
      Player player = (Player)sender;
      if (loc.getYaw() == 0.0F) {
         loc.setYaw(player.getLocation().getYaw());
      }

      if (loc.getPitch() == 0.0F) {
         loc.setPitch(player.getLocation().getPitch());
      }

      player.teleport(loc);
   }
}
