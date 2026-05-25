package net.xtrafrancyz.BedWars.game.usables;

import net.xtrafrancyz.BedWars.BedWars;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3i;
import net.xtrafrancyz.VimeNetwork.api.util.E;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public class BridgeBuilder implements Listener {
   public static final ItemStack ITEM_FLAT_BRIDGE;
   public static final ItemStack ITEM_STAIR_BRIDGE;
   private final BedWars plugin;

   public BridgeBuilder(BedWars plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onInteract(PlayerInteractEvent event) {
      if (E.isRightClick(event) && event.hasItem() && event.getItem().getType() == Material.MONSTER_EGG) {
         boolean stairs = false;
         switch (event.getItem().getDurability()) {
            case 98:
               stairs = true;
            case 95:
               event.setCancelled(true);
               Location loc = event.getPlayer().getLocation();
               loc.add((double)0.0F, (double)-1.0F, (double)0.0F);
               float rot = (loc.getYaw() - 180.0F) % 360.0F;
               if (rot < 0.0F) {
                  rot += 360.0F;
               }

               Vector dir;
               byte stairsDirection;
               if (rot >= 0.0F && rot < 45.0F) {
                  stairsDirection = 3;
                  dir = vectorFromBlockFace(BlockFace.NORTH);
               } else if (rot >= 45.0F && rot < 135.0F) {
                  stairsDirection = 0;
                  dir = vectorFromBlockFace(BlockFace.EAST);
               } else if (rot >= 135.0F && rot < 225.0F) {
                  stairsDirection = 2;
                  dir = vectorFromBlockFace(BlockFace.SOUTH);
               } else if (rot >= 225.0F && rot < 315.0F) {
                  stairsDirection = 1;
                  dir = vectorFromBlockFace(BlockFace.WEST);
               } else {
                  stairsDirection = 3;
                  dir = vectorFromBlockFace(BlockFace.NORTH);
               }

               for(int i = 0; i < 9; ++i) {
                  if (stairs && i > 0 && i % 3 == 0) {
                     loc.add((double)0.0F, (double)1.0F, (double)0.0F);
                     loc.subtract(dir);
                     this.set(loc.getBlock(), Material.SANDSTONE_STAIRS, stairsDirection);
                     loc.add(dir);
                  }

                  this.set(loc.getBlock(), Material.SANDSTONE, (byte)2);
                  loc.add(dir);
               }

               PlayerInventory inv = event.getPlayer().getInventory();
               ItemStack used = inv.getItemInHand();
               used.setAmount(used.getAmount() - 1);
               inv.setItem(inv.getHeldItemSlot(), used.getAmount() == 0 ? null : used);
               break;
            default:
               return;
         }
      }

   }

   private void set(Block block, Material type) {
      if (block.getType() == Material.AIR) {
         block.setType(type);
         this.plugin.events.userBlocks.add(new Vec3i(block));
      }

   }

   private void set(Block block, Material type, byte data) {
      if (block.getType() == Material.AIR) {
         block.setTypeIdAndData(type.getId(), data, true);
         this.plugin.events.userBlocks.add(new Vec3i(block));
      }

   }

   private static Vector vectorFromBlockFace(BlockFace face) {
      return new Vector(face.getModX(), face.getModY(), face.getModZ());
   }

   static {
      ITEM_FLAT_BRIDGE = Items.name(new ItemStack(Material.MONSTER_EGG, 1, (short)95), "&bРовный мост", new String[]{"&7Строит перед вами ровный мост на 9 блоков"});
      ITEM_STAIR_BRIDGE = Items.name(new ItemStack(Material.MONSTER_EGG, 1, (short)98), "&bМост со ступеньками", new String[]{"&7Строит перед вами мост, который постепенно поднимается вверх"});
   }
}
