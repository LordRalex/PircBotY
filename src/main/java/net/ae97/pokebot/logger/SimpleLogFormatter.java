package net.ae97.pokebot.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class SimpleLogFormatter extends Formatter {

    private final SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");

    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder();
        builder.append(date.format(record.getMillis()));
        builder.append(" [");
        builder.append(record.getLevel().getLocalizedName().toUpperCase());
        builder.append("] ");
        builder.append(String.format(record.getMessage(), record.getParameters()));
        builder.append('\n');
        if (record.getThrown() != null) {
            StringWriter writer = new StringWriter();
            record.getThrown().printStackTrace(new PrintWriter(writer));
            builder.append(writer);
        }
        return builder.toString();
    }
}
