/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.ClashPoint.object;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum TeamPerk {
    PERSONAL_CHEST_CAPACITY(Material.ENDER_CHEST, "\u0415\u043c\u043a\u043e\u0441\u0442\u044c \u043f\u0435\u0440\u0441\u043e\u043d\u0430\u043b\u044c\u043d\u043e\u0433\u043e \u0441\u0443\u043d\u0434\u0443\u043a\u0430"),
    RP_REGENERATION(new ItemStack(Material.INK_SACK, 1, 14), "\u0420\u0435\u0433\u0435\u043d\u0435\u0440\u0430\u0446\u0438\u044f"),
    RP_HEALTH(new ItemStack(Material.ENDER_PEARL), "\u0417\u0434\u043e\u0440\u043e\u0432\u044c\u0435 \u0442\u043e\u0447\u0435\u043a \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432"),
    RP_RATE(Material.IRON_INGOT, "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0433\u0435\u043d\u0435\u0440\u0430\u0446\u0438\u0438 \u0436\u0435\u043b\u0435\u0437\u0430"),
    RP_DEBUFF(Material.POTION, "\u0414\u0435\u0431\u0430\u0444\u0444\u044b \u0432\u0440\u0430\u0433\u0430\u043c"),
    RP_THORNS(Material.CARROT_STICK, "\u041e\u0442\u0434\u0430\u0447\u0430");

    public final ItemStack is;
    public final String name;

    private TeamPerk(Material type, String name) {
        this(new ItemStack(type), name);
    }

    private TeamPerk(ItemStack is, String name) {
        this.is = is;
        this.name = name;
    }
}

