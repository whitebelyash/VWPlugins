/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.MathHelper
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.server.v1_6_R3.MathHelper;
import net.xtrafrancyz.VimeNetwork.api.event.ServiceItemClickedEvent;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class PlayerTargetCompass
implements Listener {
    private Map<Player, UpdatableData> updatable = new HashMap<Player, UpdatableData>();
    private Function<Player, Collection<Player>> targetProvider;
    private Function<Player, String> playerNameProvider;

    public PlayerTargetCompass(Plugin plugin, Function<Player, Collection<Player>> targetProvider) {
        this.targetProvider = targetProvider;
        Bukkit.getPluginManager().registerEvents((Listener)this, plugin);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::run, 20L, 20L);
        this.playerNameProvider = Player::getDisplayName;
    }

    public void setPlayerNameProvider(Function<Player, String> provider) {
        this.playerNameProvider = provider;
    }

    @EventHandler(priority=EventPriority.LOW)
    private void onInteract(ServiceItemClickedEvent event) {
        if (event.getItem().getType() == Material.COMPASS) {
            ItemMeta im;
            if (event.getItem().hasItemMeta() && (im = event.getItem().getItemMeta()).hasDisplayName() && im.getDisplayName().contains("\u043b\u043e\u0431\u0431\u0438")) {
                return;
            }
            event.setCancelled(true);
            UpdatableData data = this.updatable.get(event.getPlayer());
            if (data == null) {
                return;
            }
            if (data.nearest == null) {
                U.msg((CommandSender)event.getPlayer(), "&f\u0412\u0440\u0430\u0433\u043e\u0432 \u0431\u043e\u043b\u044c\u0448\u0435 \u043d\u0435 \u043e\u0441\u0442\u0430\u043b\u043e\u0441\u044c...");
            } else {
                int distance = (int)MathHelper.sqrt((double)data.distance);
                U.msg((CommandSender)event.getPlayer(), "&f\u0411\u043b\u0438\u0436\u0430\u0439\u0448\u0438\u0439 \u0438\u0433\u0440\u043e\u043a: " + this.playerNameProvider.apply(data.nearest) + "&r&f. \u0420\u0430\u0441\u0441\u0442\u043e\u044f\u043d\u0438\u0435: &e" + distance + " &f" + U.plurals(distance, "\u0431\u043b\u043e\u043a", "\u0431\u043b\u043e\u043a\u0430", "\u0431\u043b\u043e\u043a\u043e\u0432"));
            }
        }
    }

    private void run() {
        for (Map.Entry<Player, UpdatableData> entry : this.updatable.entrySet()) {
            Player player = entry.getKey();
            UpdatableData data = entry.getValue();
            Location loc = player.getLocation();
            double min = 3.4028234663852886E38;
            Player nearest = null;
            for (Player target : this.targetProvider.apply(player)) {
                double distance;
                if (target.equals(player) || !((distance = loc.distanceSquared(target.getLocation())) < min)) continue;
                min = distance;
                nearest = target;
            }
            data.nearest = nearest;
            data.distance = min;
            if (nearest != null) {
                player.setCompassTarget(nearest.getLocation());
                continue;
            }
            player.setCompassTarget(loc);
        }
    }

    public void addUpdatePlayer(Player player) {
        this.updatable.put(player, new UpdatableData());
    }

    public void removeUpdatePlayer(Player player) {
        this.updatable.remove(player);
    }

    private static class UpdatableData {
        Player nearest;
        double distance;

        private UpdatableData() {
        }
    }
}

