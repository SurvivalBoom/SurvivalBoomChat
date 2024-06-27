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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ExecutionException;

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
        if (prefix != null && (!message.startsWith(prefix) || message.equals(prefix))) return false;

        return true;

    }

    public void giveControl(@NotNull AsyncChatEvent event) {

        if (moderation) ModerationManager.moderate(event);
        if (event.isCancelled()) return;

        viewers(event);

        int count = Mentions.mentionsCount(event);
        if (count > Mentions.getLimit()) {
            PluginMessages.sendMessage(event.getPlayer(), PluginMessages.getMessage("mention-limit-reached").replace("{LIMIT}", String.valueOf(Mentions.getLimit())));
            event.setCancelled(true);
            return;
        }

        if (count != 0 && !event.getPlayer().hasPermission("sbchat.mention")) {
            PluginMessages.sendMessage(event.getPlayer(), PluginMessages.getMessage("mention-no-permission").replace("{PERMISSION}", "sbchat.mention"));
            event.setCancelled(true);
            return;
        }

        render(event);
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
        String message = Utils.componentDeserialize(event.message());

        if (prefix != null) message = message.replaceFirst(prefix, "");
        if (message.startsWith(" ")) message = message.replaceFirst(" ", "");

        // Якщо гравець не має прав на відправку кольорових повідомлень, пропустити повідомлення через костиль і очистити його від кольорових кодів.
        if (!player.hasPermission("sbchat.chat.colors")) message = ((TextComponent) PluginMessages.parseOnlyColors(message)).content();

        ChatRenderer renderer = ((source, sourceDisplayName, message1, viewer) -> {

            Placeholders placeholders = new Placeholders();
            placeholders.add("{PREFIX}", Utils.getPrefix(player));
            placeholders.add("{PLAYER}", player.getName());
            placeholders.add("{SUFFIX}", Utils.getSuffix(player));

            Component out = Mentions.processRender(source, viewer, message1);

            return PluginMessages.parse(format.replace("{MESSAGE}", Utils.componentDeserialize(Objects.requireNonNullElse(out, message1))), player, placeholders);

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

        Collection<Player> playersNearby;
        try {
            playersNearby = Bukkit.getScheduler().callSyncMethod(SurvivalBoomChat.getPlugin(), () -> player.getLocation().getNearbyPlayers(range)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (range > 0 && !playersNearby.contains(p)) continue;
            if (view_permission != null && !p.hasPermission(view_permission)) continue;
            viewers.add(p);
        }

        if (viewers.size() == 2) {

            Runnable runnable;

            if (Bukkit.getOnlinePlayers().size() == 1) runnable = () -> PluginMessages.sendMessage(player, PluginMessages.getMessage("nobody-hear-you-online"));
            else if (range > 0 && playersNearby.size() == 0) runnable = () -> PluginMessages.sendMessage(player, PluginMessages.getMessage("nobody-hear-you-range"));
            else runnable = () -> PluginMessages.sendMessage(player, PluginMessages.getMessage("nobody-hear-you"));

            Bukkit.getScheduler().runTaskLater(SurvivalBoomChat.getPlugin(), () -> {
                if (event.isCancelled()) return;
                runnable.run();
            }, 20L);
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

    @NotNull
    public String getName() {
        return name;
    }

    @Nullable
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String toString() {
        return name;
    }

}
