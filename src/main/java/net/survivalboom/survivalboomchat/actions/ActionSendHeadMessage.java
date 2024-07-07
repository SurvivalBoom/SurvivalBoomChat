package net.survivalboom.survivalboomchat.actions;

import net.survivalboom.survivalboomchat.chatheads.ChatHeads;
import net.survivalboom.survivalboomchat.configuration.PluginMessages;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ActionSendHeadMessage extends Action {

    public ActionSendHeadMessage(@NotNull String original_action_text, @NotNull String action_text, int delay) {
        super(original_action_text, action_text, ActionType.SEND_HEAD_MESSAGE, delay);
    }

    @Override
    public void run(@NotNull Player player, @NotNull String parsedActionText) {
        for (String s : ChatHeads.createHeadImage(player, parsedActionText)) PluginMessages.sendMessage(player, s);
    }
}
