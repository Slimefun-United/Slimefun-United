package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import io.github.bakedlibs.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import javax.annotation.Nonnull;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class ItemIdCommand extends SubCommand {
    protected ItemIdCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "id", false);
    }

    @Override
    public void onExecute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        if (sender instanceof Player p) {
            if (sender.hasPermission("slimefun.command.id")) {
                var item = p.getInventory().getItemInMainHand();
                if (item.getType() != Material.AIR) {
                    var sfItem = SlimefunItem.getByItem(item);
                    if (sfItem != null) {
                        var sfId = sfItem.getId();
                        var msg = new TextComponent("The ID of this item is: ");
                        var idMsg = new TextComponent(sfId);
                        idMsg.setUnderlined(true);
                        idMsg.setItalic(true);
                        idMsg.setColor(ChatColor.GRAY);
                        idMsg.setHoverEvent(
                                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to copy to clipboard")));
                        idMsg.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, sfId));
                        sender.spigot().sendMessage(msg, idMsg);
                    } else {
                        Slimefun.getLocalization().sendMessage(sender, "messages.invalid-item-in-hand", true);
                    }
                } else {
                    sender.sendMessage(ChatColors.color("&bPlease hold the item you want to check in your main hand!"));
                }
            } else {
                Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
            }
        } else {
            Slimefun.getLocalization().sendMessage(sender, "messages.only-players", true);
        }
    }
}
