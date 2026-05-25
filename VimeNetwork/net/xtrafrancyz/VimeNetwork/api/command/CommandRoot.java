package net.xtrafrancyz.VimeNetwork.api.command;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;

public abstract class CommandRoot implements CommandExecutor, TabCompleter {
   private Map subs = new HashMap();
   private List publicSubs = new ArrayList();

   public CommandRoot() {
      BiConsumer<String, RegisteredSub> addSub = (cmd, subx) -> {
         if (this.subs.put(cmd, subx) != null) {
            throw new IllegalArgumentException("[" + this.getClass().getSimpleName() + "] Sub " + cmd + " is already registered for " + subx.method.getName());
         }
      };
      Class clazz = this.getClass();

      do {
         for(Method method : clazz.getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.isAnnotationPresent(CmdSub.class)) {
               RegisteredSub sub = new RegisteredSub(method);
               CmdSub asub = (CmdSub)method.getAnnotation(CmdSub.class);
               sub.hidden = asub.hidden();

               for(String name : asub.value()) {
                  if (!sub.hidden) {
                     this.publicSubs.add(new PublicSub(name.toLowerCase(), sub));
                  }

                  addSub.accept(name.toLowerCase(), sub);
               }

               for(String alias : asub.aliases()) {
                  addSub.accept(alias.toLowerCase(), sub);
               }

               if (asub.ranks().length == 0) {
                  sub.rankExact = false;
                  sub.ranks = new Rank[]{asub.rank()};
               } else {
                  sub.rankExact = true;
                  sub.ranks = asub.ranks();
               }
            }
         }

         clazz = clazz.getSuperclass();
      } while(clazz != CommandRoot.class);

      Collections.sort(this.publicSubs);
   }

   protected abstract boolean main(CommandSender var1, Command var2, String var3, String[] var4);

   protected void runCommand(Runnable action, CommandSender sender, Command cmd, String label, String[] args) {
      action.run();
   }

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (args.length > 0) {
         RegisteredSub sub = (RegisteredSub)this.subs.get(args[0].toLowerCase());
         if (sub != null) {
            if (!sub.isAvailableFor(this.getRank(sender), sender)) {
               return true;
            }

            String[] a = new String[args.length - 1];
            if (a.length != 0) {
               System.arraycopy(args, 1, a, 0, a.length);
            }

            this.runCommand(() -> {
               try {
                  sub.method.invoke(this, new SubCommandData(sender, label, args[0].toLowerCase(), a));
               } catch (Exception e) {
                  e.printStackTrace();
               }

            }, sender, cmd, label, args);
            return true;
         }
      }

      this.runCommand(() -> this.main(sender, cmd, label, args), sender, cmd, label, args);
      return true;
   }

   public List onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
      if (!this.publicSubs.isEmpty() && args.length == 1) {
         Rank rank = this.getRank(sender);
         List<String> list = new ArrayList(this.publicSubs.size());
         String begin = args[0].toLowerCase();

         for(PublicSub sub : this.publicSubs) {
            if (sub.cmd.startsWith(begin) && sub.sub.isAvailableFor(rank, (CommandSender)null)) {
               list.add(sub.cmd);
            }
         }

         return list;
      } else {
         return null;
      }
   }

   protected Rank getRank(CommandSender sender) {
      return sender instanceof ConsoleCommandSender ? Rank.ADMIN : VimeNetwork.getPlayer(sender.getName()).getRank();
   }

   protected List getPublicSubs() {
      return this.publicSubs;
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
            for(Rank rank1 : this.ranks) {
               if (rank1 == rank) {
                  return true;
               }
            }

            if (inform != null) {
               U.msg(inform, T.error("VimeWorld", "Для этого действия необходим статус " + this.ranks[0].getDisplayName()));
            }

            return false;
         } else {
            return rank.has(inform, this.ranks[0]);
         }
      }
   }

   protected static class PublicSub implements Comparable {
      public String cmd;
      public RegisteredSub sub;

      public PublicSub(String cmd, RegisteredSub sub) {
         this.cmd = cmd;
         this.sub = sub;
      }

      public int compareTo(PublicSub o) {
         return this.cmd.compareToIgnoreCase(o.cmd);
      }
   }
}
