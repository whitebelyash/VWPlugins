/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.bukkit.texteria.Texteria
 *  net.xtrafrancyz.bukkit.texteria.utils.ParsedTime
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.Core.network.packet.Packet57StreamStatus;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.VTexteria;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import net.xtrafrancyz.bukkit.texteria.Texteria;
import net.xtrafrancyz.bukkit.texteria.utils.ParsedTime;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class StreamMenu
implements IMenu {
    private Inventory inv;
    private List<StreamerData> list = new ArrayList<StreamerData>();
    private Map<String, StreamerData> byStreamer = new HashMap<String, StreamerData>();
    private boolean dirty = true;
    private int oldSize = 0;

    public StreamMenu() {
        this.inv = Bukkit.createInventory((InventoryHolder)this, (int)36, (String)"\u0410\u043a\u0442\u0438\u0432\u043d\u044b\u0435 \u0441\u0442\u0440\u0438\u043c\u044b");
        this.inv.setItem(3, Items.name(Material.PAPER, "&a\u0427\u0442\u043e \u044d\u0442\u043e \u0442\u0430\u043a\u043e\u0435?", " &f\u0417\u0434\u0435\u0441\u044c \u043e\u0442\u043e\u0431\u0440\u0430\u0436\u0430\u044e\u0442\u0441\u044f \u0441\u0442\u0440\u0438\u043c\u044b,", "&f\u043a\u043e\u0442\u043e\u0440\u044b\u0435 \u0432 \u0434\u0430\u043d\u043d\u044b\u0439 \u043c\u043e\u043c\u0435\u043d\u0442 \u0438\u0434\u0443\u0442", "&f\u043d\u0430 \u043f\u0440\u043e\u0435\u043a\u0442\u0435."));
        this.inv.setItem(4, Items.name(Material.PAPER, "&a\u041a\u0442\u043e \u043c\u043e\u0436\u0435\u0442 \u0434\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u0441\u0442\u0440\u0438\u043c?", " &f\u0414\u043e\u0431\u0430\u0432\u043b\u044f\u0442\u044c \u0441\u0442\u0440\u0438\u043c\u044b \u043c\u043e\u0433\u0443\u0442 \u0442\u043e\u043b\u044c\u043a\u043e,", "&f\u0438\u0433\u0440\u043e\u043a\u0438 \u0441\u043e \u0441\u0442\u0430\u0442\u0443\u0441\u043e\u043c &cYouTube", "&f\u0438\u043b\u0438 &3\u0430\u0434\u043c\u0438\u043d\u0438\u0441\u0442\u0440\u0430\u0442\u043e\u0440\u044b&f."));
        this.inv.setItem(5, Items.name(Material.PAPER, "&a\u0417\u0430\u0447\u0435\u043c \u044d\u0442\u043e \u0432\u0441\u0451 \u043d\u0443\u0436\u043d\u043e?", " &f\u041c\u043d\u043e\u0433\u0438\u0435 \u0438\u0433\u0440\u043e\u043a\u0438 \u0438 \u043d\u0435 \u0437\u043d\u0430\u044e\u0442 \u0447\u0442\u043e", "&f\u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u0435 \u0438\u0433\u0440\u0430\u0435\u0442 \u043c\u043d\u043e\u0436\u0435\u0441\u0442\u0432\u043e \u043f\u043e\u043f\u0443\u043b\u044f\u0440\u043d\u044b\u0445", "&f\u043b\u0438\u0447\u043d\u043e\u0441\u0442\u0435\u0439, \u043a\u043e\u0442\u043e\u0440\u044b\u0435 \u0442\u0430\u043a\u0436\u0435 \u0445\u043e\u0442\u044f\u0442", "&f\u043f\u043e\u0438\u0433\u0440\u0430\u0442\u044c \u0438 \u043f\u043e\u043e\u0431\u0449\u0430\u0442\u044c\u0441\u044f \u0441 \u0438\u0433\u0440\u043e\u043a\u0430\u043c\u0438.", " &f\u0412\u044b \u0441\u043c\u043e\u0436\u0435\u0442\u0435 \u043f\u043e\u0441\u043c\u043e\u0442\u0440\u0435\u0442\u044c \u0430\u043a\u0442\u0438\u0432\u043d\u044b\u0435", "&f\u0441\u0442\u0440\u0438\u043c\u044b \u0438 \"\u043f\u043e\u043f\u0430\u0441\u0442\u044c \u0432 \u0442\u0435\u043b\u0438\u043a\"."));
        Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)VNPlugin.instance(), this::streamNotifier, 6000L, 12000L);
    }

    private Player[] getNotificablePlayers() {
        ArrayList<Player> players = new ArrayList<Player>(VPlayer.PLAYERS.size());
        for (VPlayer player : VPlayer.PLAYERS.values()) {
            if (!player.settings.get(3)) continue;
            players.add(player.player);
        }
        return players.toArray(new Player[players.size()]);
    }

    private void streamNotifier() {
        if (this.list.size() > 0) {
            Player[] players = this.getNotificablePlayers();
            VTexteria.showStreamMessage(this.list, players);
            String msg = U.colored(T.success("VimeWorld", "\u041d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u0435 \u0438\u0434\u0451\u0442 " + this.list.size() + " " + U.plurals(this.list.size(), "\u0441\u0442\u0440\u0438\u043c", "\u0441\u0442\u0440\u0438\u043c\u0430", "\u0441\u0442\u0440\u0438\u043c\u043e\u0432") + "! \u041d\u0430\u043f\u0438\u0448\u0438\u0442\u0435 &l/streams&a \u0434\u043b\u044f \u043f\u0440\u043e\u0441\u043c\u043e\u0442\u0440\u0430"));
            for (Player player : players) {
                player.sendMessage(msg);
            }
        }
    }

    public void update(Packet57StreamStatus packet) {
        if (packet.status == Packet57StreamStatus.Status.OFFLINE) {
            StreamerData streamer = this.byStreamer.get(packet.owner);
            if (streamer != null) {
                streamer.removeStream(packet.url);
                if (streamer.streams.isEmpty()) {
                    this.byStreamer.remove(packet.owner);
                    this.list.remove(streamer);
                }
                this.dirty = true;
            }
        } else {
            StreamerData streamer = this.byStreamer.get(packet.owner);
            if (streamer == null) {
                streamer = new StreamerData(packet.owner);
                this.byStreamer.put(packet.owner, streamer);
                this.list.add(streamer);
            }
            streamer.updateStream(packet);
            this.dirty = true;
            if (packet.notification) {
                Player[] players = this.getNotificablePlayers();
                VTexteria.showNewStreamer(streamer, players);
                String platform = streamer.sortedStreams().get((int)0).platform;
                platform = platform.equals("\u0412\u041a\u043e\u043d\u0442\u0430\u043a\u0442\u0435") ? "\u0432\u043e " + platform : "\u043d\u0430 " + platform;
                String msg = U.colored(T.success(streamer.owner, "\u041d\u043e\u0432\u044b\u0439 \u0441\u0442\u0440\u0438\u043c " + platform + "! \u041d\u0430\u043f\u0438\u0448\u0438\u0442\u0435 &l/streams&a \u0434\u043b\u044f \u043f\u0440\u043e\u0441\u043c\u043e\u0442\u0440\u0430"));
                for (Player player : players) {
                    player.sendMessage(msg);
                }
            }
        }
        if (!this.inv.getViewers().isEmpty()) {
            this.update();
        }
    }

    private void update() {
        if (this.dirty) {
            Collections.sort(this.list);
            int size = Math.max(this.list.size(), this.oldSize);
            for (int i = 9; i < size + 9; ++i) {
                ItemStack is = null;
                if (i - 9 < this.list.size()) {
                    StreamerData streamer = this.list.get(i - 9);
                    String title = streamer.getTitle() == null ? "&b\u0421\u0442\u0440\u0438\u043c " + streamer.owner : "&b" + streamer.getTitle();
                    if (title.length() > 35) {
                        title = title.substring(0, 33) + "...";
                    }
                    ParsedTime time = new ParsedTime((long)(streamer.getDuration() * 1000));
                    String duration = time.days > 0 ? time.days + " \u0434. " + time.hours + " \u0447." : ParsedTime.numToString((int)time.hours, (int)2) + ":" + ParsedTime.numToString((int)time.minutes, (int)2) + " \u0447.";
                    is = Items.name(Material.EMERALD, title, "&f\u0421\u0442\u0440\u0438\u043c\u0435\u0440: &c" + streamer.owner, "&f\u0417\u0440\u0438\u0442\u0435\u043b\u0435\u0439: &a" + streamer.getTotalViewers(), "&f\u0421\u0435\u0440\u0432\u0438\u0441: &e" + streamer.sortedStreams().stream().map(s -> s.platform).distinct().collect(Collectors.joining("&f, &e")), "&f\u0414\u043b\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c: &e" + duration, "", " &a\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u043f\u0440\u043e\u0441\u043c\u043e\u0442\u0440\u0430");
                }
                this.inv.setItem(i, is);
            }
            this.oldSize = this.list.size();
        }
    }

    public void clear() {
        this.list.clear();
        this.byStreamer.clear();
        for (int i = 9; i < this.inv.getSize(); ++i) {
            this.inv.setItem(i, null);
        }
    }

    @Override
    public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
        if (slot > 8) {
            if (this.list.size() <= (slot -= 9)) {
                return;
            }
            List<StreamData> streams = this.list.get((int)slot).streams;
            if (streams.size() == 1) {
                Texteria.openUrl((String)streams.get((int)0).url, (Player[])new Player[]{player});
            } else {
                new PlatformSelectMenu(this.list.get(slot)).show(player);
            }
        }
    }

    @Override
    public void show(Player player) {
        this.update();
        IMenu.super.show(player);
    }

    public Inventory getInventory() {
        return this.inv;
    }

    public static class StreamerData
    implements Comparable<StreamerData> {
        public List<StreamData> streams;
        public String owner;

        public StreamerData(String owner) {
            this.owner = owner;
            this.streams = new LinkedList<StreamData>();
        }

        public int getDuration() {
            return this.streams.stream().mapToInt(s -> s.duration).max().orElse(0);
        }

        public int getTotalViewers() {
            return this.streams.stream().mapToInt(s -> s.viewers).sum();
        }

        public String getTitle() {
            StreamData data = this.sortedStreams().get(0);
            if (data == null || data.title == null) {
                return null;
            }
            return data.title;
        }

        public List<StreamData> sortedStreams() {
            return this.streams;
        }

        public void removeStream(String url) {
            Iterator<StreamData> it = this.streams.iterator();
            while (it.hasNext()) {
                if (!it.next().url.equals(url)) continue;
                it.remove();
                return;
            }
        }

        public void updateStream(Packet57StreamStatus packet) {
            for (StreamData stream : this.streams) {
                if (!stream.url.equals(packet.url)) continue;
                stream.title = packet.title;
                stream.viewers = packet.viewers;
                stream.duration = packet.duration;
                Collections.sort(this.streams);
                return;
            }
            StreamData stream = new StreamData(packet.url, this.owner);
            stream.title = packet.title;
            stream.viewers = packet.viewers;
            stream.platform = packet.platform;
            stream.duration = packet.duration;
            this.streams.add(stream);
            Collections.sort(this.streams);
        }

        public boolean equals(Object obj) {
            return obj instanceof StreamerData && ((StreamerData)obj).owner.equals(this.owner);
        }

        @Override
        public int compareTo(StreamerData o) {
            return o.getTotalViewers() - this.getTotalViewers();
        }
    }

    public static class StreamData
    implements Comparable<StreamData> {
        public String url;
        public String owner;
        public String title;
        public String platform;
        public int viewers = 0;
        public int duration = 0;

        public StreamData(String url, String owner) {
            this.url = url;
            this.owner = owner;
        }

        public boolean equals(Object obj) {
            return obj instanceof StreamData && ((StreamData)obj).url.equals(this.url);
        }

        @Override
        public int compareTo(StreamData o) {
            return o.viewers - this.viewers;
        }
    }

    private static class PlatformSelectMenu
    implements IMenu {
        private Inventory inv;
        public List<StreamData> streams;

        public PlatformSelectMenu(StreamerData streamer) {
            this.inv = Bukkit.createInventory((InventoryHolder)this, (int)9, (String)("\u0421\u0442\u0440\u0438\u043c\u044b " + streamer.owner));
            this.streams = new ArrayList<StreamData>(streamer.sortedStreams());
            for (int i = 0; i < this.streams.size(); ++i) {
                StreamData stream = this.streams.get(i);
                String title = stream.title == null ? "&b\u0421\u0442\u0440\u0438\u043c " + stream.owner : "&b" + stream.title;
                if (title.length() > 35) {
                    title = title.substring(0, 33) + "...";
                }
                ParsedTime time = new ParsedTime((long)stream.duration * 1000L);
                ItemStack is = Items.name(Material.DIAMOND, title, "&f\u0421\u0442\u0440\u0438\u043c\u0435\u0440: &c" + stream.owner, "&f\u0417\u0440\u0438\u0442\u0435\u043b\u0435\u0439: &a" + stream.viewers, "&f\u0421\u0435\u0440\u0432\u0438\u0441: &e" + stream.platform, "&f\u0414\u043b\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c: &e" + ParsedTime.numToString((int)time.hours, (int)2) + ":" + ParsedTime.numToString((int)time.minutes, (int)2) + " \u0447.", "", " &a\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u043f\u0440\u043e\u0441\u043c\u043e\u0442\u0440\u0430");
                this.inv.setItem(i, is);
            }
        }

        @Override
        public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
            if (slot > 8) {
                if (this.streams.size() <= (slot -= 9)) {
                    return;
                }
                Texteria.openUrl((String)this.streams.get((int)slot).url, (Player[])new Player[]{player});
            }
        }

        public Inventory getInventory() {
            return this.inv;
        }
    }
}

