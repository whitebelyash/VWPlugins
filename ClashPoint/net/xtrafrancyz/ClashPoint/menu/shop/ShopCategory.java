/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.util.Items
 *  net.xtrafrancyz.bukkit.texteria.utils.Visibility
 *  net.xtrafrancyz.bukkit.texteria.utils.Visibility$Always
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 */
package net.xtrafrancyz.ClashPoint.menu.shop;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import net.xtrafrancyz.ClashPoint.game.CPTexteria;
import net.xtrafrancyz.ClashPoint.menu.shop.ShopMenu;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.bukkit.texteria.utils.Visibility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ShopCategory {
    private ItemStack menuItem;
    public final String name;
    public final List<ShopMenu.Item> items;

    public ShopCategory(Material type, String name) {
        this(new ItemStack(type), name);
    }

    public ShopCategory(ItemStack is, String name) {
        this.menuItem = is;
        this.name = name;
        this.items = new ArrayList<ShopMenu.Item>();
    }

    public ShopCategory item(ShopMenu.Price price, Material type, int amount) {
        return this.item(price, new ItemStack(type, amount));
    }

    public ShopCategory item(ShopMenu.Price price, Material type) {
        return this.item(price, new ItemStack(type));
    }

    public ShopCategory item(ShopMenu.Price price, ItemStack is) {
        this.items.add(new ShopMenu.Item(is, price));
        return this;
    }

    public ShopCategory item(ShopMenu.Price price, String listName, ItemStack is) {
        this.items.add(new ShopMenu.Item(is, price).setListName(listName));
        return this;
    }

    public ShopCategory action(ShopMenu.Price price, String listName, ItemStack is, Function<Player, Boolean> action) {
        this.items.add(new ShopMenu.Item(is, price, action).setListName(listName));
        return this;
    }

    public ShopCategory action(ShopMenu.Price price, ItemStack is, Function<Player, Boolean> action) {
        this.items.add(new ShopMenu.Item(is, price, action));
        return this;
    }

    public ShopCategory sword(ShopMenu.Price price, ItemStack is) {
        return this.action(price, is, player -> {
            boolean replaced = ShopCategory.replaceOrAddSwordAction(player, is);
            if (replaced) {
                CPTexteria.showCustomMessage(player, "\u0412\u0430\u0448 \u0441\u0442\u0430\u0440\u044b\u0439 \u043c\u0435\u0447 \u0437\u0430\u043c\u0435\u043d\u0435\u043d \u043d\u0430 \u043d\u043e\u0432\u044b\u0439", -1, 3000L, (Visibility)new Visibility.Always());
            }
            return true;
        });
    }

    public ShopCategory armorSet(ShopMenu.Price price, String listName, ItemStack menuItem, Material[] armor, Object ... enchantments) {
        return this.action(price, listName, menuItem, player -> {
            PlayerInventory inv = player.getInventory();
            long sum = Stream.of(armor).mapToInt(type -> {
                ItemStack is = new ItemStack(type);
                if (enchantments.length != 0) {
                    is = Items.enchant((ItemStack)is, (Object[])enchantments);
                }
                return ShopCategory.wearOrAdd(inv, is);
            }).sum();
            if (sum > 0L) {
                CPTexteria.showCustomMessage(player, "\u0412\u0430\u0448\u0430 \u0441\u0442\u0430\u0440\u0430\u044f \u0431\u0440\u043e\u043d\u044f \u0437\u0430\u043c\u0435\u043d\u0435\u043d\u0430 \u043d\u0430 \u043d\u043e\u0432\u0443\u044e", -1, 3000L, (Visibility)new Visibility.Always());
            }
            return true;
        });
    }

    private static int wearOrAdd(PlayerInventory inv, ItemStack is) {
        ItemStack dressed;
        int armorSlot;
        switch (is.getType()) {
            case CHAINMAIL_HELMET: 
            case GOLD_HELMET: 
            case DIAMOND_HELMET: 
            case IRON_HELMET: 
            case LEATHER_HELMET: {
                armorSlot = 3;
                break;
            }
            case CHAINMAIL_CHESTPLATE: 
            case GOLD_CHESTPLATE: 
            case DIAMOND_CHESTPLATE: 
            case IRON_CHESTPLATE: 
            case LEATHER_CHESTPLATE: {
                armorSlot = 2;
                break;
            }
            case CHAINMAIL_LEGGINGS: 
            case GOLD_LEGGINGS: 
            case DIAMOND_LEGGINGS: 
            case IRON_LEGGINGS: 
            case LEATHER_LEGGINGS: {
                armorSlot = 1;
                break;
            }
            case CHAINMAIL_BOOTS: 
            case GOLD_BOOTS: 
            case DIAMOND_BOOTS: 
            case IRON_BOOTS: 
            case LEATHER_BOOTS: {
                armorSlot = 0;
                break;
            }
            default: {
                armorSlot = -1;
            }
        }
        if (armorSlot != -1 && ((dressed = inv.getItem(inv.getSize() + armorSlot)) == null || "\u041d\u0430\u0447\u0430\u043b\u044c\u043d\u044b\u0439 \u043f\u0440\u0435\u0434\u043c\u0435\u0442".equals(Items.getLore((ItemStack)dressed, (int)-1)))) {
            inv.setItem(inv.getSize() + armorSlot, is);
            return 1;
        }
        inv.addItem(new ItemStack[]{is});
        return 0;
    }

    private static boolean replaceOrAddSwordAction(Player player, ItemStack is) {
        PlayerInventory inv = player.getInventory();
        int slot = -1;
        for (Map.Entry entry : inv.all(Material.WOOD_SWORD).entrySet()) {
            if (!"\u041d\u0430\u0447\u0430\u043b\u044c\u043d\u044b\u0439 \u043f\u0440\u0435\u0434\u043c\u0435\u0442".equals(Items.getLore((ItemStack)((ItemStack)entry.getValue()), (int)0))) continue;
            slot = (Integer)entry.getKey();
            break;
        }
        if (slot == -1) {
            inv.addItem(new ItemStack[]{is});
            return false;
        }
        inv.setItem(slot, is);
        return true;
    }

    void commit() {
        ArrayList<String> lore = new ArrayList<String>();
        HashSet<String> antiDuplicate = new HashSet<String>();
        for (ShopMenu.Item item : this.items) {
            String name = item.listName != null ? item.listName : Items.getName((ItemStack)item.is);
            if (!antiDuplicate.add(name)) continue;
            lore.add("&7- " + name);
        }
        this.menuItem = Items.name((ItemStack)this.menuItem, (String)("&b" + this.name), lore);
    }

    public ItemStack getMenuItem() {
        return this.menuItem;
    }
}

