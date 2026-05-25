package net.xtrafrancyz.VimeNetwork.luckyblock;

class LBActionEntry {
   LBAction action;
   int weight;

   public LBActionEntry(int weight, LBAction action) {
      this.weight = weight;
      this.action = action;
   }
}
