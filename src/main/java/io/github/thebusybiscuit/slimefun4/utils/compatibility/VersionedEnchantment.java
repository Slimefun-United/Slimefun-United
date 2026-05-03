package io.github.thebusybiscuit.slimefun4.utils.compatibility;

import city.norain.slimefun4.SlimefunExtended;
import io.github.bakedlibs.dough.versions.MinecraftVersion;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;

// https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/browse/src/main/java/org/bukkit/craftbukkit/legacy/FieldRename.java?until=2a6207fe150b6165722fce94c83cc1f206620ab5&untilPath=src%2Fmain%2Fjava%2Forg%2Fbukkit%2Fcraftbukkit%2Flegacy%2FFieldRename.java#86-110
public class VersionedEnchantment {
    public static final Enchantment EFFICIENCY;
    public static final Enchantment UNBREAKING;
    public static final Enchantment PROTECTION;
    public static final Enchantment SHARPNESS;
    public static final Enchantment LUCK_OF_THE_SEA;
    public static final Enchantment AQUA_AFFINITY;
    public static final Enchantment FORTUNE;

    static {
        MinecraftVersion version = SlimefunExtended.getMinecraftVersion();

        // DIG_SPEED is renamed to EFFICIENCY in 1.20.5
        EFFICIENCY = version.isAtLeast(1, 20, 5) ? Enchantment.EFFICIENCY : getKey("DIG_SPEED");

        // DURABILITY is renamed to UNBREAKING in 1.20.5
        UNBREAKING = version.isAtLeast(1, 20, 5) ? Enchantment.UNBREAKING : getKey("DURABILITY");

        // PROTECTION_ENVIRONMENTAL is renamed to PROTECTION in 1.20.5
        PROTECTION = version.isAtLeast(1, 20, 5) ? Enchantment.PROTECTION : getKey("PROTECTION_ENVIRONMENTAL");

        // DAMAGE_ALL is renamed to SHARPNESS in 1.20.5
        SHARPNESS = version.isAtLeast(1, 20, 5) ? Enchantment.SHARPNESS : getKey("DAMAGE_ALL");

        // LUCK is renamed to LUCK_OF_THE_SEA in 1.20.5
        LUCK_OF_THE_SEA = version.isAtLeast(1, 20, 5) ? Enchantment.LUCK_OF_THE_SEA : getKey("LUCK");

        // WATER_WORKER is renamed to AQUA_AFFINITY in 1.20.5
        AQUA_AFFINITY = version.isAtLeast(1, 20, 5) ? Enchantment.AQUA_AFFINITY : getKey("WATER_WORKER");

        // LOOT_BONUS_BLOCKS is renamed to FORTUNE in 1.20.5
        FORTUNE = version.isAtLeast(1, 20, 5) ? Enchantment.FORTUNE : getKey("LOOT_BONUS_BLOCKS");
    }

    @Nullable private static Enchantment getKey(@Nonnull String key) {
        return Registry.ENCHANTMENT.get(NamespacedKey.minecraft(key));
    }
}
