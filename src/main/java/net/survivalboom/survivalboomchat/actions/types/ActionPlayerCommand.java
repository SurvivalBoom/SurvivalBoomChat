package net.survivalboom.survivalboomchat.actions.types;

import net.survivalboom.survivalboomchat.actions.Action;
import net.survivalboom.survivalboomchat.actions.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ActionPlayerCommand extends Action {

    public ActionPlayerCommand(String original_action_text, String action_text, int delay) {
        super(original_action_text, action_text, ActionType.PLAYER_COMMAND, delay);
    }

    @Override
    public void run(@NotNull Player player, @NotNull String parsedActionText) {
        Bukkit.dispatchCommand(player, parsedActionText);
    }
}
