package net.ae97.pircboty.exception;

import net.ae97.pircboty.api.Event;
import org.apache.commons.lang3.Validate;

public class UnknownEventException extends RuntimeException {

    private static final long serialVersionUID = 40292L;

    public UnknownEventException(Event event, Throwable cause) {
        super("Unknown Event " + (event == null ? null : event.getClass().toString()), cause);
        Validate.notNull(event, "Event cannot be null");
    }

    public UnknownEventException(Event event) {
        this(event, null);
    }
}
