package net.survivalboom.survivalboomchat.events;

import net.survivalboom.survivalboomchat.placeholders.Placeholders;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class Event {

    private final String name;
    private final List<EventAction> eventActionList = new ArrayList<>();

    public Event(@NotNull String name, @NotNull ConfigurationSection section) {

        this.name = name;

        Set<String> keys = section.getKeys(false);
        if (keys.size() < 1) throw new EventCreationFailed("Provided event section doesn't contain any keys");

        if (keys.contains("enabled")) eventActionList.add(new EventAction(section));
        else {
            for (String key : keys) eventActionList.add(new EventAction(section.getConfigurationSection(key)));
        }

    }


    public void perform(@NotNull Player player, @Nullable Placeholders placeholders) {
        for (EventAction action : eventActionList) action.perform(player, true, placeholders);
    }

    public void perform(@NotNull Player player, boolean performOthers, @Nullable Placeholders placeholders) {
        for (EventAction action : eventActionList) action.perform(player, performOthers, placeholders);
    }

    public void perform(@NotNull Player player, @NotNull List<Player> otherPlayers, @Nullable Placeholders placeholders) {
        for (EventAction action : eventActionList) action.perform(player, otherPlayers, placeholders);
    }

    public void perform(Collection<Player> players, @Nullable Placeholders placeholders) {
        for (EventAction action : eventActionList) action.perform(players, placeholders);
    }

    public void perform(@NotNull List<Player> targets, @NotNull List<Player> otherPlayers, @Nullable Placeholders placeholders) {
        eventActionList.forEach(e -> e.perform(targets, otherPlayers, placeholders));
    }

}
