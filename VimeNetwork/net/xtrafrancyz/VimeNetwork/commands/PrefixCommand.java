package net.xtrafrancyz.VimeNetwork.commands;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.Permission;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PrefixCommand implements CommandExecutor {
   private final Pattern pattern = Pattern.compile("^[a-zA-Z0-9_.-]{2,4}$");
   private final Set blacklist = new HashSet(Arrays.asList("ebl0", "hyem", "3blo", "p1zd", "ueba", "pizd", "eblo", "ebat", "blya", "pidr", "xuy", "ebal", "her", "h3r", "3bal", "3ba1", "eba1", "p1dr", "xy1", "b1ya", "blea", "61ya", "xyi", "xyev", "hyev", "huya", "huev", "xyem", "xy_i", "hyi_", "_hyi", "h_yi", "xyyi", "xyii", "xu_i", "xuui", "xyll", "mudk", "xxyi", "twar", "dick", "cock", "cunt", "quim", "t8ap", "1bap", "tbap", "uebk", "yoba", "y0ba", "eb0k", "ebu", "uebu", "yeba", "mraz", "hyil", "g0vn", "govn", "lycu", "lucy", "l1cy", "okss", "oksi", "adm", "modr", "oks1", "admi", "oksl", "xelp", "smai", "smak", "lyc1", "aksi", "admn", "aclm", "adm_", "anus", "anal", "a_dm", "pisy", "ad_m", "sosu", "s0su", "sosi", "sisi", "4len", "s1s1", "suka", "suki", "srat", "sratb", "sru", "ass", "qay", "gay", "g4y", "lsd", "lesb", "sh1t", "shit", "cum", "l3sb", "slut", "elda", "eldk", "e1da", "sprm", "dcp", "daun", "porn", "sex", "cekc", "ebis", "urod", "ueby", "yebu", "xu_y", "x_uy", "xu-y", "cyka", "syka", ".loh", ".lox", "lox.", "loh.", "hui.", "xui.", "xuy.", "huy.", ".xui", ".ass", ".huy", ".xuy", ".adm", "adm.", "-adm", "hui", "huy", "hyi", "04ko", "o4ko", "bdsm", "bdcm", "ceo", "cto", "mod", ".mod", "hlpr", "help", "cuka", "own", "hax"));

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      NetworkPlayer player = VimeNetwork.getPlayer(sender.getName());
      if (!player.getRank().has(Permission.PREFIX)) {
         U.msg(sender, T.error("VimeWorld", "Изменять префикс могут только " + Rank.IMMORTAL.getDisplayName()));
         return false;
      } else if (args.length == 0) {
         U.msg(sender, "&e=========== &fПрефикс &e===========", "&aВы можете установить себе префикс из 4-х букв.", "&fЗапрещено использовать оскорбительные префиксы или префиксы, которые будут выдавать вас за администрацию. &cЗа такие префиксы вы можете получить бан.", "&a/prefix <префикс>&f - Установка префикса", "&a/prefix reset&f - Удаление префикса");
         return false;
      } else {
         String prefix = args[0];
         if (prefix.equalsIgnoreCase("reset")) {
            this.setPrefix(player, (String)null);
            U.msg(sender, "&aВы успешно удалили свой префикс");
            return false;
         } else if (prefix.length() >= 2 && prefix.length() <= 4) {
            if (!this.pattern.matcher(prefix).matches()) {
               U.msg(sender, "&cПрефикс может состоять только из английских букв, цифр и знаков '-' '_' '.'");
               return false;
            } else if (this.blacklist.contains(prefix.toLowerCase())) {
               U.msg(sender, "&cЭтот префикс находится в черном списке");
               return false;
            } else {
               this.setPrefix(player, prefix);
               U.msg(sender, "&aВы успешно изменили свой префикс на " + prefix);
               return false;
            }
         } else {
            U.msg(sender, "&cПрефикс может быть длиной от 2 до 4 букв");
            return false;
         }
      }
   }

   private void setPrefix(NetworkPlayer player, String prefix) {
      player.setMeta("prefix", prefix);
      player.getBukkitPlayer().setDisplayName(player.getPrefixedName());
   }
}
