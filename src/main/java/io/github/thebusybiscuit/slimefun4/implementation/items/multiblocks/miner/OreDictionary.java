package io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.miner;

import io.github.bakedlibs.dough.versions.MinecraftVersion;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Simple interface to map ore blocks to their respective item(s).
 *
 * @author TheBusyBiscuit
 *
 */
interface OreDictionary {

    @Nonnull
    @ParametersAreNonnullByDefault
    ItemStack getDrops(Material material, Random random);

    static @Nonnull OreDictionary forVersion(@Nonnull MinecraftVersion version) {
        if (version.isAtLeast(1, 17)) {
            // MC 1.17 - 1.18
            return new OreDictionary17();
        } else {
            // MC 1.16
            return new OreDictionary16();
        }
    }
}
