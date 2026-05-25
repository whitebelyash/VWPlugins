package net.xtrafrancyz.VimeNetwork.commands;

import java.util.Random;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ApiCommand implements CommandExecutor {
   private static final String TOKEN_DICT = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (!(sender instanceof Player)) {
         sender.sendMessage("Command can only be used by player");
         return false;
      } else {
         NetworkPlayer player = VimeNetwork.getPlayer(sender.getName());
         if (player.getId() == -1) {
            U.msg(sender, "&cИнформация о вас еще не загрузилась, подождите немного...");
            return false;
         } else {
            boolean reset = args.length > 0 && args[0].equals("new");
            VimeNetwork.mysql().select("SELECT * FROM api_tokens WHERE owner = " + player.getId(), (rs) -> {
               if (rs.next()) {
                  if (reset) {
                     String token = genNewToken();
                     VimeNetwork.mysql().query("UPDATE api_tokens SET token = '" + token + "' WHERE owner = " + player.getId());
                     U.msg(sender, "&aВаш новый токен авторизации:&f http://api.vime.world/web/token/" + token + " &a(перейдите по ссылке)");
                  } else {
                     String token = rs.getString("token");
                     U.msg(sender, "&aВаш текущий токен авторизации:&f http://api.vime.world/web/token/" + token + " &a(перейдите по ссылке)");
                     U.msg(sender, "&aДля генерации нового токена напишите &f/api new");
                  }
               } else {
                  String token = genNewToken();
                  VimeNetwork.mysql().query("INSERT INTO api_tokens (token, owner) VALUES ('" + token + "', " + player.getId() + ")");
                  U.msg(sender, "&aДля вас был сгенерирован новый токен авторизации:&f http://api.vime.world/web/token/" + token + " &a(перейдите по ссылке)");
               }

            });
            return false;
         }
      }
   }

   private static String genNewToken() {
      Random rand = new Random();
      char[] str = new char[32];

      for(int i = 0; i < str.length; ++i) {
         str[i] = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(rand.nextInt("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".length()));
      }

      return new String(str);
   }
}
