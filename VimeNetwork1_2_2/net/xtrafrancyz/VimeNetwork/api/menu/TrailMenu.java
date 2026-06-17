/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.MathHelper
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.event.server.PluginDisableEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.potion.PotionEffectType
 */
package net.xtrafrancyz.VimeNetwork.api.menu;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.v1_6_R3.MathHelper;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLeaveEvent;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLoadedEvent;
import net.xtrafrancyz.VimeNetwork.api.menu.ConfirmMenu;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.util.Invs;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.Particles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

public class TrailMenu
implements IMenu {
    private static boolean ignoreInvisibility = false;
    private static boolean enabled = false;
    private static String PREFIX = null;
    private static String TABLE = null;
    private static Map<String, TrailPlayer> PLAYERS;
    private final Inventory inv = Bukkit.getServer().createInventory((InventoryHolder)this, 18, "\u0412\u044b\u0431\u043e\u0440 \u0441\u043b\u0435\u0434\u0430");

    public TrailMenu(Player player) {
        NetworkPlayer networkPlayer = VimeNetwork.getPlayer(player.getName());
        TrailPlayer trailPlayer = PLAYERS.get(player.getName());
        for (Trail trail : Trail.values()) {
            this.inv.setItem(trail.slot, trail.withPrice(trailPlayer, networkPlayer));
        }
    }

    @Override
    public void onClick(ItemStack is, Player bukkitPlayer, int slot, ClickType clickType) {
        Trail finded = null;
        for (Trail t : Trail.values()) {
            if (t.slot != slot) continue;
            finded = t;
            break;
        }
        if (finded == null) {
            return;
        }
        Trail trail = finded;
        TrailPlayer player = PLAYERS.get(bukkitPlayer.getName());
        NetworkPlayer networkPlayer = VimeNetwork.getPlayer(bukkitPlayer.getName());
        if (player.active != trail) {
            if (networkPlayer.getRank().has(trail.rank)) {
                if (trail.price == -1 || networkPlayer.getRank() == Rank.IMMORTAL || player.owned.contains((Object)trail)) {
                    player.active = trail;
                    bukkitPlayer.closeInventory();
                } else if (networkPlayer.getCoins() >= trail.price) {
                    ConfirmMenu menu = new ConfirmMenu(this.inv, () -> {
                        networkPlayer.takeCoins(trail.price);
                        VimeNetwork.mysql().query("INSERT INTO `" + TABLE + "` (`userid`, `trail`) VALUES (" + networkPlayer.getId() + ", '" + trail.name() + "')");
                        player.owned.add(trail);
                        for (Trail t : Trail.values()) {
                            if (t != player.active) continue;
                            player.active = trail;
                            this.inv.setItem(t.slot, t.withPrice(player, networkPlayer));
                            break;
                        }
                        player.active = trail;
                        this.inv.setItem(trail.slot, trail.withPrice(player, networkPlayer));
                        VimeNetwork.metrics().add(PREFIX + ".buy.trail", trail.price);
                    }, trail.displayName);
                    menu.setConfirmText("&a\u041a\u0443\u043f\u0438\u0442\u044c \u0441\u043b\u0435\u0434", "&f\u0426\u0435\u043d\u0430: &e" + trail.price);
                    Invs.forceOpen((HumanEntity)bukkitPlayer, menu);
                } else {
                    VimeNetwork.texteria().showInsufficientlyCoins(bukkitPlayer);
                }
            }
        } else {
            player.active = null;
            bukkitPlayer.closeInventory();
        }
    }

    public Inventory getInventory() {
        return this.inv;
    }

    public static void init(JavaPlugin plugin, String prefix) {
        PLAYERS = new ConcurrentHashMap<String, TrailPlayer>();
        PREFIX = prefix;
        TABLE = prefix + "_trails";
        enabled = true;
        Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)plugin, (Runnable)new TrailTask(), 20L, 1L);
        Bukkit.getPluginManager().registerEvents((Listener)new TrailListener(plugin), (Plugin)plugin);
        VimeNetwork.mysql().query("CREATE TABLE IF NOT EXISTS `" + TABLE + "` (\n  `userid` int(11) NOT NULL,\n  `trail` varchar(20) NOT NULL,\n  PRIMARY KEY (`userid`,`trail`)\n) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;");
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static TrailPlayer getPlayer(String player) {
        return PLAYERS.get(player);
    }

    public static void setIgnoreInvisibility(boolean flag) {
        ignoreInvisibility = flag;
    }

    private static class TrailTask
    implements Runnable {
        private TrailTask() {
        }

        @Override
        public void run() {
            boolean hideInvis = !ignoreInvisibility;
            for (TrailPlayer player : PLAYERS.values()) {
                if (player.active == null || !player.visible || player.bukkit.isDead() || hideInvis && player.bukkit.hasPotionEffect(PotionEffectType.INVISIBILITY)) continue;
                ++player.ticks;
                Location loc = player.bukkit.getLocation();
                if (TrailTask.equalsLoc(loc, player.lastLoc)) {
                    ++player.standing;
                    if (player.standing >= 2) {
                        player.walking = false;
                    }
                } else {
                    player.lastLoc = loc;
                    player.standing = 0;
                    player.walking = true;
                }
                World w = loc.getWorld();
                switch (player.active) {
                    case FLAME: {
                        float a = (float)(-player.ticks) * 0.2f;
                        float x = MathHelper.sin((float)a) * 0.5f;
                        float z = MathHelper.cos((float)a) * 0.5f;
                        Particles.FLAME.play(w, (float)loc.getX() + x, (float)loc.getY() + 2.4f, (float)loc.getZ() + z, 0.0f, 0.0f, 0.0f, 0.0f, 1, new Player[0]);
                        break;
                    }
                    case ENDER: {
                        if (player.ticks % 3 == 0) break;
                        Particles.PORTAL.play(w, (float)loc.getX(), (float)loc.getY() + 0.3f, (float)loc.getZ(), 0.2f, 0.2f, 0.2f, 0.0f, 11, new Player[0]);
                        break;
                    }
                    case HEARTS: {
                        if (player.ticks % 3 != 0) break;
                        float a = (float)(-player.ticks) * 0.2f;
                        float x = MathHelper.sin((float)a) * 0.5f;
                        float z = MathHelper.cos((float)a) * 0.5f;
                        Particles.HEART.play(w, (float)loc.getX() + x, (float)loc.getY() + 2.4f, (float)loc.getZ() + z, 0.0f, 0.0f, 0.0f, 0.0f, 1, new Player[0]);
                        break;
                    }
                    case NOTE: {
                        float a = (float)(-player.ticks) * 0.4f;
                        float x = MathHelper.sin((float)a) * 0.5f;
                        float y = MathHelper.cos((float)(a * 2.0f)) * 0.2f;
                        float z = MathHelper.cos((float)a) * 0.5f;
                        Particles.NOTE.play(w, (float)loc.getX() + x, (float)loc.getY() + 2.4f + y, (float)loc.getZ() + z, 0.0f, 0.0f, 0.0f, 5.0f, 1, new Player[0]);
                        break;
                    }
                    case HAPPY: {
                        if (player.ticks % 3 != 0) break;
                        Particles.HAPPY_VILLAGER.play(w, (float)loc.getX(), (float)loc.getY() + 0.2f, (float)loc.getZ(), 0.2f, 0.2f, 0.2f, -1.0f, 3, new Player[0]);
                        break;
                    }
                    case MOB_SPELL: {
                        if (player.ticks % 3 != 0) break;
                        Particles.MOB_SPELL.play(w, (float)loc.getX(), (float)loc.getY() + 2.4f, (float)loc.getZ(), 0.2f, 0.2f, 0.2f, 5.0f, 3, new Player[0]);
                        break;
                    }
                    case RED_DUST: {
                        if (player.ticks % 2 != 0) break;
                        Particles.RED_DUST.play(w, (float)loc.getX(), (float)loc.getY() + 2.4f, (float)loc.getZ(), 0.15f, 0.15f, 0.15f, 5.0f, 3, new Player[0]);
                        break;
                    }
                    case COLORFUL: {
                        if (player.ticks % 3 != 0) break;
                        Particles.MOB_SPELL.play(w, (float)loc.getX(), (float)loc.getY() + 2.4f, (float)loc.getZ(), 0.2f, 0.2f, 0.2f, 5.0f, 2, new Player[0]);
                        Particles.RED_DUST.play(w, (float)loc.getX(), (float)loc.getY() + 2.4f, (float)loc.getZ(), 0.2f, 0.2f, 0.2f, 5.0f, 2, new Player[0]);
                        break;
                    }
                    case CLOUD: {
                        if (player.walking) {
                            Particles.CLOUD.play(w, (float)loc.getX(), (float)loc.getY() + 0.1f, (float)loc.getZ(), 0.2f, 0.2f, 0.2f, 0.0f, 1, new Player[0]);
                            break;
                        }
                        if (player.ticks % 3 != 0) break;
                        Particles.CLOUD.play(w, (float)loc.getX(), (float)loc.getY() + 0.5f, (float)loc.getZ(), 0.25f, 0.15f, 0.25f, 0.0f, 2, new Player[0]);
                        break;
                    }
                    case ANGRY: {
                        if (player.ticks % 2 != 0) break;
                        float a = (float)(-player.ticks) * 0.25f;
                        float x = MathHelper.sin((float)a) * 0.5f;
                        float z = MathHelper.cos((float)a) * 0.5f;
                        Particles.ANGRY_VILLAGER.play(w, (float)loc.getX() + x, (float)loc.getY() + 2.3f, (float)loc.getZ() + z, 0.0f, 0.0f, 0.0f, 0.0f, 1, new Player[0]);
                        break;
                    }
                    case SNOW: {
                        if (player.ticks % 2 != 0) break;
                        Particles.SNOWBALL_POOF.play(w, (float)loc.getX(), (float)loc.getY() + 0.6f + MathHelper.sin((float)((float)player.ticks * 0.25f)) * 0.5f, (float)loc.getZ(), 0.2f, 0.0f, 0.2f, 0.0f, 5, new Player[0]);
                        break;
                    }
                    case LAVA_DRIP: {
                        if (player.walking) {
                            if (player.ticks % 3 != 0) break;
                            Particles.DRIP_LAVA.play(w, (float)loc.getX(), (float)loc.getY() + 0.6f, (float)loc.getZ(), 0.2f, 0.4f, 0.2f, 0.0f, 3, new Player[0]);
                            break;
                        }
                        if (player.ticks % 3 != 0) break;
                        float a = (float)(-player.ticks) * 0.15f;
                        float x = MathHelper.sin((float)a) * 0.5f;
                        float z = MathHelper.cos((float)a) * 0.5f;
                        Particles.DRIP_LAVA.play(w, (float)loc.getX() + x + 0.14f, (float)loc.getY() + 2.3f, (float)loc.getZ() + z + 0.08f, 0.0f, 0.0f, 0.0f, 0.0f, 1, new Player[0]);
                        x = MathHelper.sin((float)(a += (float)Math.PI)) * 0.5f;
                        z = MathHelper.cos((float)a) * 0.5f;
                        Particles.DRIP_LAVA.play(w, (float)loc.getX() + x + 0.14f, (float)loc.getY() + 2.3f, (float)loc.getZ() + z + 0.08f, 0.0f, 0.0f, 0.0f, 0.0f, 1, new Player[0]);
                        break;
                    }
                    case SUSP: {
                        if (player.walking) {
                            Particles.DEPTH_SUSPEND.play(w, (float)loc.getX() + 0.14f, (float)loc.getY() + 0.7f, (float)loc.getZ() + 0.08f, 0.2f, 0.5f, 0.2f, 0.0f, 10, new Player[0]);
                            break;
                        }
                        if (player.ticks % 2 != 0) break;
                        Particles.DEPTH_SUSPEND.play(w, (float)loc.getX() + 0.14f, (float)loc.getY() + 0.5f, (float)loc.getZ() + 0.08f, 0.2f, 0.4f, 0.2f, 0.0f, 8, new Player[0]);
                        break;
                    }
                    case WATER_DRIP: {
                        if (player.walking) {
                            if (player.ticks % 3 != 0) break;
                            Particles.DRIP_WATER.play(w, (float)loc.getX(), (float)loc.getY() + 0.6f, (float)loc.getZ(), 0.2f, 0.4f, 0.2f, 0.0f, 3, new Player[0]);
                            break;
                        }
                        if (player.ticks % 3 != 0) break;
                        float a = (float)(-player.ticks) * 0.15f;
                        float x = MathHelper.sin((float)a) * 0.5f;
                        float z = MathHelper.cos((float)a) * 0.5f;
                        Particles.DRIP_WATER.play(w, (float)loc.getX() + x + 0.14f, (float)loc.getY() + 2.3f, (float)loc.getZ() + z + 0.08f, 0.0f, 0.0f, 0.0f, 0.0f, 1, new Player[0]);
                        x = MathHelper.sin((float)(a += (float)Math.PI)) * 0.5f;
                        z = MathHelper.cos((float)a) * 0.5f;
                        Particles.DRIP_WATER.play(w, (float)loc.getX() + x + 0.14f, (float)loc.getY() + 2.3f, (float)loc.getZ() + z + 0.08f, 0.0f, 0.0f, 0.0f, 0.0f, 1, new Player[0]);
                        break;
                    }
                    case FIREWORK: {
                        if (player.walking) {
                            if (player.ticks % 3 != 0) break;
                            Particles.FIREWORKS_SPARK.play(w, (float)loc.getX(), (float)loc.getY() + 0.6f, (float)loc.getZ(), 0.2f, 0.4f, 0.2f, 0.0f, 3, new Player[0]);
                            break;
                        }
                        if (player.ticks % 2 != 0) break;
                        float a = (float)(-player.ticks) * 0.2f;
                        float x = MathHelper.sin((float)a) * 0.5f;
                        float z = MathHelper.cos((float)a) * 0.5f;
                        Particles.FIREWORKS_SPARK.play(w, (float)loc.getX() + x, (float)loc.getY() + 0.8f, (float)loc.getZ() + z, 0.0f, 0.0f, 0.0f, 0.0f, 1, new Player[0]);
                        break;
                    }
                    case ENCHANT: {
                        if (player.walking) {
                            if (player.ticks % 3 != 0) break;
                            Particles.ENCHANTMENT_TABLE.play(w, (float)loc.getX(), (float)loc.getY() + 0.6f, (float)loc.getZ(), 0.2f, 0.4f, 0.2f, 0.1f, 10, new Player[0]);
                            break;
                        }
                        if (player.ticks % 3 != 0) break;
                        Particles.ENCHANTMENT_TABLE.play(w, (float)loc.getX(), (float)loc.getY() + 0.5f, (float)loc.getZ(), 0.2f, 0.3f, 0.2f, 0.6f, 10, new Player[0]);
                        break;
                    }
                    case MAGIC: {
                        if (player.walking) {
                            if (player.ticks % 3 != 0) break;
                            Particles.WITCH_MAGIC.play(w, (float)loc.getX(), (float)loc.getY() + 0.6f, (float)loc.getZ(), 0.2f, 0.4f, 0.2f, 0.0f, 4, new Player[0]);
                            break;
                        }
                        if (player.ticks % 2 != 0) break;
                        float a = (float)(-player.ticks) * 0.2f;
                        float x = MathHelper.sin((float)a) * 0.5f;
                        float z = MathHelper.cos((float)a) * 0.5f;
                        Particles.WITCH_MAGIC.play(w, (float)loc.getX() + x, (float)loc.getY() + 2.3f, (float)loc.getZ() + z, 0.05f, 0.0f, 0.05f, 0.0f, 2, new Player[0]);
                        x = MathHelper.sin((float)(a += (float)Math.PI)) * 0.5f;
                        z = MathHelper.cos((float)a) * 0.5f;
                        Particles.WITCH_MAGIC.play(w, (float)loc.getX() + x, (float)loc.getY() + 2.3f, (float)loc.getZ() + z, 0.05f, 0.0f, 0.05f, 0.0f, 2, new Player[0]);
                    }
                }
            }
        }

        private static boolean equalsLoc(Location a, Location b) {
            if (a == null || b == null) {
                return false;
            }
            return a.getX() == b.getX() && a.getY() == b.getY() && a.getZ() == b.getZ();
        }
    }

    private static class TrailListener
    implements Listener {
        private final JavaPlugin plugin;

        public TrailListener(JavaPlugin plugin) {
            this.plugin = plugin;
        }

        @EventHandler(priority=EventPriority.LOW)
        public void onPlayerLoaded(PlayerLoadedEvent event) {
            TrailPlayer player = new TrailPlayer(event.getPlayer());
            PLAYERS.put(event.getPlayer().getName(), player);
            VimeNetwork.mysql().select("SELECT trail FROM `" + TABLE + "` WHERE `userid` = " + event.getNetworkPlayer().getId(), rs -> {
                while (rs.next()) {
                    try {
                        player.owned.add(Trail.valueOf(rs.getString("trail").toUpperCase()));
                    }
                    catch (Exception exception) {}
                }
            });
        }

        @EventHandler(priority=EventPriority.HIGHEST)
        public void onPlayerLeave(PlayerLeaveEvent event) {
            PLAYERS.remove(event.getPlayer().getName());
        }

        @EventHandler
        public void onPluginDisable(PluginDisableEvent event) {
            if (event.getPlugin().equals(this.plugin)) {
                enabled = false;
            }
        }
    }

    public static class TrailPlayer {
        public final Player bukkit;
        public final Set<Trail> owned;
        public Trail active = null;
        public boolean visible = true;
        int ticks = 0;
        boolean walking = false;
        int standing = 0;
        Location lastLoc;

        private TrailPlayer(Player bukkit) {
            this.bukkit = bukkit;
            this.owned = EnumSet.noneOf(Trail.class);
            this.ticks = 0;
        }

        public void setActive(String name) {
            if (name == null) {
                return;
            }
            try {
                Trail type = Trail.valueOf(name.toUpperCase());
                Rank rank = VimeNetwork.getPlayer(this.bukkit).getRank();
                if (this.owned.contains((Object)type) || rank == Rank.IMMORTAL || rank.has(type.rank)) {
                    this.active = type;
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }

        public String getActiveMysqlString() {
            return this.active == null ? "NULL" : "'" + this.active.name() + "'";
        }

        public String getActive() {
            return this.active == null ? null : this.active.name();
        }
    }

    public static enum Trail {
        FLAME("\u041e\u0433\u043e\u043d\u044c", 0, new ItemStack(Material.BLAZE_POWDER), 15000),
        ENDER("\u0415\u043d\u0434\u0435\u0440", 1, new ItemStack(Material.ENDER_PEARL), 15000),
        HEARTS("\u0421\u0435\u0440\u0434\u0435\u0447\u043a\u0438", 2, new ItemStack(Material.INK_SACK, 1, 1), 15000),
        NOTE("\u041d\u043e\u0442\u044b", 3, new ItemStack(Material.NOTE_BLOCK), 15000),
        HAPPY("\u041a\u0440\u0438\u043f\u0442\u043e\u043d\u0438\u0442", 4, new ItemStack(Material.SEEDS), 15000),
        MOB_SPELL("\u041c\u0430\u0433\u0438\u0447\u0435\u0441\u043a\u0438\u0435 \u043f\u0443\u0437\u044b\u0440\u0438", 5, new ItemStack(Material.SPECKLED_MELON), 15000),
        RED_DUST("\u0426\u0432\u0435\u0442\u043d\u043e\u0439 \u0440\u0435\u0434\u0441\u0442\u043e\u0443\u043d", 6, new ItemStack(Material.REDSTONE), 15000),
        COLORFUL("\u041c\u043d\u043e\u0433\u043e \u0446\u0432\u0435\u0442\u0430", 7, new ItemStack(Material.INK_SACK, 1, 13), 15000),
        CLOUD("\u041e\u0431\u043b\u0430\u043a\u043e", 8, new ItemStack(Material.INK_SACK, 1, 15), 15000),
        ANGRY("\u0417\u043b\u043e\u0439 \u0436\u0438\u0442\u0435\u043b\u044c", 9, new ItemStack(Material.INK_SACK, 1, 14), Rank.VIP),
        SNOW("\u041a\u0443\u0441\u043a\u0438 \u0441\u043d\u0435\u0433\u0430", 10, new ItemStack(Material.SNOW_BALL), Rank.VIP),
        LAVA_DRIP("\u041a\u0430\u043f\u043b\u0438 \u043b\u0430\u0432\u044b", 11, new ItemStack(Material.LAVA_BUCKET), Rank.PREMIUM),
        SUSP("\u041f\u0443\u0441\u0442\u043e\u0442\u0430", 12, new ItemStack(Material.COAL), Rank.PREMIUM),
        WATER_DRIP("\u041a\u0430\u043f\u043b\u0438 \u0432\u043e\u0434\u044b", 13, new ItemStack(Material.WATER_BUCKET), Rank.PREMIUM),
        FIREWORK("\u0424\u0435\u0439\u0435\u0440\u0432\u0435\u0440\u043a", 14, new ItemStack(Material.FIREWORK), Rank.HOLY),
        ENCHANT("\u0417\u0430\u0447\u0430\u0440\u043e\u0432\u0430\u043b\u044c\u043d\u044b\u0439 \u0441\u0442\u043e\u043b", 15, new ItemStack(Material.ENCHANTMENT_TABLE), Rank.HOLY),
        MAGIC("\u041c\u0430\u0433\u0438\u044f", 16, new ItemStack(Material.INK_SACK, 1, 9), Rank.HOLY);

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
            this.is = Items.name(is, "&7\u0421\u043b\u0435\u0434: &e" + displayName, new String[0]);
            this.price = price;
            this.rank = rank;
        }

        public ItemStack withPrice(TrailPlayer player, NetworkPlayer networkPlayer) {
            String status = player.active == this ? "&a\u0410\u043a\u0442\u0438\u0432\u043d\u043e" : (networkPlayer.getRank() == Rank.IMMORTAL || networkPlayer.getRank().has(this.rank) && (this.price == -1 || player.owned.contains((Object)this)) ? "&e\u0414\u043e\u0441\u0442\u0443\u043f\u043d\u043e" : (this.price != -1 && networkPlayer.getCoins() >= this.price ? "&6\u041a\u0443\u043f\u0438\u0442\u044c: " + this.price : (!networkPlayer.getRank().has(this.rank) ? "&f\u0414\u043e\u0441\u0442\u0443\u043f\u043d\u043e \u0434\u043b\u044f " + this.rank.getDisplayName() : "&c\u0426\u0435\u043d\u0430: " + this.price)));
            return Items.appendLore(this.is.clone(), "&7---------------", status);
        }
    }
}

