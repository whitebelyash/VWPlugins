/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  gnu.trove.map.TIntIntMap
 *  gnu.trove.map.hash.TIntIntHashMap
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.impl.player.guild;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import net.xtrafrancyz.Commons.guild.GuildLeveling;
import net.xtrafrancyz.Commons.guild.GuildPerk;
import net.xtrafrancyz.Commons.guild.GuildStatus;
import net.xtrafrancyz.Core.network.packet.Packet69Guild;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.menu.ConfirmMenu;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.player.guild.GuildLeaderMenu;
import net.xtrafrancyz.VimeNetwork.impl.player.guild.GuildOfficerMenu;
import net.xtrafrancyz.VimeNetwork.impl.player.guild.VGuild;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class GuildMemberMenu
implements IMenu {
    protected static final int SLOT_COLOR = 2;
    protected static final int SLOT_PERKS = 3;
    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
    protected Inventory inv;
    protected NetworkPlayer player;
    protected VGuild.Member member;
    public VGuild guild;
    private int page = 0;
    private boolean hasNextPage = false;
    private TIntIntMap slotToIndex = new TIntIntHashMap(10, 0.5f, 0, -1);

    public GuildMemberMenu(NetworkPlayer player) {
        this.inv = Bukkit.createInventory((InventoryHolder)this, (int)54, (String)"\u041c\u0435\u043d\u044e \u0433\u0438\u043b\u044c\u0434\u0438\u0438");
        this.player = player;
        this.guild = (VGuild)player.getGuild();
        this.member = this.guild.getMember(player.getName());
        float percentage = (float)GuildLeveling.getPercentageToNextLevel(this.guild.exp);
        this.inv.setItem(4, Items.name(Material.PAPER, "&b&l\u0413\u0438\u043b\u044c\u0434\u0438\u044f:", "&7\u0418\u043c\u044f: &e&l" + this.guild.name, "&7\u0421\u043e\u0437\u0434\u0430\u043d\u0430: &f" + DATE_FORMAT.format(new Date((long)this.guild.creationTime * 1000L)), "&7\u0422\u0435\u0433: &f" + (this.guild.getTag() == null ? "-" : this.guild.getTag()), "&7\u041a\u043e\u0438\u043d\u043e\u0432: &e" + this.guild.coins, "&7\u0423\u0440\u043e\u0432\u0435\u043d\u044c: &e" + this.guild.level, "&7\u041e\u043f\u044b\u0442: &9" + (this.guild.exp - GuildLeveling.getTotalExp(this.guild.level)) + "&7/&9" + GuildLeveling.getExpToNextLevel(this.guild.level), "&f[" + U.genBar(48, percentage, '|', "&7", "&a") + "&f] &7" + (int)(percentage * 100.0f) + "%"));
        this.inv.setItem(5, Items.name(new ItemStack(Material.SKULL_ITEM, 1, 3), "&b&l\u0427\u043b\u0435\u043d\u044b \u0433\u0438\u043b\u044c\u0434\u0438\u0438:", "&7\u041b\u0438\u0434\u0435\u0440: &c" + this.guild.getLeader().name, "&7\u041e\u0444\u0438\u0446\u0435\u0440\u044b: &f" + this.guild.getOfficers().size(), "&7\u0418\u0433\u0440\u043e\u043a\u0438: &f" + this.guild.getNormalPlayers().size(), "&7\u0412\u0441\u0435\u0433\u043e: &f" + this.guild.members.size() + "&7/&f" + GuildPerk.getMaxMembers(this.guild.perks.get((Object)GuildPerk.MEMBERS))));
        this.updateMembers();
    }

    protected void openKickInventory(VGuild.Member member) {
        ConfirmMenu menu = new ConfirmMenu(null, () -> VimeNetwork.core().sendPacket(new Packet69Guild(this.player.getId(), Packet69Guild.Action.KICK).put("target", member.name)), "\u0418\u0441\u043a\u043b\u044e\u0447\u0438\u0442\u044c " + member.name);
        menu.setCancelledCallback(() -> this.show(this.player.getBukkitPlayer()));
        menu.setConfirmText("&a\u0418\u0441\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u0438\u0433\u0440\u043e\u043a\u0430", "&f" + member.name, "&a\u0438\u0437 \u0433\u0438\u043b\u044c\u0434\u0438\u0438");
        menu.show(this.player.getBukkitPlayer());
    }

    protected void updateMembers() {
        int slot;
        for (slot = 9; slot < this.inv.getSize(); ++slot) {
            this.inv.setItem(slot, null);
        }
        int startIndex = this.page * (this.inv.getSize() - 9);
        if (this.page > 0) {
            startIndex -= this.page * 2 - 1;
            this.inv.setItem(this.inv.getSize() - 9, Items.name(Material.ARROW, "&e\u041f\u0440\u0435\u0434\u044b\u0434\u0443\u0449\u0430\u044f \u0441\u0442\u0440\u0430\u043d\u0438\u0446\u0430", new String[0]));
        }
        slot = 9;
        this.slotToIndex.clear();
        for (int i = startIndex; slot < this.inv.getSize() - 1 && i < this.guild.members.size(); ++i, ++slot) {
            if (this.page > 0 && slot == this.inv.getSize() - 9) {
                --i;
                continue;
            }
            this.setMemberItem(slot, i);
        }
        if (slot == this.inv.getSize() - 1) {
            if (startIndex + this.slotToIndex.size() == this.guild.members.size() - 1) {
                this.setMemberItem(slot, this.guild.members.size() - 1);
                this.hasNextPage = false;
            } else {
                this.hasNextPage = startIndex + this.slotToIndex.size() != this.guild.members.size();
            }
        } else {
            this.hasNextPage = false;
        }
        if (this.hasNextPage) {
            this.inv.setItem(slot, Items.name(Material.ARROW, "&e\u0421\u043b\u0435\u0434\u0443\u044e\u0449\u0430\u044f \u0441\u0442\u0440\u0430\u043d\u0438\u0446\u0430", new String[0]));
        }
    }

    private void setMemberItem(int slot, int memberIndex) {
        VGuild.Member member = this.guild.members.get(memberIndex);
        String color = member.rank.getColor().isEmpty() ? "&f" : member.rank.getColor();
        this.inv.setItem(slot, Items.name(Items.head(member.name), color + member.name, this.getMemberLore(member)));
        this.slotToIndex.put(slot, memberIndex);
    }

    protected List<String> getMemberLore(VGuild.Member member) {
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(member.online ? "&a\u041e\u043d\u043b\u0430\u0439\u043d" : "&c\u041e\u0444\u0444\u043b\u0430\u0439\u043d");
        lore.add("&7\u0414\u0430\u0442\u0430 \u0432\u0441\u0442\u0443\u043f\u043b\u0435\u043d\u0438\u044f: &f" + DATE_FORMAT.format(new Date((long)member.joinDate * 1000L)));
        lore.add("&7\u0421\u0442\u0430\u0442\u0443\u0441: &f" + member.status.getName());
        lore.add("&7\u041a\u043b\u0430\u043d\u043e\u0432\u044b\u0445 \u043a\u043e\u0438\u043d\u043e\u0432: &e" + member.coins);
        lore.add("&7\u041a\u043b\u0430\u043d\u043e\u0432\u043e\u0433\u043e \u043e\u043f\u044b\u0442\u0430: &9" + member.exp);
        return lore;
    }

    protected void onMemberClick(VGuild.Member member, ClickType clickType) {
    }

    @Override
    public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
        if (this.hasNextPage && slot == this.inv.getSize() - 1) {
            ++this.page;
            this.updateMembers();
            return;
        }
        if (this.page > 0 && slot == this.inv.getSize() - 9) {
            --this.page;
            this.updateMembers();
            return;
        }
        if (slot >= 9 && is != null && is.getType() != Material.AIR) {
            int index = this.slotToIndex.get(slot);
            if (index < 0 || index >= this.guild.members.size()) {
                return;
            }
            VGuild.Member member = this.guild.members.get(index);
            this.onMemberClick(member, clickType);
        }
    }

    public Inventory getInventory() {
        return this.inv;
    }

    public static void openMenu(NetworkPlayer player, Packet69Guild packet) {
        switch (GuildStatus.values()[packet.data.getByte("pstatus")]) {
            case LEADER: {
                new GuildLeaderMenu(player).show(player.getBukkitPlayer());
                break;
            }
            case OFFICER: {
                new GuildOfficerMenu(player).show(player.getBukkitPlayer());
                break;
            }
            case MEMBER: {
                new GuildMemberMenu(player).show(player.getBukkitPlayer());
            }
        }
    }
}

