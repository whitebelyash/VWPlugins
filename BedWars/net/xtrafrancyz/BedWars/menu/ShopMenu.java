package net.xtrafrancyz.BedWars.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.server.v1_6_R3.EntityHuman;
import net.minecraft.server.v1_6_R3.EntityPlayer;
import net.minecraft.server.v1_6_R3.IMerchant;
import net.minecraft.server.v1_6_R3.MerchantRecipe;
import net.minecraft.server.v1_6_R3.MerchantRecipeList;
import net.xtrafrancyz.BedWars.BWTeam;
import net.xtrafrancyz.BedWars.Config;
import net.xtrafrancyz.BedWars.PlayerInfo;
import net.xtrafrancyz.BedWars.util.CommonUtils;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class ShopMenu implements IMenu {
   private final Inventory inv = Bukkit.createInventory(this, 18, "Жлобский торговец");
   protected final List shops;

   public ShopMenu() {
      this.shops = new ArrayList(this.inv.getSize());
      this.fill();

      for(int i = 0; i < this.shops.size(); ++i) {
         this.inv.setItem(i, ((Shop)this.shops.get(i)).item);
      }

   }

   protected abstract void fill();

   public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
      if (slot >= 0 && slot < this.shops.size()) {
         Shop shop = (Shop)this.shops.get(slot);
         if (shop != null) {
            shop.open(player);
         }

      }
   }

   public Inventory getInventory() {
      return this.inv;
   }

   protected ItemStack bronze(int amount) {
      ItemStack is = Config.BRONZE.clone();
      is.setAmount(amount);
      return is;
   }

   protected ItemStack iron(int amount) {
      ItemStack is = Config.IRON.clone();
      is.setAmount(amount);
      return is;
   }

   protected ItemStack gold(int amount) {
      ItemStack is = Config.GOLD.clone();
      is.setAmount(amount);
      return is;
   }

   private static boolean isLeather(ItemStack is) {
      switch (is.getType()) {
         case LEATHER_HELMET:
         case LEATHER_BOOTS:
         case LEATHER_CHESTPLATE:
         case LEATHER_LEGGINGS:
            return true;
         default:
            return false;
      }
   }

   protected static class Shop {
      ItemStack item;
      MerchantRecipeList list;

      public Shop(ItemStack item) {
         this.item = item;
         this.list = new MerchantRecipeList();
      }

      public Shop add(ItemStack needed, ItemStack result) {
         this.add(needed, result, this.list);
         return this;
      }

      public Shop add(ItemStack needed, ItemStack needed2, ItemStack result) {
         this.add(needed, needed2, result, this.list);
         return this;
      }

      public void open(Player player) {
         this.open(player, this.list);
      }

      protected void open(Player player, MerchantRecipeList list) {
         FakeMerchant merchant = new FakeMerchant(list);
         EntityPlayer nmsPlayer = ((CraftPlayer)player).getHandle();
         merchant.a_((EntityHuman)nmsPlayer);
         nmsPlayer.openTrade(merchant, this.item.getItemMeta().getDisplayName().substring(2));
      }

      protected void add(ItemStack needed, ItemStack item, MerchantRecipeList list) {
         this.add(needed, (ItemStack)null, item, list);
      }

      protected void add(ItemStack needed, ItemStack needed2, ItemStack result, MerchantRecipeList list) {
         MerchantRecipe recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(needed), needed2 == null ? null : CraftItemStack.asNMSCopy(needed2), CraftItemStack.asNMSCopy(result));
         recipe.a(99999999);
         list.add(recipe);
      }
   }

   protected static class ArmorShop extends Shop {
      Map recipes = new HashMap();
      List orig;

      public ArmorShop(ItemStack item) {
         super(item);
         this.list = null;
         this.orig = new ArrayList(6);
      }

      public Shop add(ItemStack needed, ItemStack item) {
         this.orig.add(new Pair(needed, item));
         return this;
      }

      public void open(Player player) {
         BWTeam team = PlayerInfo.get(player).team;
         if (team != null) {
            MerchantRecipeList list = (MerchantRecipeList)this.recipes.get(team);
            if (list == null) {
               list = new MerchantRecipeList();

               for(Pair pair : this.orig) {
                  ItemStack b = (ItemStack)pair.b;
                  if (ShopMenu.isLeather(b)) {
                     b = CommonUtils.paint(b.clone(), team.color);
                  }

                  this.add((ItemStack)pair.a, b, list);
               }

               this.recipes.put(team, list);
            }

            super.open(player, list);
         }
      }
   }

   private static class Pair {
      public Object a;
      public Object b;

      public Pair(Object a, Object b) {
         this.a = a;
         this.b = b;
      }
   }

   private static class FakeMerchant implements IMerchant {
      EntityHuman human;
      MerchantRecipeList recipes;

      public FakeMerchant(MerchantRecipeList recipes) {
         this.recipes = recipes;
      }

      public void a_(EntityHuman entityHuman) {
         this.human = entityHuman;
      }

      public EntityHuman m_() {
         return this.human;
      }

      public MerchantRecipeList getOffers(EntityHuman entityHuman) {
         return this.recipes;
      }

      public void a(MerchantRecipe recipe) {
         int gold = 0;
         net.minecraft.server.v1_6_R3.ItemStack item = recipe.getBuyItem1();
         if (item != null && item.id == Material.GOLD_INGOT.getId()) {
            gold += item.count;
         }

         item = recipe.getBuyItem2();
         if (item != null && item.id == Material.GOLD_INGOT.getId()) {
            gold += item.count;
         }

         if (gold > 0) {
            PlayerInfo info = PlayerInfo.get(this.human.getName());
            if (info.team != null) {
               BWTeam var10000 = info.team;
               var10000.gamePoints = (float)((double)var10000.gamePoints + 0.007 * (double)gold);
            }

            info.spentGold += gold;
            if (info.spentGold >= 64) {
               VimeNetwork.getPlayer(info.username).getAchievements().complete(Achievement.BW_SHOPPING);
            }
         }

      }

      public void a_(net.minecraft.server.v1_6_R3.ItemStack itemStack) {
      }
   }
}
