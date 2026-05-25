package net.xtrafrancyz.VimeNetwork.api.player.goals;

import java.util.Collections;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.ServerType;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class LevelsGoal extends ExpCoinsGoal {
   public LevelsGoal() {
      this(0, 0);
   }

   public LevelsGoal(int rewardCoins, int rewardExp) {
      super(rewardCoins, rewardExp);
   }

   public ItemStack getItem() {
      return new ItemStack(Material.EXP_BOTTLE);
   }

   public List getText(boolean addGame) {
      String text = "&fПолучить &e" + this.getGoal() + U.plurals(this.getGoal(), " уровень", " уровня", " уровней");
      if (addGame) {
         text = text + "&f на " + ServerType.byId(this.game).getName();
      }

      return Collections.singletonList(text);
   }
}
