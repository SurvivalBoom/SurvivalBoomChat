package net.survivalboom.survivalboomchat.utils;

import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerLocations {

    protected final TaskScheduler scheduler;

    protected final Map<Player, Location> playerLocationMap = new HashMap<>();

    protected MyScheduledTask task;



    public PlayerLocations(@NotNull TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void init0() {
        task = scheduler.runTaskLater(this::playerLocationTask, 20);
    }

    public void shutdown0() {

        task.cancel();
        task = null;

        playerLocationMap.clear();

    }

    public @NotNull String getName() {
        return "PlayerLocations";
    }


    public @NotNull Map<Player, Location> getPlayerLocations() {
        return new HashMap<>(playerLocationMap);
    }

    public @NotNull Location getPlayerLocation(@NotNull Player player) {
        return playerLocationMap.get(player);
    }

    public @NotNull List<Player> getPlayersNearby(@NotNull Location location, double distance) {

        List<Player> out = new ArrayList<>();
        getPlayerLocations().forEach(((player, loc) -> {

            if (!loc.getWorld().equals(location.getWorld())) return;

            if (loc.distance(location) > distance) return;
            out.add(player);

        }));

        return out;

    }

    protected void playerLocationTask() {

        for (Map.Entry<Player, Location> entry : new ArrayList<>(playerLocationMap.entrySet())) {

            Player player = entry.getKey();
            if (player.isOnline()) continue;

            playerLocationMap.remove(player);

        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            playerLocationMap.put(player, player.getLocation());
        }

    }

}