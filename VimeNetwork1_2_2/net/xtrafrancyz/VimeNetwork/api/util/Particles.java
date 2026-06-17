/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.EntityPlayer
 *  net.minecraft.server.v1_6_R3.Packet
 *  net.minecraft.server.v1_6_R3.Packet63WorldParticles
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.Player
 *  org.bukkit.util.NumberConversions
 */
package net.xtrafrancyz.VimeNetwork.api.util;

import java.util.List;
import net.minecraft.server.v1_6_R3.EntityPlayer;
import net.minecraft.server.v1_6_R3.Packet;
import net.minecraft.server.v1_6_R3.Packet63WorldParticles;
import net.xtrafrancyz.VimeNetwork.api.entity.NMSEntityUtils;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

public enum Particles {
    ANGRY_VILLAGER("angryVillager"),
    BUBBLE("bubble"),
    CLOUD("cloud"),
    CRIT("crit"),
    DEPTH_SUSPEND("depthsuspend"),
    DRIP_LAVA("dripLava"),
    DRIP_WATER("dripWater"),
    ENCHANTMENT_TABLE("enchantmenttable"),
    EXPLODE("explode"),
    FIREWORKS_SPARK("fireworksSpark"),
    FLAME("flame"),
    FOOTSTEP("footstep"),
    HAPPY_VILLAGER("happyVillager"),
    HEART("heart"),
    HUGE_EXPLOSION("hugeexplosion"),
    INSTANT_SPELL("instantSpell"),
    LARGE_EXPLODE("largeexplode"),
    LARGE_SMOKE("largesmoke"),
    LAVA("lava"),
    MAGIC_CRIT("magicCrit"),
    MOB_SPELL_AMBIENT("mobSpellAmbient"),
    MOB_SPELL("mobSpell"),
    NOTE("note"),
    PORTAL("portal"),
    RED_DUST("reddust"),
    SLIME("slime"),
    SMOKE("smoke"),
    SNOW_SHOVEL("snowshovel"),
    SNOWBALL_POOF("snowballpoof"),
    SPELL("spell"),
    SPLASH("splash"),
    SUSPENDED("suspended"),
    WITCH_MAGIC("witchMagic"),
    TOWNAURA("townaura");

    public static final int DEFAULT_RADIUS = 20;
    private final String id;

    private Particles(String id) {
        this.id = id;
    }

    public void play(World world, float x, float y, float z, float xOffset, float yOffset, float zOffset, float effectSpeed, int amountOfParticles, Player ... players) {
        Particles.play(world, this.id, x, y, z, xOffset, yOffset, zOffset, effectSpeed, amountOfParticles, players);
    }

    public void play(Location loc, float xOffset, float yOffset, float zOffset, float effectSpeed, int amountOfParticles, Player ... players) {
        Particles.play(loc.getWorld(), this.id, (float)loc.getX(), (float)loc.getY(), (float)loc.getZ(), xOffset, yOffset, zOffset, effectSpeed, amountOfParticles, players);
    }

    public static void playIconCrack(World world, int id, float x, float y, float z, float xOffset, float yOffset, float zOffset, float effectSpeed, int amountOfParticles, Player ... players) {
        Particles.play(world, "iconcrack_" + id, x, y, z, xOffset, yOffset, zOffset, effectSpeed, amountOfParticles, players);
    }

    public static void playTileCrack(World world, int id, int meta, float x, float y, float z, float xOffset, float yOffset, float zOffset, float effectSpeed, int amountOfParticles, Player ... players) {
        Particles.play(world, "tilecrack_" + id + "_" + meta, x, y, z, xOffset, yOffset, zOffset, effectSpeed, amountOfParticles, players);
    }

    public static void play(World world, String particle, float x, float y, float z, float xOffset, float yOffset, float zOffset, float effectSpeed, int amountOfParticles, Player ... players) {
        Packet63WorldParticles packet = new Packet63WorldParticles(particle, x, y, z, xOffset, yOffset, zOffset, effectSpeed, amountOfParticles);
        if (players.length == 0) {
            int radius = 400;
            List list = NMSEntityUtils.getNMSWorld((World)world).players;
            for (EntityPlayer player : list) {
                double distanceSquared = NumberConversions.square((double)(player.locX - (double)x)) + NumberConversions.square((double)(player.locY - (double)y)) + NumberConversions.square((double)(player.locZ - (double)z));
                if (!(distanceSquared < (double)radius)) continue;
                player.playerConnection.sendPacket((Packet)packet);
            }
        } else {
            for (Player player : players) {
                U.sendPacket(player, (Packet)packet);
            }
        }
    }
}

