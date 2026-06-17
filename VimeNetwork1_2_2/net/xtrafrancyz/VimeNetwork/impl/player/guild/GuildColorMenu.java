/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.impl.player.guild;

import java.util.HashMap;
import java.util.Map;
import net.xtrafrancyz.Core.network.packet.Packet69Guild;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.impl.player.guild.GuildMemberMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class GuildColorMenu
implements IMenu {
    private final GuildMemberMenu parent;
    private final Inventory inv;
    private final Map<Integer, String> colorMap;

    public GuildColorMenu(GuildMemberMenu parent) {
        this.parent = parent;
        this.inv = Bukkit.createInventory((InventoryHolder)this, (int)27, (String)"\u0412\u044b\u0431\u043e\u0440 \u0446\u0432\u0435\u0442\u0430 \u0433\u0438\u043b\u044c\u0434\u0438\u0438");
        this.inv.setItem(4, Items.name(Material.BED, "&f\u2190 &e\u041d\u0430\u0437\u0430\u0434", new String[0]));
        this.colorMap = new HashMap<Integer, String>();
        int slot = 10;
        this.addColor(slot++, "f", "\u0411\u0435\u043b\u044b\u0439", 0);
        this.addColor(slot++, "e", "\u0416\u0435\u043b\u0442\u044b\u0439", 11);
        this.addColor(slot++, "6", "\u041e\u0440\u0430\u043d\u0436\u0435\u0432\u044b\u0439", 14);
        this.addColor(slot++, "a", "\u0417\u0435\u043b\u0451\u043d\u044b\u0439", 10);
        this.addColor(slot++, "2", "\u0422\u0435\u043c\u043d\u043e-\u0437\u0435\u043b\u0451\u043d\u044b\u0439", 2);
        this.addColor(slot++, "c", "\u041a\u0440\u0430\u0441\u043d\u044b\u0439", 1);
        this.addColor(slot, "4", "\u041e\u0447\u0435\u043d\u044c \u043a\u0440\u0430\u0441\u043d\u044b\u0439", 331);
        slot = 19;
        this.addColor(slot++, "d", "\u0420\u043e\u0437\u043e\u0432\u044b\u0439", 13);
        this.addColor(slot++, "5", "\u0424\u0438\u043e\u043b\u0435\u0442\u043e\u0432\u044b\u0439", 5);
        this.addColor(slot++, "b", "\u0413\u043e\u043b\u0443\u0431\u043e\u0439", 12);
        this.addColor(slot++, "3", "\u0411\u0438\u0440\u044e\u0437\u043e\u0432\u044b\u0439", 6);
        this.addColor(slot++, "9", "\u0421\u0438\u043d\u0438\u0439", 4);
        this.addColor(slot++, "7", "\u0421\u0435\u0440\u044b\u0439", 7);
        this.addColor(slot, "8", "\u0422\u0435\u043c\u043d\u043e-\u0441\u0435\u0440\u044b\u0439", 8);
    }

    private void addColor(int slot, String color, String name, int wool) {
        ItemStack is = wool == 331 ? new ItemStack(Material.REDSTONE) : new ItemStack(Material.INK_SACK, 1, (short)wool);
        this.colorMap.put(slot, color);
        String tag = this.parent.guild.tag == null ? "-" : this.parent.guild.tag;
        this.inv.setItem(slot, Items.name(is, "&f" + name, "&7\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435: &" + color + this.parent.guild.name, "&7\u0422\u0435\u0433: &" + color + tag, "", "&a\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u0432\u044b\u0431\u043e\u0440\u0430"));
    }

    @Override
    public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
        if (slot == 4) {
            this.parent.show(player);
            return;
        }
        String color = this.colorMap.get(slot);
        if (color != null) {
            VimeNetwork.core().sendPacket(new Packet69Guild(VimeNetwork.getPlayer(player).getId(), Packet69Guild.Action.SET_COLOR).put("color", color));
            player.closeInventory();
        }
    }

    public Inventory getInventory() {
        return this.inv;
    }
}

