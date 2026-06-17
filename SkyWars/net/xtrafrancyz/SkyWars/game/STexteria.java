/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.Commons.F
 *  net.xtrafrancyz.VimeNetwork.api.VimeNetwork
 *  net.xtrafrancyz.bukkit.texteria.Texteria2D
 *  net.xtrafrancyz.bukkit.texteria.elements.Element
 *  net.xtrafrancyz.bukkit.texteria.elements.ProgressBar
 *  net.xtrafrancyz.bukkit.texteria.elements.ProgressTimer
 *  net.xtrafrancyz.bukkit.texteria.elements.Rectangle
 *  net.xtrafrancyz.bukkit.texteria.elements.Text
 *  net.xtrafrancyz.bukkit.texteria.elements.TextTimer
 *  net.xtrafrancyz.bukkit.texteria.elements.Vignette
 *  net.xtrafrancyz.bukkit.texteria.utils.Animation2D
 *  net.xtrafrancyz.bukkit.texteria.utils.Animation2D$Params
 *  net.xtrafrancyz.bukkit.texteria.utils.Attachment
 *  net.xtrafrancyz.bukkit.texteria.utils.IntColor
 *  net.xtrafrancyz.bukkit.texteria.utils.Position
 *  net.xtrafrancyz.bukkit.texteria.utils.Visibility
 *  net.xtrafrancyz.bukkit.texteria.utils.Visibility$IngameNotTab
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.SkyWars.game;

import java.util.LinkedList;
import java.util.Set;
import net.xtrafrancyz.Commons.F;
import net.xtrafrancyz.SkyWars.Config;
import net.xtrafrancyz.SkyWars.Island;
import net.xtrafrancyz.SkyWars.PlayerInfo;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
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

public class STexteria {
    private static final String T_KILL_VIG = "sw.kill.vig";
    private static final String T_KILL_MSG = "sw.kill.msg";
    private static final String T_LOBBY_BAR = "sw.lobby.bar";
    private static final String T_LOBBY_MSG = "sw.lobby.msg";
    private static final String T_DAMAGE = "sw.dmg";
    private static final String T_END = "sw.end";
    private static final String T_CUSTOM_MESSAGE = "sw.cmsg";
    private static final String T_CUSTOM_TIMER = "sw.ct";
    private static final String T_CUSTOM_TIMER_MSG = "sw.ct.msg";
    public static final int DEFAULT_BAR_COLOR = IntColor.setAlpha((int)-16737025, (int)200);
    private static String primaryMessage = null;
    private static long primaryStartTime = 0L;
    private static long primaryDuration = 0L;

    public static void onDefeat(PlayerInfo player) {
        VimeNetwork.texteria().showDefeat(new Player[]{player.player});
    }

    public static void onGameEnd(Island winner, Set<String> whitelist) {
        if (winner == null) {
            VimeNetwork.texteria().showTie(Bukkit.getOnlinePlayers());
        } else {
            String message;
            if (winner.players.size() == 1) {
                message = "\u041f\u043e\u0431\u0435\u0434\u0438\u043b \u0438\u0433\u0440\u043e\u043a " + winner.players.get((int)0).username;
            } else {
                message = "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u0438: " + winner.players.get((int)0).username;
                for (int i = 1; i < winner.players.size(); ++i) {
                    message = message + ", " + winner.players.get((int)i).username;
                }
            }
            LinkedList<PlayerInfo> players = new LinkedList<PlayerInfo>(PlayerInfo.PLAYERS.values());
            players.removeAll(winner.players);
            Texteria2D.add((Element)((Text)((Text)new Text(T_END, new String[]{message}).setDuration(7000L)).setOffset(0, -2)).setScale(3.0f), (Player[])((Player[])players.stream().filter(p -> !whitelist.contains(p.username)).map(p -> p.player).toArray(Player[]::new)));
            VimeNetwork.texteria().showVictory((Player[])winner.players.stream().filter(p -> !whitelist.contains(p.username)).map(p -> p.player).toArray(Player[]::new));
        }
    }

    public static void showDamage(Player damager, double damage) {
        Texteria2D.add((Element)((Text)((Text)new Text(T_DAMAGE, new String[]{"\u0423\u0440\u043e\u043d: &c" + F.formatFloat((float)((float)(damage / 2.0)), (int)1) + "\u2764"}).setOffset(0, 10)).setDuration(1500L)).setVisibility((Visibility)new Visibility.IngameNotTab()), (Player[])new Player[]{damager});
    }

    public static void onPlayerKill(PlayerInfo killer, PlayerInfo target) {
        Texteria2D.add((Element[])new Element[]{((Vignette)new Vignette(T_KILL_VIG).setColor(-9830551)).setDuration(600L), ((Text)((Text)((Text)new Text(T_KILL_MSG, new String[]{"\u0412\u044b \u0443\u0431\u0438\u043b\u0438 \u0438\u0433\u0440\u043e\u043a\u0430 " + target.player.getDisplayName()}).setOffset(0, 50)).setScale(2.0f)).setDuration(2000L)).setPosition(Position.BOTTOM)}, (Player[])new Player[]{killer.player});
    }

    public static void showBaseTimer(String message, long duration) {
        Texteria2D.add((Element[])new Element[]{((Rectangle)((Rectangle)((Rectangle)new ProgressTimer(T_LOBBY_BAR, 180, 10).setBarColor(-1).setDuration(duration)).setOffset(0, 60)).setPosition(Position.BOTTOM)).setColor(DEFAULT_BAR_COLOR), ((Text)((Text)new Text(T_LOBBY_MSG, new String[]{message}).setDuration(duration)).setOffset(0, 75)).setPosition(Position.BOTTOM)}, (Player[])Bukkit.getOnlinePlayers());
    }

    public static void showPlayersToStart() {
        Player[] players = Bukkit.getOnlinePlayers();
        Texteria2D.add((Element[])new Element[]{((Rectangle)((Rectangle)new ProgressBar(T_LOBBY_BAR, 180, 10, (float)players.length / (float)Config.getMaxPlayers()).setBarColor(-1).setPosition(Position.BOTTOM)).setOffset(0, 60)).setColor(DEFAULT_BAR_COLOR), ((Text)new Text(T_LOBBY_MSG, new String[]{"\u0418\u0433\u0440\u043e\u043a\u043e\u0432: &e" + players.length + "/" + Config.getMaxPlayers()}).setOffset(0, 75)).setPosition(Position.BOTTOM)}, (Player[])players);
    }

    public static void showCustomMessage(Player[] players, String message, int color, long duration) {
        Texteria2D.add((Element)((Text)((Text)((Text)((Text)((Text)new Text(T_CUSTOM_MESSAGE, new String[]{message}).setColor(color)).setOffset(0, 40)).setScale(2.0f)).setDuration(duration)).setPosition(Position.TOP)).setVisibility((Visibility)new Visibility.IngameNotTab()), (Player[])players);
    }

    public static void showCustomTimer(Player player, String message, int color, long duration, boolean reverse) {
        Texteria2D.add((Element[])new Element[]{((Text)((Text)new TextTimer(T_CUSTOM_TIMER_MSG, new String[]{message}).setPosition(Position.TOP)).setDuration(duration)).setOffset(0, 7), ((Rectangle)((Rectangle)((Rectangle)new ProgressTimer(T_CUSTOM_TIMER, 210, 8).setReverse(reverse).setColor(color)).setPosition(Position.TOP)).setDuration(duration)).setOffset(0, 20)}, (Player[])new Player[]{player});
    }

    public static void removeCustomTimer(Player player) {
        Texteria2D.removeGroup((String)T_CUSTOM_TIMER, (Player[])new Player[]{player});
    }

    public static void showPrimaryTopTimer(String message, long startTime, long duration) {
        primaryMessage = message;
        primaryStartTime = startTime;
        primaryDuration = duration;
        STexteria.showPrimaryTopTimer(message, startTime, duration, Bukkit.getOnlinePlayers());
    }

    public static void showPrimaryTopTimer(Player player) {
        STexteria.showPrimaryTopTimer(primaryMessage, primaryStartTime, primaryDuration, player);
    }

    private static void showPrimaryTopTimer(String message, long startTime, long duration, Player ... players) {
        int delay = (int)(startTime - System.currentTimeMillis());
        Texteria2D.add((Visibility)new Visibility.IngameNotTab(), (Element[])new Element[]{((Rectangle)((Rectangle)((Rectangle)((Rectangle)((Rectangle)((Rectangle)new ProgressTimer("sw.s.timer", 200, 3).setOffset(0, 13)).setDelay(delay)).setDuration(duration)).setPosition(Position.TOP)).setAnimation(new Animation2D().setBoth(new Animation2D.Params().setY(-20)))).setFade(500)).setColor(IntColor.setAlpha((int)-14575885, (int)200)), ((Text)((Text)new TextTimer("sw.s.timer.t", new String[]{message}).setFade(500)).setAttachment(new Attachment("sw.s.timer", Position.TOP))).setOffset(0, -1)}, (Player[])players);
    }

    public static void showMysteryTopTimer(String message, long duration) {
        Texteria2D.add((Visibility)new Visibility.IngameNotTab(), (Element[])new Element[]{((Rectangle)((Rectangle)((Rectangle)((Rectangle)((Rectangle)new ProgressTimer("sw.p.timer", 200, 3).setOffset(0, 16)).setDuration(duration)).setPosition(Position.TOP)).setAnimation(new Animation2D().setBoth(new Animation2D.Params().setY(20)))).setFade(500)).setColor(IntColor.setAlpha((int)-6543440, (int)200)), ((Text)((Text)new TextTimer("sw.p.timer.t", new String[]{message}).setFade(500)).setAttachment(new Attachment("sw.p.timer", Position.BOTTOM))).setOffset(0, 1)}, (Player[])Bukkit.getOnlinePlayers());
    }
}

