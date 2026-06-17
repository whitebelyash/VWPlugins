/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.bukkit.texteria.Texteria2D
 *  net.xtrafrancyz.bukkit.texteria.elements.Element
 *  net.xtrafrancyz.bukkit.texteria.elements.Image
 *  net.xtrafrancyz.bukkit.texteria.elements.Rectangle
 *  net.xtrafrancyz.bukkit.texteria.elements.Text
 *  net.xtrafrancyz.bukkit.texteria.utils.Animation2D
 *  net.xtrafrancyz.bukkit.texteria.utils.Animation2D$Params
 *  net.xtrafrancyz.bukkit.texteria.utils.OnClick
 *  net.xtrafrancyz.bukkit.texteria.utils.OnClick$Action
 *  net.xtrafrancyz.bukkit.texteria.utils.Position
 *  net.xtrafrancyz.bukkit.texteria.utils.Visibility
 *  net.xtrafrancyz.bukkit.texteria.utils.Visibility$Always
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.api;

import java.util.function.Supplier;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.bukkit.texteria.Texteria2D;
import net.xtrafrancyz.bukkit.texteria.elements.Element;
import net.xtrafrancyz.bukkit.texteria.elements.Image;
import net.xtrafrancyz.bukkit.texteria.elements.Rectangle;
import net.xtrafrancyz.bukkit.texteria.elements.Text;
import net.xtrafrancyz.bukkit.texteria.utils.Animation2D;
import net.xtrafrancyz.bukkit.texteria.utils.OnClick;
import net.xtrafrancyz.bukkit.texteria.utils.Position;
import net.xtrafrancyz.bukkit.texteria.utils.Visibility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class VimeTexteria {
    private static final String T_GROUP = "vn.api.";
    private static final String T_TOPMSG = "vn.api.topmsg";
    private static final String T_VICTORY = "vn.api.victory";
    private static final String T_DEFEAT = "vn.api.defeat";
    private static final String T_TIE = "vn.api.tie";
    static final VimeTexteria inst = new VimeTexteria();

    private VimeTexteria() {
    }

    public void showInsufficientlyCoins(Player player) {
        Texteria2D.add((Element)this.genInvErrorMessage(5000L, "&c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u043a\u043e\u0438\u043d\u043e\u0432", "\u0418\u0445 \u043c\u043e\u0436\u043d\u043e \u0437\u0430\u0440\u0430\u0431\u043e\u0442\u0430\u0442\u044c, \u043b\u0438\u0431\u043e \u0436\u0435 \u043a\u0443\u043f\u0438\u0442\u044c \u0432 \u041b\u041a", "&ohttp://cp.vimeworld.ru").setOnClick(new OnClick(OnClick.Action.URL, (Object)"http://cp.vimeworld.ru/exchange")), (Player[])new Player[]{player});
    }

    public void showInvErrorMessage(Player player, long duration, String ... message) {
        Texteria2D.add((Element)this.genInvErrorMessage(duration, message), (Player[])new Player[]{player});
    }

    private Text genInvErrorMessage(long duration, String ... message) {
        return (Text)((Text)((Text)((Text)((Text)new Text(T_TOPMSG, message).setPosition(Position.TOP)).setOffset(0, 30)).setDuration(duration)).setVisibility((Visibility)new Visibility.Always())).setScale(2.0f);
    }

    public void showBasicMessage(Player player, long duration, int color, String ... message) {
        Texteria2D.add((Element)((Text)((Text)((Text)((Text)new Text(T_TOPMSG, message).setPosition(Position.TOP)).setOffset(0, 30)).setDuration(duration)).setColor(color)).setScale(2.0f), (Player[])new Player[]{player});
    }

    public void showVictory(Player ... players) {
        Texteria2D.add((Element)((Rectangle)((Rectangle)((Rectangle)((Rectangle)new Image(T_VICTORY, -1, "file:texteria/victory.png").setPosition(Position.TOP)).setOffset(0, 10)).setFade(1500)).setScale(0.5f)).setDuration(7000L), (Player[])players);
    }

    public void showDefeat(Player ... players) {
        Texteria2D.add((Element)((Rectangle)((Rectangle)((Rectangle)((Rectangle)new Image(T_DEFEAT, -1, "file:texteria/defeat.png").setPosition(Position.TOP)).setOffset(0, 10)).setFade(1500)).setScale(0.5f)).setDuration(7000L), (Player[])players);
    }

    public void showTie(Player ... players) {
        Texteria2D.add((Element)((Rectangle)((Rectangle)((Rectangle)((Rectangle)new Image(T_TIE, -1, "file:texteria/tie.png").setPosition(Position.TOP)).setOffset(0, 10)).setFade(1500)).setScale(0.5f)).setDuration(7000L), (Player[])players);
    }

    public void showCountdown(int seconds, String message, Supplier<Player[]> players) {
        for (int i = 0; i <= seconds; ++i) {
            int num = i;
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)VNPlugin.instance(), () -> {
                int color = -1;
                switch (seconds - num) {
                    case 0: {
                        color = -10167017;
                        break;
                    }
                    case 1: {
                        color = -35840;
                        break;
                    }
                    case 2: {
                        color = -20992;
                        break;
                    }
                    case 3: {
                        color = -11008;
                    }
                }
                Text text = (Text)((Text)((Text)((Text)((Text)((Text)new Text(null, new String[]{String.valueOf(seconds - num)}).setScale(6.0f)).setAnimation(new Animation2D().setFinish(new Animation2D.Params().setScale(25.0f)).setStart(new Animation2D.Params().setScale(-6.0f)))).setColor(color)).setFadeStart(400)).setFadeFinish(500)).setDuration(1000L);
                if (seconds == num) {
                    ((Text)text.setDuration(1500L)).setText(new String[]{message});
                }
                Texteria2D.add((Element)text, (Player[])((Player[])players.get()));
            }, (long)(i * 20));
        }
    }
}

