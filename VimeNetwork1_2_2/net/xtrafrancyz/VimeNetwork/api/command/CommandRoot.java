/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.command.TabCompleter
 */
package net.xtrafrancyz.VimeNetwork.api.command;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.command.CmdSub;
import net.xtrafrancyz.VimeNetwork.api.command.SubCommandData;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;

public abstract class CommandRoot
implements CommandExecutor,
TabCompleter {
    private Map<String, RegisteredSub> subs = new HashMap<String, RegisteredSub>();
    private List<PublicSub> publicSubs = new ArrayList<PublicSub>();

    public CommandRoot() {
        BiConsumer<String, RegisteredSub> addSub = (cmd, sub) -> {
            if (this.subs.put((String)cmd, (RegisteredSub)sub) != null) {
                throw new IllegalArgumentException("[" + this.getClass().getSimpleName() + "] Sub " + cmd + " is already registered for " + sub.method.getName());
            }
        };
        Class<?> clazz = this.getClass();
        do {
            for (Method method : clazz.getDeclaredMethods()) {
                method.setAccessible(true);
                if (!method.isAnnotationPresent(CmdSub.class)) continue;
                RegisteredSub sub2 = new RegisteredSub(method);
                CmdSub asub = method.getAnnotation(CmdSub.class);
                sub2.hidden = asub.hidden();
                for (String name : asub.value()) {
                    if (!sub2.hidden) {
                        this.publicSubs.add(new PublicSub(name.toLowerCase(), sub2));
                    }
                    addSub.accept(name.toLowerCase(), sub2);
                }
                for (String alias : asub.aliases()) {
                    addSub.accept(alias.toLowerCase(), sub2);
                }
                if (asub.ranks().length == 0) {
                    sub2.rankExact = false;
                    sub2.ranks = new Rank[]{asub.rank()};
                    continue;
                }
                sub2.rankExact = true;
                sub2.ranks = asub.ranks();
            }
        } while ((clazz = clazz.getSuperclass()) != CommandRoot.class);
        Collections.sort(this.publicSubs);
    }

    protected abstract boolean main(CommandSender var1, Command var2, String var3, String[] var4);

    protected void runCommand(Runnable action, CommandSender sender, Command cmd, String label, String[] args) {
        action.run();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        RegisteredSub sub;
        if (args.length > 0 && (sub = this.subs.get(args[0].toLowerCase())) != null) {
            if (!sub.isAvailableFor(this.getRank(sender), sender)) {
                return true;
            }
            String[] a = new String[args.length - 1];
            if (a.length != 0) {
                System.arraycopy(args, 1, a, 0, a.length);
            }
            this.runCommand(() -> {
                try {
                    sub.method.invoke((Object)this, new SubCommandData(sender, label, args[0].toLowerCase(), a));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }, sender, cmd, label, args);
            return true;
        }
        this.runCommand(() -> this.main(sender, cmd, label, args), sender, cmd, label, args);
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!this.publicSubs.isEmpty() && args.length == 1) {
            Rank rank = this.getRank(sender);
            ArrayList<String> list = new ArrayList<String>(this.publicSubs.size());
            String begin = args[0].toLowerCase();
            for (PublicSub sub : this.publicSubs) {
                if (!sub.cmd.startsWith(begin) || !sub.sub.isAvailableFor(rank, null)) continue;
                list.add(sub.cmd);
            }
            return list;
        }
        return null;
    }

    protected Rank getRank(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return Rank.ADMIN;
        }
        return VimeNetwork.getPlayer(sender.getName()).getRank();
    }

    protected List<PublicSub> getPublicSubs() {
        return this.publicSubs;
    }

    protected static class PublicSub
    implements Comparable<PublicSub> {
        public String cmd;
        public RegisteredSub sub;

        public PublicSub(String cmd, RegisteredSub sub) {
            this.cmd = cmd;
            this.sub = sub;
        }

        @Override
        public int compareTo(PublicSub o) {
            return this.cmd.compareToIgnoreCase(o.cmd);
        }
    }

    protected static class RegisteredSub {
        Method method;
        Rank[] ranks = new Rank[0];
        boolean rankExact = false;
        boolean hidden = false;

        public RegisteredSub(Method method) {
            this.method = method;
        }

        public boolean isAvailableFor(Rank rank, CommandSender inform) {
            if (this.rankExact) {
                for (Rank rank1 : this.ranks) {
                    if (rank1 != rank) continue;
                    return true;
                }
                if (inform != null) {
                    U.msg(inform, T.error("VimeWorld", "\u0414\u043b\u044f \u044d\u0442\u043e\u0433\u043e \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u044f \u043d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c \u0441\u0442\u0430\u0442\u0443\u0441 " + this.ranks[0].getDisplayName()));
                }
                return false;
            }
            if (rank.has(this.ranks[0])) {
                return true;
            }
            if (inform != null) {
                U.msg(inform, T.error("VimeWorld", "\u0414\u043b\u044f \u044d\u0442\u043e\u0433\u043e \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u044f \u043d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c \u0441\u0442\u0430\u0442\u0443\u0441 " + this.ranks[0].getDisplayName()));
            }
            return false;
        }
    }
}

