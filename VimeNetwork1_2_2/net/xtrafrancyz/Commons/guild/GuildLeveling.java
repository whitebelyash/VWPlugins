/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Commons.guild;

public class GuildLeveling {
    private static final int BASE = 50000;
    private static final int GROWTH = 10000;
    private static final int MAX_LEVEL = 256;
    private static final int MAX_EXP;
    private static int[] EXP_TOTAL;

    public static int getLevel(int exp) {
        int[] arr = EXP_TOTAL;
        if (exp < arr[0]) {
            return 1;
        }
        if (exp > MAX_EXP) {
            return 0;
        }
        int left = 0;
        int right = 256;
        int mid;
        while (arr[mid = (right + left) / 2] != exp) {
            if (right - left == 1) {
                return left + 2;
            }
            if (arr[mid] > exp) {
                right = mid;
                continue;
            }
            left = mid;
        }
        return mid + 2;
    }

    public static int getTotalExp(int level) {
        if (level <= 1) {
            return 0;
        }
        return EXP_TOTAL[level - 2];
    }

    public static int getExpToNextLevel(int level) {
        if (level <= 0) {
            return 50000;
        }
        return 10000 * (level - 1) + 50000;
    }

    public static double getPercentageToNextLevel(int exp) {
        int lvl = GuildLeveling.getLevel(exp);
        return (double)(exp - GuildLeveling.getTotalExp(lvl)) / (double)GuildLeveling.getExpToNextLevel(lvl);
    }

    static {
        EXP_TOTAL = new int[256];
        GuildLeveling.EXP_TOTAL[0] = 50000;
        for (int i = 1; i < 256; ++i) {
            GuildLeveling.EXP_TOTAL[i] = EXP_TOTAL[i - 1] + GuildLeveling.getExpToNextLevel(i + 1);
        }
        MAX_EXP = EXP_TOTAL[255];
    }
}

