package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class DebugFishCommand extends SubCommand {

    @ParametersAreNonnullByDefault
    DebugFishCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "debug_fish", false);
    }

    @Override
    protected @Nonnull String getDescription() {
        return "commands.debug_fish";
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (sender instanceof Player player && sender.hasPermission("slimefun.debugging")) {
            player.getInventory().addItem(SlimefunItems.DEBUG_FISH.item());
        } else {
            Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
        }
    }
}
