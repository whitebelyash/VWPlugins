/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.SystemUtils
 *  org.bukkit.Bukkit
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.api.updater;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.event.FileUpdateEvent;
import net.xtrafrancyz.VimeNetwork.api.updater.WatchedDir;
import net.xtrafrancyz.VimeNetwork.api.updater.WatchedEntry;
import net.xtrafrancyz.VimeNetwork.api.updater.WatchedFile;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import net.xtrafrancyz.VimeNetwork.tasks.Restart;
import org.apache.commons.lang3.SystemUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class UpdateWatcher
implements Listener {
    public static final File UPDATE_DIR = (SystemUtils.IS_OS_WINDOWS ? new File("C:/VimeWorld/update") : new File("/home/vimeworld/update")).getAbsoluteFile();
    public static final File SERVER_UPDATE_DIR = new File(UPDATE_DIR, VimeNetwork.lobby().getServerTypeId());
    private final VNPlugin plugin;
    private boolean restartNeeded = false;
    private boolean restartScheduled = false;
    private WatchedDir root;

    public UpdateWatcher(VNPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
        if (SERVER_UPDATE_DIR.exists()) {
            try {
                this.root = this.genTree();
            }
            catch (Exception ex) {
                plugin.getLogger().log(Level.WARNING, "[UpdateWatcher] Cant build tree", ex);
                return;
            }
            Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)plugin, this::check, 2400L, 2400L);
        }
    }

    @EventHandler
    private void onUpdate(FileUpdateEvent event) {
        if (event.isFile()) {
            WatchedEntry curr = event.getCurrent();
            switch (curr.path) {
                case "server.properties": 
                case "ops.txt": 
                case "bukkit.yml": 
                case "spigot.yml": 
                case "build.conf": {
                    event.setAction(FileUpdateEvent.Action.RESTART);
                    return;
                }
            }
            if (curr.path.startsWith("plugins/") && curr.path.endsWith(".jar")) {
                event.setAction(FileUpdateEvent.Action.RESTART);
                return;
            }
            if (curr.path.startsWith("configs/") && curr.path.endsWith(".zip")) {
                if (Bukkit.getPluginManager().isPluginEnabled("GameReloader")) {
                    if (VPlayer.PLAYERS.isEmpty()) {
                        event.setAction(FileUpdateEvent.Action.RESTART);
                    } else {
                        this.plugin.getLogger().info("[UpdateWatcher] Configs update found. Ignored due to plugin GameReloader.");
                    }
                } else {
                    event.setAction(FileUpdateEvent.Action.RESTART);
                    return;
                }
            }
            if (!curr.path.contains("/") && curr.path.endsWith(".jar")) {
                event.setAction(FileUpdateEvent.Action.RESTART);
                return;
            }
        } else if (event.isDir() && event.getOld().path.equals("plugins")) {
            List<WatchedEntry> old = ((WatchedDir)event.getOld()).getEntries();
            List<WatchedEntry> curr = ((WatchedDir)event.getCurrent()).getEntries();
            Consumer<List> preparer = list -> {
                list.removeIf(f -> !(f instanceof WatchedFile) || !f.path.endsWith(".jar"));
                list.sort(Comparator.comparing(WatchedEntry::getPath));
            };
            preparer.accept(old);
            preparer.accept(curr);
            if (old.equals(curr)) {
                return;
            }
            event.setAction(FileUpdateEvent.Action.RESTART);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    private void onUpdateEnd(FileUpdateEvent event) {
        if (event.getAction() == FileUpdateEvent.Action.RESTART) {
            this.plugin.getLogger().info("Found update (" + event.getOld() + "). Trying to restart server.");
            this.tryToRestart();
        }
    }

    private WatchedDir genTree() throws Exception {
        WatchedDir root = new WatchedDir("", SERVER_UPDATE_DIR);
        File build = new File(SERVER_UPDATE_DIR, "build.conf");
        if (build.exists()) {
            Files.readAllLines(build.toPath(), StandardCharsets.UTF_8).forEach(line -> {
                if ((line = line.trim()).isEmpty() || line.charAt(0) == '#') {
                    return;
                }
                int index = line.indexOf(":");
                String type = line.substring(0, index);
                line = line.substring(index + 1);
                String[] split = line.split("=>");
                String path = split[0].trim();
                String update = split[1].trim();
                String[] parts = path.split("/");
                WatchedDir t = root;
                for (int i = 0; i < parts.length - 1; ++i) {
                    WatchedEntry entry0 = t.entries.get(parts[i]);
                    if (entry0 == null) {
                        t = new WatchedDir(t.path + "/" + parts[i], new File(t.dir, parts[i]));
                        t.entries.put(parts[i], t);
                        continue;
                    }
                    if (entry0 instanceof WatchedDir) {
                        t = (WatchedDir)entry0;
                        continue;
                    }
                    throw new IllegalArgumentException(path + " has file in path");
                }
                String name = parts[parts.length - 1];
                switch (type) {
                    case "file": {
                        if (t.entries.containsKey(name)) break;
                        t.entries.put(name, new WatchedFile(path, new File(UPDATE_DIR, update)));
                        break;
                    }
                    case "dir": {
                        WatchedDir existing = (WatchedDir)t.entries.get(name);
                        WatchedDir rewrited = new WatchedDir(path, new File(UPDATE_DIR, update));
                        if (existing != null) {
                            this.rewrite(existing, rewrited);
                        }
                        t.entries.put(name, rewrited);
                        break;
                    }
                    default: {
                        this.plugin.getLogger().info("[UpdateWatcher] Skipped unknown build type");
                        return;
                    }
                }
            });
        }
        return root;
    }

    private void rewrite(WatchedDir existed, WatchedDir rewrited) {
        for (Map.Entry<String, WatchedEntry> entry : existed.entries.entrySet()) {
            if (entry.getValue() instanceof WatchedFile) {
                rewrited.entries.put(entry.getKey(), entry.getValue());
                continue;
            }
            WatchedDir rew0 = (WatchedDir)rewrited.entries.get(entry.getKey());
            this.rewrite((WatchedDir)entry.getValue(), rew0);
        }
    }

    private void check() {
        WatchedDir current;
        if (!this.plugin.config.updaterEnabled || this.restartScheduled) {
            return;
        }
        try {
            current = this.genTree();
        }
        catch (Exception ex) {
            this.plugin.getLogger().log(Level.WARNING, "[UpdateWatcher] Cant build tree", ex);
            return;
        }
        this.root.hasChanges(current);
        if (this.restartNeeded) {
            this.tryToRestart();
        }
    }

    public boolean isRestartNeeded() {
        return this.restartNeeded;
    }

    public void tryToRestart() {
        this.restartNeeded = true;
        if (!this.restartScheduled && VPlayer.PLAYERS.isEmpty()) {
            Restart.restart();
            this.restartScheduled = true;
        }
    }
}

