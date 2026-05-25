package net.xtrafrancyz.VimeNetwork.impl.player;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.linked.TIntLinkedList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.xtrafrancyz.Core.network.packet.Packet10PlayerGiveExp;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import org.bukkit.Bukkit;

public class VExpBuffer {
   private final VNPlugin plugin;

   public VExpBuffer(VNPlugin plugin) {
      this.plugin = plugin;
      Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::flush, 200L, 200L);
   }

   private void flush() {
      int total = 0;
      TIntObjectMap<TIntLinkedList> map = new TIntObjectHashMap();

      for(VPlayer player : VPlayer.PLAYERS.values()) {
         if (player.expBuffer > 0) {
            total += player.expBuffer;
            TIntLinkedList list = (TIntLinkedList)map.get(player.expBuffer);
            if (list == null) {
               map.put(player.expBuffer, list = new TIntLinkedList());
            }

            list.add(player.id);
            player.expBuffer = 0;
         }
      }

      VimeNetwork.metrics().add("exp.added", total);
      TIntObjectIterator<TIntLinkedList> it = map.iterator();

      while(it.hasNext()) {
         it.advance();
         this.plugin.core.sendPacket(new Packet10PlayerGiveExp(((TIntLinkedList)it.value()).toArray(), it.key()));
      }

      map.clear();
   }

   public void saveNow(VPlayer player) {
      if (player.expBuffer != 0) {
         this.plugin.core.sendPacket(new Packet10PlayerGiveExp(player.id, player.expBuffer));
         player.expBuffer = 0;
      }

   }

   public void finish() {
      this.flush();
   }
}
