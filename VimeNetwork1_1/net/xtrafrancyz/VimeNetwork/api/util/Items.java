package net.xtrafrancyz.VimeNetwork.api.util;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TShortObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TShortObjectHashMap;
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
      String[] args = str.trim().split(" ");
      short meta = 0;
      String typeStr;
      if (args[0].contains(":")) {
         String[] split1 = args[0].split(":");
         typeStr = split1[0];
         meta = Short.parseShort(split1[1]);
      } else {
         typeStr = args[0];
      }

      Material type;
      try {
         type = Material.getMaterial(Integer.parseInt(typeStr));
      } catch (Exception var14) {
         type = Material.getMaterial(typeStr.toUpperCase());
      }

      if (type == null) {
         System.out.println("Cannot parse item '" + str + "'");
         return new ItemStack(Material.AIR);
      } else {
         int start = 1;
         int amount = 1;
         if (args.length > 1) {
            try {
               amount = Integer.parseInt(args[1]);
               start = 2;
            } catch (NumberFormatException var13) {
            }
         }

         ItemStack is = new ItemStack(type, amount, meta);

         for(int i = start; i < args.length; ++i) {
            String[] vals = args[i].split("=", 2);
            if (vals.length == 2) {
               switch (vals[0]) {
                  case "color":
                     LeatherArmorMeta lam = (LeatherArmorMeta)is.getItemMeta();
                     lam.setColor(Color.fromRGB(Integer.parseInt(vals[1].charAt(0) == '#' ? vals[1].substring(1) : vals[1], 16)));
                     is.setItemMeta(lam);
                     break;
                  case "name":
                     is = name(is, vals[1]);
                     break;
                  case "lore":
                     is = setLore(is, vals[1].replace("_", " ").split("\\^"));
               }
            }
         }

         return is;
      }
   }

   public static ItemStack glow(Material type) {
      return glow(new ItemStack(type));
   }

   public static ItemStack glow(ItemStack is) {
      return nbt(is).set("ench", new NBTTagList()).build();
   }

   public static ItemStack head(String player) {
      ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
      SkullMeta sm = (SkullMeta)is.getItemMeta();
      sm.setOwner(player);
      is.setItemMeta(sm);
      return is;
   }

   public static NBT nbt(ItemStack is) {
      return new NBT(is);
   }

   public static ItemStack menuTitle(Material mat, String title, String... lore) {
      return menuTitle(new ItemStack(mat), title, lore);
   }

   public static ItemStack menuTitle(ItemStack is, String title, String... lore) {
      return name(is, "&f>> &e&l" + title + " &f<<", lore);
   }

   public static ItemStack name(Material mat, String name, String... lore) {
      return name(new ItemStack(mat), name, lore);
   }

   public static ItemStack name(ItemStack is, String name, String... lore) {
      ItemMeta im = is.getItemMeta();
      im.setDisplayName(U.colored(name));
      if (lore.length > 0) {
         im.setLore(Arrays.asList(U.colored(lore)));
      }

      is.setItemMeta(im);
      return is;
   }

   public static ItemStack name(Material mat, String name, List lore) {
      return name(new ItemStack(mat), name, lore);
   }

   public static ItemStack name(ItemStack is, String name, List lore) {
      ItemMeta im = is.getItemMeta();
      im.setDisplayName(U.colored(name));
      if (!lore.isEmpty()) {
         im.setLore(U.colored(lore));
      }

      is.setItemMeta(im);
      return is;
   }

   public static ItemStack setLore(ItemStack is, String... lore) {
      return lore.length == 0 ? is : setLore(is, Arrays.asList(lore));
   }

   public static ItemStack setLore(ItemStack is, List lore) {
      ItemMeta im = is.getItemMeta();
      im.setLore(U.colored(lore));
      is.setItemMeta(im);
      return is;
   }

   public static ItemStack appendLore(ItemStack is, String... lore) {
      return lore.length == 0 ? is : appendLore(is, Arrays.asList(lore));
   }

   public static ItemStack appendLore(ItemStack is, List lore) {
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

   public static ItemStack prependLore(ItemStack is, String... lore) {
      return lore.length == 0 ? is : prependLore(is, Arrays.asList(lore));
   }

   public static ItemStack prependLore(ItemStack is, List lore) {
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
      } else {
         List<String> lore = im.getLore();
         if (index < 0) {
            index += lore.size();
         }

         return index >= 0 && index < lore.size() ? (String)lore.get(index) : null;
      }
   }

   public static String getName(ItemStack is) {
      return Items.Namer.getName(is);
   }

   public static String getFullName(ItemStack is) {
      return Items.Namer.getFullName(is);
   }

   public static ItemStack enchant(ItemStack is, Ench... enchantments) {
      for(Ench e : enchantments) {
         is.addUnsafeEnchantment(e.enchantment, e.level);
      }

      return is;
   }

   public static ItemStack enchant(ItemStack is, Object... params) {
      if (params.length % 2 == 1) {
         throw new IllegalArgumentException("params must be as pairs of {Enchantment, level}");
      } else {
         for(int i = 0; i < params.length; i += 2) {
            is.addUnsafeEnchantment((Enchantment)params[i], (Integer)params[i + 1]);
         }

         return is;
      }
   }

   public static class Ench {
      Enchantment enchantment;
      int level;

      public Ench(Enchantment enchantment, int level) {
         this.enchantment = enchantment;
         this.level = level;
      }
   }

   public static class Safe {
      public static final String DISPLAY_NAME = "display.Name";
      public static final String DISPLAY_LORE = "display.Lore";

      public static ItemStack name(ItemStack is, String name) {
         return Items.nbt(is).setString("display.Name", U.colored(name)).build();
      }

      public static ItemStack lore(ItemStack is, String... lore) {
         return lore(is, Arrays.asList(lore));
      }

      public static ItemStack lore(ItemStack is, List lore) {
         return Items.nbt(is).setStringList("display.Lore", U.colored(lore)).build();
      }

      public static ItemStack appendLore(ItemStack is, String... lore) {
         return appendLore(is, Arrays.asList(lore));
      }

      public static ItemStack appendLore(ItemStack is, List lore) {
         NBT nbt = Items.nbt(is);
         List<String> list = nbt.getStringList("display.Lore");
         list.addAll(U.colored(lore));
         return nbt.setStringList("display.Lore", list).build();
      }

      public static ItemStack prependLore(ItemStack is, String... lore) {
         return prependLore(is, Arrays.asList(lore));
      }

      public static ItemStack prependLore(ItemStack is, List lore) {
         NBT nbt = Items.nbt(is);
         List<String> list = nbt.getStringList("display.Lore");
         list.addAll(0, U.colored(lore));
         return nbt.setStringList("display.Lore", list).build();
      }
   }

   public static class NBT {
      private final net.minecraft.server.v1_6_R3.ItemStack nms;

      private NBT(ItemStack is) {
         this.nms = CraftItemStack.asNMSCopy(is);
         if (!this.nms.hasTag()) {
            this.nms.setTag(new NBTTagCompound("tag"));
         }

      }

      private NBTTagCompound getTag(String path, boolean write) {
         if (path.contains(".")) {
            String[] parts = path.split("\\.");
            NBTTagCompound t = this.nms.getTag();

            for(int i = 0; i < parts.length - 1; ++i) {
               NBTTagCompound t0 = t.getCompound(parts[i]);
               if (write && t.hasKey(parts[i])) {
                  t.setCompound(parts[i], t0);
               }

               t = t0;
            }

            return t;
         } else {
            return this.nms.getTag();
         }
      }

      private String getKey(String path) {
         int index = path.lastIndexOf(46);
         return index == -1 ? path : path.substring(index + 1);
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

      public NBT setStringList(String path, List val) {
         String key = this.getKey(path);
         NBTTagList nbtList = new NBTTagList(key);

         for(String s : val) {
            nbtList.add(new NBTTagString("", s));
         }

         this.getTag(path, true).set(key, nbtList);
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

      public List getStringList(String path) {
         NBTTagList nbtList = (NBTTagList)this.get(path);
         if (nbtList != null && nbtList.size() != 0) {
            ArrayList<String> list = new ArrayList(nbtList.size());

            for(int i = 0; i < nbtList.size(); ++i) {
               list.add(((NBTTagString)nbtList.get(i)).data);
            }

            return list;
         } else {
            return new ArrayList();
         }
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
         return CraftItemStack.asCraftMirror(this.nms);
      }
   }

   public static class Namer {
      private static final TIntObjectMap ITEMS = new TIntObjectHashMap();
      private static final TIntObjectMap ENCHANTMENTS = new TIntObjectHashMap();
      private static final TIntObjectMap EFFECTS = new TIntObjectHashMap();
      public static final String[] ROMAN_NUMBERS = new String[]{"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};

      static String getFullName(ItemStack is) {
         StringBuilder sb = new StringBuilder(getName(is));
         Map<Enchantment, Integer> ench = is.getEnchantments();
         if (!ench.isEmpty()) {
            sb.append(" (");
            boolean first = true;

            for(Map.Entry entry : ench.entrySet()) {
               if (!first) {
                  sb.append(", ");
               } else {
                  first = false;
               }

               sb.append((String)ENCHANTMENTS.get(((Enchantment)entry.getKey()).getId())).append(' ');
               if ((Integer)entry.getValue() >= 10) {
                  sb.append(entry.getValue());
               } else {
                  sb.append(ROMAN_NUMBERS[(Integer)entry.getValue() - 1]);
               }
            }

            sb.append(')');
         }

         if (is.getAmount() > 1) {
            sb.append(" x").append(is.getAmount());
         }

         return sb.toString();
      }

      public static String[] getFullNameMultilined(ItemStack is) {
         List<String> list = new LinkedList();
         String name = getName(is);
         if (is.getAmount() > 1) {
            name = name + " x" + is.getAmount();
         }

         list.add(name);

         for(Map.Entry entry : is.getEnchantments().entrySet()) {
            list.add("  &9" + (String)ENCHANTMENTS.get(((Enchantment)entry.getKey()).getId()) + " " + ROMAN_NUMBERS[(Integer)entry.getValue() - 1]);
         }

         return (String[])list.toArray(new String[list.size()]);
      }

      static String getName(ItemStack is) {
         TShortObjectMap<String> map = (TShortObjectMap)ITEMS.get(is.getTypeId());
         ItemMeta im = is.hasItemMeta() ? is.getItemMeta() : null;
         String name;
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
            name = name + " на " + effect.getDuration() / 20 + " сек.";
         }

         return name;
      }

      static {
         YamlConfiguration conf = YamlConfiguration.loadConfiguration(VNPlugin.instance().getResource("assets/items.yml"));

         for(String key : conf.getKeys(false)) {
            int id = Integer.parseInt(key);
            TShortObjectMap<String> names = new TShortObjectHashMap();
            ITEMS.put(id, names);
            Object o = conf.get(key);
            if (o instanceof String) {
               names.put((short)-1, (String)o);
            } else {
               for(Map.Entry entry : ((ConfigurationSection)o).getValues(false).entrySet()) {
                  names.put(Short.parseShort((String)entry.getKey()), (String)entry.getValue());
               }
            }
         }

         conf = YamlConfiguration.loadConfiguration(VNPlugin.instance().getResource("assets/enchantments.yml"));

         for(String key : conf.getKeys(false)) {
            ENCHANTMENTS.put(Integer.parseInt(key), conf.getString(key));
         }

         conf = YamlConfiguration.loadConfiguration(VNPlugin.instance().getResource("assets/effects.yml"));

         for(String key : conf.getKeys(false)) {
            EFFECTS.put(Integer.parseInt(key), conf.getString(key));
         }

      }
   }
}
