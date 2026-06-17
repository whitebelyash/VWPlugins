package net.xtrafrancyz.VimeNetwork.api.player;

import java.time.LocalDate;

public class DailyMetaValue {
   private NetworkPlayer player;
   private String key;

   public DailyMetaValue(NetworkPlayer player, String key) {
      this.player = player;
      this.key = key;
   }

   public String getValue() {
      String meta = this.player.getMeta(this.key);
      if (meta == null) {
         return null;
      } else {
         String[] split = meta.split(";");
         if (split[0].equals(getCurrentDay())) {
            return split[1];
         } else {
            this.player.removeMeta(this.key);
            return null;
         }
      }
   }

   public void setValue(String value) {
      this.player.setMeta(this.key, getCurrentDay() + ";" + value);
   }

   private static String getCurrentDay() {
      return String.valueOf(LocalDate.now().getDayOfYear());
   }
}
