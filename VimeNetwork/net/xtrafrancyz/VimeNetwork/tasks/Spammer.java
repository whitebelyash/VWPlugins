package net.xtrafrancyz.VimeNetwork.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.util.Rand;
import net.xtrafrancyz.bukkit.texteria.Texteria2D;
import net.xtrafrancyz.bukkit.texteria.elements.Text;
import net.xtrafrancyz.bukkit.texteria.utils.Position;
import net.xtrafrancyz.bukkit.texteria.utils.Visibility;
import org.bukkit.Bukkit;

public class Spammer implements Runnable {
   private static final List MESSAGES = new ArrayList();
   private static final String TEXT_COLOR = "&f";
   private static final String BOLD_COLOR = "&a";

   public static void addMessage(String message) {
      if (!MESSAGES.contains(message)) {
         MESSAGES.add(message.replace("^t", "&f").replace("^b", "&a"));
      }

   }

   public void run() {
      Texteria2D.add(((Text)((Text)((Text)(new Text("vn.spam", new String[]{(String)Rand.of(MESSAGES)})).setOffset(2, 2)).setPosition(Position.BOTTOM_LEFT)).setDuration(45000L)).setVisibility(new Visibility.IngameNotChat()), Bukkit.getOnlinePlayers());
   }

   static {
      String c1 = "&f";
      String c2 = "&a";
      MESSAGES.addAll(Arrays.asList(c1 + "Наш форум " + c2 + "http://f.vimeworld.ru", c1 + "TeamSpeak 3 " + c2 + "http://ts.vimeworld.ru", c1 + "Общайтесь в ts3 " + c2 + "http://ts.vimeworld.ru", c1 + "Покупка коинов " + c2 + "http://cp.vimeworld.ru", c1 + "Личный кабинет " + c2 + "http://cp.vimeworld.ru", c1 + "Группа ВК " + c2 + "https://vk.com/vimeworld", c1 + "Желаем Вам удачной игры!", c1 + "Покупка &a[V]" + c1 + ", &b[P]" + c1 + ", &6[H]" + c1 + " - " + c2 + "http://cp.vimeworld.ru", c1 + "Жалобы на форум " + c2 + "http://f.vimeworld.ru", c1 + "Вернуться в лобби " + c2 + "/hub", c1 + "Личные сообщения " + c2 + "/m", c1 + "Флудят в лс? " + c2 + "/ignore", c1 + "Профиль и настройки " + c2 + "/me", c1 + "Быстрый ответ на личное сообщение " + c2 + "/r", c1 + "Список доступных команд: " + c2 + "/help", c1 + "Заметил плохого игрока: " + c2 + "/report", c1 + "Играйте вместе с друзьями: " + c2 + "/party", c1 + "Играйте вместе с друзьями: " + c2 + "/friend", c1 + "Есть 10к подписчиков? Получи статус &cYouTube" + c1 + "!", c1 + "Открой чат и я исчезну :3", c1 + "Не забывай кушать!", c1 + "Не будь как все, будь &dImmortal" + c1 + "!", c1 + "На VimeWorld самые лучшие игроки ^_^"));
   }
}
