/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.bukkit.texteria.utils.ParsedTime
 */
package net.xtrafrancyz.VimeNetwork.api.player;

import net.xtrafrancyz.bukkit.texteria.utils.ParsedTime;

public class Multiplier {
    private int multiplier;
    private int durationMins;

    public Multiplier(int multiplier, int durationMins) {
        this.multiplier = multiplier;
        this.durationMins = durationMins;
    }

    public int getMultiplier() {
        return this.multiplier;
    }

    public int getDuration() {
        return this.durationMins;
    }

    public String getText(String color1, String color2) {
        return color1 + "x" + this.multiplier + color2 + " \u043d\u0430 " + color1 + new ParsedTime((long)(this.durationMins * 60 * 1000)).format() + color2;
    }

    public int hashCode() {
        int result = this.durationMins;
        result = 31 * result + this.multiplier;
        return result;
    }

    public String toString() {
        return "Multiplier{duration=" + this.durationMins + ", multiplier=" + this.multiplier + "}";
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Multiplier)) {
            return false;
        }
        Multiplier other = (Multiplier)obj;
        return other.durationMins == this.durationMins && other.multiplier == this.multiplier;
    }
}

