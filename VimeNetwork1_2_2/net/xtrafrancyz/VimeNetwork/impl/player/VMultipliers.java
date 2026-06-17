/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  gnu.trove.iterator.TObjectIntIterator
 *  gnu.trove.map.TObjectIntMap
 *  gnu.trove.map.hash.TObjectIntHashMap
 */
package net.xtrafrancyz.VimeNetwork.impl.player;

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.xtrafrancyz.Commons.F;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.VTexteria;
import net.xtrafrancyz.VimeNetwork.api.player.Multiplier;
import net.xtrafrancyz.VimeNetwork.api.player.Multipliers;
import net.xtrafrancyz.VimeNetwork.api.player.OwnedMultiplier;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;

public class VMultipliers
implements Multipliers {
    public static final String META_EXTRA = "mult";
    public static final String META_INVENTORY = "mult.inv";
    private final VPlayer player;
    private final TObjectIntMap<Multiplier> map;
    private int extra = 1;
    private long extraTo = -1L;

    public VMultipliers(VPlayer player) {
        this.player = player;
        this.map = new TObjectIntHashMap();
    }

    public void load() {
        try {
            String meta = this.player.getMeta(META_EXTRA);
            if (meta != null) {
                String[] split = meta.split("-");
                this.extra = Integer.parseInt(split[0]);
                this.extraTo = Long.parseLong(split[1]);
                if (this.extraTo < System.currentTimeMillis()) {
                    this.deactivate();
                }
            }
            if ((meta = this.player.getMeta(META_INVENTORY)) != null) {
                for (String str : meta.split(";")) {
                    String[] split = str.split("=");
                    int amount = Integer.parseInt(split[1]);
                    String[] m = split[0].split("-");
                    int multiplier = Integer.parseInt(m[0]);
                    int duration = Integer.parseInt(m[1]);
                    this.map.put((Object)new Multiplier(multiplier, duration), amount);
                }
            }
        }
        catch (Exception ex) {
            VNPlugin.instance().getLogger().log(Level.WARNING, null, ex);
        }
    }

    private void save() {
        if (this.map.isEmpty()) {
            this.player.removeMeta(META_INVENTORY);
            return;
        }
        StringBuilder sb = new StringBuilder();
        TObjectIntIterator it = this.map.iterator();
        while (it.hasNext()) {
            it.advance();
            sb.append(((Multiplier)it.key()).getMultiplier()).append('-').append(((Multiplier)it.key()).getDuration()).append('=').append(it.value());
            if (!it.hasNext()) continue;
            sb.append(';');
        }
        this.player.setMeta(META_INVENTORY, sb.toString());
    }

    @Override
    public float getCurrentMultiplier() {
        return (float)(this.getRankMultiplier() + this.getExtraMultiplier()) + this.getGuildMultiplier();
    }

    @Override
    public int getRankMultiplier() {
        switch (this.player.rank) {
            case IMMORTAL: {
                return 5;
            }
            case ADMIN: 
            case CHIEF: 
            case WARDEN: 
            case MODER: 
            case YOUTUBE: 
            case BUILDER: 
            case MAPLEAD: 
            case DEV: 
            case HOLY: {
                return 4;
            }
            case PREMIUM: {
                return 3;
            }
            case VIP: {
                return 2;
            }
        }
        return 1;
    }

    @Override
    public float getGuildMultiplier() {
        return this.player.guild == null ? 0.0f : this.player.guild.coinsMultiplier;
    }

    @Override
    public String getFormattedMultiplier() {
        float mult = this.getCurrentMultiplier();
        if (mult % 1.0f < 1.0E-4f) {
            return Integer.toString(Math.round(mult));
        }
        return F.formatFloat(mult, 1);
    }

    @Override
    public int getExtraMultiplier() {
        return this.extra - 1;
    }

    @Override
    public long getExtraEndTime() {
        return this.extraTo;
    }

    @Override
    public void add(Multiplier mult, int amount) {
        this.map.put((Object)mult, this.map.get((Object)mult) + amount);
        this.save();
    }

    @Override
    public void activate(Multiplier mult) {
        int amount = this.map.get((Object)mult);
        if (amount > 0) {
            this.take(mult);
            this.extra = mult.getMultiplier();
            this.extraTo = System.currentTimeMillis() + (long)(mult.getDuration() * 60 * 1000);
            this.player.coinsTexteriaMultView = 0;
            this.player.setMeta(META_EXTRA, this.extra + "-" + this.extraTo);
            VTexteria.showCoins(this.player);
        }
    }

    @Override
    public void deactivate() {
        this.extra = 1;
        this.extraTo = -1L;
        this.player.removeMeta(META_EXTRA);
        this.player.coinsTexteriaMultView = 0;
        VTexteria.showCoins(this.player);
    }

    @Override
    public void take(Multiplier mult, int amount) {
        int old = this.map.get((Object)mult);
        if (old == 0) {
            return;
        }
        if (old - amount <= 0) {
            this.map.remove((Object)mult);
        } else {
            this.map.put((Object)mult, old - amount);
        }
        this.save();
    }

    @Override
    public int getAmount(Multiplier mult) {
        return this.map.get((Object)mult);
    }

    @Override
    public List<OwnedMultiplier> list() {
        ArrayList<OwnedMultiplier> list = new ArrayList<OwnedMultiplier>(this.map.size());
        TObjectIntIterator it = this.map.iterator();
        while (it.hasNext()) {
            it.advance();
            list.add(new OwnedMultiplier((Multiplier)it.key(), it.value()));
        }
        return list;
    }
}

