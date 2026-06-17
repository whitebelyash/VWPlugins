/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.SkyWars.game.loot;

import java.util.List;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class LootGenerator {
    protected Random rand = new Random();
    public int rotation = 0;

    public abstract List<ItemStack> basic();

    public abstract List<ItemStack> middle();

    public abstract List<ItemStack> mystic();

    protected boolean isDiamond(Material type) {
        switch (type) {
            case DIAMOND_HELMET: 
            case DIAMOND_CHESTPLATE: 
            case DIAMOND_LEGGINGS: 
            case DIAMOND_BOOTS: {
                return true;
            }
        }
        return false;
    }

    protected boolean isIron(Material type) {
        switch (type) {
            case IRON_HELMET: 
            case IRON_CHESTPLATE: 
            case IRON_LEGGINGS: 
            case IRON_BOOTS: {
                return true;
            }
        }
        return false;
    }
}

