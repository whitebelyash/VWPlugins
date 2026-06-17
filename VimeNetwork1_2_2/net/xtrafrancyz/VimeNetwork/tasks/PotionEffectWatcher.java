/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.potion.PotionEffect
 */
package net.xtrafrancyz.VimeNetwork.tasks;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerEffectAddEvent;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerEffectRemoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;

public class PotionEffectWatcher
implements Runnable {
    private Map<Player, Collection<PotionEffect>> oldEffects = new HashMap<Player, Collection<PotionEffect>>();

    @Override
    public void run() {
        if (!VimeNetwork.features().POTION_EFFECT_EVENTS.isEnabled()) {
            return;
        }
        Player[] players = Bukkit.getOnlinePlayers();
        HashMap<Player, Collection<PotionEffect>> currEffects = new HashMap<Player, Collection<PotionEffect>>((int)((float)players.length * 1.33f));
        for (Player player : players) {
            Collection<PotionEffect> old = this.oldEffects.get(player);
            Collection curr = player.getActivePotionEffects();
            if (old == null) {
                old = Collections.emptyList();
            }
            block1: for (PotionEffect ope : old) {
                for (PotionEffect cpe : curr) {
                    if (cpe.getType() != ope.getType()) continue;
                    continue block1;
                }
                Bukkit.getPluginManager().callEvent((Event)new PlayerEffectRemoveEvent(player, ope.getType()));
            }
            Iterator it = curr.iterator();
            block3: while (it.hasNext()) {
                PotionEffect cpe = (PotionEffect)it.next();
                for (PotionEffect ope : old) {
                    if (cpe.getType() != ope.getType()) continue;
                    continue block3;
                }
                PlayerEffectAddEvent event = new PlayerEffectAddEvent(player, cpe);
                Bukkit.getPluginManager().callEvent((Event)event);
                if (event.isCancelled()) {
                    it.remove();
                    player.removePotionEffect(cpe.getType());
                    continue;
                }
                if (event.getEffect().equals((Object)cpe)) continue;
                player.addPotionEffect(event.getEffect(), true);
            }
            old.clear();
            currEffects.put(player, curr);
        }
        this.oldEffects.clear();
        this.oldEffects = currEffects;
    }
}

