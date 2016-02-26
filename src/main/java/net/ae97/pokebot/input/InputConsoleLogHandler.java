/*
 * Copyright (C) 2014 Lord_Ralex
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.ae97.pokebot.input;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import jline.console.ConsoleReader;
import net.ae97.pokebot.logger.ConsoleLogHandler;

/**
 *
 * @author Lord_Ralex
 */
public class InputConsoleLogHandler extends ConsoleLogHandler {

    private final KeyboardListener keyboard;
    private final OutputStream output;

    public InputConsoleLogHandler(KeyboardListener keyboard, OutputStream out) throws IOException {
        this.keyboard = keyboard;
        this.output = out;
        ConsoleReader reader = this.keyboard.getJLine();
        reader.setPrompt(": ");
    }

    @Override
    public synchronized void publish(LogRecord record) {
        try {
            ConsoleReader reader = keyboard.getJLine();
            reader.print(ConsoleReader.RESET_LINE + "");
            reader.flush();
            super.publish(record);
            if (keyboard.isRunning()) {
                if (!(record instanceof ConsoleParserLogRecord)) {
                    reader.drawLine();
                }
                reader.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(InputConsoleLogHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void close() {
        keyboard.shutdown();
        super.close();
    }
}
