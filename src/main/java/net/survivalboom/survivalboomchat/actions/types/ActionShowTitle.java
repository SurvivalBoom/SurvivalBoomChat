package net.survivalboom.survivalboomchat.actions.types;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.survivalboom.survivalboomchat.actions.Action;
import net.survivalboom.survivalboomchat.actions.ActionPerformFailed;
import net.survivalboom.survivalboomchat.actions.ActionType;
import net.survivalboom.survivalboomchat.configuration.PluginMessages;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class ActionShowTitle extends Action {

    public ActionShowTitle(String original_action_text, String action_text, int delay) {
        super(original_action_text, action_text, ActionType.SHOW_TITLE, delay);
    }

    @Override
    public void run(@NotNull Player player, @NotNull String parsedActionText) {

        String[] args = parsedActionText.split(" ");

        long fadein;
        long fadeout;
        long stay;
        String txt;
        try {

            fadein = Long.parseLong(args[0]);
            stay = Long.parseLong(args[1]);
            fadeout = Long.parseLong(args[2]);

            txt = parsedActionText.substring(String.format("%s %s %s ", fadein, stay, fadeout).length());


        } catch (NumberFormatException | IndexOutOfBoundsException e) { throw new ActionPerformFailed("Usage: [show-title] FADE_IN STAY FADE_OUT TITLE{NL}SUBTITLE"); }

        Title TITLE;
        if (txt.contains("{NL}")) {

            String[] texts = txt.split("\\{NL}");

            Component title = PluginMessages.parse(texts[0], player);
            Component subtitle = PluginMessages.parse(texts[1], player);

            TITLE = Title.title(title, subtitle, Title.Times.times(Duration.ofSeconds(fadein), Duration.ofSeconds(stay), Duration.ofSeconds(fadeout)));

        } else {

            Component title = PluginMessages.parse(txt, player);
            Component subtitle = PluginMessages.parse("", player);

            TITLE = Title.title(title, subtitle, Title.Times.times(Duration.ofSeconds(fadein), Duration.ofSeconds(stay), Duration.ofSeconds(fadeout)));
        }

        player.showTitle(TITLE);
    }
}
