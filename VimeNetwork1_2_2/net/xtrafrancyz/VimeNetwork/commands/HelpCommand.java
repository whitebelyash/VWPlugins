/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.commands;

import java.util.ArrayList;
import java.util.List;
import net.xtrafrancyz.Commons.player.Permission;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpCommand
implements CommandExecutor {
    private ArrayList<Desc> commands = new ArrayList(20);

    public void addCommand(String command, String help) {
        this.addCommand(command, help, Rank.PLAYER);
    }

    public void addCommand(String command, String help, Rank rank) {
        for (Desc desc : this.commands) {
            if (!desc.command.equals(command)) continue;
            return;
        }
        this.commands.add(new RankDesc(command, help, rank));
    }

    public void addCommand(String command, String help, Permission permission) {
        for (Desc desc : this.commands) {
            if (!desc.command.equals(command)) continue;
            return;
        }
        this.commands.add(new PermissionDesc(command, help, permission));
    }

    public boolean onCommand(CommandSender sender, Command argcmd, String label, String[] args) {
        int index;
        if (!(sender instanceof Player)) {
            U.msg(sender, "&c\u041a\u043e\u043c\u0430\u043d\u0434\u0430 \u0440\u0430\u0437\u0440\u0435\u0448\u0435\u043d\u0430 \u0442\u043e\u043b\u044c\u043a\u043e \u0438\u0433\u0440\u043e\u043a\u0430\u043c");
            return true;
        }
        List<Desc> cmds = this.getAllowedCommands(VPlayer.get(sender.getName()));
        int page = 0;
        int pages = (cmds.size() - 1) / 9;
        if (args.length == 1) {
            try {
                page = Integer.parseInt(args[0]) - 1;
                if (page > pages) {
                    page = pages;
                }
                if (page < 0) {
                    page = 0;
                }
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        U.msg(sender, "&e---------- &fVimeWorld \u041f\u043e\u043c\u043e\u0449\u044c [&e" + (page + 1) + "&f/&e" + (pages + 1) + "&f]&e ---------------");
        for (int i = index = page * 9; i < cmds.size() && i < index + 9; ++i) {
            Desc cmd = cmds.get(i);
            sender.sendMessage(ChatColor.YELLOW + "/" + cmd.command + ChatColor.WHITE + ": " + cmd.help);
        }
        return true;
    }

    private List<Desc> getAllowedCommands(NetworkPlayer player) {
        ArrayList<Desc> cmds = new ArrayList<Desc>(this.commands.size());
        this.commands.stream().filter(cmd -> cmd.canUse(player)).forEachOrdered(cmds::add);
        return cmds;
    }

    private static class PermissionDesc
    extends Desc {
        Permission permission;

        PermissionDesc(String command, String help, Permission permission) {
            super(command, help);
            this.permission = permission;
        }

        @Override
        public boolean canUse(NetworkPlayer player) {
            return player.getRank().has(this.permission);
        }
    }

    private static class RankDesc
    extends Desc {
        Rank rank;

        RankDesc(String command, String help, Rank rank) {
            super(command, help);
            this.rank = rank;
        }

        @Override
        public boolean canUse(NetworkPlayer player) {
            return player.getRank().has(this.rank);
        }
    }

    private static abstract class Desc {
        String command;
        String help;

        Desc(String command, String help) {
            this.command = U.colored(command);
            this.help = U.colored(help);
        }

        public abstract boolean canUse(NetworkPlayer var1);

        public String toString() {
            return this.command;
        }
    }
}

