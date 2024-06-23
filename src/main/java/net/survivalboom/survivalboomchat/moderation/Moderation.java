package net.survivalboom.survivalboomchat.moderation;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.survivalboom.survivalboomchat.events.Event;
import net.survivalboom.survivalboomchat.moderation.types.CheckType;
import net.survivalboom.survivalboomchat.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class Moderation {

    protected final String name;
    protected final boolean enabled;
    protected final Event triggerEvent;
    protected final boolean block;
    protected final CheckType type;
    protected final ConfigurationSection section;
    protected final String replacement;


    public Moderation(@NotNull String name, @NotNull ConfigurationSection section, @NotNull CheckType type) {
        this.name = name;
        this.section = section;

        enabled = section.getBoolean("enabled");

        ConfigurationSection eventSection = section.getConfigurationSection("event");
        if (eventSection == null) triggerEvent = null;
        else triggerEvent = new Event(String.format("%s-moderation-event", name), eventSection);

        block = section.getBoolean("block");
        replacement = section.getString("replacement");

        this.type = type;
    }

    public void moderate(@NotNull AsyncChatEvent event) {
        boolean result = check(event);
        if (!result) return;
        punishment(event);
    }

    public boolean check(@NotNull AsyncChatEvent event) {
        throw new IllegalArgumentException("Not implemented");
    }

    public void punishment(@NotNull AsyncChatEvent event) {
        if (block) event.setCancelled(true);
        else clean(event);
        if (triggerEvent == null) return;
        triggerEvent.perform(event.getPlayer(), null);
    }

    public void clean(@NotNull AsyncChatEvent event) {

    }

}
