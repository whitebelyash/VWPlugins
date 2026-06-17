/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.DamageSource
 *  net.minecraft.server.v1_6_R3.Entity
 *  net.xtrafrancyz.VimeNetwork.api.VimeNetwork
 *  net.xtrafrancyz.VimeNetwork.api.entity.NMSEntityUtils
 *  net.xtrafrancyz.VimeNetwork.api.geom.Vec3f
 *  net.xtrafrancyz.VimeNetwork.api.holo.TextHologram
 *  net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer
 *  net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement
 *  net.xtrafrancyz.VimeNetwork.api.util.Spectators
 *  net.xtrafrancyz.VimeNetwork.api.util.U
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.command.CommandSender
 *  org.bukkit.craftbukkit.v1_6_R3.entity.CraftEntity
 *  org.bukkit.entity.EnderCrystal
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.ClashPoint.object;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.server.v1_6_R3.DamageSource;
import net.xtrafrancyz.ClashPoint.Config;
import net.xtrafrancyz.ClashPoint.game.CPTexteria;
import net.xtrafrancyz.ClashPoint.game.entity.EntityResourcePoint;
import net.xtrafrancyz.ClashPoint.object.CPTeam;
import net.xtrafrancyz.ClashPoint.object.PlayerInfo;
import net.xtrafrancyz.ClashPoint.object.TeamPerk;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.entity.NMSEntityUtils;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3f;
import net.xtrafrancyz.VimeNetwork.api.holo.TextHologram;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.util.Spectators;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftEntity;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ResourcePoint {
    private static int idCounter = 0;
    public static final int DEFAULT_HEALTH = 50;
    private final int id = idCounter++;
    private final CPTeam team;
    private final Location location;
    private final Map<PlayerInfo, Long> damageThrottle;
    private EnderCrystal entity;
    private TextHologram hologram;
    public boolean active = true;
    public boolean destroyed = false;
    public int maxHealth = 50;
    public float health;

    public ResourcePoint(CPTeam team, Location location) {
        this.team = team;
        this.location = location;
        this.damageThrottle = new WeakHashMap<PlayerInfo, Long>();
    }

    public CPTeam getTeam() {
        return this.team;
    }

    public Location getLocation() {
        return this.location;
    }

    public int getId() {
        return this.id;
    }

    public void prepareForGame() {
        this.hologram = VimeNetwork.holograms().createText(new Vec3f(this.location).add(0.5f, 2.2f, 0.5f), new String[]{""});
        this.health = this.maxHealth;
        this.deactivate0();
    }

    public void invalidate() {
        this.destroyed = true;
        if (this.entity != null) {
            this.entity.remove();
        }
        if (this.hologram != null) {
            this.hologram.remove();
        }
    }

    public void activate() {
        this.active = true;
        this.entity = (EnderCrystal)NMSEntityUtils.spawn((net.minecraft.server.v1_6_R3.Entity)new EntityResourcePoint(NMSEntityUtils.getNMSWorld((World)this.location.getWorld()), this), (Location)this.location.clone().add(0.5, 0.0, 0.5));
        this.updateHolo();
    }

    public void deactivate() {
        if (!this.active) {
            return;
        }
        this.deactivate0();
    }

    private void deactivate0() {
        this.active = false;
        if (this.entity != null) {
            this.entity.remove();
        }
        this.updateHolo();
    }

    public void destroy(Player destroyer) {
        this.destroyed = true;
        this.active = false;
        if (this.entity != null) {
            this.entity.remove();
        }
        this.team.onResourcePointDestroyed(this);
        PlayerInfo player = PlayerInfo.get(destroyer);
        U.bcast((String)("&f\u0418\u0433\u0440\u043e\u043a " + player.team.chatColor + player.player.getName() + "&f \u0440\u0430\u0437\u0440\u0443\u0448\u0438\u043b " + this.team.chatColor + this.team.names[1] + " \u0442\u043e\u0447\u043a\u0443 \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432"));
        this.team.updateRecord();
        ++player.stats.resourcePointsBreaked;
        int alive = (int)Config.teams.stream().filter(t -> t.getResourcePoints().size() > 0).count();
        player.team.gamePoints += (float)(2 + Math.min(2, alive));
        NetworkPlayer networkPlayer = VimeNetwork.getPlayer((Player)player.player);
        networkPlayer.addCoins(20);
        networkPlayer.giveExp(20);
        boolean achievementCompleted = false;
        if (player.stats.resourcePointsBreaked >= 1) {
            achievementCompleted = networkPlayer.getAchievements().complete(Achievement.CP_RESOURCE_POINTS_BREAK_1);
        }
        if (player.stats.resourcePointsBreaked >= 10) {
            achievementCompleted |= networkPlayer.getAchievements().complete(Achievement.CP_RESOURCE_POINTS_BREAK_10);
        }
        if (player.stats.resourcePointsBreaked >= 100) {
            achievementCompleted |= networkPlayer.getAchievements().complete(Achievement.CP_RESOURCE_POINTS_BREAK_100);
        }
        if (player.stats.resourcePointsBreaked >= 500) {
            achievementCompleted |= networkPlayer.getAchievements().complete(Achievement.CP_RESOURCE_POINTS_BREAK_500);
        }
        if (++player.resourcePointsBroken == 3) {
            achievementCompleted |= networkPlayer.getAchievements().complete(Achievement.CP_DESTROYER);
        }
        if (achievementCompleted) {
            CPTexteria.pointBreakMsgToTeam(this.team, player);
        } else {
            CPTexteria.onPointBreak(this.team, player);
        }
        this.updateHolo();
    }

    public void onDamage(Player player, float damage) {
        if (!this.active || this.destroyed) {
            return;
        }
        if (Spectators.instance().contains(player)) {
            return;
        }
        PlayerInfo info = PlayerInfo.get(player);
        if (info.team == this.team) {
            U.msg((CommandSender)player, (String[])new String[]{"&c\u041d\u0435 \u043b\u043e\u043c\u0430\u0439\u0442\u0435 \u0441\u0432\u043e\u044e \u0442\u043e\u0447\u043a\u0443 \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432, \u043e\u043d\u0430 \u0432\u0430\u043c \u0435\u0449\u0435 \u043f\u0440\u0438\u0433\u043e\u0434\u0438\u0442\u0441\u044f..."});
            return;
        }
        Long lastDamage = this.damageThrottle.getOrDefault(info, 0L);
        if (System.currentTimeMillis() - lastDamage < 150L) {
            return;
        }
        this.damageThrottle.put(info, System.currentTimeMillis());
        int thorns = this.team.getPerkLevel(TeamPerk.RP_THORNS);
        if (thorns > 0) {
            ResourcePoint.damageByThorns((Entity)player, (Entity)this.entity, damage * (float)thorns * 0.2f);
        }
        this.health = Math.max(0.0f, this.health - damage);
        if (this.health == 0.0f) {
            this.destroy(player);
        } else {
            CPTexteria.onPointDamage(this, info);
            this.updateHolo();
        }
    }

    public void updateHolo() {
        String line2 = this.destroyed ? "&c&l\u0423\u043d\u0438\u0447\u0442\u043e\u0436\u0435\u043d\u0430" : (this.active ? "&a&l\u0410\u043a\u0442\u0438\u0432\u043d\u0430&r " + this.getHealthBar() : "&7\u041d\u0435 \u0430\u043a\u0442\u0438\u0432\u043d\u0430&r " + this.getHealthBar());
        this.hologram.setText(new String[]{this.getTeam().chatColor + "\u0422\u043e\u0447\u043a\u0430 \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432", U.colored((String)line2)});
    }

    public String getHealthBar() {
        float percent = this.health / (float)this.maxHealth;
        String color = (double)percent <= 0.25 ? "&c" : ((double)percent <= 0.5 ? "&6" : ((double)percent <= 0.75 ? "&e" : "&a"));
        return color + (int)Math.ceil(this.health) + "&f/&a" + this.maxHealth + "&c \u2764";
    }

    private static void damageByThorns(Entity target, Entity damager, float damage) {
        ((CraftEntity)target).getHandle().damageEntity(DamageSource.a((net.minecraft.server.v1_6_R3.Entity)((CraftEntity)damager).getHandle()), damage);
    }
}

