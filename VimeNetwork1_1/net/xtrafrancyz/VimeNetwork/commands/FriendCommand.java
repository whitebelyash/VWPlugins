package net.xtrafrancyz.VimeNetwork.commands;

import net.xtrafrancyz.Core.network.packet.Packet62FriendAction;
import net.xtrafrancyz.VimeNetwork.api.CoreBukkit;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.command.CmdSub;
import net.xtrafrancyz.VimeNetwork.api.command.CommandRoot;
import net.xtrafrancyz.VimeNetwork.api.command.SubCommandData;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.bukkit.texteria.Texteria2D;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FriendCommand extends CommandRoot {
   private CoreBukkit core = VimeNetwork.core();

   protected void runCommand(Runnable action, CommandSender sender, Command cmd, String label, String[] args) {
      if (!this.core.isConnected()) {
         U.msg(sender, "&cВ данный момент команда не работает. Попробуйте позже");
      } else if (VimeNetwork.getPlayer(sender.getName()).getId() == -1) {
         U.msg(sender, "&cДанные загружаются, подождите немного...");
      } else {
         super.runCommand(action, sender, cmd, label, args);
      }
   }

   protected boolean main(CommandSender sender, Command cmd, String label, String[] args) {
      this.help(new SubCommandData(sender, label, "help", new String[0]));
      return false;
   }

   @CmdSub(
      value = {"add"},
      aliases = {"a"}
   )
   protected void add(SubCommandData data) {
      if (data.getArgs().length != 1) {
         U.msg(data.getSender(), "&cИспользование: /" + data.getLabel() + " add <ник игрока>");
      } else if (data.getArgs()[0].equalsIgnoreCase(data.getSender().getName())) {
         U.msg(data.getSender(), "&cВы не можете добавить себя в друзья");
      } else {
         this.core.sendPacket(new Packet62FriendAction(this.getPlayerId(data), Packet62FriendAction.Action.ADD, data.getArgs()[0]));
      }
   }

   @CmdSub(
      value = {"accept"},
      aliases = {"acpt"}
   )
   protected void accept(SubCommandData data) {
      if (data.getArgs().length != 1) {
         U.msg(data.getSender(), "&cИспользование: /" + data.getLabel() + " accept <ник игрока>");
      } else if (data.getArgs()[0].equalsIgnoreCase(data.getSender().getName())) {
         U.msg(data.getSender(), "&cВы не можете принять заявку в друзья от себя");
      } else {
         if (data.getSub().equals("acpt")) {
            Texteria2D.removeGroup("vn.n.", new Player[]{data.getPlayer()});
         }

         this.core.sendPacket(new Packet62FriendAction(this.getPlayerId(data), Packet62FriendAction.Action.ACCEPT, data.getArgs()[0]));
      }
   }

   @CmdSub(
      value = {"deny"},
      aliases = {"d", "dny"}
   )
   protected void deny(SubCommandData data) {
      if (data.getArgs().length != 1) {
         U.msg(data.getSender(), "&cИспользование: /" + data.getLabel() + " deny <ник игрока>");
      } else if (data.getArgs()[0].equalsIgnoreCase(data.getSender().getName())) {
         U.msg(data.getSender(), "&cВы не можете отклонить заявку в друзья от себя");
      } else {
         if (data.getSub().equals("dny")) {
            Texteria2D.removeGroup("vn.n.", new Player[]{data.getPlayer()});
         }

         this.core.sendPacket(new Packet62FriendAction(this.getPlayerId(data), Packet62FriendAction.Action.DENY, data.getArgs()[0]));
      }
   }

   @CmdSub(
      value = {"remove"},
      aliases = {"r"}
   )
   protected void remove(SubCommandData data) {
      if (data.getArgs().length != 1) {
         U.msg(data.getSender(), "&cИспользование: /" + data.getLabel() + " remove <ник друга>");
      } else if (data.getArgs()[0].equalsIgnoreCase(data.getSender().getName())) {
         U.msg(data.getSender(), "&cВы не можете удалить себя из друзей");
      } else {
         this.core.sendPacket(new Packet62FriendAction(this.getPlayerId(data), Packet62FriendAction.Action.REMOVE, data.getArgs()[0]));
      }
   }

   @CmdSub(
      value = {"list"},
      aliases = {"l"}
   )
   protected void list(SubCommandData data) {
      if (data.getArgs().length > 1) {
         U.msg(data.getSender(), "&cИспользование: /" + data.getLabel() + " list [страница]");
      } else {
         String page = "1";
         if (data.getArgs().length == 1) {
            page = data.getArgs()[0];

            try {
               Integer.parseInt(page);
            } catch (NumberFormatException var4) {
               U.msg(data.getSender(), "&cНомер страницы должен быть числом, это же логично");
               return;
            }
         }

         this.core.sendPacket(new Packet62FriendAction(this.getPlayerId(data), Packet62FriendAction.Action.LIST, page));
      }
   }

   @CmdSub(
      value = {"help"},
      hidden = true
   )
   protected void help(SubCommandData data) {
      U.msg(data.getSender(), "&e---------- &2Друзья &f(&e/friend /f&f)&e ---------------", "&e/" + data.getLabel() + "&7 add&4(a)&7 <игрок>&f: добавить игрока в друзья", "&e/" + data.getLabel() + "&7 accept <игрок>&f: принять запрос на дружбу", "&e/" + data.getLabel() + "&7 deny&4(d)&7 <игрок>&f: отклонить запрос на дружбу", "&e/" + data.getLabel() + "&7 remove&4(r)&7 <игрок>&f: удалить друга", "&e/" + data.getLabel() + "&7 list&4(l)&7 [страница]&f: список ваших друзей");
   }

   private int getPlayerId(SubCommandData data) {
      return VimeNetwork.getPlayer(data.getSender().getName()).getId();
   }
}
