/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  gnu.trove.map.hash.TIntObjectHashMap
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.api.player;

import gnu.trove.map.hash.TIntObjectHashMap;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum ArrowTrail {
    HEARTS(1, "\u0421\u0435\u0440\u0434\u0435\u0447\u043a\u0438", new ItemStack(Material.INK_SACK, 1, 1)),
    ANGRY_VILLAGER(2, "\u0420\u0430\u0437\u0431\u0438\u0442\u044b\u0435 \u0441\u0435\u0440\u0434\u0435\u0447\u043a\u0438", new ItemStack(Material.INK_SACK, 1, 11)),
    HAPPY_VILLAGER(3, "\u0417\u0435\u043b\u0451\u043d\u044b\u0435 \u0444\u0438\u0433\u0443\u0448\u043a\u0438", new ItemStack(Material.INK_SACK, 1, 2)),
    FIREWORK(4, "\u0411\u0435\u043b\u044b\u0435 \u0447\u0430\u0441\u0442\u0438\u0446\u044b", new ItemStack(Material.INK_SACK, 1, 15)),
    MAGIC_CRIT(5, "\u041f\u0443\u0437\u044b\u0440\u0438\u043a\u0438", Material.POTION),
    SMOKE(6, "\u0414\u044b\u043c\u043e\u043a", Material.TORCH),
    DRIP_LAVA(7, "\u041a\u0430\u043f\u0435\u043b\u044c\u043a\u0438 \u043b\u0430\u0432\u044b", Material.LAVA_BUCKET),
    DRIP_WATER(8, "\u041a\u0430\u043f\u0435\u043b\u044c\u043a\u0438 \u0432\u043e\u0434\u0438\u0447\u043a\u0438", Material.WATER_BUCKET),
    SNOWBALL_POOF(9, "\u0421\u043d\u0435\u0436\u043e\u043a", Material.SNOW_BALL),
    SLIME(10, "\u0421\u043b\u0430\u0439\u043c", Material.SLIME_BALL),
    WITCH_MAGIC(11, "\u041c\u0430\u0430\u0430\u0433\u0438\u044f", new ItemStack(Material.INK_SACK, 1, 5));

    private static final TIntObjectHashMap<ArrowTrail> byId;
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
        return (ArrowTrail)((Object)byId.get(id));
    }

    static {
        byId = new TIntObjectHashMap();
        for (ArrowTrail trail : ArrowTrail.values()) {
            ArrowTrail old = (ArrowTrail)((Object)byId.put(trail.id, (Object)trail));
            if (old == null) continue;
            throw new RuntimeException("Duplicate trail id " + (Object)((Object)old) + " and " + (Object)((Object)trail));
        }
    }
}

