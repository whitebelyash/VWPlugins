package net.xtrafrancyz.Core.network.connector;

class CallbackData {
   public int id;
   public CoreCallback callback;
   public Runnable onTimeout;
   public long timeToLive;

   public CallbackData(int id, CoreCallback callback, long timeout, Runnable onTimeout) {
      this.id = id;
      this.callback = callback;
      this.timeToLive = System.currentTimeMillis() + timeout;
      this.onTimeout = onTimeout;
   }
}
