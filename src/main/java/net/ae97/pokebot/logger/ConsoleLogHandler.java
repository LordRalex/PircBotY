package net.ae97.pokebot.logger;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;

public class ConsoleLogHandler extends ConsoleHandler {

    public ConsoleLogHandler() {
        super();
        this.setFormatter(new SimpleLogFormatter());
    }

    @Override
    public synchronized final void setFormatter(Formatter newFormatter) {
        super.setFormatter(newFormatter);
    }
}
