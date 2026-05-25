package net.xtrafrancyz.Core.player;

public enum Rank {
   PLAYER("", "Игрок", (String)null),
   VIP("&a", "VIP", "V"),
   PREMIUM("&b", "Premium", "P"),
   HOLY("&6", "Holy", "H"),
   IMMORTAL("&d", "Immortal", "I"),
   BUILDER("&2", "Билдер", "Билдер"),
   MAPLEAD("&2", "Главный билдер", "Гл. билдер"),
   YOUTUBE("&c", "You&cTube", "&cYou&cTube"),
   DEV("&3", "Разработчик", "Dev"),
   MODER("&9", "Модератор", "Модер"),
   WARDEN("&9", "Проверенный модератор", "Модер"),
   CHIEF("&9", "Главный модератор", "Гл. модер"),
   ADMIN("&3&l", "Главный админ", "Гл. админ");

   private String color;
   private String name;
   private String prefix;

   private Rank(String color, String name, String prefix) {
      this.color = color;
      this.name = name == null ? "" : name;
      this.prefix = prefix == null ? "" : prefix;
   }

   public boolean has(Rank rank) {
      return this.compareTo(rank) >= 0;
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
      return this.color + this.name + "&r";
   }
}
