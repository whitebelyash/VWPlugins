package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import java.util.List;
import net.minecraft.server.v1_6_R3.EntityArrow;
import net.minecraft.server.v1_6_R3.EntityFallingBlock;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.luckyblock.LBActionItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

public class LuckySpiderBow extends LBActionItem {
   protected void populateDrop(List drop, Block block, Player player) {
      ItemStack is = Items.name(Material.BOW, "&aЛук-паук", "&7Стреляет паутиной");
      is = this.lb.controller.setShootBowCallback(this, is);
      is.setDurability((short)(Material.BOW.getMaxDurability() - 9));
      drop.add(is);
      drop.add(new ItemStack(Material.ARROW, 10));
   }

   public void onShootBow(EntityShootBowEvent event) {
      EntityArrow handle = ((CraftArrow)event.getProjectile()).getHandle();
      EntityFallingBlock web = new EntityFallingBlock(handle.world, handle.locX, handle.locY, handle.locZ, Material.WEB.getId());
      web.motX = handle.motX;
      web.motY = handle.motY;
      web.motZ = handle.motZ;
      web.a(false);
      web.dropItem = false;
      web.c = 1;
      web.world.addEntity(web);
      event.setProjectile(web.getBukkitEntity());
   }
}
