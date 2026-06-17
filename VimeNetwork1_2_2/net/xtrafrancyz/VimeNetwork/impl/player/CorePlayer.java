/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.impl.player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.xtrafrancyz.Core.network.packet.Packet11PlayerGiveExpSimple;
import net.xtrafrancyz.Core.network.packet.Packet6PlayerMetaChange;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import org.bukkit.entity.Player;

public class CorePlayer
extends VPlayer {
    public Map<String, String> meta = new ConcurrentHashMap<String, String>();

    public CorePlayer(Player player) {
        super(player);
    }

    @Override
    public String getMeta(String key) {
        return this.meta.get(key);
    }

    @Override
    public void setMeta(String key, String value) {
        if (value == null) {
            this.removeMeta(key);
        } else {
            String prev = this.meta.put(key, value);
            if (!value.equals(prev)) {
                this.plugin.core.sendPacket(new Packet6PlayerMetaChange(this.id, key, value));
            }
        }
    }

    @Override
    public boolean hasMeta(String key) {
        return this.meta.containsKey(key);
    }

    @Override
    public String removeMeta(String key) {
        String prev = this.meta.remove(key);
        if (prev != null) {
            this.plugin.core.sendPacket(new Packet6PlayerMetaChange(this.id, key, null));
        }
        return prev;
    }

    @Override
    public Map<String, String> getMetaMap() {
        return this.meta;
    }

    @Override
    public void giveExpExact(int exp) {
        super.giveExpExact(exp);
        if (exp > 0) {
            this.plugin.core.sendPacket(new Packet11PlayerGiveExpSimple(this.id, exp));
        }
    }

    @Override
    public void dispose() {
        this.meta.clear();
        super.dispose();
    }
}

