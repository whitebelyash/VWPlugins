/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  gnu.trove.map.TIntObjectMap
 *  gnu.trove.map.hash.TIntObjectHashMap
 */
package net.xtrafrancyz.VimeNetwork.api;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.ServerType;

public interface Lobby {
    default public void setMenuText(List<String> lines) {
        this.setMenuText(lines.toArray(new String[lines.size()]));
    }

    public void setMenuText(String ... var1);

    public void setMaxPlayers(int var1);

    public int getMaxPlayers();

    public void setConnectableState(State var1);

    public String getServerId();

    public ServerType getServerType();

    public String getServerTypeId();

    public int getServerNumber();

    public String getHost();

    public int getPort();

    public void shutdown();

    public void forceSend();

    public static enum State {
        ALLOW_SPECTATORS(0),
        ALLOW_ALL(1),
        ALLOW_VIP(2),
        DENY_ALL(10),
        OFFLINE(11);

        private static final TIntObjectMap<State> byId;
        private int id;

        private State(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        public static State byId(int id) {
            return (State)((Object)byId.get(id));
        }

        static {
            byId = new TIntObjectHashMap(16);
            for (State s : State.values()) {
                byId.put(s.id, (Object)s);
            }
        }
    }
}

