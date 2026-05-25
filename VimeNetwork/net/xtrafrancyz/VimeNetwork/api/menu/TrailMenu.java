package net.xtrafrancyz.VimeNetwork.api.menu;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.v1_6_R3.MathHelper;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLeaveEvent;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLoadedEvent;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.util.Invs;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.Particles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

public class TrailMenu implements IMenu {
   private static boolean ignoreInvisibility = false;
   private static boolean enabled = false;
   private static String PREFIX = null;
   private static String TABLE = null;
   private static Map PLAYERS;
   private final Inventory inv = Bukkit.getServer().createInventory(this, 18, "Выбор следа");

   public TrailMenu(Player player) {
      NetworkPlayer networkPlayer = VimeNetwork.getPlayer(player.getName());
      TrailPlayer trailPlayer = (TrailPlayer)PLAYERS.get(player.getName());

      for(Trail trail : TrailMenu.Trail.values()) {
         this.inv.setItem(trail.slot, trail.withPrice(trailPlayer, networkPlayer));
      }

   }

   public void onClick(ItemStack is, Player bukkitPlayer, int slot, ClickType clickType) {
      Trail finded = null;

      for(Trail t : TrailMenu.Trail.values()) {
         if (t.slot == slot) {
            finded = t;
            break;
         }
      }

      if (finded != null) {
         TrailPlayer player = (TrailPlayer)PLAYERS.get(bukkitPlayer.getName());
         NetworkPlayer networkPlayer = VimeNetwork.getPlayer(bukkitPlayer.getName());
         if (player.active != finded) {
            if (networkPlayer.getRank().has(finded.rank)) {
               if (finded.price != -1 && networkPlayer.getRank() != Rank.IMMORTAL && !player.owned.contains(finded)) {
                  if (networkPlayer.getCoins() >= finded.price) {
                     ConfirmMenu menu = new ConfirmMenu(this.inv, () -> {
                        networkPlayer.takeCoins(finded.price);
                        VimeNetwork.mysql().query("INSERT INTO `" + TABLE + "` (`userid`, `trail`) VALUES (" + networkPlayer.getId() + ", '" + finded.name() + "')");
                        player.owned.add(finded);

                        for(Trail t : TrailMenu.Trail.values()) {
                           if (t == player.active) {
                              player.active = finded;
                              this.inv.setItem(t.slot, t.withPrice(player, networkPlayer));
                              break;
                           }
                        }

                        player.active = finded;
                        this.inv.setItem(finded.slot, finded.withPrice(player, networkPlayer));
                        VimeNetwork.metrics().add(PREFIX + ".buy.trail", finded.price);
                     }, finded.displayName);
                     menu.setConfirmText("&aКупить след", "&fЦена: &e" + finded.price);
                     Invs.forceOpen(bukkitPlayer, (InventoryHolder)menu);
                  } else {
                     VimeNetwork.texteria().showInsufficientlyCoins(bukkitPlayer);
                  }
               } else {
                  player.active = finded;
                  bukkitPlayer.closeInventory();
               }
            }
         } else {
            player.active = null;
            bukkitPlayer.closeInventory();
         }

      }
   }

   public Inventory getInventory() {
      return this.inv;
   }

   public static void init(JavaPlugin plugin, String prefix) {
      PLAYERS = new ConcurrentHashMap();
      PREFIX = prefix;
      TABLE = prefix + "_trails";
      enabled = true;
      Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new TrailTask(), 20L, 1L);
      Bukkit.getPluginManager().registerEvents(new TrailListener(plugin), plugin);
      VimeNetwork.mysql().query("CREATE TABLE IF NOT EXISTS `" + TABLE + "` (\n  `userid` int(11) NOT NULL,\n  `trail` varchar(20) NOT NULL,\n  PRIMARY KEY (`userid`,`trail`)\n) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;");
   }

   public static boolean isEnabled() {
      return enabled;
   }

   public static TrailPlayer getPlayer(String player) {
      return (TrailPlayer)PLAYERS.get(player);
   }

   public static void setIgnoreInvisibility(boolean flag) {
      ignoreInvisibility = flag;
   }

   public static enum Trail {
      FLAME("Огонь", 0, new ItemStack(Material.BLAZE_POWDER), 15000),
      ENDER("Ендер", 1, new ItemStack(Material.ENDER_PEARL), 15000),
      HEARTS("Сердечки", 2, new ItemStack(Material.INK_SACK, 1, (short)1), 15000),
      NOTE("Ноты", 3, new ItemStack(Material.NOTE_BLOCK), 15000),
      HAPPY("Криптонит", 4, new ItemStack(Material.SEEDS), 15000),
      MOB_SPELL("Магические пузыри", 5, new ItemStack(Material.SPECKLED_MELON), 15000),
      RED_DUST("Цветной редстоун", 6, new ItemStack(Material.REDSTONE), 15000),
      COLORFUL("Много цвета", 7, new ItemStack(Material.INK_SACK, 1, (short)13), 15000),
      CLOUD("Облако", 8, new ItemStack(Material.INK_SACK, 1, (short)15), 15000),
      ANGRY("Злой житель", 9, new ItemStack(Material.INK_SACK, 1, (short)14), Rank.VIP),
      SNOW("Куски снега", 10, new ItemStack(Material.SNOW_BALL), Rank.VIP),
      LAVA_DRIP("Капли лавы", 11, new ItemStack(Material.LAVA_BUCKET), Rank.PREMIUM),
      SUSP("Пустота", 12, new ItemStack(Material.COAL), Rank.PREMIUM),
      WATER_DRIP("Капли воды", 13, new ItemStack(Material.WATER_BUCKET), Rank.PREMIUM),
      FIREWORK("Фейерверк", 14, new ItemStack(Material.FIREWORK), Rank.HOLY),
      ENCHANT("Зачаровальный стол", 15, new ItemStack(Material.ENCHANTMENT_TABLE), Rank.HOLY),
      MAGIC("Магия", 16, new ItemStack(Material.INK_SACK, 1, (short)9), Rank.HOLY);

      private ItemStack is;
      final Rank rank;
      final String displayName;
      final int slot;
      final int price;

      private Trail(String displayName, int slot, ItemStack is, int price) {
         this(displayName, slot, is, price, Rank.PLAYER);
      }

      private Trail(String displayName, int slot, ItemStack is, Rank rank) {
         this(displayName, slot, is, -1, rank);
      }

      private Trail(String displayName, int slot, ItemStack is, int price, Rank rank) {
         this.displayName = displayName;
         this.slot = slot;
         this.is = Items.name(is, "&7След: &e" + displayName);
         this.price = price;
         this.rank = rank;
      }

      public ItemStack withPrice(TrailPlayer player, NetworkPlayer networkPlayer) {
         String status;
         if (player.active == this) {
            status = "&aАктивно";
         } else if (networkPlayer.getRank() != Rank.IMMORTAL && (!networkPlayer.getRank().has(this.rank) || this.price != -1 && !player.owned.contains(this))) {
            if (this.price != -1 && networkPlayer.getCoins() >= this.price) {
               status = "&6Купить: " + this.price;
            } else if (!networkPlayer.getRank().has(this.rank)) {
               status = "&fДоступно для " + this.rank.getDisplayName();
            } else {
               status = "&cЦена: " + this.price;
            }
         } else {
            status = "&eДоступно";
         }

         return Items.appendLore(this.is.clone(), "&7---------------", status);
      }
   }

   public static class TrailPlayer {
      public final Player bukkit;
      public final Set owned;
      public Trail active;
      public boolean visible;
      int ticks;
      boolean walking;
      int standing;
      Location lastLoc;

      private TrailPlayer(Player bukkit) {
         this.active = null;
         this.visible = true;
         this.ticks = 0;
         this.walking = false;
         this.standing = 0;
         this.bukkit = bukkit;
         this.owned = EnumSet.noneOf(Trail.class);
         this.ticks = 0;
      }

      public void setActive(String name) {
         if (name != null) {
            try {
               Trail type = TrailMenu.Trail.valueOf(name.toUpperCase());
               Rank rank = VimeNetwork.getPlayer(this.bukkit).getRank();
               if (this.owned.contains(type) || rank == Rank.IMMORTAL || rank.has(type.rank)) {
                  this.active = type;
               }
            } catch (Exception var4) {
            }

         }
      }

      public String getActiveMysqlString() {
         return this.active == null ? "NULL" : "'" + this.active.name() + "'";
      }
   }

   private static class TrailListener implements Listener {
      private final JavaPlugin plugin;

      public TrailListener(JavaPlugin plugin) {
         this.plugin = plugin;
      }

      @EventHandler(
         priority = EventPriority.LOW
      )
      public void onPlayerLoaded(PlayerLoadedEvent event) {
         TrailPlayer player = new TrailPlayer(event.getPlayer());
         TrailMenu.PLAYERS.put(event.getPlayer().getName(), player);
         VimeNetwork.mysql().select("SELECT trail FROM `" + TrailMenu.TABLE + "` WHERE `userid` = " + event.getNetworkPlayer().getId(), (rs) -> {
            while(rs.next()) {
               try {
                  player.owned.add(TrailMenu.Trail.valueOf(rs.getString("trail").toUpperCase()));
               } catch (Exception var3) {
               }
            }

         });
      }

      @EventHandler(
         priority = EventPriority.HIGHEST
      )
      public void onPlayerLeave(PlayerLeaveEvent event) {
         TrailMenu.PLAYERS.remove(event.getPlayer().getName());
      }

      @EventHandler
      public void onPluginDisable(PluginDisableEvent event) {
         if (event.getPlugin().equals(this.plugin)) {
            TrailMenu.enabled = false;
         }

      }
   }

   private static class TrailTask implements Runnable {
      private TrailTask() {
      }

      public void run() {
         boolean hideInvis = !TrailMenu.ignoreInvisibility;

         for(TrailPlayer player : TrailMenu.PLAYERS.values()) {
            if (player.active != null && player.visible && !player.bukkit.isDead() && (!hideInvis || !player.bukkit.hasPotionEffect(PotionEffectType.INVISIBILITY))) {
               ++player.ticks;
               Location loc = player.bukkit.getLocation();
               if (equalsLoc(loc, player.lastLoc)) {
                  ++player.standing;
                  if (player.standing >= 2) {
                     player.walking = false;
                  }
               } else {
                  player.lastLoc = loc;
                  player.standing = 0;
                  player.walking = true;
               }

               switch (player.active) {
                  case FLAME:
                     float a = (float)(-player.ticks) * 0.2F;
                     float x = MathHelper.sin(a) * 0.5F;
                     float z = MathHelper.cos(a) * 0.5F;
                     Particles.FLAME.play((float)loc.getX() + x, (float)loc.getY() + 2.4F, (float)loc.getZ() + z, 0.0F, 0.0F, 0.0F, 0.0F, 1);
                     break;
                  case ENDER:
                     if (player.ticks % 3 != 0) {
                        Particles.PORTAL.play((float)loc.getX(), (float)loc.getY() + 0.3F, (float)loc.getZ(), 0.2F, 0.2F, 0.2F, 0.0F, 11);
                     }
                     break;
                  case HEARTS:
                     if (player.ticks % 3 == 0) {
                        float a = (float)(-player.ticks) * 0.2F;
                        float x = MathHelper.sin(a) * 0.5F;
                        float z = MathHelper.cos(a) * 0.5F;
                        Particles.HEART.play((float)loc.getX() + x, (float)loc.getY() + 2.4F, (float)loc.getZ() + z, 0.0F, 0.0F, 0.0F, 0.0F, 1);
                     }
                     break;
                  case NOTE:
                     float a = (float)(-player.ticks) * 0.4F;
                     float x = MathHelper.sin(a) * 0.5F;
                     float y = MathHelper.cos(a * 2.0F) * 0.2F;
                     float z = MathHelper.cos(a) * 0.5F;
                     Particles.NOTE.play((float)loc.getX() + x, (float)loc.getY() + 2.4F + y, (float)loc.getZ() + z, 0.0F, 0.0F, 0.0F, 5.0F, 1);
                     break;
                  case HAPPY:
                     if (player.ticks % 3 == 0) {
                        Particles.HAPPY_VILLAGER.play((float)loc.getX(), (float)loc.getY() + 0.2F, (float)loc.getZ(), 0.2F, 0.2F, 0.2F, -1.0F, 3);
                     }
                     break;
                  case MOB_SPELL:
                     if (player.ticks % 3 == 0) {
                        Particles.MOB_SPELL.play((float)loc.getX(), (float)loc.getY() + 2.4F, (float)loc.getZ(), 0.2F, 0.2F, 0.2F, 5.0F, 3);
                     }
                     break;
                  case RED_DUST:
                     if (player.ticks % 2 == 0) {
                        Particles.RED_DUST.play((float)loc.getX(), (float)loc.getY() + 2.4F, (float)loc.getZ(), 0.15F, 0.15F, 0.15F, 5.0F, 3);
                     }
                     break;
                  case COLORFUL:
                     if (player.ticks % 3 == 0) {
                        Particles.MOB_SPELL.play((float)loc.getX(), (float)loc.getY() + 2.4F, (float)loc.getZ(), 0.2F, 0.2F, 0.2F, 5.0F, 2);
                        Particles.RED_DUST.play((float)loc.getX(), (float)loc.getY() + 2.4F, (float)loc.getZ(), 0.2F, 0.2F, 0.2F, 5.0F, 2);
                     }
                     break;
                  case CLOUD:
                     if (player.walking) {
                        Particles.CLOUD.play((float)loc.getX(), (float)loc.getY() + 0.1F, (float)loc.getZ(), 0.2F, 0.2F, 0.2F, 0.0F, 1);
                     } else if (player.ticks % 3 == 0) {
                        Particles.CLOUD.play((float)loc.getX(), (float)loc.getY() + 0.5F, (float)loc.getZ(), 0.25F, 0.15F, 0.25F, 0.0F, 2);
                     }
                     break;
                  case ANGRY:
                     if (player.ticks % 2 == 0) {
                        float a = (float)(-player.ticks) * 0.25F;
                        float x = MathHelper.sin(a) * 0.5F;
                        float z = MathHelper.cos(a) * 0.5F;
                        Particles.ANGRY_VILLAGER.play((float)loc.getX() + x, (float)loc.getY() + 2.3F, (float)loc.getZ() + z, 0.0F, 0.0F, 0.0F, 0.0F, 1);
                     }
                     break;
                  case SNOW:
                     if (player.ticks % 2 == 0) {
                        Particles.SNOWBALL_POOF.play((float)loc.getX(), (float)loc.getY() + 0.6F + MathHelper.sin((float)player.ticks * 0.25F) * 0.5F, (float)loc.getZ(), 0.2F, 0.0F, 0.2F, 0.0F, 5);
                     }
                     break;
                  case LAVA_DRIP:
                     if (player.walking) {
                        if (player.ticks % 3 == 0) {
                           Particles.DRIP_LAVA.play((float)loc.getX(), (float)loc.getY() + 0.6F, (float)loc.getZ(), 0.2F, 0.4F, 0.2F, 0.0F, 3);
                        }
                     } else if (player.ticks % 3 == 0) {
                        float a = (float)(-player.ticks) * 0.15F;
                        float x = MathHelper.sin(a) * 0.5F;
                        float z = MathHelper.cos(a) * 0.5F;
                        Particles.DRIP_LAVA.play((float)loc.getX() + x + 0.14F, (float)loc.getY() + 2.3F, (float)loc.getZ() + z + 0.08F, 0.0F, 0.0F, 0.0F, 0.0F, 1);
                        a += (float)Math.PI;
                        x = MathHelper.sin(a) * 0.5F;
                        z = MathHelper.cos(a) * 0.5F;
                        Particles.DRIP_LAVA.play((float)loc.getX() + x + 0.14F, (float)loc.getY() + 2.3F, (float)loc.getZ() + z + 0.08F, 0.0F, 0.0F, 0.0F, 0.0F, 1);
                     }
                     break;
                  case SUSP:
                     if (player.walking) {
                        Particles.DEPTH_SUSPEND.play((float)loc.getX() + 0.14F, (float)loc.getY() + 0.7F, (float)loc.getZ() + 0.08F, 0.2F, 0.5F, 0.2F, 0.0F, 10);
                     } else if (player.ticks % 2 == 0) {
                        Particles.DEPTH_SUSPEND.play((float)loc.getX() + 0.14F, (float)loc.getY() + 0.5F, (float)loc.getZ() + 0.08F, 0.2F, 0.4F, 0.2F, 0.0F, 8);
                     }
                     break;
                  case WATER_DRIP:
                     if (player.walking) {
                        if (player.ticks % 3 == 0) {
                           Particles.DRIP_WATER.play((float)loc.getX(), (float)loc.getY() + 0.6F, (float)loc.getZ(), 0.2F, 0.4F, 0.2F, 0.0F, 3);
                        }
                     } else if (player.ticks % 3 == 0) {
                        float a = (float)(-player.ticks) * 0.15F;
                        float x = MathHelper.sin(a) * 0.5F;
                        float z = MathHelper.cos(a) * 0.5F;
                        Particles.DRIP_WATER.play((float)loc.getX() + x + 0.14F, (float)loc.getY() + 2.3F, (float)loc.getZ() + z + 0.08F, 0.0F, 0.0F, 0.0F, 0.0F, 1);
                        a += (float)Math.PI;
                        x = MathHelper.sin(a) * 0.5F;
                        z = MathHelper.cos(a) * 0.5F;
                        Particles.DRIP_WATER.play((float)loc.getX() + x + 0.14F, (float)loc.getY() + 2.3F, (float)loc.getZ() + z + 0.08F, 0.0F, 0.0F, 0.0F, 0.0F, 1);
                     }
                     break;
                  case FIREWORK:
                     if (player.walking) {
                        if (player.ticks % 3 == 0) {
                           Particles.FIREWORKS_SPARK.play((float)loc.getX(), (float)loc.getY() + 0.6F, (float)loc.getZ(), 0.2F, 0.4F, 0.2F, 0.0F, 3);
                        }
                     } else if (player.ticks % 2 == 0) {
                        float a = (float)(-player.ticks) * 0.2F;
                        float x = MathHelper.sin(a) * 0.5F;
                        float z = MathHelper.cos(a) * 0.5F;
                        Particles.FIREWORKS_SPARK.play((float)loc.getX() + x, (float)loc.getY() + 0.8F, (float)loc.getZ() + z, 0.0F, 0.0F, 0.0F, 0.0F, 1);
                     }
                     break;
                  case ENCHANT:
                     if (player.walking) {
                        if (player.ticks % 3 == 0) {
                           Particles.ENCHANTMENT_TABLE.play((float)loc.getX(), (float)loc.getY() + 0.6F, (float)loc.getZ(), 0.2F, 0.4F, 0.2F, 0.1F, 10);
                        }
                     } else if (player.ticks % 3 == 0) {
                        Particles.ENCHANTMENT_TABLE.play((float)loc.getX(), (float)loc.getY() + 0.5F, (float)loc.getZ(), 0.2F, 0.3F, 0.2F, 0.6F, 10);
                     }
                     break;
                  case MAGIC:
                     if (player.walking) {
                        if (player.ticks % 3 == 0) {
                           Particles.WITCH_MAGIC.play((float)loc.getX(), (float)loc.getY() + 0.6F, (float)loc.getZ(), 0.2F, 0.4F, 0.2F, 0.0F, 4);
                        }
                     } else if (player.ticks % 2 == 0) {
                        float a = (float)(-player.ticks) * 0.2F;
                        float x = MathHelper.sin(a) * 0.5F;
                        float z = MathHelper.cos(a) * 0.5F;
                        Particles.WITCH_MAGIC.play((float)loc.getX() + x, (float)loc.getY() + 2.3F, (float)loc.getZ() + z, 0.05F, 0.0F, 0.05F, 0.0F, 2);
                        a += (float)Math.PI;
                        x = MathHelper.sin(a) * 0.5F;
                        z = MathHelper.cos(a) * 0.5F;
                        Particles.WITCH_MAGIC.play((float)loc.getX() + x, (float)loc.getY() + 2.3F, (float)loc.getZ() + z, 0.05F, 0.0F, 0.05F, 0.0F, 2);
                     }
               }
            }
         }

      }

      private static boolean equalsLoc(Location a, Location b) {
         if (a != null && b != null) {
            return a.getX() == b.getX() && a.getY() == b.getY() && a.getZ() == b.getZ();
         } else {
            return false;
         }
      }
   }
}
