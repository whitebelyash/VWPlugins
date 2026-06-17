/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.MathHelper
 *  net.xtrafrancyz.VimeNetwork.api.event.PlayerLeaveEvent
 *  net.xtrafrancyz.VimeNetwork.api.geom.Vec3f
 *  net.xtrafrancyz.VimeNetwork.api.util.E
 *  net.xtrafrancyz.VimeNetwork.api.util.Items
 *  net.xtrafrancyz.VimeNetwork.api.util.Particles
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerMoveEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.ClashPoint.game.usables;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.v1_6_R3.MathHelper;
import net.xtrafrancyz.ClashPoint.ClashPoint;
import net.xtrafrancyz.ClashPoint.game.CPTexteria;
import net.xtrafrancyz.ClashPoint.game.GameState;
import net.xtrafrancyz.ClashPoint.object.PlayerInfo;
import net.xtrafrancyz.ClashPoint.util.CommonUtils;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLeaveEvent;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3f;
import net.xtrafrancyz.VimeNetwork.api.util.E;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.Particles;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

public class WarpPowder
implements Listener {
    public static final ItemStack ITEM = Items.name((Material)Material.SULPHUR, (String)"&b\u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442 \u0434\u043e\u043c\u043e\u0439 (&e6 \u0441\u0435\u043a.&b)", (String[])new String[]{"&7\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u043f\u0440\u0430\u0432\u043e\u0439 \u043a\u043d\u043e\u043f\u043a\u043e\u0439 \u0438 \u0447\u0435\u0440\u0435\u0437", "&f6 \u0441\u0435\u043a\u0443\u043d\u0434&7 \u0412\u044b \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u0443\u0435\u0442\u0435\u0441\u044c \u0434\u043e\u043c\u043e\u0439.", "&c\u0412\u043d\u0438\u043c\u0430\u043d\u0438\u0435: &f\u041f\u0435\u0440\u0435\u043c\u0435\u0449\u0435\u043d\u0438\u0435 \u043e\u0442\u043c\u0435\u043d\u044f\u0435\u0442 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044e"});
    private static final int DURATION = 6000;
    private static final int PARTICLES_FREQUENCY = 150;
    private final ClashPoint plugin;
    private final Map<Player, WarpInfo> warpers = new HashMap<Player, WarpInfo>();

    public WarpPowder(ClashPoint plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (E.isRightClick((PlayerInteractEvent)event) && event.hasItem() && event.getItem().getType() == ITEM.getType()) {
            if (event.getPlayer().getLocation().add(0.0, -1.0, 0.0).getBlock().getType() == Material.AIR) {
                return;
            }
            if (this.warpers.containsKey(event.getPlayer())) {
                return;
            }
            PlayerInventory inv = event.getPlayer().getInventory();
            ItemStack used = inv.getItemInHand();
            used.setAmount(used.getAmount() - 1);
            inv.setItem(inv.getHeldItemSlot(), used.getAmount() == 0 ? null : used);
            WarpInfo warp = new WarpInfo(event.getPlayer());
            this.warpers.put(event.getPlayer(), warp);
            warp.start();
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (this.plugin.game.getState() != GameState.GAME) {
            return;
        }
        if (CommonUtils.isSameBlock(event.getFrom(), event.getTo())) {
            return;
        }
        WarpInfo info = this.warpers.get(event.getPlayer());
        if (info != null) {
            info.cancel(false);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerLeaveEvent event) {
        WarpInfo info = this.warpers.get(event.getPlayer());
        if (info != null) {
            info.cancel(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onDamage(EntityDamageEvent event) {
        if (this.plugin.game.getState() != GameState.GAME || event.getEntityType() != EntityType.PLAYER) {
            return;
        }
        WarpInfo info = this.warpers.get((Player)event.getEntity());
        if (info != null) {
            info.cancel(false);
        }
    }

    private class WarpParticlesTask
    implements Runnable {
        private static final int CIRCLE_ELEMENTS = 20;
        private static final float Y_CONST = 0.05f;
        private static final float ALPHA_CONST = 0.31415927f;
        private static final float PI_DIV_2 = 1.5707964f;
        private final WarpInfo info;
        private int ticks = 0;

        public WarpParticlesTask(WarpInfo info) {
            this.info = info;
        }

        @Override
        public void run() {
            Vec3f loc = new Vec3f(this.info.who.getLocation());
            float y = 0.05f * (float)this.ticks;
            float alpha = 0.31415927f * (float)this.ticks;
            loc.add(0.0f, y, 0.0f);
            for (int i = 0; i < 4; ++i) {
                float a = alpha + 1.5707964f * (float)i;
                float x = MathHelper.sin((float)a);
                float z = MathHelper.cos((float)a);
                Particles.FIREWORKS_SPARK.play(loc.x + x, loc.y, loc.z + z, 0.0f, 0.0f, 0.0f, 0.0f, 1, new Player[0]);
            }
            ++this.ticks;
        }
    }

    private class WarpTask
    implements Runnable {
        private final WarpInfo info;

        public WarpTask(WarpInfo info) {
            this.info = info;
        }

        @Override
        public void run() {
            Bukkit.getScheduler().cancelTask(this.info.particlesTask);
            CPTexteria.removeCustomTimer(this.info.who);
            WarpPowder.this.warpers.remove(this.info.who);
            CPTexteria.showCustomMessage(this.info.who, "\u0414\u043e\u043c, \u043c\u0438\u043b\u044b\u0439 \u0434\u043e\u043c", -9830551, 2000L);
            this.info.who.teleport(PlayerInfo.get((Player)this.info.who).team.getSpawnLocation());
        }
    }

    private class WarpInfo {
        Player who;
        int task = -1;
        int particlesTask = -1;

        public WarpInfo(Player who) {
            this.who = who;
        }

        public void start() {
            this.task = Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)WarpPowder.this.plugin, (Runnable)new WarpTask(this), 120L);
            this.particlesTask = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)WarpPowder.this.plugin, (Runnable)new WarpParticlesTask(this), 0L, 3L);
            CPTexteria.showCustomTimer(this.who, "\u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044f... &e{S}.{mm} \u0441.", CPTexteria.DEFAULT_BAR_COLOR, 6000L, true);
            CPTexteria.showCustomMessage(this.who, "\u041d\u0435 \u0434\u0432\u0438\u0433\u0430\u0439\u0442\u0435\u0441\u044c 6 \u0441\u0435\u043a\u0443\u043d\u0434", -1, 3000L);
        }

        public void cancel(boolean silent) {
            Bukkit.getScheduler().cancelTask(this.task);
            Bukkit.getScheduler().cancelTask(this.particlesTask);
            WarpPowder.this.warpers.remove(this.who);
            if (!silent) {
                CPTexteria.removeCustomTimer(this.who);
                CPTexteria.showCustomMessage(this.who, "\u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044f \u043e\u0442\u043c\u0435\u043d\u0435\u043d\u0430", -44205, 3000L);
                this.who.getInventory().addItem(new ItemStack[]{ITEM.clone()});
            }
        }
    }
}

