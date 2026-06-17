package net.xtrafrancyz.VimeNetwork.luckyblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3i;
import net.xtrafrancyz.VimeNetwork.api.player.goals.GoalQuery;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.Rand;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class LBController implements Listener {
   private static final String NBT_INTERACT = "_i";
   private static final String NBT_SHOOT_BOW = "_sb";
   private static final String NBT_ITEM_CONSUME = "_c";
   Map blockInteract = new HashMap();
   Map blockBreak = new HashMap();
   List luckyBreakListeners = new ArrayList();
   List actions = new ArrayList();
   int weightLimit = 0;

   public LBController(LuckyBlocks lb) {
      lb.getPlugin().getServer().getPluginManager().registerEvents(this, lb.getPlugin());
   }

   public ItemStack setInteractCallback(LBAction action, ItemStack is) {
      return Items.nbt(is).setInt("_i", action.id + 1).build();
   }

   public ItemStack setShootBowCallback(LBAction action, ItemStack is) {
      return Items.nbt(is).setInt("_sb", action.id + 1).build();
   }

   public ItemStack setConsumeCallback(LBAction action, ItemStack is) {
      return Items.nbt(is).setInt("_c", action.id + 1).build();
   }

   public void setBlockInteractCallback(Block block, Consumer callback) {
      this.blockInteract.put(new Vec3i(block.getLocation()), callback);
   }

   public void removeBlockInteractCallback(Block block) {
      this.blockInteract.remove(new Vec3i(block.getLocation()));
   }

   public void setBlockBreakCallback(Block block, Consumer callback) {
      this.blockBreak.put(new Vec3i(block.getLocation()), callback);
   }

   public void removeBlockBreakCallback(Block block) {
      this.blockBreak.remove(new Vec3i(block.getLocation()));
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   private void onBlockBreak(BlockBreakEvent event) {
      if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
         if (LuckyBlocks.isLuckyBlock(event.getBlock())) {
            if (event.isCancelled()) {
               return;
            }

            try {
               for(Consumer consumer : this.luckyBreakListeners) {
                  consumer.accept(event);
               }
            } catch (Exception ex) {
               ex.printStackTrace();
            }

            int selected = Rand.intRange(0, this.weightLimit);
            LBActionEntry entry = (LBActionEntry)this.actions.get(0);

            for(LBActionEntry e : this.actions) {
               if (selected < e.weight) {
                  entry = e;
                  break;
               }

               selected -= e.weight;
            }

            entry.action.onBreak(event.getBlock(), event.getPlayer());
            VimeNetwork.getPlayer(event.getPlayer()).getGoals().trigger(VimeNetwork.lobby().getServerTypeId().toLowerCase(), GoalQuery.of("luckyblock"));
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
         } else {
            Consumer<BlockBreakEvent> callback = (Consumer)this.blockBreak.get(new Vec3i(event.getBlock().getLocation()));
            if (callback != null) {
               callback.accept(event);
            }
         }

      }
   }

   @EventHandler
   private void onInteract(PlayerInteractEvent event) {
      if (event.hasItem()) {
         int action = Items.nbt(event.getItem()).getInt("_i") - 1;
         if (action >= 0) {
            ((LBActionItem)((LBActionEntry)this.actions.get(action)).action).onItemInteract(event);
         }
      }

      if (event.hasBlock()) {
         Consumer<PlayerInteractEvent> callback = (Consumer)this.blockInteract.get(new Vec3i(event.getClickedBlock().getLocation()));
         if (callback != null) {
            callback.accept(event);
         }
      }

   }

   @EventHandler
   private void onShootBow(EntityShootBowEvent event) {
      if (event.getEntityType() == EntityType.PLAYER) {
         int action = Items.nbt(event.getBow()).getInt("_sb") - 1;
         if (action >= 0) {
            ((LBActionItem)((LBActionEntry)this.actions.get(action)).action).onShootBow(event);
         }
      }

   }

   @EventHandler
   private void onItemConsume(PlayerItemConsumeEvent event) {
      int action = Items.nbt(event.getItem()).getInt("_c") - 1;
      if (action >= 0) {
         ((LBActionItem)((LBActionEntry)this.actions.get(action)).action).onItemConsume(event);
      }

   }

   @EventHandler
   private void onFoodLevelChange(FoodLevelChangeEvent event) {
      if (event.getEntityType() == EntityType.PLAYER) {
         Player player = (Player)event.getEntity();
         if (player.getFoodLevel() < event.getFoodLevel()) {
            ItemStack hand = player.getInventory().getItemInHand();
            int action = Items.nbt(hand).getInt("_c") - 1;
            if (action >= 0) {
               PlayerItemConsumeEvent event2 = new PlayerItemConsumeEvent(player, hand);
               ((LBActionItem)((LBActionEntry)this.actions.get(action)).action).onItemConsume(event2);
               if (event2.isCancelled()) {
                  event.setCancelled(true);
               }
            }
         }
      }

   }
}
