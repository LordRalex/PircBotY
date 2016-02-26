package net.ae97.pircboty;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import net.ae97.pokebot.logger.SimpleLogFormatter;

public class FileLogHandler extends FileHandler {

    public FileLogHandler(String file) throws IOException {
        super(file, 1024 * 1024 * 50, 1024, true);
        setFormatter(new SimpleLogFormatter());
        setLevel(Level.ALL);
    }

    @Override
    public final synchronized void setFormatter(Formatter formatter) {
        super.setFormatter(formatter);
    }

    @Override
    public final synchronized void setLevel(Level newLevel) throws SecurityException {
        super.setLevel(newLevel);
    }
}
