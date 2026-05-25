package net.xtrafrancyz.GameReloader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.v1_6_R3.FileIOThread;
import net.minecraft.server.v1_6_R3.RegionFileCache;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.Lobby.State;
import net.xtrafrancyz.VimeNetwork.api.updater.UpdateWatcher;
import net.xtrafrancyz.VimeNetwork.api.util.FileUtil;
import net.xtrafrancyz.VimeNetwork.api.util.Rand;
import net.xtrafrancyz.VimeNetwork.api.util.Reflect;
import net.xtrafrancyz.VimeNetwork.api.util.ZipUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_6_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

class ReloadTask implements Runnable {
   private JavaPlugin plugin;
   private File rootDir = Bukkit.getWorldContainer();
   private Logger logger;

   public ReloadTask(JavaPlugin plugin, Logger logger) {
      this.plugin = plugin;
      this.logger = logger;
   }

   public void run() {
      try {
         if (VimeNetwork.updateWatcher().isRestartNeeded()) {
            this.shutdown("&6Обновление", false);
            return;
         }

         long start = System.currentTimeMillis();

         for(Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer(ChatColor.RED + "Перезагрузка сервера");
         }

         this.logger.info("Disabling plugin: " + this.plugin.getName());
         Bukkit.getPluginManager().disablePlugin(this.plugin);
         this.logger.info("Pasting new config...");
         this.pasteNewConfig2();
         this.logger.info("Enabling plugin: " + this.plugin.getName());
         Bukkit.getPluginManager().enablePlugin(this.plugin);
         this.logger.info("Reload complete (" + (double)(System.currentTimeMillis() - start) / (double)1000.0F + " sec.)");
      } catch (Exception ex) {
         ex.printStackTrace();
         this.shutdown("&6Регенерация");
      }

      GameReloader.reloadInProgress = false;
   }

   private void pasteNewConfig2() {
      File[] configs = (new File(UpdateWatcher.SERVER_UPDATE_DIR, "configs")).listFiles((file) -> file.isFile() && file.getName().toLowerCase().endsWith(".zip"));
      if (configs == null) {
         this.shutdown("&6Регенерация");
      } else {
         RegionFileCache.a();

         for(World world : Bukkit.getWorlds()) {
            for(Player player : world.getPlayers()) {
               player.kickPlayer(ChatColor.RED + "Перезагрузка сервера");
            }

            world.getEntities().forEach(Entity::remove);
            boolean oldKeepSpawnInMemory = world.getKeepSpawnInMemory();
            ((CraftWorld)world).getHandle().keepSpawnInMemory = false;
            world.setKeepSpawnInMemory(false);

            for(Chunk chunk : world.getLoadedChunks()) {
               ((CraftChunk)chunk).getHandle().mustSave = false;
               chunk.unload(false, false);
            }

            ((CraftWorld)world).getHandle().keepSpawnInMemory = oldKeepSpawnInMemory;
            Reflect.set(((CraftWorld)world).getHandle(), "lastChunkAccessed", (Object)null);
            Reflect.invoke(Reflect.get(((CraftWorld)world).getHandle(), "chunkTickList"), "clear", new Object[0]);
            Reflect.invoke(Reflect.get(((CraftWorld)world).getHandle(), "tickEntriesByChunk"), "clear", new Object[0]);
            Reflect.invoke(Reflect.get(Reflect.get(world, "blockMetadata"), "metadataMap"), "clear", new Object[0]);
            FileUtil.delete(world.getWorldFolder());
            if (world.getWorldFolder().exists()) {
               this.logger.warning("Cannot delete world");
               this.shutdown("&6Регенерация");
               return;
            }
         }

         Reflect.invoke(Reflect.get(((CraftServer)Bukkit.getServer()).getEntityMetadata(), "metadataMap"), "clear", new Object[0]);
         Reflect.invoke(Reflect.get(((CraftServer)Bukkit.getServer()).getPlayerMetadata(), "metadataMap"), "clear", new Object[0]);
         Reflect.invoke(Reflect.get(((CraftServer)Bukkit.getServer()).getWorldMetadata(), "metadataMap"), "clear", new Object[0]);
         FileIOThread.a.a();
         RegionFileCache.a();

         try {
            File config = this.getConfig(configs);
            if (config == null) {
               this.shutdown("&6Регенерация");
               return;
            }

            this.logger.info("Pasted config: " + config.getName());
            ZipUtil.unzip(config, this.rootDir);
         } catch (IOException e) {
            e.printStackTrace();
         }

         for(World world : Bukkit.getWorlds()) {
            world.getWorldFolder().mkdir();
            Reflect.invoke(((CraftWorld)world).getHandle().getDataManager(), "h", new Object[0]);
            (new File(world.getWorldFolder(), "players")).mkdir();
         }

         System.gc();
      }
   }

   private File getConfig(File[] configs) {
      String config = null;

      try {
         for(String str : Files.readAllLines((new File(this.rootDir, "_install.sh")).toPath(), StandardCharsets.UTF_8)) {
            if (str.startsWith("CONFIG=")) {
               config = str.substring(8, str.length() - 1);
               break;
            }
         }

         this.logger.info("Readed config name: " + config);
      } catch (IOException e) {
         e.printStackTrace();
      }

      if (config == null) {
         return null;
      } else {
         config = config + ".zip";

         for(File file : configs) {
            if (file.getName().equals(config)) {
               return file;
            }
         }

         return (File)Rand.of(configs);
      }
   }

   private void shutdown(String status) {
      this.shutdown(status, true);
   }

   private void shutdown(String status, boolean printStacktrace) {
      if (printStacktrace) {
         this.logger.info("Something is failed");
         this.logger.log(Level.INFO, "Stacktrace", new Exception("Fake exception"));
      }

      VimeNetwork.toLobby(Bukkit.getOnlinePlayers());
      VimeNetwork.lobby().setConnectableState(State.OFFLINE);
      VimeNetwork.lobby().setMenuText(new String[]{status});
      VimeNetwork.lobby().shutdown();
      Bukkit.shutdown();
   }
}
