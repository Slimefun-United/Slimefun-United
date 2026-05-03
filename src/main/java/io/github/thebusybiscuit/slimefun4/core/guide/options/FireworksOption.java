package io.github.thebusybiscuit.slimefun4.core.guide.options;

import io.github.bakedlibs.dough.data.persistent.PersistentDataAPI;
import io.github.bakedlibs.dough.items.ItemStackFactory;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.config.SlimefunConfigManager;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

class FireworksOption implements SlimefunGuideOption<Boolean> {

    @Override
    public SlimefunAddon getAddon() {
        return Slimefun.instance();
    }

    @Override
    public NamespacedKey getKey() {
        return new NamespacedKey(Slimefun.instance(), "research_fireworks");
    }

    // TODO: Merge it with the commented-out code
    /*
    @Override
    public Optional<ItemStack> getDisplayItem(Player p, ItemStack guide) {
        SlimefunRegistry registry = Slimefun.getRegistry();

        if (registry.isResearchingEnabled() && registry.isResearchFireworkEnabled()) {
            boolean enabled = getSelectedOption(p, guide).orElse(true);

            String optionState = enabled ? "enabled" : "disabled";
            List<String> lore = Slimefun.getLocalization().getMessages(p, "guide.options.fireworks." + optionState + ".text");
            lore.add("");
            lore.add("&7\u21E8 " + Slimefun.getLocalization().getMessage(p, "guide.options.fireworks." + optionState + ".click"));

            ItemStack item = ItemStackFactory.create(Material.FIREWORK_ROCKET, lore);
            return Optional.of(item);
        } else {
            return Optional.empty();
        }
    }
     */
    @Override
    public Optional<ItemStack> getDisplayItem(Player p, ItemStack guide) {
        SlimefunConfigManager cfgManager = Slimefun.getConfigManager();

        if (cfgManager.isResearchingEnabled() && cfgManager.isResearchFireworkEnabled()) {
            boolean enabled = getSelectedOption(p, guide).orElse(true);
            ItemStack item = ItemStackFactory.create(
                    Material.FIREWORK_ROCKET,
                    "&bFirework Effect: &" + (enabled ? "aEnabled" : "4Disabled"),
                    "",
                    "&7You can now choose whether",
                    "&7to show a firework effect",
                    "&7when unlocking a new item.",
                    "",
                    "&7\u21E8 &eClick to " + (enabled ? "disable" : "enable") + " firework effect");
            return Optional.of(item);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void onClick(Player p, ItemStack guide) {
        setSelectedOption(p, guide, !getSelectedOption(p, guide).orElse(true));
        SlimefunGuideSettings.openSettings(p, guide);
    }

    @Override
    public Optional<Boolean> getSelectedOption(Player p, ItemStack guide) {
        NamespacedKey key = getKey();
        boolean value = !PersistentDataAPI.hasByte(p, key) || PersistentDataAPI.getByte(p, key) == (byte) 1;
        return Optional.of(value);
    }

    @Override
    public void setSelectedOption(Player p, ItemStack guide, Boolean value) {
        PersistentDataAPI.setByte(p, getKey(), value.booleanValue() ? (byte) 1 : (byte) 0);
    }
}
