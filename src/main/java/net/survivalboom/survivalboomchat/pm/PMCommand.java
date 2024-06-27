package net.survivalboom.survivalboomchat.pm;

import net.kyori.adventure.text.TextComponent;
import net.survivalboom.survivalboomchat.SurvivalBoomChat;
import net.survivalboom.survivalboomchat.commands.TabCompleteHandler;
import net.survivalboom.survivalboomchat.configuration.PluginMessages;
import net.survivalboom.survivalboomchat.events.Event;
import net.survivalboom.survivalboomchat.placeholders.Placeholders;
import net.survivalboom.survivalboomchat.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class PMCommand implements CommandExecutor, TabCompleter {

    private static final PluginCommand command = getCommand("survivalboomprivatemessages", SurvivalBoomChat.getPlugin());
    private static final SurvivalBoomChat plugin = SurvivalBoomChat.getPlugin();
    private static String senderFormat = null;
    private static String receiverFormat = null;
    private static boolean enable;
    private static Event event = null;

    static {
        PMCommand instance = new PMCommand();
        command.setExecutor(instance);
        command.setTabCompleter(instance);
    }

    public static void init() {
        PluginMessages.consoleSend("&b>> &fLoading private messaging...");
        reload();
    }

    public static void reload() {

        enable = false;

        ConfigurationSection section = SurvivalBoomChat.getPlugin().getConfig().getConfigurationSection("private-messaging");
        if (section == null) {
            command.setAliases(new ArrayList<>());
            return;
        }

        enable = section.getBoolean("enabled");
        if (!enable) return;

        senderFormat = section.getString("sender-format");
        receiverFormat = section.getString("receiver-format");

        List<String> aliases = section.getStringList("commands");
        command.setAliases(aliases);

        getCommandMap().register(plugin.getPluginMeta().getName(), command);

        ConfigurationSection eventSection = section.getConfigurationSection("event");
        if (eventSection == null) return;

        try { event = new Event("pm-event", eventSection); }
        catch (Exception e) {
            PluginMessages.consoleSend(String.format("&c&l>> &fFailed to load private message event: &e%s", e.getMessage()));
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!enable) {
            PluginMessages.sendMessage(sender, PluginMessages.getMessage("pm-disabled"));
            return true;
        }

        if (!sender.hasPermission("sbchat.pm")) {
            PluginMessages.sendMessage(sender, PluginMessages.getMessage("mention-no-permission").replace("{PERMISSION}", "sbchat.pm"));
            return true;
        }

        String playerName = Utils.getArrayValue(args, 0);
        if (playerName == null) {
            PluginMessages.sendMessage(sender, PluginMessages.getMessage("pm-usage").replace("{COMMAND}", label));
            return true;
        }

        Player receiver = Bukkit.getPlayer(playerName);
        if (receiver == null) {
            PluginMessages.sendMessage(sender, PluginMessages.getMessage("player-not-found").replace("{PLAYER}", playerName));
            return true;
        }

        String message;
        if (args.length > 1) {

            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                builder.append(args[i]);
                if (i != args.length - 1) builder.append(" ");
            }

            message = builder.toString();

        } else message = Utils.getArrayValue(args, 1);

        if (message == null) {
            PluginMessages.sendMessage(sender, PluginMessages.getMessage("pm-usage").replace("{COMMAND}", label));
            return true;
        }

        if (sender.equals(receiver)) {
            PluginMessages.sendMessage(sender, PluginMessages.getMessage("pm-self"));
            return true;
        }

        if (!sender.hasPermission("sbchat.chat.colors")) message = ((TextComponent) PluginMessages.parseOnlyColors(message)).content();

        Placeholders senderPlaceholders = new Placeholders();
        senderPlaceholders.add("{PLAYER}", receiver.getName());
        senderPlaceholders.add("{PREFIX}", Utils.getPrefix(receiver));
        senderPlaceholders.add("{SUFFIX}", Utils.getSuffix(receiver));
        senderPlaceholders.add("{MESSAGE}", message);

        Placeholders viewerPlaceholders = new Placeholders();
        viewerPlaceholders.add("{MESSAGE}", message);

        if (sender instanceof Player player) {
            viewerPlaceholders.add("{PLAYER}", player.getName());
            viewerPlaceholders.add("{PREFIX}", Utils.getPrefix(player));
            viewerPlaceholders.add("{SUFFIX}", Utils.getSuffix(player));
        }

        else {
            viewerPlaceholders.add("{PLAYER}", "CONSOLE");
            viewerPlaceholders.add("{PREFIX}", "");
            viewerPlaceholders.add("{SUFFIX}", "");
        }

        PluginMessages.sendMessage(sender, senderPlaceholders.parse(senderFormat));
        PluginMessages.sendMessage(receiver, viewerPlaceholders.parse(receiverFormat));

        if (sender instanceof Player player) {
            if (event != null) event.perform(receiver, new ArrayList<>(List.of(player)), viewerPlaceholders);
        }

        else { if (event != null) event.perform(receiver, false, viewerPlaceholders); }


        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> out = new ArrayList<>();
        TabCompleteHandler.addPossibleCompletions(null, TabCompleteHandler.getPlayerNames(), args, out, 0, false);
        return out;
    }

    private static PluginCommand getCommand(String name, Plugin plugin) {
        PluginCommand command = null;

        try {
            Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            c.setAccessible(true);

            command = c.newInstance(name, plugin);
        } catch (SecurityException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        return command;
    }

    private static CommandMap getCommandMap() {
        CommandMap commandMap = null;

        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                Field f = SimplePluginManager.class.getDeclaredField("commandMap");
                f.setAccessible(true);

                commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return commandMap;
    }
}
