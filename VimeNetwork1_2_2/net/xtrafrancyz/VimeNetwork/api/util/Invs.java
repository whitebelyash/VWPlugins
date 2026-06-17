/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.Container
 *  net.minecraft.server.v1_6_R3.ContainerChest
 *  net.minecraft.server.v1_6_R3.EntityHuman
 *  net.minecraft.server.v1_6_R3.EntityPlayer
 *  net.minecraft.server.v1_6_R3.ICrafting
 *  net.minecraft.server.v1_6_R3.IInventory
 *  net.minecraft.server.v1_6_R3.Packet
 *  net.minecraft.server.v1_6_R3.Packet100OpenWindow
 *  net.minecraft.server.v1_6_R3.Packet103SetSlot
 *  org.bukkit.Material
 *  org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer
 *  org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory
 *  org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventory
 *  org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventoryCrafting
 *  org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.api.util;

import java.util.Map;
import net.minecraft.server.v1_6_R3.Container;
import net.minecraft.server.v1_6_R3.ContainerChest;
import net.minecraft.server.v1_6_R3.EntityHuman;
import net.minecraft.server.v1_6_R3.EntityPlayer;
import net.minecraft.server.v1_6_R3.ICrafting;
import net.minecraft.server.v1_6_R3.IInventory;
import net.minecraft.server.v1_6_R3.Packet;
import net.minecraft.server.v1_6_R3.Packet100OpenWindow;
import net.minecraft.server.v1_6_R3.Packet103SetSlot;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class Invs {
    public static void forceOpen(HumanEntity bukkitPlayer, Inventory inv) {
        IInventory iinventory = ((CraftInventory)inv).getInventory();
        EntityPlayer player = ((CraftPlayer)bukkitPlayer).getHandle();
        Container container = CraftEventFactory.callInventoryOpenEvent((EntityPlayer)player, (Container)new ContainerChest((IInventory)player.inventory, iinventory));
        if (container != null) {
            if (player.activeContainer != null) {
                CraftEventFactory.handleInventoryCloseEvent((EntityHuman)player);
                player.activeContainer.b((EntityHuman)player);
            }
            int containerCounter = player.nextContainerCounter();
            player.playerConnection.sendPacket((Packet)new Packet100OpenWindow(containerCounter, 0, iinventory.getName(), iinventory.getSize(), iinventory.c()));
            player.activeContainer = container;
            player.activeContainer.windowId = containerCounter;
            player.activeContainer.addSlotListener((ICrafting)player);
        }
    }

    public static void forceOpen(HumanEntity bukkitPlayer, InventoryHolder menu) {
        Invs.forceOpen(bukkitPlayer, menu.getInventory());
    }

    public static void sendItem(HumanEntity bukkitPlayer, Inventory inv, int slot, ItemStack item) {
        if (Invs.isInMenu(bukkitPlayer, inv)) {
            EntityPlayer player = ((CraftPlayer)bukkitPlayer).getHandle();
            player.playerConnection.sendPacket((Packet)new Packet103SetSlot(player.activeContainer.windowId, slot, CraftItemStack.asNMSCopy((ItemStack)item)));
        }
    }

    public static boolean isInMenu(HumanEntity bukkitPlayer, Inventory inv) {
        Container c = ((CraftPlayer)bukkitPlayer).getHandle().activeContainer;
        return c instanceof ContainerChest && ((ContainerChest)c).container == ((CraftInventory)inv).getInventory();
    }

    public static void clear(HumanEntity entity) {
        entity.getInventory().clear();
        entity.getInventory().setArmorContents(new ItemStack[4]);
        Inventory top = entity.getOpenInventory().getTopInventory();
        if (top instanceof CraftInventoryCrafting) {
            CraftInventoryCrafting inv = (CraftInventoryCrafting)top;
            inv.setMatrix(new ItemStack[inv.getMatrixInventory().getSize()]);
            inv.setResult(null);
        }
    }

    public static int count(Inventory inv, Material type) {
        return inv.all(type).values().stream().mapToInt(ItemStack::getAmount).sum();
    }

    public static int count(Inventory inv, ItemStack is) {
        return inv.all(is).values().stream().mapToInt(ItemStack::getAmount).sum();
    }

    public static int take(Inventory inv, Material type, int amount) {
        for (Map.Entry entry : inv.all(type).entrySet()) {
            ItemStack is = (ItemStack)entry.getValue();
            if (is.getAmount() <= amount) {
                inv.setItem(((Integer)entry.getKey()).intValue(), null);
                amount -= is.getAmount();
            } else {
                is.setAmount(is.getAmount() - amount);
                inv.setItem(((Integer)entry.getKey()).intValue(), is);
                amount = 0;
            }
            if (amount != 0) continue;
            break;
        }
        return amount;
    }
}

