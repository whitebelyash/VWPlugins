/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.Packet
 *  net.minecraft.server.v1_6_R3.Packet205ClientCommand
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.command.CommandSender
 *  org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.api.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.server.v1_6_R3.Packet;
import net.minecraft.server.v1_6_R3.Packet205ClientCommand;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class U {
    private static final Pattern CLEANUP_REDUNDANT_COLORS_REGEX = Pattern.compile("\u00a7[0-9a-fk-or]((?: *\u00a7[0-9a-fk-or])*)( *\u00a7[0-9a-fr])");

    private U() {
    }

    public static String plurals(int n, String form1, String form2, String form3) {
        if (n == 0) {
            return form3;
        }
        if ((n = Math.abs(n) % 100) > 10 && n < 20) {
            return form3;
        }
        if ((n %= 10) > 1 && n < 5) {
            return form2;
        }
        if (n == 1) {
            return form1;
        }
        return form3;
    }

    public static String pluralsCoins(int coins) {
        return coins + U.plurals(coins, " \u043a\u043e\u0438\u043d", " \u043a\u043e\u0438\u043d\u0430", " \u043a\u043e\u0438\u043d\u043e\u0432");
    }

    public static int limit(int min, int value, int max) {
        if (min > max) {
            throw new IllegalArgumentException("'min' must be less then 'max'");
        }
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static long limit(long min, long value, long max) {
        if (min > max) {
            throw new IllegalArgumentException("'min' must be less then 'max'");
        }
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static double avg(double a, double b) {
        return a < b ? a + (b - a) / 2.0 : b + (a - b) / 2.0;
    }

    public static float avg(float a, float b) {
        return a < b ? a + (b - a) / 2.0f : b + (a - b) / 2.0f;
    }

    public static String trimToLength(String str, int length) {
        if (str.length() > length) {
            return str.substring(0, length);
        }
        return str;
    }

    public static void respawnPlayer(Player player) {
        U.respawnPlayer(player, 15L);
    }

    public static void respawnPlayer(Player player, long delayTicks) {
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)VNPlugin.instance(), () -> {
            if (!VimeNetwork.isPlayerOnline(player)) {
                return;
            }
            Packet205ClientCommand packet = new Packet205ClientCommand();
            packet.a = 1;
            ((CraftPlayer)player).getHandle().playerConnection.a(packet);
        }, delayTicks);
    }

    public static void removeArrows(Player player) {
        ((CraftPlayer)player).getHandle().getDataWatcher().watch(9, (Object)0);
    }

    public static void msg(CommandSender cs, String ... msg) {
        cs.sendMessage(U.colored(msg));
    }

    public static void msg(CommandSender cs, List<String> msg) {
        for (String str : msg) {
            cs.sendMessage(U.colored(str));
        }
    }

    public static void bcast(String msg) {
        Bukkit.broadcastMessage((String)U.colored(msg));
    }

    public static String colored(String str) {
        if (str == null) {
            return null;
        }
        return ChatColor.translateAlternateColorCodes((char)'&', (String)str);
    }

    public static String[] colored(String ... lines) {
        if (lines == null) {
            return null;
        }
        for (int i = 0; i < lines.length; ++i) {
            lines[i] = U.colored(lines[i]);
        }
        return lines;
    }

    public static List<String> colored(List<String> lines) {
        ListIterator<String> it = lines.listIterator();
        while (it.hasNext()) {
            it.set(U.colored(it.next()));
        }
        return lines;
    }

    public static String normalizeColors(String str) {
        Matcher matcher = CLEANUP_REDUNDANT_COLORS_REGEX.matcher(str);
        StringBuffer sb = null;
        while (matcher.find()) {
            if (sb == null) {
                sb = new StringBuffer(str.length() - 2);
            }
            String truncated = matcher.group(1);
            int spaces = 0;
            for (char c : truncated.toCharArray()) {
                if (c != ' ') continue;
                ++spaces;
            }
            String valid = matcher.group(2);
            matcher.appendReplacement(sb, valid);
            if (spaces <= 0) continue;
            StringBuilder spacesStr = new StringBuilder(spaces);
            for (int i = 0; i < spaces; ++i) {
                spacesStr.append(' ');
            }
            sb.insert(sb.length() - 2, spacesStr);
        }
        if (sb != null) {
            matcher.appendTail(sb);
            return sb.toString();
        }
        return str;
    }

    public static void sendPacket(Player player, Packet packet) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }

    public static Location parseLocation(World world, String loc) {
        try {
            String[] data = loc.split(",");
            Location bloc = new Location(world, Double.parseDouble(data[0].trim()), Double.parseDouble(data[1].trim()), Double.parseDouble(data[2].trim()));
            if (data.length > 3) {
                bloc.setYaw(Float.parseFloat(data[3].trim()));
            }
            if (data.length > 4) {
                bloc.setPitch(Float.parseFloat(data[4].trim()));
            }
            return bloc;
        }
        catch (Exception e) {
            VNPlugin.instance().getLogger().log(Level.SEVERE, null, e);
            return new Location(world, 0.0, 0.0, 0.0);
        }
    }

    public static List<Location> parseLocations(World world, List<String> locs) {
        return locs.stream().map(loc -> U.parseLocation(world, loc)).collect(Collectors.toList());
    }

    public static List<Player> getNearbyPlayers(Location loc, double radius) {
        LinkedList<Player> list = new LinkedList<Player>();
        radius *= radius;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!(player.getLocation().distanceSquared(loc) < radius)) continue;
            list.add(player);
        }
        return list;
    }

    public static Location center(Collection<Location> locs) {
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;
        World w = null;
        for (Location loc : locs) {
            if (w == null) {
                w = loc.getWorld();
            }
            x += loc.getX();
            y += loc.getY();
            z += loc.getZ();
        }
        return new Location(w, x / (double)locs.size(), y / (double)locs.size(), z / (double)locs.size());
    }

    public static String genBar(int length, float progress, char c, String background, String filled) {
        StringBuilder sb = new StringBuilder(length + 4);
        sb.append(U.colored(filled));
        boolean filled0 = false;
        for (int i = 0; i < length; ++i) {
            if (!filled0 && (float)length * progress <= (float)i) {
                sb.append(U.colored(background));
                filled0 = true;
            }
            sb.append(c);
        }
        return sb.toString();
    }
}

