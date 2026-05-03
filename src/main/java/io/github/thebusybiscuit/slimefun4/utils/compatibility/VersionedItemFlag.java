package io.github.thebusybiscuit.slimefun4.utils.compatibility;

import city.norain.slimefun4.SlimefunExtended;
import io.github.bakedlibs.dough.versions.MinecraftVersion;
import java.lang.reflect.Field;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.inventory.ItemFlag;

public class VersionedItemFlag {
    public static final ItemFlag HIDE_ADDITIONAL_TOOLTIP;

    static {
        MinecraftVersion version = SlimefunExtended.getMinecraftVersion();

        HIDE_ADDITIONAL_TOOLTIP =
                version.isAtLeast(1, 20, 5) ? ItemFlag.HIDE_ADDITIONAL_TOOLTIP : getKey("HIDE_POTION_EFFECTS");
    }

    @Nullable private static ItemFlag getKey(@Nonnull String key) {
        try {
            Field field = ItemFlag.class.getDeclaredField(key);
            return (ItemFlag) field.get(null);
        } catch (Exception e) {
            return null;
        }
    }
}
