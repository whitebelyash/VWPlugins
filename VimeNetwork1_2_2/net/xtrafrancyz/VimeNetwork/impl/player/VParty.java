/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.impl.player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.xtrafrancyz.Core.network.packet.Packet59Party;
import net.xtrafrancyz.VimeNetwork.api.player.Party;

public class VParty
implements Party {
    public String leader;
    public List<String> players;

    public VParty(Packet59Party.PartyPlayer leader, List<Packet59Party.PartyPlayer> players) {
        this.leader = leader.username;
        this.players = players.stream().map(p -> p.username).collect(Collectors.toList());
    }

    @Override
    public String getLeader() {
        return this.leader;
    }

    @Override
    public List<String> getPlayers() {
        ArrayList<String> list = new ArrayList<String>(this.size());
        list.addAll(this.players);
        list.add(this.leader);
        return list;
    }

    @Override
    public int size() {
        return this.players.size() + 1;
    }

    public int hashCode() {
        return this.leader.hashCode();
    }

    public boolean equals(Object obj) {
        return obj instanceof Party && this.leader.equals(((Party)obj).getLeader());
    }
}

