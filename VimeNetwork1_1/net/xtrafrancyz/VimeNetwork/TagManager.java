package net.xtrafrancyz.VimeNetwork;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.function.Consumer;
import net.minecraft.server.v1_6_R3.Packet20NamedEntitySpawn;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLeaveEvent;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLoadedEvent;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.packet.OutgoingPacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class TagManager implements Listener, Consumer {
   public TIntObjectMap idToPlayer = new TIntObjectHashMap();

   public TagManager(VNPlugin plugin) {
      plugin.packets.addOutgoingListener(plugin, this);
   }

   public void accept(OutgoingPacketEvent event) {
      if (event.getPacket() instanceof Packet20NamedEntitySpawn) {
         Packet20NamedEntitySpawn packet = (Packet20NamedEntitySpawn)event.getPacket();
         NetworkPlayer player = (NetworkPlayer)this.idToPlayer.get(packet.a);
         if (player != null) {
            packet.b = player.getTag();
         }
      }

   }

   public void updateName(Player player) {
      for(Player seer : Bukkit.getOnlinePlayers()) {
         if (seer.canSee(player)) {
            seer.hidePlayer(player);
            seer.showPlayer(player);
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onPlayerLoaded(PlayerLoadedEvent event) {
      this.idToPlayer.put(event.getPlayer().getEntityId(), event.getNetworkPlayer());
      if (event.getNetworkPlayer().hasTag()) {
         this.updateName(event.getPlayer());
      }

   }

   @EventHandler
   public void onPlayerLeave(PlayerLeaveEvent event) {
      this.idToPlayer.remove(event.getPlayer().getEntityId());
   }
}
