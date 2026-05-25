package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.util.E;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.luckyblock.LBActionItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

public class LuckyEffectStick extends LBActionItem {
   private PotionEffect effect;
   private int cooldown;

   public LuckyEffectStick(PotionEffect effect, int cooldown) {
      this.effect = effect;
      this.cooldown = cooldown;
   }

   protected void populateDrop(List drop, Block block, Player player) {
      ItemStack is = Items.name(Material.STICK, "" + Items.Namer.getEffectName(this.effect));
      is = this.lb.controller.setInteractCallback(this, is);
      drop.add(is);
   }

   public void onItemInteract(PlayerInteractEvent event) {
      if (E.isRightClick(event)) {
         PlayerInventory inv = event.getPlayer().getInventory();
         ItemStack used = inv.getItemInHand();
         used.setAmount(used.getAmount() - 1);
         inv.setItem(inv.getHeldItemSlot(), used.getAmount() == 0 ? null : used);
      }

   }
}
