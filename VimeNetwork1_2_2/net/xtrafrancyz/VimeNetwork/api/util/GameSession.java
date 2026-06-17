/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.server.PluginDisableEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.api.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import net.xtrafrancyz.Core.network.packet.Packet53Answer;
import net.xtrafrancyz.Core.network.packet.Packet64SessionCreate;
import net.xtrafrancyz.Core.network.packet.Packet65SessionRemovePlayer;
import net.xtrafrancyz.Core.network.packet.Packet66SessionEnd;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.event.NPCInteractEvent;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3f;
import net.xtrafrancyz.VimeNetwork.api.holo.Hologram;
import net.xtrafrancyz.VimeNetwork.api.npc.NPC;
import net.xtrafrancyz.VimeNetwork.api.util.Spectators;
import net.xtrafrancyz.VimeNetwork.commands.VanishCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class GameSession
implements Listener {
    private final Plugin plugin;
    private final Map<String, Leaver> leavers;
    private String sessionId = null;
    private Function<Player, String> playerNameTransformer = HumanEntity::getName;
    private BiConsumer<Player, Leaver> playerSaver = null;
    private BiConsumer<Player, Leaver> playerRestorer = null;
    private BiPredicate<Player, Leaver> damageFilter = (damager, leaver) -> true;
    private BiConsumer<Player, Leaver> onLeaverKill = null;

    public GameSession(Plugin plugin) {
        this.plugin = plugin;
        this.leavers = new HashMap<String, Leaver>();
        Bukkit.getPluginManager().registerEvents((Listener)this, plugin);
    }

    public void setPlayerNameTransformer(Function<Player, String> transformer) {
        this.playerNameTransformer = transformer;
    }

    public void setPlayerSaver(BiConsumer<Player, Leaver> saver) {
        this.playerSaver = saver;
    }

    public void setPlayerRestorer(BiConsumer<Player, Leaver> restorer) {
        this.playerRestorer = restorer;
    }

    public void setLeaverKillListener(BiConsumer<Player, Leaver> listener) {
        this.onLeaverKill = listener;
    }

    public void setDamageFilter(BiPredicate<Player, Leaver> filter) {
        this.damageFilter = filter;
    }

    public void savePlayer(Player player) {
        this.leavers.put(player.getName(), new Leaver(this, player));
    }

    public boolean hasPlayer(Player player) {
        return this.leavers.containsKey(player.getName());
    }

    public Leaver getPlayer(Player player) {
        return this.leavers.get(player.getName());
    }

    public void restorePlayer(Player player) {
        Leaver leaver = this.leavers.get(player.getName());
        if (leaver != null) {
            if (leaver.hologram != null) {
                leaver.npc.remove();
                leaver.hologram.remove();
            }
            this.playerRestorer.accept(player, leaver);
            if (leaver.inventory != null) {
                player.getInventory().setContents(leaver.inventory);
            }
            if (leaver.armor != null) {
                player.getInventory().setArmorContents(leaver.armor);
            }
            if (leaver.location != null) {
                player.teleport(leaver.location);
            }
            player.setMaxHealth(leaver.maxHealth);
            player.setHealth(leaver.health);
            player.setSaturation(leaver.saturation);
            player.setFoodLevel(leaver.foodLevel);
            player.setFallDistance(leaver.fallDistance);
        }
    }

    public void create(Collection<Player> players) {
        int[] ids = players.stream().mapToInt(p -> VimeNetwork.getPlayer(p).getId()).toArray();
        VimeNetwork.core().sendPacket(new Packet64SessionCreate(ids), packet0 -> {
            this.sessionId = ((Packet53Answer)packet0).status;
        }, 1000L, () -> this.plugin.getLogger().warning("Failed to create game session. Timeout"));
    }

    public void removePlayer(int userid) {
        if (this.sessionExists()) {
            VimeNetwork.core().sendPacket(new Packet65SessionRemovePlayer(this.sessionId, userid));
        }
    }

    public boolean endSession() {
        if (this.sessionExists()) {
            VimeNetwork.core().sendPacket(new Packet66SessionEnd(this.sessionId));
            this.sessionId = null;
            return true;
        }
        return false;
    }

    public boolean sessionExists() {
        return this.sessionId != null;
    }

    @EventHandler
    public void onNPCInteract(NPCInteractEvent event) {
        if (event.getAction() == NPCInteractEvent.Action.LEFT_CLICK) {
            if (Spectators.instance().contains(event.getPlayer())) {
                return;
            }
            for (Leaver leaver : this.leavers.values()) {
                if (leaver.npc == null || leaver.npc != event.getNpc()) continue;
                if (!this.damageFilter.test(event.getPlayer(), leaver)) {
                    return;
                }
                leaver.npc.remove();
                leaver.hologram.remove();
                leaver.inventory = new ItemStack[0];
                leaver.armor = new ItemStack[0];
                leaver.location = null;
                leaver.killed = true;
                if (this.onLeaverKill == null) break;
                this.onLeaverKill.accept(event.getPlayer(), leaver);
                break;
            }
        }
    }

    @EventHandler
    private void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin() == this.plugin) {
            this.leavers.clear();
            this.endSession();
        }
    }

    public static class Leaver {
        public String username;
        public double health;
        public double maxHealth;
        public int foodLevel;
        public float saturation;
        public float fallDistance;
        public Location location;
        public ItemStack[] armor;
        public ItemStack[] inventory;
        public boolean killed;
        public final Map<String, Object> customData;
        private NPC npc;
        private Hologram hologram;

        public Leaver(GameSession session, Player player) {
            this.username = player.getName();
            this.customData = new HashMap<String, Object>();
            this.killed = player.isDead();
            if (!this.killed) {
                VanishCommand.VanishData data;
                int heldSlot = 0;
                if (Spectators.instance().contains(player) && (data = VNPlugin.instance().vanishCommand.data.get(player.getName())) != null) {
                    this.location = data.lastLoc;
                    this.armor = data.armor;
                    this.inventory = data.inventory;
                    this.health = data.health;
                    this.maxHealth = data.maxHealth;
                }
                if (this.location == null) {
                    this.location = player.getLocation();
                    this.armor = player.getInventory().getArmorContents();
                    this.inventory = player.getInventory().getContents();
                    this.health = player.getHealth();
                    this.maxHealth = player.getMaxHealth();
                    heldSlot = player.getInventory().getHeldItemSlot();
                }
                this.saturation = player.getSaturation();
                this.foodLevel = player.getFoodLevel();
                this.fallDistance = player.getFallDistance();
                this.npc = VimeNetwork.npcs().create((String)session.playerNameTransformer.apply(player), this.location);
                this.npc.setHelmet(this.armor[3]);
                this.npc.setChestplate(this.armor[2]);
                this.npc.setLeggings(this.armor[1]);
                this.npc.setBoots(this.armor[0]);
                this.npc.setItemInHand(this.inventory[heldSlot]);
                this.hologram = VimeNetwork.holograms().createText(new Vec3f(this.location).add(0.0f, 2.5f, 0.0f), "&c&l\u0412\u042b\u0428\u0415\u041b");
            }
            if (session.playerSaver != null) {
                session.playerSaver.accept(player, this);
            }
        }
    }
}

