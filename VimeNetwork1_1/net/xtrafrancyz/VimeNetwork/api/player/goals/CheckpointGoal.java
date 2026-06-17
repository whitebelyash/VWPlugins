package net.xtrafrancyz.VimeNetwork.api.player.goals;

import java.util.Collections;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.ServerType;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CheckpointGoal extends ExpCoinsGoal {
   public CheckpointGoal() {
      this(0, 0);
   }

   public CheckpointGoal(int rewardCoins, int rewardExp) {
      super(rewardCoins, rewardExp);
   }

   public ItemStack getItem() {
      return new ItemStack(Material.GOLD_NUGGET);
   }

   public List getText(boolean addGame) {
      String text = "&fПройти &e" + this.getGoal() + U.plurals(this.getGoal(), " чекпоинт", " чекпоинта", " чекпоинтов");
      if (addGame) {
         text = text + "&f на " + ServerType.byId(this.game).getName();
      }

      return Collections.singletonList(text);
   }
}
