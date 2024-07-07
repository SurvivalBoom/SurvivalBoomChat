package net.survivalboom.survivalboomchat.eventmessages;

import net.survivalboom.survivalboomchat.events.Event;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class EventMessage {

    private final String name;
    private final String message;
    private final Event event;
    private final int onlineLimit;
    private final boolean enabled;

    public EventMessage(@NotNull String name, @NotNull ConfigurationSection section) {

        this.name = name;
        message = section.getString("message");
        onlineLimit = section.getInt("online-limit", -1);
        enabled = section.getBoolean("enabled");

        ConfigurationSection eventSection = section.getConfigurationSection("event");
        if (eventSection != null) event = new Event(name, eventSection);
        else event = null;

    }

    @NotNull
    public String getName() {
        return name;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    public int getOnlineLimit() {
        return onlineLimit;
    }

    public Event getEvent() {
        return event;
    }

    public boolean isEnabled() {
        return enabled;
    }

}
