/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  gnu.trove.set.hash.TIntHashSet
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.impl.player;

import gnu.trove.set.hash.TIntHashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.xtrafrancyz.Commons.Leveling;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.VTexteria;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.ArrowTrail;
import net.xtrafrancyz.VimeNetwork.api.player.Collectable;
import net.xtrafrancyz.VimeNetwork.api.player.Guild;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.PlayerTag;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.util.Fireworks;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.player.VAchievements;
import net.xtrafrancyz.VimeNetwork.impl.player.VGoals;
import net.xtrafrancyz.VimeNetwork.impl.player.VMultipliers;
import net.xtrafrancyz.VimeNetwork.impl.player.VParty;
import net.xtrafrancyz.VimeNetwork.impl.player.VStats;
import net.xtrafrancyz.VimeNetwork.impl.player.VTreasures;
import net.xtrafrancyz.VimeNetwork.impl.player.guild.VGuild;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public abstract class VPlayer
implements NetworkPlayer {
    public static final ConcurrentHashMap<String, VPlayer> PLAYERS = new ConcurrentHashMap();
    public static final ConcurrentHashMap<Integer, VPlayer> IDS = new ConcurrentHashMap();
    private static final String ARROW_SELECTED = "arr.sel";
    private static final String ARROW_AVAILABLE = "arr.open";
    public static final int SETTING_PARTY_INVITES = 0;
    public static final int SETTING_PRIVATE_MESSAGES = 1;
    public static final int SETTING_GOALS_NOTIFICATION = 2;
    public static final int SETTING_STREAMS_NOTIFICATION = 3;
    public static final int SETTING_FRIEND_INVITES = 4;
    public static final int SETTING_FRIEND_NOTIFICATIONS = 5;
    public static final int SETTING_FRIEND_EXACT_LOCATION = 6;
    public static final int SETTING_GUILD_INVITES = 7;
    protected final VNPlugin plugin = VNPlugin.instance();
    public int id = -1;
    public final String username;
    public final Player player;
    public boolean loaded = false;
    public long loginTime = System.currentTimeMillis();
    public Rank rank = Rank.PLAYER;
    public PlayerTag tag;
    public VGoals goals;
    public VTreasures treasures;
    public VParty party;
    public VAchievements achievements;
    public VStats stats;
    public VGuild guild;
    private ArrowTrail arrowTrail = null;
    public TIntHashSet availableArrowTrails;
    public Collectable settings;
    public VMultipliers multipliers;
    public Player lastDamager = null;
    public Entity lastDamagerEntity = null;
    public int lastDamagerPurgeTask = -1;
    public long lastDamageFromPlayer = 0L;
    public long lastDeath = 0L;
    public int coins = 0;
    public int coinsTexteriaMultView = 0;
    public float coinsTexteriaMult = 0.0f;
    public volatile int coinsAddBuffer = 0;
    public int level = 0;
    public int exp = 0;
    public int expBuffer = 0;
    public static Function<Player, VPlayer> CONSTRUCTOR = null;

    VPlayer(Player player) {
        this.player = player;
        this.username = player.getName();
        this.tag = new PlayerTag(this);
        this.goals = new VGoals(this);
        this.treasures = new VTreasures(this);
        this.achievements = new VAchievements(this);
        this.stats = new VStats(this);
        this.availableArrowTrails = new TIntHashSet();
        this.multipliers = new VMultipliers(this);
        this.settings = new Collectable((NetworkPlayer)this, "settings", new boolean[]{true, true, true, true, true, true, false, true});
    }

    public void onMetaLoaded() {
        this.goals.load();
        this.settings.load();
        this.multipliers.load();
        this.treasures.load();
        this.loadArrows();
    }

    public void onMetaUpdate(String key, String value) {
        if (key.startsWith("goal.")) {
            this.goals.load();
        } else if (key.equals("settings")) {
            this.settings.load();
        } else if (key.equals("trsr.count")) {
            this.treasures.load();
        } else if (key.equals("mult") || key.equals("mult.inv")) {
            this.multipliers.load();
        } else if (key.equals(ARROW_SELECTED) || key.equals(ARROW_AVAILABLE)) {
            this.loadArrows();
        }
    }

    private void loadArrows() {
        String val = this.getMeta(ARROW_SELECTED);
        if (val != null) {
            try {
                this.arrowTrail = ArrowTrail.byId(Integer.parseInt(val));
            }
            catch (Exception ex) {
                this.plugin.getLogger().warning("[" + this.username + "] ArrowTrail " + val + " not exists [1]");
                this.removeMeta(ARROW_SELECTED);
            }
        }
        if ((val = this.getMeta(ARROW_AVAILABLE)) != null) {
            boolean changed = false;
            for (String str : val.split(",")) {
                try {
                    this.availableArrowTrails.add(Integer.parseInt(str));
                }
                catch (Exception ex) {
                    this.plugin.getLogger().warning("[" + this.username + "] ArrowTrail " + val + " not exists [2]");
                    changed = true;
                }
            }
            if (changed) {
                this.saveAvailableArrowTrails();
            }
        }
    }

    private void saveAvailableArrowTrails() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int id : this.availableArrowTrails.toArray()) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            sb.append(id);
        }
        this.setMeta(ARROW_AVAILABLE, sb.toString());
    }

    @Override
    public int addCoins(int amount) {
        amount = Math.round((float)Math.floor((float)amount * (this.multipliers.getCurrentMultiplier() + 1.0E-6f)));
        this.addCoinsExact(amount);
        return amount;
    }

    @Override
    public void addCoinsExact(int amount) {
        this.plugin.coins.addCoins(this, amount, false);
    }

    @Override
    public int getCoins() {
        return this.coins;
    }

    @Override
    public void takeCoins(int amount) {
        this.plugin.coins.takeCoins(this, amount, false);
    }

    @Override
    public Rank getRank() {
        return this.rank;
    }

    @Override
    public Player getBukkitPlayer() {
        return this.player;
    }

    @Override
    public void toLobby() {
        VimeNetwork.toLobby(this.player);
    }

    @Override
    public void toServer(String id) {
        VimeNetwork.toServer(id, this.player);
    }

    @Override
    public PlayerTag getTag() {
        return this.tag;
    }

    @Override
    public String getName() {
        return this.username;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getPrefixedName() {
        String prefix = this.getRankPrefix();
        if (!prefix.isEmpty()) {
            return this.rank.getColor() + "[" + prefix + ChatColor.RESET + this.rank.getColor() + "] " + this.username + ChatColor.RESET;
        }
        return this.rank.getColor() + this.username + ChatColor.RESET;
    }

    @Override
    public String getRankPrefix() {
        String prefix = this.getMeta("prefix");
        return prefix == null ? this.rank.getPrefix() : prefix;
    }

    @Override
    public String getColoredName() {
        return this.rank.getColor() + this.username + ChatColor.RESET;
    }

    @Override
    public boolean isOnline() {
        return PLAYERS.containsKey(this.username);
    }

    @Override
    public VGoals getGoals() {
        return this.goals;
    }

    @Override
    public VTreasures getTreasures() {
        return this.treasures;
    }

    @Override
    public VAchievements getAchievements() {
        return this.achievements;
    }

    @Override
    public VStats getStats() {
        return this.stats;
    }

    @Override
    public boolean hasParty() {
        return this.party != null;
    }

    @Override
    public VParty getParty() {
        return this.party;
    }

    @Override
    public boolean isPartyLeader() {
        return this.hasParty() && this.party.leader.equals(this.username);
    }

    @Override
    public boolean hasGuild() {
        return this.guild != null && this.guild.name != null;
    }

    @Override
    public Guild getGuild() {
        return this.hasGuild() ? this.guild : null;
    }

    @Override
    public ArrowTrail getArrowTrail() {
        return this.arrowTrail;
    }

    @Override
    public void setArrowTrail(ArrowTrail arrowTrail) {
        if (arrowTrail != this.arrowTrail) {
            this.arrowTrail = arrowTrail;
            if (arrowTrail == null) {
                this.removeMeta(ARROW_SELECTED);
            } else {
                this.setMeta(ARROW_SELECTED, arrowTrail.getId() + "");
            }
        }
    }

    @Override
    public void unlockArrowTrail(ArrowTrail trail) {
        if (this.availableArrowTrails.add(trail.getId())) {
            this.saveAvailableArrowTrails();
        }
    }

    @Override
    public VMultipliers getMultipliers() {
        return this.multipliers;
    }

    @Override
    public long getLoginTime() {
        return this.loginTime;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public int getTotalExp() {
        return this.exp;
    }

    @Override
    public int getPartialExp() {
        return this.exp - Leveling.getTotalExp(this.level);
    }

    @Override
    public void giveExp(int exp) {
        if (exp <= 0) {
            return;
        }
        this.exp += exp;
        this.expBuffer += exp;
        this.updateExp(exp);
    }

    @Override
    public void giveExpExact(int exp) {
        if (exp <= 0) {
            return;
        }
        this.exp += exp;
        this.updateExp(exp);
    }

    public void dispose() {
        this.goals.goals.clear();
        this.goals = null;
        if (this.guild != null) {
            this.guild.removePlayer(this);
        }
        if (this.party != null) {
            this.party.players.clear();
            this.party = null;
        }
    }

    public boolean equals(Object obj) {
        return obj == this;
    }

    public int hashCode() {
        return this.username.hashCode();
    }

    public String toString() {
        return "VPlayer{name='" + this.username + "'}";
    }

    private void updateExp(int given) {
        VTexteria.showGiveExp(this, given);
        if (this.exp >= Leveling.getTotalExp(this.level + 1)) {
            this.level = Leveling.getLevel(this.exp);
            VTexteria.showUsername(this);
            U.msg((CommandSender)this.player, T.success("VimeWorld", "\u041f\u043e\u0437\u0434\u0440\u0430\u0432\u043b\u044f\u0435\u043c! \u0412\u044b \u043f\u043e\u043b\u0443\u0447\u0438\u043b\u0438 &f" + this.level + "&a \u0443\u0440\u043e\u0432\u0435\u043d\u044c! \u0417\u0430\u0439\u0434\u0438\u0442\u0435 \u0432 \u043c\u0435\u043d\u044e &f/me&a \u0447\u0442\u043e\u0431\u044b \u043f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u043d\u0430\u0433\u0440\u0430\u0434\u0443!"));
            boolean launchFirework = true;
            if (this.level >= 10 && this.achievements.complete(Achievement.GLOBAL_10_LVL)) {
                launchFirework = false;
            }
            if (this.level >= 50 && this.achievements.complete(Achievement.GLOBAL_50_LVL)) {
                launchFirework = false;
            }
            if (launchFirework) {
                Fireworks.playRandom(this.player.getLocation());
            }
        }
    }

    public static VPlayer get(String player) {
        VPlayer val = PLAYERS.get(player);
        if (val == null) {
            Player bukkitPlayer = Bukkit.getPlayerExact((String)player);
            if (bukkitPlayer == null) {
                throw new IllegalArgumentException("Player with name '" + player + "' not exists on server");
            }
            val = PLAYERS.computeIfAbsent(player, name -> CONSTRUCTOR.apply(bukkitPlayer));
        }
        return val;
    }

    public static VPlayer get(Player player) {
        VPlayer val = PLAYERS.get(player.getName());
        if (val == null) {
            val = PLAYERS.computeIfAbsent(player.getName(), name -> CONSTRUCTOR.apply(player));
        }
        return val;
    }
}

