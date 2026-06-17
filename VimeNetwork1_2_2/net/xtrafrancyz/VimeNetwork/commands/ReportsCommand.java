/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.tuple.Pair
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.Core.CoreByteMap;
import net.xtrafrancyz.Core.network.packet.Packet1PlayerInfo;
import net.xtrafrancyz.Core.network.packet.Packet202GetPlayerInfo;
import net.xtrafrancyz.Core.network.packet.Packet52CustomMessage;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.menu.ConfirmMenu;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.commands.EtpCommand;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ReportsCommand
implements CommandExecutor {
    private ReportsInventory menu = new ReportsInventory();

    public ReportsCommand() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)VNPlugin.instance(), () -> {
            if (this.menu.lastUsed != 0L && System.currentTimeMillis() - this.menu.lastUsed > 1200000L) {
                this.menu.cleanup();
            }
        }, 12000L, 12000L);
        VimeNetwork.core().addHandler(Packet52CustomMessage.class, this::onCustomMessage);
    }

    public void onCustomMessage(Packet52CustomMessage packet) {
        if (packet.tag.equals("reports") && packet.data.getString("action", "").equals("list")) {
            ArrayList<Violator> violators = new ArrayList<Violator>();
            for (CoreByteMap candidate : packet.data.getMapArray("candidates")) {
                String server = candidate.getString("server");
                if (server == null) continue;
                Violator violator = new Violator(candidate.getString("username"), Rank.getRank(candidate.getString("rank")), server);
                for (CoreByteMap report : candidate.getMapArray("reports")) {
                    violator.reports.add(new Report(report.getString("reporter"), report.getString("details"), report.getInt("time")));
                }
                violators.add(violator);
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)VNPlugin.instance(), () -> this.menu.load(violators), 2L);
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!VimeNetwork.hasRank(sender, Rank.MODER, true)) {
            return true;
        }
        this.menu.show((Player)sender);
        return true;
    }

    private static class Report {
        public String reporter;
        public String details;
        public int time;

        public Report(String reporter, String details, int time) {
            this.reporter = reporter;
            this.details = details;
            this.time = time;
        }
    }

    private static class Violator {
        public final String username;
        public final Rank rank;
        public final String server;
        public List<Report> reports;

        public Violator(String username, Rank rank, String server) {
            this.username = username;
            this.rank = rank;
            this.server = server;
            this.reports = new LinkedList<Report>();
        }
    }

    private static class BanMenu
    implements IMenu {
        private static List<Pair<Integer, String>> BANS = Arrays.asList(Pair.of((Object)0, (Object)"\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435 \u0447\u0438\u0442\u043e\u0432"), Pair.of((Object)10080, (Object)"\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435 \u0431\u0430\u0433\u043e\u0432"), Pair.of((Object)2880, (Object)"\u041d\u0435\u043a\u043e\u0440\u0440\u0435\u043a\u0442\u043d\u0430\u044f \u043f\u043e\u0441\u0442\u0440\u043e\u0439\u043a\u0430"));
        private Violator violator;
        private ViolatorMenu parent;
        private Inventory inv;

        public BanMenu(Violator violator, ViolatorMenu parent) {
            this.violator = violator;
            this.parent = parent;
            this.inv = Bukkit.createInventory((InventoryHolder)this, (int)27, (String)("\u0411\u0430\u043d \u0434\u043b\u044f " + violator.username));
            this.inv.setItem(4, Items.name(Material.BED, "&f\u2190 &e\u041d\u0430\u0437\u0430\u0434", new String[0]));
            int index = 0;
            for (Pair<Integer, String> ban : BANS) {
                String time = "&f\u043d\u0430\u0432\u0441\u0435\u0433\u0434\u0430";
                if ((Integer)ban.getLeft() != 0) {
                    time = "\u043d\u0430 &f" + ban.getLeft() + "&a \u043c\u0438\u043d.";
                }
                this.inv.setItem(this.getSlot(index++), Items.name(new ItemStack(Material.WOOL, 1, 14), "&a\u0411\u0430\u043d " + time, "&f" + (String)ban.getRight()));
            }
        }

        private int getSlot(int index) {
            return 10 + 9 * (index / 7) + index % 7;
        }

        private int getIndex(int slot) {
            if (slot % 9 == 0 || (slot + 1) % 9 == 0) {
                return -1;
            }
            if ((slot -= 10) < 0) {
                return -1;
            }
            int row = slot / 9;
            return row * 7 + (slot - row * 9) % 7;
        }

        @Override
        public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
            if (slot == 4) {
                this.parent.show(player);
                return;
            }
            int index = this.getIndex(slot);
            if (index >= 0 && index < BANS.size()) {
                Pair<Integer, String> ban = BANS.get(index);
                ConfirmMenu menu = new ConfirmMenu(this.inv, () -> {
                    VimeNetwork.core().sendPacket(new Packet52CustomMessage("reports", Packet52CustomMessage.Scope.CORE).put("action", "ban").put("violator", this.violator.username).put("moder", player.getName()).put("duration", ban.getLeft()).put("reason", ban.getRight()));
                    this.parent.parent.requestViolators();
                    this.parent.parent.show(player);
                }, "\u041f\u043e\u0434\u0442\u0432\u0435\u0440\u0436\u0434\u0435\u043d\u0438\u0435 \u0431\u0430\u043d\u0430");
                String time = "\u043d\u0430\u0432\u0441\u0435\u0433\u0434\u0430&a";
                if ((Integer)ban.getLeft() != 0) {
                    time = "\u043d\u0430 &f" + ban.getLeft() + "&a \u043c\u0438\u043d.";
                }
                menu.setConfirmText("&a\u0417\u0430\u0431\u0430\u043d\u0438\u0442\u044c " + this.violator.username, "&f" + time + " \u0441 \u043f\u0440\u0438\u0447\u043d\u043e\u0439", "&f" + (String)ban.getRight());
                menu.setBackOnConfirm(false);
                menu.show(player);
            }
        }

        public Inventory getInventory() {
            return this.inv;
        }
    }

    private static class ViolatorMenu
    implements IMenu {
        private ReportsInventory parent;
        private Inventory inv;
        private Violator violator;

        public ViolatorMenu(ReportsInventory parent, Violator violator) {
            this.parent = parent;
            this.violator = violator;
            this.inv = Bukkit.createInventory((InventoryHolder)this, (int)54, (String)("\u041d\u0430\u0440\u0443\u0448\u0438\u0442\u0435\u043b\u044c " + violator.username));
            this.inv.setItem(4, Items.name(Material.BED, "&f\u2190 &e\u041d\u0430\u0437\u0430\u0434", new String[0]));
            this.inv.setItem(20, Items.name(new ItemStack(Material.WOOL, 1, 5), "&a\u0418\u0433\u0440\u043e\u043a \u0445\u043e\u0440\u043e\u0448\u0438\u0439", "&7\u0415\u0441\u043b\u0438 \u0443 \u0438\u0433\u0440\u043e\u043a\u0430 \u0432\u0441\u0435 \u0432\u043f\u043e\u0440\u044f\u0434\u043a\u0435", "&7\u0438 \u0436\u0430\u043b\u043e\u0431\u0430 \u043d\u0430 \u043d\u0435\u0433\u043e \u043e\u0448\u0438\u0431\u043e\u0447\u043d\u0430"));
            this.inv.setItem(22, Items.name(Material.WOOL, "&a\u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f", "&7\u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u0443\u0435\u0442 \u0432\u0430\u0441 \u043a \u043d\u0430\u0440\u0443\u0448\u0438\u0442\u0435\u043b\u044e"));
            this.inv.setItem(24, Items.name(new ItemStack(Material.WOOL, 1, 14), "&a\u0417\u0430\u0431\u0430\u043d\u0438\u0442\u044c", "&7\u041e\u0442\u043a\u0440\u044b\u0432\u0430\u0435\u0442 \u043c\u0435\u043d\u044e \u0441 \u0432\u044b\u0431\u043e\u0440\u043e\u043c", "&7\u043f\u0440\u0438\u0447\u0438\u043d\u044b \u0438 \u0432\u0440\u0435\u043c\u0435\u043d\u0438 \u0431\u0430\u043d\u0430"));
            int index = 0;
            for (Report report : violator.reports) {
                int slot = 37 + index;
                if (slot >= this.inv.getSize()) break;
                int diff = (int)((System.currentTimeMillis() / 1000L - (long)report.time) / 60L);
                String time = diff == 0 ? "\u0442\u043e\u043b\u044c\u043a\u043e \u0447\u0442\u043e" : diff + " \u043c. \u043d\u0430\u0437\u0430\u0434";
                this.inv.setItem(slot, Items.name(Material.PAPER, "&f\u0420\u0435\u043f\u043e\u0440\u0442 #" + (index + 1), "&e\u0412\u0440\u0435\u043c\u044f: &f" + time, "&e\u041e\u0442\u043f\u0440\u0430\u0432\u0438\u0442\u0435\u043b\u044c: &f" + report.reporter, "&e\u041f\u0440\u0438\u0447\u0438\u043d\u0430: &f" + report.details));
                ++index;
            }
        }

        @Override
        public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
            switch (slot) {
                case 4: {
                    this.parent.show(player);
                    break;
                }
                case 20: {
                    ConfirmMenu menu = new ConfirmMenu(this.inv, () -> {
                        VimeNetwork.core().sendPacket(new Packet52CustomMessage("reports", Packet52CustomMessage.Scope.CORE).put("action", "reject").put("violator", this.violator.username).put("moder", player.getName()));
                        this.parent.requestViolators();
                        this.parent.show(player);
                    }, "\u041e\u0442\u043a\u043b\u043e\u043d\u0438\u0442\u044c \u0436\u0430\u043b\u043e\u0431\u0443");
                    menu.setConfirmText("&a\u0421 \u0438\u0433\u0440\u043e\u043a\u043e\u043c \u0432\u0441\u0435 \u0432\u043f\u043e\u0440\u044f\u0434\u043a\u0435", new String[0]);
                    menu.setBackOnConfirm(false);
                    menu.show(player);
                    break;
                }
                case 22: {
                    VimeNetwork.core().sendPacket(new Packet202GetPlayerInfo(this.violator.username, 8), packet0 -> {
                        if (packet0.getId() == 1) {
                            Packet1PlayerInfo response = (Packet1PlayerInfo)packet0;
                            if (response.bukkit != null) {
                                EtpCommand.tpToServerNPlayer((CommandSender)player, response.bukkit, this.violator.username);
                                return;
                            }
                        }
                        U.msg((CommandSender)player, "&c\u0418\u0433\u0440\u043e\u043a &f" + this.violator.username + "&c \u043e\u0444\u0444\u043b\u0430\u0439\u043d");
                    }, 400L, () -> U.msg((CommandSender)player, "&c\u041e\u0448\u0438\u0431\u043a\u0430 \u0441\u0432\u044f\u0437\u0438 \u0441 \u0433\u043b\u0430\u0432\u043d\u044b\u043c \u0441\u0435\u0440\u0432\u0435\u0440\u043e\u043c"));
                    break;
                }
                case 24: {
                    new BanMenu(this.violator, this).show(player);
                }
            }
        }

        public Inventory getInventory() {
            return this.inv;
        }
    }

    private static class ReportsInventory
    implements IMenu {
        private long lastUsed = 0L;
        private Inventory inv;
        private boolean loading;
        private List<Violator> violators = new ArrayList<Violator>();

        private ReportsInventory() {
        }

        public void cleanup() {
            this.inv = null;
            this.violators.clear();
            this.lastUsed = 0L;
        }

        private void use() {
            this.lastUsed = System.currentTimeMillis();
            if (this.inv == null) {
                this.inv = Bukkit.createInventory((InventoryHolder)this, (int)54, (String)"\u041c\u0435\u043d\u044e \u0436\u0430\u043b\u043e\u0431");
                this.inv.setItem(4, Items.name(Material.MAGMA_CREAM, "&a\u041e\u0431\u043d\u043e\u0432\u0438\u0442\u044c", new String[0]));
            }
        }

        public void load(ArrayList<Violator> violators) {
            violators.sort((v1, v2) -> {
                int res = Integer.compare(v2.reports.size(), v1.reports.size());
                if (res != 0) {
                    return res;
                }
                return v1.username.compareTo(v2.username);
            });
            this.violators = violators;
            for (int i = 0; i < violators.size(); ++i) {
                Violator violator = violators.get(i);
                ArrayList<String> lore = new ArrayList<String>();
                lore.add("&a\u0421\u0435\u0440\u0432\u0435\u0440: &f" + violator.server);
                lore.add("&a\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u0436\u0430\u043b\u043e\u0431: &f" + violator.reports.size());
                for (Report report : violator.reports) {
                    lore.add(" &e" + report.reporter + "&f: &7" + report.details);
                }
                lore.add("");
                lore.add("&a\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u0439");
                String prefix = "&f";
                if (violator.rank != Rank.PLAYER) {
                    prefix = violator.rank.getColor() + "[" + violator.rank.getPrefix() + "] ";
                }
                this.inv.setItem(this.getSlot(i), Items.name(Items.head(violator.username), prefix + violator.username, lore));
            }
            this.loading = false;
        }

        private int getSlot(int index) {
            return 9 + index;
        }

        private int getIndex(int slot) {
            return slot - 9;
        }

        private void requestViolators() {
            this.loading = true;
            this.use();
            if (this.violators != null) {
                for (int i = 0; i < this.violators.size(); ++i) {
                    this.inv.clear(this.getSlot(i));
                }
            }
            VimeNetwork.core().sendPacket(new Packet52CustomMessage("reports", Packet52CustomMessage.Scope.CORE).put("action", "list"));
        }

        @Override
        public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
            if (this.loading) {
                return;
            }
            if (slot == 4) {
                this.requestViolators();
                return;
            }
            int index = this.getIndex(slot);
            if (index >= 0 && index < this.violators.size()) {
                new ViolatorMenu(this, this.violators.get(index)).show(player);
            }
        }

        public Inventory getInventory() {
            return this.inv;
        }

        @Override
        public void show(Player player) {
            this.requestViolators();
            this.use();
            IMenu.super.show(player);
        }
    }
}

