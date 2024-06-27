package net.survivalboom.survivalboomchat.chats;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.survivalboom.survivalboomchat.SurvivalBoomChat;
import net.survivalboom.survivalboomchat.configuration.PluginMessages;
import net.survivalboom.survivalboomchat.events.Event;
import net.survivalboom.survivalboomchat.placeholders.Placeholders;
import net.survivalboom.survivalboomchat.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Mentions {

    private static String format = null;
    private static boolean enabled = false;
    private static Event pingEvent = null;
    private static int limit = 0;

    public static void init() {
        PluginMessages.consoleSend("&b>> &fLoading mentions...");
        reload();
    }

    public static void reload() {

        format = null;
        enabled = false;
        pingEvent = null;

        ConfigurationSection section = SurvivalBoomChat.getPlugin().getConfig().getConfigurationSection("mentions");
        if (section == null) return;

        enabled = section.getBoolean("enabled");
        format = section.getString("format");
        limit = section.getInt("limit");

        ConfigurationSection eventSection = section.getConfigurationSection("ping-event");
        if (eventSection == null) return;

        pingEvent = new Event("ping-event", eventSection);

    }

    public static void processEvent(@NotNull AsyncChatEvent event) {

        if (!enabled || format == null) return;

        Player player = event.getPlayer();

        int count = 0;
        for (Audience audience : event.viewers()) {

            String message = Utils.componentDeserialize(event.message());
            if (!(audience instanceof Player p)) continue;

            String playerName = p.getName();

            if (!message.contains(playerName) || player.equals(p)) continue;

            Placeholders placeholders = new Placeholders();
            placeholders.add("{MENTION}", playerName);
            placeholders.add("{MENTIONER}", player.getName());

            if (count > limit) {
                PluginMessages.sendMessage(player, PluginMessages.getMessage("mention-limit-reached").replace("{LIMIT}", String.valueOf(limit)));
                break;
            }

            count++;

            String replacement = placeholders.parse(format);
            Component component = event.message().replaceText(playerName, PluginMessages.parse(replacement));

            SurvivalBoomChat.getPlugin().getLogger().warning(Utils.componentDeserialize(component));

            event.message(component);

            if (pingEvent == null) continue;
            pingEvent.perform(player, placeholders);

        }

    }

    public static int mentionsCount(@NotNull AsyncChatEvent event) {

        if (!enabled || format == null) return 0;

        int count = 0;
        for (Audience audience : event.viewers()) {

            String message = Utils.componentDeserialize(event.message());
            if (!(audience instanceof Player p)) continue;

            String playerName = p.getName();

            if (!message.contains(playerName) || event.getPlayer().equals(p)) continue;

            count = count + StringUtils.countMatches(message, playerName);

        }

        return count;

    }

    public static boolean checkMentionsCount(@NotNull AsyncChatEvent event) {
        return mentionsCount(event) > limit;
    }

    @Nullable
    public static Component processRender(@NotNull Audience source, @NotNull Audience viewer, @NotNull Component message) {

        if (!enabled || format == null) return message;

        if (!(viewer instanceof Player player) || !(source instanceof Player sender)) return null;

        String messageStr = Utils.componentDeserialize(message);

        String playerName = player.getName();

        if (!messageStr.contains(playerName) || player.getName().equals(sender.getName())) return null;

        Placeholders placeholders = new Placeholders();
        placeholders.add("{MENTION}", playerName);
        placeholders.add("{MENTIONER}", player.getName());

        String replacement = placeholders.parse(format);
        Component component = message.replaceText(playerName, PluginMessages.parse(replacement));

        if (pingEvent == null) return component;
        pingEvent.perform(player, new ArrayList<>(List.of(sender)), placeholders);

        return component;


    }
    public static int getLimit() {
        return limit;
    }

}
