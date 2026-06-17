package net.xtrafrancyz.VimeNetwork.commands;

import net.xtrafrancyz.Core.network.packet.Packet56StreamAction;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.command.CmdSub;
import net.xtrafrancyz.VimeNetwork.api.command.CommandRoot;
import net.xtrafrancyz.VimeNetwork.api.command.SubCommandData;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class StreamCommand extends CommandRoot {
   protected void runCommand(Runnable action, CommandSender sender, Command cmd, String label, String[] args) {
      if (!VimeNetwork.core().isConnected()) {
         U.msg(sender, "&cВ данный момент команда не работает. Попробуйте позже");
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
      ranks = {Rank.YOUTUBE, Rank.ADMIN},
      hidden = true
   )
   protected void add(SubCommandData data) {
      if (data.getArgs().length != 1) {
         U.msg(data.getSender(), "&cИспользование: /" + data.getLabel() + " " + data.getSub() + " <ссылка на стрим>");
      } else {
         VimeNetwork.core().sendPacket(new Packet56StreamAction(data.getSender().getName(), data.getArgs()[0], Packet56StreamAction.Action.ADD));
      }

   }

   @CmdSub(
      value = {"remove"},
      ranks = {Rank.YOUTUBE, Rank.ADMIN},
      hidden = true
   )
   protected void remove(SubCommandData data) {
      if (data.getArgs().length != 1) {
         U.msg(data.getSender(), "&cИспользование: /" + data.getLabel() + " " + data.getSub() + " <ссылка на стрим>");
      } else {
         VimeNetwork.core().sendPacket(new Packet56StreamAction(data.getSender().getName(), data.getArgs()[0], Packet56StreamAction.Action.REMOVE));
      }

   }

   @CmdSub({"list"})
   protected void list(SubCommandData data) {
      VNPlugin.instance().streamMenu.show(data.getPlayer());
   }

   @CmdSub({"help"})
   protected void help(SubCommandData data) {
      U.msg(data.getSender(), "&e---------- &fСтримы&e ---------------");
      if (this.hasPerm(data.getSender())) {
         U.msg(data.getSender(), "Поддерживаются сервисы: YouTube, Twitch, vk.com, GoodGame.ru");
         U.msg(data.getSender(), "&e/" + data.getLabel() + "&7 add <ссылка на стрим>&f: Добавление стрима");
         U.msg(data.getSender(), "&e/" + data.getLabel() + "&7 remove <ссылка на стрим>&f: Удаление стрима");
      }

      U.msg(data.getSender(), "&e/" + data.getLabel() + "&7 list&f: Список стримов");
   }

   private boolean hasPerm(CommandSender sender) {
      switch (VimeNetwork.getPlayer(sender.getName()).getRank()) {
         case YOUTUBE:
         case ADMIN:
            return true;
         default:
            return false;
      }
   }
}
