package net.xtrafrancyz.VimeNetwork.api;

import net.minecraft.server.v1_6_R3.Packet250CustomPayload;
import net.xtrafrancyz.Core.CoreByteMap;
import net.xtrafrancyz.Core.network.packet.Packet61SendPlayerToServer;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.holo.Holograms;
import net.xtrafrancyz.VimeNetwork.api.mysql.MysqlThread;
import net.xtrafrancyz.VimeNetwork.api.npc.NPCs;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.Permission;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.updater.UpdateWatcher;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.VPlayerManager;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import net.xtrafrancyz.VimeNetwork.packet.BungeeBridge;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.entity.Player;

public class VimeNetwork {
   public static final Gson gson = new Gson();

   private VimeNetwork() {
   }

   public static boolean isDev() {
      return VNPlugin.instance().config.dev;
   }

   public static boolean isTournament() {
      return VNPlugin.instance().config.tournament;
   }

   public static Lobby lobby() {
      return VNPlugin.instance().lobby;
   }

   public static Features features() {
      return Features.inst;
   }

   public static VimeTexteria texteria() {
      return VimeTexteria.inst;
   }

   public static MysqlThread mysql() {
      return VNPlugin.instance().mysql;
   }

   public static CoreBukkit core() {
      return VNPlugin.instance().core;
   }

   public static UpdateWatcher updateWatcher() {
      return VNPlugin.instance().updateWatcher;
   }

   public static Metrics metrics() {
      return VNPlugin.instance().metrics;
   }

   public static Holograms holograms() {
      return VNPlugin.instance().holograms;
   }

   public static NPCs npcs() {
      return VNPlugin.instance().npcs;
   }

   public static boolean hasRank(CommandSender who, Rank rank, boolean inform) {
      return who instanceof ConsoleCommandSender || getPlayer(who.getName()).getRank().has(inform ? (Player)who : null, (Rank)rank);
   }

   public static boolean hasPermission(CommandSender who, Permission permission, boolean inform) {
      return who instanceof ConsoleCommandSender || getPlayer(who.getName()).getRank().has(inform ? (Player)who : null, (Permission)permission);
   }

   public static NetworkPlayer getPlayer(String player) {
      return VPlayer.get(player);
   }

   public static NetworkPlayer getPlayer(Player player) {
      return VPlayer.get(player);
   }

   public static NetworkPlayer getPlayer(int userid) {
      return (NetworkPlayer)VPlayer.IDS.get(userid);
   }

   public static boolean isPlayerOnline(String player) {
      return VPlayer.PLAYERS.containsKey(player);
   }

   public static boolean isPlayerOnline(Player player) {
      return VPlayer.PLAYERS.containsKey(player.getName());
   }

   public static boolean isPlayerOnline(int userid) {
      return VPlayer.IDS.containsKey(userid);
   }

   public static void addCommandHelp(String command, String help) {
      VNPlugin.instance().help.addCommand(command, help);
   }

   public static void addCommandHelp(String command, String help, Rank rank) {
      VNPlugin.instance().help.addCommand(command, help, rank);
   }

   public static void addCommandHelp(String command, String help, Permission permission) {
      VNPlugin.instance().help.addCommand(command, help, permission);
   }

   public static void ban(String player, int minutes, String reason, String banner) {
      VPlayerManager.ban(player, minutes, reason, banner);
   }

   public static void mute(String username, int minutes, String reason, String muter) {
      VPlayerManager.mute(username, minutes, reason, muter);
   }

   public static void unmute(String username, String unmuter) {
      VPlayerManager.unmute(username, unmuter);
   }

   public static void logAction(String username, String action) {
      VPlayerManager.logAction(username, action, (String)null, (String)null);
   }

   public static void logAction(String username, String action, String target) {
      VPlayerManager.logAction(username, action, target, (String)null);
   }

   public static void logAction(String username, String action, String target, String comment) {
      VPlayerManager.logAction(username, action, target, comment);
   }

   public static void setWindowTitle(Player player, String title) {
      U.sendPacket(player, new Packet250CustomPayload("Vime", ("setTitle:" + title).getBytes()));
   }

   public static void toLobby(Player... players) {
      for(Player player : players) {
         BungeeBridge.toLobby(player);
      }

   }

   public static void toServer(String server, Player... players) {
      for(Player player : players) {
         BungeeBridge.toServer(player, server);
      }

   }

   public static void toServer(String server, Player player, CoreByteMap switchData) {
      if (core().isConnected() && switchData != null && !switchData.isEmpty()) {
         core().sendPacket(new Packet61SendPlayerToServer(player.getName(), server, switchData));
      } else {
         toServer(server, player);
      }

   }
}
