/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  gnu.trove.map.TIntObjectMap
 *  gnu.trove.map.TShortObjectMap
 *  gnu.trove.map.hash.TIntObjectHashMap
 *  gnu.trove.map.hash.TShortObjectHashMap
 *  net.minecraft.server.v1_6_R3.ItemStack
 *  net.minecraft.server.v1_6_R3.NBTBase
 *  net.minecraft.server.v1_6_R3.NBTTagCompound
 *  net.minecraft.server.v1_6_R3.NBTTagList
 *  net.minecraft.server.v1_6_R3.NBTTagString
 *  org.bukkit.Color
 *  org.bukkit.Material
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.LeatherArmorMeta
 *  org.bukkit.inventory.meta.SkullMeta
 *  org.bukkit.potion.PotionEffect
 */
package net.xtrafrancyz.VimeNetwork.api.util;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TShortObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TShortObjectHashMap;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.minecraft.server.v1_6_R3.NBTBase;
import net.minecraft.server.v1_6_R3.NBTTagCompound;
import net.minecraft.server.v1_6_R3.NBTTagList;
import net.minecraft.server.v1_6_R3.NBTTagString;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

public class Items {
    private Items() {
    }

    public static ItemStack parse(String str) {
        Material type;
        String typeStr;
        String[] args = str.trim().split(" ");
        short meta = 0;
        if (args[0].contains(":")) {
            String[] split1 = args[0].split(":");
            typeStr = split1[0];
            meta = Short.parseShort(split1[1]);
        } else {
            typeStr = args[0];
        }
        try {
            type = Material.getMaterial((int)Integer.parseInt(typeStr));
        }
        catch (Exception ex) {
            type = Material.getMaterial((String)typeStr.toUpperCase());
        }
        if (type == null) {
            System.out.println("Cannot parse item '" + str + "'");
            return new ItemStack(Material.AIR);
        }
        int start = 1;
        int amount = 1;
        if (args.length > 1) {
            try {
                amount = Integer.parseInt(args[1]);
                start = 2;
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        ItemStack is = new ItemStack(type, amount, meta);
        block14: for (int i = start; i < args.length; ++i) {
            String[] vals = args[i].split("=", 2);
            if (vals.length != 2) continue;
            switch (vals[0]) {
                case "color": {
                    is = Items.paint(is, Color.fromRGB((int)Integer.parseInt(vals[1].charAt(0) == '#' ? vals[1].substring(1) : vals[1], 16)));
                    continue block14;
                }
                case "name": {
                    is = Items.name(is, vals[1], new String[0]);
                    continue block14;
                }
                case "lore": {
                    is = Items.setLore(is, vals[1].replace("_", " ").split("\\^"));
                }
            }
        }
        return is;
    }

    public static ItemStack glow(Material type) {
        return Items.glow(new ItemStack(type));
    }

    public static ItemStack glow(ItemStack is) {
        return Items.nbt(is).set("ench", (NBTBase)new NBTTagList()).build();
    }

    public static ItemStack head(String player) {
        ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, 3);
        SkullMeta sm = (SkullMeta)is.getItemMeta();
        sm.setOwner(player);
        is.setItemMeta((ItemMeta)sm);
        return is;
    }

    public static NBT nbt(ItemStack is) {
        return new NBT(is);
    }

    public static ItemStack menuTitle(Material mat, String title, String ... lore) {
        return Items.menuTitle(new ItemStack(mat), title, lore);
    }

    public static ItemStack menuTitle(ItemStack is, String title, String ... lore) {
        return Items.name(is, "&f>> &e&l" + title + " &f<<", lore);
    }

    public static ItemStack name(Material mat, String name, String ... lore) {
        return Items.name(new ItemStack(mat), name, lore);
    }

    public static ItemStack name(ItemStack is, String name, String ... lore) {
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(U.colored(name));
        if (lore.length > 0) {
            im.setLore(Arrays.asList(U.colored(lore)));
        }
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack name(Material mat, String name, List<String> lore) {
        return Items.name(new ItemStack(mat), name, lore);
    }

    public static ItemStack name(ItemStack is, String name, List<String> lore) {
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(U.colored(name));
        if (!lore.isEmpty()) {
            im.setLore(U.colored(lore));
        }
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack setLore(ItemStack is, String ... lore) {
        if (lore.length == 0) {
            return is;
        }
        return Items.setLore(is, Arrays.asList(lore));
    }

    public static ItemStack setLore(ItemStack is, List<String> lore) {
        ItemMeta im = is.getItemMeta();
        im.setLore(U.colored(lore));
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack appendLore(ItemStack is, String ... lore) {
        if (lore.length == 0) {
            return is;
        }
        return Items.appendLore(is, Arrays.asList(lore));
    }

    public static ItemStack appendLore(ItemStack is, List<String> lore) {
        ItemMeta im = is.getItemMeta();
        List<String> lore0 = im.getLore();
        if (lore0 == null) {
            lore0 = U.colored(lore);
        } else {
            lore0.addAll(U.colored(lore));
        }
        im.setLore(lore0);
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack prependLore(ItemStack is, String ... lore) {
        if (lore.length == 0) {
            return is;
        }
        return Items.prependLore(is, Arrays.asList(lore));
    }

    public static ItemStack prependLore(ItemStack is, List<String> lore) {
        ItemMeta im = is.getItemMeta();
        List<String> lore0 = im.getLore();
        if (lore0 == null) {
            lore0 = U.colored(lore);
        } else {
            lore0.addAll(0, U.colored(lore));
        }
        im.setLore(lore0);
        is.setItemMeta(im);
        return is;
    }

    public static String getLore(ItemStack is, int index) {
        ItemMeta im = is.getItemMeta();
        if (!im.hasLore()) {
            return null;
        }
        List lore = im.getLore();
        if (index < 0) {
            index += lore.size();
        }
        if (index < 0 || index >= lore.size()) {
            return null;
        }
        return (String)lore.get(index);
    }

    public static String getName(ItemStack is) {
        return Namer.getName(is);
    }

    public static String getFullName(ItemStack is) {
        return Namer.getFullName(is);
    }

    public static ItemStack enchant(Material type, Object ... params) {
        return Items.enchant(new ItemStack(type), params);
    }

    public static ItemStack enchant(ItemStack is, Object ... params) {
        if (params.length % 2 == 1) {
            throw new IllegalArgumentException("params must be as pairs of {Enchantment, level}");
        }
        for (int i = 0; i < params.length; i += 2) {
            is.addUnsafeEnchantment((Enchantment)params[i], ((Integer)params[i + 1]).intValue());
        }
        return is;
    }

    public static ItemStack paint(ItemStack is, Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta)is.getItemMeta();
        meta.setColor(color);
        is.setItemMeta((ItemMeta)meta);
        return is;
    }

    public static class Namer {
        private static final TIntObjectMap<TShortObjectMap<String>> ITEMS = new TIntObjectHashMap();
        private static final TIntObjectMap<String> ENCHANTMENTS = new TIntObjectHashMap();
        private static final TIntObjectMap<String> EFFECTS = new TIntObjectHashMap();
        public static final String[] ROMAN_NUMBERS = new String[]{"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};

        static String getFullName(ItemStack is) {
            StringBuilder sb = new StringBuilder(Namer.getName(is));
            Map ench = is.getEnchantments();
            if (!ench.isEmpty()) {
                sb.append(" (");
                boolean first = true;
                for (Map.Entry entry : ench.entrySet()) {
                    if (!first) {
                        sb.append(", ");
                    } else {
                        first = false;
                    }
                    sb.append((String)ENCHANTMENTS.get(((Enchantment)entry.getKey()).getId())).append(' ');
                    if ((Integer)entry.getValue() >= 10) {
                        sb.append(entry.getValue());
                        continue;
                    }
                    sb.append(ROMAN_NUMBERS[(Integer)entry.getValue() - 1]);
                }
                sb.append(')');
            }
            if (is.getAmount() > 1) {
                sb.append(" x").append(is.getAmount());
            }
            return sb.toString();
        }

        public static String[] getFullNameMultilined(ItemStack is) {
            LinkedList<String> list = new LinkedList<String>();
            String name = Namer.getName(is);
            if (is.getAmount() > 1) {
                name = name + " x" + is.getAmount();
            }
            list.add(name);
            for (Map.Entry entry : is.getEnchantments().entrySet()) {
                list.add("  &9" + (String)ENCHANTMENTS.get(((Enchantment)entry.getKey()).getId()) + " " + ROMAN_NUMBERS[(Integer)entry.getValue() - 1]);
            }
            return list.toArray(new String[list.size()]);
        }

        static String getName(ItemStack is) {
            String name;
            ItemMeta im;
            TShortObjectMap map = (TShortObjectMap)ITEMS.get(is.getTypeId());
            ItemMeta itemMeta = im = is.hasItemMeta() ? is.getItemMeta() : null;
            if (im != null && im.hasDisplayName()) {
                name = im.getDisplayName();
            } else if (map == null) {
                name = "#err_item_" + is.getTypeId();
            } else {
                name = (String)map.get(is.getDurability());
                if (name == null) {
                    name = (String)map.get((short)-1);
                }
            }
            return name;
        }

        public static String getEffectName(PotionEffect effect) {
            String name = (String)EFFECTS.get(effect.getType().getId());
            if (name == null) {
                name = "#err_effect_" + effect.getType().getId();
            }
            name = name + " " + ROMAN_NUMBERS[effect.getAmplifier()];
            if (effect.getDuration() >= 20) {
                name = name + " \u043d\u0430 " + effect.getDuration() / 20 + " \u0441\u0435\u043a.";
            }
            return name;
        }

        static {
            YamlConfiguration conf = YamlConfiguration.loadConfiguration((InputStream)VNPlugin.instance().getResource("assets/items.yml"));
            for (String key : conf.getKeys(false)) {
                int id = Integer.parseInt(key);
                TShortObjectHashMap names = new TShortObjectHashMap();
                ITEMS.put(id, (Object)names);
                Object o = conf.get(key);
                if (o instanceof String) {
                    names.put((short)-1, (Object)((String)o));
                    continue;
                }
                for (Map.Entry entry : ((ConfigurationSection)o).getValues(false).entrySet()) {
                    names.put(Short.parseShort((String)entry.getKey()), (Object)((String)entry.getValue()));
                }
            }
            conf = YamlConfiguration.loadConfiguration((InputStream)VNPlugin.instance().getResource("assets/enchantments.yml"));
            for (String key : conf.getKeys(false)) {
                ENCHANTMENTS.put(Integer.parseInt(key), (Object)conf.getString(key));
            }
            conf = YamlConfiguration.loadConfiguration((InputStream)VNPlugin.instance().getResource("assets/effects.yml"));
            for (String key : conf.getKeys(false)) {
                EFFECTS.put(Integer.parseInt(key), (Object)conf.getString(key));
            }
        }
    }

    public static class NBT {
        private final net.minecraft.server.v1_6_R3.ItemStack nms;

        private NBT(ItemStack is) {
            this.nms = CraftItemStack.asNMSCopy((ItemStack)is);
            if (!this.nms.hasTag()) {
                this.nms.setTag(new NBTTagCompound("tag"));
            }
        }

        private NBTTagCompound getTag(String path, boolean write) {
            if (path.contains(".")) {
                String[] parts = path.split("\\.");
                NBTTagCompound t = this.nms.getTag();
                for (int i = 0; i < parts.length - 1; ++i) {
                    NBTTagCompound t0 = t.getCompound(parts[i]);
                    if (write && t.hasKey(parts[i])) {
                        t.setCompound(parts[i], t0);
                    }
                    t = t0;
                }
                return t;
            }
            return this.nms.getTag();
        }

        private String getKey(String path) {
            int index = path.lastIndexOf(46);
            if (index == -1) {
                return path;
            }
            return path.substring(index + 1);
        }

        public NBT set(String path, NBTBase val) {
            this.getTag(path, true).set(this.getKey(path), val);
            return this;
        }

        public NBT setByte(String path, byte val) {
            this.getTag(path, true).setByte(this.getKey(path), val);
            return this;
        }

        public NBT setByteArray(String path, byte[] val) {
            this.getTag(path, true).setByteArray(this.getKey(path), val);
            return this;
        }

        public NBT setBoolean(String path, boolean val) {
            this.getTag(path, true).setBoolean(this.getKey(path), val);
            return this;
        }

        public NBT setDouble(String path, double val) {
            this.getTag(path, true).setDouble(this.getKey(path), val);
            return this;
        }

        public NBT setFloat(String path, float val) {
            this.getTag(path, true).setFloat(this.getKey(path), val);
            return this;
        }

        public NBT setInt(String path, int val) {
            this.getTag(path, true).setInt(this.getKey(path), val);
            return this;
        }

        public NBT setIntArray(String path, int[] val) {
            this.getTag(path, true).setIntArray(this.getKey(path), val);
            return this;
        }

        public NBT setLong(String path, long val) {
            this.getTag(path, true).setLong(this.getKey(path), val);
            return this;
        }

        public NBT setShort(String path, short val) {
            this.getTag(path, true).setShort(this.getKey(path), val);
            return this;
        }

        public NBT setString(String path, String val) {
            this.getTag(path, true).setString(this.getKey(path), val);
            return this;
        }

        public NBT setStringList(String path, List<String> val) {
            String key = this.getKey(path);
            NBTTagList nbtList = new NBTTagList(key);
            for (String s : val) {
                nbtList.add((NBTBase)new NBTTagString("", s));
            }
            this.getTag(path, true).set(key, (NBTBase)nbtList);
            return this;
        }

        public NBTBase get(String path) {
            return this.getTag(path, false).get(this.getKey(path));
        }

        public byte getByte(String path) {
            return this.getTag(path, false).getByte(this.getKey(path));
        }

        public byte[] getByteArray(String path) {
            return this.getTag(path, false).getByteArray(this.getKey(path));
        }

        public boolean getBoolean(String path) {
            return this.getTag(path, false).getBoolean(this.getKey(path));
        }

        public double getDouble(String path) {
            return this.getTag(path, false).getDouble(this.getKey(path));
        }

        public float getFloat(String path) {
            return this.getTag(path, false).getFloat(this.getKey(path));
        }

        public int getInt(String path) {
            return this.getTag(path, false).getInt(this.getKey(path));
        }

        public int[] getIntArray(String path) {
            return this.getTag(path, false).getIntArray(this.getKey(path));
        }

        public long getLong(String path) {
            return this.getTag(path, false).getLong(this.getKey(path));
        }

        public short getShort(String path) {
            return this.getTag(path, false).getShort(this.getKey(path));
        }

        public String getString(String path) {
            return this.getTag(path, false).getString(this.getKey(path));
        }

        public List<String> getStringList(String path) {
            NBTTagList nbtList = (NBTTagList)this.get(path);
            if (nbtList == null || nbtList.size() == 0) {
                return new ArrayList<String>();
            }
            ArrayList<String> list = new ArrayList<String>(nbtList.size());
            for (int i = 0; i < nbtList.size(); ++i) {
                list.add(((NBTTagString)nbtList.get((int)i)).data);
            }
            return list;
        }

        public NBT remove(String path) {
            this.getTag(path, false).remove(this.getKey(path));
            return this;
        }

        public boolean contains(String path) {
            return this.getTag(path, false).hasKey(this.getKey(path));
        }

        public NBTTagCompound getHandle() {
            return this.nms.getTag();
        }

        public ItemStack build() {
            return CraftItemStack.asCraftMirror((net.minecraft.server.v1_6_R3.ItemStack)this.nms);
        }
    }

    public static class Safe {
        public static final String DISPLAY_NAME = "display.Name";
        public static final String DISPLAY_LORE = "display.Lore";

        public static ItemStack name(ItemStack is, String name) {
            return Items.nbt(is).setString(DISPLAY_NAME, U.colored(name)).build();
        }

        public static ItemStack lore(ItemStack is, String ... lore) {
            return Safe.lore(is, Arrays.asList(lore));
        }

        public static ItemStack lore(ItemStack is, List<String> lore) {
            return Items.nbt(is).setStringList(DISPLAY_LORE, U.colored(lore)).build();
        }

        public static ItemStack appendLore(ItemStack is, String ... lore) {
            return Safe.appendLore(is, Arrays.asList(lore));
        }

        public static ItemStack appendLore(ItemStack is, List<String> lore) {
            NBT nbt = Items.nbt(is);
            List<String> list = nbt.getStringList(DISPLAY_LORE);
            list.addAll(U.colored(lore));
            return nbt.setStringList(DISPLAY_LORE, list).build();
        }

        public static ItemStack prependLore(ItemStack is, String ... lore) {
            return Safe.prependLore(is, Arrays.asList(lore));
        }

        public static ItemStack prependLore(ItemStack is, List<String> lore) {
            NBT nbt = Items.nbt(is);
            List<String> list = nbt.getStringList(DISPLAY_LORE);
            list.addAll(0, U.colored(lore));
            return nbt.setStringList(DISPLAY_LORE, list).build();
        }
    }
}

