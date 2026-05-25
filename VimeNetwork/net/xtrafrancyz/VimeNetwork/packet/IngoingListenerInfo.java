package net.xtrafrancyz.VimeNetwork.packet;

import java.util.function.Consumer;
import org.bukkit.plugin.Plugin;

class IngoingListenerInfo {
   public final Plugin plugin;
   public final Consumer listener;

   public IngoingListenerInfo(Plugin plugin, Consumer listener) {
      this.plugin = plugin;
      this.listener = listener;
   }
}
