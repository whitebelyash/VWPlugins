/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.util.Items
 *  net.xtrafrancyz.VimeNetwork.api.util.Rand
 *  org.bukkit.Material
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.PotionMeta
 *  org.bukkit.potion.Potion
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.potion.PotionType
 */
package net.xtrafrancyz.SkyWars.game.loot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import net.xtrafrancyz.SkyWars.Registry;
import net.xtrafrancyz.SkyWars.game.loot.LootGenerator;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.Rand;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class StandardLootGenerator
extends LootGenerator {
    private static final List<ItemStack> HELMETS = Arrays.asList(new ItemStack(Material.DIAMOND_HELMET), new ItemStack(Material.DIAMOND_HELMET), new ItemStack(Material.GOLD_HELMET), new ItemStack(Material.IRON_HELMET), new ItemStack(Material.IRON_HELMET));
    private static final List<ItemStack> CHESTPLATES = Arrays.asList(new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.GOLD_CHESTPLATE), new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.IRON_CHESTPLATE));
    private static final List<ItemStack> LEGGINGS = Arrays.asList(new ItemStack(Material.DIAMOND_LEGGINGS), new ItemStack(Material.DIAMOND_LEGGINGS), new ItemStack(Material.GOLD_LEGGINGS), new ItemStack(Material.IRON_LEGGINGS), new ItemStack(Material.IRON_LEGGINGS));
    private static final List<ItemStack> BOOTS = Arrays.asList(new ItemStack(Material.DIAMOND_BOOTS), new ItemStack(Material.DIAMOND_BOOTS), new ItemStack(Material.GOLD_BOOTS), new ItemStack(Material.IRON_BOOTS), new ItemStack(Material.IRON_BOOTS));
    private static final List<Supplier<ItemStack>> POTIONS = Arrays.asList(() -> {
        boolean low = Rand.nextBoolean();
        Potion p = new Potion(PotionType.SPEED);
        p.setSplash(true);
        ItemStack potion = p.toItemStack(1);
        PotionMeta meta = (PotionMeta)potion.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, (low ? 60 : 30) * 20, low ? 0 : 1), true);
        potion.setItemMeta((ItemMeta)meta);
        return potion;
    }, () -> {
        boolean low = Rand.nextBoolean();
        Potion p = new Potion(PotionType.REGEN);
        p.setSplash(true);
        ItemStack potion = p.toItemStack(1);
        PotionMeta meta = (PotionMeta)potion.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, (low ? 33 : 16) * 20, low ? 0 : 1), true);
        potion.setItemMeta((ItemMeta)meta);
        return potion;
    }, () -> new ItemStack(Material.POTION, 1, 8261));

    @Override
    public List<ItemStack> basic() {
        ItemStack sword;
        float rnd;
        ArrayList<ItemStack> items = new ArrayList<ItemStack>(32);
        items.add((ItemStack)Rand.of(HELMETS));
        items.add((ItemStack)Rand.of(CHESTPLATES));
        items.add((ItemStack)Rand.of(LEGGINGS));
        items.add((ItemStack)Rand.of(BOOTS));
        if (this.rand.nextFloat() < 0.05f) {
            items.remove(this.rand.nextInt(4));
        }
        if (this.rand.nextFloat() < 0.2f) {
            items.add((ItemStack)Rand.of((List[])new List[]{HELMETS, CHESTPLATES, LEGGINGS, BOOTS}));
        }
        for (ItemStack item : items) {
            rnd = this.rand.nextFloat();
            Material type = item.getType();
            if (this.isDiamond(type)) {
                if (rnd < 0.1f) {
                    item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                }
            } else if (this.isIron(type)) {
                if (rnd < 0.03f) {
                    item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                } else if (rnd < 0.1f) {
                    item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                }
            } else if (rnd < 0.02f) {
                item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
            } else if (rnd < 0.05f) {
                item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
            } else if (rnd < 0.15f) {
                item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
            }
            if (!(Rand.nextFloat() < 0.05f)) continue;
            item.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, 1);
        }
        rnd = Rand.nextFloat();
        if (rnd < 0.3f) {
            sword = new ItemStack(Material.DIAMOND_SWORD);
            if (Rand.nextFloat() < 0.1f) {
                sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
            }
        } else if (rnd < 0.6f) {
            sword = new ItemStack(Material.STONE_SWORD);
            if (Rand.nextFloat() < 0.15f) {
                sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
            }
            if (Rand.nextFloat() < 0.04f) {
                sword.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
            }
        } else {
            sword = new ItemStack((Material)Rand.of((Object[])new Material[]{Material.STONE_SWORD, Material.GOLD_SWORD}));
            rnd = Rand.nextFloat();
            if (rnd < 0.05f) {
                sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
            } else if (rnd < 0.4f) {
                sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
            }
            if (Rand.nextFloat() < 0.04f) {
                sword.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
            }
        }
        items.add(sword);
        if (this.rand.nextFloat() < 0.1f) {
            items.add(new ItemStack(Material.BOW));
        }
        items.add((ItemStack)Rand.of(Registry.PICKAXES));
        if (this.rand.nextFloat() < 0.4f) {
            items.add((ItemStack)Rand.of(Registry.AXES));
        }
        if (this.rand.nextFloat() < 0.3f) {
            items.add((ItemStack)Rand.of(Registry.SPADES));
        }
        if (this.rand.nextFloat() < 0.15f) {
            items.add(new ItemStack(Material.FISHING_ROD));
        }
        if (this.rand.nextFloat() < 0.3f) {
            items.add(new ItemStack(Material.GOLDEN_APPLE, Rand.intRange((int)2, (int)5)));
        }
        if ((double)this.rand.nextFloat() < 0.3) {
            items.add(new ItemStack(Material.FLINT_AND_STEEL));
        }
        if ((double)this.rand.nextFloat() < 0.4) {
            items.add(new ItemStack(Material.TNT, Rand.intRange((int)5, (int)15)));
        }
        if (this.rand.nextFloat() < 0.01f) {
            ItemStack item;
            item = Items.name((Material)Material.SLIME_BALL, (String)"&a\u0421\u043b\u0438\u0437\u044c \u0431\u043e\u0433\u043e\u0432", (String[])new String[0]);
            item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 3);
            items.add(item);
        }
        if (this.rand.nextFloat() < 0.05f) {
            items.add(Items.name((Material)Material.COMPASS, (String)"&eGPS \u0442\u0440\u0435\u043a\u0435\u0440", (String[])new String[]{"&7\u0423\u043a\u0430\u0436\u0435\u0442 \u0432\u0430\u043c \u043d\u0430 \u0431\u043b\u0438\u0436\u0430\u0439\u0448\u0435\u0433\u043e \u0432\u0440\u0430\u0433\u0430", "VimeWorld.ru"}));
        }
        if (this.rand.nextFloat() < 0.2f) {
            items.add(new ItemStack(Material.EGG, Rand.intRange((int)4, (int)8)));
        }
        if (this.rand.nextFloat() < 0.2f) {
            items.add(new ItemStack(Material.SNOW_BALL, Rand.intRange((int)4, (int)8)));
        }
        if (this.rand.nextFloat() < 0.3f) {
            items.add(new ItemStack(Material.ENCHANTMENT_TABLE));
        }
        items.add((ItemStack)((Supplier)Rand.of(Registry.FOOD)).get());
        items.add(new ItemStack(Material.ARROW, Rand.intRange((int)16, (int)32)));
        items.add(new ItemStack(Material.EXP_BOTTLE, Rand.intRange((int)8, (int)24)));
        items.add(new ItemStack(Material.WATER_BUCKET));
        items.add(new ItemStack(Material.LAVA_BUCKET));
        switch (this.rand.nextInt(4)) {
            case 0: {
                items.add((ItemStack)((Supplier)Rand.of(Registry.BLOCKS)).get());
            }
            case 1: 
            case 2: {
                items.add((ItemStack)((Supplier)Rand.of(Registry.BLOCKS)).get());
            }
            case 3: {
                items.add((ItemStack)((Supplier)Rand.of(Registry.BLOCKS)).get());
            }
        }
        items.add((ItemStack)((Supplier)Rand.of(POTIONS)).get());
        if (this.rand.nextFloat() < 0.1f) {
            items.add((ItemStack)((Supplier)Rand.of(POTIONS)).get());
        }
        if (this.rotation > 0) {
            if (this.rand.nextFloat() < 0.2f) {
                items.add(new ItemStack(Material.ENDER_PEARL));
            }
            if (this.rand.nextFloat() < 0.4f) {
                items.add(new ItemStack(Material.GOLDEN_APPLE, Rand.intRange((int)2, (int)4)));
            }
        }
        return items;
    }

    @Override
    public List<ItemStack> middle() {
        float rnd;
        ArrayList<ItemStack> items = new ArrayList<ItemStack>(32);
        int num = Rand.intRange((int)1, (int)3);
        for (int i = 0; i < num; ++i) {
            ItemStack item = (ItemStack)Rand.of((List[])new List[]{HELMETS, CHESTPLATES, LEGGINGS, BOOTS});
            rnd = Rand.nextFloat();
            if (rnd < 0.04f) {
                item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
            } else if (rnd < 0.08f) {
                item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
            } else if (rnd < 0.2f) {
                item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
            }
            rnd = Rand.nextFloat();
            if (rnd < 0.03f) {
                item.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 3);
            } else if (rnd < 0.06f) {
                item.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 2);
            } else if (rnd < 0.1f) {
                item.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 1);
            }
            if (this.rand.nextFloat() < 0.05f) {
                item.addUnsafeEnchantment(Enchantment.THORNS, 1);
            }
            items.add(item);
        }
        if (this.rand.nextFloat() < 0.2f) {
            ItemStack item = new ItemStack(Material.BOW);
            rnd = Rand.nextFloat();
            if (rnd < 0.05f) {
                item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 3);
            } else if (rnd < 0.1f) {
                item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 2);
            } else if (rnd < 0.2f) {
                item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
            }
            if (this.rand.nextFloat() < 0.02f) {
                item.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 1);
            }
            items.add(item);
        }
        if (this.rand.nextFloat() < 0.5f) {
            ItemStack item = new ItemStack((Material)Rand.of((Object[])new Material[]{Material.DIAMOND_SWORD, Material.IRON_SWORD}));
            rnd = Rand.nextFloat();
            if (rnd < 0.04f) {
                item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 3);
            } else if (rnd < 0.1f) {
                item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
            } else if (rnd < 0.2f) {
                item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
            }
            rnd = Rand.nextFloat();
            if (rnd < 0.02f) {
                item.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 2);
            } else if (rnd < 0.07f) {
                item.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
            }
            items.add(item);
        }
        if (this.rand.nextFloat() < 0.3f) {
            items.add(new ItemStack(Material.GOLDEN_APPLE, Rand.intRange((int)1, (int)4)));
        }
        items.add((ItemStack)((Supplier)Rand.of(Registry.FOOD)).get());
        if (this.rand.nextFloat() < 0.1f) {
            items.add(new ItemStack(Material.STICK, Rand.intRange((int)2, (int)10)));
        }
        if (this.rand.nextFloat() < 0.3f) {
            items.add(new ItemStack(Material.FLINT_AND_STEEL));
        }
        if (this.rand.nextFloat() < 0.5f) {
            items.add((ItemStack)((Supplier)Rand.of(Registry.BLOCKS)).get());
        }
        if (this.rand.nextFloat() < 0.2f) {
            items.add(new ItemStack(Material.BROWN_MUSHROOM));
        }
        if (this.rand.nextFloat() < 0.2f) {
            items.add(new ItemStack(Material.RED_MUSHROOM));
        }
        if (this.rand.nextFloat() < 0.3f) {
            items.add(new ItemStack(Material.ARROW, Rand.intRange((int)12, (int)32)));
        }
        if (this.rand.nextFloat() < 0.3f) {
            items.add(new ItemStack(Material.WATER_BUCKET));
        }
        if (this.rand.nextFloat() < 0.1f) {
            items.add(new ItemStack(Material.LAVA_BUCKET));
        }
        if (this.rand.nextFloat() < 0.2f) {
            items.add(new ItemStack(Material.EXP_BOTTLE, Rand.intRange((int)8, (int)24)));
        }
        if (this.rand.nextFloat() < 0.1f) {
            items.add(Items.name((Material)Material.COMPASS, (String)"&eGPS \u0442\u0440\u0435\u043a\u0435\u0440", (String[])new String[]{"&7\u0423\u043a\u0430\u0436\u0435\u0442 \u0432\u0430\u043c \u043d\u0430 \u0431\u043b\u0438\u0436\u0430\u0439\u0448\u0435\u0433\u043e \u0432\u0440\u0430\u0433\u0430", "VimeWorld.ru"}));
        }
        if (this.rand.nextFloat() < 0.5f) {
            items.add((ItemStack)((Supplier)Rand.of(POTIONS)).get());
        }
        return items;
    }

    @Override
    public List<ItemStack> mystic() {
        float rnd;
        ArrayList<ItemStack> items = new ArrayList<ItemStack>(25);
        if (this.rand.nextFloat() < 0.1f) {
            ItemStack item = Items.name((Material)Material.SLIME_BALL, (String)"&a\u0421\u043b\u0438\u0437\u044c \u0431\u043e\u0433\u043e\u0432", (String[])new String[0]);
            item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 3);
            items.add(item);
        }
        int amount = Rand.intRange((int)1, (int)3);
        block6: for (int i = 0; i < amount; ++i) {
            ItemStack is = ((ItemStack)Rand.of(Registry.DIAMOND_ITEMS)).clone();
            switch (is.getType()) {
                case DIAMOND_BOOTS: 
                case DIAMOND_CHESTPLATE: 
                case DIAMOND_HELMET: 
                case DIAMOND_LEGGINGS: {
                    rnd = this.rand.nextFloat();
                    if (rnd < 0.2f) {
                        is.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                    } else if (rnd < 0.8f) {
                        is.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                    }
                    if (this.rand.nextFloat() < 0.3f) {
                        is.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, Rand.intRange((int)1, (int)2));
                    }
                    if (this.rand.nextFloat() < 0.3f) {
                        is.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, Rand.intRange((int)1, (int)2));
                    }
                    if (!(this.rand.nextFloat() < 0.2f)) break;
                    is.addUnsafeEnchantment(Enchantment.THORNS, 1);
                    break;
                }
                case DIAMOND_SWORD: {
                    rnd = this.rand.nextFloat();
                    if (rnd < 0.2f) {
                        is.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
                    } else if (rnd < 0.7f) {
                        is.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
                    }
                    rnd = this.rand.nextFloat();
                    if (rnd < 0.1f) {
                        is.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                    } else if (rnd < 0.3f) {
                        is.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                    }
                    rnd = this.rand.nextFloat();
                    if (rnd < 0.05f) {
                        is.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 2);
                        break;
                    }
                    if (!(rnd < 0.2f)) break;
                    is.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
                    break;
                }
                case DIAMOND_PICKAXE: {
                    is.addUnsafeEnchantment(Enchantment.DIG_SPEED, Rand.intRange((int)2, (int)4));
                    if (!(this.rand.nextFloat() < 0.1f)) break;
                    is.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                    break;
                }
                case DIAMOND_AXE: {
                    is.addUnsafeEnchantment(Enchantment.DIG_SPEED, Rand.intRange((int)2, (int)4));
                    is.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
                    break;
                }
                default: {
                    continue block6;
                }
            }
            items.add(is);
        }
        ItemStack bow = new ItemStack(Material.BOW);
        rnd = this.rand.nextFloat();
        if (rnd < 0.05f) {
            bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 3);
        } else if (rnd < 0.3f) {
            bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 2);
        } else if (rnd < 0.8f) {
            bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
        }
        rnd = this.rand.nextFloat();
        if (rnd < 0.05f) {
            bow.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 2);
        } else if (rnd < 0.3f) {
            bow.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
        }
        rnd = this.rand.nextFloat();
        if (rnd < 0.05f) {
            bow.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 2);
        } else if (rnd < 0.3f) {
            bow.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 1);
        }
        if (this.rand.nextFloat() < 0.05f) {
            bow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        }
        items.add(bow);
        items.add(new ItemStack(Material.ARROW, Rand.intRange((int)24, (int)48)));
        if (this.rand.nextFloat() < 0.2f) {
            items.add(new ItemStack(Material.ENDER_PEARL));
        }
        if (this.rand.nextFloat() < 0.2f) {
            items.add(new ItemStack(Material.ENDER_PEARL));
        }
        items.add(new ItemStack(Material.GOLDEN_APPLE, Rand.intRange((int)2, (int)6)));
        items.add((ItemStack)((Supplier)Rand.of(Registry.FOOD)).get());
        if (this.rand.nextFloat() < 0.8f) {
            items.add(Items.name((Material)Material.COMPASS, (String)"&eGPS \u0442\u0440\u0435\u043a\u0435\u0440", (String[])new String[]{"&7\u0423\u043a\u0430\u0436\u0435\u0442 \u0432\u0430\u043c \u043d\u0430 \u0431\u043b\u0438\u0436\u0430\u0439\u0448\u0435\u0433\u043e \u0432\u0440\u0430\u0433\u0430", "VimeWorld.ru"}));
        }
        return items;
    }
}

