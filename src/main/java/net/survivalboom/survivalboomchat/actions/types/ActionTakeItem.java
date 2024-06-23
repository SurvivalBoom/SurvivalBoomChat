package net.survivalboom.survivalboombedwars.actions.types;

import net.survivalboom.survivalboombedwars.actions.ActionPerformFailed;
import net.survivalboom.survivalboomchat.actions.Action;
import net.survivalboom.survivalboomchat.actions.ActionType;
import net.survivalboom.survivalboomchat.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ActionTakeItem extends Action {

    public ActionTakeItem(String original_action_text, String action_text, int delay) {
        super(original_action_text, action_text, ActionType.TAKE_ITEM, delay);
    }

    @Override
    public void run(@NotNull Player player, @NotNull String parsedActionText) {

        String[] args = parsedActionText.split(" ");

        String itemName;
        int amount;
        try {
            itemName = args[0];
            amount = Integer.parseInt(args[1]);
        }

        catch (IndexOutOfBoundsException | NumberFormatException e) { throw new ActionPerformFailed("Usage: [take-item] MATERIAL AMOUNT");}

        Material material = Material.getMaterial(itemName);
        if (material == null) throw new ActionPerformFailed(String.format("Invalid material '%s'", itemName));

        Utils.removeItem(player, material, amount);

    }

}
