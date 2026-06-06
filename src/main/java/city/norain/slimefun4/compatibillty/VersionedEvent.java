package city.norain.slimefun4.compatibillty;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.ExplosionResult;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;

@UtilityClass
public class VersionedEvent {
    private Constructor<BlockExplodeEvent> BLOCK_EXPLODE_EVENT_CONSTRUCTOR;

    private Method GET_TOP_INVENTORY;
    private Method GET_CLICKED_INVENTORY;

    public void init() {
        if (Slimefun.getMinecraftVersion().isBefore(MinecraftVersion.MINECRAFT_1_21)) {
            try {
                BLOCK_EXPLODE_EVENT_CONSTRUCTOR =
                        BlockExplodeEvent.class.getConstructor(Block.class, List.class, float.class);

                GET_TOP_INVENTORY =
                        Class.forName("org.bukkit.inventory.InventoryView").getMethod("getTopInventory");
                GET_TOP_INVENTORY.setAccessible(true);

                GET_CLICKED_INVENTORY = Class.forName("org.bukkit.event.inventory.InventoryClickEvent")
                        .getMethod("getClickedInventory");
                GET_CLICKED_INVENTORY.setAccessible(true);

            } catch (NoSuchMethodException | ClassNotFoundException e) {
                Slimefun.logger()
                        .log(
                                Level.WARNING,
                                "Unable to initialize event version compatibility module. Some features may not work correctly.",
                                e);
            }
        }
    }

    @SneakyThrows
    public BlockExplodeEvent newBlockExplodeEvent(Block block, List<Block> affectedBlock, float yield) {
        if (Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_21)) {
            return new BlockExplodeEvent(block, block.getState(), affectedBlock, yield, ExplosionResult.DESTROY);
        } else {
            if (BLOCK_EXPLODE_EVENT_CONSTRUCTOR == null) {
                throw new IllegalStateException("Unable to create BlockExplodeEvent: missing constructor");
            }
            return BLOCK_EXPLODE_EVENT_CONSTRUCTOR.newInstance(block, affectedBlock, yield);
        }
    }

    /**
     * See https://www.spigotmc.org/threads/inventoryview-changed-to-interface-backwards-compatibility.651754/
     */
    @SneakyThrows
    public Inventory getTopInventory(InventoryEvent event) {
        if (Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_21)) {
            return event.getView().getTopInventory();
        } else {
            if (GET_TOP_INVENTORY == null) {
                throw new IllegalStateException("Unable to get top inventory: missing method");
            }

            return (Inventory) GET_TOP_INVENTORY.invoke(event.getView());
        }
    }

    @SneakyThrows
    public Inventory getClickedInventory(InventoryClickEvent event) {
        if (Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_21)) {
            return event.getClickedInventory();
        } else {
            if (GET_CLICKED_INVENTORY == null) {
                throw new IllegalStateException("Unable to get clicked inventory: missing method");
            }

            return (Inventory) GET_CLICKED_INVENTORY.invoke(event);
        }
    }
}
