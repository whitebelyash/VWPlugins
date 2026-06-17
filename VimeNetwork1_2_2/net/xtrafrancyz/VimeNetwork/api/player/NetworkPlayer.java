/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.api.player;

import java.util.Map;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.Commons.player.Permission;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.player.ArrowTrail;
import net.xtrafrancyz.VimeNetwork.api.player.Guild;
import net.xtrafrancyz.VimeNetwork.api.player.Multipliers;
import net.xtrafrancyz.VimeNetwork.api.player.Party;
import net.xtrafrancyz.VimeNetwork.api.player.PlayerTag;
import net.xtrafrancyz.VimeNetwork.api.player.Stats;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievements;
import net.xtrafrancyz.VimeNetwork.api.player.goals.Goals;
import net.xtrafrancyz.VimeNetwork.api.player.treasure.Treasures;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface NetworkPlayer {
    public String getMeta(String var1);

    public void setMeta(String var1, String var2);

    public String removeMeta(String var1);

    public boolean hasMeta(String var1);

    public Map<String, String> getMetaMap();

    public PlayerTag getTag();

    public int addCoins(int var1);

    public void addCoinsExact(int var1);

    public int getCoins();

    public void takeCoins(int var1);

    public void toLobby();

    public void toServer(String var1);

    public Rank getRank();

    default public boolean has(Permission permission) {
        return this.getRank().has(permission);
    }

    default public boolean hasAndNotify(Permission permission) {
        if (this.getRank().has(permission)) {
            return true;
        }
        U.msg((CommandSender)this.getBukkitPlayer(), T.error("VimeWorld", "\u0423 \u0432\u0430\u0441 \u043d\u0435\u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u043f\u0440\u0430\u0432 \u0434\u043b\u044f \u0441\u043e\u0432\u0435\u0440\u0448\u0435\u043d\u0438\u044f \u044d\u0442\u043e\u0433\u043e \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u044f"));
        return false;
    }

    default public boolean has(Rank rank) {
        return this.getRank().has(rank);
    }

    default public boolean hasAndNotify(Rank rank) {
        if (this.getRank().has(rank)) {
            return true;
        }
        U.msg((CommandSender)this.getBukkitPlayer(), T.error("VimeWorld", "\u0414\u043b\u044f \u044d\u0442\u043e\u0433\u043e \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u044f \u043d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c \u0441\u0442\u0430\u0442\u0443\u0441 " + rank.getColor() + rank.getName()));
        return false;
    }

    public String getName();

    public int getId();

    public String getPrefixedName();

    public String getRankPrefix();

    public String getColoredName();

    public Player getBukkitPlayer();

    public boolean isOnline();

    public Goals getGoals();

    public Treasures getTreasures();

    public Achievements getAchievements();

    public Stats getStats();

    public boolean hasParty();

    public Party getParty();

    public boolean isPartyLeader();

    public boolean hasGuild();

    public Guild getGuild();

    public ArrowTrail getArrowTrail();

    public void setArrowTrail(ArrowTrail var1);

    public void unlockArrowTrail(ArrowTrail var1);

    public Multipliers getMultipliers();

    public long getLoginTime();

    public int getLevel();

    public int getTotalExp();

    public int getPartialExp();

    public void giveExp(int var1);

    public void giveExpExact(int var1);
}

