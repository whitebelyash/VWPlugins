package net.xtrafrancyz.VimeNetwork.api.player;

import gnu.trove.map.hash.TIntObjectHashMap;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum ArrowTrail {
   HEARTS(1, "Сердечки", new ItemStack(Material.INK_SACK, 1, (short)1)),
   ANGRY_VILLAGER(2, "Разбитые сердечки", new ItemStack(Material.INK_SACK, 1, (short)11)),
   HAPPY_VILLAGER(3, "Зелёные фигушки", new ItemStack(Material.INK_SACK, 1, (short)2)),
   FIREWORK(4, "Белые частицы", new ItemStack(Material.INK_SACK, 1, (short)15)),
   MAGIC_CRIT(5, "Пузырики", Material.POTION),
   SMOKE(6, "Дымок", Material.TORCH),
   DRIP_LAVA(7, "Капельки лавы", Material.LAVA_BUCKET),
   DRIP_WATER(8, "Капельки водички", Material.WATER_BUCKET),
   SNOWBALL_POOF(9, "Снежок", Material.SNOW_BALL),
   SLIME(10, "Слайм", Material.SLIME_BALL),
   WITCH_MAGIC(11, "Мааагия", new ItemStack(Material.INK_SACK, 1, (short)5));

   private static final TIntObjectHashMap byId = new TIntObjectHashMap();
   private final int id;
   private final String name;
   private final ItemStack is;

   private ArrowTrail(int id, String name, Material type) {
      this(id, name, new ItemStack(type));
   }

   private ArrowTrail(int id, String name, ItemStack is) {
      this.id = id;
      this.name = name;
      this.is = is;
   }

   public String getName() {
      return this.name;
   }

   public int getId() {
      return this.id;
   }

   public ItemStack getItem() {
      return this.is.clone();
   }

   public static ArrowTrail byId(int id) {
      return (ArrowTrail)byId.get(id);
   }

   static {
      for(ArrowTrail trail : values()) {
         ArrowTrail old = (ArrowTrail)byId.put(trail.id, trail);
         if (old != null) {
            throw new RuntimeException("Duplicate trail id " + old + " and " + trail);
         }
      }

   }
}
