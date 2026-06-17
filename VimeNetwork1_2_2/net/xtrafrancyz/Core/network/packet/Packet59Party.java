/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import java.util.ArrayList;
import java.util.List;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet;

public class Packet59Party
extends Packet {
    public String username;
    public PartyPlayer leader = null;
    public List<PartyPlayer> players;

    private Packet59Party() {
    }

    public Packet59Party(String username, PartyPlayer leader, List<PartyPlayer> players) {
        this.username = username;
        this.leader = leader;
        this.players = players != null ? players : new ArrayList<PartyPlayer>();
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeString(this.username);
        if (this.leader != null) {
            buf.write((byte)1);
            buf.writeString(this.leader.username);
            buf.writeVarInt(this.leader.level);
            buf.writeString(this.leader.rank.name());
        } else {
            buf.write((byte)0);
        }
        buf.write((byte)this.players.size());
        for (PartyPlayer player : this.players) {
            buf.writeString(player.username);
            buf.writeVarInt(player.level);
            buf.writeString(player.rank.name());
        }
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        this.username = buf.readString();
        if (buf.read() == 1) {
            this.leader = new PartyPlayer();
            this.leader.username = buf.readString();
            this.leader.level = buf.readVarInt();
            this.leader.rank = Rank.getRank(buf.readString());
        }
        int size = buf.read();
        this.players = new ArrayList<PartyPlayer>(size);
        for (int i = 0; i < size; ++i) {
            PartyPlayer player = new PartyPlayer();
            player.username = buf.readString();
            player.level = buf.readVarInt();
            player.rank = Rank.getRank(buf.readString());
            this.players.add(player);
        }
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle59Party(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{leader=" + this.leader + ", size=" + this.players.size() + "}";
    }

    public static class PartyPlayer {
        public String username;
        public int level;
        public Rank rank;
    }
}

