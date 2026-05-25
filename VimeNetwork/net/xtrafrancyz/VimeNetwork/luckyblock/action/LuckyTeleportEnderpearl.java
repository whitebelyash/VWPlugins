package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import java.util.List;
import java.util.function.Supplier;
import net.xtrafrancyz.VimeNetwork.api.util.E;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.Rand;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.luckyblock.LBActionItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class LuckyTeleportEnderpearl extends LBActionItem {
   Supplier targets;

   public LuckyTeleportEnderpearl(Supplier targets) {
      this.targets = targets;
   }

   protected void populateDrop(List drop, Block block, Player player) {
      ItemStack is = Items.name(Material.ENDER_PEARL, "&cМега жемчуг эндера", "Телепортирует вас к случайному живому игроку");
      is = this.lb.controller.setInteractCallback(this, is);
      drop.add(is);
   }

   public void onItemInteract(PlayerInteractEvent event) {
      if (E.isRightClick(event)) {
         event.setCancelled(true);
         List<Player> players = (List)this.targets.get();
         players.remove(event.getPlayer());
         if (players.isEmpty()) {
            U.msg(event.getPlayer(), (String[])("&cВы остались одни"));
            return;
         }

         event.getPlayer().teleport((Entity)Rand.of(players));
         PlayerInventory inv = event.getPlayer().getInventory();
         ItemStack used = inv.getItemInHand();
         used.setAmount(used.getAmount() - 1);
         inv.setItem(inv.getHeldItemSlot(), used.getAmount() == 0 ? null : used);
      }

   }
}
