/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.bukkit.texteria.Texteria2D
 *  net.xtrafrancyz.bukkit.texteria.elements.Button
 *  net.xtrafrancyz.bukkit.texteria.elements.Element
 *  net.xtrafrancyz.bukkit.texteria.elements.Rectangle
 *  net.xtrafrancyz.bukkit.texteria.elements.Text
 *  net.xtrafrancyz.bukkit.texteria.elements.TextTimer
 *  net.xtrafrancyz.bukkit.texteria.utils.Animation2D
 *  net.xtrafrancyz.bukkit.texteria.utils.Animation2D$Params
 *  net.xtrafrancyz.bukkit.texteria.utils.Attachment
 *  net.xtrafrancyz.bukkit.texteria.utils.ByteMap
 *  net.xtrafrancyz.bukkit.texteria.utils.IntColor
 *  net.xtrafrancyz.bukkit.texteria.utils.OnClick
 *  net.xtrafrancyz.bukkit.texteria.utils.OnClick$Action
 *  net.xtrafrancyz.bukkit.texteria.utils.Position
 *  net.xtrafrancyz.bukkit.texteria.utils.Visibility
 *  net.xtrafrancyz.bukkit.texteria.utils.Visibility$Always
 *  net.xtrafrancyz.bukkit.texteria.utils.Visibility$IngameNotF3
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork;

import java.util.List;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.player.goals.Goal;
import net.xtrafrancyz.VimeNetwork.impl.StreamMenu;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import net.xtrafrancyz.bukkit.texteria.Texteria2D;
import net.xtrafrancyz.bukkit.texteria.elements.Button;
import net.xtrafrancyz.bukkit.texteria.elements.Element;
import net.xtrafrancyz.bukkit.texteria.elements.Rectangle;
import net.xtrafrancyz.bukkit.texteria.elements.Text;
import net.xtrafrancyz.bukkit.texteria.elements.TextTimer;
import net.xtrafrancyz.bukkit.texteria.utils.Animation2D;
import net.xtrafrancyz.bukkit.texteria.utils.Attachment;
import net.xtrafrancyz.bukkit.texteria.utils.ByteMap;
import net.xtrafrancyz.bukkit.texteria.utils.IntColor;
import net.xtrafrancyz.bukkit.texteria.utils.OnClick;
import net.xtrafrancyz.bukkit.texteria.utils.Position;
import net.xtrafrancyz.bukkit.texteria.utils.Visibility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class VTexteria {
    private static final String T_COINS = "vn.c.";
    private static final String T_COINS_X = "vn.c.x";
    private static final String T_COINS_TEXT = "vn.c.text";
    private static final String T_COINS_CHANGE = "vn.c.change";
    public static final String T_NOTIFICATION = "vn.n.";
    private static final String T_NOTIFICATION_BG = "vn.n.bg";
    private static final String T_NOTIFICATION_TITLE = "vn.n.title";
    private static final String T_NOTIFICATION_TEXT = "vn.n.text";
    private static final String T_NOTIFICATION_TEXT2 = "vn.n.text2";
    private static final Animation2D ANIM_GIVE_EXP = new Animation2D().setFinish(new Animation2D.Params().setY(-30));
    private static final Animation2D ANIM_ADD_COINS = new Animation2D().setFinish(new Animation2D.Params().setY(30));
    private static final Animation2D ANIM_NOTIFICATION = new Animation2D().setBoth(new Animation2D.Params().setX(-150));
    private static final Text TC_SERVER_ID = (Text)((Text)((Text)new Text("vn.s", new String[]{VimeNetwork.lobby().getServerId()}).setPosition(Position.TOP_LEFT)).setOffset(2, 12)).setVisibility((Visibility)new Visibility.IngameNotF3());
    private static final ByteMap CALLBACK_OPEN_GOALS = new ByteMap();
    private static final ByteMap CALLBACK_OPEN_STREAMS = new ByteMap();

    public static void showServerId(Player ... players) {
        Texteria2D.add((Element)TC_SERVER_ID, (Player[])players);
    }

    public static void showUsername(VPlayer player) {
        String text = player.username;
        if (player.getLevel() != 0) {
            text = "[&e" + player.getLevel() + "&r] " + text;
        }
        Texteria2D.add((Element)((Text)((Text)new Text("vn.n", new String[]{text}).setPosition(Position.TOP_LEFT)).setOffset(2, 2)).setVisibility((Visibility)new Visibility.IngameNotF3()), (Player[])new Player[]{player.player});
    }

    public static void showGiveExp(VPlayer player, int amount) {
        Texteria2D.add((Element)((Text)((Text)((Text)((Text)((Text)new Text(null, new String[]{"&a+" + String.valueOf(amount)}).setFadeStart(300)).setFadeFinish(500)).setDuration(1200L)).setOffset(4, 0)).setAnimation(ANIM_GIVE_EXP)).setAttachment(new Attachment("vn.n", Position.RIGHT).setRemoveWhenParentRemove(false)), (Player[])new Player[]{player.player});
    }

    public static void showCoins(VPlayer player) {
        Text elem;
        String text = "\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u043a\u043e\u0438\u043d\u043e\u0432: " + (player.getCoins() < 0 ? "&c" : "&e") + player.getCoins();
        float mult = player.getMultipliers().getCurrentMultiplier();
        if (mult != 1.0f) {
            long to;
            elem = (Text)new Text(T_COINS_TEXT, new String[]{text}).setAttachment(new Attachment(T_COINS_X, Position.RIGHT));
            int prev = mult != player.coinsTexteriaMult ? 0 : player.coinsTexteriaMultView;
            Text x = null;
            if (player.multipliers.isActivated() && (to = player.multipliers.getExtraEndTime() - System.currentTimeMillis()) > 0L) {
                String timeFormat = "{H}\u0447. {M}\u043c.";
                player.coinsTexteriaMultView = 1;
                if (to > 86400000L) {
                    timeFormat = "{D}\u0434. {H}\u0447.";
                    player.coinsTexteriaMultView = 2;
                } else if (to <= 3601000L) {
                    timeFormat = "{M}\u043c.";
                    player.coinsTexteriaMultView = 3;
                }
                if (prev != player.coinsTexteriaMultView) {
                    x = new TextTimer(T_COINS_X, new String[]{"&e[&dx" + player.getMultipliers().getFormattedMultiplier() + "&f - " + timeFormat + "&e]&f "}).setTimerDuration(to);
                }
            } else {
                player.coinsTexteriaMultView = 4;
                if (prev != player.coinsTexteriaMultView) {
                    x = new Text(T_COINS_X, new String[]{"&e[&dx" + player.getMultipliers().getFormattedMultiplier() + "&e]&f "});
                }
            }
            if (x != null) {
                player.coinsTexteriaMult = mult;
                Texteria2D.add((Element)((Text)((Text)x.setPosition(Position.BOTTOM_LEFT)).setOffset(2, 16)).setVisibility((Visibility)new Visibility.Always()), (Player[])new Player[]{player.player});
            }
        } else {
            elem = (Text)((Text)new Text(T_COINS_TEXT, new String[]{text}).setOffset(2, 16)).setPosition(Position.BOTTOM_LEFT);
        }
        Texteria2D.add((Element)elem.setVisibility((Visibility)new Visibility.Always()), (Player[])new Player[]{player.player});
    }

    public static void showAchievementMessage(Achievement achievement, Player player) {
        Texteria2D.add((Element[])new Element[]{((Text)((Text)new Text("a1", new String[]{achievement.getName()}).setScale(4.0f)).setDuration(5000L)).setOffset(-10, -20), ((Text)((Text)new Text("a2", achievement.getDescription()).setScale(2.0f)).setOffset(0, 10)).setDuration(5000L)}, (Player[])new Player[]{player});
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)VNPlugin.instance(), () -> Texteria2D.add((Element)((Text)((Text)((Text)((Text)new Text("a3", new String[]{"&a\u2714"}).setAttachment(new Attachment("a1", Position.RIGHT))).setDuration(4250L)).setScale(4.0f)).setOffset(15, 0)).setFadeStart(400), (Player[])new Player[]{player}), 15L);
    }

    public static void showGuildInvite(Player player, String inviter, String guild) {
        Attachment att = new Attachment(T_NOTIFICATION_BG, Position.TOP_LEFT).setOrientation(Position.BOTTOM_RIGHT);
        Texteria2D.remove((String)T_NOTIFICATION, (Player[])new Player[]{player});
        Texteria2D.add((Visibility)new Visibility.Always(), (Element[])new Element[]{((Rectangle)((Rectangle)((Rectangle)((Rectangle)((Rectangle)new Rectangle(T_NOTIFICATION_BG, 142, 43).setColor(-14575885)).setFade(500)).setOffset(2, 12)).setPosition(Position.TOP_LEFT)).setDuration(8000L)).setAnimation(ANIM_NOTIFICATION), ((Text)((Text)new Text(T_NOTIFICATION_TITLE, new String[]{"\u041f\u0440\u0438\u0433\u043b\u0430\u0448\u0435\u043d\u0438\u0435 \u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u044e"}).setOffset(5, 2)).setFade(500)).setAttachment(att), ((Text)((Text)((Text)new Text(T_NOTIFICATION_TEXT, new String[]{"&e" + guild + "&f \u043e\u0442"}).setOffset(5, 14)).setFade(500)).setAttachment(att)).setOrientation(1), ((Text)((Text)((Text)new Text("vn.n.text4", new String[]{"&r&l" + inviter}).setOffset(5, 26)).setFade(500)).setAttachment(att)).setOrientation(1), ((Button)((Button)((Button)((Button)new Button(T_NOTIFICATION_TEXT2, 50, 11, "\u041f\u0440\u0438\u043d\u044f\u0442\u044c").setColor(-12858815)).setHoverColor(-10757535).setOffset(-4, -4)).setFade(500)).setOnClick(new OnClick(OnClick.Action.CHAT, (Object)("/g acpt " + inviter)))).setAttachment(new Attachment(T_NOTIFICATION_BG, Position.BOTTOM_RIGHT).setOrientation(Position.TOP_LEFT))}, (Player[])new Player[]{player});
    }

    public static void showPartyInvite(Player player, String inviter) {
        Attachment att = new Attachment(T_NOTIFICATION_BG, Position.TOP_LEFT).setOrientation(Position.BOTTOM_RIGHT);
        Texteria2D.remove((String)T_NOTIFICATION, (Player[])new Player[]{player});
        Texteria2D.add((Visibility)new Visibility.Always(), (Element[])new Element[]{((Rectangle)((Rectangle)((Rectangle)((Rectangle)((Rectangle)new Rectangle(T_NOTIFICATION_BG, 142, 43).setColor(-14575885)).setFade(500)).setOffset(2, 12)).setPosition(Position.TOP_LEFT)).setDuration(8000L)).setAnimation(ANIM_NOTIFICATION), ((Text)((Text)new Text(T_NOTIFICATION_TITLE, new String[]{"\u041f\u0440\u0438\u0433\u043b\u0430\u0448\u0435\u043d\u0438\u0435 \u0432 \u0433\u0440\u0443\u043f\u043f\u0443"}).setOffset(5, 2)).setFade(500)).setAttachment(att), ((Text)((Text)((Text)new Text(T_NOTIFICATION_TEXT, new String[]{"&r\u041e\u0442 \u0438\u0433\u0440\u043e\u043a\u0430 &l" + inviter}).setOffset(5, 14)).setFade(500)).setAttachment(att)).setOrientation(1), ((Button)((Button)((Button)((Button)new Button(T_NOTIFICATION_TEXT2, 50, 11, "\u041f\u0440\u0438\u043d\u044f\u0442\u044c").setColor(-12858815)).setHoverColor(-10757535).setOffset(-4, -4)).setFade(500)).setOnClick(new OnClick(OnClick.Action.CHAT, (Object)("/p j " + inviter)))).setAttachment(new Attachment(T_NOTIFICATION_BG, Position.BOTTOM_RIGHT).setOrientation(Position.TOP_LEFT))}, (Player[])new Player[]{player});
    }

    public static void showFriendRequest(Player player, String requester) {
        Attachment att = new Attachment(T_NOTIFICATION_BG, Position.TOP_LEFT).setOrientation(Position.BOTTOM_RIGHT);
        Texteria2D.remove((String)T_NOTIFICATION, (Player[])new Player[]{player});
        Texteria2D.add((Visibility)new Visibility.Always(), (Element[])new Element[]{((Rectangle)((Rectangle)((Rectangle)((Rectangle)((Rectangle)new Rectangle(T_NOTIFICATION_BG, 142, 43).setColor(-14575885)).setFade(500)).setOffset(2, 12)).setPosition(Position.TOP_LEFT)).setDuration(8000L)).setAnimation(ANIM_NOTIFICATION), ((Text)((Text)new Text(T_NOTIFICATION_TITLE, new String[]{"\u0417\u0430\u043f\u0440\u043e\u0441 \u0432 \u0434\u0440\u0443\u0437\u044c\u044f"}).setOffset(5, 2)).setFade(500)).setAttachment(att), ((Text)((Text)((Text)new Text(T_NOTIFICATION_TEXT, new String[]{"&r\u041e\u0442 \u0438\u0433\u0440\u043e\u043a\u0430 &l" + requester}).setOffset(5, 14)).setFade(500)).setAttachment(att)).setOrientation(1), ((Button)((Button)((Button)((Button)new Button(T_NOTIFICATION_TEXT2, 50, 11, "\u041e\u0442\u043a\u043b\u043e\u043d\u0438\u0442\u044c").setColor(-769226)).setHoverColor(-37269).setOffset(-58, -4)).setFade(500)).setOnClick(new OnClick(OnClick.Action.CHAT, (Object)("/f dny " + requester)))).setAttachment(new Attachment(T_NOTIFICATION_BG, Position.BOTTOM_RIGHT).setOrientation(Position.TOP_LEFT)), ((Button)((Button)((Button)((Button)new Button("vn.n.text3", 50, 11, "\u041f\u0440\u0438\u043d\u044f\u0442\u044c").setColor(-12858815)).setHoverColor(-10757535).setOffset(-4, -4)).setFade(500)).setOnClick(new OnClick(OnClick.Action.CHAT, (Object)("/f acpt " + requester)))).setAttachment(new Attachment(T_NOTIFICATION_BG, Position.BOTTOM_RIGHT).setOrientation(Position.TOP_LEFT))}, (Player[])new Player[]{player});
    }

    public static void showCoinsChange(VPlayer player, int amount) {
        if (amount > 0) {
            Texteria2D.add((Element)((Text)((Text)((Text)((Text)((Text)((Text)new Text(T_COINS_CHANGE, new String[]{"+" + amount}).setColor(-16711936)).setOffset(1, 0)).setFadeFinish(500)).setDuration(1200L)).setVisibility((Visibility)new Visibility.Always())).setAnimation(ANIM_ADD_COINS)).setAttachment(new Attachment(T_COINS_TEXT, Position.RIGHT).setRemoveWhenParentRemove(false)), (Player[])new Player[]{player.player});
        } else if (amount < 0) {
            Texteria2D.add((Element)((Text)((Text)((Text)((Text)((Text)((Text)new Text(T_COINS_CHANGE, new String[]{String.valueOf(amount)}).setColor(-65536)).setOffset(1, 0)).setFadeFinish(500)).setDuration(1200L)).setVisibility((Visibility)new Visibility.Always())).setAnimation(ANIM_ADD_COINS)).setAttachment(new Attachment(T_COINS_TEXT, Position.RIGHT).setRemoveWhenParentRemove(false)), (Player[])new Player[]{player.player});
        }
    }

    public static void showStreamMessage(List<StreamMenu.StreamerData> streams, Player[] players) {
        VTexteria.streamMessage(players, IntColor.setAlpha((int)-14575885, (int)200), "&l\u0410\u043a\u0442\u0438\u0432\u043d\u044b\u0445 \u0441\u0442\u0440\u0438\u043c\u043e\u0432: " + streams.size(), new String[0]);
    }

    public static void showNewStreamer(StreamMenu.StreamerData streamer, Player[] players) {
        VTexteria.streamMessage(players, IntColor.setAlpha((int)-14575885, (int)200), "&l\u041d\u043e\u0432\u044b\u0439 \u0441\u0442\u0440\u0438\u043c \u043d\u0430 " + streamer.sortedStreams().get((int)0).platform, "\u0418\u0433\u0440\u043e\u043a &e" + streamer.owner + "&f \u0432\u0435\u0434\u0451\u0442", "\u0441\u0442\u0440\u0438\u043c. \u0417\u0440\u0438\u0442\u0435\u043b\u0435\u0439: &e" + streamer.getTotalViewers());
    }

    private static void streamMessage(Player[] players, int color, String title, String ... text) {
        Attachment att = new Attachment(T_NOTIFICATION_BG, Position.TOP_LEFT).setOrientation(Position.BOTTOM_RIGHT);
        Texteria2D.remove((String)T_NOTIFICATION, (Player[])players);
        Texteria2D.add((Visibility)new Visibility.Always(), (Element[])new Element[]{((Rectangle)((Rectangle)((Rectangle)((Rectangle)((Rectangle)((Rectangle)new Rectangle(T_NOTIFICATION_BG, 142, 17 + 9 * text.length + 12).setColor(color)).setFade(500)).setOffset(2, 12)).setPosition(Position.TOP_LEFT)).setDuration(8000L)).setAnimation(ANIM_NOTIFICATION)).setOnClick(new OnClick(OnClick.Action.CALLBACK, (Object)CALLBACK_OPEN_STREAMS)), ((Text)((Text)new Text(T_NOTIFICATION_TITLE, new String[]{title}).setOffset(5, 2)).setFade(500)).setAttachment(att), ((Text)((Text)((Text)new Text(T_NOTIFICATION_TEXT, text).setOffset(5, 14)).setFade(500)).setAttachment(att)).setOrientation(1), ((Text)((Text)new Text(T_NOTIFICATION_TEXT2, new String[]{"\u041d\u0430\u043f\u0438\u0448\u0438\u0442\u0435 &l/streams&f \u0434\u043b\u044f \u043f\u0440\u043e\u0441\u043c\u043e\u0442\u0440\u0430"}).setOffset(5, 17 + 9 * text.length)).setFade(500)).setAttachment(att)}, (Player[])players);
    }

    public static void showGoalComplete(NetworkPlayer player, Goal goal) {
        VTexteria.goalMessage(player.getBukkitPlayer(), IntColor.setAlpha((int)-11751600, (int)200), "&l\u0412\u044b \u0432\u044b\u043f\u043e\u043b\u043d\u0438\u043b\u0438 \u0437\u0430\u0434\u0430\u043d\u0438\u0435:", VTexteria.getGoalText(goal));
    }

    public static void showGoalAdded(NetworkPlayer player, Goal goal) {
        String[] text = VTexteria.getGoalText(goal);
        Attachment att = new Attachment(T_NOTIFICATION_BG, Position.TOP_LEFT).setOrientation(Position.BOTTOM_RIGHT);
        Texteria2D.remove((String)T_NOTIFICATION, (Player[])new Player[]{player.getBukkitPlayer()});
        Texteria2D.add((Visibility)new Visibility.Always(), (Element[])new Element[]{((Rectangle)((Rectangle)((Rectangle)((Rectangle)((Rectangle)new Rectangle(T_NOTIFICATION_BG, 132, 19 + 9 * text.length + 12).setColor(IntColor.setAlpha((int)-14575885, (int)200))).setFade(500)).setOffset(2, 12)).setPosition(Position.TOP_LEFT)).setDuration(6000L)).setAnimation(ANIM_NOTIFICATION), ((Text)((Text)new Text(T_NOTIFICATION_TITLE, new String[]{"&l\u0414\u043e\u0431\u0430\u0432\u043b\u0435\u043d\u043e \u043d\u043e\u0432\u043e\u0435 \u0437\u0430\u0434\u0430\u043d\u0438\u0435:"}).setOffset(5, 2)).setFade(500)).setAttachment(att), ((Text)((Text)((Text)new Text(T_NOTIFICATION_TEXT, text).setOffset(5, 14)).setFade(500)).setAttachment(att)).setOrientation(1), ((Button)((Button)((Button)((Button)new Button(T_NOTIFICATION_TEXT2, 50, 11, "\u041e\u0442\u043a\u0440\u044b\u0442\u044c").setColor(-10177034)).setHoverColor(-12409355).setOffset(-4, -4)).setFade(500)).setOnClick(new OnClick(OnClick.Action.CALLBACK, (Object)CALLBACK_OPEN_GOALS))).setAttachment(new Attachment(T_NOTIFICATION_BG, Position.BOTTOM_RIGHT).setOrientation(Position.TOP_LEFT))}, (Player[])new Player[]{player.getBukkitPlayer()});
    }

    public static void showGoalMessage(NetworkPlayer player) {
        VTexteria.goalMessage(player.getBukkitPlayer(), IntColor.setAlpha((int)-16537100, (int)200), "&l\u0410\u043a\u0442\u0438\u0432\u043d\u043e \u0437\u0430\u0434\u0430\u043d\u0438\u0439: " + player.getGoals().getActiveGoals().size(), "\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u043f\u0440\u043e\u0441\u043c\u043e\u0442\u0440\u0430");
    }

    public static void showGoalExpired(NetworkPlayer player, Goal goal) {
        VTexteria.goalMessage(player.getBukkitPlayer(), IntColor.setAlpha((int)-769226, (int)200), "&l\u0417\u0430\u0434\u0430\u043d\u0438\u0435 \u043f\u0440\u043e\u0441\u0440\u043e\u0447\u0435\u043d\u043e: ", VTexteria.getGoalText(goal));
    }

    private static String[] getGoalText(Goal goal) {
        List<String> text = goal.getText(true);
        return text.toArray(new String[text.size()]);
    }

    private static void goalMessage(Player player, int color, String title, String ... text) {
        Attachment att = new Attachment(T_NOTIFICATION_BG, Position.TOP_LEFT).setOrientation(Position.BOTTOM_RIGHT);
        Texteria2D.remove((String)T_NOTIFICATION, (Player[])new Player[]{player});
        Texteria2D.add((Visibility)new Visibility.Always(), (Element[])new Element[]{((Rectangle)((Rectangle)((Rectangle)((Rectangle)((Rectangle)((Rectangle)new Rectangle(T_NOTIFICATION_BG, 132, 17 + 9 * text.length).setColor(color)).setFade(500)).setOffset(2, 12)).setPosition(Position.TOP_LEFT)).setDuration(6000L)).setAnimation(ANIM_NOTIFICATION)).setOnClick(new OnClick(OnClick.Action.CALLBACK, (Object)CALLBACK_OPEN_GOALS)), ((Text)((Text)new Text(T_NOTIFICATION_TITLE, new String[]{title}).setOffset(5, 2)).setFade(500)).setAttachment(att), ((Text)((Text)((Text)new Text(T_NOTIFICATION_TEXT, text).setOffset(5, 14)).setFade(500)).setAttachment(att)).setOrientation(1)}, (Player[])new Player[]{player});
    }

    static {
        CALLBACK_OPEN_GOALS.put((Object)"module", (Object)"VimeNetwork");
        CALLBACK_OPEN_GOALS.put((Object)"action", (Object)"goals-inv");
        CALLBACK_OPEN_STREAMS.put((Object)"module", (Object)"VimeNetwork");
        CALLBACK_OPEN_STREAMS.put((Object)"action", (Object)"streams-inv");
    }
}

