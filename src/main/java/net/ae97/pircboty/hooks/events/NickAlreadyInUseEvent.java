package net.ae97.pircboty.hooks.events;

import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.hooks.Event;

public class NickAlreadyInUseEvent<T extends PircBotY> extends Event<T> {

    private final String usedNick;
    private final String autoNewNick;
    private final boolean autoNickChange;

    public NickAlreadyInUseEvent(T bot, String usedNick, String autoNewNick, boolean autoNickChange) {
        super(bot);
        this.usedNick = usedNick;
        this.autoNewNick = autoNewNick;
        this.autoNickChange = autoNickChange;
    }

    @Override
    public void respond(String newNick) {
        getBot().sendIRC().changeNick(newNick);
    }

    public String getUsedNick() {
        return usedNick;
    }

    public String getAutoNewNick() {
        return autoNewNick;
    }

    public boolean isAutoNickChange() {
        return autoNickChange;
    }
}
