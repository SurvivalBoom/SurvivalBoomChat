package net.survivalboom.survivalboomchat.moderation.types;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.clip.placeholderapi.libs.kyori.adventure.text.TextComponent;
import net.survivalboom.survivalboomchat.configuration.PluginMessages;
import net.survivalboom.survivalboomchat.moderation.Moderation;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Links extends Moderation {

    private final List<String> regexes;
    private final List<String> whitelist;

    public Links(@NotNull String name, @NotNull ConfigurationSection section, @NotNull CheckType type) {
        super(name, section, type);
        regexes = section.getStringList("regex");
        whitelist = section.getStringList("whitelist");
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public void clean(@NotNull AsyncChatEvent event) {
        String message = ((TextComponent) event.message()).content();
        find(message).forEach(s -> event.message().replaceText(s, PluginMessages.parse(replacement)));
    }

    @Override
    public boolean check(@NotNull AsyncChatEvent event) {
        String message = ((TextComponent) event.message()).content();
        return find(message).size() > 0;
    }

    private List<String> find(@NotNull String message) {

        List<String> out = new ArrayList<>();

        for (String regex : regexes) {

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(message);

            while (matcher.find()) {
                String str = matcher.group();
                if (whitelist.contains(str)) continue;
                out.add(str);
            }

        }

        return out;

    }

}
