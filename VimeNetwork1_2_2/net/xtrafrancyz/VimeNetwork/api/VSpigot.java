/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.EntityHuman
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.v1_6_R3.CraftWorld
 */
package net.xtrafrancyz.VimeNetwork.api;

import java.util.logging.Level;
import net.minecraft.server.v1_6_R3.EntityHuman;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;

public class VSpigot {
    private VSpigot() {
    }

    public static void setWorldLightUpdates(World world, boolean lightUpdates) {
        try {
            ((CraftWorld)world).getHandle()._lightUpdates = lightUpdates;
        }
        catch (Exception ex) {
            VNPlugin.instance().getLogger().log(Level.WARNING, null, ex);
        }
    }

    public static void setWorldUselessPhysics(World world, boolean uselessPhysics) {
        try {
            ((CraftWorld)world).getHandle()._uselessPhysics = uselessPhysics;
        }
        catch (Exception ex) {
            VNPlugin.instance().getLogger().log(Level.WARNING, null, ex);
        }
    }

    public static void setFlyFallDamage(boolean value) {
        try {
            EntityHuman.FLY_FALL_DAMAGE = value;
        }
        catch (Exception ex) {
            VNPlugin.instance().getLogger().log(Level.WARNING, null, ex);
        }
    }

    public static void setChestCatDetection(World world, boolean value) {
        try {
            ((CraftWorld)world).getHandle()._chestCatDetection = value;
        }
        catch (Exception ex) {
            VNPlugin.instance().getLogger().log(Level.WARNING, null, ex);
        }
    }

    public static void setDeathFromHunger(World world, boolean value) {
        try {
            ((CraftWorld)world).getHandle()._deathFromHunger = value;
        }
        catch (Exception ex) {
            VNPlugin.instance().getLogger().log(Level.WARNING, null, ex);
        }
    }
}

