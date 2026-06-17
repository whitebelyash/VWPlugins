/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  gnu.trove.map.TIntObjectMap
 *  gnu.trove.map.hash.TIntObjectHashMap
 *  net.xtrafrancyz.VimeNetwork.api.menu.IMenu
 *  net.xtrafrancyz.VimeNetwork.api.util.U
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.ClashPoint.menu.shop;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.HashMap;
import java.util.function.Function;
import net.xtrafrancyz.ClashPoint.menu.shop.CategoryMenu;
import net.xtrafrancyz.ClashPoint.menu.shop.Currency;
import net.xtrafrancyz.ClashPoint.menu.shop.ShopCategory;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class ShopMenu
implements IMenu {
    private final Inventory inv = Bukkit.createInventory((InventoryHolder)this, (int)27, (String)"\u0416\u043b\u043e\u0431\u0441\u043a\u0438\u0439 \u0442\u043e\u0440\u0433\u043e\u0432\u0435\u0446");
    private TIntObjectMap<ShopCategory> categories = new TIntObjectHashMap();

    public ShopMenu() {
        this.fill();
        this.categories.forEachEntry((slot, category) -> {
            this.inv.setItem(slot, category.getMenuItem());
            return true;
        });
    }

    protected abstract void fill();

    protected void category(int slot, ShopCategory category) {
        category.commit();
        this.categories.put(slot, (Object)category);
    }

    public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
        ShopCategory category = (ShopCategory)this.categories.get(slot);
        if (category != null) {
            new CategoryMenu(this, category).show(player);
        }
    }

    public Price diamond(int amount) {
        return new Price(Currency.DIAMOND, amount);
    }

    public Price gold(int amount) {
        return new Price(Currency.GOLD, amount);
    }

    public Price iron(int amount) {
        return new Price(Currency.IRON, amount);
    }

    public Inventory getInventory() {
        return this.inv;
    }

    public static class Item {
        public ItemStack is;
        public Function<Player, Boolean> action;
        public Price price;
        public String listName;

        public Item(ItemStack is, Price price) {
            this.is = is;
            this.price = price;
            this.action = player -> {
                HashMap leftover = player.getInventory().addItem(new ItemStack[]{this.is.clone()});
                return leftover.isEmpty() || ((ItemStack)leftover.values().iterator().next()).getAmount() != this.is.getAmount();
            };
        }

        public Item(ItemStack is, Price price, Function<Player, Boolean> action) {
            this.is = is;
            this.price = price;
            this.action = action;
        }

        public Item setListName(String name) {
            this.listName = name;
            return this;
        }
    }

    public static class Price {
        public Currency currency;
        public int amount;

        public Price(Currency currency, int amount) {
            this.currency = currency;
            this.amount = amount;
        }

        public String getReadable() {
            return this.amount + " " + U.plurals((int)this.amount, (String)this.currency.form1, (String)this.currency.form2, (String)this.currency.form3);
        }
    }
}

