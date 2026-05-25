package net.xtrafrancyz.VimeNetwork.api.player.goals;

import java.util.Collections;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.ServerType;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PointGoal extends ExpCoinsGoal {
   public PointGoal() {
      this(0, 0);
   }

   public PointGoal(int rewardCoins, int rewardExp) {
      super(rewardCoins, rewardExp);
   }

   public ItemStack getItem() {
      return new ItemStack(Material.GOLD_NUGGET);
   }

   public List getText(boolean addGame) {
      String text = "&fПолучить &e" + this.getGoal() + U.plurals(this.getGoal(), " очко", " очка", " очков");
      if (addGame) {
         text = text + "&f на " + ServerType.byId(this.game).getName();
      }

      return Collections.singletonList(text);
   }
}
