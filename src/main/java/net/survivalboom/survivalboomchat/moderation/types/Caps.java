package net.survivalboom.survivalboomchat.moderation.types;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import net.survivalboom.survivalboomchat.moderation.Moderation;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class Caps extends Moderation {

    private final int percentage;
    private final int length;

    public Caps(@NotNull String name, @NotNull ConfigurationSection section, @NotNull CheckType type) {
        super(name, section, type);
        percentage = section.getInt("percent");
        length = section.getInt("length");
    }

    @Override
    public boolean check(@NotNull AsyncChatEvent event) {

        String message = ((TextComponent) event.message()).content();
        if (message.length() < length) return false;

        return percentage > 0 && calculateCapsPercentage(message) > percentage;

    }

    public double calculateCapsPercentage(String message) {
        int totalChars = message.length();
        int uppercaseChars = 0;

        for (int i = 0; i < totalChars; i++) {
            if (Character.isUpperCase(message.charAt(i))) {
                uppercaseChars++;
            }
        }

        return (double) uppercaseChars / totalChars * 100;
    }

}
