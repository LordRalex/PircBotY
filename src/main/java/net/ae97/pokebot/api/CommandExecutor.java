package net.ae97.pokebot.api;

import net.ae97.pokebot.api.events.CommandEvent;

public interface CommandExecutor {

    public void runEvent(CommandEvent event);

    public String[] getAliases();
}
