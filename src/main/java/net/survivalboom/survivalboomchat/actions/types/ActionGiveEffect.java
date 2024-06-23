package net.survivalboom.survivalboomchat.actions.types;

import net.survivalboom.survivalboomchat.actions.Action;
import net.survivalboom.survivalboomchat.actions.ActionPerformFailed;
import net.survivalboom.survivalboomchat.actions.ActionType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class ActionGiveEffect extends Action {

    public ActionGiveEffect(String original_action_text, String action_text, int delay) {
        super(original_action_text, action_text, ActionType.GIVE_EFFECT, delay);
    }

    @Override
    public void run(@NotNull Player player, @NotNull String parsedActionText) {

        String[] args = parsedActionText.split(" ");
        String name;

        int time;
        int power;
        boolean particles;
        try {

            name = args[0];
            time = Integer.parseInt(args[1]) * 20;

            if (time == 0) time = Integer.MAX_VALUE;

            power = Integer.parseInt(args[2]);
            particles = Boolean.parseBoolean(args[3]);

        } catch (NumberFormatException | IndexOutOfBoundsException e) { throw new ActionPerformFailed("Usage: [give-effect] EFFECT_NAME EFFECT_TIME EFFECT_POWER (true/false) HIDE_PARTICLES "); }

        PotionEffectType type;
        try { type = PotionEffectType.getByName(name); } catch (IllegalArgumentException e) { throw new ActionPerformFailed(String.format("Invalid effect %s", name)); }


        player.addPotionEffect(type.createEffect(time, power).withParticles(particles));


    }
}
