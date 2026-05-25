package net.xtrafrancyz.VimeNetwork.api.player.goals;

import java.util.Map;

public interface Goals {
   void add(String var1, Goal var2);

   void addCustom(String var1, Goal var2);

   boolean remove(String var1);

   boolean contains(String var1);

   void trigger(String var1, GoalQuery var2);

   void triggerAmount(String var1, int var2, GoalQuery var3);

   void openInventory();

   Map getActiveGoals();

   Map getCustomGoals();
}
