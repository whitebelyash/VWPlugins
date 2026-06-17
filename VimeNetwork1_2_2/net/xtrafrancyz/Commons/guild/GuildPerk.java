/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Commons.guild;

import java.util.HashMap;
import java.util.Map;

public enum GuildPerk {
    MEMBERS(1, "\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u0447\u043b\u0435\u043d\u043e\u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u0438", new Upgrade(100000, 1), new Upgrade(200000, 1), new Upgrade(300000, 2), new Upgrade(400000, 2), new Upgrade(500000, 3), new Upgrade(600000, 3), new Upgrade(700000, 4), new Upgrade(800000, 4), new Upgrade(900000, 5), new Upgrade(1000000, 5), new Upgrade(1100000, 6), new Upgrade(1200000, 7), new Upgrade(1300000, 8), new Upgrade(1400000, 9), new Upgrade(1500000, 10), new Upgrade(1600000, 11)),
    COINS(2, "\u0415\u0436\u0435\u0434\u043d\u0435\u0432\u043d\u044b\u0439 \u043b\u0438\u043c\u0438\u0442 \u043a\u043e\u0438\u043d\u043e\u0432", new Upgrade(50000, 1), new Upgrade(100000, 2), new Upgrade(150000, 3), new Upgrade(200000, 4), new Upgrade(250000, 5), new Upgrade(300000, 6), new Upgrade(350000, 7), new Upgrade(400000, 8), new Upgrade(450000, 9), new Upgrade(500000, 10)),
    PARTY(3, "\u0421\u043e\u0437\u0434\u0430\u043d\u0438\u0435 \u0433\u0440\u0443\u043f\u043f\u044b", new Upgrade(500000, 5)),
    MOTD(4, "\u041f\u0440\u0438\u0432\u0435\u0442\u0441\u0442\u0432\u0435\u043d\u043d\u043e\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435", new Upgrade(500000, 5)),
    COINS_MULT(5, "\u0414\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u044b\u0439 \u043c\u043d\u043e\u0436\u0438\u0442\u0435\u043b\u044c \u043a\u043e\u0438\u043d\u043e\u0432", new Upgrade(100000, 1), new Upgrade(300000, 2), new Upgrade(500000, 3), new Upgrade(700000, 4), new Upgrade(900000, 5), new Upgrade(1100000, 6), new Upgrade(1300000, 7), new Upgrade(1500000, 8), new Upgrade(1700000, 9), new Upgrade(1900000, 10)),
    TAG(6, "\u0422\u0435\u0433 \u0433\u0438\u043b\u044c\u0434\u0438\u0438", new Upgrade(3000000, 5)),
    COLOR(7, "\u0426\u0432\u0435\u0442 \u0433\u0438\u043b\u044c\u0434\u0438\u0438", new Upgrade(5000000, 6));

    private static final Map<Integer, GuildPerk> byId;
    public final int id;
    public final String name;
    public final Upgrade[] upgrades;

    private GuildPerk(int id, String name, Upgrade ... upgrades) {
        this.id = id;
        this.name = name;
        this.upgrades = upgrades;
    }

    public static GuildPerk byId(int id) {
        return byId.get(id);
    }

    public static int getMaxMembers(int level) {
        return 20 + level * 5;
    }

    public static int getMaxDailyCoins(int level) {
        return 50000 + 5000 * level;
    }

    public static float getCoinsMultiplier(int level) {
        return (float)level * 0.1f;
    }

    static {
        byId = new HashMap<Integer, GuildPerk>();
        for (GuildPerk perk : GuildPerk.values()) {
            byId.put(perk.id, perk);
        }
    }

    public static class Upgrade {
        public int price;
        public int neededLevel;

        public Upgrade(int price, int neededLevel) {
            this.price = price;
            this.neededLevel = neededLevel;
        }
    }
}

