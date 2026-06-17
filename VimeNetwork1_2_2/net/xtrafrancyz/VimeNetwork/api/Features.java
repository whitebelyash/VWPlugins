/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.EntityHuman
 *  net.minecraft.server.v1_6_R3.IPlayerFileData
 *  net.minecraft.server.v1_6_R3.MinecraftServer
 *  net.minecraft.server.v1_6_R3.NBTTagCompound
 *  net.minecraft.server.v1_6_R3.PlayerList
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.World
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.api;

import net.minecraft.server.v1_6_R3.EntityHuman;
import net.minecraft.server.v1_6_R3.IPlayerFileData;
import net.minecraft.server.v1_6_R3.MinecraftServer;
import net.minecraft.server.v1_6_R3.NBTTagCompound;
import net.minecraft.server.v1_6_R3.PlayerList;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Features {
    static final Features inst = new Features();
    public final Feature POTION_EFFECT_EVENTS = new Feature("Potion Effect Events", false);
    public final Feature CANCEL_DROP_ITEM_FIX = new Feature("Cancel Drop Item Fix", true);
    public final AlwaysDayFeature ALWAYS_DAY = new AlwaysDayFeature();
    public final Feature ALWAYS_SUN = new AlwaysSunFeature();
    public final Feature DISABLE_FOOD = new Feature("Disable Food", false);
    public final Feature CHANGE_TAGS = new Feature("Change Visible Names", false);
    public final Feature ADD_GUILD_TAGS = new Feature("Add Guild Tags", false);
    public final Feature CHANGE_PLAYER_LIST_NAMES = new Feature("Change Player List Names", false);
    public final Feature CHANGE_CHAT = new Feature("Change Chat", false);
    public final Feature JOIN_LEAVE_MESSAGES = new Feature("Login & Leave Messages", false);
    public final AntiLeaveFeature ANTI_LEAVE = new AntiLeaveFeature();
    public final AutoWindowTitleFeature AUTO_WINDOW_TITLE = new AutoWindowTitleFeature();
    public final SavePlayerDataFeature SAVE_PLAYER_DATA = new SavePlayerDataFeature();
    public final Feature PER_WORLD_TABLIST = new Feature("Per World Tablist", false);
    public final Feature AUTO_RESTART = new Feature("Auto Restart", true);

    private Features() {
    }

    public static class SavePlayerDataFeature
    extends Feature {
        private IPlayerFileData original;

        SavePlayerDataFeature() {
            super("Save Player Data", true);
        }

        @Override
        public void setEnabled(boolean enabled) {
            if (this.isEnabled() == enabled) {
                return;
            }
            super.setEnabled(enabled);
            PlayerList list = MinecraftServer.getServer().getPlayerList();
            if (enabled) {
                list.playerFileData = this.original;
            } else {
                this.original = list.playerFileData;
                list.playerFileData = new IPlayerFileData(){

                    public void save(EntityHuman human) {
                    }

                    public NBTTagCompound load(EntityHuman human) {
                        return null;
                    }

                    public String[] getSeenPlayers() {
                        return new String[0];
                    }
                };
            }
        }
    }

    public static class AntiLeaveFeature
    extends Feature {
        private int damageDelayMillis = 10000;

        AntiLeaveFeature() {
            super("AntiLeave", false);
        }

        public void setDamageDelay(int delay) {
            this.damageDelayMillis = delay;
        }

        public int getDamageDelay() {
            return this.damageDelayMillis;
        }
    }

    public static class AutoWindowTitleFeature
    extends Feature {
        private String title = ChatColor.stripColor((String)Bukkit.getMotd());

        AutoWindowTitleFeature() {
            super("Auto Window Title", true);
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            if (enabled) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    VimeNetwork.setWindowTitle(player, this.title);
                }
            }
        }

        public void setTitle(String title) {
            if (!title.equals(this.title)) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    VimeNetwork.setWindowTitle(player, title);
                }
            }
            this.title = title;
        }

        public String getTitle() {
            return this.title;
        }
    }

    static class AlwaysSunFeature
    extends Feature {
        AlwaysSunFeature() {
            super("Always Sun", false);
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            if (enabled) {
                for (World world : Bukkit.getWorlds()) {
                    world.setStorm(false);
                }
            }
        }
    }

    public static class AlwaysDayFeature
    extends Feature {
        private long time = 8000L;

        AlwaysDayFeature() {
            super("Always Day", false);
        }

        public void setTime(long time) {
            long old = this.time;
            this.time = time;
            if (time != old) {
                this.update();
            }
        }

        public long getTime() {
            return this.time;
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            this.update();
        }

        private void update() {
            for (World world : Bukkit.getWorlds()) {
                if (this.isEnabled()) {
                    world.setGameRuleValue("doDaylightCycle", "false");
                    world.setFullTime(this.time);
                    continue;
                }
                world.setGameRuleValue("doDaylightCycle", "true");
            }
        }
    }

    public static class Feature {
        private final String name;
        private boolean enabled;

        Feature(String name, boolean enabled) {
            this.name = name;
            this.enabled = enabled;
        }

        public void setEnabled(boolean enabled) {
            if (this.enabled != enabled) {
                this.enabled = enabled;
                VNPlugin.instance().getLogger().info("Feature [" + this.name + "] is " + (enabled ? "enabled" : "disabled"));
            }
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public String getName() {
            return this.name;
        }
    }
}

