/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.Packet20NamedEntitySpawn
 *  net.xtrafrancyz.VimeNetwork.TagManager
 *  net.xtrafrancyz.VimeNetwork.VNPlugin
 *  net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer
 *  net.xtrafrancyz.VimeNetwork.packet.OutgoingPacketEvent
 *  org.bukkit.ChatColor
 */
package net.xtrafrancyz.SkyWars;

import java.util.function.Consumer;
import net.minecraft.server.v1_6_R3.Packet20NamedEntitySpawn;
import net.xtrafrancyz.SkyWars.Config;
import net.xtrafrancyz.SkyWars.PlayerInfo;
import net.xtrafrancyz.VimeNetwork.TagManager;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.packet.OutgoingPacketEvent;
import org.bukkit.ChatColor;

public class SWTagManager
implements Consumer<OutgoingPacketEvent> {
    private final TagManager tags;

    public SWTagManager() {
        this.tags = VNPlugin.instance().tags;
    }

    @Override
    public void accept(OutgoingPacketEvent event) {
        if (!(event.getPacket() instanceof Packet20NamedEntitySpawn)) {
            return;
        }
        Packet20NamedEntitySpawn packet = (Packet20NamedEntitySpawn)event.getPacket();
        NetworkPlayer playerNetwork = (NetworkPlayer)this.tags.idToPlayer.get(packet.a);
        if (playerNetwork != null) {
            PlayerInfo player = PlayerInfo.PLAYERS.get(playerNetwork.getName());
            PlayerInfo receiver = PlayerInfo.PLAYERS.get(event.getReceiver().getName());
            if (player == null || receiver == null) {
                packet.b = playerNetwork.getTag().getVisibleName().replace("@@", "");
                return;
            }
            String replacement = "";
            if (Config.islandPlayers > 1 && player.island != null) {
                replacement = "[" + player.island.tag + "] ";
            }
            if (player.island != null && receiver.island != null) {
                replacement = receiver.island.equals(player.island) ? ChatColor.GREEN + replacement : ChatColor.RED + replacement;
            }
            packet.b = playerNetwork.getTag().getVisibleName().replace("@@", replacement);
        }
    }
}

