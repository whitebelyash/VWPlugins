package net.xtrafrancyz.VimeNetwork.api.player;

import java.util.EnumSet;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public enum Rank {
   PLAYER("", "Игрок", ""),
   VIP("&a", "VIP", "V"),
   PREMIUM("&b", "Premium", "P"),
   HOLY("&6", "Holy", "H"),
   IMMORTAL("&d", "Immortal", "I"),
   BUILDER("&2", "Строитель", "Билдер"),
   MAPLEAD("&2", "Главный билдер", "Гл. билдер"),
   YOUTUBE("&c", "You&cTube", "&fYou&cTube"),
   DEV("&3", "Разработчик", "Dev"),
   MODER("&9", "Модератор", "Модер"),
   WARDEN("&9", "Проверенный модератор", "Модер"),
   CHIEF("&9", "Главный модератор", "Гл. модер"),
   ADMIN("&3&l", "Главный админ", "Гл. админ");

   private String color;
   private String name;
   private String prefix;
   private EnumSet permissions;

   private Rank(String color, String name, String prefix) {
      this.color = U.colored(color);
      this.name = name == null ? "" : U.colored(name);
      this.prefix = prefix == null ? "" : U.colored(prefix);
      this.permissions = EnumSet.noneOf(Permission.class);
   }

   private void addPerm(Permission permission) {
      this.permissions.add(permission);
   }

   private void addAllPermsFrom(Rank other) {
      this.permissions.addAll(other.permissions);
   }

   public boolean has(Permission permission) {
      return this.permissions.contains(permission);
   }

   public boolean has(CommandSender sender, Permission permission) {
      if (this.permissions.contains(permission)) {
         return true;
      } else {
         if (sender != null) {
            U.msg(sender, T.error("VimeWorld", "У вас недостаточно прав для совершения этого действия"));
         }

         return false;
      }
   }

   public boolean has(Rank rank) {
      return this.has((CommandSender)null, (Rank)rank);
   }

   public boolean has(CommandSender sender, Rank rank) {
      if (this.compareTo(rank) >= 0) {
         return true;
      } else {
         if (sender != null) {
            U.msg(sender, T.error("VimeWorld", "Для этого действия необходим статус " + rank.color + rank.name));
         }

         return false;
      }
   }

   public String getName() {
      return this.name;
   }

   public String getPrefix() {
      return this.prefix;
   }

   public String getColor() {
      return this.color;
   }

   public String getDisplayName() {
      return this.color + this.name + ChatColor.RESET;
   }

   public static Rank getRank(String name) {
      if (name != null && !name.isEmpty()) {
         name = name.toUpperCase();

         try {
            return valueOf(name);
         } catch (IllegalArgumentException var2) {
            return PLAYER;
         }
      } else {
         return PLAYER;
      }
   }

   static {
      IMMORTAL.addPerm(Permission.PREFIX);
      BUILDER.addPerm(Permission.BUILDER);
      MAPLEAD.addAllPermsFrom(BUILDER);
      MAPLEAD.addPerm(Permission.VANISH);
      DEV.addPerm(Permission.BAN);
      DEV.addPerm(Permission.MUTE);
      MODER.addPerm(Permission.BAN);
      MODER.addPerm(Permission.MUTE);
      WARDEN.addAllPermsFrom(MODER);
      WARDEN.addPerm(Permission.VANISH);
      CHIEF.addAllPermsFrom(MAPLEAD);
      CHIEF.addAllPermsFrom(WARDEN);
      ADMIN.addAllPermsFrom(CHIEF);
      ADMIN.addPerm(Permission.PREFIX);
   }
}
