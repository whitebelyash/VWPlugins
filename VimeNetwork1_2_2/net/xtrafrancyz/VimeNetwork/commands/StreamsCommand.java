/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.commands;

import net.xtrafrancyz.VimeNetwork.VNPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StreamsCommand
implements CommandExecutor {
    private VNPlugin plugin;

    public StreamsCommand(VNPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.plugin.streamMenu.show((Player)sender);
        return true;
    }
}

