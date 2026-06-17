/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.bukkit.texteria.Texteria2D
 *  net.xtrafrancyz.bukkit.texteria.elements.Element
 *  net.xtrafrancyz.bukkit.texteria.elements.Text
 *  net.xtrafrancyz.bukkit.texteria.utils.Position
 *  net.xtrafrancyz.bukkit.texteria.utils.Visibility
 *  net.xtrafrancyz.bukkit.texteria.utils.Visibility$IngameNotF3
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.core;

import java.util.ArrayList;
import java.util.function.Function;
import net.xtrafrancyz.Commons.Leveling;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.Commons.guild.GuildPerk;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet;
import net.xtrafrancyz.Core.network.packet.Packet11PlayerGiveExpSimple;
import net.xtrafrancyz.Core.network.packet.Packet1PlayerInfo;
import net.xtrafrancyz.Core.network.packet.Packet301Subscribe;
import net.xtrafrancyz.Core.network.packet.Packet302Unsubscribe;
import net.xtrafrancyz.Core.network.packet.Packet51ChatMessage;
import net.xtrafrancyz.Core.network.packet.Packet52CustomMessage;
import net.xtrafrancyz.Core.network.packet.Packet54PrivateMessage;
import net.xtrafrancyz.Core.network.packet.Packet57StreamStatus;
import net.xtrafrancyz.Core.network.packet.Packet59Party;
import net.xtrafrancyz.Core.network.packet.Packet5PlayerCoinsChange;
import net.xtrafrancyz.Core.network.packet.Packet60PartyInvite;
import net.xtrafrancyz.Core.network.packet.Packet63FriendRequest;
import net.xtrafrancyz.Core.network.packet.Packet69Guild;
import net.xtrafrancyz.Core.network.packet.Packet6PlayerMetaChange;
import net.xtrafrancyz.VimeNetwork.Debug;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.VTexteria;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.event.CoreCustomMessageEvent;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLoadedEvent;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.player.CorePlayer;
import net.xtrafrancyz.VimeNetwork.impl.player.VParty;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import net.xtrafrancyz.VimeNetwork.impl.player.guild.GuildMemberMenu;
import net.xtrafrancyz.VimeNetwork.impl.player.guild.VGuild;
import net.xtrafrancyz.bukkit.texteria.Texteria2D;
import net.xtrafrancyz.bukkit.texteria.elements.Element;
import net.xtrafrancyz.bukkit.texteria.elements.Text;
import net.xtrafrancyz.bukkit.texteria.utils.Position;
import net.xtrafrancyz.bukkit.texteria.utils.Visibility;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

public class BukkitPacketHandler
extends PacketHandler {
    private final VNPlugin plugin;

    public BukkitPacketHandler(VNPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(Packet packet) {
        if (Debug.CORE.isEnabled()) {
            this.plugin.getLogger().info("[Core] -> " + packet.toString());
        }
    }

    @Override
    public void handle1PlayerInfo(Packet1PlayerInfo packet) {
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> {
            if (!VimeNetwork.isPlayerOnline(packet.username)) {
                return;
            }
            CorePlayer player = (CorePlayer)VPlayer.get(packet.username);
            player.id = packet.id;
            player.coins = packet.coins;
            VPlayer.IDS.put(player.id, player);
            if (!player.loaded) {
                player.exp = packet.exp;
                player.level = Leveling.getLevel(player.exp);
            } else {
                int received = packet.exp - player.exp - player.expBuffer;
                if (received > 0) {
                    player.giveExp(received);
                    player.expBuffer -= received;
                }
            }
            if ((packet.queryFlags & 4) == 4) {
                Rank rank = Rank.getRank(packet.rank);
                if (player.loaded && rank != player.rank) {
                    player.getTag().refresh();
                }
                player.rank = rank;
            }
            if ((packet.queryFlags & 1) == 1) {
                player.meta.clear();
                player.meta.putAll(packet.meta);
                player.onMetaLoaded();
            }
            if ((packet.queryFlags & 0x20) == 32) {
                player.stats.load(packet);
            }
            if ((packet.queryFlags & 0x40) == 64) {
                player.achievements.load(packet);
            }
            if ((packet.queryFlags & 0x100) == 256) {
                if (player.guild != null) {
                    player.guild.removePlayer(player);
                }
                if (packet.guildId == -1) {
                    player.guild = null;
                    if (player.loaded) {
                        player.getTag().refresh();
                    }
                } else {
                    player.guild = VGuild.CACHE.computeIfAbsent(packet.guildId, VGuild::new);
                    player.guild.addPlayer(player);
                }
            }
            player.player.setDisplayName(player.getPrefixedName());
            VTexteria.showCoins(player);
            if (VimeNetwork.features().CHANGE_PLAYER_LIST_NAMES.isEnabled()) {
                String name = player.rank.getColor() + player.username;
                if (name.length() > 16) {
                    name = name.substring(0, 15);
                }
                player.player.setPlayerListName(name);
            }
            if (!player.loaded) {
                player.loaded = true;
                Bukkit.getPluginManager().callEvent((Event)new PlayerLoadedEvent(player, packet.switchData));
            }
        });
    }

    @Override
    public void handle5PlayerCoinsChange(Packet5PlayerCoinsChange packet) {
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> {
            for (int userid : packet.users) {
                VPlayer player = VPlayer.IDS.get(userid);
                if (player == null) continue;
                player.coins += packet.change;
                if (packet.change < 0) {
                    this.plugin.coins.takeCoins(player, -packet.change, true);
                    continue;
                }
                this.plugin.coins.addCoins(player, packet.change, true);
            }
        });
    }

    @Override
    public void handle6PlayerMetaChange(Packet6PlayerMetaChange packet) {
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> {
            VPlayer player = VPlayer.IDS.get(packet.userid);
            if (player == null) {
                return;
            }
            ((CorePlayer)player).meta.put(packet.key, packet.value);
            player.onMetaUpdate(packet.key, packet.value);
        });
    }

    @Override
    public void handle11PlayerGiveExpSimple(Packet11PlayerGiveExpSimple packet) {
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> {
            VPlayer player = VPlayer.IDS.get(packet.userid);
            if (player == null) {
                return;
            }
            player.exp += packet.exp;
            if (player.exp >= Leveling.getTotalExp(player.level + 1)) {
                player.level = Leveling.getLevel(player.exp);
                VTexteria.showUsername(player);
            }
        });
    }

    @Override
    public void handle51ChatMessage(Packet51ChatMessage packet) {
        String message = U.colored(packet.message);
        for (String receiver : packet.receivers) {
            Player player = Bukkit.getPlayerExact((String)receiver);
            if (player == null) continue;
            player.sendMessage(message);
        }
    }

    @Override
    public void handle52CustomMessage(Packet52CustomMessage packet) {
        CoreCustomMessageEvent event = new CoreCustomMessageEvent(packet);
        Bukkit.getPluginManager().callEvent((Event)event);
        if (event.isCancelled()) {
            return;
        }
        switch (packet.tag) {
            case "cmd": {
                Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), (String)packet.data.getString("cmd")));
                break;
            }
            case "bcast": {
                U.bcast(packet.data.getString("message"));
            }
        }
    }

    @Override
    public void handle54PrivateMessage(Packet54PrivateMessage packet) {
        Player player = Bukkit.getPlayerExact((String)packet.receiver);
        if (player != null) {
            player.sendMessage(U.colored("&e[&f" + packet.sender + "&e -> &f\u0412\u044b&e] ") + packet.message);
            Bukkit.getLogger().info("[" + packet.sender + " -> " + player.getName() + "] " + packet.message);
        }
    }

    @Override
    public void handle57StreamStatus(Packet57StreamStatus packet) {
        this.plugin.streamMenu.update(packet);
    }

    @Override
    public void handle60PartyInvite(Packet60PartyInvite packet) {
        if (!VimeNetwork.isPlayerOnline(packet.username)) {
            return;
        }
        VPlayer player = VPlayer.get(packet.username);
        if (!player.settings.get(0)) {
            return;
        }
        U.msg((CommandSender)player.player, T.success("&d\u0413\u0440\u0443\u043f\u043f\u0430", "\u0418\u0433\u0440\u043e\u043a &f" + packet.inviter + "&a \u043f\u0440\u0438\u0433\u043b\u0430\u0448\u0430\u0435\u0442 \u0432\u0430\u0441 \u0432 \u0441\u0432\u043e\u044e \u0433\u0440\u0443\u043f\u043f\u0443. \u0423 \u0432\u0430\u0441 \u0435\u0441\u0442\u044c &f60 \u0441\u0435\u043a\u0443\u043d\u0434&a \u043d\u0430 \u043e\u0442\u0432\u0435\u0442"));
        U.msg((CommandSender)player.player, T.success("&d\u0413\u0440\u0443\u043f\u043f\u0430", "\u0414\u043b\u044f \u043f\u0440\u0438\u043d\u044f\u0442\u0438\u044f \u043f\u0440\u0438\u0433\u043b\u0430\u0448\u0435\u043d\u0438\u044f \u043d\u0430\u043f\u0438\u0448\u0438\u0442\u0435: &f/party join " + packet.inviter));
        VTexteria.showPartyInvite(player.player, packet.inviter);
    }

    @Override
    public void handle59Party(Packet59Party packet) {
        if (!VimeNetwork.isPlayerOnline(packet.username)) {
            return;
        }
        VPlayer player = VPlayer.get(packet.username);
        if (packet.leader != null) {
            player.party = new VParty(packet.leader, packet.players);
            player.getAchievements().complete(Achievement.GLOBAL_PARTY);
            if (player.party.size() >= 5) {
                player.getAchievements().complete(Achievement.GLOBAL_PARTY_5);
            }
            Function<Packet59Party.PartyPlayer, String> nameFormatter = member -> "[&e" + member.level + "&f] " + member.rank.getColor() + member.username;
            packet.players.sort((p1, p2) -> {
                int diff = p2.level - p1.level;
                if (diff != 0) {
                    return diff;
                }
                int r = p2.rank.compareTo(p1.rank);
                if (r != 0) {
                    return r;
                }
                return p1.username.compareToIgnoreCase(p2.username);
            });
            ArrayList<String> list = new ArrayList<String>(player.party.size());
            if (!player.isPartyLeader()) {
                list.add(nameFormatter.apply(packet.leader) + " &c\u2603");
                for (Packet59Party.PartyPlayer member2 : packet.players) {
                    if (member2.username.equals(packet.username)) continue;
                    list.add(nameFormatter.apply(member2));
                }
            } else {
                for (Packet59Party.PartyPlayer member3 : packet.players) {
                    list.add(nameFormatter.apply(member3));
                }
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> Texteria2D.add((Element)((Text)((Text)new Text("vn.p", list.toArray(new String[list.size()])).setPosition(Position.TOP_LEFT)).setOrientation(1).setVisibility((Visibility)new Visibility.IngameNotF3())).setOffset(2, 22), (Player[])new Player[]{player.getBukkitPlayer()}));
        } else {
            player.party = null;
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> Texteria2D.remove((String)"vn.p", (Player[])new Player[]{player.getBukkitPlayer()}));
        }
    }

    @Override
    public void handle63FriendRequest(Packet63FriendRequest packet) {
        if (!VimeNetwork.isPlayerOnline(packet.username)) {
            return;
        }
        VPlayer player = VPlayer.get(packet.username);
        if (!player.settings.get(4)) {
            return;
        }
        U.msg((CommandSender)player.player, T.success("&2\u0414\u0440\u0443\u0437\u044c\u044f", "\u0418\u0433\u0440\u043e\u043a &f" + packet.requester + "&a \u0445\u043e\u0447\u0435\u0442 \u0434\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u0432\u0430\u0441 \u0432 \u0434\u0440\u0443\u0437\u044c\u044f. \u0423 \u0432\u0430\u0441 \u0435\u0441\u0442\u044c &f5 \u043c\u0438\u043d\u0443\u0442&a \u043d\u0430 \u043e\u0442\u0432\u0435\u0442"), T.success("&2\u0414\u0440\u0443\u0437\u044c\u044f", "\u0414\u043b\u044f \u043f\u0440\u0438\u043d\u044f\u0442\u0438\u044f \u0437\u0430\u043f\u0440\u043e\u0441\u0430 \u043d\u0430\u043f\u0438\u0448\u0438\u0442\u0435: &f/friend accept " + packet.requester));
        VTexteria.showFriendRequest(player.player, packet.requester);
    }

    @Override
    public void handle69Guild(Packet69Guild packet) {
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> {
            if (packet.action == Packet69Guild.Action.UPDATE || packet.action == Packet69Guild.Action.OPEN_MENU) {
                VGuild guild = VGuild.CACHE.computeIfAbsent(packet.data.getInt("id"), VGuild::new);
                switch (packet.action) {
                    case UPDATE: {
                        guild.tag = packet.data.containsKey("tag") ? packet.data.getString("tag") : null;
                        guild.name = packet.data.getString("name");
                        if (packet.data.containsKey("color")) {
                            guild.color = U.colored("&" + packet.data.getString("color"));
                        }
                        guild.coinsMultiplier = GuildPerk.getCoinsMultiplier(packet.data.getByte("cm"));
                        if (guild.coinsMultiplier > 0.0f) {
                            for (NetworkPlayer player : guild.getOnlinePlayers()) {
                                VPlayer impl = (VPlayer)player;
                                if (impl.getMultipliers().getCurrentMultiplier() == impl.coinsTexteriaMult) continue;
                                VTexteria.showCoins(impl);
                            }
                        }
                        for (NetworkPlayer player : guild.getOnlinePlayers()) {
                            player.getTag().refresh();
                        }
                        break;
                    }
                    case OPEN_MENU: {
                        NetworkPlayer player = VimeNetwork.getPlayer(packet.userid);
                        if (player == null) {
                            return;
                        }
                        guild.fillData(packet);
                        GuildMemberMenu.openMenu(player, packet);
                        break;
                    }
                }
            }
            switch (packet.action) {
                case INVITE: {
                    NetworkPlayer player = VimeNetwork.getPlayer(packet.userid);
                    if (player == null) {
                        return;
                    }
                    VPlayer impl = (VPlayer)player;
                    if (!impl.settings.get(7)) break;
                    String inviter = packet.data.getString("inviter");
                    String name = packet.data.getString("name");
                    U.msg((CommandSender)player.getBukkitPlayer(), T.success("&2\u0413\u0438\u043b\u044c\u0434\u0438\u044f", "\u0412\u0430\u0441 \u043f\u0440\u0438\u0433\u043b\u0430\u0441\u0438\u043b\u0438 \u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u044e &f" + name + "&a, \u043d\u0430\u043f\u0438\u0448\u0438\u0442\u0435 /g accept " + inviter));
                    VTexteria.showGuildInvite(player.getBukkitPlayer(), inviter, name);
                    break;
                }
            }
        });
    }

    @Override
    public void handle301Subscribe(Packet301Subscribe packet) {
        String[] stringArray = packet.events;
        int n = stringArray.length;
        for (int i = 0; i < n; ++i) {
            String event;
            switch (event = stringArray[i]) {
                case "console.log": {
                    this.plugin.core.logHandler.active = true;
                    this.plugin.getLogger().info("Log is now sending to core");
                }
            }
        }
    }

    @Override
    public void handle302Unsubscribe(Packet302Unsubscribe packet) {
        switch (packet.event) {
            case "console.log": {
                this.plugin.core.logHandler.active = false;
                this.plugin.getLogger().info("Log is not sending to core anymore");
            }
        }
    }
}

