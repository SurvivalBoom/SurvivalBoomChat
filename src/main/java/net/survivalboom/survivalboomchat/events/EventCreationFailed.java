package net.survivalboom.survivalboomchat.events;

import org.jetbrains.annotations.NotNull;

public class EventCreationFailed extends RuntimeException {

    public EventCreationFailed(@NotNull String msg) {
        super(msg);
    }

}
