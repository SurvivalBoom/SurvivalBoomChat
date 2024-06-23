package net.survivalboom.survivalboomchat.chats;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.survivalboom.survivalboomchat.SurvivalBoomChat;
import net.survivalboom.survivalboomchat.configuration.PluginMessages;
import net.survivalboom.survivalboomchat.events.Event;
import net.survivalboom.survivalboomchat.moderation.ModerationManager;
import net.survivalboom.survivalboomchat.placeholders.Placeholders;
import net.survivalboom.survivalboomchat.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class Chat {

    private final boolean enabled;
    private final String name;
    private final String displayName;
    private final boolean moderation;
    private final String prefix;
    private final String format;
    private final int range;
    private final String view_permission;
    private final String write_permission;
    private final Event chatEvent;

    public Chat(@NotNull String name, @NotNull ConfigurationSection section) {

        this.name = name;
        displayName = section.getString("name", name);

        enabled = section.getBoolean("enabled");

        moderation = section.getBoolean("moderation", true);
        prefix = section.getString("prefix");

        format = section.getString("format");

        range = section.getInt("range", -1);

        view_permission = section.getString("view-permission");
        write_permission = section.getString("write-permission");

        ConfigurationSection eventSection = section.getConfigurationSection("event");
        if (eventSection == null) {
            chatEvent = null;
            return;
        }

        chatEvent = new Event(String.format("%s-chat-event", name), eventSection);

    }

    public boolean suitable(@NotNull AsyncChatEvent event) {

        if (!enabled) return false;

        Player player = event.getPlayer();
        String message = ((TextComponent) event.message()).content();

        if (write_permission != null && !player.hasPermission(write_permission)) return false;
        return prefix == null || message.startsWith(prefix);

    }

    public void giveControl(@NotNull AsyncChatEvent event) {

        if (moderation) ModerationManager.moderate(event);
        if (event.isCancelled()) return;

        render(event);
        viewers(event);
        performEvents(event);

    }

    public void performEvents(@NotNull AsyncChatEvent event) {

        if (chatEvent == null) return;

        Player player = event.getPlayer();
        Set<Audience> viewers = event.viewers();

        List<Player> players = new ArrayList<>();
        viewers.forEach(v -> {
            if (v instanceof  Player p) players.add(p);
        });

        chatEvent.perform(player, players, generatePlaceholders(event));

    }

    // Код спизжений з SurvivalBoomBedWars :D
    public void render(@NotNull AsyncChatEvent event) {

        Player player = event.getPlayer();
        String message = ((TextComponent) event.message()).content();

        if (prefix != null) message = message.replaceFirst(prefix, "");
        if (message.startsWith(" ")) message = message.replaceFirst(" ", "");

        // Якщо гравець не має прав на відправку кольорових повідомлень, пропустити повідомлення через костиль і очистити його від кольорових кодів.
        if (!player.hasPermission("sbchat.chat.colors")) message = ((TextComponent) PluginMessages.parseOnlyColors(message)).content();

        String finalMessage = message;
        ChatRenderer renderer = ((source, sourceDisplayName, message1, viewer) -> {

            Placeholders placeholders = new Placeholders();
            placeholders.add("{PREFIX}", Utils.getPrefix(player));
            placeholders.add("{PLAYER}", player.getName());
            placeholders.add("{SUFFIX}", Utils.getSuffix(player));

            Component base = PluginMessages.parse(format, player, placeholders);
            //noinspection UnstableApiUsage
            return base.replaceText("{MESSAGE}", PluginMessages.parseOnlyColors(finalMessage));

        });

        event.message(PluginMessages.parseOnlyColors(message));
        event.renderer(renderer);

    }

    public void viewers(@NotNull AsyncChatEvent event) {

        Player player = event.getPlayer();

        Set<Audience> viewers = event.viewers();

        viewers.clear();
        viewers.add(player);
        viewers.add(Bukkit.getConsoleSender());

        Collection<Player> playersNearby = player.getLocation().getNearbyPlayers(range);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (range > 0 && !playersNearby.contains(p)) continue;
            if (view_permission != null && !p.hasPermission(view_permission)) continue;
            viewers.add(p);
        }

        if (viewers.size() == 2) {

            if (Bukkit.getOnlinePlayers().size() == 1) {
                Bukkit.getScheduler().runTaskLater(SurvivalBoomChat.getPlugin(), () -> PluginMessages.sendMessage(player, PluginMessages.getMessage("nobody-hear-you-online")), 20L);
                return;
            }

            if (range > 0 && playersNearby.size() == 0) {
                Bukkit.getScheduler().runTaskLater(SurvivalBoomChat.getPlugin(), () -> PluginMessages.sendMessage(player, PluginMessages.getMessage("nobody-hear-you-range")), 20L);
                return;
            }

            Bukkit.getScheduler().runTaskLater(SurvivalBoomChat.getPlugin(), () -> PluginMessages.sendMessage(player, PluginMessages.getMessage("nobody-hear-you")), 20L);

        }

    }

    public Placeholders generatePlaceholders(@NotNull AsyncChatEvent event) {

        Player player = event.getPlayer();

        Placeholders placeholders = new Placeholders();
        placeholders.add("{PREFIX}", Utils.getPrefix(player));
        placeholders.add("{PLAYER}", player.getName());
        placeholders.add("{SUFFIX}", Utils.getSuffix(player));

        return placeholders;

    }

}
