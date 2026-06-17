/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Rand {
    private Rand() {
    }

    @SafeVarargs
    public static <T> T of(T ... args) {
        return args[Rand.nextInt(args.length)];
    }

    public static <T> T of(Collection<T> collection) {
        if (collection instanceof List) {
            return Rand.of((List)collection);
        }
        int index = Rand.nextInt(collection.size());
        Iterator<T> it = collection.iterator();
        for (int i = 0; i < index; ++i) {
            it.next();
        }
        return it.next();
    }

    public static <T> T of(List<T> list) {
        return list.get(Rand.nextInt(list.size()));
    }

    @SafeVarargs
    public static <T> T of(List<T> ... lists) {
        int var = 0;
        for (List<T> l : lists) {
            var += l.size();
        }
        var = Rand.nextInt(var);
        for (List<T> l : lists) {
            if (var >= l.size()) {
                var -= l.size();
                continue;
            }
            return l.get(var);
        }
        throw new IllegalArgumentException("Received lists is empty");
    }

    public static <T extends Enum> T of(Class<T> enumClazz) {
        return (T)((Enum)Rand.of(enumClazz.getEnumConstants()));
    }

    public static int intRange(int from, int to) {
        int min = Math.min(from, to);
        int max = Math.max(from, to);
        return min + Rand.nextInt(max - min + 1);
    }

    public static float floatRange(float from, float to) {
        float min = Math.min(from, to);
        float max = Math.max(from, to);
        return Rand.nextFloat() * (max - min) + min;
    }

    public static double doubleRange(double from, double to) {
        double min = Math.min(from, to);
        double max = Math.max(from, to);
        return Rand.nextDouble() * (max - min) + min;
    }

    public static float nextFloat() {
        return Rand.getRandom().nextFloat();
    }

    public static double nextDouble() {
        return Rand.getRandom().nextDouble();
    }

    public static boolean nextBoolean() {
        return Rand.getRandom().nextBoolean();
    }

    public static int nextInt(int limit) {
        return Rand.getRandom().nextInt(limit);
    }

    public static Random getRandom() {
        return ThreadLocalRandom.current();
    }
}

