package net.xtrafrancyz.VimeNetwork.api.player.goals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.ServerType;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.inventory.ItemStack;

public class WavesGoal extends ExpCoinsGoal {
   public int streak;

   public WavesGoal() {
      this(0, 0);
   }

   public WavesGoal(int rewardCoins, int rewardExp) {
      super(rewardCoins, rewardExp);
      this.streak = 0;
   }

   public void write(JsonObject json) {
      super.write(json);
      if (this.streak > 0) {
         json.addProperty("s", this.streak);
      }

   }

   public void read(JsonObject json) {
      super.read(json);
      JsonElement elem = json.get("s");
      if (elem != null) {
         this.streak = elem.getAsInt();
      }

   }

   public boolean isApplicable(NetworkPlayer player, GoalQuery query) {
      Object streak0 = query.data.get("streak");
      return this.streak == 0 || streak0 != null && (Integer)streak0 == this.streak;
   }

   public ItemStack getItem() {
      return new ItemStack(Material.NETHER_STAR);
   }

   public List getText(boolean addGame) {
      if (this.streak > 0) {
         return addGame ? Arrays.asList("&fДостигнуть &e" + this.streak + "-й волны", "&fна " + ServerType.byId(this.game).getName()) : Collections.singletonList("&fДостигнуть &e" + this.streak + "-й волны");
      } else {
         return addGame ? Arrays.asList("&fПродержаться &e" + this.getGoal() + U.plurals(this.getGoal(), " волну", " волны", " волн"), "&fна " + ServerType.byId(this.game).getName()) : Collections.singletonList("&fПродержаться &e" + this.getGoal() + U.plurals(this.getGoal(), " волну", " волны", " волн"));
      }
   }
}
