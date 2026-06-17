/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.Entity
 *  net.minecraft.server.v1_6_R3.EntityFireworks
 *  net.minecraft.server.v1_6_R3.WorldServer
 *  org.bukkit.Color
 *  org.bukkit.FireworkEffect
 *  org.bukkit.Location
 *  org.bukkit.craftbukkit.v1_6_R3.CraftWorld
 *  org.bukkit.craftbukkit.v1_6_R3.entity.CraftFirework
 *  org.bukkit.entity.Firework
 *  org.bukkit.inventory.meta.FireworkMeta
 */
package net.xtrafrancyz.VimeNetwork.api.util;

import net.minecraft.server.v1_6_R3.Entity;
import net.minecraft.server.v1_6_R3.EntityFireworks;
import net.minecraft.server.v1_6_R3.WorldServer;
import net.xtrafrancyz.VimeNetwork.api.util.Rand;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftFirework;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class Fireworks {
    private static final FireworkEffect[] FIREWORK_EFFECTS = new FireworkEffect[]{FireworkEffect.builder().withColor(new Color[]{Color.RED, Color.PURPLE, Color.MAROON}).build(), FireworkEffect.builder().withColor(new Color[]{Color.BLUE, Color.AQUA, Color.NAVY, Color.TEAL}).build(), FireworkEffect.builder().withColor(new Color[]{Color.FUCHSIA, Color.AQUA, Color.ORANGE}).build(), FireworkEffect.builder().withColor(new Color[]{Color.FUCHSIA, Color.WHITE}).withTrail().build(), FireworkEffect.builder().withColor(new Color[]{Color.GRAY, Color.SILVER, Color.GREEN}).build(), FireworkEffect.builder().withColor(new Color[]{Color.GREEN, Color.LIME}).build(), FireworkEffect.builder().withColor(new Color[]{Color.RED, Color.YELLOW}).build(), FireworkEffect.builder().withColor(new Color[]{Color.GREEN, Color.GRAY, Color.FUCHSIA}).build(), FireworkEffect.builder().withColor(new Color[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.AQUA}).build(), FireworkEffect.builder().withColor(new Color[]{Color.PURPLE, Color.GREEN, Color.YELLOW}).build(), FireworkEffect.builder().withColor(new Color[]{Color.BLUE, Color.MAROON, Color.WHITE}).withTrail().build(), FireworkEffect.builder().withColor(new Color[]{Color.BLUE, Color.YELLOW}).withTrail().build(), FireworkEffect.builder().withColor(new Color[]{Color.FUCHSIA, Color.NAVY, Color.RED}).withTrail().build(), FireworkEffect.builder().withColor(new Color[]{Color.LIME, Color.ORANGE, Color.TEAL}).withTrail().build(), FireworkEffect.builder().withColor(new Color[]{Color.GRAY, Color.MAROON, Color.NAVY}).withTrail().build(), FireworkEffect.builder().withColor(new Color[]{Color.AQUA, Color.RED, Color.FUCHSIA}).withTrail().build()};

    private Fireworks() {
    }

    public static void play(Location loc, FireworkEffect fe) {
        Firework fw = (Firework)loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta data = fw.getFireworkMeta();
        data.clearEffects();
        data.setPower(1);
        data.addEffect(fe);
        fw.setFireworkMeta(data);
        WorldServer nms_world = ((CraftWorld)loc.getWorld()).getHandle();
        EntityFireworks nms_firework = ((CraftFirework)fw).getHandle();
        nms_world.broadcastEntityEffect((Entity)nms_firework, (byte)17);
        fw.remove();
    }

    public static void playRandom(Location loc) {
        Fireworks.play(loc, Fireworks.getRandomEffect());
    }

    public static void launch(Location loc, FireworkEffect fe) {
        Firework fw = (Firework)loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta data = fw.getFireworkMeta();
        data.clearEffects();
        data.setPower(1);
        data.addEffect(fe);
        fw.setFireworkMeta(data);
    }

    public static void launchRandom(Location loc) {
        Fireworks.launch(loc, Fireworks.getRandomEffect());
    }

    public static FireworkEffect getRandomEffect() {
        return Rand.of(FIREWORK_EFFECTS);
    }
}

