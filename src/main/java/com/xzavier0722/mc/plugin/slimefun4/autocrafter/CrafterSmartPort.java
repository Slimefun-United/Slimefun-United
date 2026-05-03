package com.xzavier0722.mc.plugin.slimefun4.autocrafter;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.bakedlibs.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;
import java.util.List;
import javax.annotation.Nonnull;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CrafterSmartPort extends SlimefunItem {

    public static final int[] INPUT_SLOTS = {
        0, 1, 2, 3, 4, 5, 9, 10, 11, 12, 13, 14, 18, 19, 20, 21, 22, 23, 27, 28, 29, 30, 31, 32, 36, 37, 38, 39, 40, 41,
        45, 46, 47, 48, 49, 50
    };
    public static final int[] OUTPUT_SLOTS = {7, 8, 16, 17, 25, 26, 34, 35, 43, 44, 52, 53};

    public CrafterSmartPort(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        new BlockMenuPreset("CRAFTER_SMART_PORT", "&aAuto-Crafter Smart Interface") {
            @Override
            public void init() {
                addItem(6, getCountItem(), (p, slot, item, action) -> false);

                for (int i = 15; i < 54; i += 9) {
                    addItem(i, ChestMenuUtils.getBackground(), (p, slot, item, action) -> false);
                }

                for (int slot : OUTPUT_SLOTS) {
                    addMenuClickHandler(slot, new AdvancedMenuClickHandler() {
                        @Override
                        public boolean onClick(Player p, int slot, ItemStack cursor, ClickAction action) {
                            return false;
                        }

                        @Override
                        public boolean onClick(
                                InventoryClickEvent e, Player p, int slot, ItemStack cursor, ClickAction action) {
                            return cursor == null || cursor.getType().isAir();
                        }
                    });
                }
            }

            @Override
            public boolean canOpen(@Nonnull Block b, @Nonnull Player p) {
                return p.hasPermission("slimefun.inventory.bypass")
                        || Slimefun.getProtectionManager()
                                .hasPermission(p, b.getLocation(), Interaction.INTERACT_BLOCK);
            }

            @Override
            public void newInstance(@Nonnull BlockMenu menu, @Nonnull Block b) {
                // Resume the ingredient count
                String countStr = StorageCacheUtils.getData(b.getLocation(), "ingredientCount");
                if (countStr != null) {
                    menu.getItemInSlot(6).setAmount(Math.max(1, Integer.parseInt(countStr)));
                }
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(DirtyChestMenu menu, ItemTransportFlow flow, ItemStack item) {
                if (flow == ItemTransportFlow.WITHDRAW) return OUTPUT_SLOTS;

                ItemStackWrapper wrapper = ItemStackWrapper.wrap(item);
                int amountLimit = INPUT_SLOTS.length / menu.getItemInSlot(6).getAmount() * wrapper.getMaxStackSize();

                // Check the current amount
                int itemAmount = wrapper.getAmount();
                for (int slot : INPUT_SLOTS) {
                    ItemStack itemInSlot = menu.getItemInSlot(slot);
                    if (SlimefunUtils.isItemSimilar(itemInSlot, wrapper, true, false)
                            && (itemAmount += itemInSlot.getAmount()) > amountLimit) {
                        // Amount has reached the limited, just return.
                        return new int[0];
                    }
                }

                return INPUT_SLOTS;
            }
        };

        CrafterInteractorManager.register(getId(), CrafterSmartPortParser::new);
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockBreakHandler(false, true) {
            @Override
            public void onPlayerBreak(
                    @Nonnull BlockBreakEvent e, @Nonnull ItemStack item, @Nonnull List<ItemStack> drops) {
                BlockMenu inv = StorageCacheUtils.getMenu(e.getBlock().getLocation());
                if (inv != null) {
                    for (int slot : INPUT_SLOTS) {
                        ItemStack itemInSlot = inv.getItemInSlot(slot);
                        if (itemInSlot != null) drops.add(itemInSlot);
                    }

                    for (int slot : OUTPUT_SLOTS) {
                        ItemStack itemInSlot = inv.getItemInSlot(slot);
                        if (itemInSlot != null) drops.add(itemInSlot);
                    }
                }
            }
        });
    }

    private ItemStack getCountItem() {
        ItemStack countItem = new ItemStack(Material.CLOCK);
        ItemMeta im = countItem.getItemMeta();
        im.setDisplayName(ChatColor.BLUE + "Crafting table ingredient count");
        countItem.setItemMeta(im);
        return countItem;
    }

    @Override
    public boolean loadDataByDefault() {
        return true;
    }
}
