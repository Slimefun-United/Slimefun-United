package com.xzavier0722.mc.plugin.slimefun4.storage.util;

import city.norain.slimefun4.SlimefunExtended;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtils {
    /**
     * Converts the given {@link Location} into the standardized location-info string used by the database.
     *
     * @param l the location to convert
     * @return the normalized location-info string
     */
    public static String getLocKey(Location l) {
        return l.getWorld().getName() + ";" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ();
    }

    /**
     * Converts the {@link Chunk} into the standardized chunk-info string used by the database.
     *
     * @param chunk the chunk to convert
     * @return the normalized chunk-info string
     */
    public static String getChunkKey(Chunk chunk) {
        return chunk.getWorld().getName() + ";" + chunk.getX() + ":" + chunk.getZ();
    }

    public static String getChunkKey(Location loc) {
        return loc.getWorld().getName() + ";" + (loc.getBlockX() >> 4) + ":" + (loc.getBlockZ() >> 4);
    }

    public static Location toLocation(String lKey) {
        if (lKey == null || lKey.isEmpty()) {
            return null;
        }

        try {
            var strArr = lKey.split(";");
            var loc = strArr[1].split(":");
            return new Location(
                    Bukkit.getWorld(strArr[0]),
                    Double.parseDouble(loc[0]),
                    Double.parseDouble(loc[1]),
                    Double.parseDouble(loc[2]));
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse location [" + lKey + "]", e);
        }
    }

    public static boolean isSameChunk(Chunk c1, Chunk c2) {
        return c1 == c2
                || (isSameWorld(c1.getWorld(), c2.getWorld()) && c1.getX() == c2.getX() && c1.getZ() == c2.getZ());
    }

    public static boolean isSameLoc(Location l1, Location l2) {
        return l1 == l2
                || (isSameChunk(l1.getChunk(), l2.getChunk())
                        && l1.getBlockX() == l2.getBlockX()
                        && l1.getBlockY() == l2.getBlockY()
                        && l1.getBlockZ() == l2.getBlockZ());
    }

    public static Chunk toChunk(World w, String cKey) {
        var loc = cKey.split(";")[1].split(":");
        if (SlimefunExtended.isAtLeast(1, 19, 4)) {
            return w.getChunkAt(Integer.parseInt(loc[0]), Integer.parseInt(loc[1]), false);
        } else {
            return w.getChunkAt(Integer.parseInt(loc[0]), Integer.parseInt(loc[1]));
        }
    }

    public static boolean isSameWorld(World w1, World w2) {
        return w1.getName().equals(w2.getName());
    }

    /**
     * Converts the location into a human-readable string.
     *
     * <p>Note: Do not use this during database conversions!</p>
     *
     * @param location the location to convert
     * @return a human-readable location string
     */
    public static String locationToString(Location location) {
        if (location == null) {
            return "null";
        }

        return "[world="
                + location.getWorld().getName()
                + ",x="
                + location.getX()
                + ",y="
                + location.getY()
                + ",z="
                + location.getZ()
                + "]";
    }
}
