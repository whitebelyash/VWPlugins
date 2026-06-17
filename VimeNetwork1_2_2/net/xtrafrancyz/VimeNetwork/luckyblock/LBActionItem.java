/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 *  org.bukkit.event.entity.EntityShootBowEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerItemConsumeEvent
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.luckyblock;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.luckyblock.LBAction;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public abstract class LBActionItem
extends LBAction {
    @Override
    public void onBreak(Block block, Player player) {
        Location loc = block.getLocation().add(0.5, 0.5, 0.5);
        LinkedList<ItemStack> list = new LinkedList<ItemStack>();
        this.populateDrop(list, block, player);
        for (ItemStack is : list) {
            this.giveItem(player, loc, is);
        }
    }

    protected void giveItem(Player player, Location loc, ItemStack is) {
        HashMap map = player.getInventory().addItem(new ItemStack[]{is});
        for (ItemStack leftover : map.values()) {
            loc.getWorld().dropItemNaturally(loc, leftover);
        }
    }

    protected abstract void populateDrop(List<ItemStack> var1, Block var2, Player var3);

    public void onItemInteract(PlayerInteractEvent event) {
    }

    public void onShootBow(EntityShootBowEvent event) {
    }

    public void onItemConsume(PlayerItemConsumeEvent event) {
    }
}

