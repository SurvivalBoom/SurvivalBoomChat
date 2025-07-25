package net.survivalboom.survivalboomchat.moderation;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import net.survivalboom.survivalboomchat.configuration.PluginMessages;
import net.survivalboom.survivalboomchat.events.Event;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Moderation {

    protected final String name;
    protected final boolean enabled;
    protected final Event triggerEvent;
    protected final boolean block;
    protected final CheckType type;
    protected final ConfigurationSection section;
    protected final String replacement;
    protected final String adminMessage;
    protected final String blockMessage;
    protected final String notifyPermission;
    protected final String bypassPermission;


    public Moderation(@NotNull String name, @NotNull ConfigurationSection section, @NotNull CheckType type) {
        this.name = name;
        this.section = section;

        enabled = section.getBoolean("enabled");

        ConfigurationSection eventSection = section.getConfigurationSection("event");
        if (eventSection == null) triggerEvent = null;
        else triggerEvent = new Event(String.format("%s-moderation-event", name), eventSection);

        block = section.getBoolean("block");
        replacement = section.getString("replacement");
        adminMessage = section.getString("admin-message");
        blockMessage = section.getString("block-message");

        notifyPermission = section.getString("notify-permission");
        bypassPermission = section.getString("bypass-permission");

        this.type = type;
    }

    public void moderate(@NotNull AsyncChatEvent event, @NotNull AtomicBoolean checked) {
        boolean result = check(event);
        if (!result) return;
        if (!checked.get()) punishment(event);
        if (block) event.setCancelled(true);
        else clean(event);
        checked.set(true);
    }

    public boolean check(@NotNull AsyncChatEvent event) {
        return false;
    }

    public void punishment(@NotNull AsyncChatEvent event) {
        if (triggerEvent == null) return;
        triggerEvent.perform(event.getPlayer(), null);
        PluginMessages.sendMessage(event.getPlayer(), blockMessage);

        String adminMsg = adminMessage
                .replace("{PLAYER}", event.getPlayer().getName())
                .replace("{MESSAGE}", ((TextComponent) event.message()).content());

        PluginMessages.sendAdmins(notifyPermission, adminMsg);
    }

    public void clean(@NotNull AsyncChatEvent event) {

    }

    @NotNull
    public String getName() {
        return name;
    }

    protected boolean checkBypass(@NotNull Player player) {
        if (bypassPermission == null) return false;
        return player.hasPermission(bypassPermission);
    }

}
