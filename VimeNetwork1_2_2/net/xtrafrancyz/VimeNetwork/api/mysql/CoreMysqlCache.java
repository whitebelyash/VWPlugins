/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.mysql;

import java.util.List;
import java.util.function.Consumer;
import net.xtrafrancyz.Core.network.packet.Packet75MysqlCacheRequest;
import net.xtrafrancyz.Core.network.packet.Packet76MysqlCacheResponse;
import net.xtrafrancyz.Core.network.packet.Packet77MysqlCacheUpdate;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;

public class CoreMysqlCache {
    public static void load(String table, Object key, List<String> columns, Consumer<Object[]> callback) {
        if (VimeNetwork.core().isConnected()) {
            VimeNetwork.core().sendPacket(new Packet75MysqlCacheRequest(table, key, columns), p0 -> callback.accept(((Packet76MysqlCacheResponse)p0).values), 1000L, () -> VNPlugin.instance().getLogger().warning("Cannot load mysql cache from core. Timeout (1s)"));
        } else {
            callback.accept(null);
        }
    }

    public static void save(String table, Object key, List<String> columns, List<Object> values) {
        CoreMysqlCache.save(table, key, columns.toArray(new String[columns.size()]), values.toArray());
    }

    public static void save(String table, Object key, String[] columns, Object[] values) {
        if (VimeNetwork.core().isConnected()) {
            VimeNetwork.core().sendPacket(new Packet77MysqlCacheUpdate(table, key, columns, values));
        }
    }
}

