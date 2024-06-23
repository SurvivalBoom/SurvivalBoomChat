package net.survivalboom.survivalboomchat.actions.types;

import net.survivalboom.survivalboombedwars.actions.ActionPerformFailed;
import net.survivalboom.survivalboomchat.actions.Action;
import net.survivalboom.survivalboomchat.actions.ActionType;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ActionStopSound extends Action {

    public ActionStopSound(String original_action_text, String action_text, int delay) {
        super(original_action_text, action_text, ActionType.STOP_SOUND, delay);
    }

    @Override
    public void run(@NotNull Player player, @NotNull String parsedActionText) {

        Sound sound;
        try { sound = Sound.valueOf(parsedActionText); } catch (IllegalArgumentException e) { throw new ActionPerformFailed(String.format("Can't parse sound %s", parsedActionText)); }

        player.stopSound(sound);
    }
}
