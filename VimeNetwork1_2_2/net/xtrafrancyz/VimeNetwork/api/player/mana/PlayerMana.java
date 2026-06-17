/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.api.player.mana;

import net.xtrafrancyz.VimeNetwork.api.player.mana.Mana;
import org.bukkit.entity.Player;

public class PlayerMana {
    Mana controller;
    Player player;
    int regen;
    int max;
    int mana;
    boolean showText;

    public PlayerMana(int max, int regen) {
        this(max, regen, 0, false);
    }

    public PlayerMana(int max, int regen, int mana, boolean showText) {
        this.max = max;
        this.regen = regen;
        this.mana = mana;
        this.showText = showText;
    }

    public PlayerMana showText(boolean flag) {
        if (this.player != null) {
            throw new IllegalStateException("Flag must be set exactly after object creating");
        }
        this.showText = flag;
        return this;
    }

    public int get() {
        return this.mana;
    }

    public void add(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be greater than 0");
        }
        if (this.mana == this.max) {
            return;
        }
        this.mana = Math.min(this.max, this.mana + amount);
        this.controller.updateTexteria(this);
    }

    public void take(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be greater than 0");
        }
        this.mana = Math.max(0, this.mana - amount);
        this.controller.updateTexteria(this);
    }

    public void setMax(int max) {
        if (max == this.max) {
            return;
        }
        this.max = max;
        this.controller.updateTexteria(this);
    }

    public void setRegen(int regen) {
        this.regen = regen;
    }

    public int getRegen() {
        return this.regen;
    }

    public Player getPlayer() {
        return this.player;
    }
}

