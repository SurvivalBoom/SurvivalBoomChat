package net.survivalboom.survivalboomchat.commands.cmds;

import net.survivalboom.survivalboomchat.configuration.PluginMessages;
import net.survivalboom.survivalboomchat.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class IgnoreCommand {

    public static void command(@NotNull CommandSender sender, String[] args)  {
        if (!(sender instanceof Player)) {
            Player player = (Player) sender;
            if (!sender.hasPermission("sbchat.ignore")) {
                if (args.length == 0) {
                    Player p = Bukkit.getPlayer(args[0]);
                    if (p.hasPermission("sbchat.bypass")) {
                        PluginMessages.sendMessage(player, PluginMessages.getMessage("ignore-bypass"));
                        return;
                    }
                    if (Database.isPingMuted(player, p)) {
                        Database.mutePing(player, p);
                        PluginMessages.sendMessage(player, PluginMessages.getMessage("ignore-success"));
                        return;
                    } else {
                       Database.unmutePing(player, p);
                        PluginMessages.sendMessage(player, PluginMessages.getMessage("unignore-success"));
                        return;
                    }
                }
            }
        }
    }

}
