/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.craftbukkit.v1_6_R3.CraftServer
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.plugin.messaging.PluginMessageListener
 */
package net.xtrafrancyz.VimeNetwork;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import net.xtrafrancyz.Commons.player.Permission;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.VimeNetwork.Events;
import net.xtrafrancyz.VimeNetwork.MysqlWorker;
import net.xtrafrancyz.VimeNetwork.TagManager;
import net.xtrafrancyz.VimeNetwork.VConfig;
import net.xtrafrancyz.VimeNetwork.VTexteria;
import net.xtrafrancyz.VimeNetwork.api.Lobby;
import net.xtrafrancyz.VimeNetwork.api.Material2;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.event.FileUpdateEvent;
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
import net.xtrafrancyz.VimeNetwork.commands.GuildAdminCommand;
import net.xtrafrancyz.VimeNetwork.commands.GuildCommand;
import net.xtrafrancyz.VimeNetwork.commands.HelpCommand;
import net.xtrafrancyz.VimeNetwork.commands.IgnoreCommand;
import net.xtrafrancyz.VimeNetwork.commands.KickCommand;
import net.xtrafrancyz.VimeNetwork.commands.ListCommand;
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
import net.xtrafrancyz.VimeNetwork.impl.player.guild.VGuild;
import net.xtrafrancyz.VimeNetwork.listeners.ArrowTrailListener;
import net.xtrafrancyz.VimeNetwork.listeners.CancelDropItemFix;
import net.xtrafrancyz.VimeNetwork.listeners.DeathListener;
import net.xtrafrancyz.VimeNetwork.listeners.InventoryListener;
import net.xtrafrancyz.VimeNetwork.listeners.PerWorldTablist;
import net.xtrafrancyz.VimeNetwork.listeners.ServiceItems;
import net.xtrafrancyz.VimeNetwork.listeners.WorldProxyListener;
import net.xtrafrancyz.VimeNetwork.packet.BungeeBridge;
import net.xtrafrancyz.VimeNetwork.packet.PacketInjector;
import net.xtrafrancyz.VimeNetwork.packet.SpigotPacketInjector;
import net.xtrafrancyz.VimeNetwork.tasks.GoalInformer;
import net.xtrafrancyz.VimeNetwork.tasks.GoalsCleaner;
import net.xtrafrancyz.VimeNetwork.tasks.MemoryFix;
import net.xtrafrancyz.VimeNetwork.tasks.PlayerMetaSaver;
import net.xtrafrancyz.VimeNetwork.tasks.PotionEffectWatcher;
import net.xtrafrancyz.VimeNetwork.tasks.Restart;
import net.xtrafrancyz.VimeNetwork.tasks.SpaceAchievementActivator;
import net.xtrafrancyz.VimeNetwork.tasks.Spammer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class VNPlugin
extends JavaPlugin
implements Listener {
    static VNPlugin instance;
    public ScheduledExecutorService scheduledExecutor;
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
            Reflect.set(cEntity, "entityCount", (Object)1);
        }
        String vmname = ManagementFactory.getRuntimeMXBean().getName();
        String pid = vmname.split("@")[0];
        File pidfile = new File("pid");
        if (pidfile.exists()) {
            pidfile.delete();
        }
        try {
            Files.write(pidfile.toPath(), pid.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE_NEW);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(pidfile::delete, "VimeNetwork pid deleter"));
    }

    public void onEnable() {
        this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        this.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new CancelDropItemFix(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new InventoryListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new DeathListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new Events(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new ServiceItems(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new ArrowTrailListener(), (Plugin)this);
        this.packets = new SpigotPacketInjector();
        this.getServer().getPluginManager().registerEvents((Listener)this.packets, (Plugin)this);
        this.tags = new TagManager(this);
        this.getServer().getPluginManager().registerEvents((Listener)this.tags, (Plugin)this);
        this.holograms = new VHolograms();
        this.getServer().getPluginManager().registerEvents((Listener)this.holograms, (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new WorldProxyListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PerWorldTablist(), (Plugin)this);
        this.config = new VConfig(this);
        this.metrics = new VMetrics(this);
        this.mysql = new MysqlWorker(this);
        this.coins = new VCoins(this);
        this.expBuffer = new VExpBuffer(this);
        this.lobby = this.config.coreEnabled ? new CoreLobby(this) : new MysqlLobby(this);
        this.help = new HelpCommand();
        this.core = new CoreBukkitImpl(this);
        this.streamMenu = new StreamMenu();
        this.npcs = new VNPCs();
        this.updateWatcher = new UpdateWatcher(this);
        this.mysql.start();
        VPlayer.CONSTRUCTOR = this.core.isEnabled() ? CorePlayer::new : MysqlPlayer::new;
        BungeeBridge bungeeBridge = new BungeeBridge();
        this.getServer().getMessenger().registerIncomingPluginChannel((Plugin)this, "VimeBungee", (PluginMessageListener)bungeeBridge);
        this.getServer().getMessenger().registerIncomingPluginChannel((Plugin)this, "Vime", (PluginMessageListener)bungeeBridge);
        this.getServer().getMessenger().registerOutgoingPluginChannel((Plugin)this, "Vime");
        this.getServer().getMessenger().registerOutgoingPluginChannel((Plugin)this, "VimeBungee");
        this.getServer().getMessenger().registerOutgoingPluginChannel((Plugin)this, "BungeeCord");
        this.getCommand("hub").setExecutor((sender, cmd, label, args) -> {
            VimeNetwork.toLobby((Player)sender);
            return true;
        });
        Object executor = this.core.isEnabled() ? new CoreMessageCommand() : new MessageCommand();
        this.getCommand("msg").setExecutor((CommandExecutor)executor);
        this.getCommand("r").setExecutor((CommandExecutor)executor);
        executor = this.core.isEnabled() ? new CoreIgnoreCommand() : new IgnoreCommand();
        this.getCommand("ignore").setExecutor((CommandExecutor)executor);
        this.getCommand("unignore").setExecutor((CommandExecutor)executor);
        executor = new BanCommand();
        this.getCommand("ban").setExecutor((CommandExecutor)executor);
        this.getCommand("unban").setExecutor((CommandExecutor)executor);
        executor = new MuteCommand();
        this.getCommand("mute").setExecutor((CommandExecutor)executor);
        this.getCommand("unmute").setExecutor((CommandExecutor)executor);
        executor = new GamemodeCommand();
        this.getCommand("gamemode").setExecutor((CommandExecutor)executor);
        this.getCommand("gms").setExecutor((CommandExecutor)executor);
        this.getCommand("gmc").setExecutor((CommandExecutor)executor);
        this.getCommand("gma").setExecutor((CommandExecutor)executor);
        this.getCommand("help").setExecutor((CommandExecutor)this.help);
        this.getCommand("api").setExecutor((CommandExecutor)new ApiCommand());
        this.getCommand("vimeworld").setExecutor((CommandExecutor)new VimeWorldCommand(this));
        this.getCommand("kick").setExecutor((CommandExecutor)new KickCommand());
        this.getCommand("worldspawn").setExecutor((CommandExecutor)new WorldSpawnCommand());
        this.getCommand("stream").setExecutor((CommandExecutor)new StreamCommand());
        this.getCommand("streams").setExecutor((CommandExecutor)new StreamsCommand(this));
        this.getCommand("party").setExecutor((CommandExecutor)new PartyCommand());
        this.getCommand("friend").setExecutor((CommandExecutor)new FriendCommand());
        this.getCommand("me").setExecutor((CommandExecutor)new MeCommand());
        this.getCommand("tp").setExecutor((CommandExecutor)new TpCommand());
        this.getCommand("stp").setExecutor((CommandExecutor)new StpCommand());
        this.getCommand("etp").setExecutor((CommandExecutor)new EtpCommand());
        this.getCommand("find").setExecutor((CommandExecutor)new FindCommand());
        this.vanishCommand = new VanishCommand();
        this.getCommand("vanish").setExecutor((CommandExecutor)this.vanishCommand);
        this.getCommand("speed").setExecutor((CommandExecutor)new SpeedCommand());
        this.getCommand("prefix").setExecutor((CommandExecutor)new PrefixCommand());
        this.getCommand("alert").setExecutor((CommandExecutor)new AlertCommand());
        this.getCommand("report").setExecutor((CommandExecutor)new ReportCommand());
        this.getCommand("reports").setExecutor((CommandExecutor)new ReportsCommand());
        this.getCommand("guild").setExecutor((CommandExecutor)new GuildCommand());
        this.getCommand("guildadm").setExecutor((CommandExecutor)new GuildAdminCommand());
        this.getCommand("list").setExecutor((CommandExecutor)new ListCommand());
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this, () -> {
            this.help.addCommand("kick&7 <\u0438\u0433\u0440\u043e\u043a> <\u043f\u0440\u0438\u0447\u0438\u043d\u0430>", "\u041a\u0438\u043a \u0438\u0433\u0440\u043e\u043a\u0430", Rank.MODER);
            this.help.addCommand("ban&7 <\u0438\u0433\u0440\u043e\u043a> [\u043c\u0438\u043d\u0443\u0442\u044b] <\u043f\u0440\u0438\u0447\u0438\u043d\u0430>", "\u0411\u0430\u043d \u0438\u0433\u0440\u043e\u043a\u0430", Permission.BAN);
            this.help.addCommand("unban&7 <\u0438\u0433\u0440\u043e\u043a>", "\u0421\u043d\u044f\u0442\u044c \u0431\u0430\u043d \u0441 \u0438\u0433\u0440\u043e\u043a\u0430", Permission.BAN);
            this.help.addCommand("mute&7 <\u0438\u0433\u0440\u043e\u043a> [\u043c\u0438\u043d\u0443\u0442\u044b] [\u043f\u0440\u0438\u0447\u0438\u043d\u0430]", "\u0412\u044b\u0434\u0430\u0442\u044c \u043c\u0443\u0442 \u0438\u0433\u0440\u043e\u043a\u0443", Permission.MUTE);
            this.help.addCommand("unmute&7 <\u0438\u0433\u0440\u043e\u043a>", "\u0421\u043d\u044f\u0442\u044c \u043c\u0443\u0442 \u0441 \u0438\u0433\u0440\u043e\u043a\u0430", Permission.MUTE);
            this.help.addCommand("&6vanish", "\u0420\u0435\u0436\u0438\u043c \u043d\u0430\u0431\u043b\u044e\u0434\u0430\u0442\u0435\u043b\u044f", Permission.VANISH);
            this.help.addCommand("&6tp&7 <\u0438\u0433\u0440\u043e\u043a>", "\u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044f \u043a \u0438\u0433\u0440\u043e\u043a\u0443", Permission.VANISH);
            this.help.addCommand("&6speed&7 <\u0441\u043a\u043e\u0440\u043e\u0441\u0442\u044c>", "\u0423\u0441\u0442\u0430\u043d\u0430\u0432\u043b\u0438\u0432\u0430\u0435\u0442 \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0445\u043e\u0434\u044c\u0431\u044b \u0438\u043b\u0438 \u043f\u043e\u043b\u0451\u0442\u0430", Permission.VANISH);
            this.help.addCommand("stp&7 <\u0438\u0433\u0440\u043e\u043a>", "\u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044f \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440 \u0438\u0433\u0440\u043e\u043a\u0430", Rank.WARDEN);
            this.help.addCommand("etp&7 <\u0438\u0433\u0440\u043e\u043a>", "\u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044f \u043a \u0438\u0433\u0440\u043e\u043a\u0443, \u043d\u0430 \u043a\u0430\u043a\u043e\u043c \u0441\u0435\u0440\u0432\u0435\u0440\u0435 \u0431\u044b \u043e\u043d \u043d\u0435 \u043d\u0430\u0445\u043e\u0434\u0438\u043b\u0441\u044f", Rank.WARDEN);
            this.help.addCommand("find&7 <\u0438\u0433\u0440\u043e\u043a>", "\u041d\u0430\u0439\u0442\u0438 \u0441\u0435\u0440\u0432\u0435\u0440 \u0438\u0433\u0440\u043e\u043a\u0430", Rank.WARDEN);
            this.help.addCommand("hub", "\u041f\u0435\u0440\u0435\u043c\u0435\u0449\u0430\u0435\u0442 \u0432\u0430\u0441 \u0432 \u043b\u043e\u0431\u0431\u0438");
            this.help.addCommand("me", "\u0418\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f \u043e \u0432\u0430\u0441");
            this.help.addCommand("msg&7 <\u0438\u0433\u0440\u043e\u043a> <\u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435>", "\u041e\u0442\u043f\u0440\u0430\u0432\u0438\u0442\u044c \u043f\u0440\u0438\u0432\u0430\u0442\u043d\u043e\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435");
            this.help.addCommand("r&7 <\u043e\u0442\u0432\u0435\u0442>", "\u041e\u0442\u0432\u0435\u0442 \u043f\u043e\u0441\u043b\u0435\u0434\u043d\u0435\u043c\u0443 \u043d\u0430\u043f\u0438\u0441\u0430\u0432\u0448\u0435\u043c\u0443 \u0412\u0430\u043c \u0438\u0433\u0440\u043e\u043a\u0443");
            this.help.addCommand("ignore&7 <\u0438\u0433\u0440\u043e\u043a>", "\u0417\u0430\u043f\u0440\u0435\u0442\u0438\u0442\u044c \u0438\u0433\u0440\u043e\u043a\u0443 \u043f\u0438\u0441\u0430\u0442\u044c \u0412\u0430\u043c \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f (@all - \u0432\u044b\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u043b\u0438\u0447\u043a\u0443)");
            this.help.addCommand("unignore&7 <\u0438\u0433\u0440\u043e\u043a>", "\u0421\u043d\u044f\u0442\u044c \u0437\u0430\u043f\u0440\u0435\u0442 (@all - \u0432\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u043b\u0438\u0447\u043a\u0443)");
            this.help.addCommand("party", "\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0433\u0440\u0443\u043f\u043f\u043e\u0439 (\u043f\u0430\u0442\u0438)");
            this.help.addCommand("guild", "\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0433\u0438\u043b\u044c\u0434\u0438\u0435\u0439");
            this.help.addCommand("friend", "\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0434\u0440\u0443\u0437\u044c\u044f\u043c\u0438");
            this.help.addCommand("streams", "\u041f\u0440\u043e\u0441\u043c\u043e\u0442\u0440\u0435\u0442\u044c \u0442\u0435\u043a\u0443\u0449\u0438\u0435 \u0441\u0442\u0440\u0438\u043c\u044b \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u0435");
            this.help.addCommand("prefix&7 <\u043f\u0440\u0435\u0444\u0438\u043a\u0441>", "\u0418\u0437\u043c\u0435\u043d\u0438\u0442\u044c \u0441\u0432\u043e\u0439 \u043f\u0440\u0435\u0444\u0438\u043a\u0441", Permission.PREFIX);
            this.help.addCommand("gm&7 <\u0440\u0435\u0436\u0438\u043c>", "\u0418\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u0435 \u0438\u0433\u0440\u043e\u0432\u043e\u0433\u043e \u0440\u0435\u0436\u0438\u043c\u0430", Rank.CHIEF);
            this.help.addCommand("vime", "\u0421\u043b\u0443\u0436\u0435\u0431\u043d\u044b\u0435 \u043a\u043e\u043c\u0430\u043d\u0434\u044b", Rank.CHIEF);
            this.help.addCommand("guildadm", "\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0447\u0443\u0436\u0438\u043c\u0438 \u0433\u0438\u043b\u044c\u0434\u0438\u044f\u043c\u0438", Rank.CHIEF);
            this.help.addCommand("worldspawn", "\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0441\u043f\u0430\u0432\u043d\u043e\u043c \u043c\u0438\u0440\u0430", Rank.ADMIN);
            this.help.addCommand("alert", "\u0421\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435 \u043d\u0430 \u0432\u0441\u0435 \u0441\u0435\u0440\u0432\u0435\u0440\u0430", Rank.ADMIN);
            if (VimeNetwork.isTournament()) {
                this.help.addCommand("list", "\u0421\u043f\u0438\u0441\u043e\u043a \u0438\u0433\u0440\u043e\u043a\u043e\u0432 \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u0435", Permission.ORGANIZER);
            } else {
                this.help.addCommand("list", "\u0421\u043f\u0438\u0441\u043e\u043a \u0438\u0433\u0440\u043e\u043a\u043e\u0432 \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u0435", Rank.CHIEF);
            }
            if (this.config.coreEnabled) {
                this.core.connect();
            }
        });
        this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)new PotionEffectWatcher(), 10L, 5L);
        this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)new SpaceAchievementActivator(), 30L, 30L);
        this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)new Spammer(), 0L, 1200L);
        this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)new MemoryFix(), 100L, 100L);
        this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)new GoalsCleaner(), 150L, 150L);
        this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)new GoalInformer(), 12000L, 12000L);
        this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)new VGuild.Cleaner(), 6000L, 6000L);
        if (!this.core.isEnabled()) {
            this.metaSaver = new PlayerMetaSaver(this);
            this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)this.metaSaver, 20L, 20L);
        }
        VTexteria.showServerId(Bukkit.getOnlinePlayers());
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this, () -> {
            if (!VimeNetwork.isDev() && VimeNetwork.features().AUTO_RESTART.isEnabled()) {
                Restart.schedule();
            }
        });
    }

    public void onDisable() {
        if (!((CraftServer)Bukkit.getServer()).getServer().isRunning()) {
            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(10000L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
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
        this.scheduledExecutor.shutdownNow();
    }

    @EventHandler
    private void onFileUpdated(FileUpdateEvent event) {
        if (event.isFile() && event.getCurrent().getPath().equals("plugins/VimeNetwork/dynamic.yml")) {
            this.getLogger().info("Config update found");
            try {
                ((WatchedFile)event.getCurrent()).copyUpdate();
                this.config.loadDynamic();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static VNPlugin instance() {
        return instance;
    }
}

