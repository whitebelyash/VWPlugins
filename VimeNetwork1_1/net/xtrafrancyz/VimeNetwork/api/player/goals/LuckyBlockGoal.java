package net.xtrafrancyz.VimeNetwork.api.player.goals;

import java.util.Collections;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.ServerType;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.luckyblock.LuckyBlocks;
import org.bukkit.inventory.ItemStack;

public class LuckyBlockGoal extends ExpCoinsGoal {
   public LuckyBlockGoal() {
      this(0, 0);
   }

   public LuckyBlockGoal(int rewardCoins, int rewardExp) {
      super(rewardCoins, rewardExp);
   }

   public ItemStack getItem() {
      return LuckyBlocks.getLuckyBlock();
   }

   public List getText(boolean addGame) {
      String text = "&fСломать &e" + this.getGoal() + U.plurals(this.getGoal(), " Lucky Block", " Lucky Block'a", " Lucky Block'ов");
      if (addGame) {
         text = text + "&f на " + ServerType.byId(this.game).getName();
      }

      return Collections.singletonList(text);
   }
}
