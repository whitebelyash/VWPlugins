package net.xtrafrancyz.VimeNetwork.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.xtrafrancyz.Commons.Leveling;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet;
import net.xtrafrancyz.Core.network.packet.Packet10PlayerGiveExp;
import net.xtrafrancyz.Core.network.packet.Packet1PlayerInfo;
import net.xtrafrancyz.Core.network.packet.Packet301Subscribe;
import net.xtrafrancyz.Core.network.packet.Packet302Unsubscribe;
import net.xtrafrancyz.Core.network.packet.Packet51ChatMessage;
import net.xtrafrancyz.Core.network.packet.Packet52CustomMessage;
import net.xtrafrancyz.Core.network.packet.Packet54PrivateMessage;
import net.xtrafrancyz.Core.network.packet.Packet57StreamStatus;
import net.xtrafrancyz.Core.network.packet.Packet59Party;
import net.xtrafrancyz.Core.network.packet.Packet5PlayerCoinsChange;
import net.xtrafrancyz.Core.network.packet.Packet60PartyInvite;
import net.xtrafrancyz.Core.network.packet.Packet63FriendRequest;
import net.xtrafrancyz.Core.network.packet.Packet6PlayerMetaChange;
import net.xtrafrancyz.VimeNetwork.Debug;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.VTexteria;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.event.CoreCustomMessageEvent;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLoadedEvent;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.player.CorePlayer;
import net.xtrafrancyz.VimeNetwork.impl.player.VParty;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import net.xtrafrancyz.bukkit.texteria.Texteria2D;
import net.xtrafrancyz.bukkit.texteria.elements.Text;
import net.xtrafrancyz.bukkit.texteria.utils.Position;
import net.xtrafrancyz.bukkit.texteria.utils.Visibility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BukkitPacketHandler extends PacketHandler {
   private final VNPlugin plugin;

   public BukkitPacketHandler(VNPlugin plugin) {
      this.plugin = plugin;
   }

   public void handle(Packet packet) {
      if (Debug.CORE.isEnabled()) {
         this.plugin.getLogger().info("[Core] -> " + packet.toString());
      }

   }

   public void handle1PlayerInfo(Packet1PlayerInfo packet) {
      Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
         if (VimeNetwork.isPlayerOnline(packet.username)) {
            CorePlayer player = (CorePlayer)VPlayer.get(packet.username);
            player.id = packet.id;
            player.coins = packet.coins;
            VPlayer.IDS.put(player.id, player);
            if (!player.loaded) {
               player.exp = packet.exp;
               player.level = Leveling.getLevel(player.exp);
            } else {
               int received = packet.exp - player.exp - player.expBuffer;
               if (received > 0) {
                  player.giveExp(received);
                  player.expBuffer -= received;
               }
            }

            if ((packet.queryFlags & 4) == 4) {
               player.rank = Rank.getRank(packet.rank);
            }

            if ((packet.queryFlags & 1) == 1) {
               player.meta.clear();
               player.meta.putAll(packet.meta);
               player.onMetaLoaded();
            }

            if ((packet.queryFlags & 32) == 32) {
               player.stats.load(packet);
            }

            if ((packet.queryFlags & 64) == 64) {
               player.achievements.load(packet);
            }

            player.player.setDisplayName(player.getPrefixedName());
            VTexteria.showCoins(player);
            if (VimeNetwork.features().CHANGE_PLAYER_LIST_NAMES.isEnabled()) {
               String name = player.rank.getColor() + player.username;
               if (name.length() > 16) {
                  name = name.substring(0, 15);
               }

               player.player.setPlayerListName(name);
            }

            if (!player.loaded) {
               player.loaded = true;
               if (VimeNetwork.features().CHANGE_TAGS.isEnabled() && player.rank.has(Rank.VIP)) {
                  player.setTag(player.rank.getColor() + player.username);
               }

               Bukkit.getPluginManager().callEvent(new PlayerLoadedEvent(player, packet.switchData));
            }

         }
      });
   }

   public void handle5PlayerCoinsChange(Packet5PlayerCoinsChange packet) {
      Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
         for(int userid : packet.users) {
            VPlayer player = (VPlayer)VPlayer.IDS.get(userid);
            if (player != null) {
               player.coins += packet.change;
               VTexteria.showCoins(player);
               VTexteria.showCoinsChange(player, packet.change);
            }
         }

      });
   }

   public void handle6PlayerMetaChange(Packet6PlayerMetaChange packet) {
      Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
         VPlayer player = (VPlayer)VPlayer.IDS.get(packet.userid);
         if (player != null) {
            ((CorePlayer)player).meta.put(packet.key, packet.value);
            player.onMetaUpdate(packet.key, packet.value);
         }
      });
   }

   public void handle10PlayerGiveExp(Packet10PlayerGiveExp packet) {
      Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
         for(int id : packet.users) {
            VPlayer player = (VPlayer)VPlayer.IDS.get(id);
            if (player != null) {
               player.exp += packet.exp;
               if (player.exp >= Leveling.getTotalExp(player.level + 1)) {
                  player.level = Leveling.getLevel(player.exp);
                  VTexteria.showUsername(player);
               }
            }
         }

      });
   }

   public void handle51ChatMessage(Packet51ChatMessage packet) {
      String message = U.colored(packet.message);

      for(String receiver : packet.receivers) {
         Player player = Bukkit.getPlayerExact(receiver);
         if (player != null) {
            player.sendMessage(message);
         }
      }

   }

   public void handle52CustomMessage(Packet52CustomMessage packet) {
      CoreCustomMessageEvent event = new CoreCustomMessageEvent(packet);
      Bukkit.getPluginManager().callEvent(event);
      if (!event.isCancelled()) {
         switch (packet.tag) {
            case "cmd":
               Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), packet.data.getString("cmd")));
               break;
            case "bcast":
               U.bcast(packet.data.getString("message"));
         }

      }
   }

   public void handle54PrivateMessage(Packet54PrivateMessage packet) {
      Player player = Bukkit.getPlayerExact(packet.receiver);
      if (player != null) {
         player.sendMessage(U.colored("&e[&f" + packet.sender + "&e -> &fВы&e] ") + packet.message);
         Bukkit.getLogger().info("[" + packet.sender + " -> " + player.getName() + "] " + packet.message);
      }

   }

   public void handle57StreamStatus(Packet57StreamStatus packet) {
      this.plugin.streamMenu.update(packet);
   }

   public void handle60PartyInvite(Packet60PartyInvite packet) {
      if (VimeNetwork.isPlayerOnline(packet.username)) {
         VPlayer player = VPlayer.get(packet.username);
         if (player.settings.get(0)) {
            U.msg(player.player, (String[])(T.success("&dГруппа", "Игрок &f" + packet.inviter + "&a приглашает вас в свою группу. У вас есть &f60 секунд&a на ответ")));
            U.msg(player.player, (String[])(T.success("&dГруппа", "Для принятия приглашения напишите: &f/party join " + packet.inviter)));
            VTexteria.showPartyInvite(player.player, packet.inviter);
         }
      }
   }

   public void handle59Party(Packet59Party packet) {
      if (VimeNetwork.isPlayerOnline(packet.username)) {
         VPlayer player = VPlayer.get(packet.username);
         if (packet.leader != null) {
            player.party = new VParty(packet.leader, packet.players);
            player.getAchievements().complete(Achievement.GLOBAL_PARTY);
            if (player.party.size() >= 5) {
               player.getAchievements().complete(Achievement.GLOBAL_PARTY_5);
            }

            Function<Packet59Party.PartyPlayer, String> nameFormatter = (memberx) -> "[&e" + memberx.level + "&f] " + memberx.rank.getColor() + memberx.username;
            packet.players.sort((p1, p2) -> {
               int diff = p2.level - p1.level;
               if (diff != 0) {
                  return diff;
               } else {
                  int r = p2.rank.compareTo(p1.rank);
                  return r != 0 ? r : p1.username.compareToIgnoreCase(p2.username);
               }
            });
            List<String> list = new ArrayList(player.party.size());
            if (!player.isPartyLeader()) {
               list.add((String)nameFormatter.apply(packet.leader) + " &c☃");

               for(Packet59Party.PartyPlayer member : packet.players) {
                  if (!member.username.equals(packet.username)) {
                     list.add(nameFormatter.apply(member));
                  }
               }
            } else {
               for(Packet59Party.PartyPlayer member : packet.players) {
                  list.add(nameFormatter.apply(member));
               }
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> Texteria2D.add(((Text)((Text)(new Text("vn.p", (String[])list.toArray(new String[list.size()]))).setPosition(Position.TOP_LEFT)).setOrientation(1).setVisibility(new Visibility.IngameNotF3())).setOffset(2, 22), new Player[]{player.getBukkitPlayer()}));
         } else {
            player.party = null;
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> Texteria2D.remove("vn.p", new Player[]{player.getBukkitPlayer()}));
         }

      }
   }

   public void handle63FriendRequest(Packet63FriendRequest packet) {
      if (VimeNetwork.isPlayerOnline(packet.username)) {
         VPlayer player = VPlayer.get(packet.username);
         if (player.settings.get(4)) {
            U.msg(player.player, (String[])(T.success("&2Друзья", "Игрок &f" + packet.requester + "&a хочет добавить вас в друзья. У вас есть &f5 минут&a на ответ"), T.success("&2Друзья", "Для принятия запроса напишите: &f/friend accept " + packet.requester)));
            VTexteria.showFriendRequest(player.player, packet.requester);
         }
      }
   }

   public void handle301Subscribe(Packet301Subscribe packet) {
      for(String event : packet.events) {
         switch (event) {
            case "console.log":
               this.plugin.core.logHandler.active = true;
               this.plugin.getLogger().info("Log is now sending to core");
         }
      }

   }

   public void handle302Unsubscribe(Packet302Unsubscribe packet) {
      switch (packet.event) {
         case "console.log":
            this.plugin.core.logHandler.active = false;
            this.plugin.getLogger().info("Log is not sending to core anymore");
         default:
      }
   }
}
