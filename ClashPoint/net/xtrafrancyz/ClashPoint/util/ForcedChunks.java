/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  gnu.trove.set.TIntSet
 *  gnu.trove.set.hash.TIntHashSet
 *  org.bukkit.Chunk
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.world.ChunkUnloadEvent
 */
package net.xtrafrancyz.ClashPoint.util;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ForcedChunks
implements Listener {
    private final TIntSet chunks = new TIntHashSet();

    public void addChunk(Chunk chunk) {
        this.chunks.add(ForcedChunks.getId(chunk));
    }

    public void addAndLoadChunk(Chunk chunk) {
        chunk.load();
        this.addChunk(chunk);
    }

    public boolean hasChunk(Chunk chunk) {
        return this.chunks.contains(ForcedChunks.getId(chunk));
    }

    public void clear() {
        this.chunks.clear();
    }

    private static int getId(Chunk chunk) {
        return (short)chunk.getX() << 16 | (short)chunk.getZ() & 0xFFFF;
    }

    @EventHandler
    private void onChunkUnload(ChunkUnloadEvent event) {
        if (this.hasChunk(event.getChunk())) {
            event.setCancelled(true);
        }
    }
}

