package net.xtrafrancyz.VimeNetwork.api.npc;

import org.bukkit.Location;

public interface NPCs {
   NPC create(String var1, Location var2);

   void remove(int var1);

   NPC get(int var1);

   void reset();
}
