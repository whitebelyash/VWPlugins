package net.xtrafrancyz.BedWars;

import java.util.Set;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLeaveEvent;
import net.xtrafrancyz.VimeNetwork.api.util.Spectators;
import net.xtrafrancyz.bukkit.texteria.Texteria2D;
import net.xtrafrancyz.bukkit.texteria.TexteriaCallbackEvent;
import net.xtrafrancyz.bukkit.texteria.elements.Image;
import net.xtrafrancyz.bukkit.texteria.elements.ProgressBar;
import net.xtrafrancyz.bukkit.texteria.elements.Rectangle;
import net.xtrafrancyz.bukkit.texteria.elements.Text;
import net.xtrafrancyz.bukkit.texteria.elements.TextTimer;
import net.xtrafrancyz.bukkit.texteria.utils.Attachment;
import net.xtrafrancyz.bukkit.texteria.utils.ByteMap;
import net.xtrafrancyz.bukkit.texteria.utils.IntColor;
import net.xtrafrancyz.bukkit.texteria.utils.OnClick;
import net.xtrafrancyz.bukkit.texteria.utils.Position;
import net.xtrafrancyz.bukkit.texteria.utils.TexteriaBuffer;
import net.xtrafrancyz.bukkit.texteria.utils.OnClick.Action;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class TournamentUI implements Listener {
   public static TournamentUI instance;
   private final BedWars plugin;

   public TournamentUI(BedWars plugin) {
      this.plugin = plugin;
      instance = this;
      Spectators.instance().addListener(plugin, (player, spectator) -> {
         if (spectator) {
            this.showUI(player);
         } else {
            Texteria2D.removeGroup("bw.s.*", new Player[]{player});
         }

      });
   }

   public Player[] getWatchers() {
      Set<Player> list = Spectators.instance().getSpectators();
      return (Player[])list.toArray(new Player[list.size()]);
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.MONITOR
   )
   private void onPlayerDamage(EntityDamageEvent event) {
      if (event.getEntityType() == EntityType.PLAYER) {
         Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> this.updateHealth((Player)event.getEntity()));
      }

   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.MONITOR
   )
   private void onPlayerRegen(EntityRegainHealthEvent event) {
      if (event.getEntityType() == EntityType.PLAYER) {
         Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> this.updateHealth((Player)event.getEntity()));
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   private void onPlayerDeathMonitor(EntityDeathEvent event) {
      if (event.getEntityType() == EntityType.PLAYER) {
         PlayerInfo info = PlayerInfo.get((Player)event.getEntity());
         if (info.team != null) {
            Texteria2D.add(((Rectangle)((Rectangle)((Rectangle)((Rectangle)(new Rectangle((String)null, 24)).setAttachment((new Attachment("bw.s.p." + info.username, Position.TOP_LEFT)).setOrientation(Position.BOTTOM_RIGHT).setRemoveWhenParentRemove(false))).setDuration(1000L)).setFadeFinish(900)).setFadeStart(100)).setColor(-769226), this.getWatchers());
            this.updateHealth((Player)event.getEntity(), 0.0F);
         }
      }

   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   private void onPlayerDeathLow(EntityDeathEvent event) {
      if (event.getEntityType() == EntityType.PLAYER) {
         PlayerInfo info = PlayerInfo.get((Player)event.getEntity());
         if (info.team != null && info.team.bedBreaked) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> this.showUI(this.getWatchers()));
         }
      }

   }

   @EventHandler
   private void onPlayerRespawn(PlayerRespawnEvent event) {
      PlayerInfo info = PlayerInfo.get(event.getPlayer());
      if (info.team != null) {
         this.updateHealth(event.getPlayer(), 1.0F);
      }

   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   private void onPlayerLeave(PlayerLeaveEvent event) {
      PlayerInfo info = PlayerInfo.get(event.getPlayer());
      if (info.team != null) {
         Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> this.showUI(this.getWatchers()));
      }

   }

   @EventHandler
   private void onTexteriaClick(TexteriaCallbackEvent event) {
      String module = event.getData().getString("module");
      if (module != null && module.equals("bw-tournament-ui") && this.plugin.spectators.contains(event.getPlayer())) {
         switch (event.getData().getString("action", "")) {
            case "tp":
               Player target = Bukkit.getPlayerExact(event.getData().getString("player"));
               if (target != null) {
                  event.getPlayer().teleport(target);
               }
         }
      }

   }

   private void updateHealth(Player player) {
      this.updateHealth(player, (float)(player.getHealth() / player.getMaxHealth()));
   }

   private void updateHealth(Player player, float value) {
      ByteMap map = new ByteMap();
      map.put("p", value);
      Texteria2D.edit("bw.s.p." + player.getName() + ".hp", map, this.getWatchers());
   }

   public void showUI(Player... watchers) {
      Texteria2D.removeGroup("bw.s.*", watchers);
      TexteriaBuffer buffer = new TexteriaBuffer();
      buffer.enable();

      for(int i = 0; i < Config.teams.size(); ++i) {
         BWTeam team = (BWTeam)Config.teams.get(i);
         int width = team.players.isEmpty() ? 0 : team.players.size() * 25 - 1;
         int height = 3;
         switch (i) {
            case 0:
               this.showTeam(buffer, team, (Rectangle)((Rectangle)(new Rectangle("bw.s.team0", width, height)).setPosition(Position.TOP)).setOffset(team.players.isEmpty() ? -10 : -30 - width / 2, 0));
               break;
            case 1:
               this.showTeam(buffer, team, (Rectangle)((Rectangle)(new Rectangle("bw.s.team1", width, height)).setPosition(Position.TOP)).setOffset(team.players.isEmpty() ? 10 : 30 + width / 2, 0));
               break;
            default:
               if (i % 2 == 0) {
                  this.showTeam(buffer, team, (Rectangle)((Rectangle)(new Rectangle("bw.s.team" + i, width, height)).setAttachment(new Attachment("bw.s.team" + (i - 2), Position.LEFT))).setOffset(team.players.isEmpty() ? 0 : -20, 0));
               } else {
                  this.showTeam(buffer, team, (Rectangle)((Rectangle)(new Rectangle("bw.s.team" + i, width, height)).setAttachment(new Attachment("bw.s.team" + (i - 2), Position.RIGHT))).setOffset(team.players.isEmpty() ? 0 : 20, 0));
               }
         }
      }

      buffer.add(((Text)((Text)((Text)((Text)(new TextTimer("bw.s.timer", new String[]{"{M}:{SS}"})).setFade(0)).setScale(2.0F)).setOffset(0, 6)).setDuration(this.plugin.game.endTime - System.currentTimeMillis())).setPosition(Position.TOP), new Player[0]);
      buffer.send(watchers);
   }

   private void showTeam(TexteriaBuffer buffer, BWTeam team, Rectangle bound) {
      Attachment attachment = (new Attachment(bound.id, Position.BOTTOM_LEFT)).setOrientation(Position.BOTTOM_RIGHT).setRemoveWhenParentRemove(false);

      for(int i = 0; i < team.players.size(); ++i) {
         PlayerInfo info = (PlayerInfo)team.players.get(i);
         String playerId = "bw.s.p." + info.username;
         ByteMap clickData = new ByteMap();
         clickData.put("module", "bw-tournament-ui");
         clickData.put("action", "tp");
         clickData.put("player", info.username);
         buffer.add(((Rectangle)((Rectangle)((Rectangle)(new Image(playerId, 24, "http://skin.vimeworld.ru/head/" + info.username + "/8.png")).setOffset(25 * i, 1)).setAttachment(attachment)).setFade(0)).setOnClick(new OnClick(Action.CALLBACK, clickData)), new Player[0]);
         buffer.add(((Rectangle)((Rectangle)((Rectangle)(new ProgressBar(playerId + ".hp", 24, 2, (float)(info.player.getHealth() / info.player.getMaxHealth()))).setBarColor(IntColor.setAlpha(-10395295, 150)).setColor(-8978685)).setOffset(25 * i, 26)).setAttachment(attachment)).setFade(0), new Player[0]);
      }

      bound.setColor(this.getTeamColor(team.color));
      bound.setFade(0);
      buffer.add(bound, new Player[0]);
   }

   private int getTeamColor(Color color) {
      if (color == Color.RED) {
         return -769226;
      } else if (color == Color.BLUE) {
         return -14575885;
      } else {
         return color == Color.GREEN ? -8978685 : -16777216 + color.asRGB();
      }
   }
}
