/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.player.treasure;

public enum TreasureType {
    BASIC("b", "&b&l\u0421\u0443\u043d\u0434\u0443\u043a \u043d\u0443\u0431\u0430", 1000),
    ANCIENT("a", "&6&l\u0414\u0440\u0435\u0432\u043d\u0438\u0439 \u0441\u0443\u043d\u0434\u0443\u043a", 6000),
    MYTHICAL("m", "&d&l\u041c\u0438\u0441\u0442\u0438\u0447\u0435\u0441\u043a\u0438\u0439 \u0441\u0443\u043d\u0434\u0443\u043a", 18000);

    public final String id;
    public final String name;
    public final int price;

    private TreasureType(String id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}

