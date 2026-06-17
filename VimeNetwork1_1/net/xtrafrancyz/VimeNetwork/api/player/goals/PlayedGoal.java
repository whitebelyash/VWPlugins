package net.xtrafrancyz.VimeNetwork.api.player.goals;

import java.util.Collections;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.ServerType;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PlayedGoal extends ExpCoinsGoal {
   public PlayedGoal() {
      this(0, 0);
   }

   public PlayedGoal(int rewardCoins, int rewardExp) {
      super(rewardCoins, rewardExp);
   }

   public ItemStack getItem() {
      return new ItemStack(Material.EMERALD);
   }

   public List getText(boolean addGame) {
      String text = "&fСыграть &e" + this.getGoal() + U.plurals(this.getGoal(), " игру", " игры", " игр");
      if (addGame) {
         text = text + "&f на " + ServerType.byId(this.game).getName();
      }

      return Collections.singletonList(text);
   }
}
