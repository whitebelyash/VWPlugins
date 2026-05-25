package net.xtrafrancyz.VimeNetwork.api;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.List;

public interface Lobby {
   default void setMenuText(List lines) {
      this.setMenuText((String[])lines.toArray(new String[lines.size()]));
   }

   void setMenuText(String... var1);

   void setMaxPlayers(int var1);

   int getMaxPlayers();

   void setConnectableState(State var1);

   String getServerId();

   ServerType getServerType();

   String getServerTypeId();

   int getServerNumber();

   String getHost();

   int getPort();

   void shutdown();

   void forceSend();

   public static enum State {
      ALLOW_SPECTATORS(0),
      ALLOW_ALL(1),
      ALLOW_VIP(2),
      DENY_ALL(10),
      OFFLINE(11);

      private static final TIntObjectMap byId = new TIntObjectHashMap(16);
      private int id;

      private State(int id) {
         this.id = id;
      }

      public int getId() {
         return this.id;
      }

      public static State byId(int id) {
         return (State)byId.get(id);
      }

      static {
         for(State s : values()) {
            byId.put(s.id, s);
         }

      }
   }
}
