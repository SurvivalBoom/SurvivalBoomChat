package net.survivalboom.survivalboomchat.events;

import net.survivalboom.survivalboomchat.actions.Actions;
import net.survivalboom.survivalboomchat.placeholders.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class EventAction {

    private final boolean isEnabled;
    private final Actions actions;
    private final Actions othersActions;
    private final String permisssion;
    private final int onlineLimit;

    public EventAction(@NotNull ConfigurationSection section) {

        isEnabled = section.getBoolean("enabled");
        actions = new Actions(section.getStringList("actions"));
        othersActions = new Actions(section.getStringList("others-actions"));
        permisssion = section.getString("permission");
        onlineLimit = section.getInt("online-limit");

    }

    public void perform(@NotNull Player player, boolean performOthers, @Nullable Placeholders placeholders) {

        if (!isEnabled) return;
        if (permisssion != null && !player.hasPermission(permisssion)) return;

        actions.perform(player, player, placeholders);

        if (!performOthers) return;

        for (Player otherPlayer : Bukkit.getOnlinePlayers()) if (otherPlayer.getName().equals(player.getName())) othersActions.perform(otherPlayer, player, placeholders);
    }

    public void perform(@NotNull Player player, List<Player> otherPlayers, @Nullable Placeholders placeholders) {

        if (!isEnabled) return;
        if (permisssion != null && !player.hasPermission(permisssion)) return;

        actions.perform(player, player, placeholders);

        otherPlayers.remove(player);

        for (Player otherPlayer : otherPlayers) othersActions.perform(otherPlayer, player, placeholders);
    }

    public void perform(Collection<Player> otherPlayers, @Nullable Placeholders placeholders) {

        if (!isEnabled) return;

        for (Player player : otherPlayers) actions.perform(player, player, placeholders);

    }

    public void perform(@NotNull List<Player> targets, @NotNull List<Player> otherPlayers, @Nullable Placeholders placeholders) {

        if (!isEnabled) return;

        otherPlayers.removeIf(targets::contains);

        targets.forEach(p -> actions.perform(p, p, placeholders));
        otherPlayers.forEach(p -> othersActions.perform(p, p, placeholders));

    }

}
