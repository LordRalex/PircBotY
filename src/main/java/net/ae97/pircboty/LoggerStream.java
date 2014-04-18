package net.ae97.pircboty;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerStream extends PrintStream {

    private final Logger logger;
    private final Level level;

    public LoggerStream(PrintStream f, Logger l, Level le) {
        super(f);
        logger = l;
        level = le;
    }

    @Override
    public void write(byte[] b) throws IOException {
        logger.log(level, new String(b));
    }
}
