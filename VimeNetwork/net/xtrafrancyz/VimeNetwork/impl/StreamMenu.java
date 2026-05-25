package net.xtrafrancyz.VimeNetwork.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.Core.network.packet.Packet57StreamStatus;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.VTexteria;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import net.xtrafrancyz.bukkit.texteria.Texteria;
import net.xtrafrancyz.bukkit.texteria.utils.ParsedTime;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class StreamMenu implements IMenu {
   private Inventory inv = Bukkit.createInventory(this, 36, "Активные стримы");
   private List list = new ArrayList();
   private Map byStreamer = new HashMap();
   private long start = System.currentTimeMillis();
   private boolean dirty = true;
   private int oldSize = 0;

   public StreamMenu() {
      this.inv.setItem(3, Items.name(Material.PAPER, "&aЧто это такое?", " &fЗдесь отображаются стримы,", "&fкоторые в данный момент идут", "&fна проекте."));
      this.inv.setItem(4, Items.name(Material.PAPER, "&aКто может добавить стрим?", " &fДобавлять стримы могут только,", "&fигроки со статусом &cYouTube", "&fили &3администраторы&f."));
      this.inv.setItem(5, Items.name(Material.PAPER, "&aЗачем это всё нужно?", " &fМногие игроки и не знают что", "&fна сервере играет множество популярных", "&fличностей, которые также хотят", "&fпоиграть и пообщаться с игроками.", " &fВы сможете посмотреть активные", "&fстримы и \"попасть в телик\"."));
      Bukkit.getScheduler().scheduleSyncRepeatingTask(VNPlugin.instance(), this::streamNotifier, 6000L, 12000L);
   }

   private Player[] getNotificablePlayers() {
      List<Player> players = new ArrayList(VPlayer.PLAYERS.size());

      for(VPlayer player : VPlayer.PLAYERS.values()) {
         if (player.settings.get(3)) {
            players.add(player.player);
         }
      }

      return (Player[])players.toArray(new Player[players.size()]);
   }

   private void streamNotifier() {
      if (this.list.size() > 0) {
         Player[] players = this.getNotificablePlayers();
         VTexteria.showStreamMessage(this.list, players);
         String msg = U.colored(T.success("VimeWorld", "На сервере идёт " + this.list.size() + " " + U.plurals(this.list.size(), "стрим", "стрима", "стримов") + "! Напишите &l/streams&a для просмотра"));

         for(Player player : players) {
            player.sendMessage(msg);
         }
      }

   }

   public void update(Packet57StreamStatus packet) {
      if (packet.status == Packet57StreamStatus.Status.OFFLINE) {
         StreamerData streamer = (StreamerData)this.byStreamer.get(packet.owner);
         if (streamer != null) {
            streamer.removeStream(packet.url);
            if (streamer.streams.isEmpty()) {
               this.byStreamer.remove(packet.owner);
               this.list.remove(streamer);
            }

            this.dirty = true;
         }
      } else {
         boolean newStreamer = false;
         StreamerData streamer = (StreamerData)this.byStreamer.get(packet.owner);
         if (streamer == null) {
            this.byStreamer.put(packet.owner, streamer = new StreamerData(packet.owner));
            this.list.add(streamer);
            newStreamer = true;
         }

         streamer.updateStream(packet);
         this.dirty = true;
         if (newStreamer && System.currentTimeMillis() - this.start > 20000L) {
            Player[] players = this.getNotificablePlayers();
            VTexteria.showNewStreamer(streamer, players);
            String platform = ((StreamData)streamer.sortedStreams().get(0)).platform;
            if (platform.equals("ВКонтакте")) {
               platform = "во " + platform;
            } else {
               platform = "на " + platform;
            }

            String msg = U.colored(T.success(streamer.owner, "Новый стрим " + platform + "! Напишите &l/streams&a для просмотра"));

            for(Player player : players) {
               player.sendMessage(msg);
            }
         }
      }

      if (!this.inv.getViewers().isEmpty()) {
         this.update();
      }

   }

   private void update() {
      if (this.dirty) {
         Collections.sort(this.list);
         int size = Math.max(this.list.size(), this.oldSize);

         for(int i = 9; i < size + 9; ++i) {
            ItemStack is = null;
            if (i - 9 < this.list.size()) {
               StreamerData streamer = (StreamerData)this.list.get(i - 9);
               String title;
               if (streamer.getTitle() == null) {
                  title = "&bСтрим " + streamer.owner;
               } else {
                  title = "&b" + streamer.getTitle();
               }

               if (title.length() > 35) {
                  title = title.substring(0, 33) + "...";
               }

               ParsedTime time = new ParsedTime((long)(streamer.getDuration() * 1000));
               String duration;
               if (time.days > 0) {
                  duration = time.days + " д. " + time.hours + " ч.";
               } else {
                  duration = ParsedTime.numToString(time.hours, 2) + ":" + ParsedTime.numToString(time.minutes, 2) + " ч.";
               }

               is = Items.name(Material.EMERALD, title, "&fСтример: &c" + streamer.owner, "&fЗрителей: &a" + streamer.getTotalViewers(), "&fСервис: &e" + (String)streamer.sortedStreams().stream().map((s) -> s.platform).distinct().collect(Collectors.joining("&f, &e")), "&fДлительность: &e" + duration, "", " &aНажмите для просмотра");
            }

            this.inv.setItem(i, is);
         }

         this.oldSize = this.list.size();
      }

   }

   public void clear() {
      this.list.clear();
      this.byStreamer.clear();

      for(int i = 9; i < this.inv.getSize(); ++i) {
         this.inv.setItem(i, (ItemStack)null);
      }

   }

   public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
      if (slot > 8) {
         slot -= 9;
         if (this.list.size() <= slot) {
            return;
         }

         List<StreamData> streams = ((StreamerData)this.list.get(slot)).streams;
         if (streams.size() == 1) {
            Texteria.openUrl(((StreamData)streams.get(0)).url, new Player[]{player});
         } else {
            (new PlatformSelectMenu((StreamerData)this.list.get(slot))).show(player);
         }
      }

   }

   public void show(Player player) {
      this.update();
      IMenu.super.show(player);
   }

   public Inventory getInventory() {
      return this.inv;
   }

   private static class PlatformSelectMenu implements IMenu {
      private Inventory inv;
      public List streams;

      public PlatformSelectMenu(StreamerData streamer) {
         this.inv = Bukkit.createInventory(this, 9, "Стримы " + streamer.owner);
         this.streams = new ArrayList(streamer.sortedStreams());

         for(int i = 0; i < this.streams.size(); ++i) {
            StreamData stream = (StreamData)this.streams.get(i);
            String title;
            if (stream.title == null) {
               title = "&bСтрим " + stream.owner;
            } else {
               title = "&b" + stream.title;
            }

            if (title.length() > 35) {
               title = title.substring(0, 33) + "...";
            }

            ParsedTime time = new ParsedTime((long)stream.duration * 1000L);
            ItemStack is = Items.name(Material.DIAMOND, title, "&fСтример: &c" + stream.owner, "&fЗрителей: &a" + stream.viewers, "&fСервис: &e" + stream.platform, "&fДлительность: &e" + ParsedTime.numToString(time.hours, 2) + ":" + ParsedTime.numToString(time.minutes, 2) + " ч.", "", " &aНажмите для просмотра");
            this.inv.setItem(i, is);
         }

      }

      public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
         if (slot > 8) {
            slot -= 9;
            if (this.streams.size() <= slot) {
               return;
            }

            Texteria.openUrl(((StreamData)this.streams.get(slot)).url, new Player[]{player});
         }

      }

      public Inventory getInventory() {
         return this.inv;
      }
   }

   public static class StreamData implements Comparable {
      public String url;
      public String owner;
      public String title;
      public String platform;
      public int viewers = 0;
      public int duration = 0;

      public StreamData(String url, String owner) {
         this.url = url;
         this.owner = owner;
      }

      public boolean equals(Object obj) {
         return obj instanceof StreamData && ((StreamData)obj).url.equals(this.url);
      }

      public int compareTo(StreamData o) {
         return o.viewers - this.viewers;
      }
   }

   public static class StreamerData implements Comparable {
      public List streams;
      public String owner;

      public StreamerData(String owner) {
         this.owner = owner;
         this.streams = new LinkedList();
      }

      public int getDuration() {
         return this.streams.stream().mapToInt((s) -> s.duration).max().orElse(0);
      }

      public int getTotalViewers() {
         return this.streams.stream().mapToInt((s) -> s.viewers).sum();
      }

      public String getTitle() {
         StreamData data = (StreamData)this.sortedStreams().get(0);
         return data != null && data.title != null ? data.title : null;
      }

      public List sortedStreams() {
         return this.streams;
      }

      public void removeStream(String url) {
         Iterator<StreamData> it = this.streams.iterator();

         while(it.hasNext()) {
            if (((StreamData)it.next()).url.equals(url)) {
               it.remove();
               return;
            }
         }

      }

      public void updateStream(Packet57StreamStatus packet) {
         for(StreamData stream : this.streams) {
            if (stream.url.equals(packet.url)) {
               stream.title = packet.title;
               stream.viewers = packet.viewers;
               stream.duration = packet.duration;
               Collections.sort(this.streams);
               return;
            }
         }

         StreamData stream = new StreamData(packet.url, this.owner);
         stream.title = packet.title;
         stream.viewers = packet.viewers;
         stream.platform = packet.platform;
         stream.duration = packet.duration;
         this.streams.add(stream);
         Collections.sort(this.streams);
      }

      public boolean equals(Object obj) {
         return obj instanceof StreamerData && ((StreamerData)obj).owner.equals(this.owner);
      }

      public int compareTo(StreamerData o) {
         return o.getTotalViewers() - this.getTotalViewers();
      }
   }
}
