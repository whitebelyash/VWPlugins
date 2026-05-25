package net.xtrafrancyz.VimeNetwork.api.util;

import java.util.Map;
import net.minecraft.server.v1_6_R3.Container;
import net.minecraft.server.v1_6_R3.ContainerChest;
import net.minecraft.server.v1_6_R3.EntityPlayer;
import net.minecraft.server.v1_6_R3.IInventory;
import net.minecraft.server.v1_6_R3.Packet100OpenWindow;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventoryCrafting;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class Invs {
   public static void forceOpen(HumanEntity bukkitPlayer, Inventory inv) {
      IInventory iinventory = ((CraftInventory)inv).getInventory();
      EntityPlayer player = ((CraftPlayer)bukkitPlayer).getHandle();
      Container container = CraftEventFactory.callInventoryOpenEvent(player, new ContainerChest(player.inventory, iinventory));
      if (container != null) {
         if (player.activeContainer != null) {
            CraftEventFactory.handleInventoryCloseEvent(player);
            player.activeContainer.b(player);
         }

         int containerCounter = player.nextContainerCounter();
         player.playerConnection.sendPacket(new Packet100OpenWindow(containerCounter, 0, iinventory.getName(), iinventory.getSize(), iinventory.c()));
         player.activeContainer = container;
         player.activeContainer.windowId = containerCounter;
         player.activeContainer.addSlotListener(player);
      }

   }

   public static void forceOpen(HumanEntity bukkitPlayer, InventoryHolder menu) {
      forceOpen(bukkitPlayer, menu.getInventory());
   }

   public static void clear(HumanEntity entity) {
      entity.getInventory().clear();
      entity.getInventory().setArmorContents(new ItemStack[4]);
      Inventory top = entity.getOpenInventory().getTopInventory();
      if (top instanceof CraftInventoryCrafting) {
         CraftInventoryCrafting inv = (CraftInventoryCrafting)top;
         inv.setMatrix(new ItemStack[inv.getMatrixInventory().getSize()]);
         inv.setResult((ItemStack)null);
      }

   }

   public static int count(Inventory inv, Material type) {
      return inv.all(type).values().stream().mapToInt(ItemStack::getAmount).sum();
   }

   public static int count(Inventory inv, ItemStack is) {
      return inv.all(is).values().stream().mapToInt(ItemStack::getAmount).sum();
   }

   public static int take(Inventory inv, Material type, int amount) {
      for(Map.Entry entry : inv.all(type).entrySet()) {
         ItemStack is = (ItemStack)entry.getValue();
         if (is.getAmount() <= amount) {
            inv.setItem((Integer)entry.getKey(), (ItemStack)null);
            amount -= is.getAmount();
         } else {
            is.setAmount(is.getAmount() - amount);
            inv.setItem((Integer)entry.getKey(), is);
            amount = 0;
         }

         if (amount == 0) {
            break;
         }
      }

      return amount;
   }
}
