package net.xtrafrancyz.BedWars.game.usables;

import java.util.HashMap;
import java.util.Map;
import net.xtrafrancyz.BedWars.BWTeam;
import net.xtrafrancyz.BedWars.BedWars;
import net.xtrafrancyz.BedWars.PlayerInfo;
import net.xtrafrancyz.BedWars.game.BTexteria;
import net.xtrafrancyz.BedWars.game.GameState;
import net.xtrafrancyz.BedWars.util.CommonUtils;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3i;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Trap implements Listener {
   public static final ItemStack ITEM;
   private static final int DURATION = 10000;
   private final BedWars plugin;
   public final Map traps = new HashMap();

   public Trap(BedWars plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onPlayerMove(PlayerMoveEvent event) {
      if (this.plugin.game.getState() == GameState.GAME) {
         if (!CommonUtils.isSameBlock(event.getFrom(), event.getTo())) {
            if (!this.plugin.spectators.contains(event.getPlayer())) {
               Block block = event.getTo().getBlock();
               if (block.getType() == Material.TRIPWIRE) {
                  Vec3i loc = new Vec3i(event.getTo());
                  BWTeam team = (BWTeam)this.traps.get(loc);
                  if (team != null) {
                     PlayerInfo noob = PlayerInfo.get(event.getPlayer());
                     if (team.equals(noob.team)) {
                        return;
                     }

                     this.traps.remove(loc);
                     noob.player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 2), true);
                     noob.player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 2), true);
                     noob.player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1), true);
                     int task = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> noob.player.playSound(noob.player.getLocation(), Sound.FUSE, 2.0F, 1.0F), 0L, 20L);
                     Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> Bukkit.getScheduler().cancelTask(task), 200L);
                     block.setType(Material.AIR);

                     for(PlayerInfo owner : team.players) {
                        U.msg(owner.player, new String[]{"&aИгрок " + noob.team.chatColor + noob.username + "&a попался в вашу ловушку"});
                     }

                     BTexteria.showCustomMessage(noob.player, "Вы попались в " + team.chatColor + team.names[1] + " &rловушку", -1, 3000L);
                     BTexteria.showCustomTimer(noob.player, "Действие ловушки &e{S}.{mm} с.", -44205, 10000L, false);
                  }
               }

            }
         }
      }
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.HIGH
   )
   public void onBlockPlace(BlockPlaceEvent event) {
      if (this.plugin.game.getState() == GameState.GAME) {
         if (event.getBlock().getType() == Material.TRIPWIRE) {
            PlayerInfo player = PlayerInfo.get(event.getPlayer());
            if (player.team != null) {
               this.traps.put(new Vec3i(event.getBlock()), player.team);
               U.msg(player.player, new String[]{"&eЛовушка установлена"});
            }
         }

      }
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.HIGH
   )
   public void onBlockBreak(BlockBreakEvent event) {
      boolean upper = false;
      Block toDestroy = event.getBlock();
      if (toDestroy.getType() != Material.TRIPWIRE) {
         toDestroy = toDestroy.getRelative(BlockFace.UP);
         upper = true;
      }

      if (toDestroy.getType() == Material.TRIPWIRE) {
         BWTeam placer = (BWTeam)this.traps.remove(new Vec3i(toDestroy));
         if (placer != null) {
            PlayerInfo breaker = PlayerInfo.get(event.getPlayer());
            if (placer.equals(breaker.team)) {
               U.msg(breaker.player, new String[]{"&cВы сломали свою ловушку"});
               toDestroy.getWorld().dropItemNaturally(toDestroy.getLocation(), ITEM.clone());
            } else {
               U.msg(breaker.player, new String[]{"&aВы сломали вражескую ловушку"});
            }

            toDestroy.setType(Material.AIR);
            if (!upper) {
               event.setCancelled(true);
            }
         }

      }
   }

   static {
      ITEM = Items.name(Material.STRING, "&bЛовушка", new String[]{"&7 Информирует вас о том, когда", "&7противник наступает на ловушку.", "&7 Также он получает отрицательные", "&7эффекты на 10 сек."});
   }
}
