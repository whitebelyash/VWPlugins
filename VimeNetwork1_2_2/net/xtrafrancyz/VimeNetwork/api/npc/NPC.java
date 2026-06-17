/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.api.npc;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public interface NPC {
    public String getName();

    public Location getLocation();

    public void setName(String var1);

    public int getEntityId();

    public void setLocation(Location var1);

    public void setViewDistance(int var1);

    public void remove();

    public void setItemInHand(ItemStack var1);

    public void setArmor(ItemStack[] var1);

    public void setHelmet(ItemStack var1);

    public void setChestplate(ItemStack var1);

    public void setLeggings(ItemStack var1);

    public void setBoots(ItemStack var1);

    public void setNameVisible(boolean var1);

    public void setData(String var1, Object var2);

    public Object getData(String var1);
}

