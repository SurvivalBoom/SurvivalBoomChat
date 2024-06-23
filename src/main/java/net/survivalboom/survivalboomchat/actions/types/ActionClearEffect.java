package net.survivalboom.survivalboomchat.actions.types;

import net.survivalboom.survivalboomchat.actions.Action;
import net.survivalboom.survivalboomchat.actions.ActionPerformFailed;
import net.survivalboom.survivalboomchat.actions.ActionType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class ActionClearEffect extends Action {

    public ActionClearEffect(String original_action_text, String action_text, int delay) {
        super(original_action_text, action_text, ActionType.CLEAR_EFFECT, delay);
    }

    @Override
    public void run(@NotNull Player player, @NotNull String parsedActionText) {

        PotionEffectType type;
        try { type = PotionEffectType.getByName(parsedActionText); }
        catch (IllegalArgumentException e) { throw new ActionPerformFailed(String.format("Invalid effect %s", parsedActionText)); }

        player.removePotionEffect(type);
    }
}
