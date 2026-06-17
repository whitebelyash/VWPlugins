/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 */
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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class LuckyTeleportEnderpearl
extends LBActionItem {
    Supplier<List<Player>> targets;

    public LuckyTeleportEnderpearl(Supplier<List<Player>> targets) {
        this.targets = targets;
    }

    @Override
    protected void populateDrop(List<ItemStack> drop, Block block, Player player) {
        ItemStack is = Items.name(Material.ENDER_PEARL, "&c\u041c\u0435\u0433\u0430 \u0436\u0435\u043c\u0447\u0443\u0433 \u044d\u043d\u0434\u0435\u0440\u0430", "\u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u0443\u0435\u0442 \u0432\u0430\u0441 \u043a \u0441\u043b\u0443\u0447\u0430\u0439\u043d\u043e\u043c\u0443 \u0436\u0438\u0432\u043e\u043c\u0443 \u0438\u0433\u0440\u043e\u043a\u0443");
        is = this.lb.controller.setInteractCallback(this, is);
        drop.add(is);
    }

    @Override
    public void onItemInteract(PlayerInteractEvent event) {
        if (E.isRightClick(event)) {
            event.setCancelled(true);
            List<Player> players = this.targets.get();
            players.remove(event.getPlayer());
            if (players.isEmpty()) {
                U.msg((CommandSender)event.getPlayer(), "&c\u0412\u044b \u043e\u0441\u0442\u0430\u043b\u0438\u0441\u044c \u043e\u0434\u043d\u0438");
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

