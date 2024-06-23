package net.survivalboom.survivalboomchat.actions.types;

import net.survivalboom.survivalboomchat.actions.Action;
import net.survivalboom.survivalboomchat.actions.ActionType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ActionStopAllSounds extends Action {
    public ActionStopAllSounds(String original_action_text, String action_text, int delay) {
        super(original_action_text, action_text, ActionType.STOP_ALL_SOUNDS, delay);
    }

    @Override
    public void run(@NotNull Player player, @NotNull String parsedActionText) {
        player.stopAllSounds();
    }
}
