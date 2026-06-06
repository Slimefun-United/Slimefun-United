package com.xzavier0722.mc.plugin.slimefun4.storage.controller;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.LocationUtils;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;
import lombok.ToString;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

/**
 * Data container used by Slimefun to store machine and block item state.
 * <p>Contains the associated key-value storage, location information, and optional menu data.
 * This is the primary storage class for block data in Slimefun.</p>
 *
 * @author Xzavier0722
 *
 * @see ASlimefunDataContainer
 */
@ToString
public class SlimefunBlockData extends ASlimefunDataContainer {
    private final Location location;
    private volatile BlockMenu menu;

    @ParametersAreNonnullByDefault
    SlimefunBlockData(Location location, String sfId) {
        super(LocationUtils.getLocKey(location), sfId);
        this.location = location;
    }

    @ParametersAreNonnullByDefault
    SlimefunBlockData(Location location, SlimefunBlockData other) {
        super(LocationUtils.getLocKey(location), other, other.getSfId());
        this.location = location;
    }

    @Nonnull
    public Location getLocation() {
        return location;
    }

    @Nonnull
    public String getSfId() {
        return super.getSfId();
    }

    @ParametersAreNullableByDefault
    void setBlockMenu(BlockMenu blockMenu) {
        menu = blockMenu;
    }

    @Nullable public BlockMenu getBlockMenu() {
        return menu;
    }

    @Nullable public ItemStack[] getMenuContents() {
        if (menu == null) {
            return null;
        }
        var re = new ItemStack[54];
        var presetSlots = menu.getPreset().getPresetSlots();
        var inv = menu.toInventory().getContents();
        for (var i = 0; i < inv.length; i++) {
            if (presetSlots.contains(i)) {
                continue;
            }
            re[i] = inv[i];
        }

        return re;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void scheduleUpdateData(String key) {
        Slimefun.getDatabaseManager().getBlockDataController().scheduleDelayedBlockDataUpdate(this, key);
    }
}
