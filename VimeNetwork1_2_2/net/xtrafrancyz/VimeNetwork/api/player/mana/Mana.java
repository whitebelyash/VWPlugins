/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.bukkit.texteria.Texteria2D
 *  net.xtrafrancyz.bukkit.texteria.elements.Element
 *  net.xtrafrancyz.bukkit.texteria.elements.ProgressBar
 *  net.xtrafrancyz.bukkit.texteria.elements.Rectangle
 *  net.xtrafrancyz.bukkit.texteria.elements.Text
 *  net.xtrafrancyz.bukkit.texteria.utils.ByteMap
 *  net.xtrafrancyz.bukkit.texteria.utils.Position
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.api.player.mana;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLeaveEvent;
import net.xtrafrancyz.VimeNetwork.api.player.mana.PlayerMana;
import net.xtrafrancyz.bukkit.texteria.Texteria2D;
import net.xtrafrancyz.bukkit.texteria.elements.Element;
import net.xtrafrancyz.bukkit.texteria.elements.ProgressBar;
import net.xtrafrancyz.bukkit.texteria.elements.Rectangle;
import net.xtrafrancyz.bukkit.texteria.elements.Text;
import net.xtrafrancyz.bukkit.texteria.utils.ByteMap;
import net.xtrafrancyz.bukkit.texteria.utils.Position;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class Mana
implements Listener {
    private static final String T_BAR = "-mn";
    private static final String T_TEXT = "-mnt";
    final Plugin plugin;
    Map<String, PlayerMana> players;

    public Mana(Plugin plugin) {
        this.plugin = plugin;
        this.players = new ConcurrentHashMap<String, PlayerMana>();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::regen, 20L, 20L);
    }

    private void regen() {
        for (PlayerMana mana : this.players.values()) {
            if (mana.mana == mana.max) continue;
            mana.add(mana.regen);
        }
    }

    void addTexteria(PlayerMana player) {
        Texteria2D.add((Element)((Rectangle)((Rectangle)new ProgressBar(T_BAR, 80, 7, (float)player.mana / (float)player.max).setBarColor(-12763843).setBorderColor(-16777216).setColor(-16537100)).setOffset(50, 41)).setPosition(Position.BOTTOM), (Player[])new Player[]{player.player});
        if (player.showText) {
            Texteria2D.add((Element)((Text)new Text(T_TEXT, new String[]{"MP: " + player.mana + "/" + player.max}).setOffset(50, 40)).setWidth(80).setPosition(Position.BOTTOM), (Player[])new Player[]{player.player});
        }
    }

    void updateTexteria(PlayerMana player) {
        ByteMap data;
        if (player.showText) {
            data = new ByteMap();
            data.put((Object)".text", (Object)new String[]{"MP: " + player.mana + "/" + player.max});
            Texteria2D.edit((String)T_TEXT, (ByteMap)data, (Player[])new Player[]{player.player});
        }
        data = new ByteMap();
        data.put((Object)"p", (Object)Float.valueOf((float)player.mana / (float)player.max));
        Texteria2D.edit((String)T_BAR, (ByteMap)data, (Player[])new Player[]{player.player});
    }

    public void add(Player player, PlayerMana mana) {
        mana.player = player;
        mana.controller = this;
        this.players.put(player.getName(), mana);
        this.addTexteria(mana);
    }

    public void remove(Player player) {
        if (this.players.remove(player.getName()) != null) {
            Texteria2D.removeGroup((String)T_BAR, (Player[])new Player[]{player});
        }
    }

    public PlayerMana of(Player player) {
        return this.players.get(player.getName());
    }

    public PlayerMana of(String player) {
        return this.players.get(player);
    }

    @EventHandler
    public void onPlayerLeave(PlayerLeaveEvent event) {
        this.players.remove(event.getPlayer().getName());
    }
}

