package net.xtrafrancyz.BedWars.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import net.xtrafrancyz.BedWars.BWTeam;
import net.xtrafrancyz.BedWars.Config;
import net.xtrafrancyz.BedWars.PlayerInfo;
import net.xtrafrancyz.BedWars.TournamentUI;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.util.Spectators;
import net.xtrafrancyz.bukkit.texteria.Texteria2D;
import net.xtrafrancyz.bukkit.texteria.elements.Element;
import net.xtrafrancyz.bukkit.texteria.elements.ProgressBar;
import net.xtrafrancyz.bukkit.texteria.elements.ProgressTimer;
import net.xtrafrancyz.bukkit.texteria.elements.Rectangle;
import net.xtrafrancyz.bukkit.texteria.elements.Text;
import net.xtrafrancyz.bukkit.texteria.elements.TextTimer;
import net.xtrafrancyz.bukkit.texteria.elements.Vignette;
import net.xtrafrancyz.bukkit.texteria.utils.Animation2D;
import net.xtrafrancyz.bukkit.texteria.utils.Attachment;
import net.xtrafrancyz.bukkit.texteria.utils.IntColor;
import net.xtrafrancyz.bukkit.texteria.utils.Position;
import net.xtrafrancyz.bukkit.texteria.utils.Visibility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BTexteria {
   private static final String T_KILL_VIG = "bw.kill.vig";
   private static final String T_KILL_MSG = "bw.kill.msg";
   private static final String T_LOBBY_BAR = "bw.lobby.bar";
   private static final String T_LOBBY_MSG = "bw.lobby.msg";
   private static final String T_BED_VIG = "bw.bed.vig";
   private static final String T_BED_MSG = "bw.bed.msg";
   private static final String T_END = "bw.end";
   private static final String T_CUSTOM_MESSAGE = "bw.cmsg";
   private static final String T_CUSTOM_TIMER = "bw.ct";
   private static final String T_CUSTOM_TIMER_MSG = "bw.ct.msg";
   private static final String T_MAPLEAVE = "bw.ml";
   private static final String T_MAPLEAVE_MSG = "bw.ml.msg";
   private static final String T_MAPLEAVE_TIMER = "bw.ml.tmr";
   public static final int DEFAULT_BAR_COLOR = IntColor.setAlpha(-16737025, 200);
   private static String primaryMessage = null;
   private static long primaryStartTime = 0L;
   private static long primaryDuration = 0L;

   public static void onBedBreak(BWTeam team, PlayerInfo breaker) {
      bedBreakMsgToTeam(team, breaker);
      Texteria2D.add(((Text)((Text)(new Text("bw.bed.msg", new String[]{"Вы разрушили", team.chatColor + team.names[1] + " кровать"})).setDuration(7000L)).setOffset(0, -2)).setScale(3.0F), new Player[]{breaker.player});
   }

   public static void bedBreakMsgToTeam(BWTeam team, PlayerInfo breaker) {
      String[] text;
      if (breaker != null) {
         text = new String[]{"Ваша кровать разрушена игроком", breaker.team.chatColor + breaker.username};
      } else {
         text = new String[]{"Ваша кровать разрушена"};
      }

      Texteria2D.add(new Element[]{((Vignette)(new Vignette("bw.bed.vig")).setColor(-65536)).setDuration(610L), ((Text)((Text)(new Text("bw.bed.msg", text)).setDuration(7000L)).setOffset(0, -2)).setScale(3.0F)}, team.getBukkitPlayers());
   }

   public static void onLoose(PlayerInfo player) {
      VimeNetwork.texteria().showDefeat(new Player[]{player.player});
   }

   public static void onGameEnd(BWTeam winner) {
      if (winner == null) {
         VimeNetwork.texteria().showTie(Bukkit.getOnlinePlayers());
      } else {
         String message = "Победила " + winner.chatColor + winner.names[0] + " команда!";
         LinkedList<PlayerInfo> players = new LinkedList(PlayerInfo.PLAYERS.values());
         winner.players.forEach(players::remove);
         Texteria2D.add(((Text)((Text)(new Text("bw.end", new String[]{message})).setDuration(7000L)).setOffset(0, -2)).setScale(3.0F), (Player[])players.stream().map((p) -> p.player).toArray((x$0) -> new Player[x$0]));
         VimeNetwork.texteria().showVictory((Player[])winner.players.stream().map((p) -> p.player).toArray((x$0) -> new Player[x$0]));
      }

   }

   public static void onPlayerKill(PlayerInfo killer, String target) {
      Texteria2D.add(new Element[]{((Vignette)(new Vignette("bw.kill.vig")).setColor(-9830551)).setDuration(600L), ((Text)((Text)((Text)(new Text("bw.kill.msg", new String[]{"Вы убили игрока " + target})).setOffset(0, 50)).setScale(2.0F)).setDuration(2000L)).setPosition(Position.BOTTOM)}, new Player[]{killer.player});
   }

   public static void showTimer(String message, long duration) {
      Texteria2D.add(new Element[]{((Rectangle)((Rectangle)((Rectangle)(new ProgressTimer("bw.lobby.bar", 180, 10)).setBarColor(-1).setDuration(duration)).setOffset(0, 60)).setPosition(Position.BOTTOM)).setColor(DEFAULT_BAR_COLOR), ((Text)((Text)(new Text("bw.lobby.msg", new String[]{message})).setDuration(duration)).setOffset(0, 75)).setPosition(Position.BOTTOM)}, Bukkit.getOnlinePlayers());
   }

   public static void showPlayersToStart() {
      if (!VimeNetwork.isTournament()) {
         Player[] players = Bukkit.getOnlinePlayers();
         Texteria2D.add(new Element[]{((Rectangle)((Rectangle)(new ProgressBar("bw.lobby.bar", 180, 10, (float)players.length / (float)Config.getMaxPlayers())).setBarColor(-1).setPosition(Position.BOTTOM)).setOffset(0, 60)).setColor(DEFAULT_BAR_COLOR), ((Text)(new Text("bw.lobby.msg", new String[]{"Игроков: &e" + players.length + "/" + Config.getMaxPlayers()})).setOffset(0, 75)).setPosition(Position.BOTTOM)}, players);
      }
   }

   public static void showCustomMessage(Player player, String message, int color, long duration) {
      Texteria2D.add(((Text)((Text)((Text)((Text)((Text)(new Text("bw.cmsg", new String[]{message})).setColor(color)).setOffset(0, 50)).setScale(2.0F)).setDuration(duration)).setPosition(Position.TOP)).setVisibility(new Visibility.IngameNotTab()), new Player[]{player});
   }

   public static void showCustomTimer(Player player, String message, int color, long duration, boolean reverse) {
      Texteria2D.add(new Visibility.IngameNotTab(), new Element[]{((Rectangle)((Rectangle)((Rectangle)((Rectangle)((Rectangle)(new ProgressTimer("bw.ct", 200, 3)).setReverse(reverse).setOffset(0, 16)).setDuration(duration)).setPosition(Position.TOP)).setAnimation((new Animation2D()).setBoth((new Animation2D.Params()).setY(20)))).setFade(500)).setColor(color), ((Text)((Text)(new TextTimer("bw.ct.msg", new String[]{message})).setFade(500)).setAttachment(new Attachment("bw.ct", Position.BOTTOM))).setOffset(0, 1)}, new Player[]{player});
   }

   public static void removeCustomTimer(Player player) {
      Texteria2D.removeGroup("bw.ct", new Player[]{player});
   }

   public static void showPrimaryTopTimer(String message, long startTime, long duration) {
      primaryMessage = message;
      primaryStartTime = startTime;
      primaryDuration = duration;
      showPrimaryTopTimer(message, startTime, duration, Bukkit.getOnlinePlayers());
   }

   public static void showPrimaryTopTimer(Player player) {
      showPrimaryTopTimer(primaryMessage, primaryStartTime, primaryDuration, player);
   }

   private static void showPrimaryTopTimer(String message, long startTime, long duration, Player... players) {
      if (TournamentUI.instance != null) {
         List<Player> copy = new ArrayList(Arrays.asList(players));
         Spectators var10001 = Spectators.instance();
         copy.removeIf(var10001::contains);
         players = (Player[])copy.toArray(new Player[copy.size()]);
      }

      int delay = (int)(startTime - System.currentTimeMillis());
      Texteria2D.add(new Visibility.IngameNotTab(), new Element[]{((Rectangle)((Rectangle)((Rectangle)((Rectangle)((Rectangle)((Rectangle)(new ProgressTimer("bw.pt.t", 200, 3)).setOffset(0, 13)).setDelay(delay)).setDuration(duration)).setPosition(Position.TOP)).setAnimation((new Animation2D()).setBoth((new Animation2D.Params()).setY(-20)))).setFade(500)).setColor(IntColor.setAlpha(-14575885, 200)), ((Text)((Text)(new TextTimer("bw.pt.t.t", new String[]{message})).setFade(500)).setAttachment(new Attachment("bw.pt.t", Position.TOP))).setOffset(0, -1)}, players);
   }

   public static void onMapLeave(Player player, long duration) {
      Texteria2D.add(new Element[]{((Vignette)(new Vignette("bw.ml")).setColor(-44205)).setDuration(duration), ((Text)((Text)(new TextTimer("bw.ml.msg", new String[]{"Вы вышли за край карты.", "Через &e{S}.{m} с.&f вы будете возвращены", "на свою базу"})).setPosition(Position.TOP)).setDuration(duration)).setOffset(0, 37), ((Rectangle)((Rectangle)((Rectangle)(new ProgressTimer("bw.ml.tmr", 210, 8)).setColor(DEFAULT_BAR_COLOR)).setPosition(Position.TOP)).setDuration(duration)).setOffset(0, 28)}, new Player[]{player});
   }

   public static void removeMapLeave(Player player) {
      Texteria2D.removeGroup("bw.ml", new Player[]{player});
   }
}
