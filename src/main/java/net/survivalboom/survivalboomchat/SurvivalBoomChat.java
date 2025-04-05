package net.survivalboom.survivalboomchat;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import net.survivalboom.survivalboomchat.chatheads.ChatHeads;
import net.survivalboom.survivalboomchat.chats.ChatManager;
import net.survivalboom.survivalboomchat.chats.Mentions;
import net.survivalboom.survivalboomchat.commands.CommandsHandler;
import net.survivalboom.survivalboomchat.commands.TabCompleteHandler;
import net.survivalboom.survivalboomchat.configuration.PluginMessages;
import net.survivalboom.survivalboomchat.database.Database;
import net.survivalboom.survivalboomchat.eventmessages.EventMessagesManager;
import net.survivalboom.survivalboomchat.moderation.ModerationManager;
import net.survivalboom.survivalboomchat.pm.PMCommand;
import net.survivalboom.survivalboomchat.utils.PlayerLocations;
import net.survivalboom.survivalboomchat.utils.Utils;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class SurvivalBoomChat extends JavaPlugin {
    private static TaskScheduler scheduler;
    private static SurvivalBoomChat plugin = null;
    private static final String compiledFor = "SurvivalBoomChat Public Release. Compiled For Public Use";
    private static PlayerLocations playerLocations;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        scheduler = UniversalScheduler.getScheduler(plugin);
        playerLocations = new PlayerLocations(scheduler);
        playerLocations.init0();
        try {

            sendSplash();

            PluginMessages.consoleSend("&b>> &fChecking files...");
            checkFiles(false);

            PluginMessages.consoleSend("&b>> &fLoading configuration...");
            getConfig().load(new File(getDataFolder(), "config.yml"));
            PluginMessages.reload(new File(getDataFolder(), "messages.yml"));

            ModerationManager.init();
            ChatManager.init();
            Mentions.init();
            PMCommand.init();
            ChatHeads.reload();
            EventMessagesManager.init();
            Database.init();

            PluginMessages.consoleSend("&b>> &fRegistering plugin components...");
            PluginCommand command = getCommand("survivalboomchat");
            command.setTabCompleter(new TabCompleteHandler());
            command.setExecutor(new CommandsHandler());

            PluginCommand ignorecommand = getCommand("ignore");
            ignorecommand.setTabCompleter(new TabCompleteHandler());
            ignorecommand.setExecutor(new CommandsHandler());

            PluginMessages.consoleSend("&a>> &fPlugin &aSurvivalBoomChat &fsuccessfully enabled!");

        }

        catch (Exception e) {
            PluginMessages.consoleSend("&c&l! &fLooks like &cSurvivalBoomChat &fjust crashed! &fSowwy >.<");
            Utils.sendStackTrace(e);
        }

    }

    @Override
    public void onDisable() {
        if (Database.isEnabled()) Database.shutdown();
        // Plugin shutdown logic
        playerLocations.shutdown0();
        PluginMessages.consoleSend("&a>> &fPlugin &aSurvivalBoomChat &fsuccessfully disabled!");
    }

    @NotNull
    public static TaskScheduler getScheduler() {
        return scheduler;
    }

    @NotNull
    public static PlayerLocations getPlayerLocations() {
        return playerLocations;
    }

    @NotNull
    public static SurvivalBoomChat getPlugin() {
        return plugin;
    }

    @NotNull @SuppressWarnings("UnstableApiUsage")
    public static String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    public static void sendSplash() {

        PluginMessages.consoleSend("&b");
        PluginMessages.consoleSend("&b   _____                  _            _ &3____");
        PluginMessages.consoleSend("&b  / ____|                (_)          | &3|  _ \\                       ");
        PluginMessages.consoleSend("&b | (___  _   _ _ ____   _____   ____ _| &3| |_) | ___   ___  _ __ ___   ");
        PluginMessages.consoleSend("&b  \\___ \\| | | | '__\\ \\ / / \\ \\ / / _` | &3|  _ < / _ \\ / _ \\| '_ ` _ \\ ");
        PluginMessages.consoleSend("&b  ____) | |_| | |   \\ V /| |\\ V / (_| | &3| |_) | (_) | (_) | | | | | | ");
        PluginMessages.consoleSend("&b |_____/ \\__,_|_|    \\_/ |_| \\_/ \\__,_|_&3|____/ \\___/ \\___/|_| |_| |_|   ");
        PluginMessages.consoleSend("    &dSurvivalBoom Network &8| &fBy &bTIMURishche &8|  &dSurvivalBoomChat &fv&3{VERSION}".replace("{VERSION}", getVersion()));

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void checkFiles(boolean silent) {

        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdir();

        String[] files = {"config.yml", "messages.yml", "swears.txt", "documentation.txt"};
        for (String file : files) if (Utils.copyPluginFile(file) && !silent) PluginMessages.consoleSend(String.format("&3>> &fCreated &3%s", file));

    }

    @NotNull
    public static String getCompiledFor() {
        return compiledFor;
    }
}
