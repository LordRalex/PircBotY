package net.ae97.pokebot.input;

import java.io.IOException;
import java.util.logging.Level;
import jline.console.ConsoleReader;
import net.ae97.pokebot.PokeBot;

public final class KeyboardListener extends Thread {

    private final ConsoleReader kb;
    private final ConsoleParser parser = new ConsoleParser();
    private boolean run = false;

    public KeyboardListener() throws IOException {
        setName("Keyboard_Listener_Thread");
        kb = new ConsoleReader();
    }

    @Override
    public void run() {
        String line;
        run = true;
        while (run) {
            try {
                line = kb.readLine();
                if (line != null && !line.isEmpty()) {
                    if (line.split(" ")[0].equalsIgnoreCase("stop")) {
                        run = false;
                    } else {
                        parser.parse(line);
                    }
                }
            } catch (Exception e) {
                PokeBot.getLogger().log(Level.SEVERE, "Error on keyboard input:", e);
            }
        }
        run = false;
        shutdown();
        PokeBot.getLogger().log(Level.INFO, "Ending keyboard listener");
    }

    public boolean isRunning() {
        return run;
    }

    public void shutdown() {
        run = false;
        PokeBot.shutdown();
    }

    public ConsoleReader getJLine() {
        return kb;
    }
}
