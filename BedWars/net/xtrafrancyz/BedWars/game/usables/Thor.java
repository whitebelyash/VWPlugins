package net.xtrafrancyz.BedWars.game.usables;

import com.google.common.collect.Sets;
import java.util.HashSet;
import net.xtrafrancyz.BedWars.PlayerInfo;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.util.E;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Thor implements Listener {
   public static final ItemStack ITEM;
   private static final HashSet TRANSPARENT_BLOCKS;

   @EventHandler
   public void onInteract(PlayerInteractEvent event) {
      if (E.isRightClick(event) && event.hasItem() && event.getItem().getType() == ITEM.getType()) {
         event.getPlayer().getWorld().strikeLightning(event.getPlayer().getTargetBlock(TRANSPARENT_BLOCKS, 150).getLocation());
         PlayerInventory inv = event.getPlayer().getInventory();
         ItemStack used = inv.getItemInHand();
         used.setAmount(used.getAmount() - 1);
         inv.setItem(inv.getHeldItemSlot(), used.getAmount() == 0 ? null : used);
         if (PlayerInfo.get(event.getPlayer()).thorUses++ == 49) {
            VimeNetwork.getPlayer(event.getPlayer()).getAchievements().complete(Achievement.BW_THOR_FATHER);
         }
      }

   }

   static {
      ITEM = Items.name(Material.BONE, "&bКость Тора", new String[]{"&7Метает молнии"});
      TRANSPARENT_BLOCKS = Sets.newHashSet(new Byte[]{0, 6, 8, 9, 20, 27, 28, 30, 31, 37, 38, 40, 44, 50, 51, 64, 65, 66, 69, 70, 71, 72, 78, 85, 101, 102, 106, 111, 113, 126, -113, -99, -85, 67});
   }
}
