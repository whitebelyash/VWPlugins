/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.event.PlayerLeaveEvent
 *  net.xtrafrancyz.VimeNetwork.api.util.Spectators
 *  net.xtrafrancyz.bukkit.texteria.Texteria2D
 *  net.xtrafrancyz.bukkit.texteria.TexteriaCallbackEvent
 *  net.xtrafrancyz.bukkit.texteria.elements.Element
 *  net.xtrafrancyz.bukkit.texteria.elements.Image
 *  net.xtrafrancyz.bukkit.texteria.elements.ProgressBar
 *  net.xtrafrancyz.bukkit.texteria.elements.Rectangle
 *  net.xtrafrancyz.bukkit.texteria.elements.Text
 *  net.xtrafrancyz.bukkit.texteria.elements.TextTimer
 *  net.xtrafrancyz.bukkit.texteria.utils.Attachment
 *  net.xtrafrancyz.bukkit.texteria.utils.ByteMap
 *  net.xtrafrancyz.bukkit.texteria.utils.IntColor
 *  net.xtrafrancyz.bukkit.texteria.utils.OnClick
 *  net.xtrafrancyz.bukkit.texteria.utils.OnClick$Action
 *  net.xtrafrancyz.bukkit.texteria.utils.Position
 *  net.xtrafrancyz.bukkit.texteria.utils.TexteriaBuffer
 *  org.bukkit.Bukkit
 *  org.bukkit.Color
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityDeathEvent
 *  org.bukkit.event.entity.EntityRegainHealthEvent
 *  org.bukkit.event.player.PlayerRespawnEvent
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.ClashPoint;

import java.util.Set;
import net.xtrafrancyz.ClashPoint.ClashPoint;
import net.xtrafrancyz.ClashPoint.Config;
import net.xtrafrancyz.ClashPoint.object.CPTeam;
import net.xtrafrancyz.ClashPoint.object.PlayerInfo;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLeaveEvent;
import net.xtrafrancyz.VimeNetwork.api.util.Spectators;
import net.xtrafrancyz.bukkit.texteria.Texteria2D;
import net.xtrafrancyz.bukkit.texteria.TexteriaCallbackEvent;
import net.xtrafrancyz.bukkit.texteria.elements.Element;
import net.xtrafrancyz.bukkit.texteria.elements.Image;
import net.xtrafrancyz.bukkit.texteria.elements.ProgressBar;
import net.xtrafrancyz.bukkit.texteria.elements.Rectangle;
import net.xtrafrancyz.bukkit.texteria.elements.Text;
import net.xtrafrancyz.bukkit.texteria.elements.TextTimer;
import net.xtrafrancyz.bukkit.texteria.utils.Attachment;
import net.xtrafrancyz.bukkit.texteria.utils.ByteMap;
import net.xtrafrancyz.bukkit.texteria.utils.IntColor;
import net.xtrafrancyz.bukkit.texteria.utils.OnClick;
import net.xtrafrancyz.bukkit.texteria.utils.Position;
import net.xtrafrancyz.bukkit.texteria.utils.TexteriaBuffer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;

public class TournamentUI
implements Listener {
    public static TournamentUI instance;
    private final ClashPoint plugin;

    public TournamentUI(ClashPoint plugin) {
        this.plugin = plugin;
        instance = this;
        Spectators.instance().addListener((Plugin)plugin, (player, spectator) -> {
            if (spectator) {
                this.showUI(player);
            } else {
                Texteria2D.removeGroup((String)"cp.s.*", (Player[])new Player[]{player});
            }
        });
    }

    public Player[] getWatchers() {
        Set list = Spectators.instance().getSpectators();
        return list.toArray(new Player[list.size()]);
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    private void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> this.updateHealth((Player)event.getEntity()));
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    private void onPlayerRegen(EntityRegainHealthEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> this.updateHealth((Player)event.getEntity()));
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    private void onPlayerDeathMonitor(EntityDeathEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            PlayerInfo info = PlayerInfo.get((Player)event.getEntity());
            if (info.team != null) {
                Texteria2D.add((Element)((Rectangle)((Rectangle)((Rectangle)((Rectangle)new Rectangle(null, 24).setAttachment(new Attachment("cp.s.p." + info.username, Position.TOP_LEFT).setOrientation(Position.BOTTOM_RIGHT).setRemoveWhenParentRemove(false))).setDuration(1000L)).setFadeFinish(900)).setFadeStart(100)).setColor(-769226), (Player[])this.getWatchers());
                this.updateHealth((Player)event.getEntity(), 0.0f);
            }
        }
    }

    @EventHandler(priority=EventPriority.LOW)
    private void onPlayerDeathLow(EntityDeathEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            PlayerInfo info = PlayerInfo.get((Player)event.getEntity());
            if (info.team != null && info.team.getResourcePoints().isEmpty()) {
                Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> this.showUI(this.getWatchers()));
            }
        }
    }

    @EventHandler
    private void onPlayerRespawn(PlayerRespawnEvent event) {
        PlayerInfo info = PlayerInfo.get(event.getPlayer());
        if (info.team != null) {
            this.updateHealth(event.getPlayer(), 1.0f);
        }
    }

    @EventHandler(priority=EventPriority.LOW)
    private void onPlayerLeave(PlayerLeaveEvent event) {
        PlayerInfo info = PlayerInfo.get(event.getPlayer());
        if (info.team != null) {
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> this.showUI(this.getWatchers()));
        }
    }

    @EventHandler
    private void onTexteriaClick(TexteriaCallbackEvent event) {
        String module = event.getData().getString("module");
        if (module != null && module.equals("cp-tournament-ui") && this.plugin.spectators.contains(event.getPlayer())) {
            switch (event.getData().getString("action", "")) {
                case "tp": {
                    Player target = Bukkit.getPlayerExact((String)event.getData().getString("player"));
                    if (target == null) break;
                    event.getPlayer().teleport((Entity)target);
                }
            }
        }
    }

    private void updateHealth(Player player) {
        this.updateHealth(player, (float)(player.getHealth() / player.getMaxHealth()));
    }

    private void updateHealth(Player player, float value) {
        ByteMap map = new ByteMap();
        map.put((Object)"p", (Object)Float.valueOf(value));
        Texteria2D.edit((String)("cp.s.p." + player.getName() + ".hp"), (ByteMap)map, (Player[])this.getWatchers());
    }

    public void showUI(Player ... watchers) {
        Texteria2D.removeGroup((String)"cp.s.*", (Player[])watchers);
        TexteriaBuffer buffer = new TexteriaBuffer();
        buffer.enable();
        block4: for (int i = 0; i < Config.teams.size(); ++i) {
            CPTeam team = Config.teams.get(i);
            int width = team.players.isEmpty() ? 0 : team.players.size() * 25 - 1;
            int height = 3;
            switch (i) {
                case 0: {
                    this.showTeam(buffer, team, (Rectangle)((Rectangle)new Rectangle("cp.s.team0", width, height).setPosition(Position.TOP)).setOffset(team.players.isEmpty() ? -10 : -30 - width / 2, 0));
                    continue block4;
                }
                case 1: {
                    this.showTeam(buffer, team, (Rectangle)((Rectangle)new Rectangle("cp.s.team1", width, height).setPosition(Position.TOP)).setOffset(team.players.isEmpty() ? 10 : 30 + width / 2, 0));
                    continue block4;
                }
                default: {
                    if (i % 2 == 0) {
                        this.showTeam(buffer, team, (Rectangle)((Rectangle)new Rectangle("cp.s.team" + i, width, height).setAttachment(new Attachment("cp.s.team" + (i - 2), Position.LEFT))).setOffset(team.players.isEmpty() ? 0 : -20, 0));
                        continue block4;
                    }
                    this.showTeam(buffer, team, (Rectangle)((Rectangle)new Rectangle("cp.s.team" + i, width, height).setAttachment(new Attachment("cp.s.team" + (i - 2), Position.RIGHT))).setOffset(team.players.isEmpty() ? 0 : 20, 0));
                }
            }
        }
        buffer.add(((Text)((Text)((Text)((Text)new TextTimer("cp.s.timer", new String[]{"{M}:{SS}"}).setFade(0)).setScale(2.0f)).setOffset(0, 6)).setDuration(this.plugin.game.endTime - System.currentTimeMillis())).setPosition(Position.TOP), new Player[0]);
        buffer.send(watchers);
    }

    private void showTeam(TexteriaBuffer buffer, CPTeam team, Rectangle bound) {
        Attachment attachment = new Attachment(bound.id, Position.BOTTOM_LEFT).setOrientation(Position.BOTTOM_RIGHT).setRemoveWhenParentRemove(false);
        for (int i = 0; i < team.players.size(); ++i) {
            PlayerInfo info = team.players.get(i);
            String playerId = "cp.s.p." + info.username;
            ByteMap clickData = new ByteMap();
            clickData.put((Object)"module", (Object)"cp-tournament-ui");
            clickData.put((Object)"action", (Object)"tp");
            clickData.put((Object)"player", (Object)info.username);
            buffer.add(((Rectangle)((Rectangle)((Rectangle)new Image(playerId, 24, "http://skin.vimeworld.ru/head/" + info.username + "/8.png").setOffset(25 * i, 1)).setAttachment(attachment)).setFade(0)).setOnClick(new OnClick(OnClick.Action.CALLBACK, (Object)clickData)), new Player[0]);
            buffer.add(((Rectangle)((Rectangle)((Rectangle)new ProgressBar(playerId + ".hp", 24, 2, (float)(info.player.getHealth() / info.player.getMaxHealth())).setBarColor(IntColor.setAlpha((int)-10395295, (int)150)).setColor(-8978685)).setOffset(25 * i, 26)).setAttachment(attachment)).setFade(0), new Player[0]);
        }
        bound.setColor(this.getTeamColor(team.color));
        bound.setFade(0);
        buffer.add((Element)bound, new Player[0]);
    }

    private int getTeamColor(Color color) {
        if (color == Color.RED) {
            return -769226;
        }
        if (color == Color.BLUE) {
            return -14575885;
        }
        if (color == Color.GREEN) {
            return -8978685;
        }
        return -16777216 + color.asRGB();
    }
}

