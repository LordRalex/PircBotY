package net.ae97.pircboty;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import net.ae97.pokebot.logger.SimpleLogFormatter;

public class FileLogHandler extends FileHandler {

    public FileLogHandler(String file) throws IOException {
        super(file, 1024 * 1024 * 50, 1024, true);
        setFormatter(new SimpleLogFormatter());
        this.setLevel(Level.ALL);
    }
}
