package net.xtrafrancyz.VimeNetwork.impl.player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.xtrafrancyz.Core.network.packet.Packet59Party;
import net.xtrafrancyz.VimeNetwork.api.player.Party;

public class VParty implements Party {
   public String leader;
   public List players;

   public VParty(Packet59Party.PartyPlayer leader, List players) {
      this.leader = leader.username;
      this.players = (List)players.stream().map((p) -> p.username).collect(Collectors.toList());
   }

   public String getLeader() {
      return this.leader;
   }

   public List getPlayers() {
      List<String> list = new ArrayList(this.size());
      list.addAll(this.players);
      list.add(this.leader);
      return list;
   }

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
