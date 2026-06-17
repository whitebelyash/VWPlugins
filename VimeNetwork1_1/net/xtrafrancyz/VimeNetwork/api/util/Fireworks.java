package net.xtrafrancyz.VimeNetwork.api.util;

import net.minecraft.server.v1_6_R3.EntityFireworks;
import net.minecraft.server.v1_6_R3.WorldServer;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftFirework;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class Fireworks {
   private static final FireworkEffect[] FIREWORK_EFFECTS;

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
      nms_world.broadcastEntityEffect(nms_firework, (byte)17);
      fw.remove();
   }

   public static void playRandom(Location loc) {
      play(loc, getRandomEffect());
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
      launch(loc, getRandomEffect());
   }

   public static FireworkEffect getRandomEffect() {
      return (FireworkEffect)Rand.of((Object[])FIREWORK_EFFECTS);
   }

   static {
      FIREWORK_EFFECTS = new FireworkEffect[]{FireworkEffect.builder().withColor(new Color[]{Color.RED, Color.PURPLE, Color.MAROON}).build(), FireworkEffect.builder().withColor(new Color[]{Color.BLUE, Color.AQUA, Color.NAVY, Color.TEAL}).build(), FireworkEffect.builder().withColor(new Color[]{Color.FUCHSIA, Color.AQUA, Color.ORANGE}).build(), FireworkEffect.builder().withColor(new Color[]{Color.FUCHSIA, Color.WHITE}).withTrail().build(), FireworkEffect.builder().withColor(new Color[]{Color.GRAY, Color.SILVER, Color.GREEN}).build(), FireworkEffect.builder().withColor(new Color[]{Color.GREEN, Color.LIME}).build(), FireworkEffect.builder().withColor(new Color[]{Color.RED, Color.YELLOW}).build(), FireworkEffect.builder().withColor(new Color[]{Color.GREEN, Color.GRAY, Color.FUCHSIA}).build(), FireworkEffect.builder().withColor(new Color[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.AQUA}).build(), FireworkEffect.builder().withColor(new Color[]{Color.PURPLE, Color.GREEN, Color.YELLOW}).build(), FireworkEffect.builder().withColor(new Color[]{Color.BLUE, Color.MAROON, Color.WHITE}).withTrail().build(), FireworkEffect.builder().withColor(new Color[]{Color.BLUE, Color.YELLOW}).withTrail().build(), FireworkEffect.builder().withColor(new Color[]{Color.FUCHSIA, Color.NAVY, Color.RED}).withTrail().build(), FireworkEffect.builder().withColor(new Color[]{Color.LIME, Color.ORANGE, Color.TEAL}).withTrail().build(), FireworkEffect.builder().withColor(new Color[]{Color.GRAY, Color.MAROON, Color.NAVY}).withTrail().build(), FireworkEffect.builder().withColor(new Color[]{Color.AQUA, Color.RED, Color.FUCHSIA}).withTrail().build()};
   }
}
