package net.xtrafrancyz.BedWars.menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import net.xtrafrancyz.BedWars.BWTeam;
import net.xtrafrancyz.BedWars.BedWars;
import net.xtrafrancyz.BedWars.Config;
import net.xtrafrancyz.BedWars.PlayerInfo;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.Rand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TeamSelectMenu implements IMenu {
   private static final int RANDOM_SLOT = 31;
   private final BedWars plugin;
   private final Inventory inv;
   private final BWTeam[] teams;

   public TeamSelectMenu(BedWars plugin) {
      this.plugin = plugin;
      this.inv = Bukkit.createInventory(this, 36, "Выбор команды");
      this.teams = new BWTeam[this.inv.getSize()];
      BWTeam[] tms = (BWTeam[])Config.teams.toArray(new BWTeam[Config.teams.size()]);
      switch (tms.length) {
         case 2:
            this.set(11, tms[0]);
            this.set(15, tms[1]);
            break;
         case 3:
            this.set(11, tms[0]);
            this.set(13, tms[1]);
            this.set(15, tms[2]);
            break;
         case 4:
            this.set(10, tms[0]);
            this.set(12, tms[1]);
            this.set(14, tms[2]);
            this.set(16, tms[3]);
            break;
         case 5:
         case 7:
         default:
            throw new IllegalArgumentException("Number of team must be one of: 2, 3, 4, 6, 8");
         case 6:
            this.set(10, tms[0]);
            this.set(11, tms[1]);
            this.set(12, tms[2]);
            this.set(14, tms[3]);
            this.set(15, tms[4]);
            this.set(16, tms[5]);
            break;
         case 8:
            this.set(9, tms[0]);
            this.set(10, tms[1]);
            this.set(11, tms[2]);
            this.set(12, tms[3]);
            this.set(14, tms[4]);
            this.set(15, tms[5]);
            this.set(16, tms[6]);
            this.set(17, tms[7]);
      }

      this.inv.setItem(31, Items.glow(Items.name(Material.NAME_TAG, "&fСлучайная команда", new String[]{"Нажмите для выбора"})));
      this.update();
   }

   private void set(int slot, BWTeam team) {
      team.slot = slot;
      this.teams[slot] = team;
   }

   public void update() {
      for(BWTeam team : this.teams) {
         if (team != null) {
            this.update(team);
         }
      }

   }

   public void update(BWTeam team) {
      ArrayList<String> lore = new ArrayList(2 + team.players.size());
      if (team.players.size() < Config.teamPlayers) {
         lore.add("Нажмите для выбора");
      }

      if (team.players.size() > 0) {
         lore.add((Object)null);
      }

      lore.addAll((Collection)team.players.stream().map((pi) -> ChatColor.GRAY + pi.player.getDisplayName()).collect(Collectors.toList()));
      this.inv.setItem(team.slot, Items.name(new ItemStack(Material.WOOL, 1, team.wool), team.chatColor + team.names[0] + " команда &f[" + team.players.size() + "/" + Config.teamPlayers + "]", lore));
   }

   public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
      BWTeam team;
      if (slot == 31) {
         List<BWTeam> ts = new LinkedList(Config.teams);
         Iterator<BWTeam> it = ts.iterator();

         while(it.hasNext()) {
            if (((BWTeam)it.next()).players.size() >= Config.teamPlayers) {
               it.remove();
            }
         }

         if (ts.size() == 0) {
            return;
         }

         team = (BWTeam)Rand.of(ts);
      } else {
         team = this.teams[slot];
         if (team == null) {
            return;
         }

         if (team.players.size() >= Config.teamPlayers) {
            return;
         }
      }

      this.plugin.game.join(PlayerInfo.get(player), team, false);
      NetworkPlayer networkPlayer = VimeNetwork.getPlayer(player);
      if (networkPlayer.isPartyLeader()) {
         for(String partyPlayer : networkPlayer.getParty().getPlayers()) {
            if (team.players.size() >= Config.teamPlayers) {
               break;
            }

            PlayerInfo pi = (PlayerInfo)PlayerInfo.PLAYERS.get(partyPlayer);
            if (pi != null && pi.team == null) {
               this.plugin.game.join(pi, team, false);
            }
         }
      }

      player.closeInventory();
   }

   public Inventory getInventory() {
      return this.inv;
   }
}
