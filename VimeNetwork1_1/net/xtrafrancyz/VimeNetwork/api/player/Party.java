package net.xtrafrancyz.VimeNetwork.api.player;

import java.util.List;

public interface Party {
   String getLeader();

   List getPlayers();

   int size();
}
