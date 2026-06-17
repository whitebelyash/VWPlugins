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

public class GameSession implements Listener {
   private final Plugin plugin;
   private final Map leavers;
   private String sessionId = null;
   private Function playerNameTransformer = HumanEntity::getName;
   private BiConsumer playerSaver = null;
   private BiConsumer playerRestorer = null;
   private BiPredicate damageFilter = (damager, leaver) -> true;
   private BiConsumer onLeaverKill = null;

   public GameSession(Plugin plugin) {
      this.plugin = plugin;
      this.leavers = new HashMap();
      Bukkit.getPluginManager().registerEvents(this, plugin);
   }

   public void setPlayerNameTransformer(Function transformer) {
      this.playerNameTransformer = transformer;
   }

   public void setPlayerSaver(BiConsumer saver) {
      this.playerSaver = saver;
   }

   public void setPlayerRestorer(BiConsumer restorer) {
      this.playerRestorer = restorer;
   }

   public void setLeaverKillListener(BiConsumer listener) {
      this.onLeaverKill = listener;
   }

   public void setDamageFilter(BiPredicate filter) {
      this.damageFilter = filter;
   }

   public void savePlayer(Player player) {
      this.leavers.put(player.getName(), new Leaver(this, player));
   }

   public boolean hasPlayer(Player player) {
      return this.leavers.containsKey(player.getName());
   }

   public Leaver getPlayer(Player player) {
      return (Leaver)this.leavers.get(player.getName());
   }

   public void restorePlayer(Player player) {
      Leaver leaver = (Leaver)this.leavers.get(player.getName());
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

   public void create(Collection players) {
      int[] ids = players.stream().mapToInt((p) -> VimeNetwork.getPlayer(p).getId()).toArray();
      VimeNetwork.core().sendPacket(new Packet64SessionCreate(ids), (packet0) -> this.sessionId = ((Packet53Answer)packet0).status, 1000L, () -> this.plugin.getLogger().warning("Failed to create game session. Timeout"));
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
      } else {
         return false;
      }
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

         for(Leaver leaver : this.leavers.values()) {
            if (leaver.npc != null && leaver.npc == event.getNpc()) {
               if (!this.damageFilter.test(event.getPlayer(), leaver)) {
                  return;
               }

               leaver.npc.remove();
               leaver.hologram.remove();
               leaver.inventory = new ItemStack[0];
               leaver.armor = new ItemStack[0];
               leaver.location = null;
               leaver.killed = true;
               if (this.onLeaverKill != null) {
                  this.onLeaverKill.accept(event.getPlayer(), leaver);
               }
               break;
            }
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
      public final Map customData;
      private NPC npc;
      private Hologram hologram;

      public Leaver(GameSession session, Player player) {
         this.username = player.getName();
         this.customData = new HashMap();
         this.killed = player.isDead();
         if (!this.killed) {
            int heldSlot = 0;
            if (Spectators.instance().contains(player)) {
               VanishCommand.VanishData data = (VanishCommand.VanishData)VNPlugin.instance().vanishCommand.data.get(player.getName());
               if (data != null) {
                  this.location = data.lastLoc;
                  this.armor = data.armor;
                  this.inventory = data.inventory;
                  this.health = data.health;
                  this.maxHealth = data.maxHealth;
               }
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
            this.hologram = VimeNetwork.holograms().createText((new Vec3f(this.location)).add(0.0F, 2.5F, 0.0F), "&c&lВЫШЕЛ");
         }

         if (session.playerSaver != null) {
            session.playerSaver.accept(player, this);
         }

      }
   }
}
