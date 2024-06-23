package net.survivalboom.survivalboomchat.commands.cmds;

import net.survivalboom.survivalboomchat.chats.ChatManager;
import net.survivalboom.survivalboomchat.configuration.PluginMessages;
import net.survivalboom.survivalboomchat.moderation.ModerationManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommands {

    public static void command(@NotNull CommandSender sender) {

        if (!sender.hasPermission("sbchat.reload")) {
            PluginMessages.sendMessage(sender, PluginMessages.getMessage("no-permission").replace("{PERMISSION}", "sbchat.reload"));
            return;
        }


        if (ChatManager.reload() || ModerationManager.reload()) {
            PluginMessages.sendMessage(sender, PluginMessages.getMessage("reload-error"));
            return;
        }

        PluginMessages.sendMessage(sender, PluginMessages.getMessage("reload-success"));

    }

}
