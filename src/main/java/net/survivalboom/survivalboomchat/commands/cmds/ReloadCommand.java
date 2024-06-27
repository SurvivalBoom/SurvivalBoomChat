package net.survivalboom.survivalboomchat.commands.cmds;

import net.survivalboom.survivalboomchat.SurvivalBoomChat;
import net.survivalboom.survivalboomchat.chats.ChatManager;
import net.survivalboom.survivalboomchat.chats.Mentions;
import net.survivalboom.survivalboomchat.configuration.PluginMessages;
import net.survivalboom.survivalboomchat.moderation.ModerationManager;
import net.survivalboom.survivalboomchat.pm.PMCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class ReloadCommand {

    public static void command(@NotNull CommandSender sender) throws IOException, InvalidConfigurationException {

        if (!sender.hasPermission("sbchat.reload")) {
            PluginMessages.sendMessage(sender, PluginMessages.getMessage("no-permission").replace("{PERMISSION}", "sbchat.reload"));
            return;
        }

        File dataFolder = SurvivalBoomChat.getPlugin().getDataFolder();
        PluginMessages.reload(new File(dataFolder, "messages.yml"));
        SurvivalBoomChat.getPlugin().getConfig().load(new File(dataFolder, "config.yml"));

        Mentions.reload();
        PMCommand.reload();

        if (ChatManager.reload() || ModerationManager.reload()) {
            PluginMessages.sendMessage(sender, PluginMessages.getMessage("reload-error"));
            return;
        }

        PluginMessages.sendMessage(sender, PluginMessages.getMessage("reload-success"));

    }

}
