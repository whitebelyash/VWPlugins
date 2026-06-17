/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package net.xtrafrancyz.VimeNetwork.impl.player.guild;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.xtrafrancyz.Commons.guild.GuildLeveling;
import net.xtrafrancyz.Commons.guild.GuildPerk;
import net.xtrafrancyz.Commons.guild.GuildStatus;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.Core.CoreByteMap;
import net.xtrafrancyz.Core.network.packet.Packet69Guild;
import net.xtrafrancyz.VimeNetwork.api.player.Guild;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import org.bukkit.ChatColor;

public class VGuild
implements Guild {
    public static final Map<Integer, VGuild> CACHE = new HashMap<Integer, VGuild>();
    private Set<NetworkPlayer> onlinePlayers = new HashSet<NetworkPlayer>();
    private long lastUse;
    private final int id;
    public String name;
    public String tag;
    public String color = ChatColor.WHITE.toString();
    public float coinsMultiplier;
    public List<Member> members;
    public int coins;
    public int exp;
    public int level;
    public int creationTime;
    public Map<GuildPerk, Integer> perks;

    public VGuild(int id) {
        this.id = id;
        this.lastUse = System.currentTimeMillis();
    }

    public void addPlayer(NetworkPlayer player) {
        this.onlinePlayers.add(player);
    }

    public void removePlayer(NetworkPlayer player) {
        this.onlinePlayers.remove(player);
        if (this.onlinePlayers.isEmpty()) {
            this.lastUse = System.currentTimeMillis();
        }
    }

    public void fillData(Packet69Guild packet) {
        this.coins = packet.data.getInt("coins");
        this.exp = packet.data.getInt("exp");
        this.level = GuildLeveling.getLevel(this.exp);
        this.creationTime = packet.data.getInt("created");
        this.perks = new EnumMap<GuildPerk, Integer>(GuildPerk.class);
        byte[] perkLevels = packet.data.getByteArray("perks");
        for (GuildPerk perk : GuildPerk.values()) {
            this.perks.put(perk, Integer.valueOf(perkLevels[perk.ordinal()]));
        }
        CoreByteMap[] members = packet.data.getMapArray("members");
        this.members = new ArrayList<Member>(members.length);
        for (CoreByteMap sm : members) {
            this.members.add(new Member(sm));
        }
        this.members.sort(Comparator.comparingInt(m -> m.status.ordinal()).thenComparing(m -> m.name));
    }

    public List<Member> getNormalPlayers() {
        ArrayList<Member> list = new ArrayList<Member>();
        for (Member member : this.members) {
            if (member.status != GuildStatus.MEMBER) continue;
            list.add(member);
        }
        return list;
    }

    public List<Member> getOfficers() {
        ArrayList<Member> list = new ArrayList<Member>();
        for (Member member : this.members) {
            if (member.status != GuildStatus.OFFICER) continue;
            list.add(member);
        }
        return list;
    }

    public Member getLeader() {
        return this.members.get(0);
    }

    public Member getMember(String name) {
        for (Member member : this.members) {
            if (!member.name.equalsIgnoreCase(name)) continue;
            return member;
        }
        return null;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getTag() {
        return this.tag;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getColor() {
        return this.color;
    }

    @Override
    public Set<NetworkPlayer> getOnlinePlayers() {
        return this.onlinePlayers;
    }

    public static class Cleaner
    implements Runnable {
        @Override
        public void run() {
            CACHE.values().removeIf(guild -> ((VGuild)guild).onlinePlayers.isEmpty() && System.currentTimeMillis() - ((VGuild)guild).lastUse > 1800000L);
        }
    }

    public static class Member {
        public String name;
        public Rank rank;
        public GuildStatus status;
        public boolean online;
        public int coins;
        public int exp;
        public int joinDate;

        public Member(CoreByteMap member) {
            this.name = member.getString("n");
            this.rank = Rank.values()[member.getByte("r")];
            this.status = GuildStatus.values()[member.getByte("s")];
            this.coins = member.getInt("c");
            this.exp = member.getInt("e");
            this.joinDate = member.getInt("j");
            this.online = member.getBoolean("o");
        }
    }
}

