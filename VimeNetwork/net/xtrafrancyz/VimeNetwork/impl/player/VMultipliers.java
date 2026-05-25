package net.xtrafrancyz.VimeNetwork.impl.player;

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.VTexteria;
import net.xtrafrancyz.VimeNetwork.api.player.Multiplier;
import net.xtrafrancyz.VimeNetwork.api.player.Multipliers;
import net.xtrafrancyz.VimeNetwork.api.player.OwnedMultiplier;

public class VMultipliers implements Multipliers {
   public static final String META_EXTRA = "mult";
   public static final String META_INVENTORY = "mult.inv";
   private final VPlayer player;
   private final TObjectIntMap map;
   private int extra = 1;
   private long extraTo = -1L;

   public VMultipliers(VPlayer player) {
      this.player = player;
      this.map = new TObjectIntHashMap();
   }

   public void load() {
      try {
         String meta = this.player.getMeta("mult");
         if (meta != null) {
            String[] split = meta.split("-");
            this.extra = Integer.parseInt(split[0]);
            this.extraTo = Long.parseLong(split[1]);
            if (this.extraTo < System.currentTimeMillis()) {
               this.deactivate();
            }
         }

         meta = this.player.getMeta("mult.inv");
         if (meta != null) {
            for(String str : meta.split(";")) {
               String[] split = str.split("=");
               int amount = Integer.parseInt(split[1]);
               String[] m = split[0].split("-");
               int multiplier = Integer.parseInt(m[0]);
               int duration = Integer.parseInt(m[1]);
               this.map.put(new Multiplier(multiplier, duration), amount);
            }
         }
      } catch (Exception ex) {
         VNPlugin.instance().getLogger().log(Level.WARNING, (String)null, ex);
      }

   }

   private void save() {
      if (this.map.isEmpty()) {
         this.player.removeMeta("mult.inv");
      } else {
         StringBuilder sb = new StringBuilder();
         TObjectIntIterator<Multiplier> it = this.map.iterator();

         while(it.hasNext()) {
            it.advance();
            sb.append(((Multiplier)it.key()).getMultiplier()).append('-').append(((Multiplier)it.key()).getDuration()).append('=').append(it.value());
            if (it.hasNext()) {
               sb.append(';');
            }
         }

         this.player.setMeta("mult.inv", sb.toString());
      }
   }

   public int getCurrentMultiplier() {
      return this.getRankMultiplier() + this.getExtraMultiplier();
   }

   public int getRankMultiplier() {
      switch (this.player.rank) {
         case IMMORTAL:
            return 5;
         case ADMIN:
         case CHIEF:
         case WARDEN:
         case MODER:
         case YOUTUBE:
         case BUILDER:
         case MAPLEAD:
         case DEV:
         case HOLY:
            return 4;
         case PREMIUM:
            return 3;
         case VIP:
            return 2;
         default:
            return 1;
      }
   }

   public int getExtraMultiplier() {
      return this.extra - 1;
   }

   public long getExtraEndTime() {
      return this.extraTo;
   }

   public void add(Multiplier mult, int amount) {
      this.map.put(mult, this.map.get(mult) + amount);
      this.save();
   }

   public void activate(Multiplier mult) {
      int amount = this.map.get(mult);
      if (amount > 0) {
         this.take(mult);
         this.extra = mult.getMultiplier();
         this.extraTo = System.currentTimeMillis() + (long)(mult.getDuration() * 60 * 1000);
         this.player.coinsTexteria = 0;
         this.player.setMeta("mult", this.extra + "-" + this.extraTo);
         VTexteria.showCoins(this.player);
      }

   }

   public void deactivate() {
      this.extra = 1;
      this.extraTo = -1L;
      this.player.removeMeta("mult");
      this.player.coinsTexteria = 0;
      VTexteria.showCoins(this.player);
   }

   public void take(Multiplier mult, int amount) {
      int old = this.map.get(mult);
      if (old != 0) {
         if (old - amount <= 0) {
            this.map.remove(mult);
         } else {
            this.map.put(mult, old - amount);
         }

         this.save();
      }
   }

   public int getAmount(Multiplier mult) {
      return this.map.get(mult);
   }

   public List list() {
      List<OwnedMultiplier> list = new ArrayList(this.map.size());
      TObjectIntIterator<Multiplier> it = this.map.iterator();

      while(it.hasNext()) {
         it.advance();
         list.add(new OwnedMultiplier((Multiplier)it.key(), it.value()));
      }

      return list;
   }
}
