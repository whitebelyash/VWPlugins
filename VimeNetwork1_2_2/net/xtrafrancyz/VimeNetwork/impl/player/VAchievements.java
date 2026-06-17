/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  gnu.trove.map.hash.TIntObjectHashMap
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandSender
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.impl.player;

import gnu.trove.map.hash.TIntObjectHashMap;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.Core.network.packet.Packet1PlayerInfo;
import net.xtrafrancyz.Core.network.packet.Packet8PlayerGetAchievement;
import net.xtrafrancyz.VimeNetwork.Debug;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.VTexteria;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievements;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.CompletedAchievement;
import net.xtrafrancyz.VimeNetwork.api.util.Fireworks;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class VAchievements
implements Achievements {
    private final VPlayer player;
    private final TIntObjectHashMap<CompletedAchievement> completed;

    public VAchievements(VPlayer player) {
        this.player = player;
        this.completed = new TIntObjectHashMap();
    }

    public void load(Packet1PlayerInfo packet) {
        for (int[] a : packet.achievements) {
            Achievement achievement = Achievement.byId(a[0]);
            if (achievement == null) {
                VNPlugin.instance().getLogger().warning("Achievement ID " + a[0] + " not exists. Please, fix it!");
                continue;
            }
            this.completed.put(achievement.getId(), (Object)new CompletedAchievement(achievement, a[1]));
        }
    }

    @Override
    public boolean isCompleted(Achievement achievement) {
        return this.completed.contains(achievement.getId());
    }

    @Override
    public CompletedAchievement getCompletedAchievement(Achievement achievement) {
        return (CompletedAchievement)this.completed.get(achievement.getId());
    }

    @Override
    public boolean complete(Achievement achievement) {
        if (Debug.ACHIEVEMENTS.isEnabled()) {
            this.player.player.sendMessage("&5[DEBUG]&f \u0421\u0440\u0430\u0431\u043e\u0442\u0430\u043b\u043e \u0434\u043e\u0441\u0442\u0438\u0436\u0435\u043d\u0438\u0435: " + achievement.getName());
        }
        if (this.player.loaded && !this.isCompleted(achievement)) {
            U.msg((CommandSender)this.player.player, T.system("VimeWorld", "&7\u0412\u044b \u043f\u043e\u043b\u0443\u0447\u0438\u043b\u0438 \u043d\u043e\u0432\u043e\u0435 \u0434\u043e\u0441\u0442\u0438\u0436\u0435\u043d\u0438\u0435: &f" + achievement.getName()));
            for (String line : achievement.getDescription()) {
                U.msg((CommandSender)this.player.player, T.system("VimeWorld", line));
            }
            U.msg((CommandSender)this.player.player, T.system("VimeWorld", "&7\u041d\u0430\u0433\u0440\u0430\u0434\u0430: &e" + achievement.getReward()));
            VTexteria.showAchievementMessage(achievement, this.player.player);
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)VNPlugin.instance(), () -> Fireworks.playRandom(this.player.player.getLocation()));
            this.completed.put(achievement.getId(), (Object)new CompletedAchievement(achievement, (int)(System.currentTimeMillis() / 1000L)));
            this.player.addCoinsExact(achievement.getReward());
            VimeNetwork.core().sendPacket(new Packet8PlayerGetAchievement(this.player.id, achievement.getId()));
            return true;
        }
        return false;
    }

    @Override
    public int getCompletedCount() {
        return this.completed.size();
    }
}

