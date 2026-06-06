package city.norain.slimefun4.compatibillty;

import city.norain.slimefun4.SlimefunExtended;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;

@UtilityClass
public class CompatibilityUtil {
    /**
     * Returns the item material the player used to place this block.
     * For most blocks this matches {@link BlockData#getMaterial()}, but some blocks rely on a different material when
     * being placed.
     * Note: this does not cover every special block data type in existence.
     *
     * @param blockData the block data to inspect
     * @return the material used when placing this block
     */
    public Material getPlacementMaterial(BlockData blockData) {
        if (SlimefunExtended.isAtLeast(1, 19, 4)) {
            return blockData.getPlacementMaterial();
        } else {
            switch (blockData.getMaterial()) {
                case PLAYER_WALL_HEAD -> {
                    return Material.PLAYER_HEAD;
                }
                case REDSTONE_WIRE -> {
                    return Material.REDSTONE;
                }
                default -> {
                    var mat = blockData.getMaterial();
                    var enumName = blockData.getMaterial().name();

                    if (Ageable.class.equals(mat.data) && enumName.endsWith("S")) {
                        var itemMat = Material.getMaterial(enumName.substring(0, enumName.length() - 1));
                        return itemMat != null && itemMat.isItem() ? itemMat : mat;
                    }

                    if (WallSign.class.equals(mat.data) && enumName.contains("_WALL_")) {
                        Material itemMat = Material.getMaterial(enumName.replace("_WALL_", "_"));

                        if (itemMat != null && itemMat.isItem()) {
                            return mat;
                        }
                    }

                    // Fallback to original material
                    return blockData.getMaterial();
                }
            }
        }
    }

    /**
     * Checks whether the player is still considered connected.
     * Prior to Minecraft 1.20 this cannot be guaranteed; instead we fall back to the online status only.
     *
     * @param player the offline player instance
     * @return {@code true} if the connection for that player is active
     */
    public boolean isConnected(OfflinePlayer player) {
        if (SlimefunExtended.isAtLeast(1, 20) && Slimefun.instance().getServer().getOnlineMode()) {
            return player.isConnected();
        } else {
            return player.isOnline();
        }
    }
}
