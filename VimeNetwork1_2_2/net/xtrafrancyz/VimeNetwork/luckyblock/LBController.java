/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.GameMode
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.entity.EntityShootBowEvent
 *  org.bukkit.event.entity.FoodLevelChangeEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerItemConsumeEvent
 *  org.bukkit.inventory.ItemStack
 */
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
import net.xtrafrancyz.VimeNetwork.luckyblock.LBAction;
import net.xtrafrancyz.VimeNetwork.luckyblock.LBActionEntry;
import net.xtrafrancyz.VimeNetwork.luckyblock.LBActionItem;
import net.xtrafrancyz.VimeNetwork.luckyblock.LuckyBlocks;
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

public class LBController
implements Listener {
    private static final String NBT_INTERACT = "_i";
    private static final String NBT_SHOOT_BOW = "_sb";
    private static final String NBT_ITEM_CONSUME = "_c";
    Map<Vec3i, Consumer<PlayerInteractEvent>> blockInteract;
    Map<Vec3i, Consumer<BlockBreakEvent>> blockBreak;
    List<Consumer<BlockBreakEvent>> luckyBreakListeners;
    List<LBActionEntry> actions = new ArrayList<LBActionEntry>();
    int weightLimit = 0;

    public LBController(LuckyBlocks lb) {
        this.luckyBreakListeners = new ArrayList<Consumer<BlockBreakEvent>>();
        this.blockInteract = new HashMap<Vec3i, Consumer<PlayerInteractEvent>>();
        this.blockBreak = new HashMap<Vec3i, Consumer<BlockBreakEvent>>();
        lb.getPlugin().getServer().getPluginManager().registerEvents((Listener)this, lb.getPlugin());
    }

    public ItemStack setInteractCallback(LBAction action, ItemStack is) {
        return Items.nbt(is).setInt(NBT_INTERACT, action.id + 1).build();
    }

    public ItemStack setShootBowCallback(LBAction action, ItemStack is) {
        return Items.nbt(is).setInt(NBT_SHOOT_BOW, action.id + 1).build();
    }

    public ItemStack setConsumeCallback(LBAction action, ItemStack is) {
        return Items.nbt(is).setInt(NBT_ITEM_CONSUME, action.id + 1).build();
    }

    public void setBlockInteractCallback(Block block, Consumer<PlayerInteractEvent> callback) {
        this.blockInteract.put(new Vec3i(block.getLocation()), callback);
    }

    public void removeBlockInteractCallback(Block block) {
        this.blockInteract.remove(new Vec3i(block.getLocation()));
    }

    public void setBlockBreakCallback(Block block, Consumer<BlockBreakEvent> callback) {
        this.blockBreak.put(new Vec3i(block.getLocation()), callback);
    }

    public void removeBlockBreakCallback(Block block) {
        this.blockBreak.remove(new Vec3i(block.getLocation()));
    }

    @EventHandler(priority=EventPriority.HIGH)
    private void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if (LuckyBlocks.isLuckyBlock(event.getBlock())) {
            if (event.isCancelled()) {
                return;
            }
            try {
                for (Consumer<BlockBreakEvent> consumer : this.luckyBreakListeners) {
                    consumer.accept(event);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            int selected = Rand.intRange(0, this.weightLimit);
            LBActionEntry entry = this.actions.get(0);
            for (LBActionEntry e : this.actions) {
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
            Consumer<BlockBreakEvent> callback = this.blockBreak.get(new Vec3i(event.getBlock().getLocation()));
            if (callback != null) {
                callback.accept(event);
            }
        }
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        Consumer<PlayerInteractEvent> callback;
        int action;
        if (event.hasItem() && (action = Items.nbt(event.getItem()).getInt(NBT_INTERACT) - 1) >= 0) {
            ((LBActionItem)this.actions.get((int)action).action).onItemInteract(event);
        }
        if (event.hasBlock() && (callback = this.blockInteract.get(new Vec3i(event.getClickedBlock().getLocation()))) != null) {
            callback.accept(event);
        }
    }

    @EventHandler
    private void onShootBow(EntityShootBowEvent event) {
        int action;
        if (event.getEntityType() == EntityType.PLAYER && (action = Items.nbt(event.getBow()).getInt(NBT_SHOOT_BOW) - 1) >= 0) {
            ((LBActionItem)this.actions.get((int)action).action).onShootBow(event);
        }
    }

    @EventHandler
    private void onItemConsume(PlayerItemConsumeEvent event) {
        int action = Items.nbt(event.getItem()).getInt(NBT_ITEM_CONSUME) - 1;
        if (action >= 0) {
            ((LBActionItem)this.actions.get((int)action).action).onItemConsume(event);
        }
    }

    @EventHandler
    private void onFoodLevelChange(FoodLevelChangeEvent event) {
        ItemStack hand;
        int action;
        Player player;
        if (event.getEntityType() == EntityType.PLAYER && (player = (Player)event.getEntity()).getFoodLevel() < event.getFoodLevel() && (action = Items.nbt(hand = player.getInventory().getItemInHand()).getInt(NBT_ITEM_CONSUME) - 1) >= 0) {
            PlayerItemConsumeEvent event2 = new PlayerItemConsumeEvent(player, hand);
            ((LBActionItem)this.actions.get((int)action).action).onItemConsume(event2);
            if (event2.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }
}

