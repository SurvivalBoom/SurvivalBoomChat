package net.survivalboom.survivalboomchat.chats;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.survivalboom.survivalboomchat.SurvivalBoomChat;
import net.survivalboom.survivalboomchat.configuration.PluginMessages;
import net.survivalboom.survivalboomchat.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ChatManager implements Listener {

    private static final Map<String, Chat> registeredChats = new HashMap<>();

    public static void init() {
        PluginMessages.consoleSend("&b>> &fLoading chats...");
        reload();
        Bukkit.getPluginManager().registerEvents(new ChatManager(), SurvivalBoomChat.getPlugin());
    }

    public static boolean reload() {

        FileConfiguration configuration = SurvivalBoomChat.getPlugin().getConfig();
        ConfigurationSection section = configuration.getConfigurationSection("chats");
        if (section == null) return false;

        boolean error = false;

        registeredChats.clear();
        for (String s : section.getKeys(false)) {

            ConfigurationSection ss = section.getConfigurationSection(s);
            if (ss == null) continue;

            try {
                Chat chat = new Chat(s, ss);
                registeredChats.put(s, chat);
            }

            catch (Exception e) {
                PluginMessages.consoleSend(String.format("&c>> &fFailed to load chat &c%s&f: &e%s", s, e.getMessage()));
                error = true;
            }

        }

        return error;

    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOW)
    public void onChat(AsyncChatEvent event) {

        try {

            Chat chat = findChat(event);
            if (chat == null) {
                event.setCancelled(true);
                PluginMessages.sendMessage(event.getPlayer(), PluginMessages.getMessage("chat-not-found"));
                return;
            }

            chat.giveControl(event);

        } catch (Exception e) {
            PluginMessages.sendMessage(event.getPlayer(), PluginMessages.getMessage("chat-error"));
            Utils.sendPluginError("Failed to process player's message!", e);
            event.setCancelled(true);
        }

    }

    @Nullable
    public Chat findChat(@NotNull AsyncChatEvent event) {

        List<Chat> chats = new ArrayList<>(registeredChats.values());
        chats.sort(Comparator.comparing(chat -> chat.getPrefix() == null));

        Chat out = null;
        for (Chat chat : chats) {
            if (!chat.suitable(event)) continue;
            out = chat;
            break;
        }

        return out;

    }

}
