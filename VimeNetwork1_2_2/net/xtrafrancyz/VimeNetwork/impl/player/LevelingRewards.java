/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package net.xtrafrancyz.VimeNetwork.impl.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.player.Multiplier;
import net.xtrafrancyz.VimeNetwork.api.player.treasure.TreasureType;
import net.xtrafrancyz.VimeNetwork.api.util.Reflect;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import org.bukkit.command.CommandSender;

public class LevelingRewards {
    public static List<LevelingReward> REWARDS = new ArrayList<LevelingReward>();

    static {
        REWARDS.add(null);
        REWARDS.add(new CoinsReward(4000));
        REWARDS.add(new TreasureReward(TreasureType.BASIC, 3));
        REWARDS.add(new CoinsReward(5000));
        REWARDS.add(new MultiplierReward(2, 360, "6 \u0447\u0430\u0441\u043e\u0432"));
        REWARDS.add(new CoinsReward(6000));
        REWARDS.add(new TreasureReward(TreasureType.ANCIENT, 1));
        REWARDS.add(new MultiplierReward(2, 360, "6 \u0447\u0430\u0441\u043e\u0432"));
        REWARDS.add(new CoinsReward(8000));
        REWARDS.add(new TreasureReward(TreasureType.ANCIENT, 2));
        REWARDS.add(new CoinsReward(10000));
        REWARDS.add(new MultiplierReward(2, 720, "12 \u0447\u0430\u0441\u043e\u0432"));
        REWARDS.add(new CoinsReward(12000));
        REWARDS.add(new TreasureReward(TreasureType.ANCIENT, 3));
        REWARDS.add(new MultiplierReward(2, 1440, "1 \u0434\u0435\u043d\u044c"));
        REWARDS.add(new CoinsReward(14000));
        REWARDS.add(new CoinsReward(16000));
        REWARDS.add(new CoinsReward(18000));
        REWARDS.add(new TreasureReward(TreasureType.MYTHICAL, 1));
        REWARDS.add(new CoinsReward(20000));
        REWARDS.add(new MultiplierReward(3, 360, "6 \u0447\u0430\u0441\u043e\u0432"));
        REWARDS.add(new CoinsReward(22000));
        REWARDS.add(new MultiplierReward(3, 360, "6 \u0447\u0430\u0441\u043e\u0432"));
        REWARDS.add(new CoinsReward(24000));
        REWARDS.add(new TreasureReward(TreasureType.MYTHICAL, 2));
        REWARDS.add(new MultiplierReward(3, 720, "12 \u0447\u0430\u0441\u043e\u0432"));
        REWARDS.add(new CoinsReward(26000));
        REWARDS.add(new TreasureReward(TreasureType.MYTHICAL, 2));
        REWARDS.add(new CoinsReward(28000));
        REWARDS.add(new MultiplierReward(3, 720, "12 \u0447\u0430\u0441\u043e\u0432"));
        REWARDS.add(new CoinsReward(30000));
        REWARDS.add(new TreasureReward(TreasureType.MYTHICAL, 2));
        REWARDS.add(new MultiplierReward(3, 720, "12 \u0447\u0430\u0441\u043e\u0432", 2));
        REWARDS.add(new CoinsReward(32000));
        REWARDS.add(new CoinsReward(34000));
        REWARDS.add(new TreasureReward(TreasureType.MYTHICAL, 2));
        REWARDS.add(new MultiplierReward(3, 720, "12 \u0447\u0430\u0441\u043e\u0432", 2));
        REWARDS.add(new CoinsReward(36000));
        REWARDS.add(new CoinsReward(38000));
        REWARDS.add(new CoinsReward(40000));
        REWARDS.add(new TreasureReward(TreasureType.MYTHICAL, 2));
        REWARDS.add(new MultiplierReward(3, 720, "12 \u0447\u0430\u0441\u043e\u0432", 2));
        REWARDS.add(new TreasureReward(TreasureType.MYTHICAL, 3));
        REWARDS.add(new TreasureReward(TreasureType.MYTHICAL, 3));
        REWARDS.add(new CoinsReward(42000));
        REWARDS.add(new MultiplierReward(3, 720, "12 \u0447\u0430\u0441\u043e\u0432", 3));
        REWARDS.add(new MultiplierReward(3, 1440, "1 \u0434\u0435\u043d\u044c", 3));
        REWARDS.add(new CoinsReward(44000));
        REWARDS.add(new CoinsReward(46000));
        REWARDS.add(new CoinsReward(48000));
        REWARDS.add(new CoinsReward(50000));
        REWARDS.add(new TreasureReward(TreasureType.MYTHICAL, 3));
        REWARDS.add(new TreasureReward(TreasureType.MYTHICAL, 4));
        REWARDS.add(new TreasureReward(TreasureType.MYTHICAL, 4));
        REWARDS.add(new TreasureReward(TreasureType.MYTHICAL, 4));
        REWARDS.add(new CoinsReward(52000));
        REWARDS.add(new MultiplierReward(3, 1440, "1 \u0434\u0435\u043d\u044c", 4));
        REWARDS.add(new CoinsReward(54000));
        REWARDS.add(new CoinsReward(56000));
        REWARDS.add(new CoinsReward(58000));
    }

    private static class MiniDotReward
    extends LevelingReward {
        private int itemId;

        public MiniDotReward(String name, int itemId) {
            super(name);
            this.itemId = itemId;
        }

        @Override
        public void accept(VPlayer player) {
            Class<?> MiniDot = Reflect.findClass("net.xtrafrancyz.bukkit.minidot.MiniDot");
            Object plugin = Reflect.invoke(MiniDot, "instance", new Object[0]);
            Object database = Reflect.get(plugin, "database");
            Reflect.invoke(database, "unlockItem", player.player, this.itemId);
        }
    }

    private static class MultiplierReward
    extends LevelingReward {
        private Multiplier multiplier;
        private int amount;
        private String timeStr;

        public MultiplierReward(int multiplier, int minutes, String timeStr) {
            this(multiplier, minutes, timeStr, 1);
        }

        public MultiplierReward(int multiplier, int minutes, String timeStr, int amount) {
            if (amount > 1) {
                timeStr = timeStr + "&7 (x" + amount + ")";
            }
            this.timeStr = timeStr;
            this.text.add("&7+ \u041c\u043d\u043e\u0436\u0438\u0442\u0435\u043b\u044c \u043a\u043e\u0438\u043d\u043e\u0432 &fx" + multiplier + " &7\u043d\u0430 &f" + timeStr);
            this.text.add("");
            this.text.add("&8\u0414\u043b\u044f \u0430\u043a\u0442\u0438\u0432\u0430\u0446\u0438\u0438 \u043c\u043d\u043e\u0436\u0438\u0442\u0435\u043b\u044f \u043a\u043e\u0438\u043d\u043e\u0432");
            this.text.add("&8\u0437\u0430\u0439\u0434\u0438\u0442\u0435 \u0432 \u043c\u0435\u043d\u044e &f/me&8 \u0438 \u043d\u0430\u0436\u043c\u0438\u0442\u0435");
            this.text.add("&8\u043d\u0430 \u0437\u043e\u043b\u043e\u0442\u043e\u0439 \u0441\u043b\u0438\u0442\u043e\u043a.");
            this.multiplier = new Multiplier(multiplier, minutes);
            this.amount = amount;
        }

        @Override
        public void accept(VPlayer player) {
            player.getMultipliers().add(this.multiplier, this.amount);
            U.msg((CommandSender)player.player, "&a\u0412\u044b \u043f\u043e\u043b\u0443\u0447\u0438\u043b\u0438 \u043c\u043d\u043e\u0436\u0438\u0442\u0435\u043b\u044c \u043a\u043e\u0438\u043d\u043e\u0432 &fx" + this.multiplier.getMultiplier() + "&a \u043d\u0430 &f" + this.timeStr);
        }
    }

    private static class TreasureReward
    extends LevelingReward {
        private final TreasureType type;
        private final int amount;

        public TreasureReward(TreasureType type, int amount) {
            super("&7+ " + type.name + (amount > 1 ? " " + amount + " \u0448\u0442." : ""));
            this.type = type;
            this.amount = amount;
        }

        @Override
        public void accept(VPlayer player) {
            player.getTreasures().add(this.type, this.amount);
            String message = "&a\u0412\u044b \u043f\u043e\u043b\u0443\u0447\u0438\u043b\u0438 " + this.type.name;
            if (this.amount > 1) {
                message = message + " " + this.amount + " \u0448\u0442.";
            }
            U.msg((CommandSender)player.player, message);
        }
    }

    private static class CoinsReward
    extends LevelingReward {
        private final int coins;

        public CoinsReward(int coins) {
            super("&7+ &e" + U.pluralsCoins(coins));
            this.coins = coins;
        }

        @Override
        public void accept(VPlayer player) {
            player.addCoinsExact(this.coins);
        }
    }

    public static abstract class LevelingReward {
        protected final List<String> text;

        protected LevelingReward() {
            this.text = new ArrayList<String>();
        }

        protected LevelingReward(String name) {
            this.text = Collections.singletonList(name);
        }

        public List<String> getText() {
            return this.text;
        }

        public abstract void accept(VPlayer var1);
    }
}

