package net.xtrafrancyz.VimeNetwork.api.npc;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public interface NPC {
   String getName();

   Location getLocation();

   void setName(String var1);

   int getEntityId();

   void setLocation(Location var1);

   void setViewDistance(int var1);

   void remove();

   void setItemInHand(ItemStack var1);

   void setArmor(ItemStack[] var1);

   void setHelmet(ItemStack var1);

   void setChestplate(ItemStack var1);

   void setLeggings(ItemStack var1);

   void setBoots(ItemStack var1);

   void setNameVisible(boolean var1);

   void setData(String var1, Object var2);

   Object getData(String var1);
}
