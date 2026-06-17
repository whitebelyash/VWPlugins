/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.conf.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class VConfig {
    private final VNPlugin plugin;
    public String mysqlUrl;
    public String mysqlUsername;
    public String mysqlPassword;
    public boolean bungeeEnable;
    public Set<String> bungeeIps;
    public boolean lobbyEnabled;
    public String lobbyServerId;
    public String lobbyServerHost;
    public boolean coreEnabled;
    public String coreHost;
    public int corePort;
    public boolean updaterEnabled;
    public boolean dev;
    public boolean tournament;

    public VConfig(VNPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        this.loadConfig();
    }

    private void loadConfig() {
        FileConfiguration config = this.plugin.getConfig();
        this.mysqlUrl = "jdbc:mysql://" + config.getString("mysql.host", "localhost") + ":" + config.getString("mysql.port", "3306") + "/" + config.getString("mysql.database", "minecraft");
        this.mysqlUsername = config.getString("mysql.username", "root");
        this.mysqlPassword = config.getString("mysql.password", "");
        this.coreEnabled = config.getBoolean("core.enabled", false);
        this.coreHost = config.getString("core.host", "localhost");
        this.corePort = config.getInt("core.port", 25431);
        this.lobbyEnabled = config.getBoolean("lobby.enabled", false);
        this.lobbyServerHost = config.getString("lobby.serverHost", "localhost");
        this.lobbyServerId = config.getString("lobby.serverId");
        this.updaterEnabled = config.getBoolean("updater.enabled", true);
        this.dev = config.getBoolean("dev", false);
        this.tournament = config.getBoolean("tournament", false);
        this.loadDynamic();
    }

    public void loadDynamic() {
        Configuration config = new Configuration((Plugin)this.plugin, "dynamic.yml");
        this.bungeeEnable = config.getBoolean("bungee.enabled", true);
        this.bungeeIps = Collections.unmodifiableSet(new HashSet<String>(config.getStringList("bungee.ips")));
    }
}

