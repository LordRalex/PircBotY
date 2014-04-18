package net.ae97.pokebot.api.events;

public interface ReplyableEvent {

    public void reply(String... messages);
}
