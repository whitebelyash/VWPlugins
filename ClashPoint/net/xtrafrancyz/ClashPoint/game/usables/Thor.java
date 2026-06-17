/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  net.xtrafrancyz.VimeNetwork.api.util.E
 *  net.xtrafrancyz.VimeNetwork.api.util.Items
 *  org.bukkit.Material
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 */
package net.xtrafrancyz.ClashPoint.game.usables;

import com.google.common.collect.Sets;
import java.util.HashSet;
import net.xtrafrancyz.VimeNetwork.api.util.E;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Thor
implements Listener {
    public static final ItemStack ITEM = Items.name((Material)Material.BONE, (String)"&b\u041a\u043e\u0441\u0442\u044c \u0422\u043e\u0440\u0430", (String[])new String[]{"&7\u041c\u0435\u0442\u0430\u0435\u0442 \u043c\u043e\u043b\u043d\u0438\u0438"});
    private static final HashSet<Byte> TRANSPARENT_BLOCKS = Sets.newHashSet((Object[])new Byte[]{(byte)0, (byte)6, (byte)8, (byte)9, (byte)20, (byte)27, (byte)28, (byte)30, (byte)31, (byte)37, (byte)38, (byte)40, (byte)44, (byte)50, (byte)51, (byte)64, (byte)65, (byte)66, (byte)69, (byte)70, (byte)71, (byte)72, (byte)78, (byte)85, (byte)101, (byte)102, (byte)106, (byte)111, (byte)113, (byte)126, (byte)-113, (byte)-99, (byte)-85, (byte)67});

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (E.isRightClick((PlayerInteractEvent)event) && event.hasItem() && event.getItem().getType() == ITEM.getType()) {
            event.getPlayer().getWorld().strikeLightning(event.getPlayer().getTargetBlock(TRANSPARENT_BLOCKS, 150).getLocation());
            PlayerInventory inv = event.getPlayer().getInventory();
            ItemStack used = inv.getItemInHand();
            used.setAmount(used.getAmount() - 1);
            inv.setItem(inv.getHeldItemSlot(), used.getAmount() == 0 ? null : used);
        }
    }
}

