/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.player.goals;

import java.util.Map;
import net.xtrafrancyz.VimeNetwork.api.player.goals.Goal;
import net.xtrafrancyz.VimeNetwork.api.player.goals.GoalQuery;

public interface Goals {
    public void add(String var1, Goal var2);

    public void addCustom(String var1, Goal var2);

    public boolean remove(String var1);

    public boolean contains(String var1);

    public void trigger(String var1, GoalQuery var2);

    public void triggerAmount(String var1, int var2, GoalQuery var3);

    public void openInventory();

    public Map<String, Goal> getActiveGoals();

    public Map<String, Goal> getCustomGoals();
}

