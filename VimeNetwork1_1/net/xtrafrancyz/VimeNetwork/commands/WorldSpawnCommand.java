package net.xtrafrancyz.VimeNetwork.commands;

import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldSpawnCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (!VimeNetwork.hasRank(sender, Rank.ADMIN, true)) {
         return true;
      } else if (!(sender instanceof Player)) {
         U.msg(sender, "&cКоманда доступна только игрокам");
         return true;
      } else {
         Player player = (Player)sender;
         if (args.length == 0) {
            this.help(player);
         } else if (args[0].equalsIgnoreCase("tp")) {
            player.teleport(player.getWorld().getSpawnLocation());
            U.msg(player, (String[])("&aВы телепортированы на спавн"));
         } else if (args[0].equalsIgnoreCase("set")) {
            int x;
            int y;
            int z;
            if (args.length == 1) {
               Location loc = player.getLocation();
               x = loc.getBlockX();
               y = loc.getBlockY();
               z = loc.getBlockZ();
            } else {
               if (args.length != 4) {
                  U.msg(player, (String[])("&c/" + label + " set [x y z]"));
                  return true;
               }

               try {
                  x = Integer.parseInt(args[1]);
                  y = Integer.parseInt(args[2]);
                  z = Integer.parseInt(args[3]);
               } catch (Exception var10) {
                  U.msg(player, (String[])("&cКоординаты должны быть целыми числами"));
                  return true;
               }
            }

            if (player.getWorld().setSpawnLocation(x, y, z)) {
               U.msg(player, (String[])("&aТочка спавна установлена: " + x + " " + y + " " + z));
            } else {
               U.msg(player, (String[])("&cОшибка, мир не дал поставить точку спавна"));
            }
         } else if (args[0].equalsIgnoreCase("get")) {
            Location loc = player.getWorld().getSpawnLocation();
            U.msg(player, (String[])("&aТочка спавна: " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ()));
         } else {
            this.help(player);
         }

         return true;
      }
   }

   private void help(CommandSender sender) {
      U.msg(sender, "&e/worldspawn tp&f: Тп на точку спавна", "&e/worldspawn set [x y z]&f: Установка точки спавна", "&e/worldspawn get&f: Текущая точка спавна");
   }
}
