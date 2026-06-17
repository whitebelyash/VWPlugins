/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.util.Invs
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package net.xtrafrancyz.ClashPoint.task;

import net.xtrafrancyz.ClashPoint.Config;
import net.xtrafrancyz.ClashPoint.object.CPTeam;
import net.xtrafrancyz.ClashPoint.object.PlayerInfo;
import net.xtrafrancyz.ClashPoint.object.ResourcePoint;
import net.xtrafrancyz.ClashPoint.object.TeamPerk;
import net.xtrafrancyz.VimeNetwork.api.util.Invs;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TeamTicker
implements Runnable {
    private final CPTeam team;
    private long lastTick;

    public TeamTicker(CPTeam team) {
        this.team = team;
        this.lastTick = System.currentTimeMillis();
    }

    @Override
    public void run() {
        if (!this.team.getResourcePoints().isEmpty()) {
            int level;
            int regenLevel;
            int freqMillis = 50 * Config.ironFrequency / this.team.getResourcePoints().size();
            int rateLevel = this.team.getPerkLevel(TeamPerk.RP_RATE);
            if (rateLevel == 1) {
                freqMillis = (int)((float)freqMillis * 0.75f);
            } else if (rateLevel == 2) {
                freqMillis = (int)((float)freqMillis * 0.5f);
            }
            int ticks = (int)((System.currentTimeMillis() - this.lastTick) / (long)freqMillis);
            if (ticks > 0) {
                this.lastTick += (long)(ticks * freqMillis);
                for (PlayerInfo player : this.team.players) {
                    ItemStack is = Config.IRON.clone();
                    is.setAmount(ticks);
                    player.personalInventory.addItem(new ItemStack[]{is});
                }
            }
            if ((regenLevel = this.team.getPerkLevel(TeamPerk.RP_REGENERATION)) > 0) {
                int heal = 0;
                if (regenLevel == 1) {
                    heal = 1;
                } else if (regenLevel == 2) {
                    heal = 3;
                }
                for (ResourcePoint rp : this.team.getResourcePoints()) {
                    if (rp.destroyed || rp.health == (float)rp.maxHealth) continue;
                    rp.health = Math.min((float)rp.maxHealth, rp.health + (float)heal);
                    rp.updateHolo();
                }
            }
            if ((level = this.team.getPerkLevel(TeamPerk.RP_DEBUFF)) > 0) {
                for (ResourcePoint rp : this.team.getResourcePoints()) {
                    if (!rp.active) continue;
                    for (CPTeam team : Config.teams) {
                        if (team == rp.getTeam()) continue;
                        block10: for (Player enemy : team.getBukkitPlayers()) {
                            if (rp.getLocation().distanceSquared(enemy.getLocation()) > 100.0) continue;
                            switch (level) {
                                case 1: {
                                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 0), true);
                                    continue block10;
                                }
                                case 2: {
                                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 0), true);
                                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 0), true);
                                    continue block10;
                                }
                                case 3: {
                                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 1), true);
                                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 0), true);
                                    continue block10;
                                }
                                case 4: {
                                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 1), true);
                                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 1), true);
                                }
                            }
                        }
                    }
                }
            }
        }
        for (PlayerInfo player : this.team.players) {
            int prevIron = player.cachedIron;
            int prevGold = player.cachedGold;
            int prevDiamond = player.cachedDiamond;
            player.cachedIron = Invs.count((Inventory)player.personalInventory, (Material)Material.IRON_INGOT) + Invs.count((Inventory)player.player.getInventory(), (Material)Material.IRON_INGOT);
            player.cachedGold = Invs.count((Inventory)player.personalInventory, (Material)Material.GOLD_INGOT) + Invs.count((Inventory)player.player.getInventory(), (Material)Material.GOLD_INGOT);
            player.cachedDiamond = Invs.count((Inventory)player.personalInventory, (Material)Material.DIAMOND) + Invs.count((Inventory)player.player.getInventory(), (Material)Material.DIAMOND);
            if (prevIron == player.cachedIron && prevGold == player.cachedGold && prevDiamond == player.cachedDiamond) continue;
            player.updateResourceBar();
        }
    }
}

