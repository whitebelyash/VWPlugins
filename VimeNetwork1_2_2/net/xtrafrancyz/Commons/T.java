/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Commons;

import net.xtrafrancyz.Commons.F;

public class T {
    private T() {
    }

    public static String system(String title, String text) {
        return title + " &e> &f" + text;
    }

    public static String warning(String title, String text) {
        return T.system(title, "&6" + text);
    }

    public static String error(String title, String text) {
        return T.system(title, "&c" + text);
    }

    public static String success(String title, String text) {
        return T.system(title, "&a" + text);
    }

    public static String kickBanMessage(String username, String reason, int timeSeconds, String banner) {
        String bantime = timeSeconds == 0 ? "\u043d\u0430\u0432\u0441\u0435\u0433\u0434\u0430" : F.formatSecondsShort(timeSeconds);
        String message = "&7* * * * * * * * * * * * * * * * * * *\n&c\u0412\u044b \u0431\u044b\u043b\u0438 \u0437\u0430\u0431\u0430\u043d\u0435\u043d\u044b\n\n&c\u041f\u0440\u0438\u0447\u0438\u043d\u0430: &e" + reason + "\n&c\u0412\u0440\u0435\u043c\u044f \u0431\u0430\u043d\u0430: &e" + bantime + "\n&c\u0412\u0430\u0441 \u0437\u0430\u0431\u0430\u043d\u0438\u043b: &e" + banner + "\n&7* * * * * * * * * * * * * * * * * * *";
        if (message.length() > 256) {
            return message.substring(0, 256);
        }
        return message;
    }

    public static String kickMessage(String username, String reason, String kicker) {
        String message = "&7* * * * * * * * * * * * * * * * * * *\n&c\u0412\u044b \u0431\u044b\u043b\u0438 \u043a\u0438\u043a\u043d\u0443\u0442\u044b \u0441 \u0441\u0435\u0440\u0432\u0435\u0440\u0430\n\n&c\u041f\u0440\u0438\u0447\u0438\u043d\u0430: &e" + reason + "\n&c\u0412\u0430\u0441 \u043a\u0438\u043a\u043d\u0443\u043b: &e" + kicker + "\n&7* * * * * * * * * * * * * * * * * * *";
        if (message.length() > 256) {
            return message.substring(0, 256);
        }
        return message;
    }
}

