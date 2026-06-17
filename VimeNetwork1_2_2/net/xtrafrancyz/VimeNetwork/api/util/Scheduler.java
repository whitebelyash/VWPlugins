/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.MinecraftServer
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.api.util;

import net.minecraft.server.v1_6_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Scheduler {
    public static int schedule(Plugin plugin, Runnable r) {
        return Scheduler.schedule(plugin, r, 0);
    }

    public static int schedule(Plugin plugin, Runnable r, int delay) {
        return Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, r, (long)delay);
    }

    public static int scheduleRepeating(Plugin plugin, Runnable r, int delay, int period) {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, r, (long)delay, (long)period);
    }

    public static int scheduleRepeatingTimes(Plugin plugin, Runnable r, int delay, int period, int times) {
        TimesRepeatingTask task = new TimesRepeatingTask(r, times);
        task.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, (Runnable)task, (long)delay, (long)period);
        return task.taskId;
    }

    public static int scheduleRepeatingUntil(Plugin plugin, Runnable r, int delay, int period, int length) {
        UntilRepeatingTask task = new UntilRepeatingTask(r, length);
        task.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, (Runnable)task, (long)delay, (long)period);
        return task.taskId;
    }

    public static void cancel(int task) {
        Bukkit.getScheduler().cancelTask(task);
    }

    private static class TimesRepeatingTask
    implements Runnable {
        Runnable r;
        int bound;
        int executed = 0;
        int taskId = -1;

        public TimesRepeatingTask(Runnable r, int bound) {
            this.r = r;
            this.bound = bound;
        }

        @Override
        public void run() {
            if (++this.executed >= this.bound) {
                Scheduler.cancel(this.taskId);
            }
            this.r.run();
        }
    }

    private static class UntilRepeatingTask
    implements Runnable {
        Runnable r;
        int bound;
        int taskId = -1;
        int startTicks;

        public UntilRepeatingTask(Runnable r, int bound) {
            this.r = r;
            this.bound = bound;
            this.startTicks = MinecraftServer.currentTick;
        }

        @Override
        public void run() {
            if (MinecraftServer.currentTick > this.startTicks + this.bound) {
                Scheduler.cancel(this.taskId);
                return;
            }
            this.r.run();
        }
    }
}

