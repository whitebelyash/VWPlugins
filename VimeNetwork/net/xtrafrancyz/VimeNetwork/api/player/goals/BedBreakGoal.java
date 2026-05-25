package net.xtrafrancyz.VimeNetwork.api.player.goals;

import java.util.Collections;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.ServerType;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BedBreakGoal extends ExpCoinsGoal {
   public BedBreakGoal() {
      this(0, 0);
   }

   public BedBreakGoal(int rewardCoins, int rewardExp) {
      super(rewardCoins, rewardExp);
   }

   public ItemStack getItem() {
      return new ItemStack(Material.BED);
   }

   public List getText(boolean addGame) {
      String text = "&fРазрушить &e" + this.getGoal() + U.plurals(this.getGoal(), " кровать", " кровати", " кроватей");
      if (addGame) {
         text = text + "&f на " + ServerType.byId(this.game).getName();
      }

      return Collections.singletonList(text);
   }
}
