package net.xtrafrancyz.VimeNetwork.impl.player;

import gnu.trove.map.hash.TIntObjectHashMap;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.Core.network.packet.Packet1PlayerInfo;
import net.xtrafrancyz.Core.network.packet.Packet8PlayerGetAchievement;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.VTexteria;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievements;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.CompletedAchievement;
import net.xtrafrancyz.VimeNetwork.api.util.Fireworks;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;

public class VAchievements implements Achievements {
   private final VPlayer player;
   private final TIntObjectHashMap completed;

   public VAchievements(VPlayer player) {
      this.player = player;
      this.completed = new TIntObjectHashMap();
   }

   public void load(Packet1PlayerInfo packet) {
      for(int[] a : packet.achievements) {
         Achievement achievement = Achievement.byId(a[0]);
         if (achievement == null) {
            VNPlugin.instance().getLogger().warning("Achievement ID " + a[0] + " not exists. Please, fix it!");
         } else {
            this.completed.put(achievement.getId(), new CompletedAchievement(achievement, a[1]));
         }
      }

   }

   public boolean isCompleted(Achievement achievement) {
      return this.completed.contains(achievement.getId());
   }

   public CompletedAchievement getCompletedAchievement(Achievement achievement) {
      return (CompletedAchievement)this.completed.get(achievement.getId());
   }

   public boolean complete(Achievement achievement) {
      if (this.player.loaded && !this.isCompleted(achievement)) {
         U.msg(this.player.player, (String[])(T.system("VimeWorld", "&7Вы получили новое достижение: &f" + achievement.getName())));

         for(String line : achievement.getDescription()) {
            U.msg(this.player.player, (String[])(T.system("VimeWorld", line)));
         }

         U.msg(this.player.player, (String[])(T.system("VimeWorld", "&7Награда: &e" + achievement.getReward())));
         VTexteria.showAchievementMessage(achievement, this.player.player);
         Bukkit.getScheduler().scheduleSyncDelayedTask(VNPlugin.instance(), () -> Fireworks.playRandom(this.player.player.getLocation()));
         this.completed.put(achievement.getId(), new CompletedAchievement(achievement, (int)(System.currentTimeMillis() / 1000L)));
         this.player.addCoinsExact(achievement.getReward());
         VimeNetwork.core().sendPacket(new Packet8PlayerGetAchievement(this.player.id, achievement.getId()));
         return true;
      } else {
         return false;
      }
   }

   public int getCompletedCount() {
      return this.completed.size();
   }
}
