/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  gnu.trove.set.TIntSet
 *  gnu.trove.set.hash.TIntHashSet
 *  net.minecraft.server.v1_6_R3.Block
 *  net.minecraft.server.v1_6_R3.BlockStepAbstract
 *  net.minecraft.server.v1_6_R3.Item
 *  net.minecraft.server.v1_6_R3.ItemStep
 *  net.minecraft.server.v1_6_R3.Material
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.Recipe
 *  org.bukkit.inventory.ShapedRecipe
 *  org.bukkit.inventory.ShapelessRecipe
 */
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
import net.minecraft.server.v1_6_R3.Material;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class Material2 {
    public static final org.bukkit.Material LOG2;
    public static final org.bukkit.Material ACACIA_STAIRS;
    public static final org.bukkit.Material BIG_OAK_STAIRS;
    public static final org.bukkit.Material BARRIER;
    public static final org.bukkit.Material PRISMARINE;
    public static final org.bukkit.Material SEA_LANTERN;
    public static final org.bukkit.Material PACKED_ICE;
    public static final org.bukkit.Material RED_SANDSTONE;
    public static final org.bukkit.Material RED_SANDSTONE_STAIRS;
    public static final org.bukkit.Material RED_SANDSTONE_STEP_DOUBLE;
    public static final org.bukkit.Material RED_SANDSTONE_STEP;
    public static final org.bukkit.Material PURPUR_BLOCK;
    public static final org.bukkit.Material PURPUR_PILLAR;
    public static final org.bukkit.Material PURPUR_STAIRS;
    public static final org.bukkit.Material PURPUR_STEP_DOUBLE;
    public static final org.bukkit.Material PURPUR_STEP;
    public static final org.bukkit.Material END_BRICKS;
    public static final org.bukkit.Material MAGMA;
    public static final org.bukkit.Material NETHER_WART_BLOCK;
    public static final org.bukkit.Material RED_NETHER_BRICK;
    public static final org.bukkit.Material BONE_BLOCK;
    private static final TIntSet customMaterials;

    public static void load() {
        Iterator it = Bukkit.recipeIterator();
        while (it.hasNext()) {
            ItemStack result = ((Recipe)it.next()).getResult();
            if (result == null || result.getType() != org.bukkit.Material.SANDSTONE || result.getDurability() != 0 || result.getAmount() != 1) continue;
            it.remove();
            break;
        }
        Bukkit.addRecipe((Recipe)new ShapedRecipe(new ItemStack(org.bukkit.Material.SANDSTONE)).shape(new String[]{"##", "##"}).setIngredient('#', org.bukkit.Material.SAND, 0));
        Bukkit.addRecipe((Recipe)new ShapelessRecipe(new ItemStack(org.bukkit.Material.WOOD, 4, 4)).addIngredient(LOG2, 0));
        Bukkit.addRecipe((Recipe)new ShapelessRecipe(new ItemStack(org.bukkit.Material.WOOD, 4, 5)).addIngredient(LOG2, 1));
        Bukkit.addRecipe((Recipe)new ShapedRecipe(new ItemStack(org.bukkit.Material.WOOD_STEP, 6, 4)).shape(new String[]{"###"}).setIngredient('#', org.bukkit.Material.WOOD, 4));
        Bukkit.addRecipe((Recipe)new ShapedRecipe(new ItemStack(org.bukkit.Material.WOOD_STEP, 6, 5)).shape(new String[]{"###"}).setIngredient('#', org.bukkit.Material.WOOD, 5));
        Bukkit.addRecipe((Recipe)new ShapedRecipe(new ItemStack(ACACIA_STAIRS, 4)).shape(new String[]{"#  ", "## ", "###"}).setIngredient('#', org.bukkit.Material.WOOD, 4));
        Bukkit.addRecipe((Recipe)new ShapedRecipe(new ItemStack(BIG_OAK_STAIRS, 4)).shape(new String[]{"#  ", "## ", "###"}).setIngredient('#', org.bukkit.Material.WOOD, 5));
        Bukkit.addRecipe((Recipe)new ShapedRecipe(new ItemStack(RED_SANDSTONE_STEP, 6)).shape(new String[]{"###"}).setIngredient('#', RED_SANDSTONE));
        Bukkit.addRecipe((Recipe)new ShapedRecipe(new ItemStack(RED_SANDSTONE)).shape(new String[]{"##", "##"}).setIngredient('#', org.bukkit.Material.SAND, 1));
        Bukkit.addRecipe((Recipe)new ShapedRecipe(new ItemStack(RED_SANDSTONE, 4, 2)).shape(new String[]{"##", "##"}).setIngredient('#', RED_SANDSTONE));
        Bukkit.addRecipe((Recipe)new ShapedRecipe(new ItemStack(RED_SANDSTONE, 1, 1)).shape(new String[]{"#", "#"}).setIngredient('#', RED_SANDSTONE_STEP));
        Bukkit.addRecipe((Recipe)new ShapedRecipe(new ItemStack(RED_SANDSTONE_STAIRS, 4)).shape(new String[]{"#  ", "## ", "###"}).setIngredient('#', RED_SANDSTONE));
        Bukkit.addRecipe((Recipe)new ShapedRecipe(new ItemStack(NETHER_WART_BLOCK)).shape(new String[]{"###", "###", "###"}).setIngredient('#', org.bukkit.Material.NETHER_STALK));
        Bukkit.addRecipe((Recipe)new ShapedRecipe(new ItemStack(RED_NETHER_BRICK)).shape(new String[]{"-#", "#-"}).setIngredient('#', org.bukkit.Material.NETHER_STALK).setIngredient('-', org.bukkit.Material.NETHER_BRICK_ITEM));
        Bukkit.addRecipe((Recipe)new ShapelessRecipe(new ItemStack(org.bukkit.Material.INK_SACK, 9, 15)).addIngredient(BONE_BLOCK));
        Bukkit.addRecipe((Recipe)new ShapedRecipe(new ItemStack(BONE_BLOCK)).shape(new String[]{"###", "###", "###"}).setIngredient('#', org.bukkit.Material.INK_SACK, 15));
        Bukkit.addRecipe((Recipe)new ShapedRecipe(new ItemStack(END_BRICKS, 4)).shape(new String[]{"##", "##"}).setIngredient('#', org.bukkit.Material.ENDER_STONE));
        Bukkit.addRecipe((Recipe)new ShapedRecipe(new ItemStack(PURPUR_STEP, 6)).shape(new String[]{"###"}).setIngredient('#', PURPUR_BLOCK));
        Bukkit.addRecipe((Recipe)new ShapedRecipe(new ItemStack(PURPUR_PILLAR)).shape(new String[]{"#", "#"}).setIngredient('#', PURPUR_STEP));
        Bukkit.addRecipe((Recipe)new ShapedRecipe(new ItemStack(PURPUR_STAIRS, 4)).shape(new String[]{"#  ", "## ", "###"}).setIngredient('#', PURPUR_BLOCK));
    }

    public static boolean isOccluding(org.bukkit.Material type) {
        if (Material2.isCustom(type)) {
            return type != ACACIA_STAIRS && type != BIG_OAK_STAIRS && type != RED_SANDSTONE_STEP && type != RED_SANDSTONE_STAIRS && type != PURPUR_STAIRS && type != PURPUR_STEP;
        }
        return type.isOccluding();
    }

    public static boolean isFlammable(org.bukkit.Material type) {
        if (Material2.isCustom(type)) {
            return type == LOG2 || type == ACACIA_STAIRS || type == BIG_OAK_STAIRS || type == MAGMA;
        }
        return type.isFlammable();
    }

    public static boolean isBurnable(org.bukkit.Material type) {
        if (Material2.isCustom(type)) {
            return type == LOG2 || type == ACACIA_STAIRS || type == BIG_OAK_STAIRS;
        }
        return type.isBurnable();
    }

    public static boolean isSolid(org.bukkit.Material type) {
        if (Material2.isCustom(type)) {
            return true;
        }
        return type.isSolid();
    }

    public static boolean isTransparent(org.bukkit.Material type) {
        if (type == BARRIER) {
            return true;
        }
        return type.isTransparent();
    }

    public static boolean hasGravity(org.bukkit.Material type) {
        return type.hasGravity();
    }

    public static boolean isCustom(org.bukkit.Material type) {
        return customMaterials.contains(type.getId());
    }

    static {
        customMaterials = new TIntHashSet();
        if (org.bukkit.Material.getMaterial((String)"LOG2") == null) {
            LOG2 = NmsBlockUtils.addBlock(162, "LOG2", (Block)new BlockNewRotatable(162, Material.WOOD).setHardness(2.0f), true);
            ACACIA_STAIRS = NmsBlockUtils.addBlock(163, "ACACIA_STAIRS", (Block)new BlockNewStairs(163, Block.WOOD, 4), true);
            BIG_OAK_STAIRS = NmsBlockUtils.addBlock(164, "BIG_OAK_STAIRS", (Block)new BlockNewStairs(164, Block.WOOD, 5), true);
            BARRIER = NmsBlockUtils.addBlock(166, "BARRIER", new BlockBarrier(166), false);
            PRISMARINE = NmsBlockUtils.addBlock(168, "PRISMARINE", new VBlock(168, Material.STONE).setResistance(10.0f).setHardness(1.5f).dropExactMeta(), true);
            SEA_LANTERN = NmsBlockUtils.addBlock(169, "SEA_LANTERN", new BlockSeaLantern(169), false);
            PACKED_ICE = NmsBlockUtils.addBlock(174, "PACKED_ICE", new BlockPackedIce(174), false);
            RED_SANDSTONE = NmsBlockUtils.addBlock(179, "RED_SANDSTONE", new VBlock(179, Material.STONE).setHardness(0.8f).dropExactMeta(), true);
            RED_SANDSTONE_STAIRS = NmsBlockUtils.addBlock(180, "RED_SANDSTONE_STAIRS", (Block)new BlockNewStairs(180, Block.byId[179], 0), true);
            RED_SANDSTONE_STEP_DOUBLE = NmsBlockUtils.addBlock(181, "RED_SANDSTONE_STEP_DOUBLE", (Block)new BlockRedSandstoneStep(181, true), false);
            RED_SANDSTONE_STEP = NmsBlockUtils.addBlock(182, "RED_SANDSTONE_STEP", (Block)new BlockRedSandstoneStep(182, false), false);
            PURPUR_BLOCK = NmsBlockUtils.addBlock(201, "PURPUR_BLOCK", new VBlock(201, Material.STONE).setResistance(10.0f).setHardness(1.5f), false);
            PURPUR_PILLAR = NmsBlockUtils.addBlock(202, "PURPUR_PILLAR", (Block)new BlockNewRotatable(202, Material.STONE).setResistance(10.0f).setHardness(1.5f), true);
            PURPUR_STAIRS = NmsBlockUtils.addBlock(203, "PURPUR_STAIRS", (Block)new BlockNewStairs(203, Block.byId[201], 0), true);
            PURPUR_STEP_DOUBLE = NmsBlockUtils.addBlock(204, "PURPUR_STEP_DOUBLE", (Block)new BlockPurpurStep(204, true), false);
            PURPUR_STEP = NmsBlockUtils.addBlock(205, "PURPUR_STEP", (Block)new BlockPurpurStep(205, false), false);
            END_BRICKS = NmsBlockUtils.addBlock(206, "END_BRICKS", new VBlock(206, Material.STONE).setHardness(0.8f), false);
            MAGMA = NmsBlockUtils.addBlock(213, "MAGMA", new BlockMagma(213), false);
            NETHER_WART_BLOCK = NmsBlockUtils.addBlock(214, "NETHER_WART_BLOCK", new VBlock(214, Material.GRASS).setHardness(1.0f), false);
            RED_NETHER_BRICK = NmsBlockUtils.addBlock(215, "RED_NETHER_BRICK", new VBlock(215, Material.STONE).setHardness(2.0f).setResistance(10.0f), false);
            BONE_BLOCK = NmsBlockUtils.addBlock(216, "BONE_BLOCK", (Block)new BlockNewRotatable(216, Material.STONE).setHardness(2.0f), false);
            Consumer<Integer> addItemBlockMetadata = id -> {
                Item.byId[id.intValue()] = null;
                new ItemBlockWithData(id - 256);
            };
            addItemBlockMetadata.accept(Block.STONE.id);
            addItemBlockMetadata.accept(Block.DIRT.id);
            addItemBlockMetadata.accept(Block.SPONGE.id);
            addItemBlockMetadata.accept(Block.SAND.id);
            Block.byId[1] = null;
            Reflect.setFinal(Block.class, "STONE", (Object)new BlockNewStone(1));
            Block.byId[12] = null;
            Reflect.setFinal(Block.class, "SAND", (Object)new BlockNewSand(12));
            Item.byId[181] = null;
            Item.byId[181] = new ItemStep(-75, (BlockStepAbstract)Block.byId[182], (BlockStepAbstract)Block.byId[181], true);
            Item.byId[182] = null;
            Item.byId[182] = new ItemStep(-74, (BlockStepAbstract)Block.byId[182], (BlockStepAbstract)Block.byId[181], false);
            Item.byId[204] = null;
            Item.byId[204] = new ItemStep(-52, (BlockStepAbstract)Block.byId[205], (BlockStepAbstract)Block.byId[204], true);
            Item.byId[205] = null;
            Item.byId[205] = new ItemStep(-51, (BlockStepAbstract)Block.byId[205], (BlockStepAbstract)Block.byId[204], false);
            try {
                Map byName = (Map)Reflect.get(org.bukkit.Material.class, "BY_NAME");
                for (Field f : Material2.class.getDeclaredFields()) {
                    if (f.getType() != org.bukkit.Material.class || !Modifier.isStatic(f.getModifiers())) continue;
                    org.bukkit.Material type = (org.bukkit.Material)f.get(null);
                    customMaterials.add(type.getId());
                    byName.put(type.name(), type);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            LOG2 = org.bukkit.Material.getMaterial((String)"LOG2");
            ACACIA_STAIRS = org.bukkit.Material.getMaterial((String)"ACACIA_STAIRS");
            BIG_OAK_STAIRS = org.bukkit.Material.getMaterial((String)"BIG_OAK_STAIRS");
            BARRIER = org.bukkit.Material.getMaterial((String)"BARRIER");
            PRISMARINE = org.bukkit.Material.getMaterial((String)"PRISMARINE");
            SEA_LANTERN = org.bukkit.Material.getMaterial((String)"SEA_LANTERN");
            PACKED_ICE = org.bukkit.Material.getMaterial((String)"PACKED_ICE");
            RED_SANDSTONE = org.bukkit.Material.getMaterial((String)"RED_SANDSTONE");
            RED_SANDSTONE_STAIRS = org.bukkit.Material.getMaterial((String)"RED_SANDSTONE_STAIRS");
            RED_SANDSTONE_STEP_DOUBLE = org.bukkit.Material.getMaterial((String)"RED_SANDSTONE_STEP_DOUBLE");
            RED_SANDSTONE_STEP = org.bukkit.Material.getMaterial((String)"RED_SANDSTONE_STEP");
            PURPUR_BLOCK = org.bukkit.Material.getMaterial((String)"PURPUR_BLOCK");
            PURPUR_PILLAR = org.bukkit.Material.getMaterial((String)"PURPUR_PILLAR");
            PURPUR_STAIRS = org.bukkit.Material.getMaterial((String)"PURPUR_STAIRS");
            PURPUR_STEP_DOUBLE = org.bukkit.Material.getMaterial((String)"PURPUR_STEP_DOUBLE");
            PURPUR_STEP = org.bukkit.Material.getMaterial((String)"PURPUR_STEP");
            END_BRICKS = org.bukkit.Material.getMaterial((String)"END_BRICKS");
            MAGMA = org.bukkit.Material.getMaterial((String)"MAGMA");
            NETHER_WART_BLOCK = org.bukkit.Material.getMaterial((String)"NETHER_WART_BLOCK");
            RED_NETHER_BRICK = org.bukkit.Material.getMaterial((String)"RED_NETHER_BRICK");
            BONE_BLOCK = org.bukkit.Material.getMaterial((String)"BONE_BLOCK");
            try {
                for (Field f : Material2.class.getDeclaredFields()) {
                    if (f.getType() != org.bukkit.Material.class || !Modifier.isStatic(f.getModifiers())) continue;
                    customMaterials.add(((org.bukkit.Material)f.get(null)).getId());
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

