/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.craftbukkit.libs.com.google.gson.JsonElement
 *  org.bukkit.craftbukkit.libs.com.google.gson.JsonObject
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.api.player.goals;

import java.util.ArrayList;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.ServerType;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.goals.ExpCoinsGoal;
import net.xtrafrancyz.VimeNetwork.api.player.goals.GoalQuery;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.inventory.ItemStack;

public class KillsGoal
extends ExpCoinsGoal {
    public Material neededWeapon = null;
    public int streak = 0;

    public KillsGoal() {
        this(0, 0);
    }

    public KillsGoal(int rewardCoins, int rewardExp) {
        super(rewardCoins, rewardExp);
    }

    @Override
    public void write(JsonObject json) {
        super.write(json);
        if (this.neededWeapon != null) {
            json.addProperty("w", (Number)this.neededWeapon.getId());
        }
        if (this.streak != 0) {
            json.addProperty("s", (Number)this.streak);
        }
    }

    @Override
    public void read(JsonObject json) {
        super.read(json);
        JsonElement elem = json.get("w");
        if (elem != null) {
            this.neededWeapon = Material.getMaterial((int)elem.getAsInt());
        }
        if ((elem = json.get("s")) != null) {
            this.streak = elem.getAsInt();
        }
    }

    @Override
    public boolean isApplicable(NetworkPlayer player, GoalQuery query) {
        if (this.streak > 0) {
            Integer s = (Integer)query.data.get("streak");
            return s != null && s == this.streak;
        }
        if (this.neededWeapon != null) {
            ItemStack weapon = (ItemStack)query.data.get("weapon");
            return weapon != null && this.neededWeapon.getId() == weapon.getTypeId();
        }
        return true;
    }

    @Override
    public ItemStack getItem() {
        if (this.streak > 0) {
            return new ItemStack(Material.DIAMOND_SWORD);
        }
        if (this.neededWeapon != null) {
            return new ItemStack(this.neededWeapon);
        }
        return new ItemStack(Material.GOLD_SWORD);
    }

    @Override
    public List<String> getText(boolean addGame) {
        ArrayList<String> list = new ArrayList<String>(2);
        if (this.streak > 0) {
            list.add("&f\u0423\u0431\u0438\u0442\u044c &e" + this.streak + U.plurals(this.streak, " \u0447\u0435\u043b\u043e\u0432\u0435\u043a\u0430", " \u0447\u0435\u043b\u043e\u0432\u0435\u043a\u0430", " \u0447\u0435\u043b\u043e\u0432\u0435\u043a"));
            String text = "&f\u0437\u0430 \u043e\u0434\u043d\u0443 \u0438\u0433\u0440\u0443";
            if (addGame) {
                text = text + " \u043d\u0430 " + ServerType.byId(this.game).getName();
            }
            list.add(text);
        } else {
            String text = "&f\u0423\u0431\u0438\u0442\u044c &e" + this.getGoal() + U.plurals(this.getGoal(), " \u0447\u0435\u043b\u043e\u0432\u0435\u043a\u0430", " \u0447\u0435\u043b\u043e\u0432\u0435\u043a\u0430", " \u0447\u0435\u043b\u043e\u0432\u0435\u043a");
            if (addGame) {
                text = text + "&f \u043d\u0430 " + ServerType.byId(this.game).getName();
            }
            list.add(text);
            if (this.neededWeapon != null) {
                String name = null;
                switch (this.neededWeapon) {
                    case BOW: {
                        name = "\u043b\u0443\u043a\u0430";
                        break;
                    }
                    case DIAMOND_SWORD: {
                        name = "\u0430\u043b\u043c\u0430\u0437\u043d\u043e\u0433\u043e \u043c\u0435\u0447\u0430";
                        break;
                    }
                    case IRON_SWORD: {
                        name = "\u0436\u0435\u043b\u0435\u0437\u043d\u043e\u0433\u043e \u043c\u0435\u0447\u0430";
                        break;
                    }
                    case GOLD_SWORD: {
                        name = "\u0437\u043e\u043b\u043e\u0442\u043e\u0433\u043e \u043c\u0435\u0447\u0430";
                        break;
                    }
                    case STONE_SWORD: {
                        name = "\u043a\u0430\u043c\u0435\u043d\u043d\u043e\u0433\u043e \u043c\u0435\u0447\u0430";
                        break;
                    }
                    case WOOD_SWORD: {
                        name = "\u0434\u0435\u0440\u0435\u0432\u044f\u043d\u043d\u043e\u0433\u043e \u043c\u0435\u0447\u0430";
                    }
                }
                if (name != null) {
                    list.add("&f\u0441 \u043f\u043e\u043c\u043e\u0449\u044c\u044e &e" + name);
                }
            }
        }
        return list;
    }
}

