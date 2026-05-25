package net.xtrafrancyz.BedWars.game.usables;

import net.xtrafrancyz.BedWars.BWTeam;
import net.xtrafrancyz.BedWars.PlayerInfo;
import net.xtrafrancyz.BedWars.util.CommonUtils;
import net.xtrafrancyz.VimeNetwork.api.util.E;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ItemPackage implements Listener {
   private static final ItemStack[] PACKAGE_1;
   private static final ItemStack[] PACKAGE_2;
   private static final ItemStack[] PACKAGE_3;
   private static final ItemStack[] PACKAGE_4;
   public static final ItemStack ITEM_LVL_1;
   public static final ItemStack ITEM_LVL_2;
   public static final ItemStack ITEM_LVL_3;
   public static final ItemStack ITEM_LVL_4;

   @EventHandler
   public void onInteract(PlayerInteractEvent event) {
      if (E.isRightClick(event) && event.hasItem() && event.getItem().getType() == Material.WORKBENCH) {
         ItemStack[] items;
         switch (Items.nbt(event.getItem()).getByte("lvl")) {
            case 1:
               items = PACKAGE_1;
               break;
            case 2:
               items = PACKAGE_2;
               break;
            case 3:
               items = PACKAGE_3;
               break;
            case 4:
               items = PACKAGE_4;
               break;
            default:
               return;
         }

         event.setCancelled(true);
         PlayerInventory inv = event.getPlayer().getInventory();
         ItemStack used = inv.getItemInHand();
         used.setAmount(used.getAmount() - 1);
         inv.setItem(inv.getHeldItemSlot(), used.getAmount() == 0 ? null : used);
         ItemStack[] var5 = items;
         int var6 = items.length;
         int var7 = 0;

         while(var7 < var6) {
            ItemStack is = var5[var7];
            ItemStack clone = is.clone();
            switch (clone.getType()) {
               case LEATHER_BOOTS:
               case LEATHER_CHESTPLATE:
               case LEATHER_HELMET:
               case LEATHER_LEGGINGS:
                  BWTeam team = PlayerInfo.get(event.getPlayer()).team;
                  if (team != null) {
                     clone = CommonUtils.paint(clone, team.color);
                  }
               default:
                  inv.addItem(new ItemStack[]{clone});
                  ++var7;
            }
         }

         event.getPlayer().updateInventory();
      }

   }

   static {
      PACKAGE_1 = new ItemStack[]{Items.enchant(new ItemStack(Material.STICK), new Items.Ench[]{new Items.Ench(Enchantment.KNOCKBACK, 1)}), Items.enchant(new ItemStack(Material.WOOD_PICKAXE), new Items.Ench[]{new Items.Ench(Enchantment.DIG_SPEED, 3), new Items.Ench(Enchantment.DURABILITY, 1)}), new ItemStack(Material.SANDSTONE, 32, (short)2)};
      PACKAGE_2 = new ItemStack[]{Items.enchant(new ItemStack(Material.STICK), new Items.Ench[]{new Items.Ench(Enchantment.KNOCKBACK, 1)}), Items.enchant(new ItemStack(Material.WOOD_PICKAXE), new Items.Ench[]{new Items.Ench(Enchantment.DIG_SPEED, 3), new Items.Ench(Enchantment.DURABILITY, 1)}), new ItemStack(Material.SANDSTONE, 48, (short)2), Items.name(new ItemStack(Material.GRILLED_PORK, 3), "&fМясо единорога", new String[0]), Items.enchant(new ItemStack(Material.LEATHER_HELMET), new Items.Ench[]{new Items.Ench(Enchantment.PROTECTION_ENVIRONMENTAL, 1), new Items.Ench(Enchantment.DURABILITY, 1)}), Items.enchant(new ItemStack(Material.LEATHER_LEGGINGS), new Items.Ench[]{new Items.Ench(Enchantment.PROTECTION_ENVIRONMENTAL, 1), new Items.Ench(Enchantment.DURABILITY, 1)}), Items.enchant(new ItemStack(Material.LEATHER_BOOTS), new Items.Ench[]{new Items.Ench(Enchantment.PROTECTION_ENVIRONMENTAL, 1), new Items.Ench(Enchantment.DURABILITY, 1)})};
      PACKAGE_3 = new ItemStack[]{Items.enchant(Items.name(Material.GOLD_SWORD, "&fЗолотой меч уровень 1", new String[0]), new Items.Ench[]{new Items.Ench(Enchantment.DAMAGE_ALL, 1), new Items.Ench(Enchantment.DURABILITY, 1)}), Items.enchant(new ItemStack(Material.STONE_PICKAXE), new Items.Ench[]{new Items.Ench(Enchantment.DIG_SPEED, 4), new Items.Ench(Enchantment.DURABILITY, 1)}), new ItemStack(Material.SANDSTONE, 64, (short)2), Items.name(new ItemStack(Material.GRILLED_PORK, 6), "&fМясо единорога", new String[0]), Items.enchant(new ItemStack(Material.LEATHER_HELMET), new Items.Ench[]{new Items.Ench(Enchantment.PROTECTION_ENVIRONMENTAL, 1), new Items.Ench(Enchantment.DURABILITY, 1)}), Items.enchant(Items.name(Material.CHAINMAIL_CHESTPLATE, "&bКольчуга уровень 1", new String[0]), new Items.Ench[]{new Items.Ench(Enchantment.PROTECTION_ENVIRONMENTAL, 1), new Items.Ench(Enchantment.DURABILITY, 1)}), Items.enchant(new ItemStack(Material.LEATHER_LEGGINGS), new Items.Ench[]{new Items.Ench(Enchantment.PROTECTION_ENVIRONMENTAL, 1), new Items.Ench(Enchantment.DURABILITY, 1)}), Items.enchant(new ItemStack(Material.LEATHER_BOOTS), new Items.Ench[]{new Items.Ench(Enchantment.PROTECTION_ENVIRONMENTAL, 1), new Items.Ench(Enchantment.DURABILITY, 1)})};
      PACKAGE_4 = new ItemStack[]{Items.enchant(Items.name(Material.GOLD_SWORD, "&fЗолотой меч уровень 2", new String[0]), new Items.Ench[]{new Items.Ench(Enchantment.DAMAGE_ALL, 2), new Items.Ench(Enchantment.DURABILITY, 1)}), Items.enchant(new ItemStack(Material.STONE_PICKAXE), new Items.Ench[]{new Items.Ench(Enchantment.DIG_SPEED, 4), new Items.Ench(Enchantment.DURABILITY, 1)}), new ItemStack(Material.SANDSTONE, 64, (short)2), new ItemStack(Material.SANDSTONE, 64, (short)2), Items.name(new ItemStack(Material.GRILLED_PORK, 10), "&fМясо единорога", new String[0]), Items.enchant(new ItemStack(Material.LEATHER_HELMET), new Items.Ench[]{new Items.Ench(Enchantment.PROTECTION_ENVIRONMENTAL, 1), new Items.Ench(Enchantment.DURABILITY, 1)}), Items.enchant(Items.name(Material.CHAINMAIL_CHESTPLATE, "&bКольчуга уровень 3", new String[0]), new Items.Ench[]{new Items.Ench(Enchantment.PROTECTION_ENVIRONMENTAL, 3), new Items.Ench(Enchantment.DURABILITY, 1)}), Items.enchant(new ItemStack(Material.LEATHER_LEGGINGS), new Items.Ench[]{new Items.Ench(Enchantment.PROTECTION_ENVIRONMENTAL, 1), new Items.Ench(Enchantment.DURABILITY, 1)}), Items.enchant(new ItemStack(Material.LEATHER_BOOTS), new Items.Ench[]{new Items.Ench(Enchantment.PROTECTION_ENVIRONMENTAL, 1), new Items.Ench(Enchantment.DURABILITY, 1)})};
      ITEM_LVL_1 = Items.nbt(Items.name(Material.WORKBENCH, "&aПакет вещей 1", new String[]{"&7- Палка на отдачу", "&7- Деревянная кирка", "&7- 32 песчаника"})).setByte("lvl", (byte)1).build();
      ITEM_LVL_2 = Items.nbt(Items.name(Material.WORKBENCH, "&aПакет вещей 2", new String[]{"&7- Палка на отдачу", "&7- Деревянная кирка", "&7- 48 песчаника", "&7- 3 куска мяса единорога", "&7- Кожаный шлем", "&7- Кожаные поножи", "&7- Кожаные ботинки"})).setByte("lvl", (byte)2).build();
      ITEM_LVL_3 = Items.nbt(Items.name(Material.WORKBENCH, "&aПакет вещей 3", new String[]{"&7- Золотой меч уровень 1", "&7- Каменная кирка", "&7- 64 песчаника", "&7- 6 кусков мяса единорога", "&7- Кожаный шлем", "&7- Кольчуга уровень 1", "&7- Кожаные поножи", "&7- Кожаные ботинки"})).setByte("lvl", (byte)3).build();
      ITEM_LVL_4 = Items.nbt(Items.name(Material.WORKBENCH, "&aПакет вещей 4", new String[]{"&7- Золотой меч уровень 2", "&7- Каменная кирка", "&7- 128 песчаника", "&7- 10 кусков мяса единорога", "&7- Кожаный шлем", "&7- Кольчуга уровень 3", "&7- Кожаные поножи", "&7- Кожаные ботинки"})).setByte("lvl", (byte)4).build();
   }
}
