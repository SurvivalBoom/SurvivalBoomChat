package net.survivalboom.survivalboomchat.moderation;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import net.survivalboom.survivalboomchat.SurvivalBoomChat;
import net.survivalboom.survivalboomchat.configuration.PluginMessages;
import net.survivalboom.survivalboomchat.moderation.types.Caps;
import net.survivalboom.survivalboomchat.moderation.types.Links;
import net.survivalboom.survivalboomchat.moderation.types.Swears;
import net.survivalboom.survivalboomchat.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ModerationManager {

    private static final List<Moderation> checks = new ArrayList<>();

    public static void init() {
        PluginMessages.consoleSend("&b>> &fLoading moderation checks...");
        reload();
    }

    public static boolean reload() {

        FileConfiguration configuration = SurvivalBoomChat.getPlugin().getConfig();
        ConfigurationSection section = configuration.getConfigurationSection("moderation");

        boolean error = false;

        checks.clear();
        for (String s : section.getKeys(false)) {
            ConfigurationSection ss = section.getConfigurationSection(s);
            if (ss == null) continue;
            try {

                String typeRaw = ss.getString("type", "");
                CheckType type = Utils.getEnumValue(CheckType.class, typeRaw);
                if (type == null) throw new IllegalArgumentException("Invalid moderation check type. Valid: CAPS, LINKS, SWEARS");

                Moderation moderation;
                switch (type) {

                    case CAPS -> moderation = new Caps(s, ss, type);

                    case LINK -> moderation = new Links(s, ss, type);

                    case SWEAR -> moderation = new Swears(s, ss, type);

                    default -> { throw new IllegalArgumentException("HRUK"); }

                }

                checks.add(moderation);

            }
            catch (Exception e) {
                PluginMessages.consoleSend(String.format("&c>> &fFailed to load moderation check &c%s: &e%s", s, e.getMessage()));
                error = true;
            }
        }

        return error;

    }

    public static void moderate(@NotNull AsyncChatEvent event) {
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        checks.forEach(m -> {
            m.moderate(event, atomicBoolean);
        });
    }

}
