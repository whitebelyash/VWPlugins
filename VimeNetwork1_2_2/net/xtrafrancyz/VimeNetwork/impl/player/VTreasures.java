/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package net.xtrafrancyz.VimeNetwork.impl.player;

import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.ServerType;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.player.treasure.TreasureType;
import net.xtrafrancyz.VimeNetwork.api.player.treasure.Treasures;
import net.xtrafrancyz.VimeNetwork.api.util.Rand;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import org.bukkit.command.CommandSender;

public class VTreasures
implements Treasures {
    public static final String META_KEY = "trsr.count";
    private final VPlayer player;
    private TreasuresEntry entry;

    public VTreasures(VPlayer player) {
        this.player = player;
        this.entry = new TreasuresEntry();
    }

    @Override
    public int get(TreasureType type) {
        switch (type) {
            case BASIC: {
                return this.entry.basic;
            }
            case ANCIENT: {
                return this.entry.ancient;
            }
            case MYTHICAL: {
                return this.entry.mythical;
            }
        }
        return 0;
    }

    @Override
    public void add(TreasureType type, int amount) {
        switch (type) {
            case BASIC: {
                this.entry.basic += amount;
                if (this.entry.basic >= 0) break;
                this.entry.basic = 0;
                break;
            }
            case ANCIENT: {
                this.entry.ancient += amount;
                if (this.entry.ancient >= 0) break;
                this.entry.ancient = 0;
                break;
            }
            case MYTHICAL: {
                this.entry.mythical += amount;
                if (this.entry.mythical >= 0) break;
                this.entry.mythical = 0;
            }
        }
        this.save();
    }

    @Override
    public void take(TreasureType type, int amount) {
        this.add(type, -amount);
    }

    @Override
    public boolean hasAny() {
        return this.entry.basic + this.entry.ancient + this.entry.mythical > 0;
    }

    @Override
    public void giveWithMessage(TreasureType type, float chance) {
        if (this.player.has(Rank.IMMORTAL)) {
            chance *= 3.0f;
        } else if (this.player.has(Rank.HOLY)) {
            chance *= 2.3f;
        } else if (this.player.has(Rank.PREMIUM)) {
            chance *= 1.8f;
        } else if (this.player.has(Rank.VIP)) {
            chance *= 1.35f;
        }
        if (Rand.nextFloat() <= chance) {
            this.giveWithMessage(type);
        }
    }

    @Override
    public void giveWithMessage(TreasureType type) {
        this.add(type, 1);
        U.msg((CommandSender)this.player.player, "&a\u0412\u044b \u043f\u043e\u043b\u0443\u0447\u0438\u043b\u0438 " + type.name);
        U.bcast(T.system("&r\u0421\u043e\u043a\u0440\u043e\u0432\u0438\u0449\u043d\u0438\u0446\u0430", "\u0418\u0433\u0440\u043e\u043a\u0443 &e" + this.player.player.getDisplayName() + " &f\u0432\u044b\u043f\u0430\u043b: " + type.name));
        if (VimeNetwork.lobby().getServerType() != ServerType.LOBBY) {
            this.player.getAchievements().complete(Achievement.GLOBAL_LOOT_CHEST);
        }
    }

    public void load() {
        String meta = this.player.getMeta(META_KEY);
        this.entry = meta == null ? new TreasuresEntry() : (TreasuresEntry)VimeNetwork.gson.fromJson(meta, TreasuresEntry.class);
    }

    private void save() {
        if (this.hasAny()) {
            this.player.setMeta(META_KEY, VimeNetwork.gson.toJson((Object)this.entry));
        } else {
            this.player.removeMeta(META_KEY);
        }
    }

    public static class TreasuresEntry {
        public int basic = 0;
        public int ancient = 0;
        public int mythical = 0;
    }
}

