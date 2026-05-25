package net.xtrafrancyz.bukkit.texteria;

import net.xtrafrancyz.bukkit.texteria.elements.Element;
import net.xtrafrancyz.bukkit.texteria.elements.ProgressTimer;
import net.xtrafrancyz.bukkit.texteria.elements.RadialProgressTimer;
import net.xtrafrancyz.bukkit.texteria.elements.Rectangle;
import net.xtrafrancyz.bukkit.texteria.elements.Table;
import net.xtrafrancyz.bukkit.texteria.elements.Text;
import net.xtrafrancyz.bukkit.texteria.elements.Vignette;
import net.xtrafrancyz.bukkit.texteria.utils.Animation2D;
import net.xtrafrancyz.bukkit.texteria.utils.Animation3D;
import net.xtrafrancyz.bukkit.texteria.utils.Visibility;
import net.xtrafrancyz.bukkit.texteria.world.WorldGroup;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length == 0) {
         sender.sendMessage("/texteria group|text|rect|ptimer|rtimer|vig|vis|anim|3d");
         return true;
      } else if (args[0].equals("group")) {
         Element[] cs = new Element[]{((Text)(new Text("test.text", new String[]{"eeeeeeeeee"})).setOffset(0, 30)).setDuration(3000L), (new Rectangle("test.rect", 20)).setDuration(3000L)};
         Texteria2D.add(cs, (Player)sender);
         sender.sendMessage("Пакет отправлен");
         return true;
      } else {
         Element elem = null;
         switch (args[0]) {
            case "text":
               elem = new Text("test", new String[]{"Test text"});
               break;
            case "rect":
               elem = new Rectangle("test", 50, 100);
               break;
            case "ptimer":
               elem = (new ProgressTimer("test", 100, 50)).setBarColor(-65536);
               break;
            case "rtimer":
               elem = new RadialProgressTimer("test", 40);
               break;
            case "vig":
               elem = (new Vignette("test")).setColor(-16737025);
               break;
            case "vis":
               elem = ((Text)(new Text("test", new String[]{"Видно всегда"})).setScale(2.0F)).setVisibility(new Visibility.Always());
               break;
            case "anim":
               elem = ((Text)((Text)(new Text("test", new String[]{"Анимация"})).setAnimation((new Animation2D()).setStart((new Animation2D.Params()).setY(10).setRotation(360.0F)).setFinish((new Animation2D.Params()).setScale(20.0F)))).setFadeStart(1000)).setFadeFinish(500);
               break;
            case "3d":
               WorldGroup group = new WorldGroup("test");
               group.setDuration(10000L);
               group.setFade(500);
               group.setScale(8.0F);
               group.setLocation(-9.0F, 60.0F, -25.0F);
               group.animation.setBoth((new Animation3D.Params()).setOffset(8.0F, -5.0F, 0.0F).setScale(-8.0F).setRotation(90.0F, 0.0F, 0.0F));
               group.add((Element)(new Table("test")).setTitle("Таблица рекордов").addColumn((new Table.Column("#", 15)).setCenter(true)).addColumn(new Table.Column("Ник игрока", 80)).addColumn((new Table.Column("Время", 62)).setColor(-7617718)).addRow("1", "xtrafrancyz", "43 м. 13.3 с.").addRow("2", "SmaIK", "44 м. 59.7 с.").addRow("3", "Lucy", "82 м. 21.0 с.").addRow("4", "Test1", "82 м. 21.0 с.").addRow("5", "Test1", "82 м. 21.0 с.").addRow("6", "Test1", "82 м. 21.0 с.").addRow("7", "Test1", "82 м. 21.0 с."));
               Texteria3D.addGroup(group, (Player)sender);
               sender.sendMessage("Пакет отправлен");
               return true;
         }

         if (elem == null) {
            sender.sendMessage(ChatColor.RED + "Ты чего написал, а?");
            return true;
         } else {
            Texteria2D.add(elem.setDuration(3000L), (Player)sender);
            sender.sendMessage("Пакет отправлен");
            return true;
         }
      }
   }
}
