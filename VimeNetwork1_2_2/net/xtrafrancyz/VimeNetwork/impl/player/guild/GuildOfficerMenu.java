/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.inventory.ClickType
 */
package net.xtrafrancyz.VimeNetwork.impl.player.guild;

import java.util.List;
import net.xtrafrancyz.Commons.guild.GuildStatus;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.impl.player.guild.GuildMemberMenu;
import net.xtrafrancyz.VimeNetwork.impl.player.guild.VGuild;
import org.bukkit.event.inventory.ClickType;

public class GuildOfficerMenu
extends GuildMemberMenu {
    public GuildOfficerMenu(NetworkPlayer player) {
        super(player);
    }

    @Override
    protected List<String> getMemberLore(VGuild.Member member) {
        List<String> lore = super.getMemberLore(member);
        if (this.canClick(member)) {
            lore.add("");
            lore.add("&c\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u043f\u0440\u0430\u0432\u043e\u0439 \u043a\u043d\u043e\u043f\u043a\u043e\u0439 \u0447\u0442\u043e\u0431\u044b");
            lore.add("&c \u0438\u0441\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u0438\u0433\u0440\u043e\u043a\u0430 \u0438\u0437 \u0433\u0438\u043b\u044c\u0434\u0438\u0438");
        }
        return lore;
    }

    @Override
    protected void onMemberClick(VGuild.Member member, ClickType clickType) {
        if (!this.canClick(member)) {
            return;
        }
        if (clickType == ClickType.RIGHT) {
            this.openKickInventory(member);
        }
    }

    private boolean canClick(VGuild.Member member) {
        if (member.status == GuildStatus.LEADER || member.status == GuildStatus.OFFICER && this.member.status == GuildStatus.OFFICER) {
            return false;
        }
        return !member.name.equals(this.player.getName());
    }
}

