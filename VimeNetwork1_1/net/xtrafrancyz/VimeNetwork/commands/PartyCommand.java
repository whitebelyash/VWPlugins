package net.xtrafrancyz.VimeNetwork.commands;

import com.google.common.base.Joiner;
import net.xtrafrancyz.Core.network.packet.Packet58PartyAction;
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

public class PartyCommand extends CommandRoot {
   private CoreBukkit core = VimeNetwork.core();

   protected void runCommand(Runnable action, CommandSender sender, Command cmd, String label, String[] args) {
      if (!VimeNetwork.core().isConnected()) {
         U.msg(sender, "&cВ данный момент команда не работает. Попробуйте позже");
      } else if (VimeNetwork.getPlayer(sender.getName()).getId() == -1) {
         U.msg(sender, "&cДанные загружаются, подождите немного...");
      } else {
         super.runCommand(action, sender, cmd, label, args);
      }
   }

   protected boolean main(CommandSender sender, Command cmd, String label, String[] args) {
      if (args.length == 0) {
         this.help(new SubCommandData(sender, label, "help", new String[0]));
      } else {
         this.core.sendPacket(new Packet58PartyAction(VimeNetwork.getPlayer(sender.getName()).getId(), Packet58PartyAction.Action.MESSAGE, Joiner.on(" ").join(args)));
      }

      return false;
   }

   @CmdSub(
      value = {"invite"},
      aliases = {"i"}
   )
   protected void invite(SubCommandData data) {
      if (data.getArgs().length != 1) {
         U.msg(data.getSender(), "&cИспользование: /" + data.getLabel() + " " + data.getSub() + " <игрок>");
      } else if (data.getArgs()[0].equalsIgnoreCase(data.getSender().getName())) {
         U.msg(data.getSender(), "&cВы не можете пригласить себя");
      } else {
         this.core.sendPacket(new Packet58PartyAction(this.getPlayerId(data), Packet58PartyAction.Action.INVITE, data.getArgs()[0]));
      }
   }

   @CmdSub(
      value = {"join"},
      aliases = {"j"}
   )
   protected void join(SubCommandData data) {
      if (data.getArgs().length != 1) {
         U.msg(data.getSender(), "&cИспользование: /" + data.getLabel() + " " + data.getSub() + " <ник лидера>");
      } else if (data.getArgs()[0].equalsIgnoreCase(data.getSender().getName())) {
         U.msg(data.getSender(), "&cВы не можете присоединиться к своей же группе");
      } else {
         if (data.getSub().equals("j")) {
            Texteria2D.removeGroup("vn.n.", new Player[]{data.getPlayer()});
         }

         this.core.sendPacket(new Packet58PartyAction(this.getPlayerId(data), Packet58PartyAction.Action.JOIN, data.getArgs()[0]));
      }
   }

   @CmdSub(
      value = {"kick"},
      aliases = {"k"}
   )
   protected void kick(SubCommandData data) {
      if (data.getArgs().length != 1) {
         U.msg(data.getSender(), "&cИспользование: /" + data.getLabel() + " " + data.getSub() + " <игрок>");
      } else if (data.getArgs()[0].equalsIgnoreCase(data.getSender().getName())) {
         U.msg(data.getSender(), "&cВы не можете исключить себя из группы");
      } else {
         this.core.sendPacket(new Packet58PartyAction(this.getPlayerId(data), Packet58PartyAction.Action.KICK, data.getArgs()[0]));
      }
   }

   @CmdSub({"leave"})
   protected void leave(SubCommandData data) {
      this.core.sendPacket(new Packet58PartyAction(this.getPlayerId(data), Packet58PartyAction.Action.LEAVE));
   }

   @CmdSub(
      value = {"disband"},
      aliases = {"d"}
   )
   protected void disband(SubCommandData data) {
      this.core.sendPacket(new Packet58PartyAction(this.getPlayerId(data), Packet58PartyAction.Action.DISBAND));
   }

   @CmdSub(
      value = {"warp"},
      aliases = {"w"}
   )
   protected void warp(SubCommandData data) {
      this.core.sendPacket(new Packet58PartyAction(this.getPlayerId(data), Packet58PartyAction.Action.WARP));
   }

   @CmdSub(
      value = {"list"},
      aliases = {"l"}
   )
   protected void list(SubCommandData data) {
      this.core.sendPacket(new Packet58PartyAction(this.getPlayerId(data), Packet58PartyAction.Action.LIST));
   }

   @CmdSub(
      value = {"promote"},
      aliases = {"p"}
   )
   protected void promote(SubCommandData data) {
      if (data.getArgs().length != 1) {
         U.msg(data.getSender(), "&cИспользование: /" + data.getLabel() + " " + data.getSub() + " <игрок>");
      } else if (data.getArgs()[0].equalsIgnoreCase(data.getSender().getName())) {
         U.msg(data.getSender(), "&cВы не можете сделать себя лидером");
      } else {
         this.core.sendPacket(new Packet58PartyAction(this.getPlayerId(data), Packet58PartyAction.Action.PROMOTE, data.getArgs()[0]));
      }
   }

   @CmdSub({"help"})
   protected void help(SubCommandData data) {
      U.msg(data.getSender(), "&e---------- &fГруппы (/party /p)&e ---------------", "&e/" + data.getLabel() + "&7 <сообщение>&f: отправить сообщение всей группе", "&e/" + data.getLabel() + "&7 invite&4(i)&7 <игрок>&f: пригласить игрока в группу", "&e/" + data.getLabel() + "&7 kick&4(k)&7 <игрок>&f: исключить игрока из группы", "&e/" + data.getLabel() + "&7 join&4(j)&7 <ник лидера>&f: вступить в группу игрока", "&e/" + data.getLabel() + "&7 promote&4(p)&7 <игрок>&f: сделать игрока новым лидером группы", "&e/" + data.getLabel() + "&7 list&4(l)&f: список игроков в группе", "&e/" + data.getLabel() + "&7 warp&4(w)&f: переносит всех членов группы на ваш сервер", "&e/" + data.getLabel() + "&7 leave&f: выйти из текущей группы", "&e/" + data.getLabel() + "&7 disband&4(d)&f: распустить вашу группу");
   }

   private int getPlayerId(SubCommandData data) {
      return VimeNetwork.getPlayer(data.getSender().getName()).getId();
   }
}
