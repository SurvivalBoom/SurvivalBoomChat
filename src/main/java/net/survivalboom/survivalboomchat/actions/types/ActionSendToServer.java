package net.survivalboom.survivalboomchat.actions.types;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.survivalboom.survivalboomchat.SurvivalBoomChat;
import net.survivalboom.survivalboomchat.actions.Action;
import net.survivalboom.survivalboomchat.actions.ActionType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ActionSendToServer extends Action {

    public ActionSendToServer(String original_action_text, String action_text, int delay) {
        super(original_action_text, action_text, ActionType.SEND_TO_SERVER, delay);
    }

    @Override
    public void run(@NotNull Player player, @NotNull String parsedActionText) {

        //noinspection UnstableApiUsage
        ByteArrayDataOutput message = ByteStreams.newDataOutput();

        message.writeUTF("Connect");
        message.writeUTF(parsedActionText);

        player.sendPluginMessage(SurvivalBoomChat.getPlugin(), "BungeeCord", message.toByteArray());
    }
}
