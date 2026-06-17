/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.event.PlayerLeaveEvent
 *  net.xtrafrancyz.VimeNetwork.api.util.E
 *  net.xtrafrancyz.VimeNetwork.api.util.Items
 *  net.xtrafrancyz.VimeNetwork.api.util.Particles
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.util.Vector
 */
package net.xtrafrancyz.ClashPoint.game.usables;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.xtrafrancyz.ClashPoint.ClashPoint;
import net.xtrafrancyz.ClashPoint.game.CPTexteria;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLeaveEvent;
import net.xtrafrancyz.VimeNetwork.api.util.E;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.Particles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class RescuePlatform
implements Listener {
    public static final ItemStack ITEM = Items.name((Material)Material.BLAZE_ROD, (String)"&b\u0421\u043f\u0430\u0441\u0438\u0442\u0435\u043b\u044c\u043d\u0430\u044f \u043f\u043b\u0430\u0442\u0444\u043e\u0440\u043c\u0430", (String[])new String[]{"&7\u0421\u043f\u0430\u0441\u0438 \u0441\u0435\u0431\u044f \u043e\u0442 \u043f\u0430\u0434\u0435\u043d\u0438\u044f!", "&7\u0412 \u0442\u0435\u0447\u0435\u043d\u0438\u0438 &f10 \u0441\u0435\u043a\u0443\u043d\u0434&7 \u0432\u044b \u0431\u0443\u0434\u0435\u0442\u0435", "&7\u0441\u0442\u043e\u044f\u0442\u044c \u043d\u0430 \u0441\u0442\u0435\u043a\u043b\u044f\u043d\u043d\u043e\u0439 \u043f\u043b\u0430\u0442\u0444\u043e\u0440\u043c\u0435."});
    private final ClashPoint plugin;
    private final Set<Player> cooldown = new HashSet<Player>();

    public RescuePlatform(ClashPoint plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (E.isRightClick((PlayerInteractEvent)event) && event.hasItem() && event.getItem().getType() == ITEM.getType()) {
            if (this.cooldown.contains(event.getPlayer())) {
                CPTexteria.showCustomMessage(event.getPlayer(), "\u041f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u043a\u0430", -44205, 2000L);
                return;
            }
            if (event.getPlayer().getLocation().add(0.0, -1.0, 0.0).getBlock().getType() != Material.AIR) {
                return;
            }
            PlayerInventory inv = event.getPlayer().getInventory();
            ItemStack used = inv.getItemInHand();
            used.setAmount(used.getAmount() - 1);
            inv.setItem(inv.getHeldItemSlot(), used.getAmount() == 0 ? null : used);
            event.getPlayer().setFallDistance(0.0f);
            event.getPlayer().setVelocity(new Vector(0, 0, 0));
            Platform platform = new Platform();
            Location midLoc = event.getPlayer().getLocation().add(0.0, -1.0, 0.0);
            if (midLoc.getY() < 0.0) {
                midLoc.setY(2.0);
            }
            Block mid = midLoc.getBlock();
            for (BlockFace face : BlockFace.values()) {
                Block placed;
                if (face == BlockFace.DOWN || face == BlockFace.UP || (placed = mid.getRelative(face)).getType() != Material.AIR) continue;
                placed.setType(Material.GLASS);
                platform.blocks.add(placed);
            }
            Location loc = midLoc.add(0.5, 1.3, 0.5);
            Location ploc = event.getPlayer().getLocation();
            loc.setPitch(ploc.getPitch());
            loc.setYaw(ploc.getYaw());
            event.getPlayer().teleport(loc);
            this.cooldown.add(event.getPlayer());
            Particles.CLOUD.play(loc, 2.0f, 0.2f, 2.0f, 0.0f, 100, new Player[0]);
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, (Runnable)platform, 200L);
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> this.cooldown.remove(event.getPlayer()), 400L);
            CPTexteria.showCustomTimer(event.getPlayer(), "\u041f\u043b\u0430\u0442\u0444\u043e\u0440\u043c\u0430 &e{S}.{mm} \u0441.", CPTexteria.DEFAULT_BAR_COLOR, 10000L, false);
        }
    }

    @EventHandler
    private void onPlayerLeave(PlayerLeaveEvent event) {
        this.cooldown.remove(event.getPlayer());
    }

    private class Platform
    implements Runnable {
        private List<Block> blocks = new ArrayList<Block>(20);

        private Platform() {
        }

        @Override
        public void run() {
            this.blocks.stream().filter(block -> block.getType() == Material.GLASS).forEach(block -> block.setType(Material.AIR));
            this.blocks.clear();
        }
    }
}

