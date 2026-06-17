/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringEscapeUtils
 *  org.bukkit.Bukkit
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.commands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import net.xtrafrancyz.Commons.guild.GuildLeveling;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.Core.network.packet.Packet69Guild;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLeaveEvent;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.commands.GuildCommand;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class GuildAdminCommand
implements CommandExecutor,
Listener {
    private static final String SQL_SEARCH_REQUEST = "SELECT id, name, tag FROM guilds WHERE name LIKE '%{selector}%' OR tag LIKE '%{selector}%' ORDER BY CASE WHEN name = '{selector}' OR tag = '{selector}' THEN 0               WHEN name LIKE '{selector}%' OR tag LIKE '{selector}%' THEN 1               WHEN name LIKE '%{selector}%' OR tag LIKE '%{selector}%' THEN 2               WHEN name LIKE '%{selector}' OR tag LIKE '%{selector}' THEN 3               ELSE 4          END, name, tag ASC LIMIT 10";
    private static final String SQL_SEARCH_REQUEST_ID = "SELECT id, name, tag FROM guilds WHERE id = {selector} OR name LIKE '%{selector}%' OR tag LIKE '%{selector}%' ORDER BY CASE WHEN id = {selector} THEN 0               WHEN name = '{selector}' OR tag = '{selector}' THEN 1               WHEN name LIKE '{selector}%' OR tag LIKE '{selector}%' THEN 2               WHEN name LIKE '%{selector}%' OR tag LIKE '%{selector}%' THEN 3               WHEN name LIKE '%{selector}' OR tag LIKE '%{selector}' THEN 4               ELSE 5          END, name, tag ASC LIMIT 10";
    private static final String SQL_SELECT_MEMBERS = "SELECT COUNT(*) as members, (SELECT username FROM guild_members g, users u WHERE u.id = g.userid AND g.status = 0 AND guild = {id}) as leader FROM guild_members WHERE guild = {id}";
    private Map<String, List<Integer>> results = new HashMap<String, List<Integer>>();
    private Map<String, String[]> prevCmds = new HashMap<String, String[]>();

    public GuildAdminCommand() {
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)VNPlugin.instance());
    }

    @EventHandler
    private void onPlayerLeave(PlayerLeaveEvent event) {
        this.results.remove(event.getPlayer().getName());
        this.prevCmds.remove(event.getPlayer().getName());
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!VimeNetwork.hasRank(sender, Rank.CHIEF, true)) {
            return false;
        }
        if (args.length == 0) {
            U.msg(sender, "&e/ga &7<guild>&f: \u0438\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f \u043e \u0433\u0438\u043b\u044c\u0434\u0438\u0438", "&e/ga &7<guild> name <\u043d\u043e\u0432\u043e\u0435 \u0438\u043c\u044f>&f: \u0438\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u0435 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u044f \u0433\u0438\u043b\u044c\u0434\u0438\u0438", "&e/ga &7<guild> tag <\u043d\u043e\u0432\u044b\u0439 \u0442\u0435\u0433>&f: \u0438\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u0435 \u0442\u0435\u0433\u0430 \u0433\u0438\u043b\u044c\u0434\u0438\u0438", "&e/ga &7<guild> addexp <\u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u043e\u043f\u044b\u0442\u0430>&f: \u0432\u044b\u0434\u0430\u0442\u044c \u0433\u0438\u043b\u044c\u0434\u0438\u0438 \u043e\u043f\u044b\u0442\u0430");
            return false;
        }
        if (args[0].equalsIgnoreCase("c") && args.length > 1) {
            List<Integer> matches = this.results.remove(sender.getName());
            if (matches == null || !this.prevCmds.containsKey(sender.getName())) {
                U.msg(sender, "\u041d\u0438\u0447\u0435\u0433\u043e \u043d\u0435\u0442");
            } else {
                int guildId = matches.get(Integer.parseInt(args[1]) - 1);
                args = this.prevCmds.remove(sender.getName());
                this.processCommand(sender, guildId, args);
            }
            return false;
        }
        this.results.remove(sender.getName());
        this.prevCmds.put(sender.getName(), args);
        this.selectGuild(sender, args[0]);
        return false;
    }

    private void selectGuild(CommandSender sender, String selector) {
        boolean isNumber;
        try {
            Integer.parseInt(selector);
            isNumber = true;
        }
        catch (NumberFormatException ex) {
            isNumber = false;
        }
        String query = isNumber ? SQL_SEARCH_REQUEST_ID : SQL_SEARCH_REQUEST;
        selector = StringEscapeUtils.escapeSql((String)selector);
        query = query.replace("{selector}", selector);
        VimeNetwork.mysql().select(query, rs -> {
            ArrayList<Integer> matches = new ArrayList<Integer>();
            int i = 1;
            while (rs.next()) {
                U.msg(sender, i++ + ". &7id=&e" + rs.getInt("id") + "&7, name=&e" + rs.getString("name") + "&7, tag=&e" + rs.getString("tag"));
                matches.add(rs.getInt("id"));
            }
            if (matches.isEmpty()) {
                U.msg(sender, "&c\u041f\u043e \u0432\u0430\u0448\u0435\u043c\u0443 \u0437\u0430\u043f\u0440\u043e\u0441\u0443 \u043d\u0438\u0447\u0435\u0433\u043e \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u043e");
                this.results.remove(sender.getName());
            } else {
                U.msg(sender, "&a\u0414\u043b\u044f \u043f\u0440\u043e\u0434\u043e\u043b\u0436\u0435\u043d\u0438\u044f \u043a\u043e\u043c\u0430\u043d\u0434\u044b \u043d\u0430\u043f\u0438\u0448\u0438\u0442\u0435&f /ga c <\u043d\u043e\u043c\u0435\u0440>");
                this.results.put(sender.getName(), matches);
            }
        });
    }

    private void processCommand(CommandSender sender, int guildId, String[] args) {
        if (args.length == 1) {
            args = new String[]{null, "info"};
        }
        switch (args[1].toLowerCase()) {
            case "name": {
                if (args.length < 3) {
                    U.msg(sender, "&e/ga &7<guild> name <\u043d\u043e\u0432\u043e\u0435 \u0438\u043c\u044f>&f: \u0438\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u0435 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u044f \u0433\u0438\u043b\u044c\u0434\u0438\u0438");
                    return;
                }
                String newName = GuildCommand.join(2, args).replace("'", "''");
                VimeNetwork.mysql().update("UPDATE guilds SET name = '" + newName + "' WHERE id = " + guildId, unused -> {
                    this.reloadGuild(sender, guildId);
                    U.msg(sender, "&a\u0412\u044b \u043f\u043e\u043c\u0435\u043d\u044f\u043b\u0438 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u0433\u0438\u043b\u044c\u0434\u0438\u0438");
                });
                break;
            }
            case "tag": {
                if (args.length < 3) {
                    U.msg(sender, "&e/ga &7<guild> tag <\u043d\u043e\u0432\u044b\u0439 \u0442\u0435\u0433>&f: \u0438\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u0435 \u0442\u0435\u0433\u0430 \u0433\u0438\u043b\u044c\u0434\u0438\u0438");
                    return;
                }
                String newTag = GuildCommand.join(2, args).replace("'", "''");
                VimeNetwork.mysql().update("UPDATE guilds SET tag = '" + newTag + "' WHERE id = " + guildId, unused -> {
                    this.reloadGuild(sender, guildId);
                    U.msg(sender, "&a\u0412\u044b \u043f\u043e\u043c\u0435\u043d\u044f\u043b\u0438 \u0442\u0435\u0433 \u0433\u0438\u043b\u044c\u0434\u0438\u0438");
                });
                break;
            }
            case "addexp": {
                int exp;
                if (args.length < 3) {
                    U.msg(sender, "&e/ga &7<guild> addexp <\u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u043e\u043f\u044b\u0442\u0430>&f: \u0432\u044b\u0434\u0430\u0442\u044c \u0433\u0438\u043b\u044c\u0434\u0438\u0438 \u043e\u043f\u044b\u0442\u0430");
                    return;
                }
                try {
                    exp = Integer.parseInt(args[2]);
                }
                catch (NumberFormatException ex) {
                    U.msg(sender, "&c\u0427\u0438\u0441\u043b\u043e \u0432\u0432\u043e\u0434\u0438");
                    return;
                }
                U.msg(sender, "&a\u0413\u0438\u043b\u044c\u0434\u0438\u0438 \u0441 id " + guildId + " \u0434\u043e\u0431\u0430\u0432\u043b\u0435\u043d\u043e " + exp + " \u043e\u043f\u044b\u0442\u0430");
                this.sendPacket(sender, Packet69Guild.Action.ADM_ADD_EXP, packet -> {
                    packet.put("id", guildId);
                    packet.put("exp", exp);
                });
                break;
            }
            case "info": {
                VimeNetwork.mysql().select("SELECT * FROM guilds WHERE id = " + guildId, rs -> {
                    rs.next();
                    String color = rs.getString("color") == null ? "f" : rs.getString("color");
                    U.msg(sender, "&7\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435: &" + color + rs.getString("name") + "&7, \u0422\u0435\u0433: &" + color + rs.getString("tag"), "&7\u0414\u0430\u0442\u0430 \u0441\u043e\u0437\u0434\u0430\u043d\u0438\u044f: &f" + new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH).format(new Date((long)rs.getInt("created") * 1000L)), "&7\u041a\u043e\u0438\u043d\u043e\u0432: &e" + rs.getInt("coins") + "&7, \u0423\u0440\u043e\u0432\u0435\u043d\u044c: &9" + GuildLeveling.getLevel(rs.getInt("exp")) + " &7(\u043e\u0431\u043d\u043e\u0432\u043b\u044f\u0435\u0442\u0441\u044f \u0440\u0430\u0437 \u0432 \u0447\u0430\u0441)");
                    VimeNetwork.mysql().select(SQL_SELECT_MEMBERS.replace("{id}", Integer.toString(guildId)), rs1 -> {
                        rs1.next();
                        U.msg(sender, "&7\u0427\u043b\u0435\u043d\u043e\u0432: &a" + rs1.getInt("members") + "&7, \u041b\u0438\u0434\u0435\u0440: &a" + rs1.getString("leader"));
                    });
                });
            }
        }
    }

    private void reloadGuild(CommandSender sender, int id) {
        this.sendPacket(sender, Packet69Guild.Action.ADM_RELOAD, packet -> packet.put("id", id));
    }

    private void sendPacket(CommandSender sender, Packet69Guild.Action action, Consumer<Packet69Guild> writer) {
        Packet69Guild packet = new Packet69Guild(VimeNetwork.getPlayer(sender.getName()).getId(), action);
        writer.accept(packet);
        VimeNetwork.core().sendPacket(packet);
    }
}

