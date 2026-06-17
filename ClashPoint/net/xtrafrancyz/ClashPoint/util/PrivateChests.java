/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.geom.Vec3i
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 */
package net.xtrafrancyz.ClashPoint.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.xtrafrancyz.ClashPoint.object.CPTeam;
import net.xtrafrancyz.ClashPoint.object.PlayerInfo;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3i;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class PrivateChests {
    private final Map<Vec3i, CPTeam> privateChests = new HashMap<Vec3i, CPTeam>();
    private final Map<CPTeam, Inventory> teamEnderChests = new HashMap<CPTeam, Inventory>();

    public boolean canOpen(PlayerInfo player, Location block) {
        CPTeam team = this.privateChests.get(new Vec3i(block));
        return team == null || team.equals(player.team);
    }

    public void addProtection(Location block, CPTeam team) {
        this.privateChests.put(new Vec3i(block), team);
    }

    public void removeProtection(Location block) {
        this.privateChests.remove(new Vec3i(block));
    }

    public void removeProtection(CPTeam team) {
        Iterator<Map.Entry<Vec3i, CPTeam>> it = this.privateChests.entrySet().iterator();
        while (it.hasNext()) {
            if (!it.next().getValue().equals(team)) continue;
            it.remove();
        }
    }

    public void openTeamEnderChest(PlayerInfo player) {
        player.player.openInventory(this.teamEnderChests.computeIfAbsent(player.team, k -> Bukkit.createInventory((InventoryHolder)new FakeInventoryHolder(), (int)27, (String)"\u041a\u043e\u043c\u0430\u043d\u0434\u043d\u044b\u0439 \u0441\u0443\u043d\u0434\u0443\u043a")));
    }

    private static class FakeInventoryHolder
    implements InventoryHolder {
        private FakeInventoryHolder() {
        }

        public Inventory getInventory() {
            return null;
        }
    }
}

