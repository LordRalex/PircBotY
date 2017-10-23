package net.ae97.pokebot.api;

import net.ae97.pircboty.api.events.CommandEvent;

public interface CommandExecutor {

    void runEvent(CommandEvent event);

    String[] getAliases();
}
