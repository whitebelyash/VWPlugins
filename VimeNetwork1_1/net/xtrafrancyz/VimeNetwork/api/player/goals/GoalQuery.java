package net.xtrafrancyz.VimeNetwork.api.player.goals;

import java.util.HashMap;
import java.util.Map;

public class GoalQuery {
   public final String type;
   public final Map data = new HashMap();

   private GoalQuery(String type) {
      this.type = type;
   }

   public GoalQuery put(String key, Object value) {
      this.data.put(key, value);
      return this;
   }

   public static GoalQuery of(String type) {
      return new GoalQuery(type);
   }
}
