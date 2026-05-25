package net.xtrafrancyz.bukkit.minidot.database;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.bukkit.minidot.MiniDot;

public class PlayerSaver extends Thread {
   private Queue queue = new ConcurrentLinkedQueue();

   public PlayerSaver() {
      this.setName("Queries Timer Thread");
      this.setDaemon(true);
   }

   public void save(MiniDotPlayer pi) {
      Stream var10000 = this.queue.stream().filter((qq) -> qq.player.username.equals(pi.username));
      Queue var10001 = this.queue;
      var10000.forEach(var10001::remove);
      boolean first = true;
      StringBuilder sb = new StringBuilder("UPDATE minidot_dressed SET ");

      for(MiniDotItem.Slot slot : MiniDotItem.Slot.values()) {
         Integer id = (Integer)pi.dressed.get(slot.getId());
         if (id == null) {
            id = -1;
         }

         if (!first) {
            sb.append(",");
         } else {
            first = false;
         }

         sb.append(slot.getId()).append('=').append(id);
      }

      sb.append(" WHERE userId = ").append(pi.getId());
      this.queue.add(new QueryWithTimer(10000L, sb.toString(), pi));
   }

   public void saveNow(MiniDotPlayer pi) {
      this.queue.stream().filter((qq) -> qq.player.username.equals(pi.username)).forEach((q) -> {
         this.queue.remove(q);
         VimeNetwork.mysql().query(q.query);
      });
   }

   public void run() {
      label30:
      while(true) {
         if (!this.isInterrupted()) {
            if (this.queue.isEmpty()) {
               try {
                  sleep(500L);
               } catch (InterruptedException var2) {
                  return;
               }
            }

            while(true) {
               if (this.queue.isEmpty()) {
                  continue label30;
               }

               QueryWithTimer peek = (QueryWithTimer)this.queue.peek();
               if (peek != null) {
                  if (peek.executeTime > System.currentTimeMillis()) {
                     continue label30;
                  }

                  this.queue.remove();
                  VimeNetwork.mysql().query(peek.query);
                  MiniDot.debug("Player " + peek.player.username + " saved");
               }
            }
         }

         return;
      }
   }

   public static class QueryWithTimer {
      public final long executeTime;
      public final MiniDotPlayer player;
      public final String query;

      public QueryWithTimer(long remaining, String query, MiniDotPlayer player) {
         this.executeTime = System.currentTimeMillis() + remaining;
         this.query = query;
         this.player = player;
      }
   }
}
