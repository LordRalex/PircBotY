package net.ae97.pircboty;

import java.util.logging.ConsoleHandler;

public class ConsoleLogHandler extends ConsoleHandler {

    public ConsoleLogHandler() {
        super();
        this.setFormatter(new SimpleLogFormatter());
    }

}
