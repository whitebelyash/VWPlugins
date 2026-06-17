/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.GameMode
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.commands;

import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.Commons.player.Permission;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.ServerType;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (VimeNetwork.lobby().getServerType() == ServerType.BUILD ? !VimeNetwork.hasPermission(sender, Permission.BUILDER, true) : !VimeNetwork.hasRank(sender, Rank.CHIEF, true)) {
            return true;
        }
        Player player = (Player)sender;
        switch (cmd.getName()) {
            case "gamemode": {
                if (args.length == 0) {
                    U.msg((CommandSender)player, "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /" + label + " <\u0440\u0435\u0436\u0438\u043c>");
                    break;
                }
                switch (args[0].toLowerCase()) {
                    case "0": 
                    case "s": 
                    case "survival": {
                        this.changeGamemode(player, GameMode.SURVIVAL);
                        break;
                    }
                    case "1": 
                    case "c": 
                    case "creative": {
                        this.changeGamemode(player, GameMode.CREATIVE);
                        break;
                    }
                    case "2": 
                    case "a": 
                    case "adventure": {
                        this.changeGamemode(player, GameMode.ADVENTURE);
                    }
                }
                break;
            }
            case "gms": {
                this.changeGamemode(player, GameMode.SURVIVAL);
                break;
            }
            case "gmc": {
                this.changeGamemode(player, GameMode.CREATIVE);
                break;
            }
            case "gma": {
                this.changeGamemode(player, GameMode.ADVENTURE);
            }
        }
        return true;
    }

    private void changeGamemode(Player player, GameMode mode) {
        if (player.getGameMode() == mode) {
            U.msg((CommandSender)player, T.warning("VimeWorld", "\u0418\u0433\u0440\u043e\u0432\u043e\u0439 \u0440\u0435\u0436\u0438\u043c &e" + mode.name() + " &6\u0443\u0436\u0435 \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d"));
            return;
        }
        player.setGameMode(mode);
        U.msg((CommandSender)player, T.success("VimeWorld", "\u0418\u0433\u0440\u043e\u0432\u043e\u0439 \u0440\u0435\u0436\u0438\u043c \u0438\u0437\u043c\u0435\u043d\u0451\u043d \u043d\u0430 &e" + mode.name()));
    }
}

