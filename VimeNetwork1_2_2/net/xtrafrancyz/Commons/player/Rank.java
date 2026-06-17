/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Commons.player;

import java.util.EnumSet;
import net.xtrafrancyz.Commons.player.Permission;

public enum Rank {
    PLAYER("", "\u0418\u0433\u0440\u043e\u043a", null),
    VIP("\u00a7a", "VIP", "V"),
    PREMIUM("\u00a7b", "Premium", "P"),
    HOLY("\u00a76", "Holy", "H"),
    IMMORTAL("\u00a7d", "Immortal", "I"),
    BUILDER("\u00a72", "\u0411\u0438\u043b\u0434\u0435\u0440", "\u0411\u0438\u043b\u0434\u0435\u0440"),
    MAPLEAD("\u00a72", "\u0413\u043b\u0430\u0432\u043d\u044b\u0439 \u0431\u0438\u043b\u0434\u0435\u0440", "\u0413\u043b. \u0431\u0438\u043b\u0434\u0435\u0440"),
    YOUTUBE("\u00a7c", "You\u00a7cTube", "\u00a7cYou\u00a7fTube"),
    DEV("\u00a73", "\u0420\u0430\u0437\u0440\u0430\u0431\u043e\u0442\u0447\u0438\u043a", "Dev"),
    ORGANIZER("\u00a73", "\u041e\u0440\u0433\u0430\u043d\u0438\u0437\u0430\u0442\u043e\u0440", "\u041e\u0440\u0433\u0430\u043d\u0438\u0437\u0430\u0442\u043e\u0440"),
    MODER("\u00a79", "\u041c\u043e\u0434\u0435\u0440\u0430\u0442\u043e\u0440", "\u041c\u043e\u0434\u0435\u0440"),
    WARDEN("\u00a79", "\u041f\u0440\u043e\u0432\u0435\u0440\u0435\u043d\u043d\u044b\u0439 \u043c\u043e\u0434\u0435\u0440\u0430\u0442\u043e\u0440", "\u041c\u043e\u0434\u0435\u0440"),
    CHIEF("\u00a79", "\u0413\u043b\u0430\u0432\u043d\u044b\u0439 \u043c\u043e\u0434\u0435\u0440\u0430\u0442\u043e\u0440", "\u0413\u043b. \u043c\u043e\u0434\u0435\u0440"),
    ADMIN("\u00a73\u00a7l", "\u0413\u043b\u0430\u0432\u043d\u044b\u0439 \u0430\u0434\u043c\u0438\u043d", "\u0413\u043b. \u0430\u0434\u043c\u0438\u043d");

    private String color;
    private String name;
    private String prefix;
    private EnumSet<Permission> permissions;

    private Rank(String color, String name, String prefix) {
        this.color = color;
        this.name = name == null ? "" : name;
        this.prefix = prefix == null ? "" : prefix;
        this.permissions = EnumSet.noneOf(Permission.class);
    }

    private void addPerm(Permission permission) {
        this.permissions.add(permission);
    }

    private void addAllPermsFrom(Rank other) {
        this.permissions.addAll(other.permissions);
    }

    public boolean has(Rank rank) {
        return this.compareTo(rank) >= 0;
    }

    public boolean has(Permission permission) {
        return this.permissions.contains((Object)permission);
    }

    public String getName() {
        return this.name;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getColor() {
        return this.color;
    }

    public String getDisplayName() {
        return this.color + this.name + "\u00a7r";
    }

    public static Rank getRank(String name) {
        if (name == null || name.isEmpty()) {
            return PLAYER;
        }
        name = name.toUpperCase();
        try {
            return Rank.valueOf(name);
        }
        catch (IllegalArgumentException ex) {
            return PLAYER;
        }
    }

    static {
        ORGANIZER.addPerm(Permission.ORGANIZER);
        IMMORTAL.addPerm(Permission.PREFIX);
        BUILDER.addPerm(Permission.BUILDER);
        MAPLEAD.addAllPermsFrom(BUILDER);
        MAPLEAD.addPerm(Permission.VANISH);
        MODER.addPerm(Permission.BAN);
        MODER.addPerm(Permission.MUTE);
        WARDEN.addAllPermsFrom(MODER);
        WARDEN.addPerm(Permission.VANISH);
        CHIEF.addAllPermsFrom(MAPLEAD);
        CHIEF.addAllPermsFrom(WARDEN);
        CHIEF.addAllPermsFrom(ORGANIZER);
        ADMIN.addAllPermsFrom(CHIEF);
        ADMIN.addPerm(Permission.PREFIX);
    }
}

