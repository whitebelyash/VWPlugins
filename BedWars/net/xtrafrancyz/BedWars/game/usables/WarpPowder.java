package net.xtrafrancyz.BedWars.game.usables;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.v1_6_R3.MathHelper;
import net.xtrafrancyz.BedWars.BedWars;
import net.xtrafrancyz.BedWars.PlayerInfo;
import net.xtrafrancyz.BedWars.game.BTexteria;
import net.xtrafrancyz.BedWars.game.GameState;
import net.xtrafrancyz.BedWars.util.CommonUtils;
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

public class WarpPowder implements Listener {
   public static final ItemStack ITEM;
   private static final int DURATION = 6000;
   private static final int PARTICLES_FREQUENCY = 150;
   private final BedWars plugin;
   private final Map warpers = new HashMap();

   public WarpPowder(BedWars plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onInteract(PlayerInteractEvent event) {
      if (E.isRightClick(event) && event.hasItem() && event.getItem().getType() == ITEM.getType()) {
         if (event.getPlayer().getLocation().add((double)0.0F, (double)-1.0F, (double)0.0F).getBlock().getType() == Material.AIR) {
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
      if (this.plugin.game.getState() == GameState.GAME) {
         if (!CommonUtils.isSameBlock(event.getFrom(), event.getTo())) {
            WarpInfo info = (WarpInfo)this.warpers.get(event.getPlayer());
            if (info != null) {
               info.cancel(false);
            }

         }
      }
   }

   @EventHandler
   public void onPlayerLeave(PlayerLeaveEvent event) {
      WarpInfo info = (WarpInfo)this.warpers.get(event.getPlayer());
      if (info != null) {
         info.cancel(true);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onDamage(EntityDamageEvent event) {
      if (this.plugin.game.getState() == GameState.GAME && event.getEntityType() == EntityType.PLAYER) {
         WarpInfo info = (WarpInfo)this.warpers.get((Player)event.getEntity());
         if (info != null) {
            info.cancel(false);
         }

      }
   }

   static {
      ITEM = Items.name(Material.SULPHUR, "&bТелепорт домой (&e6 сек.&b)", new String[]{"&7Нажмите правой кнопкой и через", "&f6 секунд&7 Вы телепортируетесь домой.", "&cВнимание: &fПеремещение отменяет телепортацию"});
   }

   private class WarpInfo {
      Player who;
      int task = -1;
      int particlesTask = -1;

      public WarpInfo(Player who) {
         this.who = who;
      }

      public void start() {
         this.task = Bukkit.getScheduler().scheduleSyncDelayedTask(WarpPowder.this.plugin, WarpPowder.this.new WarpTask(this), 120L);
         this.particlesTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(WarpPowder.this.plugin, WarpPowder.this.new WarpParticlesTask(this), 0L, 3L);
         BTexteria.showCustomTimer(this.who, "Телепортация... &e{S}.{mm} с.", BTexteria.DEFAULT_BAR_COLOR, 6000L, true);
         BTexteria.showCustomMessage(this.who, "Не двигайтесь 6 секунд", -1, 3000L);
      }

      public void cancel(boolean silent) {
         Bukkit.getScheduler().cancelTask(this.task);
         Bukkit.getScheduler().cancelTask(this.particlesTask);
         WarpPowder.this.warpers.remove(this.who);
         if (!silent) {
            BTexteria.removeCustomTimer(this.who);
            BTexteria.showCustomMessage(this.who, "Телепортация отменена", -44205, 3000L);
            this.who.getInventory().addItem(new ItemStack[]{WarpPowder.ITEM.clone()});
         }

      }
   }

   private class WarpTask implements Runnable {
      private final WarpInfo info;

      public WarpTask(WarpInfo info) {
         this.info = info;
      }

      public void run() {
         Bukkit.getScheduler().cancelTask(this.info.particlesTask);
         BTexteria.removeCustomTimer(this.info.who);
         WarpPowder.this.warpers.remove(this.info.who);
         BTexteria.showCustomMessage(this.info.who, "Дом, милый дом", -9830551, 2000L);
         this.info.who.teleport(PlayerInfo.get(this.info.who).team.getSpawnLocation());
      }
   }

   private class WarpParticlesTask implements Runnable {
      private static final int CIRCLE_ELEMENTS = 20;
      private static final float Y_CONST = 0.05F;
      private static final float ALPHA_CONST = ((float)Math.PI / 10F);
      private static final float PI_DIV_2 = ((float)Math.PI / 2F);
      private final WarpInfo info;
      private int ticks = 0;

      public WarpParticlesTask(WarpInfo info) {
         this.info = info;
      }

      public void run() {
         Vec3f loc = new Vec3f(this.info.who.getLocation());
         float y = 0.05F * (float)this.ticks;
         float alpha = ((float)Math.PI / 10F) * (float)this.ticks;
         loc.add(0.0F, y, 0.0F);

         for(int i = 0; i < 4; ++i) {
            float a = alpha + ((float)Math.PI / 2F) * (float)i;
            float x = MathHelper.sin(a);
            float z = MathHelper.cos(a);
            Particles.FIREWORKS_SPARK.play(loc.x + x, loc.y, loc.z + z, 0.0F, 0.0F, 0.0F, 0.0F, 1, new Player[0]);
         }

         ++this.ticks;
      }
   }
}
