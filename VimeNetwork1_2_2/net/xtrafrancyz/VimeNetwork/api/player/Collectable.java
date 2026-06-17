/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.player;

import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;

public class Collectable {
    protected final NetworkPlayer player;
    private final String key;
    protected boolean[] data;

    public Collectable(NetworkPlayer player, String key, int size) {
        this(player, key, new boolean[size]);
    }

    public Collectable(NetworkPlayer player, String key, boolean[] defaults) {
        this.player = player;
        this.key = key;
        this.data = defaults;
    }

    public boolean get(int index) {
        if (index < 0 || index >= this.data.length) {
            throw new IndexOutOfBoundsException("size=" + this.data.length);
        }
        return this.data[index];
    }

    public boolean set(int index, boolean state) {
        if (index < 0 || index >= this.data.length) {
            throw new IndexOutOfBoundsException("size=" + this.data.length);
        }
        boolean old = this.data[index];
        if (old != state) {
            this.data[index] = state;
            this.save();
        }
        return old;
    }

    public int getTrueCount() {
        int count = 0;
        for (boolean b : this.data) {
            if (!b) continue;
            ++count;
        }
        return count;
    }

    public int getFalseCount() {
        int count = 0;
        for (boolean b : this.data) {
            if (b) continue;
            ++count;
        }
        return count;
    }

    public int getSize() {
        return this.data.length;
    }

    public void save() {
        char[] chars = new char[this.data.length];
        for (int i = 0; i < chars.length; ++i) {
            chars[i] = this.data[i] ? 49 : 48;
        }
        this.player.setMeta(this.key, new String(chars));
    }

    public void load() {
        String meta = this.player.getMeta(this.key);
        if (meta != null) {
            char[] chars = meta.toCharArray();
            int size = chars.length > this.data.length ? this.data.length : chars.length;
            for (int i = 0; i < size; ++i) {
                this.data[i] = chars[i] == '1';
            }
        }
    }
}

