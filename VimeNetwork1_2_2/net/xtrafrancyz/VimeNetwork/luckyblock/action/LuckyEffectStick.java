/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.potion.PotionEffect
 */
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

public class LuckyEffectStick
extends LBActionItem {
    private PotionEffect effect;
    private int cooldown;

    public LuckyEffectStick(PotionEffect effect, int cooldown) {
        this.effect = effect;
        this.cooldown = cooldown;
    }

    @Override
    protected void populateDrop(List<ItemStack> drop, Block block, Player player) {
        ItemStack is = Items.name(Material.STICK, "" + Items.Namer.getEffectName(this.effect), new String[0]);
        is = this.lb.controller.setInteractCallback(this, is);
        drop.add(is);
    }

    @Override
    public void onItemInteract(PlayerInteractEvent event) {
        if (E.isRightClick(event)) {
            PlayerInventory inv = event.getPlayer().getInventory();
            ItemStack used = inv.getItemInHand();
            used.setAmount(used.getAmount() - 1);
            inv.setItem(inv.getHeldItemSlot(), used.getAmount() == 0 ? null : used);
        }
    }
}

