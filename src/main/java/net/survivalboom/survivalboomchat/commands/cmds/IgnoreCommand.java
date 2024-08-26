package net.survivalboom.survivalboomchat.commands.cmds;

import net.survivalboom.survivalboomchat.configuration.PluginMessages;
import net.survivalboom.survivalboomchat.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class IgnoreCommand {

    public static void command(@NotNull CommandSender sender, String[] args)  {
        if (!(sender instanceof Player player)) {
            return;
        }
            if (!player.hasPermission("sbchat.ignore")) {
                if (args.length == 1) {
                    @SuppressWarnings("DataFlowIssue") Player p = Bukkit.getPlayer(args[1]);
                    if (p != null) {
                        PluginMessages.sendMessage(player, PluginMessages.getMessage("ignore-player-not-found").replace("{player}", p.getName()));
                        return;
                    }
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
                } else {
                    PluginMessages.sendMessage(player, PluginMessages.getMessage("ignore-syntax"));
                }
            } else {
                PluginMessages.sendMessage(player, PluginMessages.getMessage("no-permission".replace("{PERMISSION}", "sbchat.ignore")));
            }
        }
    }

