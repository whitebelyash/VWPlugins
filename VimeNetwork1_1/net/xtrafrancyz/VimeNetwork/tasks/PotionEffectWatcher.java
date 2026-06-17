package net.xtrafrancyz.VimeNetwork.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerEffectAddEvent;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerEffectRemoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class PotionEffectWatcher implements Runnable {
   private Map oldEffects = new HashMap();

   public void run() {
      if (VimeNetwork.features().POTION_EFFECT_EVENTS.isEnabled()) {
         Player[] players = Bukkit.getOnlinePlayers();
         Map<Player, Collection<PotionEffect>> currEffects = new HashMap(players.length);

         for(Player player : players) {
            Collection<PotionEffect> old = (Collection)this.oldEffects.get(player);
            Collection<PotionEffect> curr = player.getActivePotionEffects();
            if (old == null) {
               old = new ArrayList(0);
            }

            label51:
            for(PotionEffect ope : old) {
               for(PotionEffect cpe : curr) {
                  if (cpe.getType() == ope.getType()) {
                     continue label51;
                  }
               }

               Bukkit.getPluginManager().callEvent(new PlayerEffectRemoveEvent(player, ope.getType()));
            }

            Iterator<PotionEffect> it = curr.iterator();

            label65:
            while(it.hasNext()) {
               PotionEffect cpe = (PotionEffect)it.next();

               for(PotionEffect ope : old) {
                  if (cpe.getType() == ope.getType()) {
                     continue label65;
                  }
               }

               PlayerEffectAddEvent event = new PlayerEffectAddEvent(player, cpe);
               Bukkit.getPluginManager().callEvent(event);
               if (event.isCancelled()) {
                  it.remove();
                  player.removePotionEffect(cpe.getType());
               } else if (!event.getEffect().equals(cpe)) {
                  player.addPotionEffect(event.getEffect(), true);
               }
            }

            old.clear();
            currEffects.put(player, curr);
         }

         this.oldEffects.clear();
         this.oldEffects = currEffects;
      }
   }
}
