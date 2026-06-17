/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 */
package net.xtrafrancyz.VimeNetwork.commands;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.Commons.player.Permission;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PrefixCommand
implements CommandExecutor {
    private final Pattern pattern = Pattern.compile("^[a-zA-Z0-9_.-]{2,4}$");
    private final Set<String> blacklist = new HashSet<String>(Arrays.asList("ebl0", "hyem", "3blo", "p1zd", "ueba", "pizd", "eblo", "ebat", "blya", "pidr", "xuy", "ebal", "her", "h3r", "3bal", "3ba1", "eba1", "p1dr", "xy1", "b1ya", "blea", "61ya", "xyi", "xyev", "hyev", "huya", "huev", "xyem", "xy_i", "hyi_", "_hyi", "h_yi", "xyyi", "xyii", "xu_i", "xuui", "xyll", "mudk", "xxyi", "twar", "dick", "cock", "cunt", "quim", "t8ap", "1bap", "tbap", "uebk", "yoba", "y0ba", "eb0k", "ebu", "uebu", "yeba", "mraz", "hyil", "g0vn", "govn", "lycu", "lucy", "l1cy", "okss", "oksi", "adm", "modr", "oks1", "admi", "oksl", "xelp", "smai", "smak", "lyc1", "aksi", "admn", "aclm", "adm_", "anus", "anal", "a_dm", "pisy", "ad_m", "sosu", "s0su", "sosi", "sisi", "4len", "s1s1", "suka", "suki", "srat", "sratb", "sru", "ass", "qay", "gay", "g4y", "lsd", "lesb", "sh1t", "shit", "cum", "l3sb", "slut", "elda", "eldk", "e1da", "sprm", "dcp", "daun", "porn", "sex", "cekc", "ebis", "urod", "ueby", "yebu", "xu_y", "x_uy", "xu-y", "cyka", "syka", ".loh", ".lox", "lox.", "loh.", "hui.", "xui.", "xuy.", "huy.", ".xui", ".ass", ".huy", ".xuy", ".adm", "adm.", "-adm", "hui", "huy", "hyi", "04ko", "o4ko", "bdsm", "bdcm", "ceo", "cto", "mod", ".mod", "hlpr", "help", "cuka", "own", "hax", "fuck"));

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        NetworkPlayer player = VimeNetwork.getPlayer(sender.getName());
        if (!player.getRank().has(Permission.PREFIX)) {
            U.msg(sender, T.error("VimeWorld", "\u0418\u0437\u043c\u0435\u043d\u044f\u0442\u044c \u043f\u0440\u0435\u0444\u0438\u043a\u0441 \u043c\u043e\u0433\u0443\u0442 \u0442\u043e\u043b\u044c\u043a\u043e " + Rank.IMMORTAL.getDisplayName()));
            return false;
        }
        if (args.length == 0) {
            U.msg(sender, "&e=========== &f\u041f\u0440\u0435\u0444\u0438\u043a\u0441 &e===========", "&a\u0412\u044b \u043c\u043e\u0436\u0435\u0442\u0435 \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u0438\u0442\u044c \u0441\u0435\u0431\u0435 \u043f\u0440\u0435\u0444\u0438\u043a\u0441 \u0438\u0437 4-\u0445 \u0431\u0443\u043a\u0432.", "&f\u0417\u0430\u043f\u0440\u0435\u0449\u0435\u043d\u043e \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u043e\u0441\u043a\u043e\u0440\u0431\u0438\u0442\u0435\u043b\u044c\u043d\u044b\u0435 \u043f\u0440\u0435\u0444\u0438\u043a\u0441\u044b \u0438\u043b\u0438 \u043f\u0440\u0435\u0444\u0438\u043a\u0441\u044b, \u043a\u043e\u0442\u043e\u0440\u044b\u0435 \u0431\u0443\u0434\u0443\u0442 \u0432\u044b\u0434\u0430\u0432\u0430\u0442\u044c \u0432\u0430\u0441 \u0437\u0430 \u0430\u0434\u043c\u0438\u043d\u0438\u0441\u0442\u0440\u0430\u0446\u0438\u044e. &c\u0417\u0430 \u0442\u0430\u043a\u0438\u0435 \u043f\u0440\u0435\u0444\u0438\u043a\u0441\u044b \u0432\u044b \u043c\u043e\u0436\u0435\u0442\u0435 \u043f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u0431\u0430\u043d.", "&a/prefix <\u043f\u0440\u0435\u0444\u0438\u043a\u0441>&f - \u0423\u0441\u0442\u0430\u043d\u043e\u0432\u043a\u0430 \u043f\u0440\u0435\u0444\u0438\u043a\u0441\u0430", "&a/prefix reset&f - \u0423\u0434\u0430\u043b\u0435\u043d\u0438\u0435 \u043f\u0440\u0435\u0444\u0438\u043a\u0441\u0430");
            return false;
        }
        String prefix = args[0];
        if (prefix.equalsIgnoreCase("reset")) {
            this.setPrefix(player, null);
            U.msg(sender, "&a\u0412\u044b \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0443\u0434\u0430\u043b\u0438\u043b\u0438 \u0441\u0432\u043e\u0439 \u043f\u0440\u0435\u0444\u0438\u043a\u0441");
            return false;
        }
        if (prefix.length() < 2 || prefix.length() > 4) {
            U.msg(sender, "&c\u041f\u0440\u0435\u0444\u0438\u043a\u0441 \u043c\u043e\u0436\u0435\u0442 \u0431\u044b\u0442\u044c \u0434\u043b\u0438\u043d\u043e\u0439 \u043e\u0442 2 \u0434\u043e 4 \u0431\u0443\u043a\u0432");
            return false;
        }
        if (!this.pattern.matcher(prefix).matches()) {
            U.msg(sender, "&c\u041f\u0440\u0435\u0444\u0438\u043a\u0441 \u043c\u043e\u0436\u0435\u0442 \u0441\u043e\u0441\u0442\u043e\u044f\u0442\u044c \u0442\u043e\u043b\u044c\u043a\u043e \u0438\u0437 \u0430\u043d\u0433\u043b\u0438\u0439\u0441\u043a\u0438\u0445 \u0431\u0443\u043a\u0432, \u0446\u0438\u0444\u0440 \u0438 \u0437\u043d\u0430\u043a\u043e\u0432 '-' '_' '.'");
            return false;
        }
        if (this.blacklist.contains(prefix.toLowerCase())) {
            U.msg(sender, "&c\u042d\u0442\u043e\u0442 \u043f\u0440\u0435\u0444\u0438\u043a\u0441 \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u0441\u044f \u0432 \u0447\u0435\u0440\u043d\u043e\u043c \u0441\u043f\u0438\u0441\u043a\u0435");
            return false;
        }
        this.setPrefix(player, prefix);
        U.msg(sender, "&a\u0412\u044b \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0438\u0437\u043c\u0435\u043d\u0438\u043b\u0438 \u0441\u0432\u043e\u0439 \u043f\u0440\u0435\u0444\u0438\u043a\u0441 \u043d\u0430 " + prefix);
        return false;
    }

    private void setPrefix(NetworkPlayer player, String prefix) {
        player.setMeta("prefix", prefix);
        player.getBukkitPlayer().setDisplayName(player.getPrefixedName());
    }
}

