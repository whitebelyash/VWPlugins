package net.xtrafrancyz.VimeNetwork.api.player.treasure;

public interface Treasures {
   int get(TreasureType var1);

   void add(TreasureType var1, int var2);

   void take(TreasureType var1, int var2);

   void giveWithMessage(TreasureType var1, float var2);

   void giveWithMessage(TreasureType var1);

   boolean hasAny();
}
