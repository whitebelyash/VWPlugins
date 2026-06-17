/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.updater;

import java.io.File;

public abstract class WatchedEntry {
    protected final String path;

    protected WatchedEntry(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public String getName() {
        if (this.path.contains(File.separator)) {
            return this.path.substring(this.path.lastIndexOf(File.separatorChar) + 1);
        }
        return this.path;
    }

    public File getLocalMirror() {
        return new File(this.path);
    }

    abstract boolean hasChanges(WatchedEntry var1);

    public int hashCode() {
        return this.path.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        return ((WatchedEntry)obj).path.equals(this.path);
    }
}

