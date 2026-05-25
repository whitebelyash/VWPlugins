package net.xtrafrancyz.VimeNetwork.tasks;

import java.util.Iterator;
import net.minecraft.server.v1_6_R3.IInventory;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;

public class MemoryFix implements Runnable {
   public void run() {
      for(World world : Bukkit.getWorlds()) {
         for(Object tile : ((CraftWorld)world).getHandle().tileEntityList) {
            if (tile instanceof IInventory) {
               Iterator<HumanEntity> it = ((IInventory)tile).getViewers().iterator();

               while(it.hasNext()) {
                  HumanEntity he = (HumanEntity)it.next();
                  if (he instanceof CraftPlayer && !VimeNetwork.isPlayerOnline(he.getName())) {
                     it.remove();
                  }
               }
            }
         }
      }

   }
}
