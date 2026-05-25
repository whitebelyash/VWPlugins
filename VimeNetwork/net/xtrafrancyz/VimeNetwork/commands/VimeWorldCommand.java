package net.xtrafrancyz.VimeNetwork.commands;

import com.google.common.base.Joiner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import net.xtrafrancyz.Commons.F;
import net.xtrafrancyz.Core.network.packet.Packet1PlayerInfo;
import net.xtrafrancyz.Core.network.packet.Packet202GetPlayerInfo;
import net.xtrafrancyz.Core.network.packet.Packet61SendPlayerToServer;
import net.xtrafrancyz.VimeNetwork.Debug;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.Lobby;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.command.CmdSub;
import net.xtrafrancyz.VimeNetwork.api.command.CommandRoot;
import net.xtrafrancyz.VimeNetwork.api.command.SubCommandData;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.player.treasure.TreasureType;
import net.xtrafrancyz.VimeNetwork.api.updater.UpdateWatcher;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.Spectators;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.bukkit.texteria.utils.ParsedTime;
import org.apache.mina.core.service.IoServiceStatistics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VimeWorldCommand extends CommandRoot {
   private final VNPlugin plugin;

   public VimeWorldCommand(VNPlugin plugin) {
      this.plugin = plugin;
   }

   protected void runCommand(Runnable action, CommandSender sender, Command cmd, String label, String[] args) {
      if (!(sender instanceof Player)) {
         super.runCommand(action, sender, cmd, label, args);
      } else {
         NetworkPlayer player = VimeNetwork.getPlayer(sender.getName());
         player.getAchievements().complete(Achievement.SECRET_COOL_HACKER);
         if (player.getRank().has(Rank.CHIEF)) {
            super.runCommand(action, sender, cmd, label, args);
         }

      }
   }

   protected boolean main(CommandSender sender, Command cmd, String label, String[] args) {
      this.help(new SubCommandData(sender, label, "help", new String[0]));
      return false;
   }

   @CmdSub(
      value = {"spec"},
      rank = Rank.CHIEF
   )
   private void spectator(SubCommandData data) {
      if (Spectators.instance().contains(data.getPlayer())) {
         U.msg(data.getSender(), "&dВы стали обычным игроком");
         Spectators.instance().remove(data.getPlayer());
      } else {
         U.msg(data.getSender(), "&dВы стали спектром");
         Spectators.instance().add(data.getPlayer());
      }

   }

   @CmdSub(
      value = {"tolobby"},
      rank = Rank.CHIEF
   )
   private void toLobby(SubCommandData data) {
      if (data.getArgs().length == 0) {
         U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <игрок>");
      } else {
         Player player = this.plugin.getServer().getPlayerExact(data.getArgs()[0]);
         if (player != null) {
            VimeNetwork.toLobby(player);
         } else {
            U.msg(data.getSender(), "&cИгрок " + data.getArgs()[0] + " не найден");
         }

      }
   }

   @CmdSub(
      value = {"tolobbyall"},
      rank = Rank.CHIEF
   )
   private void toLobbyAll(SubCommandData data) {
      U.msg(data.getSender(), "&aВсе игроки отправлены в лобби ");
      VimeNetwork.toLobby(Bukkit.getOnlinePlayers());
   }

   @CmdSub(
      value = {"toserver"},
      rank = Rank.CHIEF
   )
   private void toServer(SubCommandData data) {
      if (data.getArgs().length != 2) {
         U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <игрок> <сервер>");
      } else {
         Player player = this.plugin.getServer().getPlayerExact(data.getArgs()[0]);
         if (player != null) {
            VimeNetwork.toServer(data.getArgs()[1], player);
         } else {
            U.msg(data.getSender(), "&cИгрок " + data.getArgs()[0] + " не найден");
         }

      }
   }

   @CmdSub(
      value = {"toserverall"},
      rank = Rank.CHIEF
   )
   private void toServerAll(SubCommandData data) {
      if (data.getArgs().length != 1) {
         U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <сервер>");
      } else {
         U.msg(data.getSender(), "&aВсе игроки отправлены на сервер " + data.getArgs()[0]);
         VimeNetwork.toServer(data.getArgs()[0], Bukkit.getOnlinePlayers());
      }
   }

   @CmdSub(
      value = {"toserverof"},
      rank = Rank.CHIEF
   )
   private void toServerOf(SubCommandData data) {
      if (data.getArgs().length != 1) {
         U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <игрок>");
      } else {
         VimeNetwork.core().sendPacket(new Packet202GetPlayerInfo(data.getArgs()[0], 8), (p) -> {
            if (p.getId() == 1) {
               VimeNetwork.toServer(((Packet1PlayerInfo)p).bukkit, data.getPlayer());
            } else {
               U.msg(data.getSender(), "&cИгрок " + data.getArgs()[0] + " не найден");
            }

         }, 300L, () -> U.msg(data.getSender(), "&cОшибка связи с главным сервером"));
      }
   }

   @CmdSub(
      value = {"summon"},
      rank = Rank.CHIEF
   )
   private void summon(SubCommandData data) {
      if (data.getArgs().length != 1) {
         U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <игрок>");
      } else {
         VimeNetwork.core().sendPacket(new Packet202GetPlayerInfo(data.getArgs()[0], 8), (p) -> {
            if (p.getId() == 1) {
               U.msg(data.getSender(), "&aИгрок &f" + ((Packet1PlayerInfo)p).username + "&a телепортирован к вам на сервер");
               VimeNetwork.core().sendPacket(new Packet61SendPlayerToServer(((Packet1PlayerInfo)p).username, VimeNetwork.lobby().getServerId()));
            } else {
               U.msg(data.getSender(), "&cИгрок " + data.getArgs()[0] + " не найден");
            }

         }, 300L, () -> U.msg(data.getSender(), "&cОшибка связи с главным сервером"));
      }
   }

   @CmdSub(
      value = {"item"},
      aliases = {"i"},
      rank = Rank.CHIEF
   )
   private void item(SubCommandData data) {
      data.getPlayer().getInventory().addItem(new ItemStack[]{Items.parse(Joiner.on(' ').join(data.getArgs()))});
   }

   @CmdSub(
      value = {"stats"},
      aliases = {"status"},
      rank = Rank.CHIEF
   )
   private void stats(SubCommandData data) {
      Runtime runtime = Runtime.getRuntime();
      List<String> lines = new ArrayList();
      lines.add("&e------------ &fСтатистика &e------------");
      lines.add("&eВремя работы: &f" + (new ParsedTime(System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime())).format());
      lines.add("&eПамять: &f" + (runtime.totalMemory() - runtime.freeMemory()) / 1024L / 1024L + " MB / " + runtime.totalMemory() / 1024L / 1024L + " MB up to " + runtime.maxMemory() / 1024L / 1024L + " MB");
      lines.add("&eПодключение к бд: " + (this.plugin.mysql.isConnected() ? "&aактивно" : "&cразорвано"));
      lines.add("&eЗапросов к бд: &f" + this.plugin.mysql.getExecutedQueries());
      lines.add("&eПодключение к Core: &f" + (this.plugin.core.isConnected() ? "&aактивно" : "&cразорвано"));
      if (this.plugin.core.isConnected()) {
         IoServiceStatistics statistics = this.plugin.core.getConnector().getStatistics();
         lines.add("&eПакетов к Core: &f" + (statistics.getWrittenMessages() + statistics.getReadMessages()));
         lines.add("&eТрафик к Core: &f" + F.formatBytes(statistics.getWrittenBytes() + statistics.getReadBytes()));
      }

      U.msg(data.getSender(), lines);
   }

   @CmdSub(
      value = {"ptime"},
      rank = Rank.CHIEF
   )
   private void ptime(SubCommandData data) {
      if (data.getArgs().length != 1) {
         U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <сдвиг во времени> (0 - сбросить)");
      } else {
         data.getPlayer().setPlayerTime(Long.parseLong(data.getArgs()[0]), true);
      }
   }

   @CmdSub(
      value = {"addcoins"},
      rank = Rank.CHIEF
   )
   private void addCoins(SubCommandData data) {
      if (data.getArgs().length != 2) {
         U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <игрок|@all> <количество>");
      } else {
         int coins = Integer.parseInt(data.getArgs()[1]);
         if (data.getArgs()[0].equals("@all")) {
            for(Player player : Bukkit.getOnlinePlayers()) {
               VimeNetwork.getPlayer(player).addCoins(coins);
            }
         } else {
            Player player = this.plugin.getServer().getPlayerExact(data.getArgs()[0]);
            if (player != null) {
               VimeNetwork.getPlayer(player).addCoins(coins);
            }
         }

      }
   }

   @CmdSub(
      value = {"addcoinsexact"},
      rank = Rank.CHIEF
   )
   private void addCoinsExact(SubCommandData data) {
      if (data.getArgs().length != 2) {
         U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <игрок|@all> <количество>");
      } else {
         int coins = Integer.parseInt(data.getArgs()[1]);
         if (data.getArgs()[0].equals("@all")) {
            for(Player player : Bukkit.getOnlinePlayers()) {
               VimeNetwork.getPlayer(player).addCoinsExact(coins);
            }
         } else {
            Player player = this.plugin.getServer().getPlayerExact(data.getArgs()[0]);
            if (player != null) {
               VimeNetwork.getPlayer(player).addCoinsExact(coins);
            }
         }

      }
   }

   @CmdSub(
      value = {"giveexp"},
      rank = Rank.CHIEF
   )
   private void giveExp(SubCommandData data) {
      if (data.getArgs().length != 2) {
         U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <игрок|@all> <количество>");
      } else {
         int exp = Integer.parseInt(data.getArgs()[1]);
         if (data.getArgs()[0].equals("@all")) {
            for(Player player : Bukkit.getOnlinePlayers()) {
               VimeNetwork.getPlayer(player).giveExp(exp);
            }
         } else {
            Player player = this.plugin.getServer().getPlayerExact(data.getArgs()[0]);
            if (player != null) {
               VimeNetwork.getPlayer(player).giveExp(exp);
            }
         }

      }
   }

   @CmdSub(
      value = {"addchest"},
      rank = Rank.CHIEF
   )
   private void addChest(SubCommandData data) {
      if (data.getArgs().length < 3) {
         U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <игрок> <basic|ancient|mythical> <количество>");
      } else {
         Player player = Bukkit.getPlayerExact(data.getArgs()[0]);
         if (player == null) {
            U.msg(data.getSender(), "&cИгрок " + data.getArgs()[0] + " не найден");
         } else {
            TreasureType type;
            try {
               type = TreasureType.valueOf(data.getArgs()[1].toUpperCase());
            } catch (Exception var7) {
               U.msg(data.getSender(), "&cТип сундука " + data.getArgs()[1] + " не найден");
               return;
            }

            int amount;
            try {
               amount = Integer.parseInt(data.getArgs()[2]);
            } catch (Exception var6) {
               U.msg(data.getSender(), "&cКоличество должно быть числом");
               return;
            }

            VimeNetwork.getPlayer(player).getTreasures().add(type, amount);
         }
      }
   }

   @CmdSub(
      value = {"takechest"},
      rank = Rank.CHIEF
   )
   private void takeChest(SubCommandData data) {
      if (data.getArgs().length < 3) {
         U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <игрок> <basic|ancient|mythical> <количество>");
      } else {
         Player player = Bukkit.getPlayerExact(data.getArgs()[0]);
         if (player == null) {
            U.msg(data.getSender(), "&cИгрок " + data.getArgs()[0] + " не найден");
         } else {
            TreasureType type;
            try {
               type = TreasureType.valueOf(data.getArgs()[1].toUpperCase());
            } catch (Exception var7) {
               U.msg(data.getSender(), "&cТип сундука " + data.getArgs()[1] + " не найден");
               return;
            }

            int amount;
            try {
               amount = Integer.parseInt(data.getArgs()[2]);
            } catch (Exception var6) {
               U.msg(data.getSender(), "&cКоличество должно быть числом");
               return;
            }

            VimeNetwork.getPlayer(player).getTreasures().take(type, amount);
         }
      }
   }

   @CmdSub(
      value = {"gc"},
      rank = Rank.ADMIN
   )
   private void gc(SubCommandData data) {
      long start = System.nanoTime();
      System.gc();
      U.msg(data.getSender(), "&aСборщик мусора отработал: " + F.formatFloat((float)(System.nanoTime() - start) / 1000000.0F, 2) + " мс.");
   }

   @CmdSub(
      value = {"setname"},
      rank = Rank.ADMIN
   )
   private void setName(SubCommandData data) {
      if (data.getArgs().length == 0) {
         U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <новый ник>");
      } else {
         StringBuilder name = new StringBuilder(data.getArgs()[0]);

         for(int i = 1; i < data.getArgs().length; ++i) {
            name.append(" ").append(data.getArgs()[i]);
         }

         VimeNetwork.getPlayer(data.getPlayer()).setTag(name.toString());
         U.msg(data.getSender(), "&aНаслаждайтесь новым именем! - &r" + name);
      }
   }

   @CmdSub(
      value = {"debug"},
      rank = Rank.ADMIN
   )
   private void debug(SubCommandData data) {
      try {
         Debug group = Debug.valueOf(data.getArgs()[0].toUpperCase());
         if (group.isEnabled()) {
            U.msg(data.getSender(), "&e" + group.name() + " дебаг &cвыключен.");
            group.setEnabled(false);
         } else {
            U.msg(data.getSender(), "&e" + group.name() + " дебаг &aвключен.");
            group.setEnabled(true);
         }
      } catch (Exception var8) {
         StringBuilder str = new StringBuilder("<");

         for(Debug group : Debug.values()) {
            if (str.length() != 1) {
               str.append("&e, ");
            }

            if (group.isEnabled()) {
               str.append("&a").append(group.name());
            } else {
               str.append("&c").append(group.name());
            }
         }

         str.append("&e>");
         U.msg(data.getSender(), "&e/" + data.getLabel() + " debug " + str);
      }

   }

   @CmdSub(
      value = {"restart"},
      rank = Rank.ADMIN
   )
   private void restart(SubCommandData data) {
      VimeNetwork.toLobby(Bukkit.getOnlinePlayers());
      Bukkit.setWhitelist(true);
      VimeNetwork.lobby().setConnectableState(Lobby.State.DENY_ALL);
      VimeNetwork.lobby().shutdown();
      Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, Bukkit::shutdown, 40L);
   }

   @CmdSub(
      value = {"lobbysign"},
      rank = Rank.ADMIN
   )
   private void lobbySign(SubCommandData data) {
      Block block = data.getPlayer().getTargetBlock((HashSet)null, 5);
      if (block != null && block.getState() != null && block.getState() instanceof Sign) {
         Sign sign = (Sign)block.getState();
         sign.setLine(0, ChatColor.GRAY + "[" + ChatColor.GREEN + "Lobby" + ChatColor.GRAY + "]");
         sign.setLine(1, ChatColor.WHITE + "Вернуться");
         sign.setLine(2, ChatColor.WHITE + "в лобби");
         sign.setLine(3, (String)null);
         sign.update();
         U.msg(data.getSender(), "&aТабличка успешно установлена");
      } else {
         U.msg(data.getSender(), "&cВы должны смотреть на табличку");
      }
   }

   @CmdSub(
      value = {"head"},
      rank = Rank.ADMIN
   )
   private void head(SubCommandData data) {
      if (data.getArgs().length == 0) {
         U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <ник>");
      } else {
         data.getPlayer().getInventory().addItem(new ItemStack[]{Items.head(data.getArgs()[0])});
      }
   }

   @CmdSub(
      value = {"srv-config"},
      rank = Rank.CHIEF
   )
   private void srvConfig(SubCommandData data) {
      if (data.getArgs().length == 0) {
         U.msg(data.getSender(), "&e/" + data.getLabel() + " " + data.getSub() + "&7 list [game]&f: список конфигов для игры", "&e/" + data.getLabel() + " " + data.getSub() + "&7 set [game] <config-name>&f: установить заданный конфиг", "&e/" + data.getLabel() + " " + data.getSub() + "&7 get&f: узнать какой сейчас конфиг стоит", "&e/" + data.getLabel() + " " + data.getSub() + "&7 reset&f: вернуть все как было");
      } else {
         switch (data.getArgs()[0].toLowerCase()) {
            case "list":
               String game = VimeNetwork.lobby().getServerTypeId();
               if (data.getArgs().length == 2) {
                  game = data.getArgs()[1].toUpperCase();
               }

               if (game.contains("/") || game.contains("\\")) {
                  U.msg(data.getSender(), "&cТы что, охуел слеши пихать");
                  return;
               }

               File configsDir = new File(UpdateWatcher.UPDATE_DIR, game + "/configs");
               if (!configsDir.exists()) {
                  U.msg(data.getSender(), "&cИгра " + game + " не существует");
                  return;
               }

               File[] files = configsDir.listFiles((filex) -> filex.isFile() && filex.getName().endsWith(".zip"));
               if (files != null && files.length != 0) {
                  List<String> available = new ArrayList(files.length);

                  for(File file : files) {
                     String name = file.getName();
                     available.add(name.substring(0, name.lastIndexOf(46)));
                  }

                  Collections.sort(available);
                  U.msg(data.getSender(), "&aДоступные конфиги для сервера " + game + ":", Joiner.on("&7,&f ").join(available));
               } else {
                  U.msg(data.getSender(), "&aТам ничего нет");
               }
               break;
            case "set":
               String game;
               String configName;
               if (data.getArgs().length == 2) {
                  game = VimeNetwork.lobby().getServerTypeId();
                  configName = data.getArgs()[1];
               } else {
                  if (data.getArgs().length != 3) {
                     U.msg(data.getSender(), "&cНе так написал");
                     return;
                  }

                  game = data.getArgs()[1].toUpperCase();
                  configName = data.getArgs()[2];
               }

               if (game.contains("/") || game.contains("\\") || configName.contains("/") || configName.contains("\\")) {
                  U.msg(data.getSender(), "&cТы что, охуел слеши пихать");
                  return;
               }

               File config = new File(UpdateWatcher.UPDATE_DIR, game + "/configs/" + configName + ".zip");
               if (!config.exists()) {
                  U.msg(data.getSender(), "&cТакого конфига не существует");
                  return;
               }

               if (!(new File("_install.sh.orig")).exists()) {
                  (new File("_install.sh")).renameTo(new File("_install.sh.orig"));
               }

               BufferedWriter writer = null;
               BufferedReader reader = null;

               try {
                  writer = new BufferedWriter(new FileWriter("_install.sh"));
                  reader = new BufferedReader(new FileReader("_install.sh.orig"));

                  String line;
                  while((line = reader.readLine()) != null) {
                     if (line.startsWith("CONFIG=")) {
                        if (game.equals(VimeNetwork.lobby().getServerTypeId())) {
                           line = "CONFIG=\"" + configName + "\"";
                        } else {
                           line = "CONFIG=\"../../" + game + "/configs/" + configName + "\"";
                        }
                     }

                     writer.write(line);
                     writer.newLine();
                  }
               } catch (IOException e) {
                  e.printStackTrace();
                  U.msg(data.getSender(), e.getMessage());
               } finally {
                  if (writer != null) {
                     try {
                        writer.close();
                     } catch (IOException var40) {
                     }
                  }

                  if (reader != null) {
                     try {
                        reader.close();
                     } catch (IOException var39) {
                     }
                  }

               }

               U.msg(data.getSender(), "&aКонфиг успешно установлен", "Теперь сервер надо перезагрузить");
               break;
            case "get":
               String config = null;

               try {
                  BufferedReader reader = new BufferedReader(new FileReader("_install.sh"));
                  Throwable files = null;

                  try {
                     String line;
                     try {
                        while((line = reader.readLine()) != null) {
                           if (line.startsWith("CONFIG=")) {
                              config = line.substring(8, line.length() - 1);
                           }
                        }
                     } catch (Throwable var42) {
                        files = var42;
                        throw var42;
                     }
                  } finally {
                     if (reader != null) {
                        if (files != null) {
                           try {
                              reader.close();
                           } catch (Throwable var41) {
                              files.addSuppressed(var41);
                           }
                        } else {
                           reader.close();
                        }
                     }

                  }
               } catch (Exception var44) {
               }

               if (config != null) {
                  U.msg(data.getSender(), "&aСейчас стоит конфиг: &f" + config);
               } else {
                  U.msg(data.getSender(), "&cБред какой-то, серв битый");
               }
               break;
            case "reset":
               File orig = new File("_install.sh.orig");
               if (!orig.exists()) {
                  U.msg(data.getSender(), "&cНечего ресетить, все и так нормас");
                  return;
               }

               File current = new File("_install.sh");
               if (current.exists()) {
                  current.delete();
               }

               orig.renameTo(current);
               U.msg(data.getSender(), "&aКонфиг успешно восстановлен", "Теперь сервер надо перезагрузить");
         }

      }
   }

   @CmdSub(
      value = {"help"},
      rank = Rank.CHIEF,
      hidden = true
   )
   private void help(SubCommandData data) {
      List<String> cmds = new ArrayList();
      Rank rank = this.getRank(data.getSender());

      for(CommandRoot.PublicSub sub : this.getPublicSubs()) {
         if (sub.sub.isAvailableFor(rank, (CommandSender)null)) {
            cmds.add(sub.cmd);
         }
      }

      data.getSender().sendMessage(Joiner.on(", ").join(cmds));
   }
}
