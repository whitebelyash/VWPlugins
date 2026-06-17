/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.impl.player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import org.bukkit.entity.Player;

public class MysqlPlayer
extends VPlayer {
    public Set<String> ignored = new HashSet<String>();
    public boolean ignoreAll = false;
    public String lastWriter = null;
    public Map<String, MetaValue> meta = new ConcurrentHashMap<String, MetaValue>();

    public MysqlPlayer(Player player) {
        super(player);
    }

    @Override
    public String getMeta(String key) {
        MetaValue value = this.meta.get(key);
        return value == null ? null : value.value;
    }

    @Override
    public void setMeta(String key, String value) {
        if (value == null) {
            this.removeMeta(key);
            return;
        }
        MetaValue metaValue = this.meta.computeIfAbsent(key, k -> new MetaValue(value));
        metaValue.changed = System.currentTimeMillis();
        metaValue.value = value;
        metaValue.saved = false;
    }

    @Override
    public boolean hasMeta(String key) {
        return this.meta.containsKey(key);
    }

    @Override
    public String removeMeta(String key) {
        MetaValue prev = this.meta.get(key);
        if (prev != null) {
            prev.value = null;
            prev.saved = false;
            prev.changed = System.currentTimeMillis();
        }
        return prev == null ? null : prev.value;
    }

    @Override
    public Map<String, String> getMetaMap() {
        HashMap<String, String> newMap = new HashMap<String, String>(this.meta.size());
        for (Map.Entry<String, MetaValue> entry : this.meta.entrySet()) {
            newMap.put(entry.getKey(), entry.getValue().value);
        }
        return newMap;
    }

    @Override
    public void dispose() {
        this.meta.clear();
        this.lastWriter = null;
        this.ignored.clear();
        super.dispose();
    }

    public static class MetaValue {
        public String value;
        public String prev;
        public long changed;
        public boolean saved = false;

        public MetaValue(String value) {
            this.value = value;
        }
    }
}

