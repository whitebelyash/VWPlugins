/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.impl.player;

import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.player.ArrowTrail;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.util.Invs;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class VArrowTrailMenu
implements IMenu {
    private final Inventory inv;
    private final VPlayer player;
    private final Inventory prev;

    public VArrowTrailMenu(Inventory prev, NetworkPlayer nplayer) {
        this.prev = prev;
        this.inv = Bukkit.createInventory((InventoryHolder)this, (int)36, (String)"\u0421\u043b\u0435\u0434 \u0437\u0430 \u0441\u0442\u0440\u0435\u043b\u043e\u0439");
        this.player = (VPlayer)nplayer;
        int index = 0;
        for (ArrowTrail trail : ArrowTrail.values()) {
            String lore;
            String color;
            ItemStack is = trail.getItem();
            if (this.player.availableArrowTrails.contains(trail.getId())) {
                if (this.player.getArrowTrail() == trail) {
                    color = "&a";
                    lore = "&a\u0412\u044b\u0431\u0440\u0430\u043d\u043e";
                } else {
                    color = "&b";
                    lore = "&2\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u0432\u044b\u0431\u043e\u0440\u0430";
                }
            } else {
                color = "&c";
                lore = "&c\u041c\u043e\u0436\u043d\u043e \u043d\u0430\u0439\u0442\u0438 \u0432 \u0441\u043e\u043a\u0440\u043e\u0432\u0438\u0449\u043d\u0438\u0446\u0435";
            }
            Items.name(is, color + trail.getName(), lore);
            this.inv.setItem(this.getSlot(index++), is);
        }
    }

    private int getSlot(int index) {
        return 10 + 9 * (index / 7) + index % 7;
    }

    private int getIndex(int slot) {
        if (slot % 9 == 0 || (slot + 1) % 9 == 0) {
            return -1;
        }
        if ((slot -= 10) < 0) {
            return -1;
        }
        int row = slot / 9;
        return row * 7 + (slot - row * 9) % 7;
    }

    @Override
    public void onClick(ItemStack is, Player bukkitPlayer, int slot, ClickType clickType) {
        if (this.prev != null && slot == 4) {
            Invs.forceOpen((HumanEntity)bukkitPlayer, this.prev);
            return;
        }
        int index = this.getIndex(slot);
        if (index < 0 || index >= ArrowTrail.values().length) {
            return;
        }
        ArrowTrail selected = ArrowTrail.values()[index];
        if (this.player.availableArrowTrails.contains(selected.getId()) && this.player.getArrowTrail() != selected) {
            this.player.setArrowTrail(selected);
            bukkitPlayer.closeInventory();
        }
    }

    public Inventory getInventory() {
        return this.inv;
    }
}

