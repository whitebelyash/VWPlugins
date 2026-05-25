package net.xtrafrancyz.bukkit.minidot.database;

public class MiniDotItem {
   public final int id;
   public final int price;
   public final Slot type;
   public final String name;
   public final int discount;

   public MiniDotItem(int id, int price, Slot type, String name, int discount) {
      this.id = id;
      this.price = price;
      this.type = type;
      this.name = name;
      this.discount = discount;
   }

   public boolean isMask() {
      return this.id >= 20000 && this.id < 30000;
   }

   public boolean isFree() {
      return this.price == 0;
   }

   public boolean isHidden() {
      return this.price == -1;
   }

   public boolean isBuyable() {
      return this.price > 0;
   }

   public boolean hasDiscount() {
      return this.discount > 0;
   }

   public static enum Slot {
      HEAD("head"),
      BODY("body"),
      PET("pet");

      private final String id;

      private Slot(String id) {
         this.id = id;
      }

      public String getId() {
         return this.id;
      }
   }
}
