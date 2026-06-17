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
package net.xtrafrancyz.ClashPoint.menu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Collectors;
import net.xtrafrancyz.ClashPoint.ClashPoint;
import net.xtrafrancyz.ClashPoint.Config;
import net.xtrafrancyz.ClashPoint.object.CPTeam;
import net.xtrafrancyz.ClashPoint.object.PlayerInfo;
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

public class TeamSelectMenu
implements IMenu {
    private static final int RANDOM_SLOT = 31;
    private final ClashPoint plugin;
    private final Inventory inv;
    private final CPTeam[] teams;

    public TeamSelectMenu(ClashPoint plugin) {
        this.plugin = plugin;
        this.inv = Bukkit.createInventory((InventoryHolder)this, (int)36, (String)"\u0412\u044b\u0431\u043e\u0440 \u043a\u043e\u043c\u0430\u043d\u0434\u044b");
        this.teams = new CPTeam[this.inv.getSize()];
        CPTeam[] tms = Config.teams.toArray(new CPTeam[Config.teams.size()]);
        switch (tms.length) {
            case 2: {
                this.set(11, tms[0]);
                this.set(15, tms[1]);
                break;
            }
            case 3: {
                this.set(11, tms[0]);
                this.set(13, tms[1]);
                this.set(15, tms[2]);
                break;
            }
            case 4: {
                this.set(10, tms[0]);
                this.set(12, tms[1]);
                this.set(14, tms[2]);
                this.set(16, tms[3]);
                break;
            }
            case 6: {
                this.set(10, tms[0]);
                this.set(11, tms[1]);
                this.set(12, tms[2]);
                this.set(14, tms[3]);
                this.set(15, tms[4]);
                this.set(16, tms[5]);
                break;
            }
            case 8: {
                this.set(9, tms[0]);
                this.set(10, tms[1]);
                this.set(11, tms[2]);
                this.set(12, tms[3]);
                this.set(14, tms[4]);
                this.set(15, tms[5]);
                this.set(16, tms[6]);
                this.set(17, tms[7]);
                break;
            }
            default: {
                throw new IllegalArgumentException("Number of team must be one of: 2, 3, 4, 6, 8");
            }
        }
        this.inv.setItem(31, Items.glow((ItemStack)Items.name((Material)Material.NAME_TAG, (String)"&f\u0421\u043b\u0443\u0447\u0430\u0439\u043d\u0430\u044f \u043a\u043e\u043c\u0430\u043d\u0434\u0430", (String[])new String[]{"\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u0432\u044b\u0431\u043e\u0440\u0430"})));
        this.update();
    }

    private void set(int slot, CPTeam team) {
        team.slot = slot;
        this.teams[slot] = team;
    }

    public void update() {
        for (CPTeam team : this.teams) {
            if (team == null) continue;
            this.update(team);
        }
    }

    public void update(CPTeam team) {
        ArrayList<String> lore = new ArrayList<String>(2 + team.players.size());
        if (team.players.size() < Config.teamPlayers) {
            lore.add("\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u0432\u044b\u0431\u043e\u0440\u0430");
        }
        if (team.players.size() > 0) {
            lore.add(null);
        }
        lore.addAll(team.players.stream().map(pi -> ChatColor.GRAY + pi.player.getDisplayName()).collect(Collectors.toList()));
        this.inv.setItem(team.slot, Items.name((ItemStack)new ItemStack(Material.WOOL, 1, team.wool), (String)(team.chatColor + team.names[0] + " \u043a\u043e\u043c\u0430\u043d\u0434\u0430 &f[" + team.players.size() + "/" + Config.teamPlayers + "]"), lore));
    }

    public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
        CPTeam team;
        if (slot == 31) {
            LinkedList<CPTeam> ts = new LinkedList<CPTeam>(Config.teams);
            Iterator it = ts.iterator();
            while (it.hasNext()) {
                if (((CPTeam)it.next()).players.size() < Config.teamPlayers) continue;
                it.remove();
            }
            if (ts.size() == 0) {
                return;
            }
            team = (CPTeam)Rand.of(ts);
        } else {
            team = this.teams[slot];
            if (team == null) {
                return;
            }
            if (team.players.size() >= Config.teamPlayers) {
                return;
            }
        }
        this.plugin.game.join(PlayerInfo.get(player), team, false);
        NetworkPlayer networkPlayer = VimeNetwork.getPlayer((Player)player);
        if (networkPlayer.isPartyLeader()) {
            for (String partyPlayer : networkPlayer.getParty().getPlayers()) {
                if (team.players.size() >= Config.teamPlayers) break;
                PlayerInfo pi = PlayerInfo.PLAYERS.get(partyPlayer);
                if (pi == null || pi.team != null) continue;
                this.plugin.game.join(pi, team, false);
            }
        }
        player.closeInventory();
    }

    public Inventory getInventory() {
        return this.inv;
    }
}

