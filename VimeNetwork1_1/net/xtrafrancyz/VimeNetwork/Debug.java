package net.xtrafrancyz.VimeNetwork;

import java.util.logging.Logger;

public enum Debug {
   MYSQL,
   GOALS,
   CORE;

   private static final Logger logger = VNPlugin.instance().getLogger();
   private boolean enabled = false;

   public void info(String str) {
      if (this.enabled) {
         logger.info(str);
      }

   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public boolean isEnabled() {
      return this.enabled;
   }
}
