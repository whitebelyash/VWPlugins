package net.xtrafrancyz.VimeNetwork.api.player;

public interface Stats {
   int get(Stat var1);

   int increment(Stat var1);

   int increment(Stat var1, int var2);
}
