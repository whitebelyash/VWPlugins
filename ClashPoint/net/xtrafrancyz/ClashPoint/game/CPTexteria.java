/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.VimeNetwork
 *  net.xtrafrancyz.VimeNetwork.api.util.Spectators
 *  net.xtrafrancyz.bukkit.texteria.Texteria2D
 *  net.xtrafrancyz.bukkit.texteria.Texteria3D
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
 *  net.xtrafrancyz.bukkit.texteria.world.Beam
 *  net.xtrafrancyz.bukkit.texteria.world.WorldGroup
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.ClashPoint.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import net.xtrafrancyz.ClashPoint.Config;
import net.xtrafrancyz.ClashPoint.TournamentUI;
import net.xtrafrancyz.ClashPoint.object.CPTeam;
import net.xtrafrancyz.ClashPoint.object.PlayerInfo;
import net.xtrafrancyz.ClashPoint.object.ResourcePoint;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.util.Spectators;
import net.xtrafrancyz.bukkit.texteria.Texteria2D;
import net.xtrafrancyz.bukkit.texteria.Texteria3D;
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
import net.xtrafrancyz.bukkit.texteria.world.Beam;
import net.xtrafrancyz.bukkit.texteria.world.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CPTexteria {
    private static final String T_KILL_VIG = "cp.kill.vig";
    private static final String T_KILL_MSG = "cp.kill.msg";
    private static final String T_LOBBY_BAR = "cp.lobby.bar";
    private static final String T_LOBBY_MSG = "cp.lobby.msg";
    private static final String T_RP_VIG = "cp.rp.vig";
    private static final String T_RP_MSG = "cp.rp.msg";
    private static final String T_END = "cp.end";
    private static final String T_CUSTOM_MESSAGE = "cp.cmsg";
    private static final String T_CUSTOM_TIMER = "cp.ct";
    private static final String T_CUSTOM_TIMER_MSG = "cp.ct.msg";
    private static final String T_MAPLEAVE = "cp.ml";
    private static final String T_MAPLEAVE_MSG = "cp.ml.msg";
    private static final String T_MAPLEAVE_TIMER = "cp.ml.tmr";
    public static final int DEFAULT_BAR_COLOR = IntColor.setAlpha((int)-16737025, (int)200);
    private static String primaryMessage = null;
    private static long primaryStartTime = 0L;
    private static long primaryDuration = 0L;

    public static void onPointBreak(CPTeam team, PlayerInfo breaker) {
        CPTexteria.pointBreakMsgToTeam(team, breaker);
        Texteria2D.add((Element)((Text)((Text)new Text(T_RP_MSG, new String[]{"\u0412\u044b \u0440\u0430\u0437\u0440\u0443\u0448\u0438\u043b\u0438", team.chatColor + team.names[1] + " \u0442\u043e\u0447\u043a\u0443 \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432"}).setDuration(7000L)).setOffset(0, -2)).setScale(3.0f), (Player[])new Player[]{breaker.player});
    }

    public static void pointBreakMsgToTeam(CPTeam team, PlayerInfo breaker) {
        String[] text = breaker != null ? new String[]{"\u0412\u0430\u0448\u0430 \u0442\u043e\u0447\u043a\u0430 \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432 \u0440\u0430\u0437\u0440\u0443\u0448\u0435\u043d\u0430 \u0438\u0433\u0440\u043e\u043a\u043e\u043c", breaker.team.chatColor + breaker.username} : new String[]{"\u0412\u0430\u0448\u0430 \u0442\u043e\u0447\u043a\u0430 \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432 \u0440\u0430\u0437\u0440\u0443\u0448\u0435\u043d\u0430"};
        Texteria2D.add((Element[])new Element[]{((Vignette)new Vignette(T_RP_VIG).setColor(-65536)).setDuration(610L), ((Text)((Text)new Text(T_RP_MSG, text).setDuration(7000L)).setOffset(0, -2)).setScale(3.0f)}, (Player[])team.getBukkitPlayers());
    }

    public static void onPointDamage(ResourcePoint rp, PlayerInfo damager) {
        Player[] players = rp.getTeam().getBukkitPlayers();
        Texteria2D.add((Element[])new Element[]{((Text)((Text)((Text)new Text(T_RP_MSG, new String[]{"\u0412\u0430\u0441 \u0430\u0442\u0430\u043a\u0443\u0435\u0442 " + damager.team.chatColor + damager.username + "&f! \u0417\u0434\u043e\u0440\u043e\u0432\u044c\u0435: " + rp.getHealthBar()}).setOffset(0, 60)).setScale(2.0f)).setPosition(Position.BOTTOM)).setDuration(5000L), ((Vignette)new Vignette(T_RP_VIG).setColor(-65536)).setDuration(610L)}, (Player[])players);
        Beam beam = new Beam("cp.rp" + rp.getId(), -1);
        beam.setLocation((float)rp.getLocation().getBlockX() + 0.5f, (float)rp.getLocation().getBlockY() + 2.5f, (float)rp.getLocation().getBlockZ() + 0.5f);
        beam.setDuration(3000L);
        Texteria3D.addGroup((WorldGroup)beam, (Player[])players);
    }

    public static void onLoose(PlayerInfo player) {
        VimeNetwork.texteria().showDefeat(new Player[]{player.player});
    }

    public static void onGameEnd(CPTeam winner) {
        if (winner == null) {
            VimeNetwork.texteria().showTie(Bukkit.getOnlinePlayers());
        } else {
            String message = "\u041f\u043e\u0431\u0435\u0434\u0438\u043b\u0430 " + winner.chatColor + winner.names[0] + " \u043a\u043e\u043c\u0430\u043d\u0434\u0430!";
            LinkedList<PlayerInfo> players = new LinkedList<PlayerInfo>(PlayerInfo.PLAYERS.values());
            players.removeAll(winner.players);
            Texteria2D.add((Element)((Text)((Text)new Text(T_END, new String[]{message}).setDuration(7000L)).setOffset(0, -2)).setScale(3.0f), (Player[])((Player[])players.stream().map(p -> p.player).toArray(Player[]::new)));
            VimeNetwork.texteria().showVictory((Player[])winner.players.stream().map(p -> p.player).toArray(Player[]::new));
        }
    }

    public static void onPlayerKill(PlayerInfo killer, PlayerInfo target) {
        Texteria2D.add((Element[])new Element[]{((Vignette)new Vignette(T_KILL_VIG).setColor(-9830551)).setDuration(600L), ((Text)((Text)((Text)new Text(T_KILL_MSG, new String[]{"\u0412\u044b \u0443\u0431\u0438\u043b\u0438 \u0438\u0433\u0440\u043e\u043a\u0430 " + target.player.getDisplayName()}).setOffset(0, 50)).setScale(2.0f)).setDuration(2000L)).setPosition(Position.BOTTOM)}, (Player[])new Player[]{killer.player});
    }

    public static void showTimer(String message, long duration) {
        Texteria2D.add((Element[])new Element[]{((Rectangle)((Rectangle)((Rectangle)new ProgressTimer(T_LOBBY_BAR, 180, 10).setBarColor(-1).setDuration(duration)).setOffset(0, 60)).setPosition(Position.BOTTOM)).setColor(DEFAULT_BAR_COLOR), ((Text)((Text)new Text(T_LOBBY_MSG, new String[]{message}).setDuration(duration)).setOffset(0, 75)).setPosition(Position.BOTTOM)}, (Player[])Bukkit.getOnlinePlayers());
    }

    public static void showPlayersToStart() {
        if (VimeNetwork.isTournament()) {
            return;
        }
        Player[] players = Bukkit.getOnlinePlayers();
        Texteria2D.add((Element[])new Element[]{((Rectangle)((Rectangle)new ProgressBar(T_LOBBY_BAR, 180, 10, (float)players.length / (float)Config.getMaxPlayers()).setBarColor(-1).setPosition(Position.BOTTOM)).setOffset(0, 60)).setColor(DEFAULT_BAR_COLOR), ((Text)new Text(T_LOBBY_MSG, new String[]{"\u0418\u0433\u0440\u043e\u043a\u043e\u0432: &e" + players.length + "/" + Config.getMaxPlayers()}).setOffset(0, 75)).setPosition(Position.BOTTOM)}, (Player[])players);
    }

    public static void showCustomMessage(Player player, String message, int color, long duration) {
        CPTexteria.showCustomMessage(player, message, color, duration, (Visibility)new Visibility.IngameNotTab());
    }

    public static void showCustomMessage(Player player, String message, int color, long duration, Visibility vis) {
        Texteria2D.add((Element)((Text)((Text)((Text)((Text)((Text)new Text(T_CUSTOM_MESSAGE, new String[]{message}).setColor(color)).setOffset(0, 50)).setScale(2.0f)).setDuration(duration)).setPosition(Position.TOP)).setVisibility(vis), (Player[])new Player[]{player});
    }

    public static void showCustomTimer(Player player, String message, int color, long duration, boolean reverse) {
        Texteria2D.add((Visibility)new Visibility.IngameNotTab(), (Element[])new Element[]{((Rectangle)((Rectangle)((Rectangle)((Rectangle)((Rectangle)new ProgressTimer(T_CUSTOM_TIMER, 200, 3).setReverse(reverse).setOffset(0, 16)).setDuration(duration)).setPosition(Position.TOP)).setAnimation(new Animation2D().setBoth(new Animation2D.Params().setY(20)))).setFade(500)).setColor(color), ((Text)((Text)new TextTimer(T_CUSTOM_TIMER_MSG, new String[]{message}).setFade(500)).setAttachment(new Attachment(T_CUSTOM_TIMER, Position.BOTTOM))).setOffset(0, 1)}, (Player[])new Player[]{player});
    }

    public static void removeCustomTimer(Player player) {
        Texteria2D.removeGroup((String)T_CUSTOM_TIMER, (Player[])new Player[]{player});
    }

    public static void showPrimaryTopTimer(String message, long startTime, long duration) {
        primaryMessage = message;
        primaryStartTime = startTime;
        primaryDuration = duration;
        CPTexteria.showPrimaryTopTimer(message, startTime, duration, Bukkit.getOnlinePlayers());
    }

    public static void showPrimaryTopTimer(Player player) {
        CPTexteria.showPrimaryTopTimer(primaryMessage, primaryStartTime, primaryDuration, player);
    }

    private static void showPrimaryTopTimer(String message, long startTime, long duration, Player ... players) {
        if (TournamentUI.instance != null) {
            ArrayList<Player> copy = new ArrayList<Player>(Arrays.asList(players));
            copy.removeIf(arg_0 -> ((Spectators)Spectators.instance()).contains(arg_0));
            players = copy.toArray(new Player[copy.size()]);
        }
        int delay = (int)(startTime - System.currentTimeMillis());
        Texteria2D.add((Visibility)new Visibility.IngameNotTab(), (Element[])new Element[]{((Rectangle)((Rectangle)((Rectangle)((Rectangle)((Rectangle)((Rectangle)new ProgressTimer("cp.pt.t", 200, 3).setOffset(0, 13)).setDelay(delay)).setDuration(duration)).setPosition(Position.TOP)).setAnimation(new Animation2D().setBoth(new Animation2D.Params().setY(-20)))).setFade(500)).setColor(IntColor.setAlpha((int)-14575885, (int)200)), ((Text)((Text)new TextTimer("cp.pt.t.t", new String[]{message}).setFade(500)).setAttachment(new Attachment("cp.pt.t", Position.TOP))).setOffset(0, -1)}, (Player[])players);
    }

    public static void showSecondatyTopTimer(String message, long duration) {
        Texteria2D.add((Visibility)new Visibility.IngameNotTab(), (Element[])new Element[]{((Rectangle)((Rectangle)((Rectangle)((Rectangle)((Rectangle)new ProgressTimer("cp.st", 200, 3).setOffset(0, 16)).setDuration(duration)).setPosition(Position.TOP)).setAnimation(new Animation2D().setBoth(new Animation2D.Params().setY(20)))).setFade(500)).setColor(IntColor.setAlpha((int)-6543440, (int)200)), ((Text)((Text)new TextTimer("cp.st.t", new String[]{message}).setFade(500)).setAttachment(new Attachment("cp.st", Position.BOTTOM))).setOffset(0, 1)}, (Player[])Bukkit.getOnlinePlayers());
    }

    public static void onMapLeave(Player player, long duration) {
        Texteria2D.add((Element[])new Element[]{((Vignette)new Vignette(T_MAPLEAVE).setColor(-44205)).setDuration(duration), ((Text)((Text)new TextTimer(T_MAPLEAVE_MSG, new String[]{"\u0412\u044b \u0432\u044b\u0448\u043b\u0438 \u0437\u0430 \u043a\u0440\u0430\u0439 \u043a\u0430\u0440\u0442\u044b.", "\u0427\u0435\u0440\u0435\u0437 &e{S}.{m} \u0441.&f \u0432\u044b \u0431\u0443\u0434\u0435\u0442\u0435 \u0432\u043e\u0437\u0432\u0440\u0430\u0449\u0435\u043d\u044b", "\u043d\u0430 \u0441\u0432\u043e\u044e \u0431\u0430\u0437\u0443"}).setPosition(Position.TOP)).setDuration(duration)).setOffset(0, 37), ((Rectangle)((Rectangle)((Rectangle)new ProgressTimer(T_MAPLEAVE_TIMER, 210, 8).setColor(DEFAULT_BAR_COLOR)).setPosition(Position.TOP)).setDuration(duration)).setOffset(0, 28)}, (Player[])new Player[]{player});
    }

    public static void removeMapLeave(Player player) {
        Texteria2D.removeGroup((String)T_MAPLEAVE, (Player[])new Player[]{player});
    }
}

