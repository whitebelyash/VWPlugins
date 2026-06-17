/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.commands;

import java.util.HashMap;
import java.util.Map;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.Commons.player.Permission;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.util.Invs;
import net.xtrafrancyz.VimeNetwork.api.util.Spectators;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VanishCommand
implements CommandExecutor {
    public Map<String, VanishData> data = new HashMap<String, VanishData>();

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!VimeNetwork.hasPermission(sender, Permission.VANISH, true)) {
            return true;
        }
        Player player = (Player)sender;
        if (Spectators.instance().contains(player)) {
            this.disableVanish(player);
        } else {
            this.enableVanish(player);
        }
        return true;
    }

    public void purge(Player player) {
        this.data.remove(player.getName());
    }

    public void disableVanish(Player player) {
        if (Spectators.instance().contains(player)) {
            VanishData vanishData = this.data.remove(player.getName());
            if (vanishData == null) {
                U.msg((CommandSender)player, T.error("\u0420\u0435\u0436\u0438\u043c \u043d\u0430\u0431\u043b\u044e\u0434\u0430\u0442\u0435\u043b\u044f", "\u041a\u0430\u043a\u0430\u044f-\u0442\u043e \u0445\u0443\u0439\u043d\u044f, \u0438\u0437\u0432\u0438\u043d\u0438\u0442\u0435, \u044f \u043d\u0435 \u043f\u043e\u043d\u0438\u043c\u0430\u044e \u043a\u0443\u0434\u0430 \u0434\u0435\u043b\u0438\u0441\u044c \u0432\u0441\u0435 \u0432\u0430\u0448\u0438 \u0432\u0435\u0449\u0438"));
                return;
            }
            Spectators.instance().remove(player);
            player.getInventory().setContents(vanishData.inventory);
            player.getInventory().setArmorContents(vanishData.armor);
            player.teleport(vanishData.lastLoc);
            player.setAllowFlight(vanishData.allowFlight);
            player.setFlying(vanishData.flying);
            player.setWalkSpeed(vanishData.walkspeed);
            player.setFlySpeed(vanishData.flyspeed);
            player.setMaxHealth(vanishData.maxHealth);
            player.setHealth(vanishData.health);
            player.setFallDistance(0.0f);
            U.msg((CommandSender)player, T.warning("\u0420\u0435\u0436\u0438\u043c \u043d\u0430\u0431\u043b\u044e\u0434\u0430\u0442\u0435\u043b\u044f", "\u0414\u0435\u0430\u043a\u0442\u0438\u0432\u0438\u0440\u043e\u0432\u0430\u043d"));
        }
    }

    public void enableVanish(Player player) {
        if (!Spectators.instance().contains(player)) {
            this.data.put(player.getName(), new VanishData(player));
            Spectators.instance().add(player);
            Invs.clear((HumanEntity)player);
            player.setAllowFlight(true);
            player.setFlying(true);
            U.msg((CommandSender)player, T.success("\u0420\u0435\u0436\u0438\u043c \u043d\u0430\u0431\u043b\u044e\u0434\u0430\u0442\u0435\u043b\u044f", "\u0410\u043a\u0442\u0438\u0432\u0438\u0440\u043e\u0432\u0430\u043d"));
        }
    }

    public static class VanishData {
        public ItemStack[] inventory;
        public ItemStack[] armor;
        public Location lastLoc;
        public boolean allowFlight;
        public boolean flying;
        public float flyspeed;
        public float walkspeed;
        public double health;
        public double maxHealth;

        public VanishData(Player player) {
            this.inventory = player.getInventory().getContents();
            this.armor = player.getInventory().getArmorContents();
            this.lastLoc = player.getLocation();
            this.allowFlight = player.getAllowFlight();
            this.flying = player.isFlying();
            this.flyspeed = player.getFlySpeed();
            this.walkspeed = player.getWalkSpeed();
            this.health = player.getHealth();
            this.maxHealth = player.getMaxHealth();
        }
    }
}

