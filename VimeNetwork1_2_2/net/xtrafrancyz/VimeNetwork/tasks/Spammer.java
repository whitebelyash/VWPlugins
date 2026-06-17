/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.bukkit.texteria.Texteria2D
 *  net.xtrafrancyz.bukkit.texteria.elements.Element
 *  net.xtrafrancyz.bukkit.texteria.elements.Text
 *  net.xtrafrancyz.bukkit.texteria.utils.Position
 *  net.xtrafrancyz.bukkit.texteria.utils.Visibility
 *  net.xtrafrancyz.bukkit.texteria.utils.Visibility$IngameNotChat
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.tasks;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.util.Rand;
import net.xtrafrancyz.bukkit.texteria.Texteria2D;
import net.xtrafrancyz.bukkit.texteria.elements.Element;
import net.xtrafrancyz.bukkit.texteria.elements.Text;
import net.xtrafrancyz.bukkit.texteria.utils.Position;
import net.xtrafrancyz.bukkit.texteria.utils.Visibility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Spammer
implements Runnable {
    private static final List<String> MESSAGES = new ArrayList<String>();
    private static final String TEXT_COLOR = "&f";
    private static final String BOLD_COLOR = "&a";

    public static void addMessage(String message) {
        if (!MESSAGES.contains(message)) {
            MESSAGES.add(message.replace("^t", TEXT_COLOR).replace("^b", BOLD_COLOR));
        }
    }

    @Override
    public void run() {
        Texteria2D.add((Element)((Text)((Text)((Text)new Text("vn.spam", new String[]{Rand.of(MESSAGES)}).setOffset(2, 2)).setPosition(Position.BOTTOM_LEFT)).setDuration(45000L)).setVisibility((Visibility)new Visibility.IngameNotChat()), (Player[])Bukkit.getOnlinePlayers());
    }

    static {
        String c1 = TEXT_COLOR;
        String c2 = BOLD_COLOR;
        MESSAGES.addAll(Arrays.asList(c1 + "\u041d\u0430\u0448 \u0444\u043e\u0440\u0443\u043c " + c2 + "http://f.vimeworld.ru", c1 + "TeamSpeak 3 " + c2 + "http://ts.vimeworld.ru", c1 + "\u041e\u0431\u0449\u0430\u0439\u0442\u0435\u0441\u044c \u0432 ts3 " + c2 + "http://ts.vimeworld.ru", c1 + "\u041f\u043e\u043a\u0443\u043f\u043a\u0430 \u043a\u043e\u0438\u043d\u043e\u0432 " + c2 + "http://cp.vimeworld.ru", c1 + "\u041b\u0438\u0447\u043d\u044b\u0439 \u043a\u0430\u0431\u0438\u043d\u0435\u0442 " + c2 + "http://cp.vimeworld.ru", c1 + "\u0413\u0440\u0443\u043f\u043f\u0430 \u0412\u041a " + c2 + "https://vk.com/vimeworld", c1 + "\u0416\u0435\u043b\u0430\u0435\u043c \u0412\u0430\u043c \u0443\u0434\u0430\u0447\u043d\u043e\u0439 \u0438\u0433\u0440\u044b!", c1 + "\u041f\u043e\u043a\u0443\u043f\u043a\u0430 &a[V]" + c1 + ", &b[P]" + c1 + ", &6[H]" + c1 + " - " + c2 + "http://cp.vimeworld.ru", c1 + "\u0416\u0430\u043b\u043e\u0431\u044b \u043d\u0430 \u0444\u043e\u0440\u0443\u043c " + c2 + "http://f.vimeworld.ru", c1 + "\u0412\u0435\u0440\u043d\u0443\u0442\u044c\u0441\u044f \u0432 \u043b\u043e\u0431\u0431\u0438 " + c2 + "/hub", c1 + "\u041b\u0438\u0447\u043d\u044b\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f " + c2 + "/m", c1 + "\u0424\u043b\u0443\u0434\u044f\u0442 \u0432 \u043b\u0441? " + c2 + "/ignore", c1 + "\u041f\u0440\u043e\u0444\u0438\u043b\u044c \u0438 \u043d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 " + c2 + "/me", c1 + "\u0411\u044b\u0441\u0442\u0440\u044b\u0439 \u043e\u0442\u0432\u0435\u0442 \u043d\u0430 \u043b\u0438\u0447\u043d\u043e\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435 " + c2 + "/r", c1 + "\u0421\u043f\u0438\u0441\u043e\u043a \u0434\u043e\u0441\u0442\u0443\u043f\u043d\u044b\u0445 \u043a\u043e\u043c\u0430\u043d\u0434: " + c2 + "/help", c1 + "\u0417\u0430\u043c\u0435\u0442\u0438\u043b \u043f\u043b\u043e\u0445\u043e\u0433\u043e \u0438\u0433\u0440\u043e\u043a\u0430: " + c2 + "/report", c1 + "\u0418\u0433\u0440\u0430\u0439\u0442\u0435 \u0432\u043c\u0435\u0441\u0442\u0435 \u0441 \u0434\u0440\u0443\u0437\u044c\u044f\u043c\u0438: " + c2 + "/party", c1 + "\u0418\u0433\u0440\u0430\u0439\u0442\u0435 \u0432\u043c\u0435\u0441\u0442\u0435 \u0441 \u0434\u0440\u0443\u0437\u044c\u044f\u043c\u0438: " + c2 + "/friend", c1 + "\u0415\u0441\u0442\u044c 10\u043a \u043f\u043e\u0434\u043f\u0438\u0441\u0447\u0438\u043a\u043e\u0432? \u041f\u043e\u043b\u0443\u0447\u0438 \u0441\u0442\u0430\u0442\u0443\u0441 &cYouTube" + c1 + "!", c1 + "\u041e\u0442\u043a\u0440\u043e\u0439 \u0447\u0430\u0442 \u0438 \u044f \u0438\u0441\u0447\u0435\u0437\u043d\u0443 :3", c1 + "\u041d\u0435 \u0437\u0430\u0431\u044b\u0432\u0430\u0439 \u043a\u0443\u0448\u0430\u0442\u044c!", c1 + "\u041d\u0435 \u0431\u0443\u0434\u044c \u043a\u0430\u043a \u0432\u0441\u0435, \u0431\u0443\u0434\u044c &dImmortal" + c1 + "!", c1 + "\u041d\u0430 VimeWorld \u0441\u0430\u043c\u044b\u0435 \u043b\u0443\u0447\u0448\u0438\u0435 \u0438\u0433\u0440\u043e\u043a\u0438 ^_^"));
        LocalDate date = LocalDate.now(VimeNetwork.TZ_MOSCOW);
        int month = date.getMonth().getValue();
        int day = date.getDayOfMonth();
        if (month == 12) {
            if (day > 15 && day < 31) {
                Spammer.addMessage("^t\u0421 \u043d\u0430\u0441\u0442\u0443\u043f\u0430\u044e\u0449\u0438\u043c \u041d\u043e\u0432\u044b\u043c \u0413\u043e\u0434\u043e\u043c!");
            } else if (day >= 31) {
                Spammer.addMessage("^t\u0421 \u041d\u043e\u0432\u044b\u043c \u0413\u043e\u0434\u043e\u043c!");
            }
        } else if (month == 1 && day < 3) {
            Spammer.addMessage("^t\u0421 \u041d\u043e\u0432\u044b\u043c ^b" + date.getYear() + "^t \u0413\u043e\u0434\u043e\u043c!");
        }
    }
}

