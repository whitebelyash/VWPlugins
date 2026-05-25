package net.xtrafrancyz.VimeNetwork.api.player.goals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.ServerType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NexusGoal extends ExpCoinsGoal {
   public NexusGoal() {
      this(0, 0);
   }

   public NexusGoal(int rewardCoins, int rewardExp) {
      super(rewardCoins, rewardExp);
   }

   public ItemStack getItem() {
      return new ItemStack(Material.BED);
   }

   public List getText(boolean addGame) {
      return addGame ? Arrays.asList("&fНанести базе &e" + this.getGoal() + " урона", "&fна " + ServerType.byId(this.game).getName()) : Collections.singletonList("&fНанести базе &e" + this.getGoal() + " урона");
   }
}
