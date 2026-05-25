package net.xtrafrancyz.VimeNetwork.api.player.treasure;

public enum TreasureType {
   BASIC("b", "&b&lСундук нуба", 1000),
   ANCIENT("a", "&6&lДревний сундук", 6000),
   MYTHICAL("m", "&d&lМистический сундук", 18000);

   public final String id;
   public final String name;
   public final int price;

   private TreasureType(String id, String name, int price) {
      this.id = id;
      this.name = name;
      this.price = price;
   }
}
