/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Commons.guild;

public enum GuildStatus {
    LEADER("\u041b\u0438\u0434\u0435\u0440"),
    OFFICER("\u041e\u0444\u0438\u0446\u0435\u0440"),
    MEMBER("\u0418\u0433\u0440\u043e\u043a");

    private String name;

    private GuildStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static GuildStatus findByName(String name) {
        for (GuildStatus status : GuildStatus.values()) {
            if (!status.name.equalsIgnoreCase(name) && !status.name().equalsIgnoreCase(name)) continue;
            return status;
        }
        return null;
    }
}

