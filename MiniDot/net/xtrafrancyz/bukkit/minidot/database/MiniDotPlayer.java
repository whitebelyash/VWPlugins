package net.xtrafrancyz.bukkit.minidot.database;

import gnu.trove.set.hash.TIntHashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import org.bukkit.entity.Player;

public class MiniDotPlayer {
   public Map dressed;
   public final TIntHashSet available;
   public final Player player;
   public final String username;

   public MiniDotPlayer(Player player) {
      this.player = player;
      this.dressed = new ConcurrentHashMap();
      this.available = new TIntHashSet();
      this.username = player.getName();
   }

   public int getId() {
      return VimeNetwork.getPlayer(this.username).getId();
   }

   public int getCoins() {
      return VimeNetwork.getPlayer(this.username).getCoins();
   }

   public void takeCoins(int amount) {
      VimeNetwork.getPlayer(this.username).takeCoins(amount);
   }
}
