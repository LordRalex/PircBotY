package net.ae97.pircboty;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;

public class FileLogHandler extends FileHandler {

    public FileLogHandler(String file) throws IOException {
        super(file);
        setFormatter(new SimpleLogFormatter());
        this.setLevel(Level.ALL);
    }

}
