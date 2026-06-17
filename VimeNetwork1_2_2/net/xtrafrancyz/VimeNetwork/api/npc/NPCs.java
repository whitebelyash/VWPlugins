/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 */
package net.xtrafrancyz.VimeNetwork.api.npc;

import net.xtrafrancyz.VimeNetwork.api.npc.NPC;
import org.bukkit.Location;

public interface NPCs {
    public NPC create(String var1, Location var2);

    public void remove(int var1);

    public NPC get(int var1);

    public void reset();
}

