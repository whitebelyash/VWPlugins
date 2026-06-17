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

import java.util.Random;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ApiCommand
implements CommandExecutor {
    private static final String TOKEN_DICT = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Command can only be used by player");
            return false;
        }
        NetworkPlayer player = VimeNetwork.getPlayer(sender.getName());
        if (player.getId() == -1) {
            U.msg(sender, "&c\u0418\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f \u043e \u0432\u0430\u0441 \u0435\u0449\u0435 \u043d\u0435 \u0437\u0430\u0433\u0440\u0443\u0437\u0438\u043b\u0430\u0441\u044c, \u043f\u043e\u0434\u043e\u0436\u0434\u0438\u0442\u0435 \u043d\u0435\u043c\u043d\u043e\u0433\u043e...");
            return false;
        }
        boolean reset = args.length > 0 && args[0].equals("new");
        VimeNetwork.mysql().select("SELECT * FROM api_tokens WHERE owner = " + player.getId(), rs -> {
            if (rs.next()) {
                if (reset) {
                    String token = ApiCommand.genNewToken();
                    VimeNetwork.mysql().query("UPDATE api_tokens SET token = '" + token + "' WHERE owner = " + player.getId());
                    U.msg(sender, "&a\u0412\u0430\u0448 \u043d\u043e\u0432\u044b\u0439 \u0442\u043e\u043a\u0435\u043d \u0430\u0432\u0442\u043e\u0440\u0438\u0437\u0430\u0446\u0438\u0438:&f http://api.vime.world/web/token/" + token + " &a(\u043f\u0435\u0440\u0435\u0439\u0434\u0438\u0442\u0435 \u043f\u043e \u0441\u0441\u044b\u043b\u043a\u0435)");
                } else {
                    String token = rs.getString("token");
                    U.msg(sender, "&a\u0412\u0430\u0448 \u0442\u0435\u043a\u0443\u0449\u0438\u0439 \u0442\u043e\u043a\u0435\u043d \u0430\u0432\u0442\u043e\u0440\u0438\u0437\u0430\u0446\u0438\u0438:&f http://api.vime.world/web/token/" + token + " &a(\u043f\u0435\u0440\u0435\u0439\u0434\u0438\u0442\u0435 \u043f\u043e \u0441\u0441\u044b\u043b\u043a\u0435)");
                    U.msg(sender, "&a\u0414\u043b\u044f \u0433\u0435\u043d\u0435\u0440\u0430\u0446\u0438\u0438 \u043d\u043e\u0432\u043e\u0433\u043e \u0442\u043e\u043a\u0435\u043d\u0430 \u043d\u0430\u043f\u0438\u0448\u0438\u0442\u0435 &f/api new");
                }
            } else {
                String token = ApiCommand.genNewToken();
                VimeNetwork.mysql().query("INSERT INTO api_tokens (token, owner) VALUES ('" + token + "', " + player.getId() + ")");
                U.msg(sender, "&a\u0414\u043b\u044f \u0432\u0430\u0441 \u0431\u044b\u043b \u0441\u0433\u0435\u043d\u0435\u0440\u0438\u0440\u043e\u0432\u0430\u043d \u043d\u043e\u0432\u044b\u0439 \u0442\u043e\u043a\u0435\u043d \u0430\u0432\u0442\u043e\u0440\u0438\u0437\u0430\u0446\u0438\u0438:&f http://api.vime.world/web/token/" + token + " &a(\u043f\u0435\u0440\u0435\u0439\u0434\u0438\u0442\u0435 \u043f\u043e \u0441\u0441\u044b\u043b\u043a\u0435)");
            }
        });
        return false;
    }

    private static String genNewToken() {
        Random rand = new Random();
        char[] str = new char[32];
        for (int i = 0; i < str.length; ++i) {
            str[i] = TOKEN_DICT.charAt(rand.nextInt(TOKEN_DICT.length()));
        }
        return new String(str);
    }
}

