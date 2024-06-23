package net.survivalboom.survivalboomchat.actions.types;

import net.survivalboom.survivalboomchat.actions.Action;
import net.survivalboom.survivalboomchat.actions.ActionPerformFailed;
import net.survivalboom.survivalboomchat.actions.ActionType;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ActionPlaySound extends Action {

    public ActionPlaySound(String original_action_text, String action_text, int delay) {
        super(original_action_text, action_text, ActionType.PLAY_SOUND, delay);
    }

    @Override
    public void run(@NotNull Player player, @NotNull String parsedActionText) {

        String[] args = parsedActionText.split(" ");

        String sound;
        float volume;
        float pitch;
        Sound soundd;

        try {
            sound = args[0];
            volume = Float.parseFloat(args[1]);
            pitch = Float.parseFloat(args[2]);
        } catch (IndexOutOfBoundsException | NumberFormatException e) { throw new ActionPerformFailed("Usage: [play-sound] SOUND_NAME VOLUME PITCH"); }

        try { soundd = Sound.valueOf(sound); } catch (IllegalArgumentException e) { throw new ActionPerformFailed(String.format("Can't parse sound '%s'", sound)); }

        player.playSound(player.getLocation(), soundd, volume, pitch);
    }
}
