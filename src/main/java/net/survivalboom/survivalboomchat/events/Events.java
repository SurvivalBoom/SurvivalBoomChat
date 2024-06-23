package net.survivalboom.survivalboomchat.events;

import net.survivalboom.survivalboomchat.placeholders.Placeholders;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Events {

    private final Map<String, Event> events = new HashMap<>();

    public Events(@NotNull ConfigurationSection section) {

        for (String key : section.getKeys(false)) {
            events.put(key, new Event(key, section.getConfigurationSection(key)));
        }

    }

    public void performEventIfExist(@NotNull String key, @NotNull Player player, @Nullable Placeholders placeholders) {
        if (!events.containsKey(key)) return;
        events.get(key).perform(player, placeholders);
    }

    public void performEventIfExist(@NotNull String key, @NotNull Player player, List<Player> otherPlayers, @Nullable Placeholders placeholders) {
        if (!events.containsKey(key)) return;
        events.get(key).perform(player, otherPlayers, placeholders);
    }

    public void performEventIfExist(@NotNull String key, @NotNull Player player) {
        if (!events.containsKey(key)) return;
        events.get(key).perform(player, null);
    }

    public void performEventIfExist(@NotNull String key, @NotNull List<Player> players, @Nullable Placeholders placeholders) {
        if (!events.containsKey(key)) return;
        events.get(key).perform(players, placeholders);
    }

    @Nullable
    public Event getEvent(@NotNull String key) {
        return events.get(key);
    }



}
