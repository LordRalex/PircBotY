package net.ae97.pokebot.input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import jline.console.ConsoleReader;
import net.ae97.pircboty.PircBotY;
import net.ae97.pokebot.PokeBot;
import net.ae97.pokebot.PokeBotCore;

public final class KeyboardListener extends Thread {

    private final ConsoleReader kb;
    private final PircBotY bot;
    private final PokeBotCore core;

    public KeyboardListener(PokeBotCore a, PircBotY b) throws IOException {
        setName("Keyboard_Listener_Thread");
        kb = new ConsoleReader();
        bot = b;
        core = a;
    }

    @Override
    public void run() {
        String line;
        boolean run = true;
        String currentChan = "";
        try {
            while (run) {
                try {
                    line = kb.readLine();
                    if (line == null) {
                        run = false;
                    } else {
                        if (line.startsWith("$")) {
                            String cmd = line.substring(1).split(" ")[0];
                            if (cmd.equalsIgnoreCase("channel")) {
                                currentChan = line.split(" ")[1].toLowerCase();
                                System.out.println("Now talking in " + currentChan);
                            } else if (cmd.equalsIgnoreCase("stop")) {
                                run = false;
                            } else if (cmd.equalsIgnoreCase("join")) {
                                bot.sendIRC().joinChannel(line.split(" ")[1]);
                            } else if (cmd.equalsIgnoreCase("leave")) {
                            } else if (cmd.equalsIgnoreCase("me")) {
                                String action = line.substring(3).trim();
                                if (currentChan != null && !currentChan.isEmpty()) {
                                    bot.sendIRC().action(currentChan, action);
                                }
                            } else if (cmd.equalsIgnoreCase("kick")) {
                                List<String> args = new ArrayList<>();
                                String[] parts = line.split(" ");
                                args.addAll(Arrays.asList(parts));
                                args.remove(0);
                                String chan;
                                if (args.get(0).startsWith("#")) {
                                    chan = args.remove(0);
                                } else {
                                    chan = currentChan;
                                }
                                String target = args.remove(0);
                                String reason = "";
                                if (args.size() > 0) {
                                    for (String part : args) {
                                        reason += part + " ";
                                    }
                                    reason = reason.trim();
                                } else {
                                    reason = bot.getNick() + " has kicked " + target + " from the channel";
                                }
                                bot.getUserChannelDao().getChannel(chan).send().kick(bot.getUserChannelDao().getUser(target), reason);
                            }
                        } else {
                            if (currentChan == null || currentChan.isEmpty()) {
                            } else {
                                bot.sendIRC().message(currentChan, line);
                            }
                        }
                    }
                } catch (IOException ex) {
                    PokeBot.getLogger().log(Level.SEVERE, "An error occurred", ex);
                }
            }
        } catch (Exception e) {
        }
        synchronized (core) {
            core.notify();
        }
        kb.shutdown();
        PokeBot.getLogger().log(Level.INFO, "Ending keyboard listener");
    }

    public ConsoleReader getJLine() {
        return kb;
    }
}
