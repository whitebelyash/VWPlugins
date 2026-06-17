/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.score;

import net.xtrafrancyz.VimeNetwork.api.score.SideScoreboard;

public class Record {
    private static int counter = 0;
    private final int id = counter++;
    SideScoreboard board;
    String name;
    int value;

    Record(SideScoreboard board, String name) {
        this.board = board;
        this.name = name;
        this.value = 0;
    }

    public void setName(String name) {
        if (this.name.equals(name)) {
            return;
        }
        this.board.removeScore(this.name);
        this.name = name;
        this.update();
    }

    public void setValue(int value) {
        if (value == this.value) {
            return;
        }
        this.value = value;
        this.update();
    }

    public void set(String name, int value) {
        if (!this.name.equals(name)) {
            this.board.removeScore(this.name);
            this.name = name;
        }
        this.value = value;
        this.update();
    }

    public void update() {
        this.board.setScore(this.name, this.value);
    }

    public String getName() {
        return this.name;
    }

    public int getValue() {
        return this.value;
    }

    public void remove() {
        this.board.remove(this);
    }

    public int hashCode() {
        return this.id;
    }

    public boolean equals(Object obj) {
        return obj instanceof Record && obj.hashCode() == this.id;
    }

    public String toString() {
        return "Record " + this.id + "{" + this.name + " = " + this.value + "}";
    }
}

