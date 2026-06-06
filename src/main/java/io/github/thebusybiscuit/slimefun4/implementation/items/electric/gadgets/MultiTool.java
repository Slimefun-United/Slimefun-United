package io.github.thebusybiscuit.slimefun4.implementation.items.electric.gadgets;

import io.github.bakedlibs.dough.common.ChatColors;
import io.github.bakedlibs.dough.data.persistent.PersistentDataAPI;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable;
import io.github.thebusybiscuit.slimefun4.core.handlers.EntityInteractHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.ToolUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.compatibility.VersionedEntityType;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * The {@link MultiTool} is an electric device which can mimic
 * the behaviour of any other {@link SlimefunItem}.
 *
 * @author TheBusyBiscuit
 */
public class MultiTool extends SlimefunItem implements Rechargeable {

    private static final float COST = 0.3F;
    private static final float SPECIAL_COST = 20F;

    private final List<MultiToolMode> modes = new ArrayList<>();
    private final float capacity;

    private static final NamespacedKey key = new NamespacedKey(Slimefun.instance(), "multitool_mode");
    private static final String LORE_PREFIX = ChatColors.color("&8\u21E8 &7Mode: ");
    private static final Pattern REGEX = Pattern.compile(ChatColors.color("(&c&o)?" + LORE_PREFIX) + "(.+)");

    @ParametersAreNonnullByDefault
    public MultiTool(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            float capacity,
            List<String> items) {
        super(itemGroup, item, recipeType, recipe);

        for (int i = 0; i < items.size(); i++) {
            modes.add(new MultiToolMode(this, i, items.get(i)));
        }

        this.capacity = capacity;
    }

    @Override
    public float getMaxItemCharge(ItemStack item) {
        return capacity;
    }

    private int nextIndex(int i) {
        int index = i;

        do {
            index++;

            if (index >= modes.size()) {
                index = 0;
            }
        } while (index != i && !modes.get(index).isEnabled());

        return index;
    }

    @Nonnull
    protected ItemUseHandler getItemUseHandler() {
        return e -> {
            Player p = e.getPlayer();
            ItemStack item = e.getItem();
            ItemMeta meta = item.getItemMeta();
            e.cancel();

            // Remove old multitool mode ID used by Gugu Project's Slimefun build
            // We use a NamespacedKey called "multitool_mode" instead.
            final NamespacedKey legacyKey = new NamespacedKey(Slimefun.instance(), "MULTI_TOOL_MODE");
            if (PersistentDataAPI.hasInt(meta, key)) PersistentDataAPI.remove(meta, legacyKey);

            // Remove old integer multitool mode used by official Slimefun builds
            // Our new ID is a string instead
            if (PersistentDataAPI.hasInt(meta, key)) PersistentDataAPI.remove(meta, key);

            int index = 0;
            for (int i = 0; i < modes.size(); i++) {
                if (modes.get(i).getItemId().equals(PersistentDataAPI.getString(meta, key))) index = i;
            }

            SlimefunItem sfItem = modes.get(index).getItem();

            if (!p.isSneaking()) {
                if (sfItem != null) {
                    // Elemental Staff - Storm should take the special energy cost
                    boolean isElementalStaffStorm = sfItem.getId().equals("STAFF_ELEMENTAL_STORM");
                    float cost = isElementalStaffStorm ? SPECIAL_COST : COST;

                    if (removeItemCharge(item, cost)) {
                        sfItem.callItemHandler(ItemUseHandler.class, handler -> handler.onRightClick(e));
                    }
                }
            } else {
                index = nextIndex(index);

                SlimefunItem selectedItem = modes.get(index).getItem();
                String itemName = selectedItem != null ? selectedItem.getItemName() : "Unknown";
                Slimefun.getLocalization()
                        .sendMessage(
                                p,
                                "messages.multi-tool.mode-change",
                                true,
                                msg -> msg.replace("%device%", "Multi Tool")
                                        .replace("%mode%", ChatColor.stripColor(itemName)));

                PersistentDataAPI.setString(meta, key, modes.get(index).getItemId());

                List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

                boolean regexMatchFound = false;
                for (int i = 0; i < lore.size(); i++) {
                    String line = lore.get(i);

                    if (REGEX.matcher(line).matches()) {
                        lore.set(i, LORE_PREFIX + ChatColor.stripColor(itemName));
                        regexMatchFound = true;
                        break;
                    }
                }

                if (!regexMatchFound) {
                    lore.add(2, LORE_PREFIX + ChatColor.stripColor(itemName));
                }

                meta.setLore(lore);
                item.setItemMeta(meta);
            }
        };
    }

    @Nonnull
    private ToolUseHandler getToolUseHandler() {
        return (e, tool, fortune, drops) -> {
            // Multi Tools cannot be used as shears
            Slimefun.getLocalization().sendMessage(e.getPlayer(), "messages.multi-tool.not-shears");
            e.setCancelled(true);
        };
    }

    @Nonnull
    private EntityInteractHandler getEntityInteractionHandler() {
        return (e, item, offhand) -> {
            // Fixes #2217 - Prevent them from being used to shear entities
            EntityType type = e.getRightClicked().getType();
            if (type == VersionedEntityType.MOOSHROOM
                    || type == VersionedEntityType.SNOW_GOLEM
                    || type == EntityType.SHEEP) {
                Slimefun.getLocalization().sendMessage(e.getPlayer(), "messages.multi-tool.not-shears");
                e.setCancelled(true);
                e.setCancelled(true);
            }
        };
    }

    @Override
    public void preRegister() {
        super.preRegister();

        addItemHandler(getItemUseHandler());
        addItemHandler(getToolUseHandler());
        addItemHandler(getEntityInteractionHandler());
    }
}
