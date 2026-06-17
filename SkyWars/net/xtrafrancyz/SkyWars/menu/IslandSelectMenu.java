/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.VimeNetwork
 *  net.xtrafrancyz.VimeNetwork.api.menu.IMenu
 *  net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer
 *  net.xtrafrancyz.VimeNetwork.api.util.Items
 *  net.xtrafrancyz.VimeNetwork.api.util.Rand
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.SkyWars.menu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Collectors;
import net.xtrafrancyz.SkyWars.Config;
import net.xtrafrancyz.SkyWars.Island;
import net.xtrafrancyz.SkyWars.PlayerInfo;
import net.xtrafrancyz.SkyWars.SkyWars;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.Rand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class IslandSelectMenu
implements IMenu {
    private static final short CLAY_RED = 14;
    private static final short CLAY_YELLOW = 4;
    private static final int RANDOM_SLOT = 13;
    private final SkyWars plugin;
    private final Inventory inv;
    private final Island[] islands;

    public IslandSelectMenu(SkyWars plugin) {
        this.plugin = plugin;
        this.inv = Bukkit.createInventory((InventoryHolder)this, (int)27, (String)"\u0412\u044b\u0431\u043e\u0440 \u043e\u0441\u0442\u0440\u043e\u0432\u0430");
        this.islands = new Island[this.inv.getSize()];
        Island[] islands0 = Config.islands.toArray(new Island[Config.islands.size()]);
        switch (islands0.length) {
            case 2: {
                this.set(0, islands0[0]);
                this.set(1, islands0[1]);
                break;
            }
            case 4: {
                this.set(10, islands0[0]);
                this.set(11, islands0[1]);
                this.set(15, islands0[2]);
                this.set(16, islands0[3]);
                break;
            }
            case 6: {
                this.set(10, islands0[0]);
                this.set(11, islands0[1]);
                this.set(12, islands0[2]);
                this.set(14, islands0[3]);
                this.set(15, islands0[4]);
                this.set(16, islands0[5]);
                break;
            }
            case 8: {
                this.set(2, islands0[0]);
                this.set(3, islands0[1]);
                this.set(5, islands0[2]);
                this.set(6, islands0[3]);
                this.set(20, islands0[4]);
                this.set(21, islands0[5]);
                this.set(23, islands0[6]);
                this.set(24, islands0[7]);
                break;
            }
            case 10: {
                this.set(1, islands0[0]);
                this.set(2, islands0[1]);
                this.set(3, islands0[2]);
                this.set(5, islands0[3]);
                this.set(6, islands0[4]);
                this.set(7, islands0[5]);
                this.set(10, islands0[6]);
                this.set(11, islands0[7]);
                this.set(15, islands0[8]);
                this.set(16, islands0[9]);
                break;
            }
            case 12: {
                this.set(1, islands0[0]);
                this.set(2, islands0[1]);
                this.set(3, islands0[2]);
                this.set(5, islands0[3]);
                this.set(6, islands0[4]);
                this.set(7, islands0[5]);
                this.set(19, islands0[6]);
                this.set(20, islands0[7]);
                this.set(21, islands0[8]);
                this.set(23, islands0[9]);
                this.set(24, islands0[10]);
                this.set(25, islands0[11]);
                break;
            }
            case 16: {
                this.set(0, islands0[0]);
                this.set(1, islands0[1]);
                this.set(2, islands0[2]);
                this.set(3, islands0[3]);
                this.set(5, islands0[4]);
                this.set(6, islands0[5]);
                this.set(7, islands0[6]);
                this.set(8, islands0[7]);
                this.set(18, islands0[8]);
                this.set(19, islands0[9]);
                this.set(20, islands0[10]);
                this.set(21, islands0[11]);
                this.set(23, islands0[12]);
                this.set(24, islands0[13]);
                this.set(25, islands0[14]);
                this.set(26, islands0[15]);
                break;
            }
            case 20: {
                this.set(0, islands0[0]);
                this.set(1, islands0[1]);
                this.set(2, islands0[2]);
                this.set(3, islands0[3]);
                this.set(5, islands0[4]);
                this.set(6, islands0[5]);
                this.set(7, islands0[6]);
                this.set(8, islands0[7]);
                this.set(9, islands0[8]);
                this.set(10, islands0[9]);
                this.set(16, islands0[10]);
                this.set(17, islands0[11]);
                this.set(18, islands0[12]);
                this.set(19, islands0[13]);
                this.set(20, islands0[14]);
                this.set(21, islands0[15]);
                this.set(23, islands0[16]);
                this.set(24, islands0[17]);
                this.set(25, islands0[18]);
                this.set(26, islands0[19]);
                break;
            }
            default: {
                throw new IllegalArgumentException("Number of teams must be one of: 6, 8, 10, 12, 16, 20. Given - " + islands0.length);
            }
        }
        this.inv.setItem(13, Items.glow((ItemStack)Items.name((Material)Material.NAME_TAG, (String)"&f\u0421\u043b\u0443\u0447\u0430\u0439\u043d\u044b\u0439 \u043e\u0441\u0442\u0440\u043e\u0432", (String[])new String[]{"\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u0432\u044b\u0431\u043e\u0440\u0430"})));
        this.update();
    }

    private void set(int slot, Island island) {
        island.slot = slot;
        this.islands[slot] = island;
    }

    public void update() {
        for (Island island : this.islands) {
            if (island == null) continue;
            this.update(island);
        }
    }

    public void update(Island island) {
        ArrayList<String> lore = new ArrayList<String>(2 + island.players.size());
        if (island.players.size() < Config.islandPlayers) {
            lore.add("\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u0432\u044b\u0431\u043e\u0440\u0430");
        }
        if (island.players.size() > 0) {
            lore.add(null);
        }
        lore.addAll(island.players.stream().map(pi -> ChatColor.GRAY + pi.player.getDisplayName()).collect(Collectors.toList()));
        ItemStack is = island.players.size() == 0 ? new ItemStack(Material.EMERALD_BLOCK) : (island.players.size() < Config.islandPlayers ? new ItemStack(Material.STAINED_CLAY, island.players.size(), 4) : new ItemStack(Material.STAINED_CLAY, Config.islandPlayers, 14));
        this.inv.setItem(island.slot, Items.name((ItemStack)is, (String)("&f\u041e\u0441\u0442\u0440\u043e\u0432 " + island.id + " &f[" + island.players.size() + "/" + Config.islandPlayers + "]"), lore));
    }

    public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
        Island island;
        if (slot == 13) {
            LinkedList<Island> ts = new LinkedList<Island>(Config.islands);
            Iterator it = ts.iterator();
            while (it.hasNext()) {
                if (((Island)it.next()).players.size() < Config.islandPlayers) continue;
                it.remove();
            }
            if (ts.size() == 0) {
                return;
            }
            island = (Island)Rand.of(ts);
        } else {
            island = this.islands[slot];
            if (island == null) {
                return;
            }
            if (island.players.size() >= Config.islandPlayers) {
                return;
            }
        }
        this.plugin.game.join(PlayerInfo.get(player), island);
        NetworkPlayer networkPlayer = VimeNetwork.getPlayer((Player)player);
        if (networkPlayer.isPartyLeader()) {
            for (String partyPlayer : networkPlayer.getParty().getPlayers()) {
                if (island.players.size() >= Config.islandPlayers) break;
                PlayerInfo pi = PlayerInfo.PLAYERS.get(partyPlayer);
                if (pi == null || pi.island != null) continue;
                this.plugin.game.join(pi, island);
            }
        }
        player.closeInventory();
    }

    public Inventory getInventory() {
        return this.inv;
    }
}

