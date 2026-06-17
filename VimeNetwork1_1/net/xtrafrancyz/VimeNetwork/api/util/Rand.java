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
   public static Object of(Object... args) {
      return args[nextInt(args.length)];
   }

   public static Object of(Collection collection) {
      if (collection instanceof List) {
         return of((List)collection);
      } else {
         int index = nextInt(collection.size());
         Iterator<T> it = collection.iterator();

         for(int i = 0; i < index; ++i) {
            it.next();
         }

         return it.next();
      }
   }

   public static Object of(List list) {
      return list.get(nextInt(list.size()));
   }

   @SafeVarargs
   public static Object of(List... lists) {
      int var = 0;

      for(List l : lists) {
         var += l.size();
      }

      var = nextInt(var);

      for(List l : lists) {
         if (var < l.size()) {
            return l.get(var);
         }

         var -= l.size();
      }

      throw new IllegalArgumentException("Received lists is empty");
   }

   public static Enum of(Class enumClazz) {
      return (Enum)of(enumClazz.getEnumConstants());
   }

   public static int intRange(int from, int to) {
      int min = Math.min(from, to);
      int max = Math.max(from, to);
      return min + nextInt(max - min + 1);
   }

   public static float floatRange(float from, float to) {
      float min = Math.min(from, to);
      float max = Math.max(from, to);
      return nextFloat() * (max - min) + min;
   }

   public static double doubleRange(double from, double to) {
      double min = Math.min(from, to);
      double max = Math.max(from, to);
      return nextDouble() * (max - min) + min;
   }

   public static float nextFloat() {
      return getRandom().nextFloat();
   }

   public static double nextDouble() {
      return getRandom().nextDouble();
   }

   public static boolean nextBoolean() {
      return getRandom().nextBoolean();
   }

   public static int nextInt(int limit) {
      return getRandom().nextInt(limit);
   }

   public static Random getRandom() {
      return ThreadLocalRandom.current();
   }
}
