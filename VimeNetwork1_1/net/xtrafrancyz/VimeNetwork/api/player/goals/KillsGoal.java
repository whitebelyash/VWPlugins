package net.xtrafrancyz.VimeNetwork.api.player.goals;

import java.util.ArrayList;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.ServerType;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.inventory.ItemStack;

public class KillsGoal extends ExpCoinsGoal {
   public Material neededWeapon;
   public int streak;

   public KillsGoal() {
      this(0, 0);
   }

   public KillsGoal(int rewardCoins, int rewardExp) {
      super(rewardCoins, rewardExp);
      this.neededWeapon = null;
      this.streak = 0;
   }

   public void write(JsonObject json) {
      super.write(json);
      if (this.neededWeapon != null) {
         json.addProperty("w", this.neededWeapon.getId());
      }

      if (this.streak != 0) {
         json.addProperty("s", this.streak);
      }

   }

   public void read(JsonObject json) {
      super.read(json);
      JsonElement elem = json.get("w");
      if (elem != null) {
         this.neededWeapon = Material.getMaterial(elem.getAsInt());
      }

      elem = json.get("s");
      if (elem != null) {
         this.streak = elem.getAsInt();
      }

   }

   public boolean isApplicable(NetworkPlayer player, GoalQuery query) {
      if (this.streak > 0) {
         Integer s = (Integer)query.data.get("streak");
         return s != null && s == this.streak;
      } else if (this.neededWeapon == null) {
         return true;
      } else {
         ItemStack weapon = (ItemStack)query.data.get("weapon");
         return weapon != null && this.neededWeapon.getId() == weapon.getTypeId();
      }
   }

   public ItemStack getItem() {
      if (this.streak > 0) {
         return new ItemStack(Material.DIAMOND_SWORD);
      } else {
         return this.neededWeapon != null ? new ItemStack(this.neededWeapon) : new ItemStack(Material.GOLD_SWORD);
      }
   }

   public List getText(boolean addGame) {
      List<String> list = new ArrayList(2);
      if (this.streak > 0) {
         list.add("&fУбить &e" + this.streak + U.plurals(this.streak, " человека", " человека", " человек"));
         String text = "&fза одну игру";
         if (addGame) {
            text = text + " на " + ServerType.byId(this.game).getName();
         }

         list.add(text);
      } else {
         String text = "&fУбить &e" + this.getGoal() + U.plurals(this.getGoal(), " человека", " человека", " человек");
         if (addGame) {
            text = text + "&f на " + ServerType.byId(this.game).getName();
         }

         list.add(text);
         if (this.neededWeapon != null) {
            String name = null;
            switch (this.neededWeapon) {
               case BOW:
                  name = "лука";
                  break;
               case DIAMOND_SWORD:
                  name = "алмазного меча";
                  break;
               case IRON_SWORD:
                  name = "железного меча";
                  break;
               case GOLD_SWORD:
                  name = "золотого меча";
                  break;
               case STONE_SWORD:
                  name = "каменного меча";
                  break;
               case WOOD_SWORD:
                  name = "деревянного меча";
            }

            if (name != null) {
               list.add("&fс помощью &e" + name);
            }
         }
      }

      return list;
   }
}
