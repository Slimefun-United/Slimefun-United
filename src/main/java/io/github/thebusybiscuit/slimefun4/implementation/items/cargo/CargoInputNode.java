package io.github.thebusybiscuit.slimefun4.implementation.items.cargo;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.bakedlibs.dough.items.ItemStackFactory;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.utils.HeadTexture;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class CargoInputNode extends AbstractFilterNode {

    private static final int[] BORDER = {
        0, 1, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 17, 18, 22, 23, 26, 27, 31, 32, 33, 34, 35, 36, 40, 44, 45, 46,
        47, 48, 49, 50, 51, 52, 53
    };

    private static final String ROUND_ROBIN_MODE = "round-robin";
    private static final String SMART_FILL_MODE = "smart-fill";

    @ParametersAreNonnullByDefault
    public CargoInputNode(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            ItemStack recipeOutput) {
        super(itemGroup, item, recipeType, recipe, recipeOutput);
    }

    @Override
    protected int[] getBorder() {
        return BORDER;
    }

    @Override
    protected void onPlace(BlockPlaceEvent e) {
        super.onPlace(e);

        var l = e.getBlock().getLocation();
        StorageCacheUtils.setData(l, ROUND_ROBIN_MODE, String.valueOf(false));
        StorageCacheUtils.setData(l, SMART_FILL_MODE, String.valueOf(false));
    }

    @Override
    protected void updateBlockMenu(BlockMenu menu, Block b) {
        super.updateBlockMenu(menu, b);

        var blockData = StorageCacheUtils.getBlock(b.getLocation());
        String roundRobinMode = blockData.getData(ROUND_ROBIN_MODE);
        if (roundRobinMode == null || roundRobinMode.equals(String.valueOf(false))) {
            menu.replaceExistingItem(
                    24,
                    ItemStackFactory.create(
                            SlimefunUtils.getCustomHead(HeadTexture.ENERGY_REGULATOR.getTexture()),
                            "&7Round-Robin Mode: &4\u2718",
                            "",
                            "&e> Click to enable Round Robin Mode",
                            "&e(Items will be equally distributed on the Channel)"));
            menu.addMenuClickHandler(24, (p, slot, item, action) -> {
                StorageCacheUtils.setData(b.getLocation(), ROUND_ROBIN_MODE, String.valueOf(true));
                updateBlockMenu(menu, b);
                return false;
            });
        } else {
            menu.replaceExistingItem(
                    24,
                    ItemStackFactory.create(
                            SlimefunUtils.getCustomHead(HeadTexture.ENERGY_REGULATOR.getTexture()),
                            "&7Round-Robin Mode: &2\u2714",
                            "",
                            "&e> Click to disable Round Robin Mode",
                            "&e(Items will be equally distributed on the Channel)"));
            menu.addMenuClickHandler(24, (p, slot, item, action) -> {
                StorageCacheUtils.setData(b.getLocation(), ROUND_ROBIN_MODE, String.valueOf(false));
                updateBlockMenu(menu, b);
                return false;
            });
        }

        String smartFillNode = blockData.getData(SMART_FILL_MODE);

        // FIXME translation still needs refinement

        if (smartFillNode == null || smartFillNode.equals(String.valueOf(false))) {
            menu.replaceExistingItem(
                    16,
                    ItemStackFactory.create(
                            Material.WRITABLE_BOOK,
                            "&7\"Smart-Filling\" Mode: &4\u2718",
                            "",
                            "&e> Click to enable \"Smart-Filling\" Mode",
                            "",
                            "&fIn this mode, the Cargo node will attempt",
                            "&fto keep a constant amount of items",
                            "&fin the inventory. This is not perfect",
                            "&fand will still fill in empty slots that",
                            "&fcome before a stack of a configured item."));
            menu.addMenuClickHandler(16, (p, slot, item, action) -> {
                StorageCacheUtils.setData(b.getLocation(), SMART_FILL_MODE, String.valueOf(true));
                updateBlockMenu(menu, b);
                return false;
            });
        } else {
            menu.replaceExistingItem(
                    16,
                    ItemStackFactory.create(
                            Material.WRITTEN_BOOK,
                            "&7\"Smart-Filling\" Mode: &2\u2714",
                            "",
                            "&e> Click to disable \"Smart-Filling\" Mode",
                            "",
                            "&fIn this mode, the Cargo node will attempt",
                            "&fto keep a constant amount of items",
                            "&fin the inventory. This is not perfect",
                            "&fand will still fill in empty slots that",
                            "&fcome before a stack of a configured item."));
            menu.addMenuClickHandler(16, (p, slot, item, action) -> {
                StorageCacheUtils.setData(b.getLocation(), SMART_FILL_MODE, String.valueOf(false));
                updateBlockMenu(menu, b);
                return false;
            });
        }
    }
}
