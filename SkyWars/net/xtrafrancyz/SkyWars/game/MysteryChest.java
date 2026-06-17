/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.util.E
 *  net.xtrafrancyz.VimeNetwork.api.util.U
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.SkyWars.game;

import net.xtrafrancyz.SkyWars.Config;
import net.xtrafrancyz.SkyWars.PlayerInfo;
import net.xtrafrancyz.SkyWars.SkyWars;
import net.xtrafrancyz.SkyWars.game.Game;
import net.xtrafrancyz.SkyWars.game.STexteria;
import net.xtrafrancyz.VimeNetwork.api.util.E;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;

public class MysteryChest
implements Listener,
InventoryHolder {
    private final SkyWars plugin;
    private int task = -1;
    private Inventory inv;
    private boolean open = false;

    public MysteryChest(SkyWars plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
        this.inv = Bukkit.createInventory((InventoryHolder)this, (int)27, (String)"\u041c\u0438\u0441\u0442\u0438\u0447\u0435\u0441\u043a\u0438\u0439 \u0441\u0443\u043d\u0434\u0443\u043a");
    }

    @EventHandler(ignoreCancelled=true)
    public void onChestIntaract(PlayerInteractEvent event) {
        if (E.isRightClick((PlayerInteractEvent)event) && event.hasBlock() && event.getClickedBlock().getTypeId() == Material.ENDER_CHEST.getId()) {
            event.setCancelled(true);
            if (!Config.mysteryChest.equals((Object)event.getClickedBlock().getLocation())) {
                return;
            }
            if (!this.open) {
                U.msg((CommandSender)event.getPlayer(), (String[])new String[]{"&c\u0421\u0443\u043d\u0434\u0443\u043a \u0432 \u0434\u0430\u043d\u043d\u044b\u0439 \u043c\u043e\u043c\u0435\u043d\u0442 \u0437\u0430\u043a\u0440\u044b\u0442. \u041f\u0440\u0438\u0445\u043e\u0434\u0438\u0442\u0435 \u043f\u043e\u0437\u0436\u0435."});
                return;
            }
            event.getPlayer().openInventory(this.inv);
            PlayerInfo.get((Player)event.getPlayer()).chestOpened = true;
        }
    }

    private void open() {
        Game.randomFillInventory(this.inv, this.plugin.game.lootGenerator.mystic());
        STexteria.showMysteryTopTimer("&c\u041c\u0438\u0441\u0442\u0438\u0447\u0435\u0441\u043a\u0438\u0439 \u0441\u0443\u043d\u0434\u0443\u043a \u043e\u0442\u043a\u0440\u044b\u0442! &e{M}:{SS}", 30000L);
        STexteria.showCustomMessage(Bukkit.getOnlinePlayers(), "\u041c\u0438\u0441\u0442\u0438\u0447\u0435\u0441\u043a\u0438\u0439 \u0441\u0443\u043d\u0434\u0443\u043a \u043e\u0442\u043a\u0440\u044b\u0442!", -1982745, 3000L);
        U.bcast((String)"&d\u041c\u0438\u0441\u0442\u0438\u0447\u0435\u0441\u043a\u0438\u0439 \u0441\u0443\u043d\u0434\u0443\u043a &a\u043e\u0442\u043a\u0440\u044b\u0442&d! \u041f\u043e\u0441\u043f\u0435\u0448\u0438\u0442\u0435 \u0437\u0430\u0431\u0440\u0430\u0442\u044c \u0441\u0432\u043e\u0438 \u0437\u0430\u043a\u043e\u043d\u043d\u044b\u0435 \u0432\u0435\u0449\u0438!");
        this.open = true;
        this.task = Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, this::close, 600L);
    }

    private void close() {
        this.inv.clear();
        STexteria.showMysteryTopTimer("\u0414\u043e \u043e\u0442\u043a\u0440\u044b\u0442\u0438\u044f \u043c\u0438\u0441\u0442\u0438\u0447\u0435\u0441\u043a\u043e\u0433\u043e \u0441\u0443\u043d\u0434\u0443\u043a\u0430: &e{M}:{SS}", 180000L);
        U.bcast((String)"&d\u041c\u0438\u0441\u0442\u0438\u0447\u0435\u0441\u043a\u0438\u0439 \u0441\u0443\u043d\u0434\u0443\u043a &c\u0437\u0430\u043a\u0440\u044b\u043b\u0441\u044f");
        this.open = false;
        this.task = Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, this::open, 3600L);
    }

    public void start() {
        STexteria.showMysteryTopTimer("\u0414\u043e \u043e\u0442\u043a\u0440\u044b\u0442\u0438\u044f \u043c\u0438\u0441\u0442\u0438\u0447\u0435\u0441\u043a\u043e\u0433\u043e \u0441\u0443\u043d\u0434\u0443\u043a\u0430: &e{M}:{SS}", 180000L);
        this.task = Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, this::open, 3600L);
    }

    public void stop() {
        this.open = false;
        Bukkit.getScheduler().cancelTask(this.task);
        this.task = -1;
    }

    public Inventory getInventory() {
        return this.inv;
    }
}

