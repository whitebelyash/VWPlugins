/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork;

public enum Debug {
    MYSQL,
    CORE,
    ACHIEVEMENTS;

    private boolean enabled = false;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}

