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

import java.util.List;
import net.xtrafrancyz.Commons.guild.GuildPerk;
import net.xtrafrancyz.Commons.guild.GuildStatus;
import net.xtrafrancyz.Core.network.packet.Packet69Guild;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.menu.ConfirmMenu;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.impl.player.guild.GuildColorMenu;
import net.xtrafrancyz.VimeNetwork.impl.player.guild.GuildMemberMenu;
import net.xtrafrancyz.VimeNetwork.impl.player.guild.GuildPerksMenu;
import net.xtrafrancyz.VimeNetwork.impl.player.guild.VGuild;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class GuildLeaderMenu
extends GuildMemberMenu {
    public GuildLeaderMenu(NetworkPlayer player) {
        super(player);
        this.inv.setItem(3, Items.name(Material.GOLD_INGOT, "&b&l\u041f\u0435\u0440\u043a\u0438", "&a\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u0443\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u044f"));
        if (this.guild.perks.get((Object)GuildPerk.COLOR) > 0) {
            this.inv.setItem(2, Items.name(new ItemStack(Material.INK_SACK, 1, 1), "&b&l\u0426\u0432\u0435\u0442 \u0433\u0438\u043b\u044c\u0434\u0438\u0438", "&a\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u0443\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u044f"));
        }
    }

    @Override
    protected List<String> getMemberLore(VGuild.Member member) {
        List<String> lore = super.getMemberLore(member);
        if (member.status != GuildStatus.LEADER) {
            lore.add("");
            lore.add("&a\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u0443\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u044f");
        }
        return lore;
    }

    private List<String> superGetMemberLore(VGuild.Member member) {
        return super.getMemberLore(member);
    }

    @Override
    protected void onMemberClick(VGuild.Member member, ClickType clickType) {
        if (member.status != GuildStatus.LEADER) {
            new MemberMenu(this, member).show(this.player.getBukkitPlayer());
        }
    }

    @Override
    public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
        if (slot == 3) {
            new GuildPerksMenu(this).show(player);
            return;
        }
        if (slot == 2 && this.guild.perks.get((Object)GuildPerk.COLOR) > 0) {
            new GuildColorMenu(this).show(player);
            return;
        }
        super.onClick(is, player, slot, clickType);
    }

    private static class MemberMenu
    implements IMenu {
        private static final int SLOT_KICK = 11;
        private static final int SLOT_PROMOTE = 15;
        private GuildLeaderMenu parent;
        private VGuild.Member member;
        private Inventory inv;

        public MemberMenu(GuildLeaderMenu parent, VGuild.Member member) {
            this.parent = parent;
            this.member = member;
            this.inv = Bukkit.createInventory((InventoryHolder)this, (int)27, (String)("\u0418\u0433\u0440\u043e\u043a " + member.name));
            this.inv.setItem(0, Items.name(Material.BED, "&f\u2190 &e\u041d\u0430\u0437\u0430\u0434", new String[0]));
            this.inv.setItem(4, Items.name(Items.head(member.name), member.rank.getColor() + member.name, (List<String>)parent.superGetMemberLore(member)));
            this.inv.setItem(11, Items.name(Material.REDSTONE_BLOCK, "&c\u0418\u0441\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u0438\u0437 \u0433\u0438\u043b\u044c\u0434\u0438\u0438", new String[0]));
            ItemStack promote = member.status == GuildStatus.MEMBER ? Items.name(Material.GOLD_INGOT, "&a\u041f\u043e\u0432\u044b\u0441\u0438\u0442\u044c \u0434\u043e \u041e\u0444\u0438\u0446\u0435\u0440\u0430", new String[0]) : Items.name(Material.IRON_INGOT, "&6\u0417\u0430\u0431\u0440\u0430\u0442\u044c \u0437\u0432\u0430\u043d\u0438\u0435 \u041e\u0444\u0438\u0446\u0435\u0440\u0430", new String[0]);
            this.inv.setItem(15, promote);
        }

        @Override
        public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
            switch (slot) {
                case 0: {
                    this.parent.show(player);
                    break;
                }
                case 11: {
                    this.parent.openKickInventory(this.member);
                    break;
                }
                case 15: {
                    if (this.member.status == GuildStatus.MEMBER) {
                        ConfirmMenu menu = new ConfirmMenu(null, () -> VimeNetwork.core().sendPacket(new Packet69Guild(this.parent.player.getId(), Packet69Guild.Action.PROMOTE).put("target", this.member.name)), "\u041f\u043e\u0434\u0442\u0432\u0435\u0440\u0434\u0438\u0442\u0435 \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u0435");
                        menu.setCancelledCallback(() -> this.parent.show(player));
                        menu.setConfirmText("&a\u041f\u043e\u0432\u044b\u0441\u0438\u0442\u044c \u0438\u0433\u0440\u043e\u043a\u0430", "&f" + this.member.name, "&a\u0434\u043e \u041e\u0444\u0438\u0446\u0435\u0440\u0430");
                        menu.show(player);
                        break;
                    }
                    ConfirmMenu menu = new ConfirmMenu(null, () -> VimeNetwork.core().sendPacket(new Packet69Guild(this.parent.player.getId(), Packet69Guild.Action.DEMOTE).put("target", this.member.name)), "\u041f\u043e\u0434\u0442\u0432\u0435\u0440\u0434\u0438\u0442\u0435 \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u0435");
                    menu.setCancelledCallback(() -> this.parent.show(player));
                    menu.setConfirmText("&a\u0417\u0430\u0431\u0440\u0430\u0442\u044c \u0437\u0432\u0430\u043d\u0438\u0435 \u041e\u0444\u0438\u0446\u0435\u0440\u0430", "&a\u0443 \u0438\u0433\u0440\u043e\u043a\u0430 &f" + this.member.name);
                    menu.show(player);
                }
            }
        }

        public Inventory getInventory() {
            return this.inv;
        }
    }
}

