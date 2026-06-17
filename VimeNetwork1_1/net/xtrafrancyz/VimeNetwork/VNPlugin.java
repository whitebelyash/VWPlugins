package net.xtrafrancyz.VimeNetwork;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.ZoneId;
import net.xtrafrancyz.VimeNetwork.api.Lobby;
import net.xtrafrancyz.VimeNetwork.api.Material2;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.event.FileUpdateEvent;
import net.xtrafrancyz.VimeNetwork.api.player.Permission;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.updater.UpdateWatcher;
import net.xtrafrancyz.VimeNetwork.api.updater.WatchedFile;
import net.xtrafrancyz.VimeNetwork.api.util.Reflect;
import net.xtrafrancyz.VimeNetwork.commands.AlertCommand;
import net.xtrafrancyz.VimeNetwork.commands.ApiCommand;
import net.xtrafrancyz.VimeNetwork.commands.BanCommand;
import net.xtrafrancyz.VimeNetwork.commands.CoreIgnoreCommand;
import net.xtrafrancyz.VimeNetwork.commands.CoreMessageCommand;
import net.xtrafrancyz.VimeNetwork.commands.EtpCommand;
import net.xtrafrancyz.VimeNetwork.commands.FindCommand;
import net.xtrafrancyz.VimeNetwork.commands.FriendCommand;
import net.xtrafrancyz.VimeNetwork.commands.GamemodeCommand;
import net.xtrafrancyz.VimeNetwork.commands.HelpCommand;
import net.xtrafrancyz.VimeNetwork.commands.IgnoreCommand;
import net.xtrafrancyz.VimeNetwork.commands.KickCommand;
import net.xtrafrancyz.VimeNetwork.commands.MeCommand;
import net.xtrafrancyz.VimeNetwork.commands.MessageCommand;
import net.xtrafrancyz.VimeNetwork.commands.MuteCommand;
import net.xtrafrancyz.VimeNetwork.commands.PartyCommand;
import net.xtrafrancyz.VimeNetwork.commands.PrefixCommand;
import net.xtrafrancyz.VimeNetwork.commands.ReportCommand;
import net.xtrafrancyz.VimeNetwork.commands.ReportsCommand;
import net.xtrafrancyz.VimeNetwork.commands.SpeedCommand;
import net.xtrafrancyz.VimeNetwork.commands.StpCommand;
import net.xtrafrancyz.VimeNetwork.commands.StreamCommand;
import net.xtrafrancyz.VimeNetwork.commands.StreamsCommand;
import net.xtrafrancyz.VimeNetwork.commands.TpCommand;
import net.xtrafrancyz.VimeNetwork.commands.VanishCommand;
import net.xtrafrancyz.VimeNetwork.commands.VimeWorldCommand;
import net.xtrafrancyz.VimeNetwork.commands.WorldSpawnCommand;
import net.xtrafrancyz.VimeNetwork.core.CoreBukkitImpl;
import net.xtrafrancyz.VimeNetwork.impl.StreamMenu;
import net.xtrafrancyz.VimeNetwork.impl.VMetrics;
import net.xtrafrancyz.VimeNetwork.impl.holo.VHolograms;
import net.xtrafrancyz.VimeNetwork.impl.lobby.CoreLobby;
import net.xtrafrancyz.VimeNetwork.impl.lobby.MysqlLobby;
import net.xtrafrancyz.VimeNetwork.impl.npc.VNPCs;
import net.xtrafrancyz.VimeNetwork.impl.player.CorePlayer;
import net.xtrafrancyz.VimeNetwork.impl.player.MysqlPlayer;
import net.xtrafrancyz.VimeNetwork.impl.player.VCoins;
import net.xtrafrancyz.VimeNetwork.impl.player.VExpBuffer;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import net.xtrafrancyz.VimeNetwork.listeners.ArrowTrailListener;
import net.xtrafrancyz.VimeNetwork.listeners.CancelDropItemFix;
import net.xtrafrancyz.VimeNetwork.listeners.DeathListener;
import net.xtrafrancyz.VimeNetwork.listeners.InventoryListener;
import net.xtrafrancyz.VimeNetwork.listeners.ServiceItems;
import net.xtrafrancyz.VimeNetwork.listeners.TeleportFix;
import net.xtrafrancyz.VimeNetwork.packet.BungeeBridge;
import net.xtrafrancyz.VimeNetwork.packet.PacketInjector;
import net.xtrafrancyz.VimeNetwork.packet.SpigotPacketInjector;
import net.xtrafrancyz.VimeNetwork.tasks.GoalInformer;
import net.xtrafrancyz.VimeNetwork.tasks.GoalsCleaner;
import net.xtrafrancyz.VimeNetwork.tasks.MemoryFix;
import net.xtrafrancyz.VimeNetwork.tasks.PlayerMetaSaver;
import net.xtrafrancyz.VimeNetwork.tasks.PotionEffectWatcher;
import net.xtrafrancyz.VimeNetwork.tasks.SpaceAchievementActivator;
import net.xtrafrancyz.VimeNetwork.tasks.Spammer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class VNPlugin extends JavaPlugin implements Listener {
   static VNPlugin instance;
   public VConfig config;
   public MysqlWorker mysql;
   public HelpCommand help;
   public VCoins coins;
   public VExpBuffer expBuffer;
   public Lobby lobby;
   public TagManager tags;
   public PacketInjector packets;
   public CoreBukkitImpl core;
   public UpdateWatcher updateWatcher;
   public PlayerMetaSaver metaSaver;
   public VMetrics metrics;
   public VHolograms holograms;
   public StreamMenu streamMenu;
   public VanishCommand vanishCommand;
   public VNPCs npcs;

   public void onLoad() {
      instance = this;
      Material2.load();
      Class<?> cEntity = Reflect.findClass("net.minecraft.server.v1_6_R3.Entity");
      if ((Integer)Reflect.get(cEntity, "entityCount") == 0) {
         Reflect.set((Class)cEntity, "entityCount", 1);
      }

      String vmname = ManagementFactory.getRuntimeMXBean().getName();
      String pid = vmname.split("@")[0];
      File pidfile = new File("pid");
      if (pidfile.exists()) {
         pidfile.delete();
      }

      try {
         Files.write(pidfile.toPath(), pid.getBytes(StandardCharsets.UTF_8), new OpenOption[]{StandardOpenOption.CREATE_NEW});
      } catch (IOException e) {
         e.printStackTrace();
      }

      Runtime.getRuntime().addShutdownHook(new Thread(pidfile::delete, "VimeNetwork pid deleter"));
   }

   public void onEnable() {
      this.getServer().getPluginManager().registerEvents(this, this);
      this.getServer().getPluginManager().registerEvents(new CancelDropItemFix(), this);
      this.getServer().getPluginManager().registerEvents(new InventoryListener(), this);
      this.getServer().getPluginManager().registerEvents(new DeathListener(this), this);
      this.getServer().getPluginManager().registerEvents(new TeleportFix(this), this);
      this.getServer().getPluginManager().registerEvents(new Events(this), this);
      this.getServer().getPluginManager().registerEvents(new ServiceItems(), this);
      this.getServer().getPluginManager().registerEvents(new ArrowTrailListener(), this);
      this.getServer().getPluginManager().registerEvents(this.packets = new SpigotPacketInjector(), this);
      this.getServer().getPluginManager().registerEvents(this.tags = new TagManager(this), this);
      this.getServer().getPluginManager().registerEvents(this.holograms = new VHolograms(), this);
      this.config = new VConfig(this);
      this.metrics = new VMetrics(this);
      this.mysql = new MysqlWorker(this);
      this.coins = new VCoins(this);
      this.expBuffer = new VExpBuffer(this);
      this.lobby = (Lobby)(this.config.coreEnabled ? new CoreLobby(this) : new MysqlLobby(this));
      this.help = new HelpCommand();
      this.core = new CoreBukkitImpl(this);
      this.streamMenu = new StreamMenu();
      this.npcs = new VNPCs();
      this.updateWatcher = new UpdateWatcher(this);
      this.mysql.start();
      VPlayer.CONSTRUCTOR = this.core.isEnabled() ? CorePlayer::new : MysqlPlayer::new;
      BungeeBridge bungeeBridge = new BungeeBridge();
      this.getServer().getMessenger().registerIncomingPluginChannel(this, "VimeBungee", bungeeBridge);
      this.getServer().getMessenger().registerIncomingPluginChannel(this, "Vime", bungeeBridge);
      this.getServer().getMessenger().registerOutgoingPluginChannel(this, "Vime");
      this.getServer().getMessenger().registerOutgoingPluginChannel(this, "VimeBungee");
      this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
      this.getCommand("hub").setExecutor((sender, cmd, label, args) -> {
         VimeNetwork.toLobby((Player)sender);
         return true;
      });
      CommandExecutor executor;
      if (this.core.isEnabled()) {
         executor = new CoreMessageCommand();
      } else {
         executor = new MessageCommand();
      }

      this.getCommand("msg").setExecutor(executor);
      this.getCommand("r").setExecutor(executor);
      Object var6;
      if (this.core.isEnabled()) {
         var6 = new CoreIgnoreCommand();
      } else {
         var6 = new IgnoreCommand();
      }

      this.getCommand("ignore").setExecutor((CommandExecutor)var6);
      this.getCommand("unignore").setExecutor((CommandExecutor)var6);
      CommandExecutor var7 = new BanCommand();
      this.getCommand("ban").setExecutor(var7);
      this.getCommand("unban").setExecutor(var7);
      CommandExecutor var8 = new MuteCommand();
      this.getCommand("mute").setExecutor(var8);
      this.getCommand("unmute").setExecutor(var8);
      CommandExecutor var9 = new GamemodeCommand();
      this.getCommand("gamemode").setExecutor(var9);
      this.getCommand("gms").setExecutor(var9);
      this.getCommand("gmc").setExecutor(var9);
      this.getCommand("gma").setExecutor(var9);
      this.getCommand("help").setExecutor(this.help);
      this.getCommand("api").setExecutor(new ApiCommand());
      this.getCommand("vimeworld").setExecutor(new VimeWorldCommand(this));
      this.getCommand("kick").setExecutor(new KickCommand());
      this.getCommand("worldspawn").setExecutor(new WorldSpawnCommand());
      this.getCommand("stream").setExecutor(new StreamCommand());
      this.getCommand("streams").setExecutor(new StreamsCommand(this));
      this.getCommand("party").setExecutor(new PartyCommand());
      this.getCommand("friend").setExecutor(new FriendCommand());
      this.getCommand("me").setExecutor(new MeCommand());
      this.getCommand("tp").setExecutor(new TpCommand());
      this.getCommand("stp").setExecutor(new StpCommand());
      this.getCommand("etp").setExecutor(new EtpCommand());
      this.getCommand("find").setExecutor(new FindCommand());
      this.getCommand("vanish").setExecutor(this.vanishCommand = new VanishCommand());
      this.getCommand("speed").setExecutor(new SpeedCommand());
      this.getCommand("prefix").setExecutor(new PrefixCommand());
      this.getCommand("alert").setExecutor(new AlertCommand());
      this.getCommand("report").setExecutor(new ReportCommand());
      this.getCommand("reports").setExecutor(new ReportsCommand());
      Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
         this.help.addCommand("kick&7 <игрок> <причина>", "Кик игрока", Rank.MODER);
         this.help.addCommand("ban&7 <игрок> [минуты] <причина>", "Бан игрока", Permission.BAN);
         this.help.addCommand("unban&7 <игрок>", "Снять бан с игрока", Permission.BAN);
         this.help.addCommand("mute&7 <игрок> [минуты] [причина]", "Выдать мут игроку", Permission.MUTE);
         this.help.addCommand("unmute&7 <игрок>", "Снять мут с игрока", Permission.MUTE);
         this.help.addCommand("&6vanish", "Режим наблюдателя", Permission.VANISH);
         this.help.addCommand("&6tp&7 <игрок>", "Телепортация к игроку", Permission.VANISH);
         this.help.addCommand("&6speed&7 <скорость>", "Устанавливает скорость ходьбы или полёта", Permission.VANISH);
         this.help.addCommand("stp&7 <игрок>", "Телепортация на сервер игрока", Rank.WARDEN);
         this.help.addCommand("etp&7 <игрок>", "Телепортация к игроку, на каком сервере бы он не находился", Rank.WARDEN);
         this.help.addCommand("find&7 <игрок>", "Найти сервер игрока", Rank.WARDEN);
         this.help.addCommand("hub", "Перемещает вас в лобби");
         this.help.addCommand("me", "Информация о вас");
         this.help.addCommand("msg&7 <игрок> <сообщение>", "Отправить приватное сообщение");
         this.help.addCommand("r&7 <ответ>", "Ответ последнему написавшему Вам игроку");
         this.help.addCommand("ignore&7 <игрок>", "Запретить игроку писать Вам сообщения (@all - выключить личку)");
         this.help.addCommand("unignore&7 <игрок>", "Снять запрет (@all - включить личку)");
         this.help.addCommand("party", "Управление группой (пати)");
         this.help.addCommand("friend", "Управление друзьями");
         this.help.addCommand("streams", "Просмотреть текущие стримы на сервере");
         this.help.addCommand("prefix&7 <префикс>", "Изменить свой префикс", Permission.PREFIX);
         this.help.addCommand("gm&7 <режим>", "Изменение игрового режима", Rank.CHIEF);
         this.help.addCommand("vime", "Служебные команды", Rank.CHIEF);
         this.help.addCommand("worldspawn", "Управление спавном мира", Rank.ADMIN);
         this.help.addCommand("alert", "Сообщение на все сервера", Rank.ADMIN);
         if (this.config.coreEnabled) {
            this.core.connect();
         }

      });
      this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new PotionEffectWatcher(), 10L, 5L);
      this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new SpaceAchievementActivator(), 30L, 30L);
      this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Spammer(), 0L, 1200L);
      this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new MemoryFix(), 100L, 100L);
      this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new GoalsCleaner(), 150L, 150L);
      this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new GoalInformer(), 12000L, 12000L);
      if (!this.core.isEnabled()) {
         this.getServer().getScheduler().scheduleSyncRepeatingTask(this, this.metaSaver = new PlayerMetaSaver(this), 20L, 20L);
      }

      VTexteria.showServerId(Bukkit.getOnlinePlayers());
      LocalDate date = LocalDate.now(ZoneId.of("Europe/Moscow"));
      int month = date.getMonth().getValue();
      int day = date.getDayOfMonth();
      if (month == 12) {
         if (day > 15 && day < 31) {
            Spammer.addMessage("^tС наступающим Новым Годом!");
         } else if (day >= 31) {
            Spammer.addMessage("^tС Новым Годом!");
         }
      } else if (month == 1 && day < 3) {
         Spammer.addMessage("^tС Новым ^b" + date.getYear() + "^t Годом!");
      }

   }

   public void onDisable() {
      if (!((CraftServer)Bukkit.getServer()).getServer().isRunning()) {
         Thread t = new Thread(() -> {
            try {
               Thread.sleep(10000L);
            } catch (InterruptedException var1) {
            }

            System.exit(0);
         });
         t.setDaemon(true);
         t.setName("Shutdown watcher");
         t.start();
      }

      if (this.metaSaver != null) {
         this.metaSaver.finish();
      }

      this.coins.finish();
      this.expBuffer.finish();
      this.metrics.flush();
      this.mysql.finish();
      this.core.onDisable();
   }

   @EventHandler
   private void onFileUpdated(FileUpdateEvent event) {
      if (event.isFile() && event.getCurrent().getPath().equals("plugins/VimeNetwork/dynamic.yml")) {
         this.getLogger().info("Config update found");

         try {
            ((WatchedFile)event.getCurrent()).copyUpdate();
            this.config.loadDynamic();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

   }

   public static VNPlugin instance() {
      return instance;
   }
}
