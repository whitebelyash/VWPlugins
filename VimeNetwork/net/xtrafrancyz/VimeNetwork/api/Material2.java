package net.xtrafrancyz.VimeNetwork.api;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.server.v1_6_R3.Block;
import net.minecraft.server.v1_6_R3.BlockStepAbstract;
import net.minecraft.server.v1_6_R3.Item;
import net.minecraft.server.v1_6_R3.ItemStep;
import net.xtrafrancyz.VimeNetwork.api.util.Reflect;
import net.xtrafrancyz.VimeNetwork.impl.nms.NmsBlockUtils;
import net.xtrafrancyz.VimeNetwork.impl.nms.block.BlockBarrier;
import net.xtrafrancyz.VimeNetwork.impl.nms.block.BlockMagma;
import net.xtrafrancyz.VimeNetwork.impl.nms.block.BlockNewRotatable;
import net.xtrafrancyz.VimeNetwork.impl.nms.block.BlockNewSand;
import net.xtrafrancyz.VimeNetwork.impl.nms.block.BlockNewStairs;
import net.xtrafrancyz.VimeNetwork.impl.nms.block.BlockNewStone;
import net.xtrafrancyz.VimeNetwork.impl.nms.block.BlockPackedIce;
import net.xtrafrancyz.VimeNetwork.impl.nms.block.BlockPurpurStep;
import net.xtrafrancyz.VimeNetwork.impl.nms.block.BlockRedSandstoneStep;
import net.xtrafrancyz.VimeNetwork.impl.nms.block.BlockSeaLantern;
import net.xtrafrancyz.VimeNetwork.impl.nms.block.VBlock;
import net.xtrafrancyz.VimeNetwork.impl.nms.item.ItemBlockWithData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class Material2 {
   public static final Material LOG2;
   public static final Material ACACIA_STAIRS;
   public static final Material BIG_OAK_STAIRS;
   public static final Material BARRIER;
   public static final Material PRISMARINE;
   public static final Material SEA_LANTERN;
   public static final Material PACKED_ICE;
   public static final Material RED_SANDSTONE;
   public static final Material RED_SANDSTONE_STAIRS;
   public static final Material RED_SANDSTONE_STEP_DOUBLE;
   public static final Material RED_SANDSTONE_STEP;
   public static final Material PURPUR_BLOCK;
   public static final Material PURPUR_PILLAR;
   public static final Material PURPUR_STAIRS;
   public static final Material PURPUR_STEP_DOUBLE;
   public static final Material PURPUR_STEP;
   public static final Material END_BRICKS;
   public static final Material MAGMA;
   public static final Material NETHER_WART_BLOCK;
   public static final Material RED_NETHER_BRICK;
   public static final Material BONE_BLOCK;
   private static final TIntSet customMaterials = new TIntHashSet();

   public static void load() {
      Iterator<Recipe> it = Bukkit.recipeIterator();

      while(it.hasNext()) {
         ItemStack result = ((Recipe)it.next()).getResult();
         if (result != null && result.getType() == Material.SANDSTONE && result.getDurability() == 0 && result.getAmount() == 1) {
            it.remove();
            break;
         }
      }

      Bukkit.addRecipe((new ShapedRecipe(new ItemStack(Material.SANDSTONE))).shape(new String[]{"##", "##"}).setIngredient('#', Material.SAND, 0));
      Bukkit.addRecipe((new ShapelessRecipe(new ItemStack(Material.WOOD, 4, (short)4))).addIngredient(LOG2, 0));
      Bukkit.addRecipe((new ShapelessRecipe(new ItemStack(Material.WOOD, 4, (short)5))).addIngredient(LOG2, 1));
      Bukkit.addRecipe((new ShapedRecipe(new ItemStack(Material.WOOD_STEP, 6, (short)4))).shape(new String[]{"###"}).setIngredient('#', Material.WOOD, 4));
      Bukkit.addRecipe((new ShapedRecipe(new ItemStack(Material.WOOD_STEP, 6, (short)5))).shape(new String[]{"###"}).setIngredient('#', Material.WOOD, 5));
      Bukkit.addRecipe((new ShapedRecipe(new ItemStack(ACACIA_STAIRS, 4))).shape(new String[]{"#  ", "## ", "###"}).setIngredient('#', Material.WOOD, 4));
      Bukkit.addRecipe((new ShapedRecipe(new ItemStack(BIG_OAK_STAIRS, 4))).shape(new String[]{"#  ", "## ", "###"}).setIngredient('#', Material.WOOD, 5));
      Bukkit.addRecipe((new ShapedRecipe(new ItemStack(RED_SANDSTONE_STEP, 6))).shape(new String[]{"###"}).setIngredient('#', RED_SANDSTONE));
      Bukkit.addRecipe((new ShapedRecipe(new ItemStack(RED_SANDSTONE))).shape(new String[]{"##", "##"}).setIngredient('#', Material.SAND, 1));
      Bukkit.addRecipe((new ShapedRecipe(new ItemStack(RED_SANDSTONE, 4, (short)2))).shape(new String[]{"##", "##"}).setIngredient('#', RED_SANDSTONE));
      Bukkit.addRecipe((new ShapedRecipe(new ItemStack(RED_SANDSTONE, 1, (short)1))).shape(new String[]{"#", "#"}).setIngredient('#', RED_SANDSTONE_STEP));
      Bukkit.addRecipe((new ShapedRecipe(new ItemStack(RED_SANDSTONE_STAIRS, 4))).shape(new String[]{"#  ", "## ", "###"}).setIngredient('#', RED_SANDSTONE));
      Bukkit.addRecipe((new ShapedRecipe(new ItemStack(NETHER_WART_BLOCK))).shape(new String[]{"###", "###", "###"}).setIngredient('#', Material.NETHER_STALK));
      Bukkit.addRecipe((new ShapedRecipe(new ItemStack(RED_NETHER_BRICK))).shape(new String[]{"-#", "#-"}).setIngredient('#', Material.NETHER_STALK).setIngredient('-', Material.NETHER_BRICK_ITEM));
      Bukkit.addRecipe((new ShapelessRecipe(new ItemStack(Material.INK_SACK, 9, (short)15))).addIngredient(BONE_BLOCK));
      Bukkit.addRecipe((new ShapedRecipe(new ItemStack(BONE_BLOCK))).shape(new String[]{"###", "###", "###"}).setIngredient('#', Material.INK_SACK, 15));
      Bukkit.addRecipe((new ShapedRecipe(new ItemStack(END_BRICKS, 4))).shape(new String[]{"##", "##"}).setIngredient('#', Material.ENDER_STONE));
      Bukkit.addRecipe((new ShapedRecipe(new ItemStack(PURPUR_STEP, 6))).shape(new String[]{"###"}).setIngredient('#', PURPUR_BLOCK));
      Bukkit.addRecipe((new ShapedRecipe(new ItemStack(PURPUR_PILLAR))).shape(new String[]{"#", "#"}).setIngredient('#', PURPUR_STEP));
      Bukkit.addRecipe((new ShapedRecipe(new ItemStack(PURPUR_STAIRS, 4))).shape(new String[]{"#  ", "## ", "###"}).setIngredient('#', PURPUR_BLOCK));
   }

   public static boolean isOccluding(Material type) {
      if (!isCustom(type)) {
         return type.isOccluding();
      } else {
         return type != ACACIA_STAIRS && type != BIG_OAK_STAIRS && type != RED_SANDSTONE_STEP && type != RED_SANDSTONE_STAIRS && type != PURPUR_STAIRS && type != PURPUR_STEP;
      }
   }

   public static boolean isFlammable(Material type) {
      if (!isCustom(type)) {
         return type.isFlammable();
      } else {
         return type == LOG2 || type == ACACIA_STAIRS || type == BIG_OAK_STAIRS || type == MAGMA;
      }
   }

   public static boolean isBurnable(Material type) {
      if (!isCustom(type)) {
         return type.isBurnable();
      } else {
         return type == LOG2 || type == ACACIA_STAIRS || type == BIG_OAK_STAIRS;
      }
   }

   public static boolean isSolid(Material type) {
      return isCustom(type) ? true : type.isSolid();
   }

   public static boolean isTransparent(Material type) {
      return type == BARRIER ? true : type.isTransparent();
   }

   public static boolean hasGravity(Material type) {
      return type.hasGravity();
   }

   public static boolean isCustom(Material type) {
      return customMaterials.contains(type.getId());
   }

   static {
      if (Material.getMaterial("LOG2") == null) {
         LOG2 = NmsBlockUtils.addBlock(162, "LOG2", (Block)(new BlockNewRotatable(162, net.minecraft.server.v1_6_R3.Material.WOOD)).setHardness(2.0F), true);
         ACACIA_STAIRS = NmsBlockUtils.addBlock(163, "ACACIA_STAIRS", (Block)(new BlockNewStairs(163, Block.WOOD, 4)), true);
         BIG_OAK_STAIRS = NmsBlockUtils.addBlock(164, "BIG_OAK_STAIRS", (Block)(new BlockNewStairs(164, Block.WOOD, 5)), true);
         BARRIER = NmsBlockUtils.addBlock(166, "BARRIER", (Block)(new BlockBarrier(166)), false);
         PRISMARINE = NmsBlockUtils.addBlock(168, "PRISMARINE", (Block)(new VBlock(168, net.minecraft.server.v1_6_R3.Material.STONE)).setResistance(10.0F).setHardness(1.5F).dropExactMeta(), true);
         SEA_LANTERN = NmsBlockUtils.addBlock(169, "SEA_LANTERN", (Block)(new BlockSeaLantern(169)), false);
         PACKED_ICE = NmsBlockUtils.addBlock(174, "PACKED_ICE", (Block)(new BlockPackedIce(174)), false);
         RED_SANDSTONE = NmsBlockUtils.addBlock(179, "RED_SANDSTONE", (Block)(new VBlock(179, net.minecraft.server.v1_6_R3.Material.STONE)).setHardness(0.8F).dropExactMeta(), true);
         RED_SANDSTONE_STAIRS = NmsBlockUtils.addBlock(180, "RED_SANDSTONE_STAIRS", (Block)(new BlockNewStairs(180, Block.byId[179], 0)), true);
         RED_SANDSTONE_STEP_DOUBLE = NmsBlockUtils.addBlock(181, "RED_SANDSTONE_STEP_DOUBLE", (Block)(new BlockRedSandstoneStep(181, true)), false);
         RED_SANDSTONE_STEP = NmsBlockUtils.addBlock(182, "RED_SANDSTONE_STEP", (Block)(new BlockRedSandstoneStep(182, false)), false);
         PURPUR_BLOCK = NmsBlockUtils.addBlock(201, "PURPUR_BLOCK", (Block)(new VBlock(201, net.minecraft.server.v1_6_R3.Material.STONE)).setResistance(10.0F).setHardness(1.5F), false);
         PURPUR_PILLAR = NmsBlockUtils.addBlock(202, "PURPUR_PILLAR", (Block)(new BlockNewRotatable(202, net.minecraft.server.v1_6_R3.Material.STONE)).setResistance(10.0F).setHardness(1.5F), true);
         PURPUR_STAIRS = NmsBlockUtils.addBlock(203, "PURPUR_STAIRS", (Block)(new BlockNewStairs(203, Block.byId[201], 0)), true);
         PURPUR_STEP_DOUBLE = NmsBlockUtils.addBlock(204, "PURPUR_STEP_DOUBLE", (Block)(new BlockPurpurStep(204, true)), false);
         PURPUR_STEP = NmsBlockUtils.addBlock(205, "PURPUR_STEP", (Block)(new BlockPurpurStep(205, false)), false);
         END_BRICKS = NmsBlockUtils.addBlock(206, "END_BRICKS", (Block)(new VBlock(206, net.minecraft.server.v1_6_R3.Material.STONE)).setHardness(0.8F), false);
         MAGMA = NmsBlockUtils.addBlock(213, "MAGMA", (Block)(new BlockMagma(213)), false);
         NETHER_WART_BLOCK = NmsBlockUtils.addBlock(214, "NETHER_WART_BLOCK", (Block)(new VBlock(214, net.minecraft.server.v1_6_R3.Material.GRASS)).setHardness(1.0F), false);
         RED_NETHER_BRICK = NmsBlockUtils.addBlock(215, "RED_NETHER_BRICK", (Block)(new VBlock(215, net.minecraft.server.v1_6_R3.Material.STONE)).setHardness(2.0F).setResistance(10.0F), false);
         BONE_BLOCK = NmsBlockUtils.addBlock(216, "BONE_BLOCK", (Block)(new BlockNewRotatable(216, net.minecraft.server.v1_6_R3.Material.STONE)).setHardness(2.0F), false);
         Consumer<Integer> addItemBlockMetadata = (id) -> {
            Item.byId[id] = null;
            new ItemBlockWithData(id - 256);
         };
         addItemBlockMetadata.accept(Block.STONE.id);
         addItemBlockMetadata.accept(Block.DIRT.id);
         addItemBlockMetadata.accept(Block.SPONGE.id);
         addItemBlockMetadata.accept(Block.SAND.id);
         Block.byId[1] = null;
         Reflect.setFinal((Class)Block.class, "STONE", new BlockNewStone(1));
         Block.byId[12] = null;
         Reflect.setFinal((Class)Block.class, "SAND", new BlockNewSand(12));
         Item.byId[181] = null;
         Item.byId[181] = new ItemStep(-75, (BlockStepAbstract)Block.byId[182], (BlockStepAbstract)Block.byId[181], true);
         Item.byId[182] = null;
         Item.byId[182] = new ItemStep(-74, (BlockStepAbstract)Block.byId[182], (BlockStepAbstract)Block.byId[181], false);
         Item.byId[204] = null;
         Item.byId[204] = new ItemStep(-52, (BlockStepAbstract)Block.byId[205], (BlockStepAbstract)Block.byId[204], true);
         Item.byId[205] = null;
         Item.byId[205] = new ItemStep(-51, (BlockStepAbstract)Block.byId[205], (BlockStepAbstract)Block.byId[204], false);

         try {
            Map<String, Material> byName = (Map)Reflect.get(Material.class, "BY_NAME");

            for(Field f : Material2.class.getDeclaredFields()) {
               if (f.getType() == Material.class && Modifier.isStatic(f.getModifiers())) {
                  Material type = (Material)f.get((Object)null);
                  customMaterials.add(type.getId());
                  byName.put(type.name(), type);
               }
            }
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      } else {
         LOG2 = Material.getMaterial("LOG2");
         ACACIA_STAIRS = Material.getMaterial("ACACIA_STAIRS");
         BIG_OAK_STAIRS = Material.getMaterial("BIG_OAK_STAIRS");
         BARRIER = Material.getMaterial("BARRIER");
         PRISMARINE = Material.getMaterial("PRISMARINE");
         SEA_LANTERN = Material.getMaterial("SEA_LANTERN");
         PACKED_ICE = Material.getMaterial("PACKED_ICE");
         RED_SANDSTONE = Material.getMaterial("RED_SANDSTONE");
         RED_SANDSTONE_STAIRS = Material.getMaterial("RED_SANDSTONE_STAIRS");
         RED_SANDSTONE_STEP_DOUBLE = Material.getMaterial("RED_SANDSTONE_STEP_DOUBLE");
         RED_SANDSTONE_STEP = Material.getMaterial("RED_SANDSTONE_STEP");
         PURPUR_BLOCK = Material.getMaterial("PURPUR_BLOCK");
         PURPUR_PILLAR = Material.getMaterial("PURPUR_PILLAR");
         PURPUR_STAIRS = Material.getMaterial("PURPUR_STAIRS");
         PURPUR_STEP_DOUBLE = Material.getMaterial("PURPUR_STEP_DOUBLE");
         PURPUR_STEP = Material.getMaterial("PURPUR_STEP");
         END_BRICKS = Material.getMaterial("END_BRICKS");
         MAGMA = Material.getMaterial("MAGMA");
         NETHER_WART_BLOCK = Material.getMaterial("NETHER_WART_BLOCK");
         RED_NETHER_BRICK = Material.getMaterial("RED_NETHER_BRICK");
         BONE_BLOCK = Material.getMaterial("BONE_BLOCK");

         try {
            for(Field f : Material2.class.getDeclaredFields()) {
               if (f.getType() == Material.class && Modifier.isStatic(f.getModifiers())) {
                  customMaterials.add(((Material)f.get((Object)null)).getId());
               }
            }
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }

   }
}
