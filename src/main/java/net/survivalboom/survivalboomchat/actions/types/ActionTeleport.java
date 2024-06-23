package net.survivalboom.survivalboomchat.actions.types;

import net.survivalboom.survivalboomchat.actions.Action;
import net.survivalboom.survivalboomchat.actions.ActionPerformFailed;
import net.survivalboom.survivalboomchat.actions.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ActionTeleport extends Action {
    public ActionTeleport(String original_action_text, String action_text, int delay) {
        super(original_action_text, action_text, ActionType.TELEPORT_TO_PLAYER, delay);
    }

    @Override
    public void run(@NotNull Player player, @NotNull String parsedActionText) {

        Player other = Bukkit.getPlayer(parsedActionText);

        if (other == null) throw new ActionPerformFailed(String.format("Player %s not found", parsedActionText));

        Location location = other.getLocation();
        player.teleport(location);
    }
}
