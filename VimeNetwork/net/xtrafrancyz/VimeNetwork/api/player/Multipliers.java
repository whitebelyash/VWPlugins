package net.xtrafrancyz.VimeNetwork.api.player;

import java.util.List;

public interface Multipliers {
   int getCurrentMultiplier();

   int getRankMultiplier();

   int getExtraMultiplier();

   long getExtraEndTime();

   default void add(Multiplier mult) {
      this.add(mult, 1);
   }

   void add(Multiplier var1, int var2);

   default boolean isActivated() {
      return this.getExtraMultiplier() > 0;
   }

   void activate(Multiplier var1);

   void deactivate();

   default void take(Multiplier mult) {
      this.take(mult, 1);
   }

   void take(Multiplier var1, int var2);

   int getAmount(Multiplier var1);

   List list();
}
