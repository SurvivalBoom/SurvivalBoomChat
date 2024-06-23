package net.survivalboom.survivalboomchat.actions.types;

import net.survivalboom.survivalboomchat.actions.Action;
import net.survivalboom.survivalboomchat.actions.ActionPerformFailed;
import net.survivalboom.survivalboomchat.actions.ActionType;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ActionSpawnParticle extends Action {

    public ActionSpawnParticle(String original_action_text, String action_text, int delay) {
        super(original_action_text, action_text, ActionType.SPAWN_PARTICLE, delay);
    }

    @Override
    public void run(@NotNull Player player, @NotNull String parsedActionText) {

        String[] args = parsedActionText.split(" ");

        String name;
        int x;
        int y;
        int z;
        int speed;
        Particle particle;
        try {
            name = args[0];
            x = Integer.parseInt(args[1]);
            y = Integer.parseInt(args[2]);
            z = Integer.parseInt(args[3]);
            speed = Integer.parseInt(args[4]);
        } catch (IndexOutOfBoundsException e) { throw new ActionPerformFailed("Usage: [spawn-particle] PARTICLE_NAME PARTICLE_SPEED PARTICLE_X PARTICLE_Y PARTICLE_Z"); }

        try { particle = Particle.valueOf(name); } catch (IllegalArgumentException e) { throw new ActionPerformFailed(String.format("Invalid particle %s", name)); }

        player.spawnParticle(particle, player.getLocation(), speed, x, y, z);
    }
}
