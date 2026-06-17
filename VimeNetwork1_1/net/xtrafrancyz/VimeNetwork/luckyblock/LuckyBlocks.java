package net.xtrafrancyz.VimeNetwork.luckyblock;

import java.util.function.Consumer;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3i;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class LuckyBlocks {
   private final JavaPlugin plugin;
   public final LBController controller;

   public LuckyBlocks(JavaPlugin plugin) {
      this.plugin = plugin;
      this.controller = new LBController(this);
   }

   public void addAction(int weight, LBAction action) {
      action.plugin = this.plugin;
      action.lb = this;
      action.id = this.controller.actions.size();
      action.register();
      this.controller.actions.add(new LBActionEntry(weight, action));
      LBController var10000 = this.controller;
      var10000.weightLimit += weight;
   }

   public void addBreakListener(Consumer callback) {
      this.controller.luckyBreakListeners.add(callback);
   }

   public Plugin getPlugin() {
      return this.plugin;
   }

   public void setLuckyBlock(Location loc) {
      this.setLuckyBlock(loc.getBlock());
   }

   public void setLuckyBlock(Block block) {
      Vec3i vec = new Vec3i(block);
      Consumer<BlockBreakEvent> breakCallback = (Consumer)this.controller.blockBreak.remove(vec);
      if (breakCallback != null) {
         breakCallback.accept(new BlockBreakEvent(block, (Player)null));
      }

      this.controller.blockInteract.remove(vec);
      block.setTypeIdAndData(Material.SPONGE.getId(), (byte)1, true);
   }

   public static boolean isLuckyBlock(Block block) {
      return block.getTypeId() == Material.SPONGE.getId();
   }

   public static boolean isLuckyBlock(ItemStack is) {
      return is.getTypeId() == Material.SPONGE.getId();
   }

   public static ItemStack getLuckyBlock() {
      return Items.name(new ItemStack(Material.SPONGE, 1, (short)1), "&eLucky Block");
   }
}
