package net.ae97.pokebot.logger;

import net.ae97.pokebot.logger.SimpleLogFormatter;
import java.util.logging.ConsoleHandler;

public class ConsoleLogHandler extends ConsoleHandler {

    public ConsoleLogHandler() {
        super();
        this.setFormatter(new SimpleLogFormatter());
    }
}
