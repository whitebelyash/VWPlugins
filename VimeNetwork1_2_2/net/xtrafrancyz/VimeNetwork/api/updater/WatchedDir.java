/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.event.Event
 */
package net.xtrafrancyz.VimeNetwork.api.updater;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.xtrafrancyz.VimeNetwork.api.event.FileUpdateEvent;
import net.xtrafrancyz.VimeNetwork.api.updater.WatchedEntry;
import net.xtrafrancyz.VimeNetwork.api.updater.WatchedFile;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public class WatchedDir
extends WatchedEntry {
    final File dir;
    Map<String, WatchedEntry> entries;

    WatchedDir(String path, File dir) {
        super(path);
        this.dir = dir;
        this.entries = new HashMap<String, WatchedEntry>();
        File[] listing = dir.listFiles();
        if (listing != null) {
            for (File file : listing) {
                String p;
                String string = p = path.isEmpty() ? file.getName() : path + "/" + file.getName();
                if (file.isDirectory()) {
                    this.entries.put(file.getName(), new WatchedDir(p, file));
                    continue;
                }
                this.entries.put(file.getName(), new WatchedFile(p, file));
            }
        }
    }

    public File getUpdateDir() {
        return this.dir;
    }

    public List<WatchedEntry> getEntries() {
        return new ArrayList<WatchedEntry>(this.entries.values());
    }

    @Override
    public boolean hasChanges(WatchedEntry entry) {
        if (!(entry instanceof WatchedDir)) {
            return true;
        }
        WatchedDir curr = (WatchedDir)entry;
        if (curr.entries.size() != this.entries.size()) {
            Bukkit.getPluginManager().callEvent((Event)new FileUpdateEvent(this, curr));
            this.entries = curr.entries;
            return false;
        }
        for (Map.Entry<String, WatchedEntry> otherEntry : curr.entries.entrySet()) {
            WatchedEntry myFile = this.entries.get(otherEntry.getKey());
            if (myFile == null) {
                Bukkit.getPluginManager().callEvent((Event)new FileUpdateEvent(this, curr));
                this.entries = curr.entries;
                return false;
            }
            if (!myFile.hasChanges(otherEntry.getValue())) continue;
            Bukkit.getPluginManager().callEvent((Event)new FileUpdateEvent(this, curr));
            this.entries = curr.entries;
            return false;
        }
        return false;
    }

    public String toString() {
        return "Dir: " + this.path + " => " + this.dir.getAbsolutePath();
    }
}

