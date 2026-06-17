/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 */
package net.xtrafrancyz.ClashPoint.menu.shop;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum Currency {
    IRON(Material.IRON_INGOT, ChatColor.WHITE, "\u0436\u0435\u043b\u0435\u0437\u043e", "\u0436\u0435\u043b\u0435\u0437\u0430", "\u0436\u0435\u043b\u0435\u0437\u0430"),
    GOLD(Material.GOLD_INGOT, ChatColor.YELLOW, "\u0437\u043e\u043b\u043e\u0442\u043e", "\u0437\u043e\u043b\u043e\u0442\u0430", "\u0437\u043e\u043b\u043e\u0442\u0430"),
    DIAMOND(Material.DIAMOND, ChatColor.AQUA, "\u0430\u043b\u043c\u0430\u0437", "\u0430\u043b\u043c\u0430\u0437\u0430", "\u0430\u043b\u043c\u0430\u0437\u043e\u0432");

    public final Material material;
    public final String form1;
    public final String form2;
    public final String form3;
    public final ChatColor color;

    private Currency(Material material, ChatColor color, String form1, String form2, String form3) {
        this.material = material;
        this.color = color;
        this.form1 = form1;
        this.form2 = form2;
        this.form3 = form3;
    }
}

