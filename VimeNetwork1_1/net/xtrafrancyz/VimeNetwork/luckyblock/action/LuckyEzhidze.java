package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.luckyblock.LBActionItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LuckyEzhidze extends LBActionItem {
   protected void populateDrop(List drop, Block block, Player player) {
      ItemStack is = Items.enchant(Items.name(Material.CHAINMAIL_CHESTPLATE, "&bЕжидзе", "&7Ультра одноразовая отдача"), Enchantment.THORNS, 40);
      is.setDurability((short)(Material.CHAINMAIL_CHESTPLATE.getMaxDurability() - 1));
      drop.add(is);
   }
}
