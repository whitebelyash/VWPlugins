package net.xtrafrancyz.VimeNetwork.api.player;

import java.util.Map;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievements;
import net.xtrafrancyz.VimeNetwork.api.player.goals.Goals;
import net.xtrafrancyz.VimeNetwork.api.player.treasure.Treasures;
import org.bukkit.entity.Player;

public interface NetworkPlayer {
   String getMeta(String var1);

   void setMeta(String var1, String var2);

   String removeMeta(String var1);

   boolean hasMeta(String var1);

   Map getMetaMap();

   void setTag(String var1);

   String getTag();

   boolean hasTag();

   void removeTag();

   int addCoins(int var1);

   void addCoinsExact(int var1);

   int getCoins();

   void takeCoins(int var1);

   void toLobby();

   void toServer(String var1);

   Rank getRank();

   String getName();

   int getId();

   String getPrefixedName();

   String getPrefix();

   String getColoredName();

   Player getBukkitPlayer();

   boolean isOnline();

   Goals getGoals();

   Treasures getTreasures();

   Achievements getAchievements();

   Stats getStats();

   boolean isInParty();

   Party getParty();

   boolean isPartyLeader();

   ArrowTrail getArrowTrail();

   void setArrowTrail(ArrowTrail var1);

   void unlockArrowTrail(ArrowTrail var1);

   Multipliers getMultipliers();

   long getLoginTime();

   int getLevel();

   int getTotalExp();

   int getPartialExp();

   void giveExp(int var1);
}
