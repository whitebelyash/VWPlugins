package net.xtrafrancyz.VimeNetwork.api.player.goals;

import java.util.Arrays;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;

public abstract class ExpCoinsGoal extends Goal {
   public int rewardCoins;
   public int rewardExp;

   public ExpCoinsGoal(int rewardCoins, int rewardExp) {
      this.rewardCoins = rewardCoins;
      this.rewardExp = rewardExp;
   }

   public void write(JsonObject json) {
      super.write(json);
      json.addProperty("c", this.rewardCoins);
      json.addProperty("e", this.rewardExp);
   }

   public void read(JsonObject json) {
      super.read(json);
      this.rewardCoins = json.get("c").getAsInt();
      this.rewardExp = json.get("e").getAsInt();
   }

   public void complete(NetworkPlayer player) {
      player.addCoinsExact(this.rewardCoins);
      player.giveExp(this.rewardExp);
   }

   public List getRewardText() {
      return Arrays.asList("&7+ &e" + U.pluralsCoins(this.rewardCoins), "&7+ &9" + this.rewardExp + " опыта");
   }
}
