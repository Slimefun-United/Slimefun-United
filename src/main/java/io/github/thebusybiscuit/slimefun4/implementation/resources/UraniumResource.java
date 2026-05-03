package io.github.thebusybiscuit.slimefun4.implementation.resources;

import city.norain.slimefun4.SlimefunExtended;
import io.github.bakedlibs.dough.versions.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.geo.GEOResource;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.utils.biomes.BiomeMap;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;

/**
 * A {@link GEOResource} which consists of small chunks of Uranium.
 *
 * @author TheBusyBiscuit
 *
 */
class UraniumResource extends AbstractResource {

    private static final int DEFAULT_OVERWORLD_VALUE = 4;

    private final BiomeMap<Integer> biomes;

    UraniumResource() {
        super("uranium", "Small Chunks of Uranium", SlimefunItems.SMALL_URANIUM.item(), 2, true);

        MinecraftVersion version = SlimefunExtended.getMinecraftVersion();

        if (version.isAtLeast(1, 18)) {
            // 1.18+ renamed most biomes
            biomes = getBiomeMap(this, "/biome-maps/uranium_v1.18.json");
        } else if (version.isAtLeast(1, 17)) {
            // 1.17+ introduced cave biomes
            biomes = getBiomeMap(this, "/biome-maps/uranium_v1.17.json");
        } else {
            biomes = getBiomeMap(this, "/biome-maps/uranium_v1.16.json");
        }
    }

    @Override
    public int getDefaultSupply(Environment environment, Biome biome) {
        if (environment != Environment.NORMAL) {
            return 0;
        } else {
            return biomes.getOrDefault(biome, DEFAULT_OVERWORLD_VALUE);
        }
    }
}
