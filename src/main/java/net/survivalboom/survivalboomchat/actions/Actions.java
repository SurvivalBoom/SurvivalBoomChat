package net.survivalboom.survivalboomchat.actions;

import net.survivalboom.survivalboomchat.SurvivalBoomChat;
import net.survivalboom.survivalboomchat.actions.types.*;
import net.survivalboom.survivalboomchat.placeholders.Placeholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Actions {

    private final List<Action> ACTIONS = new ArrayList<>();

    public Actions(@NotNull List<String> actions) {

        for (String action : actions) {

            ACTIONS.add(compileAction(action));

        }

    }

    @NotNull
    private static Action compileAction(@NotNull String text) {

        String[] args = text.split(" ");
        String type_setter = args[0];

        String gibiskus = type_setter.toUpperCase().replace("-", "_").substring(1).replace("[", "").replace("]", "");
        ActionType actionType;
        try { actionType = ActionType.valueOf(gibiskus); }
        catch (IllegalArgumentException e) { throw new ActionCreationFailed(String.format("Action with name %s not found. Maybe you made a mistake in plugin configuration.", gibiskus)); }


        String action_text;
        try { action_text = text.substring(type_setter.length() + 1); }
        catch (StringIndexOutOfBoundsException e) { action_text = text.substring(type_setter.length()); }

        if (action_text.startsWith(" ")) action_text = action_text.replaceFirst(" ", "");


        int delay = 1;
        Matcher matcher = Pattern.compile("<delay=(\\d+)>").matcher(action_text);
        if (matcher.find()) {
            delay = Integer.parseInt(matcher.group(1));
            action_text = matcher.replaceAll("");
        }

        if (action_text.endsWith(" ")) action_text = action_text.substring(0, action_text.length() - 1);

        Action action = null;
        switch (actionType) {

            case PLAYER_COMMAND -> action = new ActionPlayerCommand(text, action_text, delay);

            case CONSOLE_COMMAND -> action = new ActionConsoleCommand(text, action_text, delay);

            case SEND_MESSAGE -> action = new ActionSendMessage(text, action_text, delay);

            case CLEAR_CHAT -> action = new ActionClearChat(text, action_text, delay);

            case SPAWN_PARTICLE -> action = new ActionSpawnParticle(text, action_text, delay);

            case STOP_ALL_SOUNDS -> action = new ActionStopAllSounds(text, action_text, delay);

            case STOP_SOUND -> action = new ActionStopSound(text, action_text, delay);

            case GIVE_EFFECT -> action = new ActionGiveEffect(text, action_text, delay);

            case PLAY_SOUND -> action = new ActionPlaySound(text, action_text, delay);

            case SHOW_TITLE -> action = new ActionShowTitle(text, action_text, delay);

            case CLEAR_TITLE -> action = new ActionClearTitle(text, action_text, delay);

            case SEND_TO_SERVER -> action = new ActionSendToServer(text, action_text, delay);

            case CLOSE_MENU -> action = new ActionCloseMenu(text, action_text, delay);

            case CLEAR_EFFECT -> action = new ActionClearEffect(text, action_text, delay);

            case TELEPORT_TO_PLAYER -> action = new ActionTeleport(text, action_text, delay);

            case SHOW_ACTION_TITLE -> action = new ActionShowActionTitle(text, action_text, delay);

            case SEND_HEAD_MESSAGE -> action = new ActionSendHeadMessage(text, action_text, delay);

        }

        if (action == null) throw new ActionCreationFailed(String.format("Enum ActionType contains '%s', but Actions.java doesn't generate action object for this type of action. It shouldn't work like this... ", gibiskus));

        return action;

    }

    public void perform(@NotNull Player player, @Nullable Player placeholdersTarget, @Nullable Placeholders placeholders) {
        for (Action action : ACTIONS) action.perform(player, placeholdersTarget, placeholders);
    }

    public void perform(@NotNull Player player, @Nullable Player placeholdersTarget) {
        perform(player, placeholdersTarget, null);
    }

}
