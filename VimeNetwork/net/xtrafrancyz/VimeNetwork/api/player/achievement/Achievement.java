package net.xtrafrancyz.VimeNetwork.api.player.achievement;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.player.Stat;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Achievement {
   private static final TIntObjectHashMap byId = new TIntObjectHashMap();
   private static List values = null;
   public static final Achievement GLOBAL_COINS_20000;
   public static final Achievement GLOBAL_COINS_100000;
   public static final Achievement GLOBAL_FACELESS;
   public static final Achievement GLOBAL_FIRST_GOAL;
   public static final Achievement GLOBAL_GOALS_10;
   public static final Achievement GLOBAL_GOALS_50;
   public static final Achievement GLOBAL_GOALS_200;
   public static final Achievement GLOBAL_PARTY;
   public static final Achievement GLOBAL_PARTY_5;
   public static final Achievement GLOBAL_LOOT_CHEST;
   public static final Achievement GLOBAL_TRENDY;
   public static final Achievement GLOBAL_VIP;
   public static final Achievement GLOBAL_10_LVL;
   public static final Achievement GLOBAL_50_LVL;
   public static final Achievement LOBBY_PAINTER;
   public static final Achievement LOBBY_MELON_MASTER;
   public static final Achievement LOBBY_BALLOON;
   public static final Achievement LOBBY_TOWER;
   public static final Achievement LOBBY_FOREVER_ALONE;
   public static final Achievement LOBBY_FIRST_TREASURE;
   public static final Achievement LOBBY_FIRST_MINIDOT;
   public static final Achievement LOBBY_CRAFT_TREASURE;
   public static final Achievement LOBBY_CHEST_COINS;
   public static final Achievement SW_LOBBY_PARKOUR;
   public static final Achievement SW_KOVARSTVO;
   public static final Achievement SW_KOVARSTVO2;
   public static final Achievement SW_GAPPLE;
   public static final Achievement SW_DIAMOND_SET;
   public static final Achievement SW_KILL_100;
   public static final Achievement SW_IS_IT_REAL;
   public static final Achievement SW_FIRST_KIT;
   public static final Achievement SW_LAST_EFFORT;
   public static final Achievement SW_WIN_STREAK_5;
   public static final Achievement SW_HUNGER;
   public static final Achievement SW_DIAMOND_FROM_ORE;
   public static final Achievement SW_PACIFIST;
   public static final Achievement SW_DEATH_FROM_CREEPER;
   public static final Achievement SW_WIN_1;
   public static final Achievement SW_WIN_10;
   public static final Achievement SW_WIN_100;
   public static final Achievement SW_WIN_1000;
   public static final Achievement SW_WIN_10000;
   public static final Achievement GG_LOBBY_PARKOUR;
   public static final Achievement GG_IRON_FIST;
   public static final Achievement GG_IMPERTURBABLE;
   public static final Achievement GG_KILL_100;
   public static final Achievement GG_KILL_500;
   public static final Achievement GG_KILL_1500;
   public static final Achievement GG_RAGE;
   public static final Achievement GG_WIN_1;
   public static final Achievement GG_WIN_10;
   public static final Achievement GG_WIN_100;
   public static final Achievement GG_WIN_1000;
   public static final Achievement GG_WIN_10000;
   public static final Achievement KPVP_KILL_10;
   public static final Achievement KPVP_KILL_100;
   public static final Achievement KPVP_KILL_500;
   public static final Achievement KPVP_POINTS_100;
   public static final Achievement KPVP_POINTS_500;
   public static final Achievement KPVP_FIRST_KIT;
   public static final Achievement KPVP_IMPOSSIBRU;
   public static final Achievement KPVP_KILL_STREAKER;
   public static final Achievement DR_EAT_APPLE;
   public static final Achievement DR_MATER_MAUTI;
   public static final Achievement DR_MATER_BONBON;
   public static final Achievement DR_MATER_ORBIS;
   public static final Achievement DR_MATER_SKYLANDS;
   public static final Achievement DR_MASTER;
   public static final Achievement DR_FIRST_DEATH;
   public static final Achievement DR_GAMES_10;
   public static final Achievement DR_GAMES_50;
   public static final Achievement DR_GAMES_150;
   public static final Achievement DR_GAMES_1000;
   public static final Achievement ANN_LOBBY_PARKOUR;
   public static final Achievement ANN_KILL_DIAMOND;
   public static final Achievement ANN_FIRST_KIT;
   public static final Achievement ANN_RAT;
   public static final Achievement ANN_FIRST_DIAMOND;
   public static final Achievement ANN_ROUTINE;
   public static final Achievement ANN_OM_NOM_NOM;
   public static final Achievement BW_LOBBY_PARKOUR;
   public static final Achievement BW_BREAK_BED_1;
   public static final Achievement BW_BREAK_BED_10;
   public static final Achievement BW_BREAK_BED_100;
   public static final Achievement BW_BREAK_BED_500;
   public static final Achievement BW_THOR_FATHER;
   public static final Achievement BW_KING_OF_THE_HILL;
   public static final Achievement BW_RUSHER;
   public static final Achievement BW_SHOPPING;
   public static final Achievement BW_CHSVOY;
   public static final Achievement MW_LOBBY_PARKOUR;
   public static final Achievement MW_FULL_POWER;
   public static final Achievement MW_BUY_DIA_SWORD;
   public static final Achievement MW_INCOME_100;
   public static final Achievement MW_INCOME_5000;
   public static final Achievement MW_WAVE_60;
   public static final Achievement MW_WIN_1;
   public static final Achievement MW_WIN_10;
   public static final Achievement MW_WIN_100;
   public static final Achievement MW_WIN_1000;
   public static final Achievement MW_WIN_10000;
   public static final Achievement BP_LOBBY_PARKOUR;
   public static final Achievement BP_FIRST_DEATH;
   public static final Achievement BP_TIE;
   public static final Achievement BP_WAVES_15;
   public static final Achievement BP_WIN_1;
   public static final Achievement BP_WIN_10;
   public static final Achievement BP_WIN_100;
   public static final Achievement BP_WIN_1000;
   public static final Achievement BP_WIN_10000;
   public static final Achievement HG_LOBBY_PARKOUR;
   public static final Achievement HG_WIN_1;
   public static final Achievement HG_WIN_10;
   public static final Achievement HG_WIN_100;
   public static final Achievement HG_WIN_1000;
   public static final Achievement HG_WIN_10000;
   public static final Achievement HG_KILL_10_INGAME;
   public static final Achievement HG_PACIFIST;
   public static final Achievement HG_NO_FOOD;
   public static final Achievement HG_KILL_GOLDED_CARROT;
   public static final Achievement HG_DIAMOND_SWORD;
   public static final Achievement HG_FIRST_BLOOD;
   public static final Achievement HG_KILL_8_STREAK;
   public static final Achievement HG_KILL_2_BYBOOK;
   public static final Achievement HG_10_LVL_KIT;
   public static final Achievement SB_LOBBY_PARKOUR;
   public static final Achievement SB_WIN_1;
   public static final Achievement SB_WIN_10;
   public static final Achievement SB_WIN_100;
   public static final Achievement SB_WIN_1000;
   public static final Achievement SB_WIN_10000;
   public static final Achievement BB_LOBBY_PARKOUR;
   public static final Achievement BB_WIN_1;
   public static final Achievement BB_WIN_10;
   public static final Achievement BB_WIN_100;
   public static final Achievement BB_WIN_1000;
   public static final Achievement BB_WIN_10000;
   public static final Achievement BB_ARCHITECTOR;
   public static final Achievement CP_LOBBY_PARKOUR;
   public static final Achievement CP_RESOURCE_POINTS_BREAK_1;
   public static final Achievement CP_RESOURCE_POINTS_BREAK_10;
   public static final Achievement CP_RESOURCE_POINTS_BREAK_100;
   public static final Achievement CP_RESOURCE_POINTS_BREAK_500;
   public static final Achievement CP_KING_OF_THE_HILL;
   public static final Achievement CP_DESTROYER;
   public static final Achievement CP_SHOPPING;
   public static final Achievement CP_WIN_1;
   public static final Achievement CP_WIN_10;
   public static final Achievement CP_WIN_100;
   public static final Achievement CP_WIN_1000;
   public static final Achievement CP_WIN_10000;
   public static final Achievement SECRET_NY_2017;
   public static final Achievement SECRET_COOL_HACKER;
   public static final Achievement SECRET_SELF_KILL;
   public static final Achievement SECRET_DROWNING;
   public static final Achievement SECRET_SPACE;
   private final int id;
   private String name;
   private int reward;
   private String[] description;
   private Group group;
   private boolean hidden = false;

   protected Achievement(int id, String name, int reward, Group group, String... description) {
      this.id = id;
      this.name = name;
      this.reward = reward;
      this.group = group;
      this.description = description;
      this.group.achievements.add(this);
      if (byId.put(id, this) != null) {
         throw new IllegalArgumentException("Duplicate achievement id " + id);
      }
   }

   public String getName() {
      return this.name;
   }

   public String[] getDescription() {
      return this.description;
   }

   public Group getGroup() {
      return this.group;
   }

   public int getReward() {
      return this.reward;
   }

   public int getId() {
      return this.id;
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public int hashCode() {
      return this.id;
   }

   public boolean equals(Object obj) {
      return obj instanceof Achievement && ((Achievement)obj).id == this.id;
   }

   private Achievement hidden() {
      this.hidden = true;
      return this;
   }

   public static List getAchievements() {
      if (values == null) {
         values = new ArrayList(byId.size());
         TIntObjectIterator<Achievement> it = byId.iterator();

         while(it.hasNext()) {
            it.advance();
            values.add(it.value());
         }

         values = Collections.unmodifiableList(values);
      }

      return values;
   }

   public static Achievement byId(int id) {
      return (Achievement)byId.get(id);
   }

   static {
      GLOBAL_COINS_20000 = new Achievement(1, "Экономист", 3000, Achievement.Group.GLOBAL, new String[]{"Иметь в кармане 20.000 коинов"});
      GLOBAL_COINS_100000 = new Achievement(2, "Богатенький буратино", 10000, Achievement.Group.GLOBAL, new String[]{"Иметь в кармане 100.000 коинов"});
      GLOBAL_FACELESS = new Achievement(3, "Безликий", 2000, Achievement.Group.GLOBAL, new String[]{"Получить 5 масок"});
      GLOBAL_FIRST_GOAL = new Achievement(4, "Эмм..", 1000, Achievement.Group.GLOBAL, new String[]{"Взять первое задание"});
      GLOBAL_GOALS_10 = new StatAchievement(5, "Исполнитель I", 2000, Achievement.Group.GLOBAL, Stat.GOAL_COMPLETE, 10, new String[]{"Выполнить 10 заданий"});
      GLOBAL_GOALS_50 = new StatAchievement(6, "Исполнитель II", 5000, Achievement.Group.GLOBAL, Stat.GOAL_COMPLETE, 50, new String[]{"Выполнить 50 заданий"});
      GLOBAL_GOALS_200 = new StatAchievement(7, "Исполнитель III", 20000, Achievement.Group.GLOBAL, Stat.GOAL_COMPLETE, 200, new String[]{"Выполнить 200 заданий"});
      GLOBAL_PARTY = new Achievement(8, "Кучкование", 1000, Achievement.Group.GLOBAL, new String[]{"Вступить в группу"});
      GLOBAL_PARTY_5 = new Achievement(9, "У меня есть друзья!", 2000, Achievement.Group.GLOBAL, new String[]{"Быть в группе из 5 человек"});
      GLOBAL_LOOT_CHEST = new Achievement(10, "Счастливчик", 2000, Achievement.Group.GLOBAL, new String[]{"Выбить сундук в любой игре"});
      GLOBAL_TRENDY = new Achievement(11, "Модник", 2000, Achievement.Group.GLOBAL, new String[]{"Надеть все типы персонализации"});
      GLOBAL_VIP = new Achievement(12, "Друг админа", 25000, Achievement.Group.GLOBAL, new String[]{"Получить статус &aVIP"});
      GLOBAL_10_LVL = new Achievement(13, "Я знаю, что делаю", 10000, Achievement.Group.GLOBAL, new String[]{"Получить 10 уровень"});
      GLOBAL_50_LVL = new Achievement(14, "Меня не остановить", 25000, Achievement.Group.GLOBAL, new String[]{"Получить 50 уровень"});
      LOBBY_PAINTER = new StatAchievement(101, "Маляр 3000", 3000, Achievement.Group.LOBBY, Stat.LOBBY_PAINTBALL, 1000, new String[]{"Выстрелить 1000 раз из Пушки-красилки"});
      LOBBY_MELON_MASTER = new StatAchievement(102, "Арбузный мастер", 3000, Achievement.Group.LOBBY, Stat.LOBBY_MELON, 500, new String[]{"Взорвать 500 арбузов"});
      LOBBY_BALLOON = new StatAchievement(103, "Вверх", 3000, Achievement.Group.LOBBY, Stat.LOBBY_BALLOON, 100, new String[]{"Выпустить 100 шариков"});
      LOBBY_TOWER = new Achievement(104, "Башенка", 1500, Achievement.Group.LOBBY, new String[]{"Взять на плечи 5 игроков"});
      LOBBY_FOREVER_ALONE = new Achievement(105, "Forever Alone", 1500, Achievement.Group.LOBBY, new String[]{"Скрыть всех игроков"});
      LOBBY_FIRST_TREASURE = new Achievement(106, "Кладоискатель", 1000, Achievement.Group.LOBBY, new String[]{"Открыть свой первый сундук"});
      LOBBY_FIRST_MINIDOT = new Achievement(107, "Удачный день", 1500, Achievement.Group.LOBBY, new String[]{"Получить из сундука первый", "предмет персонализации"});
      LOBBY_CRAFT_TREASURE = new Achievement(108, "Туда-сюда", 1500, Achievement.Group.LOBBY, new String[]{"Обменять любой сундук"});
      LOBBY_CHEST_COINS = new StatAchievement(109, "&lヽ(・ω・)ﾉ", 15000, Achievement.Group.LOBBY, Stat.LOBBY_CHEST_COINS, 127000, new String[]{"Получить 127.000 коинов из сундуков"});
      SW_LOBBY_PARKOUR = new Achievement(201, "Паркурист", 1500, Achievement.Group.SKY_WARS, new String[]{"Пройти паркур в лобби SkyWars"});
      SW_KOVARSTVO = new StatAchievement(202, "Коварство I", 10000, Achievement.Group.SKY_WARS, Stat.SW_THROWN_PLAYERS, 100, new String[]{"Скинуть 100 игроков в бездну"});
      SW_KOVARSTVO2 = new StatAchievement(203, "Коварство II", 25000, Achievement.Group.SKY_WARS, Stat.SW_THROWN_PLAYERS, 500, new String[]{"Скинуть 500 игроков в бездну"});
      SW_GAPPLE = new Achievement(204, "Геппл", 1500, Achievement.Group.SKY_WARS, new String[]{"Съесть золотое яблоко"});
      SW_DIAMOND_SET = new Achievement(205, "Алмазник", 1500, Achievement.Group.SKY_WARS, new String[]{"Надеть алмазный сет"});
      SW_KILL_100 = new Achievement(206, "Убивашка", 6000, Achievement.Group.SKY_WARS, new String[]{"Убить 100 человек"});
      SW_IS_IT_REAL = new Achievement(207, "Это вообще реально?", 7000, Achievement.Group.SKY_WARS, new String[]{"Победить не открыв ни одного сундука"});
      SW_FIRST_KIT = new Achievement(208, "Транжира", 3000, Achievement.Group.SKY_WARS, new String[]{"Купить свой первый набор"});
      SW_LAST_EFFORT = new Achievement(209, "Из последних сил", 2500, Achievement.Group.SKY_WARS, new String[]{"Сделать убийство, когда у вас", "осталось менее 1-го сердечка"});
      SW_WIN_STREAK_5 = new Achievement(215, "Бычара", 2500, Achievement.Group.SKY_WARS, new String[]{"Победить 5 раз подряд"});
      SW_HUNGER = new Achievement(216, "Один дома", 3000, Achievement.Group.SKY_WARS, new String[]{"Умереть с голоду"});
      SW_DIAMOND_FROM_ORE = new Achievement(217, "Блестяшка", 3000, Achievement.Group.SKY_WARS, new String[]{"Добыть жемчуг эндера из руды"});
      SW_PACIFIST = new Achievement(218, "Пацифист", 7000, Achievement.Group.SKY_WARS, new String[]{"Победить никого не убив"});
      SW_DEATH_FROM_CREEPER = new Achievement(219, "Невезучий шахтер", 3000, Achievement.Group.SKY_WARS, new String[]{"Умереть от взрыва крипера"});
      SW_WIN_1 = new WinAchievement(210, "Победитель I", 1000, Achievement.Group.SKY_WARS, new String[]{"Победить 1 раз"});
      SW_WIN_10 = new WinAchievement(211, "Победитель II", 3000, Achievement.Group.SKY_WARS, new String[]{"Победить 10 раз"});
      SW_WIN_100 = new WinAchievement(212, "Победитель III", 10000, Achievement.Group.SKY_WARS, new String[]{"Победить 100 раз"});
      SW_WIN_1000 = new WinAchievement(213, "Победитель IV", 50000, Achievement.Group.SKY_WARS, new String[]{"Победить 1.000 раз"});
      SW_WIN_10000 = new WinAchievement(214, "Чемпион", 200000, Achievement.Group.SKY_WARS, new String[]{"Победить 10.000 раз"});
      GG_LOBBY_PARKOUR = new Achievement(301, "Паркурист", 1500, Achievement.Group.GUN_GAME, new String[]{"Пройти паркур в лобби"});
      GG_IRON_FIST = new Achievement(302, "Железный кулак", 3000, Achievement.Group.GUN_GAME, new String[]{"Убить врага голыми руками"});
      GG_IMPERTURBABLE = new Achievement(303, "Невозмутимый", 4000, Achievement.Group.GUN_GAME, new String[]{"Убить игрока 12-го уровня первым"});
      GG_KILL_100 = new Achievement(304, "Убивашка I", 2000, Achievement.Group.GUN_GAME, new String[]{"Убить 100 человек"});
      GG_KILL_500 = new Achievement(305, "Убивашка II", 5000, Achievement.Group.GUN_GAME, new String[]{"Убить 500 человек"});
      GG_KILL_1500 = new Achievement(306, "Убивашка III", 10000, Achievement.Group.GUN_GAME, new String[]{"Убить 1500 человек"});
      GG_RAGE = new Achievement(307, "Бешенство", 5000, Achievement.Group.GUN_GAME, new String[]{"Убить 10 человек за одну жизнь"});
      GG_WIN_1 = new WinAchievement(308, "Победитель I", 1000, Achievement.Group.GUN_GAME, new String[]{"Победить 1 раз"});
      GG_WIN_10 = new WinAchievement(309, "Победитель II", 3000, Achievement.Group.GUN_GAME, new String[]{"Победить 10 раз"});
      GG_WIN_100 = new WinAchievement(310, "Победитель III", 10000, Achievement.Group.GUN_GAME, new String[]{"Победить 100 раз"});
      GG_WIN_1000 = new WinAchievement(311, "Победитель IV", 50000, Achievement.Group.GUN_GAME, new String[]{"Победить 1.000 раз"});
      GG_WIN_10000 = new WinAchievement(312, "Чемпион", 200000, Achievement.Group.GUN_GAME, new String[]{"Победить 10.000 раз"});
      KPVP_KILL_10 = new Achievement(401, "Убивашка I", 1000, Achievement.Group.KIT_PVP, new String[]{"Убить 10 человек"});
      KPVP_KILL_100 = new Achievement(402, "Убивашка II", 4000, Achievement.Group.KIT_PVP, new String[]{"Убить 100 человек"});
      KPVP_KILL_500 = new Achievement(403, "Убивашка III", 10000, Achievement.Group.KIT_PVP, new String[]{"Убить 500 человек"});
      KPVP_POINTS_100 = new Achievement(404, "Начало.", 2000, Achievement.Group.KIT_PVP, new String[]{"Набрать 100 очков"});
      KPVP_POINTS_500 = new Achievement(405, "500 не предел", 6000, Achievement.Group.KIT_PVP, new String[]{"Набрать 500 очков"});
      KPVP_FIRST_KIT = new Achievement(406, "Транжира", 3000, Achievement.Group.KIT_PVP, new String[]{"Купить свой первый набор"});
      KPVP_IMPOSSIBRU = new Achievement(407, "IMPOSSIBRU!", 5000, Achievement.Group.KIT_PVP, new String[]{"Убить 20 человек за одну жизнь"});
      KPVP_KILL_STREAKER = new Achievement(408, "Повержен", 5000, Achievement.Group.KIT_PVP, new String[]{"Сбить серию убийств"});
      DR_EAT_APPLE = new Achievement(501, "Любитель яблок", 1500, Achievement.Group.DEATH_RUN, new String[]{"Съесть яблоко"});
      DR_MATER_MAUTI = new StatAchievement(502, "Мастер Mauti", 5555, Achievement.Group.DEATH_RUN, Stat.DR_MAUTI_WIN, 50, new String[]{"Победить 50 раз на карте Mauti"});
      DR_MATER_BONBON = new StatAchievement(503, "Мастер BonBon", 5555, Achievement.Group.DEATH_RUN, Stat.DR_BONBON_WIN, 50, new String[]{"Победить 50 раз на карте BonBon"});
      DR_MATER_ORBIS = new StatAchievement(504, "Мастер Orbis", 5555, Achievement.Group.DEATH_RUN, Stat.DR_ORBIS_WIN, 50, new String[]{"Победить 50 раз на карте Orbis"});
      DR_MATER_SKYLANDS = new StatAchievement(505, "Мастер SkyLands", 5555, Achievement.Group.DEATH_RUN, Stat.DR_SKYLANDS_WIN, 50, new String[]{"Победить 50 раз на карте SkyLands"});
      DR_MASTER = new Achievement(506, "Мастер DeathRun", 6666, Achievement.Group.DEATH_RUN, new String[]{"Выполнить все достижения \"Мастер\""});
      DR_FIRST_DEATH = new Achievement(507, "Легкий путь", 1000, Achievement.Group.DEATH_RUN, new String[]{"Умереть самым первым"});
      DR_GAMES_10 = new Achievement(508, "Бегун I", 1500, Achievement.Group.DEATH_RUN, new String[]{"Сыграть 10 игр"});
      DR_GAMES_50 = new Achievement(509, "Бегун II", 4000, Achievement.Group.DEATH_RUN, new String[]{"Сыграть 50 игр"});
      DR_GAMES_150 = new Achievement(5010, "Бегун III", 10000, Achievement.Group.DEATH_RUN, new String[]{"Сыграть 150 игр"});
      DR_GAMES_1000 = new Achievement(5011, "Неуловимый", 25000, Achievement.Group.DEATH_RUN, new String[]{"Сыграть 1000 игр"});
      ANN_LOBBY_PARKOUR = new Achievement(607, "Паркурист", 1500, Achievement.Group.ANNIHILATION, new String[]{"Пройти паркур в лобби Annihilation"});
      ANN_KILL_DIAMOND = new Achievement(601, "Алмазофоб", 2000, Achievement.Group.ANNIHILATION, new String[]{"Убить алмазника"});
      ANN_FIRST_KIT = new Achievement(602, "Транжира", 3000, Achievement.Group.ANNIHILATION, new String[]{"Купить свою первую роль"});
      ANN_RAT = new Achievement(603, "Крыса", 5000, Achievement.Group.ANNIHILATION, new String[]{"Нанести базе последний удар"});
      ANN_FIRST_DIAMOND = new Achievement(604, "Блестяшка", 2000, Achievement.Group.ANNIHILATION, new String[]{"Добыть свой первый алмаз"});
      ANN_ROUTINE = new Achievement(605, "Рутина", 3456, Achievement.Group.ANNIHILATION, new String[]{"Добыть 50000 руды"});
      ANN_OM_NOM_NOM = new Achievement(606, "Ом-ном-ном", 1000, Achievement.Group.ANNIHILATION, new String[]{"Сломать арбуз"});
      BW_LOBBY_PARKOUR = new Achievement(701, "Паркурист", 1500, Achievement.Group.BED_WARS, new String[]{"Пройти паркур в лобби BedWars"});
      BW_BREAK_BED_1 = new Achievement(703, "Крушитель I", 1500, Achievement.Group.BED_WARS, new String[]{"Сломать одну кровать"});
      BW_BREAK_BED_10 = new Achievement(704, "Крушитель II", 4000, Achievement.Group.BED_WARS, new String[]{"Сломать 10 кроватей"});
      BW_BREAK_BED_100 = new Achievement(705, "Крушитель III", 8000, Achievement.Group.BED_WARS, new String[]{"Сломать 100 кроватей"});
      BW_BREAK_BED_500 = new Achievement(706, "Крушитель IV", 25000, Achievement.Group.BED_WARS, new String[]{"Сломать 500 кроватей"});
      BW_THOR_FATHER = new Achievement(707, "Тор, я твой отец", 4000, Achievement.Group.BED_WARS, new String[]{"Использовать кость тора 50 раз"});
      BW_KING_OF_THE_HILL = new Achievement(708, "Царь горы", 5000, Achievement.Group.BED_WARS, new String[]{"Скинуть 20 игроков за игру"});
      BW_RUSHER = new Achievement(709, "Рашер", 5000, Achievement.Group.BED_WARS, new String[]{"Сломать 3 кровати за 4 минуты"});
      BW_SHOPPING = new Achievement(710, "Шоппинг", 4000, Achievement.Group.BED_WARS, new String[]{"Потрать 64 золота за игру"});
      BW_CHSVOY = new Achievement(711, "ЧСВой", 4000, Achievement.Group.BED_WARS, new String[]{"Использовать часики 30 раз за игру"});
      MW_LOBBY_PARKOUR = new Achievement(801, "Паркурист", 1500, Achievement.Group.MOB_WARS, new String[]{"Пройти паркур в лобби MobWars"});
      MW_FULL_POWER = new Achievement(802, "Под завязку", 2000, Achievement.Group.MOB_WARS, new String[]{"Потратить всю силу"});
      MW_BUY_DIA_SWORD = new Achievement(803, "Моя прелесть", 3500, Achievement.Group.MOB_WARS, new String[]{"Купить алмазный меч"});
      MW_INCOME_100 = new Achievement(804, "Бизнесмен I", 1000, Achievement.Group.MOB_WARS, new String[]{"Иметь 100 дохода"});
      MW_INCOME_5000 = new Achievement(806, "Бизнесмен II", 4000, Achievement.Group.MOB_WARS, new String[]{"Иметь 5000 дохода"});
      MW_WAVE_60 = new Achievement(807, "Day & Night", 10000, Achievement.Group.MOB_WARS, new String[]{"Играть до 60-ой волны"});
      MW_WIN_1 = new WinAchievement(808, "Победитель I", 500, Achievement.Group.MOB_WARS, new String[]{"Победить 1 раз"});
      MW_WIN_10 = new WinAchievement(809, "Победитель II", 2000, Achievement.Group.MOB_WARS, new String[]{"Победить 10 раз"});
      MW_WIN_100 = new WinAchievement(810, "Победитель III", 8000, Achievement.Group.MOB_WARS, new String[]{"Победить 100 раз"});
      MW_WIN_1000 = new WinAchievement(811, "Победитель IV", 30000, Achievement.Group.MOB_WARS, new String[]{"Победить 1.000 раз"});
      MW_WIN_10000 = new WinAchievement(812, "Чемпион", 100000, Achievement.Group.MOB_WARS, new String[]{"Победить 10.000 раз"});
      BP_LOBBY_PARKOUR = new Achievement(901, "Паркурист", 1500, Achievement.Group.BLOCK_PARTY, new String[]{"Пройти паркур в лобби BlockParty"});
      BP_FIRST_DEATH = new Achievement(902, "Лёгкий путь", 1000, Achievement.Group.BLOCK_PARTY, new String[]{"Умереть самым первым"});
      BP_TIE = new Achievement(903, "Равноправие", 2000, Achievement.Group.BLOCK_PARTY, new String[]{"Сыграть в ничью"});
      BP_WAVES_15 = new Achievement(904, "FlyHack", 3000, Achievement.Group.BLOCK_PARTY, new String[]{"Продержаться 15 волн"});
      BP_WIN_1 = new WinAchievement(905, "Победитель I", 500, Achievement.Group.BLOCK_PARTY, new String[]{"Победить 1 раз"});
      BP_WIN_10 = new WinAchievement(906, "Победитель II", 2000, Achievement.Group.BLOCK_PARTY, new String[]{"Победить 10 раз"});
      BP_WIN_100 = new WinAchievement(907, "Победитель III", 8000, Achievement.Group.BLOCK_PARTY, new String[]{"Победить 100 раз"});
      BP_WIN_1000 = new WinAchievement(908, "Победитель IV", 30000, Achievement.Group.BLOCK_PARTY, new String[]{"Победить 1.000 раз"});
      BP_WIN_10000 = new WinAchievement(909, "Чемпион", 100000, Achievement.Group.BLOCK_PARTY, new String[]{"Победить 10.000 раз"});
      HG_LOBBY_PARKOUR = new Achievement(1014, "Паркурист", 1500, Achievement.Group.HUNGER_GAMES, new String[]{"Пройти паркур в лобби HungerGames"});
      HG_WIN_1 = new WinAchievement(1001, "Победитель I", 1000, Achievement.Group.HUNGER_GAMES, new String[]{"Победить 1 раз"});
      HG_WIN_10 = new WinAchievement(1002, "Победитель II", 3000, Achievement.Group.HUNGER_GAMES, new String[]{"Победить 10 раз"});
      HG_WIN_100 = new WinAchievement(1003, "Победитель III", 10000, Achievement.Group.HUNGER_GAMES, new String[]{"Победить 100 раз"});
      HG_WIN_1000 = new WinAchievement(1004, "Победитель IV", 50000, Achievement.Group.HUNGER_GAMES, new String[]{"Победить 1.000 раз"});
      HG_WIN_10000 = new WinAchievement(1005, "Чемпион", 200000, Achievement.Group.HUNGER_GAMES, new String[]{"Победить 10.000 раз"});
      HG_KILL_10_INGAME = new Achievement(1006, "Тащер", 4000, Achievement.Group.HUNGER_GAMES, new String[]{"Убить 10 человек за одну игру"});
      HG_PACIFIST = new Achievement(1007, "Пацифист", 5000, Achievement.Group.HUNGER_GAMES, new String[]{"Победить никого не убив"});
      HG_NO_FOOD = new Achievement(1008, "Бретарианец", 6000, Achievement.Group.HUNGER_GAMES, new String[]{"Победить ничего не съев"});
      HG_KILL_GOLDED_CARROT = new Achievement(1009, "Отшлёпан", 4000, Achievement.Group.HUNGER_GAMES, new String[]{"Убить золотой морковкой"});
      HG_DIAMOND_SWORD = new Achievement(1010, "Моя прелесть", 1000, Achievement.Group.HUNGER_GAMES, new String[]{"Получить алмазный меч"});
      HG_FIRST_BLOOD = new Achievement(1011, "ФБ!!", 1500, Achievement.Group.HUNGER_GAMES, new String[]{"Самым первым сделать убийство"});
      HG_KILL_8_STREAK = new Achievement(1012, "Так его!", 10000, Achievement.Group.HUNGER_GAMES, new String[]{"Убить человека с 8 убийствами", "в одной игре"});
      HG_KILL_2_BYBOOK = new Achievement(1013, "Учитель математики", 2000, Achievement.Group.HUNGER_GAMES, new String[]{"Убить учебником математики", "двух человек в одной игре"});
      HG_10_LVL_KIT = new Achievement(1015, "Я есть богат", 20000, Achievement.Group.HUNGER_GAMES, new String[]{"Прокачать любой набор до 10 уровня"});
      SB_LOBBY_PARKOUR = new Achievement(1106, "Паркурист", 1500, Achievement.Group.SPEED_BUILDERS, new String[]{"Пройти паркур в лобби SpeedBuilders"});
      SB_WIN_1 = new WinAchievement(1101, "Победитель I", 1000, Achievement.Group.SPEED_BUILDERS, new String[]{"Победить 1 раз"});
      SB_WIN_10 = new WinAchievement(1102, "Победитель II", 3000, Achievement.Group.SPEED_BUILDERS, new String[]{"Победить 10 раз"});
      SB_WIN_100 = new WinAchievement(1103, "Победитель III", 10000, Achievement.Group.SPEED_BUILDERS, new String[]{"Победить 100 раз"});
      SB_WIN_1000 = new WinAchievement(1104, "Победитель IV", 50000, Achievement.Group.SPEED_BUILDERS, new String[]{"Победить 1.000 раз"});
      SB_WIN_10000 = new WinAchievement(1105, "Чемпион", 200000, Achievement.Group.SPEED_BUILDERS, new String[]{"Победить 10.000 раз"});
      BB_LOBBY_PARKOUR = new Achievement(1206, "Паркурист", 1500, Achievement.Group.BUILD_BATTLE, new String[]{"Пройти паркур в лобби BuildBattle"});
      BB_WIN_1 = new WinAchievement(1201, "Победитель I", 1000, Achievement.Group.BUILD_BATTLE, new String[]{"Победить 1 раз"});
      BB_WIN_10 = new WinAchievement(1202, "Победитель II", 3000, Achievement.Group.BUILD_BATTLE, new String[]{"Победить 10 раз"});
      BB_WIN_100 = new WinAchievement(1203, "Победитель III", 10000, Achievement.Group.BUILD_BATTLE, new String[]{"Победить 100 раз"});
      BB_WIN_1000 = new WinAchievement(1204, "Победитель IV", 50000, Achievement.Group.BUILD_BATTLE, new String[]{"Победить 1.000 раз"});
      BB_WIN_10000 = new WinAchievement(1205, "Чемпион", 200000, Achievement.Group.BUILD_BATTLE, new String[]{"Победить 10.000 раз"});
      BB_ARCHITECTOR = new Achievement(1207, "Архитектор", 5000, Achievement.Group.BUILD_BATTLE, new String[]{"Выиграть с 75-ю очками"});
      CP_LOBBY_PARKOUR = new Achievement(1301, "Паркурист", 1500, Achievement.Group.CLASH_POINT, new String[]{"Пройти паркур в лобби ClashPoint"});
      CP_RESOURCE_POINTS_BREAK_1 = new Achievement(1302, "Крушитель I", 1500, Achievement.Group.CLASH_POINT, new String[]{"Сломать одну точку ресурсов"});
      CP_RESOURCE_POINTS_BREAK_10 = new Achievement(1303, "Крушитель II", 3000, Achievement.Group.CLASH_POINT, new String[]{"Сломать 10 точек ресурсов"});
      CP_RESOURCE_POINTS_BREAK_100 = new Achievement(1304, "Крушитель III", 7000, Achievement.Group.CLASH_POINT, new String[]{"Сломать 100 точек ресурсов"});
      CP_RESOURCE_POINTS_BREAK_500 = new Achievement(1305, "Крушитель IV", 20000, Achievement.Group.CLASH_POINT, new String[]{"Сломать 500 точек ресурсов"});
      CP_KING_OF_THE_HILL = new Achievement(1306, "Царь горы", 5000, Achievement.Group.CLASH_POINT, new String[]{"Скинуть 20 игроков за игру"});
      CP_DESTROYER = new Achievement(1307, "Разрушитель", 5000, Achievement.Group.CLASH_POINT, new String[]{"Сломать 3 точки ресурсов за игру"});
      CP_SHOPPING = new Achievement(1308, "Шоппинг", 4000, Achievement.Group.CLASH_POINT, new String[]{"Потрать 64 золота за игру"});
      CP_WIN_1 = new WinAchievement(1309, "Победитель I", 1000, Achievement.Group.CLASH_POINT, new String[]{"Победить 1 раз"});
      CP_WIN_10 = new WinAchievement(1310, "Победитель II", 3000, Achievement.Group.CLASH_POINT, new String[]{"Победить 10 раз"});
      CP_WIN_100 = new WinAchievement(1311, "Победитель III", 10000, Achievement.Group.CLASH_POINT, new String[]{"Победить 100 раз"});
      CP_WIN_1000 = new WinAchievement(1312, "Победитель IV", 50000, Achievement.Group.CLASH_POINT, new String[]{"Победить 1.000 раз"});
      CP_WIN_10000 = new WinAchievement(1313, "Чемпион", 200000, Achievement.Group.CLASH_POINT, new String[]{"Победить 10.000 раз"});
      SECRET_NY_2017 = new Achievement(9001, "Новый год 2017", 50000, Achievement.Group.SECRET, new String[]{"Зайти на сервер в", "новогоднюю ночь 2016 - 2017"});
      SECRET_COOL_HACKER = new Achievement(9002, "Кулхацкер", 3000, Achievement.Group.SECRET, new String[]{"Найти админскую команду"});
      SECRET_SELF_KILL = new Achievement(9003, "Зря. Кря.", 5000, Achievement.Group.SECRET, new String[]{"Застрелить себя из лука"});
      SECRET_DROWNING = new Achievement(9004, "Почти водолаз", 5000, Achievement.Group.SECRET, new String[]{"Утонуть в лобби"});
      SECRET_SPACE = new Achievement(9005, "Мама, я в космосе", 5000, Achievement.Group.SECRET, new String[]{"Подняться на высоту 2000 блоков"});
   }

   public static enum Group {
      GLOBAL("Глобальные", 3, Material.CAKE),
      SECRET("Секретные", 5, new ItemStack(Material.MONSTER_EGG, 1, (short)58)),
      LOBBY("Лобби", 4, Material.COMPASS),
      SKY_WARS("SkyWars", 31, Material.EYE_OF_ENDER),
      BED_WARS("BedWars", 32, Material.BED),
      GUN_GAME("GunGame", 20, Material.GOLD_SWORD),
      MOB_WARS("MobWars", 22, new ItemStack(Material.MONSTER_EGG, 1, (short)50)),
      DEATH_RUN("DeathRun", 24, Material.GRASS),
      KIT_PVP("KitPvP", 42, Material.SKULL),
      BLOCK_PARTY("BlockParty", 38, Material.GOLD_RECORD),
      ANNIHILATION("Annihilation", 30, Material.ENDER_STONE),
      HUNGER_GAMES("HungerGames", 40, Material.COOKED_BEEF),
      SPEED_BUILDERS("SpeedBuilders", -1, Material.MELON),
      BUILD_BATTLE("BuildBattle", 49, Material.WORKBENCH),
      CLASH_POINT("ClashPoint", 13, Material.MAGMA_CREAM);

      private final String name;
      private final int slot;
      private final ItemStack is;
      final List achievements;

      private Group(String name, int slot, Material mat) {
         this(name, slot, new ItemStack(mat));
      }

      private Group(String name, int slot, ItemStack is) {
         this.achievements = new LinkedList();
         this.name = name;
         this.slot = slot;
         this.is = is;
      }

      public int getSlot() {
         return this.slot;
      }

      public ItemStack getItemStack() {
         return this.is;
      }

      public List getAchievements() {
         return this.achievements;
      }

      public String getName() {
         return this.name;
      }
   }
}
