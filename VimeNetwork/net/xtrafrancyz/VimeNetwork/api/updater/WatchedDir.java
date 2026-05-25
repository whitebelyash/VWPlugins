package net.xtrafrancyz.VimeNetwork.api.updater;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.xtrafrancyz.VimeNetwork.api.event.FileUpdateEvent;
import org.bukkit.Bukkit;

public class WatchedDir extends WatchedEntry {
   final File dir;
   Map entries;

   WatchedDir(String path, File dir) {
      super(path);
      this.dir = dir;
      this.entries = new HashMap();
      File[] listing = dir.listFiles();
      if (listing != null) {
         for(File file : listing) {
            String p = path.isEmpty() ? file.getName() : path + "/" + file.getName();
            if (file.isDirectory()) {
               this.entries.put(file.getName(), new WatchedDir(p, file));
            } else {
               this.entries.put(file.getName(), new WatchedFile(p, file));
            }
         }
      }

   }

   public File getUpdateDir() {
      return this.dir;
   }

   public List getEntries() {
      return new ArrayList(this.entries.values());
   }

   public boolean hasChanges(WatchedEntry entry) {
      if (!(entry instanceof WatchedDir)) {
         return true;
      } else {
         WatchedDir curr = (WatchedDir)entry;
         if (curr.entries.size() != this.entries.size()) {
            Bukkit.getPluginManager().callEvent(new FileUpdateEvent(this, curr));
            this.entries = curr.entries;
            return false;
         } else {
            for(Map.Entry otherEntry : curr.entries.entrySet()) {
               WatchedEntry myFile = (WatchedEntry)this.entries.get(otherEntry.getKey());
               if (myFile == null) {
                  Bukkit.getPluginManager().callEvent(new FileUpdateEvent(this, curr));
                  this.entries = curr.entries;
                  return false;
               }

               if (myFile.hasChanges((WatchedEntry)otherEntry.getValue())) {
                  Bukkit.getPluginManager().callEvent(new FileUpdateEvent(this, curr));
                  this.entries = curr.entries;
                  return false;
               }
            }

            return false;
         }
      }
   }

   public String toString() {
      return "Dir: " + this.path + " => " + this.dir.getAbsolutePath();
   }
}
