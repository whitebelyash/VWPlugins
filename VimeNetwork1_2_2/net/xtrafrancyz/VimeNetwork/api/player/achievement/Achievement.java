/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  gnu.trove.iterator.TIntObjectIterator
 *  gnu.trove.map.hash.TIntObjectHashMap
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.api.player.achievement;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.player.Stat;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.StatAchievement;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.WinAchievement;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Achievement {
    private static final TIntObjectHashMap<Achievement> byId = new TIntObjectHashMap();
    private static List<Achievement> values = null;
    public static final Achievement GLOBAL_COINS_20000 = new Achievement(1, "\u042d\u043a\u043e\u043d\u043e\u043c\u0438\u0441\u0442", 3000, Group.GLOBAL, "\u0418\u043c\u0435\u0442\u044c \u0432 \u043a\u0430\u0440\u043c\u0430\u043d\u0435 20.000 \u043a\u043e\u0438\u043d\u043e\u0432");
    public static final Achievement GLOBAL_COINS_100000 = new Achievement(2, "\u0411\u043e\u0433\u0430\u0442\u0435\u043d\u044c\u043a\u0438\u0439 \u0431\u0443\u0440\u0430\u0442\u0438\u043d\u043e", 10000, Group.GLOBAL, "\u0418\u043c\u0435\u0442\u044c \u0432 \u043a\u0430\u0440\u043c\u0430\u043d\u0435 100.000 \u043a\u043e\u0438\u043d\u043e\u0432");
    public static final Achievement GLOBAL_FACELESS = new Achievement(3, "\u0411\u0435\u0437\u043b\u0438\u043a\u0438\u0439", 2000, Group.GLOBAL, "\u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c 5 \u043c\u0430\u0441\u043e\u043a");
    public static final Achievement GLOBAL_FIRST_GOAL = new Achievement(4, "\u042d\u043c\u043c..", 1000, Group.GLOBAL, "\u0412\u0437\u044f\u0442\u044c \u043f\u0435\u0440\u0432\u043e\u0435 \u0437\u0430\u0434\u0430\u043d\u0438\u0435");
    public static final Achievement GLOBAL_GOALS_10 = new StatAchievement(5, "\u0418\u0441\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c I", 2000, Group.GLOBAL, Stat.GOAL_COMPLETE, 10, "\u0412\u044b\u043f\u043e\u043b\u043d\u0438\u0442\u044c 10 \u0437\u0430\u0434\u0430\u043d\u0438\u0439");
    public static final Achievement GLOBAL_GOALS_50 = new StatAchievement(6, "\u0418\u0441\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c II", 5000, Group.GLOBAL, Stat.GOAL_COMPLETE, 50, "\u0412\u044b\u043f\u043e\u043b\u043d\u0438\u0442\u044c 50 \u0437\u0430\u0434\u0430\u043d\u0438\u0439");
    public static final Achievement GLOBAL_GOALS_200 = new StatAchievement(7, "\u0418\u0441\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c III", 20000, Group.GLOBAL, Stat.GOAL_COMPLETE, 200, "\u0412\u044b\u043f\u043e\u043b\u043d\u0438\u0442\u044c 200 \u0437\u0430\u0434\u0430\u043d\u0438\u0439");
    public static final Achievement GLOBAL_PARTY = new Achievement(8, "\u041a\u0443\u0447\u043a\u043e\u0432\u0430\u043d\u0438\u0435", 1000, Group.GLOBAL, "\u0412\u0441\u0442\u0443\u043f\u0438\u0442\u044c \u0432 \u0433\u0440\u0443\u043f\u043f\u0443");
    public static final Achievement GLOBAL_PARTY_5 = new Achievement(9, "\u0423 \u043c\u0435\u043d\u044f \u0435\u0441\u0442\u044c \u0434\u0440\u0443\u0437\u044c\u044f!", 2000, Group.GLOBAL, "\u0411\u044b\u0442\u044c \u0432 \u0433\u0440\u0443\u043f\u043f\u0435 \u0438\u0437 5 \u0447\u0435\u043b\u043e\u0432\u0435\u043a");
    public static final Achievement GLOBAL_LOOT_CHEST = new Achievement(10, "\u0421\u0447\u0430\u0441\u0442\u043b\u0438\u0432\u0447\u0438\u043a", 2000, Group.GLOBAL, "\u0412\u044b\u0431\u0438\u0442\u044c \u0441\u0443\u043d\u0434\u0443\u043a \u0432 \u043b\u044e\u0431\u043e\u0439 \u0438\u0433\u0440\u0435");
    public static final Achievement GLOBAL_TRENDY = new Achievement(11, "\u041c\u043e\u0434\u043d\u0438\u043a", 2000, Group.GLOBAL, "\u041d\u0430\u0434\u0435\u0442\u044c \u0432\u0441\u0435 \u0442\u0438\u043f\u044b \u043f\u0435\u0440\u0441\u043e\u043d\u0430\u043b\u0438\u0437\u0430\u0446\u0438\u0438");
    public static final Achievement GLOBAL_VIP = new Achievement(12, "\u0414\u0440\u0443\u0433 \u0430\u0434\u043c\u0438\u043d\u0430", 25000, Group.GLOBAL, "\u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u0441\u0442\u0430\u0442\u0443\u0441 &aVIP");
    public static final Achievement GLOBAL_10_LVL = new Achievement(13, "\u042f \u0437\u043d\u0430\u044e, \u0447\u0442\u043e \u0434\u0435\u043b\u0430\u044e", 10000, Group.GLOBAL, "\u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c 10 \u0443\u0440\u043e\u0432\u0435\u043d\u044c");
    public static final Achievement GLOBAL_50_LVL = new Achievement(14, "\u041c\u0435\u043d\u044f \u043d\u0435 \u043e\u0441\u0442\u0430\u043d\u043e\u0432\u0438\u0442\u044c", 25000, Group.GLOBAL, "\u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c 50 \u0443\u0440\u043e\u0432\u0435\u043d\u044c");
    public static final Achievement LOBBY_PAINTER = new StatAchievement(101, "\u041c\u0430\u043b\u044f\u0440 3000", 3000, Group.LOBBY, Stat.LOBBY_PAINTBALL, 1000, "\u0412\u044b\u0441\u0442\u0440\u0435\u043b\u0438\u0442\u044c 1000 \u0440\u0430\u0437 \u0438\u0437 \u041f\u0443\u0448\u043a\u0438-\u043a\u0440\u0430\u0441\u0438\u043b\u043a\u0438");
    public static final Achievement LOBBY_MELON_MASTER = new StatAchievement(102, "\u0410\u0440\u0431\u0443\u0437\u043d\u044b\u0439 \u043c\u0430\u0441\u0442\u0435\u0440", 3000, Group.LOBBY, Stat.LOBBY_MELON, 500, "\u0412\u0437\u043e\u0440\u0432\u0430\u0442\u044c 500 \u0430\u0440\u0431\u0443\u0437\u043e\u0432");
    public static final Achievement LOBBY_BALLOON = new StatAchievement(103, "\u0412\u0432\u0435\u0440\u0445", 3000, Group.LOBBY, Stat.LOBBY_BALLOON, 100, "\u0412\u044b\u043f\u0443\u0441\u0442\u0438\u0442\u044c 100 \u0448\u0430\u0440\u0438\u043a\u043e\u0432");
    public static final Achievement LOBBY_TOWER = new Achievement(104, "\u0411\u0430\u0448\u0435\u043d\u043a\u0430", 1500, Group.LOBBY, "\u0412\u0437\u044f\u0442\u044c \u043d\u0430 \u043f\u043b\u0435\u0447\u0438 5 \u0438\u0433\u0440\u043e\u043a\u043e\u0432");
    public static final Achievement LOBBY_FOREVER_ALONE = new Achievement(105, "Forever Alone", 1500, Group.LOBBY, "\u0421\u043a\u0440\u044b\u0442\u044c \u0432\u0441\u0435\u0445 \u0438\u0433\u0440\u043e\u043a\u043e\u0432");
    public static final Achievement LOBBY_FIRST_TREASURE = new Achievement(106, "\u041a\u043b\u0430\u0434\u043e\u0438\u0441\u043a\u0430\u0442\u0435\u043b\u044c", 1000, Group.LOBBY, "\u041e\u0442\u043a\u0440\u044b\u0442\u044c \u0441\u0432\u043e\u0439 \u043f\u0435\u0440\u0432\u044b\u0439 \u0441\u0443\u043d\u0434\u0443\u043a");
    public static final Achievement LOBBY_FIRST_MINIDOT = new Achievement(107, "\u0423\u0434\u0430\u0447\u043d\u044b\u0439 \u0434\u0435\u043d\u044c", 1500, Group.LOBBY, "\u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u0438\u0437 \u0441\u0443\u043d\u0434\u0443\u043a\u0430 \u043f\u0435\u0440\u0432\u044b\u0439", "\u043f\u0440\u0435\u0434\u043c\u0435\u0442 \u043f\u0435\u0440\u0441\u043e\u043d\u0430\u043b\u0438\u0437\u0430\u0446\u0438\u0438");
    public static final Achievement LOBBY_CRAFT_TREASURE = new Achievement(108, "\u0422\u0443\u0434\u0430-\u0441\u044e\u0434\u0430", 1500, Group.LOBBY, "\u041e\u0431\u043c\u0435\u043d\u044f\u0442\u044c \u043b\u044e\u0431\u043e\u0439 \u0441\u0443\u043d\u0434\u0443\u043a");
    public static final Achievement LOBBY_CHEST_COINS = new StatAchievement(109, "&l\u30fd(\u30fb\u03c9\u30fb)\uff89", 15000, Group.LOBBY, Stat.LOBBY_CHEST_COINS, 127000, "\u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c 127.000 \u043a\u043e\u0438\u043d\u043e\u0432 \u0438\u0437 \u0441\u0443\u043d\u0434\u0443\u043a\u043e\u0432");
    public static final Achievement SW_LOBBY_PARKOUR = new Achievement(201, "\u041f\u0430\u0440\u043a\u0443\u0440\u0438\u0441\u0442", 1500, Group.SKY_WARS, "\u041f\u0440\u043e\u0439\u0442\u0438 \u043f\u0430\u0440\u043a\u0443\u0440 \u0432 \u043b\u043e\u0431\u0431\u0438 SkyWars");
    public static final Achievement SW_KOVARSTVO = new StatAchievement(202, "\u041a\u043e\u0432\u0430\u0440\u0441\u0442\u0432\u043e I", 10000, Group.SKY_WARS, Stat.SW_THROWN_PLAYERS, 100, "\u0421\u043a\u0438\u043d\u0443\u0442\u044c 100 \u0438\u0433\u0440\u043e\u043a\u043e\u0432 \u0432 \u0431\u0435\u0437\u0434\u043d\u0443");
    public static final Achievement SW_KOVARSTVO2 = new StatAchievement(203, "\u041a\u043e\u0432\u0430\u0440\u0441\u0442\u0432\u043e II", 25000, Group.SKY_WARS, Stat.SW_THROWN_PLAYERS, 500, "\u0421\u043a\u0438\u043d\u0443\u0442\u044c 500 \u0438\u0433\u0440\u043e\u043a\u043e\u0432 \u0432 \u0431\u0435\u0437\u0434\u043d\u0443");
    public static final Achievement SW_GAPPLE = new Achievement(204, "\u0413\u0435\u043f\u043f\u043b", 1500, Group.SKY_WARS, "\u0421\u044a\u0435\u0441\u0442\u044c \u0437\u043e\u043b\u043e\u0442\u043e\u0435 \u044f\u0431\u043b\u043e\u043a\u043e");
    public static final Achievement SW_DIAMOND_SET = new Achievement(205, "\u0410\u043b\u043c\u0430\u0437\u043d\u0438\u043a", 1500, Group.SKY_WARS, "\u041d\u0430\u0434\u0435\u0442\u044c \u0430\u043b\u043c\u0430\u0437\u043d\u044b\u0439 \u0441\u0435\u0442");
    public static final Achievement SW_KILL_100 = new Achievement(206, "\u0423\u0431\u0438\u0432\u0430\u0448\u043a\u0430", 6000, Group.SKY_WARS, "\u0423\u0431\u0438\u0442\u044c 100 \u0447\u0435\u043b\u043e\u0432\u0435\u043a");
    public static final Achievement SW_IS_IT_REAL = new Achievement(207, "\u042d\u0442\u043e \u0432\u043e\u043e\u0431\u0449\u0435 \u0440\u0435\u0430\u043b\u044c\u043d\u043e?", 7000, Group.SKY_WARS, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c \u043d\u0435 \u043e\u0442\u043a\u0440\u044b\u0432 \u043d\u0438 \u043e\u0434\u043d\u043e\u0433\u043e \u0441\u0443\u043d\u0434\u0443\u043a\u0430");
    public static final Achievement SW_FIRST_KIT = new Achievement(208, "\u0422\u0440\u0430\u043d\u0436\u0438\u0440\u0430", 3000, Group.SKY_WARS, "\u041a\u0443\u043f\u0438\u0442\u044c \u0441\u0432\u043e\u0439 \u043f\u0435\u0440\u0432\u044b\u0439 \u043d\u0430\u0431\u043e\u0440");
    public static final Achievement SW_LAST_EFFORT = new Achievement(209, "\u0418\u0437 \u043f\u043e\u0441\u043b\u0435\u0434\u043d\u0438\u0445 \u0441\u0438\u043b", 2500, Group.SKY_WARS, "\u0421\u0434\u0435\u043b\u0430\u0442\u044c \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u043e, \u043a\u043e\u0433\u0434\u0430 \u0443 \u0432\u0430\u0441", "\u043e\u0441\u0442\u0430\u043b\u043e\u0441\u044c \u043c\u0435\u043d\u0435\u0435 1-\u0433\u043e \u0441\u0435\u0440\u0434\u0435\u0447\u043a\u0430");
    public static final Achievement SW_WIN_STREAK_5 = new Achievement(215, "\u0411\u044b\u0447\u0430\u0440\u0430", 2500, Group.SKY_WARS, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 5 \u0440\u0430\u0437 \u043f\u043e\u0434\u0440\u044f\u0434");
    public static final Achievement SW_HUNGER = new Achievement(216, "\u041e\u0434\u0438\u043d \u0434\u043e\u043c\u0430", 3000, Group.SKY_WARS, "\u0423\u043c\u0435\u0440\u0435\u0442\u044c \u0441 \u0433\u043e\u043b\u043e\u0434\u0443");
    public static final Achievement SW_DIAMOND_FROM_ORE = new Achievement(217, "\u0411\u043b\u0435\u0441\u0442\u044f\u0448\u043a\u0430", 3000, Group.SKY_WARS, "\u0414\u043e\u0431\u044b\u0442\u044c \u0436\u0435\u043c\u0447\u0443\u0433 \u044d\u043d\u0434\u0435\u0440\u0430 \u0438\u0437 \u0440\u0443\u0434\u044b");
    public static final Achievement SW_PACIFIST = new Achievement(218, "\u041f\u0430\u0446\u0438\u0444\u0438\u0441\u0442", 7000, Group.SKY_WARS, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c \u043d\u0438\u043a\u043e\u0433\u043e \u043d\u0435 \u0443\u0431\u0438\u0432");
    public static final Achievement SW_WIN_1 = new WinAchievement(210, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c I", 1000, Group.SKY_WARS, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 1 \u0440\u0430\u0437");
    public static final Achievement SW_WIN_10 = new WinAchievement(211, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c II", 3000, Group.SKY_WARS, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 10 \u0440\u0430\u0437");
    public static final Achievement SW_WIN_100 = new WinAchievement(212, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c III", 10000, Group.SKY_WARS, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 100 \u0440\u0430\u0437");
    public static final Achievement SW_WIN_1000 = new WinAchievement(213, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c IV", 50000, Group.SKY_WARS, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 1.000 \u0440\u0430\u0437");
    public static final Achievement SW_WIN_10000 = new WinAchievement(214, "\u0427\u0435\u043c\u043f\u0438\u043e\u043d", 200000, Group.SKY_WARS, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 10.000 \u0440\u0430\u0437");
    public static final Achievement GG_LOBBY_PARKOUR = new Achievement(301, "\u041f\u0430\u0440\u043a\u0443\u0440\u0438\u0441\u0442", 1500, Group.GUN_GAME, "\u041f\u0440\u043e\u0439\u0442\u0438 \u043f\u0430\u0440\u043a\u0443\u0440 \u0432 \u043b\u043e\u0431\u0431\u0438");
    public static final Achievement GG_IRON_FIST = new Achievement(302, "\u0416\u0435\u043b\u0435\u0437\u043d\u044b\u0439 \u043a\u0443\u043b\u0430\u043a", 3000, Group.GUN_GAME, "\u0423\u0431\u0438\u0442\u044c \u0432\u0440\u0430\u0433\u0430 \u0433\u043e\u043b\u044b\u043c\u0438 \u0440\u0443\u043a\u0430\u043c\u0438");
    public static final Achievement GG_IMPERTURBABLE = new Achievement(303, "\u041d\u0435\u0432\u043e\u0437\u043c\u0443\u0442\u0438\u043c\u044b\u0439", 4000, Group.GUN_GAME, "\u0423\u0431\u0438\u0442\u044c \u0438\u0433\u0440\u043e\u043a\u0430 12-\u0433\u043e \u0443\u0440\u043e\u0432\u043d\u044f \u043f\u0435\u0440\u0432\u044b\u043c");
    public static final Achievement GG_KILL_100 = new Achievement(304, "\u0423\u0431\u0438\u0432\u0430\u0448\u043a\u0430 I", 2000, Group.GUN_GAME, "\u0423\u0431\u0438\u0442\u044c 100 \u0447\u0435\u043b\u043e\u0432\u0435\u043a");
    public static final Achievement GG_KILL_500 = new Achievement(305, "\u0423\u0431\u0438\u0432\u0430\u0448\u043a\u0430 II", 5000, Group.GUN_GAME, "\u0423\u0431\u0438\u0442\u044c 500 \u0447\u0435\u043b\u043e\u0432\u0435\u043a");
    public static final Achievement GG_KILL_1500 = new Achievement(306, "\u0423\u0431\u0438\u0432\u0430\u0448\u043a\u0430 III", 10000, Group.GUN_GAME, "\u0423\u0431\u0438\u0442\u044c 1500 \u0447\u0435\u043b\u043e\u0432\u0435\u043a");
    public static final Achievement GG_RAGE = new Achievement(307, "\u0411\u0435\u0448\u0435\u043d\u0441\u0442\u0432\u043e", 5000, Group.GUN_GAME, "\u0423\u0431\u0438\u0442\u044c 10 \u0447\u0435\u043b\u043e\u0432\u0435\u043a \u0437\u0430 \u043e\u0434\u043d\u0443 \u0436\u0438\u0437\u043d\u044c");
    public static final Achievement GG_WIN_1 = new WinAchievement(308, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c I", 1000, Group.GUN_GAME, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 1 \u0440\u0430\u0437");
    public static final Achievement GG_WIN_10 = new WinAchievement(309, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c II", 3000, Group.GUN_GAME, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 10 \u0440\u0430\u0437");
    public static final Achievement GG_WIN_100 = new WinAchievement(310, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c III", 10000, Group.GUN_GAME, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 100 \u0440\u0430\u0437");
    public static final Achievement GG_WIN_1000 = new WinAchievement(311, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c IV", 50000, Group.GUN_GAME, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 1.000 \u0440\u0430\u0437");
    public static final Achievement GG_WIN_10000 = new WinAchievement(312, "\u0427\u0435\u043c\u043f\u0438\u043e\u043d", 200000, Group.GUN_GAME, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 10.000 \u0440\u0430\u0437");
    public static final Achievement KPVP_KILL_10 = new Achievement(401, "\u0423\u0431\u0438\u0432\u0430\u0448\u043a\u0430 I", 1000, Group.KIT_PVP, "\u0423\u0431\u0438\u0442\u044c 10 \u0447\u0435\u043b\u043e\u0432\u0435\u043a");
    public static final Achievement KPVP_KILL_100 = new Achievement(402, "\u0423\u0431\u0438\u0432\u0430\u0448\u043a\u0430 II", 4000, Group.KIT_PVP, "\u0423\u0431\u0438\u0442\u044c 100 \u0447\u0435\u043b\u043e\u0432\u0435\u043a");
    public static final Achievement KPVP_KILL_500 = new Achievement(403, "\u0423\u0431\u0438\u0432\u0430\u0448\u043a\u0430 III", 10000, Group.KIT_PVP, "\u0423\u0431\u0438\u0442\u044c 500 \u0447\u0435\u043b\u043e\u0432\u0435\u043a");
    public static final Achievement KPVP_POINTS_100 = new Achievement(404, "\u041d\u0430\u0447\u0430\u043b\u043e.", 2000, Group.KIT_PVP, "\u041d\u0430\u0431\u0440\u0430\u0442\u044c 100 \u043e\u0447\u043a\u043e\u0432");
    public static final Achievement KPVP_POINTS_500 = new Achievement(405, "500 \u043d\u0435 \u043f\u0440\u0435\u0434\u0435\u043b", 6000, Group.KIT_PVP, "\u041d\u0430\u0431\u0440\u0430\u0442\u044c 500 \u043e\u0447\u043a\u043e\u0432");
    public static final Achievement KPVP_FIRST_KIT = new Achievement(406, "\u0422\u0440\u0430\u043d\u0436\u0438\u0440\u0430", 3000, Group.KIT_PVP, "\u041a\u0443\u043f\u0438\u0442\u044c \u0441\u0432\u043e\u0439 \u043f\u0435\u0440\u0432\u044b\u0439 \u043d\u0430\u0431\u043e\u0440");
    public static final Achievement KPVP_IMPOSSIBRU = new Achievement(407, "IMPOSSIBRU!", 5000, Group.KIT_PVP, "\u0423\u0431\u0438\u0442\u044c 20 \u0447\u0435\u043b\u043e\u0432\u0435\u043a \u0437\u0430 \u043e\u0434\u043d\u0443 \u0436\u0438\u0437\u043d\u044c");
    public static final Achievement KPVP_KILL_STREAKER = new Achievement(408, "\u041f\u043e\u0432\u0435\u0440\u0436\u0435\u043d", 5000, Group.KIT_PVP, "\u0421\u0431\u0438\u0442\u044c \u0441\u0435\u0440\u0438\u044e \u0443\u0431\u0438\u0439\u0441\u0442\u0432");
    public static final Achievement DR_EAT_APPLE = new Achievement(501, "\u041b\u044e\u0431\u0438\u0442\u0435\u043b\u044c \u044f\u0431\u043b\u043e\u043a", 1500, Group.DEATH_RUN, "\u0421\u044a\u0435\u0441\u0442\u044c \u044f\u0431\u043b\u043e\u043a\u043e");
    public static final Achievement DR_MATER_MAUTI = new StatAchievement(502, "\u041c\u0430\u0441\u0442\u0435\u0440 Mauti", 5555, Group.DEATH_RUN, Stat.DR_MAUTI_WIN, 50, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 50 \u0440\u0430\u0437 \u043d\u0430 \u043a\u0430\u0440\u0442\u0435 Mauti");
    public static final Achievement DR_MATER_BONBON = new StatAchievement(503, "\u041c\u0430\u0441\u0442\u0435\u0440 BonBon", 5555, Group.DEATH_RUN, Stat.DR_BONBON_WIN, 50, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 50 \u0440\u0430\u0437 \u043d\u0430 \u043a\u0430\u0440\u0442\u0435 BonBon");
    public static final Achievement DR_MATER_ORBIS = new StatAchievement(504, "\u041c\u0430\u0441\u0442\u0435\u0440 Orbis", 5555, Group.DEATH_RUN, Stat.DR_ORBIS_WIN, 50, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 50 \u0440\u0430\u0437 \u043d\u0430 \u043a\u0430\u0440\u0442\u0435 Orbis");
    public static final Achievement DR_MATER_SKYLANDS = new StatAchievement(505, "\u041c\u0430\u0441\u0442\u0435\u0440 SkyLands", 5555, Group.DEATH_RUN, Stat.DR_SKYLANDS_WIN, 50, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 50 \u0440\u0430\u0437 \u043d\u0430 \u043a\u0430\u0440\u0442\u0435 SkyLands");
    public static final Achievement DR_MASTER = new Achievement(506, "\u041c\u0430\u0441\u0442\u0435\u0440 DeathRun", 6666, Group.DEATH_RUN, "\u0412\u044b\u043f\u043e\u043b\u043d\u0438\u0442\u044c \u0432\u0441\u0435 \u0434\u043e\u0441\u0442\u0438\u0436\u0435\u043d\u0438\u044f \"\u041c\u0430\u0441\u0442\u0435\u0440\"");
    public static final Achievement DR_FIRST_DEATH = new Achievement(507, "\u041b\u0435\u0433\u043a\u0438\u0439 \u043f\u0443\u0442\u044c", 1000, Group.DEATH_RUN, "\u0423\u043c\u0435\u0440\u0435\u0442\u044c \u0441\u0430\u043c\u044b\u043c \u043f\u0435\u0440\u0432\u044b\u043c");
    public static final Achievement DR_GAMES_10 = new Achievement(508, "\u0411\u0435\u0433\u0443\u043d I", 1500, Group.DEATH_RUN, "\u0421\u044b\u0433\u0440\u0430\u0442\u044c 10 \u0438\u0433\u0440");
    public static final Achievement DR_GAMES_50 = new Achievement(509, "\u0411\u0435\u0433\u0443\u043d II", 4000, Group.DEATH_RUN, "\u0421\u044b\u0433\u0440\u0430\u0442\u044c 50 \u0438\u0433\u0440");
    public static final Achievement DR_GAMES_150 = new Achievement(5010, "\u0411\u0435\u0433\u0443\u043d III", 10000, Group.DEATH_RUN, "\u0421\u044b\u0433\u0440\u0430\u0442\u044c 150 \u0438\u0433\u0440");
    public static final Achievement DR_GAMES_1000 = new Achievement(5011, "\u041d\u0435\u0443\u043b\u043e\u0432\u0438\u043c\u044b\u0439", 25000, Group.DEATH_RUN, "\u0421\u044b\u0433\u0440\u0430\u0442\u044c 1000 \u0438\u0433\u0440");
    public static final Achievement ANN_LOBBY_PARKOUR = new Achievement(607, "\u041f\u0430\u0440\u043a\u0443\u0440\u0438\u0441\u0442", 1500, Group.ANNIHILATION, "\u041f\u0440\u043e\u0439\u0442\u0438 \u043f\u0430\u0440\u043a\u0443\u0440 \u0432 \u043b\u043e\u0431\u0431\u0438 Annihilation");
    public static final Achievement ANN_KILL_DIAMOND = new Achievement(601, "\u0410\u043b\u043c\u0430\u0437\u043e\u0444\u043e\u0431", 2000, Group.ANNIHILATION, "\u0423\u0431\u0438\u0442\u044c \u0430\u043b\u043c\u0430\u0437\u043d\u0438\u043a\u0430");
    public static final Achievement ANN_FIRST_KIT = new Achievement(602, "\u0422\u0440\u0430\u043d\u0436\u0438\u0440\u0430", 3000, Group.ANNIHILATION, "\u041a\u0443\u043f\u0438\u0442\u044c \u0441\u0432\u043e\u044e \u043f\u0435\u0440\u0432\u0443\u044e \u0440\u043e\u043b\u044c");
    public static final Achievement ANN_RAT = new Achievement(603, "\u041a\u0440\u044b\u0441\u0430", 5000, Group.ANNIHILATION, "\u041d\u0430\u043d\u0435\u0441\u0442\u0438 \u0431\u0430\u0437\u0435 \u043f\u043e\u0441\u043b\u0435\u0434\u043d\u0438\u0439 \u0443\u0434\u0430\u0440");
    public static final Achievement ANN_FIRST_DIAMOND = new Achievement(604, "\u0411\u043b\u0435\u0441\u0442\u044f\u0448\u043a\u0430", 2000, Group.ANNIHILATION, "\u0414\u043e\u0431\u044b\u0442\u044c \u0441\u0432\u043e\u0439 \u043f\u0435\u0440\u0432\u044b\u0439 \u0430\u043b\u043c\u0430\u0437");
    public static final Achievement ANN_ROUTINE = new Achievement(605, "\u0420\u0443\u0442\u0438\u043d\u0430", 3456, Group.ANNIHILATION, "\u0414\u043e\u0431\u044b\u0442\u044c 50000 \u0440\u0443\u0434\u044b");
    public static final Achievement ANN_OM_NOM_NOM = new Achievement(606, "\u041e\u043c-\u043d\u043e\u043c-\u043d\u043e\u043c", 1000, Group.ANNIHILATION, "\u0421\u043b\u043e\u043c\u0430\u0442\u044c \u0430\u0440\u0431\u0443\u0437");
    public static final Achievement BW_LOBBY_PARKOUR = new Achievement(701, "\u041f\u0430\u0440\u043a\u0443\u0440\u0438\u0441\u0442", 1500, Group.BED_WARS, "\u041f\u0440\u043e\u0439\u0442\u0438 \u043f\u0430\u0440\u043a\u0443\u0440 \u0432 \u043b\u043e\u0431\u0431\u0438 BedWars");
    public static final Achievement BW_BREAK_BED_1 = new Achievement(703, "\u041a\u0440\u0443\u0448\u0438\u0442\u0435\u043b\u044c I", 1500, Group.BED_WARS, "\u0421\u043b\u043e\u043c\u0430\u0442\u044c \u043e\u0434\u043d\u0443 \u043a\u0440\u043e\u0432\u0430\u0442\u044c");
    public static final Achievement BW_BREAK_BED_10 = new Achievement(704, "\u041a\u0440\u0443\u0448\u0438\u0442\u0435\u043b\u044c II", 4000, Group.BED_WARS, "\u0421\u043b\u043e\u043c\u0430\u0442\u044c 10 \u043a\u0440\u043e\u0432\u0430\u0442\u0435\u0439");
    public static final Achievement BW_BREAK_BED_100 = new Achievement(705, "\u041a\u0440\u0443\u0448\u0438\u0442\u0435\u043b\u044c III", 8000, Group.BED_WARS, "\u0421\u043b\u043e\u043c\u0430\u0442\u044c 100 \u043a\u0440\u043e\u0432\u0430\u0442\u0435\u0439");
    public static final Achievement BW_BREAK_BED_500 = new Achievement(706, "\u041a\u0440\u0443\u0448\u0438\u0442\u0435\u043b\u044c IV", 25000, Group.BED_WARS, "\u0421\u043b\u043e\u043c\u0430\u0442\u044c 500 \u043a\u0440\u043e\u0432\u0430\u0442\u0435\u0439");
    public static final Achievement BW_THOR_FATHER = new Achievement(707, "\u0422\u043e\u0440, \u044f \u0442\u0432\u043e\u0439 \u043e\u0442\u0435\u0446", 4000, Group.BED_WARS, "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u043a\u043e\u0441\u0442\u044c \u0442\u043e\u0440\u0430 50 \u0440\u0430\u0437");
    public static final Achievement BW_KING_OF_THE_HILL = new Achievement(708, "\u0426\u0430\u0440\u044c \u0433\u043e\u0440\u044b", 5000, Group.BED_WARS, "\u0421\u043a\u0438\u043d\u0443\u0442\u044c 20 \u0438\u0433\u0440\u043e\u043a\u043e\u0432 \u0437\u0430 \u0438\u0433\u0440\u0443");
    public static final Achievement BW_RUSHER = new Achievement(709, "\u0420\u0430\u0448\u0435\u0440", 5000, Group.BED_WARS, "\u0421\u043b\u043e\u043c\u0430\u0442\u044c 3 \u043a\u0440\u043e\u0432\u0430\u0442\u0438 \u0437\u0430 4 \u043c\u0438\u043d\u0443\u0442\u044b");
    public static final Achievement BW_SHOPPING = new Achievement(710, "\u0428\u043e\u043f\u043f\u0438\u043d\u0433", 4000, Group.BED_WARS, "\u041f\u043e\u0442\u0440\u0430\u0442\u044c 64 \u0437\u043e\u043b\u043e\u0442\u0430 \u0437\u0430 \u0438\u0433\u0440\u0443");
    public static final Achievement BW_CHSVOY = new Achievement(711, "\u0427\u0421\u0412\u043e\u0439", 4000, Group.BED_WARS, "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u0447\u0430\u0441\u0438\u043a\u0438 30 \u0440\u0430\u0437 \u0437\u0430 \u0438\u0433\u0440\u0443");
    public static final Achievement MW_LOBBY_PARKOUR = new Achievement(801, "\u041f\u0430\u0440\u043a\u0443\u0440\u0438\u0441\u0442", 1500, Group.MOB_WARS, "\u041f\u0440\u043e\u0439\u0442\u0438 \u043f\u0430\u0440\u043a\u0443\u0440 \u0432 \u043b\u043e\u0431\u0431\u0438 MobWars");
    public static final Achievement MW_FULL_POWER = new Achievement(802, "\u041f\u043e\u0434 \u0437\u0430\u0432\u044f\u0437\u043a\u0443", 2000, Group.MOB_WARS, "\u041f\u043e\u0442\u0440\u0430\u0442\u0438\u0442\u044c \u0432\u0441\u044e \u0441\u0438\u043b\u0443");
    public static final Achievement MW_BUY_DIA_SWORD = new Achievement(803, "\u041c\u043e\u044f \u043f\u0440\u0435\u043b\u0435\u0441\u0442\u044c", 3500, Group.MOB_WARS, "\u041a\u0443\u043f\u0438\u0442\u044c \u0430\u043b\u043c\u0430\u0437\u043d\u044b\u0439 \u043c\u0435\u0447");
    public static final Achievement MW_INCOME_100 = new Achievement(804, "\u0411\u0438\u0437\u043d\u0435\u0441\u043c\u0435\u043d I", 1000, Group.MOB_WARS, "\u0418\u043c\u0435\u0442\u044c 100 \u0434\u043e\u0445\u043e\u0434\u0430");
    public static final Achievement MW_INCOME_5000 = new Achievement(806, "\u0411\u0438\u0437\u043d\u0435\u0441\u043c\u0435\u043d II", 4000, Group.MOB_WARS, "\u0418\u043c\u0435\u0442\u044c 5000 \u0434\u043e\u0445\u043e\u0434\u0430");
    public static final Achievement MW_WAVE_60 = new Achievement(807, "Day & Night", 10000, Group.MOB_WARS, "\u0418\u0433\u0440\u0430\u0442\u044c \u0434\u043e 60-\u043e\u0439 \u0432\u043e\u043b\u043d\u044b");
    public static final Achievement MW_WIN_1 = new WinAchievement(808, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c I", 500, Group.MOB_WARS, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 1 \u0440\u0430\u0437");
    public static final Achievement MW_WIN_10 = new WinAchievement(809, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c II", 2000, Group.MOB_WARS, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 10 \u0440\u0430\u0437");
    public static final Achievement MW_WIN_100 = new WinAchievement(810, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c III", 8000, Group.MOB_WARS, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 100 \u0440\u0430\u0437");
    public static final Achievement MW_WIN_1000 = new WinAchievement(811, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c IV", 30000, Group.MOB_WARS, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 1.000 \u0440\u0430\u0437");
    public static final Achievement MW_WIN_10000 = new WinAchievement(812, "\u0427\u0435\u043c\u043f\u0438\u043e\u043d", 100000, Group.MOB_WARS, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 10.000 \u0440\u0430\u0437");
    public static final Achievement BP_LOBBY_PARKOUR = new Achievement(901, "\u041f\u0430\u0440\u043a\u0443\u0440\u0438\u0441\u0442", 1500, Group.BLOCK_PARTY, "\u041f\u0440\u043e\u0439\u0442\u0438 \u043f\u0430\u0440\u043a\u0443\u0440 \u0432 \u043b\u043e\u0431\u0431\u0438 BlockParty");
    public static final Achievement BP_FIRST_DEATH = new Achievement(902, "\u041b\u0451\u0433\u043a\u0438\u0439 \u043f\u0443\u0442\u044c", 1000, Group.BLOCK_PARTY, "\u0423\u043c\u0435\u0440\u0435\u0442\u044c \u0441\u0430\u043c\u044b\u043c \u043f\u0435\u0440\u0432\u044b\u043c");
    public static final Achievement BP_TIE = new Achievement(903, "\u0420\u0430\u0432\u043d\u043e\u043f\u0440\u0430\u0432\u0438\u0435", 2000, Group.BLOCK_PARTY, "\u0421\u044b\u0433\u0440\u0430\u0442\u044c \u0432 \u043d\u0438\u0447\u044c\u044e");
    public static final Achievement BP_WAVES_15 = new Achievement(904, "FlyHack", 3000, Group.BLOCK_PARTY, "\u041f\u0440\u043e\u0434\u0435\u0440\u0436\u0430\u0442\u044c\u0441\u044f 15 \u0432\u043e\u043b\u043d");
    public static final Achievement BP_WIN_1 = new WinAchievement(905, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c I", 500, Group.BLOCK_PARTY, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 1 \u0440\u0430\u0437");
    public static final Achievement BP_WIN_10 = new WinAchievement(906, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c II", 2000, Group.BLOCK_PARTY, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 10 \u0440\u0430\u0437");
    public static final Achievement BP_WIN_100 = new WinAchievement(907, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c III", 8000, Group.BLOCK_PARTY, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 100 \u0440\u0430\u0437");
    public static final Achievement BP_WIN_1000 = new WinAchievement(908, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c IV", 30000, Group.BLOCK_PARTY, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 1.000 \u0440\u0430\u0437");
    public static final Achievement BP_WIN_10000 = new WinAchievement(909, "\u0427\u0435\u043c\u043f\u0438\u043e\u043d", 100000, Group.BLOCK_PARTY, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 10.000 \u0440\u0430\u0437");
    public static final Achievement HG_LOBBY_PARKOUR = new Achievement(1014, "\u041f\u0430\u0440\u043a\u0443\u0440\u0438\u0441\u0442", 1500, Group.HUNGER_GAMES, "\u041f\u0440\u043e\u0439\u0442\u0438 \u043f\u0430\u0440\u043a\u0443\u0440 \u0432 \u043b\u043e\u0431\u0431\u0438 HungerGames");
    public static final Achievement HG_WIN_1 = new WinAchievement(1001, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c I", 1000, Group.HUNGER_GAMES, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 1 \u0440\u0430\u0437");
    public static final Achievement HG_WIN_10 = new WinAchievement(1002, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c II", 3000, Group.HUNGER_GAMES, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 10 \u0440\u0430\u0437");
    public static final Achievement HG_WIN_100 = new WinAchievement(1003, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c III", 10000, Group.HUNGER_GAMES, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 100 \u0440\u0430\u0437");
    public static final Achievement HG_WIN_1000 = new WinAchievement(1004, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c IV", 50000, Group.HUNGER_GAMES, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 1.000 \u0440\u0430\u0437");
    public static final Achievement HG_WIN_10000 = new WinAchievement(1005, "\u0427\u0435\u043c\u043f\u0438\u043e\u043d", 200000, Group.HUNGER_GAMES, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 10.000 \u0440\u0430\u0437");
    public static final Achievement HG_KILL_10_INGAME = new Achievement(1006, "\u0422\u0430\u0449\u0435\u0440", 4000, Group.HUNGER_GAMES, "\u0423\u0431\u0438\u0442\u044c 10 \u0447\u0435\u043b\u043e\u0432\u0435\u043a \u0437\u0430 \u043e\u0434\u043d\u0443 \u0438\u0433\u0440\u0443");
    public static final Achievement HG_PACIFIST = new Achievement(1007, "\u041f\u0430\u0446\u0438\u0444\u0438\u0441\u0442", 5000, Group.HUNGER_GAMES, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c \u043d\u0438\u043a\u043e\u0433\u043e \u043d\u0435 \u0443\u0431\u0438\u0432");
    public static final Achievement HG_NO_FOOD = new Achievement(1008, "\u0411\u0440\u0435\u0442\u0430\u0440\u0438\u0430\u043d\u0435\u0446", 6000, Group.HUNGER_GAMES, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c \u043d\u0438\u0447\u0435\u0433\u043e \u043d\u0435 \u0441\u044a\u0435\u0432");
    public static final Achievement HG_KILL_GOLDED_CARROT = new Achievement(1009, "\u041e\u0442\u0448\u043b\u0451\u043f\u0430\u043d", 4000, Group.HUNGER_GAMES, "\u0423\u0431\u0438\u0442\u044c \u0437\u043e\u043b\u043e\u0442\u043e\u0439 \u043c\u043e\u0440\u043a\u043e\u0432\u043a\u043e\u0439");
    public static final Achievement HG_DIAMOND_SWORD = new Achievement(1010, "\u041c\u043e\u044f \u043f\u0440\u0435\u043b\u0435\u0441\u0442\u044c", 1000, Group.HUNGER_GAMES, "\u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u0430\u043b\u043c\u0430\u0437\u043d\u044b\u0439 \u043c\u0435\u0447");
    public static final Achievement HG_FIRST_BLOOD = new Achievement(1011, "\u0424\u0411!!", 1500, Group.HUNGER_GAMES, "\u0421\u0430\u043c\u044b\u043c \u043f\u0435\u0440\u0432\u044b\u043c \u0441\u0434\u0435\u043b\u0430\u0442\u044c \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u043e");
    public static final Achievement HG_KILL_8_STREAK = new Achievement(1012, "\u0422\u0430\u043a \u0435\u0433\u043e!", 10000, Group.HUNGER_GAMES, "\u0423\u0431\u0438\u0442\u044c \u0447\u0435\u043b\u043e\u0432\u0435\u043a\u0430 \u0441 8 \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u0430\u043c\u0438", "\u0432 \u043e\u0434\u043d\u043e\u0439 \u0438\u0433\u0440\u0435");
    public static final Achievement HG_KILL_2_BYBOOK = new Achievement(1013, "\u0423\u0447\u0438\u0442\u0435\u043b\u044c \u043c\u0430\u0442\u0435\u043c\u0430\u0442\u0438\u043a\u0438", 2000, Group.HUNGER_GAMES, "\u0423\u0431\u0438\u0442\u044c \u0443\u0447\u0435\u0431\u043d\u0438\u043a\u043e\u043c \u043c\u0430\u0442\u0435\u043c\u0430\u0442\u0438\u043a\u0438", "\u0434\u0432\u0443\u0445 \u0447\u0435\u043b\u043e\u0432\u0435\u043a \u0432 \u043e\u0434\u043d\u043e\u0439 \u0438\u0433\u0440\u0435");
    public static final Achievement HG_10_LVL_KIT = new Achievement(1015, "\u042f \u0435\u0441\u0442\u044c \u0431\u043e\u0433\u0430\u0442", 20000, Group.HUNGER_GAMES, "\u041f\u0440\u043e\u043a\u0430\u0447\u0430\u0442\u044c \u043b\u044e\u0431\u043e\u0439 \u043d\u0430\u0431\u043e\u0440 \u0434\u043e 10 \u0443\u0440\u043e\u0432\u043d\u044f");
    public static final Achievement SB_LOBBY_PARKOUR = new Achievement(1106, "\u041f\u0430\u0440\u043a\u0443\u0440\u0438\u0441\u0442", 1500, Group.SPEED_BUILDERS, "\u041f\u0440\u043e\u0439\u0442\u0438 \u043f\u0430\u0440\u043a\u0443\u0440 \u0432 \u043b\u043e\u0431\u0431\u0438 SpeedBuilders");
    public static final Achievement SB_WIN_1 = new WinAchievement(1101, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c I", 1000, Group.SPEED_BUILDERS, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 1 \u0440\u0430\u0437");
    public static final Achievement SB_WIN_10 = new WinAchievement(1102, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c II", 3000, Group.SPEED_BUILDERS, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 10 \u0440\u0430\u0437");
    public static final Achievement SB_WIN_100 = new WinAchievement(1103, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c III", 10000, Group.SPEED_BUILDERS, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 100 \u0440\u0430\u0437");
    public static final Achievement SB_WIN_1000 = new WinAchievement(1104, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c IV", 50000, Group.SPEED_BUILDERS, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 1.000 \u0440\u0430\u0437");
    public static final Achievement SB_WIN_10000 = new WinAchievement(1105, "\u0427\u0435\u043c\u043f\u0438\u043e\u043d", 200000, Group.SPEED_BUILDERS, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 10.000 \u0440\u0430\u0437");
    public static final Achievement BB_LOBBY_PARKOUR = new Achievement(1206, "\u041f\u0430\u0440\u043a\u0443\u0440\u0438\u0441\u0442", 1500, Group.BUILD_BATTLE, "\u041f\u0440\u043e\u0439\u0442\u0438 \u043f\u0430\u0440\u043a\u0443\u0440 \u0432 \u043b\u043e\u0431\u0431\u0438 BuildBattle");
    public static final Achievement BB_WIN_1 = new WinAchievement(1201, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c I", 1000, Group.BUILD_BATTLE, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 1 \u0440\u0430\u0437");
    public static final Achievement BB_WIN_10 = new WinAchievement(1202, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c II", 3000, Group.BUILD_BATTLE, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 10 \u0440\u0430\u0437");
    public static final Achievement BB_WIN_100 = new WinAchievement(1203, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c III", 10000, Group.BUILD_BATTLE, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 100 \u0440\u0430\u0437");
    public static final Achievement BB_WIN_1000 = new WinAchievement(1204, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c IV", 50000, Group.BUILD_BATTLE, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 1.000 \u0440\u0430\u0437");
    public static final Achievement BB_WIN_10000 = new WinAchievement(1205, "\u0427\u0435\u043c\u043f\u0438\u043e\u043d", 200000, Group.BUILD_BATTLE, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 10.000 \u0440\u0430\u0437");
    public static final Achievement BB_ARCHITECTOR = new Achievement(1207, "\u0410\u0440\u0445\u0438\u0442\u0435\u043a\u0442\u043e\u0440", 5000, Group.BUILD_BATTLE, "\u0412\u044b\u0438\u0433\u0440\u0430\u0442\u044c \u0441 75-\u044e \u043e\u0447\u043a\u0430\u043c\u0438");
    public static final Achievement CP_LOBBY_PARKOUR = new Achievement(1301, "\u041f\u0430\u0440\u043a\u0443\u0440\u0438\u0441\u0442", 1500, Group.CLASH_POINT, "\u041f\u0440\u043e\u0439\u0442\u0438 \u043f\u0430\u0440\u043a\u0443\u0440 \u0432 \u043b\u043e\u0431\u0431\u0438 ClashPoint");
    public static final Achievement CP_RESOURCE_POINTS_BREAK_1 = new Achievement(1302, "\u041a\u0440\u0443\u0448\u0438\u0442\u0435\u043b\u044c I", 1500, Group.CLASH_POINT, "\u0421\u043b\u043e\u043c\u0430\u0442\u044c \u043e\u0434\u043d\u0443 \u0442\u043e\u0447\u043a\u0443 \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432");
    public static final Achievement CP_RESOURCE_POINTS_BREAK_10 = new Achievement(1303, "\u041a\u0440\u0443\u0448\u0438\u0442\u0435\u043b\u044c II", 3000, Group.CLASH_POINT, "\u0421\u043b\u043e\u043c\u0430\u0442\u044c 10 \u0442\u043e\u0447\u0435\u043a \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432");
    public static final Achievement CP_RESOURCE_POINTS_BREAK_100 = new Achievement(1304, "\u041a\u0440\u0443\u0448\u0438\u0442\u0435\u043b\u044c III", 7000, Group.CLASH_POINT, "\u0421\u043b\u043e\u043c\u0430\u0442\u044c 100 \u0442\u043e\u0447\u0435\u043a \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432");
    public static final Achievement CP_RESOURCE_POINTS_BREAK_500 = new Achievement(1305, "\u041a\u0440\u0443\u0448\u0438\u0442\u0435\u043b\u044c IV", 20000, Group.CLASH_POINT, "\u0421\u043b\u043e\u043c\u0430\u0442\u044c 500 \u0442\u043e\u0447\u0435\u043a \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432");
    public static final Achievement CP_KING_OF_THE_HILL = new Achievement(1306, "\u0426\u0430\u0440\u044c \u0433\u043e\u0440\u044b", 5000, Group.CLASH_POINT, "\u0421\u043a\u0438\u043d\u0443\u0442\u044c 20 \u0438\u0433\u0440\u043e\u043a\u043e\u0432 \u0437\u0430 \u0438\u0433\u0440\u0443");
    public static final Achievement CP_DESTROYER = new Achievement(1307, "\u0420\u0430\u0437\u0440\u0443\u0448\u0438\u0442\u0435\u043b\u044c", 5000, Group.CLASH_POINT, "\u0421\u043b\u043e\u043c\u0430\u0442\u044c 3 \u0442\u043e\u0447\u043a\u0438 \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432 \u0437\u0430 \u0438\u0433\u0440\u0443");
    public static final Achievement CP_SHOPPING = new Achievement(1308, "\u0428\u043e\u043f\u043f\u0438\u043d\u0433", 4000, Group.CLASH_POINT, "\u041f\u043e\u0442\u0440\u0430\u0442\u044c 64 \u0437\u043e\u043b\u043e\u0442\u0430 \u0437\u0430 \u0438\u0433\u0440\u0443");
    public static final Achievement CP_WIN_1 = new WinAchievement(1309, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c I", 1000, Group.CLASH_POINT, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 1 \u0440\u0430\u0437");
    public static final Achievement CP_WIN_10 = new WinAchievement(1310, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c II", 3000, Group.CLASH_POINT, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 10 \u0440\u0430\u0437");
    public static final Achievement CP_WIN_100 = new WinAchievement(1311, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c III", 10000, Group.CLASH_POINT, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 100 \u0440\u0430\u0437");
    public static final Achievement CP_WIN_1000 = new WinAchievement(1312, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c IV", 50000, Group.CLASH_POINT, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 1.000 \u0440\u0430\u0437");
    public static final Achievement CP_WIN_10000 = new WinAchievement(1313, "\u0427\u0435\u043c\u043f\u0438\u043e\u043d", 200000, Group.CLASH_POINT, "\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c 10.000 \u0440\u0430\u0437");
    public static final Achievement SECRET_COOL_HACKER = new Achievement(9002, "\u041a\u0443\u043b\u0445\u0430\u0446\u043a\u0435\u0440", 3000, Group.SECRET, "\u041d\u0430\u0439\u0442\u0438 \u0430\u0434\u043c\u0438\u043d\u0441\u043a\u0443\u044e \u043a\u043e\u043c\u0430\u043d\u0434\u0443");
    public static final Achievement SECRET_SELF_KILL = new Achievement(9003, "\u0417\u0440\u044f. \u041a\u0440\u044f.", 5000, Group.SECRET, "\u0417\u0430\u0441\u0442\u0440\u0435\u043b\u0438\u0442\u044c \u0441\u0435\u0431\u044f \u0438\u0437 \u043b\u0443\u043a\u0430");
    public static final Achievement SECRET_DROWNING = new Achievement(9004, "\u041f\u043e\u0447\u0442\u0438 \u0432\u043e\u0434\u043e\u043b\u0430\u0437", 5000, Group.SECRET, "\u0423\u0442\u043e\u043d\u0443\u0442\u044c \u0432 \u043b\u043e\u0431\u0431\u0438");
    public static final Achievement SECRET_SPACE = new Achievement(9005, "\u041c\u0430\u043c\u0430, \u044f \u0432 \u043a\u043e\u0441\u043c\u043e\u0441\u0435", 5000, Group.SECRET, "\u041f\u043e\u0434\u043d\u044f\u0442\u044c\u0441\u044f \u043d\u0430 \u0432\u044b\u0441\u043e\u0442\u0443 2000 \u0431\u043b\u043e\u043a\u043e\u0432");
    public static final Achievement SECRET_5YEARS = new Achievement(9006, "\u041d\u0430\u043c 5 \u043b\u0435\u0442", 50000, Group.SECRET, "\u0410\u043a\u0442\u0438\u0432\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u043f\u0440\u043e\u043c\u043e-\u043a\u043e\u0434 \u043d\u0430 5-\u043b\u0435\u0442\u0438\u0435 VimeWorld");
    public static final Achievement SECRET_NY_2017 = new Achievement(9001, "\u041d\u043e\u0432\u044b\u0439 \u0433\u043e\u0434 2017", 50000, Group.SECRET, "\u0417\u0430\u0439\u0442\u0438 \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440 \u0432", "\u043d\u043e\u0432\u043e\u0433\u043e\u0434\u043d\u044e\u044e \u043d\u043e\u0447\u044c 2016 - 2017");
    public static final Achievement SECRET_NY_2018 = new Achievement(9007, "\u041d\u043e\u0432\u044b\u0439 \u0433\u043e\u0434 2018", 50000, Group.SECRET, "\u0417\u0430\u0439\u0442\u0438 \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440 \u0432", "\u043d\u043e\u0432\u043e\u0433\u043e\u0434\u043d\u044e\u044e \u043d\u043e\u0447\u044c 2017 - 2018");
    private final int id;
    private String name;
    private int reward;
    private String[] description;
    private Group group;
    private boolean hidden = false;

    protected Achievement(int id, String name, int reward, Group group, String ... description) {
        this.id = id;
        this.name = name;
        this.reward = reward;
        this.group = group;
        this.description = description;
        this.group.achievements.add(this);
        if (byId.put(id, (Object)this) != null) {
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

    public static List<Achievement> getAchievements() {
        if (values == null) {
            values = new ArrayList<Achievement>(byId.size());
            TIntObjectIterator it = byId.iterator();
            while (it.hasNext()) {
                it.advance();
                values.add((Achievement)it.value());
            }
            values = Collections.unmodifiableList(values);
        }
        return values;
    }

    public static Achievement byId(int id) {
        return (Achievement)byId.get(id);
    }

    public static enum Group {
        GLOBAL("\u0413\u043b\u043e\u0431\u0430\u043b\u044c\u043d\u044b\u0435", 3, Material.CAKE),
        SECRET("\u0421\u0435\u043a\u0440\u0435\u0442\u043d\u044b\u0435", 5, new ItemStack(Material.MONSTER_EGG, 1, 58)),
        LOBBY("\u041b\u043e\u0431\u0431\u0438", 4, Material.COMPASS),
        SKY_WARS("SkyWars", 31, Material.EYE_OF_ENDER),
        BED_WARS("BedWars", 32, Material.BED),
        GUN_GAME("GunGame", 20, Material.GOLD_SWORD),
        MOB_WARS("MobWars", 22, new ItemStack(Material.MONSTER_EGG, 1, 50)),
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
        final List<Achievement> achievements = new LinkedList<Achievement>();

        private Group(String name, int slot, Material mat) {
            this(name, slot, new ItemStack(mat));
        }

        private Group(String name, int slot, ItemStack is) {
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

        public List<Achievement> getAchievements() {
            return this.achievements;
        }

        public String getName() {
            return this.name;
        }
    }
}

