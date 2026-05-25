package net.xtrafrancyz.Commons;

public class T {
   private T() {
   }

   public static String system(String title, String text) {
      return title + " &e> &f" + text;
   }

   public static String warning(String title, String text) {
      return system(title, "&6" + text);
   }

   public static String error(String title, String text) {
      return system(title, "&c" + text);
   }

   public static String success(String title, String text) {
      return system(title, "&a" + text);
   }

   public static String kickBanMessage(String username, String reason, int timeSeconds, String banner) {
      String bantime;
      if (timeSeconds == 0) {
         bantime = "навсегда";
      } else {
         bantime = F.formatSecondsShort(timeSeconds);
      }

      String message = "&7* * * * * * * * * * * * * * * * * * *\n&cВы были забанены\n\n&cПричина: &e" + reason + "\n&cВремя бана: &e" + bantime + "\n&cВас забанил: &e" + banner + "\n&7* * * * * * * * * * * * * * * * * * *";
      return message.length() > 256 ? message.substring(0, 256) : message;
   }

   public static String kickMessage(String username, String reason, String kicker) {
      String message = "&7* * * * * * * * * * * * * * * * * * *\n&cВы были кикнуты с сервера\n\n&cПричина: &e" + reason + "\n&cВас кикнул: &e" + kicker + "\n&7* * * * * * * * * * * * * * * * * * *";
      return message.length() > 256 ? message.substring(0, 256) : message;
   }
}
