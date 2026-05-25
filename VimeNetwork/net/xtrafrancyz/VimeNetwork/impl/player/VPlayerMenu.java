package net.xtrafrancyz.VimeNetwork.impl.player;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.xtrafrancyz.Commons.Leveling;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.menu.ConfirmMenu;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.player.Multiplier;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.OwnedMultiplier;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.player.Stat;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.player.treasure.TreasureType;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class VPlayerMenu implements IMenu {
   private static final int SLOT_ACHIEVEMENTS = 20;
   private static final int SLOT_GOALS = 22;
   private static final int SLOT_ARROW_TRAIL = 24;
   private static final int SLOT_RANK = 1;
   private static final int SLOT_COINS = 3;
   private static final int SLOT_LEVELING = 5;
   private static final int SLOT_TREASURES = 7;
   private static final int SLOT_SETTINGS = 41;
   private static final int SLOT_MULTIPLIERS = 39;
   public final VPlayer player;
   public final Inventory inv;

   public VPlayerMenu(Player player) {
      this(VimeNetwork.getPlayer(player));
   }

   public VPlayerMenu(NetworkPlayer player) {
      this.player = (VPlayer)player;
      this.inv = Bukkit.createInventory(this, 45, player.getName());
      this.inv.setItem(1, Items.name(Material.DIAMOND, "&2Ваш статус:", player.getRank().getDisplayName()));
      this.inv.setItem(3, Items.name(Material.GOLD_NUGGET, "&2У вас на руках:", "&e" + U.pluralsCoins(player.getCoins())));
      int expToNextLevel = Leveling.getExpToNextLevel(player.getLevel());
      float progress = (float)player.getPartialExp() / (float)expToNextLevel;
      this.inv.setItem(5, Items.name(Material.EXP_BOTTLE, "&b&lУровень", "&eТекущий уровень: &f" + player.getLevel() + " (" + (int)(progress * 100.0F) + "%)", "&eПрогресс: &f" + player.getPartialExp() + "&e/&f" + expToNextLevel, "&f[" + U.genBar(48, progress, '|', "&7", "&a") + "&f]", "", "&aНажмите для просмотра наград"));
      this.inv.setItem(7, Items.name(Material.STORAGE_MINECART, "&2Сокровищница: &e", TreasureType.BASIC.name + "&f: " + player.getTreasures().get(TreasureType.BASIC), TreasureType.ANCIENT.name + "&f: " + player.getTreasures().get(TreasureType.ANCIENT), TreasureType.MYTHICAL.name + "&f: " + player.getTreasures().get(TreasureType.MYTHICAL)));
      int completed = player.getAchievements().getCompletedCount();
      int total = Achievement.getAchievements().size();
      this.inv.setItem(20, Items.name(Material.ENCHANTED_BOOK, "&b&lДостижения", "&fВыполнено: &a" + completed + "&7/" + total + " (" + Math.round((float)(100 * completed / total)) + "%)", "", "&aНажмите для просмотра"));
      this.inv.setItem(22, Items.name(Material.PAPER, "&b&lЗадания", "&fАктивно: &a" + player.getGoals().getActiveGoals().size(), "&fВыполнено: &a" + player.getStats().get(Stat.GOAL_COMPLETE), "", "&aНажмите для просмотра"));
      this.inv.setItem(24, Items.name(Material.ARROW, "&b&lСлед стрелы", player.getArrowTrail() == null ? "&fНе выбрано" : "&fВыбрано: &a" + player.getArrowTrail().getName(), "", "&aНажмите для выбора"));
      this.inv.setItem(41, Items.name(Material.REDSTONE_COMPARATOR, "&b&lНастройки", "", "&aНажмите для выбора"));
      int rmult = player.getMultipliers().getRankMultiplier();
      int emult = player.getMultipliers().getExtraMultiplier();
      this.inv.setItem(39, Items.name(Material.GOLD_INGOT, "&b&lМножитель коинов", "&fМножитель статуса: " + (rmult == 1 ? "x1" : "&ax" + rmult), "&fДополнительный: " + (emult == 0 ? "&7нет" : "&ax" + (emult + 1)), "", "&aНажмите для просмотра доступных множителей"));
   }

   public void onClick(ItemStack is, Player bukkitPlayer, int slot, ClickType clickType) {
      switch (slot) {
         case 5:
            (new LevelingMenu(this.player, this)).show(bukkitPlayer);
            break;
         case 20:
            (new VAchievementMenu(this.player)).show(bukkitPlayer);
            break;
         case 22:
            this.player.getGoals().openInventory();
            break;
         case 24:
            (new VArrowTrailMenu(this.inv, this.player)).show(bukkitPlayer);
            break;
         case 39:
            (new MultipliersMenu(this.player, this)).show(bukkitPlayer);
            break;
         case 41:
            (new SettingsMenu(this.player, this)).show(bukkitPlayer);
      }

   }

   public Inventory getInventory() {
      return this.inv;
   }

   private static class LevelingMenu implements IMenu {
      private static final int[] SLOTS = new int[]{0, 1, 2, 11, 20, 19, 18, 27, 36, 45, 46, 47, 48, 39, 30, 31, 32, 41, 50, 51, 52, 53, 44, 35, 26, 25, 24, 15, 6, 7, 8};
      private final Inventory inv;
      private final VPlayerMenu parent;
      private final VPlayer player;
      private int page = 0;
      private boolean hasNextPage;

      public LevelingMenu(VPlayer player, VPlayerMenu parent) {
         this.parent = parent;
         this.player = player;
         this.inv = Bukkit.createInventory(this, 54, "Уровень VimeWorld");
         this.inv.setItem(4, Items.name(Material.BED, "&f← &eНазад"));
         int expToNextLevel = Leveling.getExpToNextLevel(player.getLevel());
         float progress = (float)player.getPartialExp() / (float)expToNextLevel;
         this.inv.setItem(13, Items.name(Material.WRITTEN_BOOK, "&eТекущий уровень: &f" + player.getLevel() + " (" + (int)(progress * 100.0F) + "%)", "&eПрогресс: &f" + player.getPartialExp() + "&e/&f" + expToNextLevel, "&f[" + U.genBar(48, progress, '|', "&7", "&a") + "&f]"));
         this.update();
      }

      private void update() {
         for(int slot : SLOTS) {
            this.inv.setItem(slot, (ItemStack)null);
         }

         this.hasNextPage = true;
         int rewardTaken = this.getRewardTaken();
         List<String> lore = new ArrayList();
         int max = this.page > 0 ? SLOTS.length - 1 : SLOTS.length;
         int level = getPageStartLevel(this.page);

         for(int i = this.page > 0 ? 1 : 0; i < max; ++i) {
            ++level;
            if (LevelingRewards.REWARDS.size() <= level) {
               this.hasNextPage = false;
               break;
            }

            lore.addAll(((LevelingRewards.LevelingReward)LevelingRewards.REWARDS.get(level)).getText());
            lore.add("");
            ItemStack is;
            if (level <= this.player.getLevel()) {
               if (level > rewardTaken) {
                  is = new ItemStack(Material.GOLD_BLOCK);
                  lore.add("&aНажмите, чтобы забрать награду");
               } else {
                  is = new ItemStack(Material.IRON_BLOCK);
                  lore.add("&7Вы уже получили награду");
               }
            } else {
               is = new ItemStack(Material.COAL_BLOCK);
               lore.add("&cВаш уровень слишком мал");
            }

            this.inv.setItem(SLOTS[i], Items.name(is, "&d&lНаграда за " + level + " уровень", lore));
            lore.clear();
         }

         if (this.page > 0) {
            this.inv.setItem(SLOTS[0], Items.name(Material.SIGN, "&f← &eПредыдущая страница", "&fУровни: &e" + (getPageStartLevel(this.page - 1) + 1) + "&f...&e" + getPageStartLevel(this.page)));
         }

         if (this.hasNextPage) {
            if (level + 1 >= LevelingRewards.REWARDS.size()) {
               this.inv.setItem(SLOTS[SLOTS.length - 1], Items.name(Material.PAPER, "&eНаграды кончились", "&fНо вы все равно сможете дальше", "&fпрокачивать свой уровень"));
               this.hasNextPage = false;
            } else {
               this.inv.setItem(SLOTS[SLOTS.length - 1], Items.name(Material.SIGN, "&eСледующая страница &f→", "&fУровни: &e" + (level + 1) + "&f...&e" + getPageStartLevel(this.page + 2)));
            }
         }

      }

      private static int getPageStartLevel(int page) {
         if (page > 1) {
            return SLOTS.length - 1 + (page - 1) * (SLOTS.length - 2);
         } else {
            return page == 1 ? SLOTS.length - 1 : 0;
         }
      }

      private int getRewardTaken() {
         String val = this.player.getMeta("lvl-reward");
         if (val != null) {
            try {
               return Integer.parseInt(val);
            } catch (Exception var3) {
               this.player.removeMeta("lvl-reward");
            }
         }

         return 0;
      }

      public void onClick(ItemStack is, Player bukkitPlayer, int slot, ClickType clickType) {
         if (slot == 4) {
            this.parent.show(bukkitPlayer);
         } else {
            if (is != null) {
               if (this.page > 0 && slot == SLOTS[0]) {
                  --this.page;
                  this.update();
                  return;
               }

               if (this.hasNextPage && slot == SLOTS[SLOTS.length - 1]) {
                  ++this.page;
                  this.update();
                  return;
               }

               int levelClicked = -1;
               int i = 0;

               while(true) {
                  if (i < SLOTS.length) {
                     if (SLOTS[i] != slot) {
                        ++i;
                        continue;
                     }

                     levelClicked = i;
                  }

                  if (levelClicked == -1) {
                     return;
                  }

                  levelClicked += getPageStartLevel(this.page) + (this.page > 0 ? 0 : 1);
                  if (levelClicked > this.player.getLevel()) {
                     return;
                  }

                  if (levelClicked <= 0 || levelClicked >= LevelingRewards.REWARDS.size()) {
                     return;
                  }

                  i = this.getRewardTaken();
                  if (levelClicked <= i) {
                     return;
                  }

                  if (i - levelClicked != -1) {
                     U.msg(bukkitPlayer, (String[])("&cПожалуйста, забирайте награды по порядку..."));
                     return;
                  }

                  LevelingRewards.LevelingReward reward = (LevelingRewards.LevelingReward)LevelingRewards.REWARDS.get(levelClicked);
                  reward.accept(this.player);
                  List<String> lore = new ArrayList();
                  lore.addAll(reward.getText());
                  lore.add("");
                  lore.add("&e&lНаграда получена!");
                  this.inv.setItem(slot, Items.name(Material.IRON_BLOCK, "&d&lНаграда за " + levelClicked + " уровень", lore));
                  this.player.setMeta("lvl-reward", String.valueOf(levelClicked));
                  break;
               }
            }

         }
      }

      public Inventory getInventory() {
         return this.inv;
      }
   }

   private static class MultipliersMenu implements IMenu {
      private final Inventory inv;
      private final VPlayerMenu parent;
      private final VPlayer player;
      private final TIntObjectMap mapping;

      public MultipliersMenu(VPlayer player, VPlayerMenu parent) {
         this.parent = parent;
         this.player = player;
         this.mapping = new TIntObjectHashMap();
         this.inv = Bukkit.createInventory(this, 54, "Множители коинов");
         this.inv.setItem(4, Items.name(Material.BED, "&f← &eНазад"));
         this.update();
      }

      private void update() {
         for(int i = 19; i < this.mapping.size(); ++i) {
            this.inv.setItem(i, (ItemStack)null);
         }

         this.mapping.clear();
         List<OwnedMultiplier> list = this.player.getMultipliers().list();
         list.sort((a, b) -> {
            int mult = a.getMultiplier().getMultiplier() - b.getMultiplier().getMultiplier();
            return mult != 0 ? mult : a.getMultiplier().getDuration() - b.getMultiplier().getDuration();
         });
         int slot = 9;
         int lastMultiplier = 0;

         for(OwnedMultiplier owned : list) {
            if (lastMultiplier != owned.getMultiplier().getMultiplier()) {
               slot += 9;
               slot /= 9;
               slot *= 9;
               lastMultiplier = owned.getMultiplier().getMultiplier();
            }

            ItemStack is = Items.name(new ItemStack(Material.GOLD_INGOT, owned.getAmount()), "&fМножитель " + owned.getMultiplier().getText("&e", "&f"), "&fКоличество: &a" + owned.getAmount(), "", "&aНажмите для активации");
            this.mapping.put(slot, owned.getMultiplier());
            this.inv.setItem(slot++, is);
         }

      }

      public void onClick(ItemStack is, Player bukkitPlayer, int slot, ClickType clickType) {
         if (slot == 4) {
            this.parent.show(bukkitPlayer);
         } else {
            Multiplier mult = (Multiplier)this.mapping.get(slot);
            if (mult != null) {
               ConfirmMenu menu = new ConfirmMenu(this.inv, () -> {
                  this.player.getMultipliers().activate(mult);
                  this.update();
                  U.msg(bukkitPlayer, (String[])("&aМножитель " + mult.getText("&e", "&a") + " успешно активирован"));
               }, "Подтверждение активации x" + mult.getMultiplier());
               menu.setBackOnConfirm(true);
               menu.setConfirmText("&aАктивировать множитель", "&aкоинов " + mult.getText("&e", "&a"), "", "&c Если в данный момент у вас", "&cактивирован другой множитель,", "&cто он будет удалён");
               menu.show(bukkitPlayer);
            }

         }
      }

      public Inventory getInventory() {
         return this.inv;
      }
   }

   private static class SettingsMenu implements IMenu {
      private final Inventory inv;
      private final VPlayerMenu parent;
      private final VPlayer player;
      private static final List TOGGLABLES;
      private static final ItemStack ENABLED_ITEM;
      private static final ItemStack DISABLED_ITEM;

      public SettingsMenu(VPlayer player, VPlayerMenu parent) {
         this.player = player;
         this.parent = parent;
         this.inv = Bukkit.createInventory(this, 54, "Настройки");
         this.inv.setItem(4, Items.name(Material.BED, "&f← &eНазад"));
         this.updateTogglables();
      }

      public void updateTogglables() {
         for(Togglable togglable : TOGGLABLES) {
            this.inv.setItem(togglable.slot, togglable.is.clone());
            this.inv.setItem(togglable.slot + 9, togglable.isEnabled(this.player) ? ENABLED_ITEM.clone() : DISABLED_ITEM.clone());
         }

      }

      public void onClick(ItemStack is, Player bukkitPlayer, int slot, ClickType clickType) {
         if (slot == 4) {
            this.parent.show(bukkitPlayer);
         } else {
            Togglable togglable = null;

            for(Togglable t : TOGGLABLES) {
               if (t.slot == slot || t.slot == slot - 9) {
                  togglable = t;
                  break;
               }
            }

            if (togglable != null) {
               togglable.toggle(this.player);
               this.updateTogglables();
            }

         }
      }

      public Inventory getInventory() {
         return this.inv;
      }

      static {
         TOGGLABLES = Arrays.asList(new FlagTogglable(10, Items.name(Material.BOOK_AND_QUILL, "&b&lЛичные сообщения", "&7 Если выключить, то через личные", "&7сообщения вы сможете общаться только", "&7со своими друзьями.", "&7Другие игроки не смогут написать вам."), 1), new FlagTogglable(12, Items.name(new ItemStack(Material.SKULL_ITEM, 1, (short)3), "&b&lПриглашения в группу", "&7 Если выключить, то вам не", "&7будут приходить приглашения,", "&7однако всё равно можно будет", "&7присоединиться к группе", "&7с помощью команды", "&f/party join <ник игрока>"), 0), new FlagTogglable(14, Items.name(Material.PAPER, "&b&lНапоминание о заданиях", "&7 Если выключить, то оповещение:", "&fАктивно заданий: &a&l_", "&7не будет показываться."), 2), new FlagTogglable(16, Items.name(Material.EMERALD, "&b&lОповещения о стримах", "&7 Если выключить, то вы не", "&7будете видеть оповещения об", "&7идущих стримах в чате или", "&7в верхнем правом углу экрана."), 3), new FlagTogglable(38, Items.name(new ItemStack(Material.SKULL_ITEM, 1, (short)2), "&b&lЗаявки в друзья", "&7 Если выключить, то у вас не", "&7будет оповещения о заявках,", "&7однако всё равно можно будет", "&7принять заявку с помощью команды", "&f/friend accept <ник игрока>"), 4), new FlagTogglable(40, Items.name(new ItemStack(Material.MONSTER_EGG, 1, (short)50), "&b&lОповещения от друзей", "&7 Если выключить, то вам не", "&7будут показываться оповещения", "&7о том что ваш друг &fзашел&7 или", "&fвышел&7 из игры"), 5), new FlagTogglable(42, Items.name(new ItemStack(Material.EMPTY_MAP), "&b&lПоказывать друзьям ваш точный сервер", "&6 Данная функция доступна только для &aVIP", "&6игроков или выше.", "", "&7 Если включить, то ваши друзья будут", "&7точно видеть сервер, на котором вы", "&7находитесь. Например:&f находится в LOBBY_1", "&7 Иначе ваши друзья будут видеть только", "&7название игры/лобби.", "&7 Например:&f находится в Лобби"), 6) {
            boolean toggle(VPlayer player) {
               return !player.rank.has(Rank.VIP) ? player.settings.get(this.flag) : super.toggle(player);
            }
         });
         ENABLED_ITEM = Items.name(new ItemStack(Material.INK_SACK, 1, (short)10), "&aВключено", "Нажмите для выключения");
         DISABLED_ITEM = Items.name(new ItemStack(Material.INK_SACK, 1, (short)8), "&cВыключено", "Нажмите для включения");
      }
   }

   abstract static class Togglable {
      ItemStack is;
      int slot;

      Togglable(int slot, ItemStack is) {
         this.slot = slot;
         this.is = is;
      }

      abstract boolean toggle(VPlayer var1);

      abstract boolean isEnabled(VPlayer var1);

      public int hashCode() {
         return this.slot;
      }

      public boolean equals(Object obj) {
         return obj == this;
      }
   }

   static class FlagTogglable extends Togglable {
      protected int flag;

      FlagTogglable(int slot, ItemStack is, int flag) {
         super(slot, is);
         this.flag = flag;
      }

      boolean isEnabled(VPlayer player) {
         return player.settings.get(this.flag);
      }

      boolean toggle(VPlayer player) {
         boolean f = !player.settings.get(this.flag);
         player.settings.set(this.flag, f);
         return f;
      }
   }
}
