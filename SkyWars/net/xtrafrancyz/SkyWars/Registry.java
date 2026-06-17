/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.util.Rand
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.potion.PotionEffectType
 */
package net.xtrafrancyz.SkyWars;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import net.xtrafrancyz.VimeNetwork.api.util.Rand;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class Registry {
    public static final List<ItemStack> DIAMOND_ITEMS = Arrays.asList(new ItemStack(Material.DIAMOND_SWORD), new ItemStack(Material.DIAMOND_PICKAXE), new ItemStack(Material.DIAMOND_AXE), new ItemStack(Material.DIAMOND_HELMET), new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.DIAMOND_LEGGINGS), new ItemStack(Material.DIAMOND_BOOTS));
    public static final List<PotionEffectType> EFFECT_TYPES = Arrays.asList(PotionEffectType.SPEED, PotionEffectType.SLOW, PotionEffectType.FAST_DIGGING, PotionEffectType.SLOW_DIGGING, PotionEffectType.INCREASE_DAMAGE, PotionEffectType.JUMP, PotionEffectType.CONFUSION, PotionEffectType.REGENERATION, PotionEffectType.DAMAGE_RESISTANCE, PotionEffectType.FIRE_RESISTANCE, PotionEffectType.INVISIBILITY, PotionEffectType.BLINDNESS, PotionEffectType.NIGHT_VISION, PotionEffectType.HUNGER, PotionEffectType.WEAKNESS, PotionEffectType.POISON, PotionEffectType.WITHER, PotionEffectType.HEALTH_BOOST, PotionEffectType.ABSORPTION, PotionEffectType.SATURATION);
    public static final List<Supplier<ItemStack>> IRON_ORE_ITEMS = Arrays.asList(() -> new ItemStack(Material.ARROW, Rand.intRange((int)4, (int)10)), () -> new ItemStack(Material.STICK, Rand.intRange((int)2, (int)4)), () -> new ItemStack(Material.FLINT, Rand.intRange((int)2, (int)4)), () -> new ItemStack(Material.STRING, Rand.intRange((int)3, (int)4)), () -> new ItemStack(Material.FEATHER, Rand.intRange((int)2, (int)4)), () -> new ItemStack(Material.COAL, Rand.intRange((int)3, (int)4)), () -> new ItemStack(Material.DIAMOND, Rand.intRange((int)1, (int)2)), () -> new ItemStack(Material.DIAMOND, Rand.intRange((int)1, (int)2)), () -> new ItemStack(Material.IRON_INGOT, Rand.intRange((int)1, (int)3)), () -> new ItemStack(Material.IRON_INGOT, Rand.intRange((int)1, (int)3)));
    public static final List<Supplier<ItemStack>> FOOD = Arrays.asList(() -> new ItemStack(Material.CARROT_ITEM, Rand.intRange((int)8, (int)32)), () -> new ItemStack(Material.APPLE, Rand.intRange((int)8, (int)32)), () -> new ItemStack(Material.BREAD, Rand.intRange((int)8, (int)32)), () -> new ItemStack(Material.BAKED_POTATO, Rand.intRange((int)8, (int)32)), () -> new ItemStack(Material.COOKED_FISH, Rand.intRange((int)8, (int)32)), () -> new ItemStack(Material.COOKED_BEEF, Rand.intRange((int)8, (int)32)), () -> new ItemStack(Material.COOKED_CHICKEN, Rand.intRange((int)8, (int)32)));
    public static final List<Supplier<ItemStack>> BLOCKS = Arrays.asList(() -> new ItemStack(Material.STONE, Rand.intRange((int)20, (int)40)), () -> new ItemStack(Material.WOOD, Rand.intRange((int)20, (int)40)), () -> new ItemStack(Material.BRICK, Rand.intRange((int)20, (int)40)), () -> new ItemStack(Material.COBBLESTONE, Rand.intRange((int)20, (int)40)), () -> new ItemStack(Material.STONE, Rand.intRange((int)20, (int)40)), () -> new ItemStack(Material.DIRT, Rand.intRange((int)20, (int)40)));
    public static final List<ItemStack> PICKAXES = Arrays.asList(new ItemStack(Material.STONE_PICKAXE), new ItemStack(Material.IRON_PICKAXE), new ItemStack(Material.GOLD_PICKAXE), new ItemStack(Material.DIAMOND_PICKAXE));
    public static final List<ItemStack> AXES = Arrays.asList(new ItemStack(Material.STONE_AXE), new ItemStack(Material.IRON_AXE), new ItemStack(Material.GOLD_AXE), new ItemStack(Material.DIAMOND_AXE));
    public static final List<ItemStack> SWORDS = Arrays.asList(new ItemStack(Material.STONE_SWORD), new ItemStack(Material.IRON_SWORD), new ItemStack(Material.GOLD_SWORD), new ItemStack(Material.DIAMOND_SWORD));
    public static final List<ItemStack> SPADES = Arrays.asList(new ItemStack(Material.STONE_SPADE), new ItemStack(Material.IRON_SPADE), new ItemStack(Material.GOLD_SPADE), new ItemStack(Material.DIAMOND_SPADE));
    public static final List<ItemStack> HELMETS = Arrays.asList(new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.CHAINMAIL_HELMET), new ItemStack(Material.DIAMOND_HELMET), new ItemStack(Material.GOLD_HELMET), new ItemStack(Material.IRON_HELMET));
    public static final List<ItemStack> CHESTPLATES = Arrays.asList(new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.GOLD_CHESTPLATE), new ItemStack(Material.IRON_CHESTPLATE));
    public static final List<ItemStack> LEGGINGS = Arrays.asList(new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.CHAINMAIL_LEGGINGS), new ItemStack(Material.DIAMOND_LEGGINGS), new ItemStack(Material.GOLD_LEGGINGS), new ItemStack(Material.IRON_LEGGINGS));
    public static final List<ItemStack> BOOTS = Arrays.asList(new ItemStack(Material.LEATHER_BOOTS), new ItemStack(Material.CHAINMAIL_BOOTS), new ItemStack(Material.DIAMOND_BOOTS), new ItemStack(Material.GOLD_BOOTS), new ItemStack(Material.IRON_BOOTS));
}

