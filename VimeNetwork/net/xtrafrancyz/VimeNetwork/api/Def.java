package net.xtrafrancyz.VimeNetwork.api;

import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Def {
   public static final String SERVICE_LORE = "VimeWorld.ru";
   public static final ItemStack ITEM_GAME_SELECT;
   public static final ItemStack ITEM_TEAM_SELECT;
   public static final ItemStack ITEM_TO_LOBBY;
   public static final ItemStack ITEM_TRAILS;
   public static final ItemStack ITEM_MICRO_UPGRADES;
   public static final ItemStack ITEM_TARGET_COMPASS;
   public static final ItemStack ITEM_ACHIEVEMENTS;
   public static final ItemStack ITEM_SETTINGS;

   private Def() {
   }

   public static ItemStack getSettingsItem(Player player) {
      return getSettingsItem(VimeNetwork.getPlayer(player));
   }

   public static ItemStack getSettingsItem(NetworkPlayer player) {
      return Items.menuTitle(ITEM_SETTINGS.getType(), player.getPrefixedName(), "VimeWorld.ru");
   }

   public static boolean isServiceItem(ItemStack is) {
      return "VimeWorld.ru".equals(Items.getLore(is, -1));
   }

   static {
      ITEM_GAME_SELECT = Items.menuTitle(Material.SLIME_BALL, "Выбор игры", "VimeWorld.ru");
      ITEM_TEAM_SELECT = Items.menuTitle(Material.NAME_TAG, "Выбор команды", "VimeWorld.ru");
      ITEM_TO_LOBBY = Items.menuTitle(Material.COMPASS, "Вернуться в лобби", "VimeWorld.ru");
      ITEM_TRAILS = Items.menuTitle(new ItemStack(Material.INK_SACK, 1, (short)11), "Выбор следа", "VimeWorld.ru");
      ITEM_MICRO_UPGRADES = Items.menuTitle(Material.EYE_OF_ENDER, "Меню микропрокачек", "VimeWorld.ru");
      ITEM_TARGET_COMPASS = Items.setLore(new ItemStack(Material.COMPASS), "VimeWorld.ru");
      ITEM_ACHIEVEMENTS = Items.menuTitle(new ItemStack(Material.ENCHANTED_BOOK), "Достижения", "VimeWorld.ru");
      ITEM_SETTINGS = Items.menuTitle(new ItemStack(Material.NETHER_STAR), "Информация о вас", "VimeWorld.ru");
   }
}
