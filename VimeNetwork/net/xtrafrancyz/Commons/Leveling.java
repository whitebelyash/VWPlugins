package net.xtrafrancyz.Commons;

public class Leveling {
   private static int[] EXP_TOTAL = new int[500];
   private static int[] EXP_PARTIAL = new int[500];

   public static int getLevel(int exp) {
      int[] arr = EXP_TOTAL;
      if (exp < arr[0]) {
         return 1;
      } else if (exp > arr[arr.length - 1]) {
         return 0;
      } else {
         int left = 0;
         int right = arr.length;

         while(true) {
            int mid = (right + left) / 2;
            if (arr[mid] == exp) {
               return mid + 2;
            }

            if (right - left == 1) {
               return left + 2;
            }

            if (arr[mid] > exp) {
               right = mid;
            } else {
               left = mid;
            }
         }
      }
   }

   public static int getTotalExp(int level) {
      return level <= 1 ? 0 : EXP_TOTAL[level - 2];
   }

   public static int getExpToNextLevel(int level) {
      return level <= 0 ? 0 : EXP_PARTIAL[level - 1];
   }

   static {
      EXP_TOTAL[0] = EXP_PARTIAL[0] = 8000;

      for(int i = 1; i < EXP_TOTAL.length; ++i) {
         EXP_PARTIAL[i] = EXP_PARTIAL[i - 1] + 2000;
         EXP_TOTAL[i] = EXP_TOTAL[i - 1] + EXP_PARTIAL[i];
      }

   }
}
