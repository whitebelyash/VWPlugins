package net.xtrafrancyz.bukkit.texteria.utils;

public class OnClick {
   public Action action;
   public Object data;

   public OnClick(Action action, Object data) {
      this.action = action;
      this.data = data;
   }

   public static enum Action {
      URL,
      CHAT,
      CALLBACK;
   }
}
