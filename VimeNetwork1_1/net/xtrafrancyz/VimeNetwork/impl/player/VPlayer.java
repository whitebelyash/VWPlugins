package net.xtrafrancyz.VimeNetwork.impl.player;

import gnu.trove.set.hash.TIntHashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.xtrafrancyz.Commons.Leveling;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.VTexteria;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.ArrowTrail;
import net.xtrafrancyz.VimeNetwork.api.player.Collectable;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.util.Fireworks;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public abstract class VPlayer implements NetworkPlayer {
   public static final ConcurrentHashMap PLAYERS = new ConcurrentHashMap();
   public static final ConcurrentHashMap IDS = new ConcurrentHashMap();
   private static final String ARROW_SELECTED = "arr.sel";
   private static final String ARROW_AVAILABLE = "arr.open";
   public static final int SETTING_PARTY_INVITES = 0;
   public static final int SETTING_PRIVATE_MESSAGES = 1;
   public static final int SETTING_GOALS_NOTIFICATION = 2;
   public static final int SETTING_STREAMS_NOTIFICATION = 3;
   public static final int SETTING_FRIEND_INVITES = 4;
   public static final int SETTING_FRIEND_NOTIFICATIONS = 5;
   public static final int SETTING_FRIEND_EXACT_LOCATION = 6;
   protected final VNPlugin plugin = VNPlugin.instance();
   public int id = -1;
   public final String username;
   public final Player player;
   public boolean loaded = false;
   public long loginTime = System.currentTimeMillis();
   public Rank rank;
   public String tag;
   public VGoals goals;
   public VTreasures treasures;
   public VParty party;
   public VAchievements achievements;
   public VStats stats;
   private ArrowTrail arrowTrail;
   public TIntHashSet availableArrowTrails;
   public Collectable settings;
   public VMultipliers multipliers;
   public Player lastDamager;
   public Entity lastDamagerEntity;
   public int lastDamagerPurgeTask;
   public long lastDamageFromPlayer;
   public long lastDeath;
   public int coins;
   public int coinsTexteria;
   public volatile int coinsAddBuffer;
   public int level;
   public int exp;
   public int expBuffer;
   public static Function CONSTRUCTOR = null;

   VPlayer(Player player) {
      this.rank = Rank.PLAYER;
      this.arrowTrail = null;
      this.lastDamager = null;
      this.lastDamagerEntity = null;
      this.lastDamagerPurgeTask = -1;
      this.lastDamageFromPlayer = 0L;
      this.lastDeath = 0L;
      this.coins = 0;
      this.coinsTexteria = 0;
      this.coinsAddBuffer = 0;
      this.level = 0;
      this.exp = 0;
      this.expBuffer = 0;
      this.player = player;
      this.username = player.getName();
      this.tag = this.username;
      this.goals = new VGoals(this);
      this.treasures = new VTreasures(this);
      this.achievements = new VAchievements(this);
      this.stats = new VStats(this);
      this.availableArrowTrails = new TIntHashSet();
      this.multipliers = new VMultipliers(this);
      this.settings = new Collectable(this, "settings", new boolean[]{true, true, true, true, true, true, false});
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
      } else if (!key.equals("mult") && !key.equals("mult.inv")) {
         if (key.equals("arr.sel") || key.equals("arr.open")) {
            this.loadArrows();
         }
      } else {
         this.multipliers.load();
      }

   }

   private void loadArrows() {
      String val = this.getMeta("arr.sel");
      if (val != null) {
         try {
            this.arrowTrail = ArrowTrail.byId(Integer.parseInt(val));
         } catch (Exception var9) {
            this.plugin.getLogger().warning("[" + this.username + "] ArrowTrail " + val + " not exists [1]");
            this.removeMeta("arr.sel");
         }
      }

      val = this.getMeta("arr.open");
      if (val != null) {
         boolean changed = false;

         for(String str : val.split(",")) {
            try {
               this.availableArrowTrails.add(Integer.parseInt(str));
            } catch (Exception var8) {
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

      for(int id : this.availableArrowTrails.toArray()) {
         if (first) {
            first = false;
         } else {
            sb.append(',');
         }

         sb.append(id);
      }

      this.setMeta("arr.open", sb.toString());
   }

   public int addCoins(int amount) {
      amount *= this.multipliers.getCurrentMultiplier();
      this.addCoinsExact(amount);
      return amount;
   }

   public void addCoinsExact(int amount) {
      this.plugin.coins.addCoins(this, amount);
   }

   public int getCoins() {
      return this.coins;
   }

   public void takeCoins(int amount) {
      this.plugin.coins.takeCoins(this, amount);
   }

   public Rank getRank() {
      return this.rank;
   }

   public Player getBukkitPlayer() {
      return this.player;
   }

   public void toLobby() {
      VimeNetwork.toLobby(this.player);
   }

   public void toServer(String id) {
      VimeNetwork.toServer(id, this.player);
   }

   public void setTag(String name) {
      if (name.length() > 36) {
         throw new IllegalArgumentException("Visible name must be less then 36 chars");
      } else if (!this.tag.equals(name)) {
         this.tag = U.colored(name);
         this.plugin.tags.updateName(this.player);
      }
   }

   public String getTag() {
      return this.tag;
   }

   public boolean hasTag() {
      return !this.tag.equals(this.username);
   }

   public void removeTag() {
      if (VimeNetwork.features().CHANGE_TAGS.isEnabled()) {
         this.setTag(this.getColoredName());
      } else {
         this.setTag(this.username);
      }

   }

   public String getName() {
      return this.username;
   }

   public int getId() {
      return this.id;
   }

   public String getPrefixedName() {
      String prefix = this.getPrefix();
      return !prefix.isEmpty() ? this.rank.getColor() + "[" + prefix + ChatColor.RESET + this.rank.getColor() + "] " + this.username + ChatColor.RESET : this.rank.getColor() + this.username + ChatColor.RESET;
   }

   public String getPrefix() {
      String prefix = this.getMeta("prefix");
      return prefix == null ? this.rank.getPrefix() : prefix;
   }

   public String getColoredName() {
      return this.rank.getColor() + this.username + ChatColor.RESET;
   }

   public boolean isOnline() {
      return PLAYERS.containsKey(this.username);
   }

   public VGoals getGoals() {
      return this.goals;
   }

   public VTreasures getTreasures() {
      return this.treasures;
   }

   public VAchievements getAchievements() {
      return this.achievements;
   }

   public VStats getStats() {
      return this.stats;
   }

   public boolean isInParty() {
      return this.party != null;
   }

   public VParty getParty() {
      return this.party;
   }

   public boolean isPartyLeader() {
      return this.isInParty() && this.party.leader.equals(this.username);
   }

   public ArrowTrail getArrowTrail() {
      return this.arrowTrail;
   }

   public void setArrowTrail(ArrowTrail arrowTrail) {
      if (arrowTrail != this.arrowTrail) {
         this.arrowTrail = arrowTrail;
         if (arrowTrail == null) {
            this.removeMeta("arr.sel");
         } else {
            this.setMeta("arr.sel", arrowTrail.getId() + "");
         }
      }

   }

   public void unlockArrowTrail(ArrowTrail trail) {
      if (this.availableArrowTrails.add(trail.getId())) {
         this.saveAvailableArrowTrails();
      }

   }

   public VMultipliers getMultipliers() {
      return this.multipliers;
   }

   public long getLoginTime() {
      return this.loginTime;
   }

   public int getLevel() {
      return this.level;
   }

   public int getTotalExp() {
      return this.exp;
   }

   public int getPartialExp() {
      return this.exp - Leveling.getTotalExp(this.level);
   }

   public void giveExp(int exp) {
      if (exp > 0) {
         this.exp += exp;
         this.expBuffer += exp;
         VTexteria.showGiveExp(this, exp);
         if (this.exp >= Leveling.getTotalExp(this.level + 1)) {
            this.level = Leveling.getLevel(this.exp);
            VTexteria.showUsername(this);
            U.msg(this.player, (String[])(T.success("VimeWorld", "Поздравляем! Вы получили &f" + this.level + "&a уровень! Зайдите в меню &f/me&a чтобы получить награду!")));
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
   }

   public void dispose() {
      this.goals.goals.clear();
      this.goals = null;
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

   public static VPlayer get(String player) {
      VPlayer val = (VPlayer)PLAYERS.get(player);
      if (val == null) {
         Player bukkitPlayer = Bukkit.getPlayerExact(player);
         if (bukkitPlayer == null) {
            throw new IllegalArgumentException("Player with name '" + player + "' not exists on server");
         }

         val = (VPlayer)PLAYERS.computeIfAbsent(player, (name) -> (VPlayer)CONSTRUCTOR.apply(bukkitPlayer));
      }

      return val;
   }

   public static VPlayer get(Player player) {
      VPlayer val = (VPlayer)PLAYERS.get(player.getName());
      if (val == null) {
         val = (VPlayer)PLAYERS.computeIfAbsent(player.getName(), (name) -> (VPlayer)CONSTRUCTOR.apply(player));
      }

      return val;
   }
}
