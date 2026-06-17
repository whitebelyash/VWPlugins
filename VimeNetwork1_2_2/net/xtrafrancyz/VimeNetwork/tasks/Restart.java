/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.tasks;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.ServerType;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.event.ServerRestartEvent;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

public class Restart {
    private static final int RESTART_HOUR = 3;
    private static boolean forced = false;
    private static ScheduledFuture<?> future;

    public static void schedule() {
        LocalDateTime now = LocalDateTime.now(VimeNetwork.TZ_MOSCOW);
        int hours = now.getHour();
        int waitHours = hours >= 3 ? 23 - hours + 3 : 3 - hours - 1;
        int waitMins = 59 - now.getMinute();
        int waitSeconds = 59 - now.getSecond();
        if (waitHours < 2) {
            waitHours += 24;
        }
        long waitTimeMillis = (waitHours * 3600 + waitMins * 60 + waitSeconds - 300) * 1000;
        if (future != null) {
            future.cancel(false);
        }
        future = VNPlugin.instance().scheduledExecutor.schedule(Restart::countdown, waitTimeMillis, TimeUnit.MILLISECONDS);
        Bukkit.getPluginManager().callEvent((Event)new ServerRestartEvent(ServerRestartEvent.State.SCHEDULED, false));
    }

    public static void countdown() {
        if (future != null) {
            future.cancel(true);
            future = null;
        }
        Bukkit.getPluginManager().callEvent((Event)new ServerRestartEvent(ServerRestartEvent.State.COUNTDOWN, forced));
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)VNPlugin.instance(), () -> {
            U.bcast("&6=============================");
            U.bcast("&c \u0412\u043d\u0438\u043c\u0430\u043d\u0438\u0435!");
            U.bcast("&c \u0421\u0435\u0440\u0432\u0435\u0440 \u0431\u0443\u0434\u0435\u0442 \u043f\u0435\u0440\u0435\u0437\u0430\u0433\u0440\u0443\u0436\u0435\u043d \u0447\u0435\u0440\u0435\u0437 &65 \u043c\u0438\u043d\u0443\u0442&c!");
            U.bcast("&6=============================");
        });
        ScheduledExecutorService executor = VNPlugin.instance().scheduledExecutor;
        executor.schedule(() -> Restart.bcast("3 \u043c\u0438\u043d\u0443\u0442\u044b"), 120L, TimeUnit.SECONDS);
        executor.schedule(() -> Restart.bcast("1 \u043c\u0438\u043d\u0443\u0442\u0443"), 240L, TimeUnit.SECONDS);
        executor.schedule(() -> Restart.bcast("30 \u0441\u0435\u043a\u0443\u043d\u0434"), 270L, TimeUnit.SECONDS);
        executor.schedule(() -> Restart.bcast("20 \u0441\u0435\u043a\u0443\u043d\u0434"), 280L, TimeUnit.SECONDS);
        executor.schedule(() -> Restart.bcast("10 \u0441\u0435\u043a\u0443\u043d\u0434"), 290L, TimeUnit.SECONDS);
        executor.schedule(() -> Restart.bcast("5 \u0441\u0435\u043a\u0443\u043d\u0434"), 295L, TimeUnit.SECONDS);
        executor.schedule(() -> Restart.bcast("4 \u0441\u0435\u043a\u0443\u043d\u0434\u044b"), 296L, TimeUnit.SECONDS);
        executor.schedule(() -> Restart.bcast("3 \u0441\u0435\u043a\u0443\u043d\u0434\u044b"), 297L, TimeUnit.SECONDS);
        executor.schedule(() -> Restart.bcast("2 \u0441\u0435\u043a\u0443\u043d\u0434\u044b"), 298L, TimeUnit.SECONDS);
        executor.schedule(() -> Restart.bcast("1 \u0441\u0435\u043a\u0443\u043d\u0434\u0443"), 299L, TimeUnit.SECONDS);
        executor.schedule(() -> Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)VNPlugin.instance(), Restart::doRestart), 300L, TimeUnit.SECONDS);
    }

    public static void restart() {
        forced = true;
        Restart.doRestart();
    }

    private static void bcast(String time) {
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)VNPlugin.instance(), () -> U.bcast("&c \u0421\u0435\u0440\u0432\u0435\u0440 \u0431\u0443\u0434\u0435\u0442 \u043f\u0435\u0440\u0435\u0437\u0430\u0433\u0440\u0443\u0436\u0435\u043d \u0447\u0435\u0440\u0435\u0437 &6" + time + "&c!"));
    }

    private static void doRestart() {
        VNPlugin.instance().getLogger().info("Restarting server...");
        Bukkit.setWhitelist((boolean)true);
        VimeNetwork.lobby().shutdown();
        Bukkit.getPluginManager().callEvent((Event)new ServerRestartEvent(ServerRestartEvent.State.RESTART, forced));
        if (VimeNetwork.lobby().getServerType() == ServerType.LOBBY) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.kickPlayer(ChatColor.GREEN + "\u041f\u0435\u0440\u0435\u0437\u0430\u0433\u0440\u0443\u0437\u043a\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u0430");
            }
        } else if (forced) {
            VimeNetwork.toLobby(Bukkit.getOnlinePlayers());
        } else {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.kickPlayer(ChatColor.GREEN + "\u041f\u0435\u0440\u0435\u0437\u0430\u0433\u0440\u0443\u0437\u043a\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u0430");
            }
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)VNPlugin.instance(), Bukkit::shutdown, 5L);
    }
}

