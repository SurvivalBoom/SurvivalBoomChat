package net.survivalboom.survivalboomchat.moderation.types;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.survivalboom.survivalboomchat.SurvivalBoomChat;
import net.survivalboom.survivalboomchat.configuration.PluginMessages;
import net.survivalboom.survivalboomchat.moderation.CheckType;
import net.survivalboom.survivalboomchat.moderation.Moderation;
import net.survivalboom.survivalboomchat.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Swears extends Moderation {

    private final List<String> swears;

    public Swears(@NotNull String name, @NotNull ConfigurationSection section, @NotNull CheckType type) {
        super(name, section, type);

        String fileName = section.getString("file", "null");
        File file = new File(SurvivalBoomChat.getPlugin().getDataFolder(), fileName);

        if (!file.exists() || file.isDirectory()) throw new IllegalArgumentException(String.format("File %s does not exist", fileName));
        swears = readBadWordsFromFile(file);

    }

    @Override
    public boolean check(@NotNull AsyncChatEvent event) {
        if (checkBypass(event.getPlayer())) return false;
        String message = ((TextComponent) event.message()).content().toLowerCase();
        return find(message).size() > 0;
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public void clean(@NotNull AsyncChatEvent event) {
        String message = ((TextComponent) event.message()).content();
        find(message).forEach(s -> event.message(Utils.componentReplace(event.message(), s, replacement)));
    }

    private List<String> readBadWordsFromFile(File file) {

        List<String> badWords = new ArrayList<>();

        try {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    badWords.add(line.trim().toLowerCase());
                }

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return badWords;
    }

    @NotNull
    private List<String> find(@NotNull String message) {

        List<String> out = new ArrayList<>();

        for (String s : swears) {
            if (!message.contains(s)) continue;
            out.add(s);
        }

        return out;

    }

}
