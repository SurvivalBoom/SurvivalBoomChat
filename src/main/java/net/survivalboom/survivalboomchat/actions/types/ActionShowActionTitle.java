package net.survivalboom.survivalboomchat.actions.types;

import net.survivalboom.survivalboomchat.actions.Action;
import net.survivalboom.survivalboomchat.actions.ActionType;
import net.survivalboom.survivalboomchat.configuration.PluginMessages;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ActionShowActionTitle extends Action {

    public ActionShowActionTitle(String original_action_text, String action_text, int delay) {
        super(original_action_text, action_text, ActionType.SHOW_ACTION_TITLE, delay);
    }

    @Override
    public void run(@NotNull Player player, @NotNull String parsedActionText) {

        player.sendActionBar(PluginMessages.parse(parsedActionText, player));

    }
}
