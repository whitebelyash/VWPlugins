/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.geom.Vec3i
 *  net.xtrafrancyz.VimeNetwork.api.util.Items
 *  net.xtrafrancyz.VimeNetwork.api.util.U
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.command.CommandSender
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.player.PlayerMoveEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package net.xtrafrancyz.ClashPoint.game.usables;

import java.util.HashMap;
import java.util.Map;
import net.xtrafrancyz.ClashPoint.ClashPoint;
import net.xtrafrancyz.ClashPoint.game.CPTexteria;
import net.xtrafrancyz.ClashPoint.game.GameState;
import net.xtrafrancyz.ClashPoint.object.CPTeam;
import net.xtrafrancyz.ClashPoint.object.PlayerInfo;
import net.xtrafrancyz.ClashPoint.util.CommonUtils;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3i;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Trap
implements Listener {
    public static final ItemStack ITEM = Items.name((Material)Material.STRING, (String)"&b\u041b\u043e\u0432\u0443\u0448\u043a\u0430", (String[])new String[]{"&7 \u0418\u043d\u0444\u043e\u0440\u043c\u0438\u0440\u0443\u0435\u0442 \u0432\u0430\u0441 \u043e \u0442\u043e\u043c, \u043a\u043e\u0433\u0434\u0430", "&7\u043f\u0440\u043e\u0442\u0438\u0432\u043d\u0438\u043a \u043d\u0430\u0441\u0442\u0443\u043f\u0430\u0435\u0442 \u043d\u0430 \u043b\u043e\u0432\u0443\u0448\u043a\u0443.", "&7 \u0422\u0430\u043a\u0436\u0435 \u043e\u043d \u043f\u043e\u043b\u0443\u0447\u0430\u0435\u0442 \u043e\u0442\u0440\u0438\u0446\u0430\u0442\u0435\u043b\u044c\u043d\u044b\u0435", "&7\u044d\u0444\u0444\u0435\u043a\u0442\u044b \u043d\u0430 10 \u0441\u0435\u043a."});
    private static final int DURATION = 10000;
    private final ClashPoint plugin;
    public final Map<Vec3i, CPTeam> traps = new HashMap<Vec3i, CPTeam>();

    public Trap(ClashPoint plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Vec3i loc;
        CPTeam team;
        if (this.plugin.game.getState() != GameState.GAME) {
            return;
        }
        if (CommonUtils.isSameBlock(event.getFrom(), event.getTo())) {
            return;
        }
        if (this.plugin.spectators.contains(event.getPlayer())) {
            return;
        }
        Block block = event.getTo().getBlock();
        if (block.getType() == Material.TRIPWIRE && (team = this.traps.get(loc = new Vec3i(event.getTo()))) != null) {
            PlayerInfo noob = PlayerInfo.get(event.getPlayer());
            if (team.equals(noob.team)) {
                return;
            }
            this.traps.remove(loc);
            noob.player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 2), true);
            noob.player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 2), true);
            noob.player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1), true);
            int task = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this.plugin, () -> noob.player.playSound(noob.player.getLocation(), Sound.FUSE, 2.0f, 1.0f), 0L, 20L);
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> Bukkit.getScheduler().cancelTask(task), 200L);
            block.setType(Material.AIR);
            for (PlayerInfo owner : team.players) {
                U.msg((CommandSender)owner.player, (String[])new String[]{"&a\u0418\u0433\u0440\u043e\u043a " + noob.team.chatColor + noob.username + "&a \u043f\u043e\u043f\u0430\u043b\u0441\u044f \u0432 \u0432\u0430\u0448\u0443 \u043b\u043e\u0432\u0443\u0448\u043a\u0443"});
            }
            CPTexteria.showCustomMessage(noob.player, "\u0412\u044b \u043f\u043e\u043f\u0430\u043b\u0438\u0441\u044c \u0432 " + team.chatColor + team.names[1] + " &r\u043b\u043e\u0432\u0443\u0448\u043a\u0443", -1, 3000L);
            CPTexteria.showCustomTimer(noob.player, "\u0414\u0435\u0439\u0441\u0442\u0432\u0438\u0435 \u043b\u043e\u0432\u0443\u0448\u043a\u0438 &e{S}.{mm} \u0441.", -44205, 10000L, false);
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.plugin.game.getState() != GameState.GAME) {
            return;
        }
        if (event.getBlock().getType() == Material.TRIPWIRE) {
            PlayerInfo player = PlayerInfo.get(event.getPlayer());
            if (player.team != null) {
                this.traps.put(new Vec3i(event.getBlock()), player.team);
                U.msg((CommandSender)player.player, (String[])new String[]{"&e\u041b\u043e\u0432\u0443\u0448\u043a\u0430 \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u0430"});
            }
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        boolean upper = false;
        Block toDestroy = event.getBlock();
        if (toDestroy.getType() != Material.TRIPWIRE) {
            toDestroy = toDestroy.getRelative(BlockFace.UP);
            upper = true;
        }
        if (toDestroy.getType() != Material.TRIPWIRE) {
            return;
        }
        CPTeam placer = this.traps.remove(new Vec3i(toDestroy));
        if (placer != null) {
            PlayerInfo breaker = PlayerInfo.get(event.getPlayer());
            if (placer.equals(breaker.team)) {
                U.msg((CommandSender)breaker.player, (String[])new String[]{"&c\u0412\u044b \u0441\u043b\u043e\u043c\u0430\u043b\u0438 \u0441\u0432\u043e\u044e \u043b\u043e\u0432\u0443\u0448\u043a\u0443"});
                toDestroy.getWorld().dropItemNaturally(toDestroy.getLocation(), ITEM.clone());
            } else {
                U.msg((CommandSender)breaker.player, (String[])new String[]{"&a\u0412\u044b \u0441\u043b\u043e\u043c\u0430\u043b\u0438 \u0432\u0440\u0430\u0436\u0435\u0441\u043a\u0443\u044e \u043b\u043e\u0432\u0443\u0448\u043a\u0443"});
            }
            toDestroy.setType(Material.AIR);
            if (!upper) {
                event.setCancelled(true);
            }
        }
    }
}

