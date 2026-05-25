package net.xtrafrancyz.bukkit.minidot;

import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLeaveEvent;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLoadedEvent;
import net.xtrafrancyz.bukkit.minidot.database.MiniDotPlayer;
import net.xtrafrancyz.bukkit.minidot.database.MiniDotRepository;
import net.xtrafrancyz.bukkit.minidot.database.PlayerSaver;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class MiniDot extends JavaPlugin implements Listener, PluginMessageListener {
   private static final boolean DEBUG = false;
   private static MiniDot instance;
   public MiniDotRepository database;
   public PlayerSaver playerSaver;

   public void onEnable() {
      instance = this;
      Messenger.plugin = this;
      this.database = new MiniDotRepository();
      if (!VimeNetwork.core().isEnabled()) {
         this.playerSaver = new PlayerSaver();
         this.playerSaver.start();
      }

      this.getServer().getMessenger().registerOutgoingPluginChannel(this, "MiniDotMod");
      this.getServer().getMessenger().registerIncomingPluginChannel(this, "MiniDotMod", this);
      this.getServer().getPluginManager().registerEvents(this, this);
   }

   public void onDisable() {
      if (!VimeNetwork.core().isEnabled()) {
         this.playerSaver.interrupt();
      }

   }

   public static void debug(String msg) {
   }

   public static MiniDot instance() {
      return instance;
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent event) {
      ((CraftPlayer)event.getPlayer()).addChannel("MiniDotMod");
   }

   @EventHandler
   public void onPlayerLoaded(PlayerLoadedEvent event) {
      Messenger.sendAllInfoToPlayer(event.getPlayer());
      Messenger.sendDiscountsForPlayer(event.getPlayer());
      this.database.loadPlayer(event.getPlayer());
   }

   @EventHandler
   public void onPlayerLeave(PlayerLeaveEvent event) {
      this.database.unloadPlayer(event.getPlayer().getName());
   }

   public void onPluginMessageReceived(String channel, Player player, byte[] data) {
      if (channel.equals("MiniDotMod")) {
         MiniDotPlayer pi = this.database.getPlayer(player.getName());
         if (pi != null) {
            Messenger.recievePacket(pi, data);
         }
      }

   }
}
