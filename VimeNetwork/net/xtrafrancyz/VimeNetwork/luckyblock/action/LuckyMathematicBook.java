package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.luckyblock.LBActionItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LuckyMathematicBook extends LBActionItem {
   protected void populateDrop(List drop, Block block, Player player) {
      drop.add(Items.enchant(Items.name(Material.BOOK, "&bУчебник математики", "&7Бьет гранитом науки"), Enchantment.DAMAGE_ALL, 2));
   }
}
