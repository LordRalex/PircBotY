package net.ae97.pokebot;

import java.io.File;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ae97.pircboty.Channel;
import net.ae97.pircboty.FileLogHandler;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pokebot.config.Configuration;
import net.ae97.pokebot.eventhandler.EventHandler;
import net.ae97.pokebot.extension.ExtensionManager;
import net.ae97.pokebot.input.InputConsoleLogHandler;
import net.ae97.pokebot.input.KeyboardListener;
import net.ae97.pokebot.logger.LoggerStream;
import net.ae97.pokebot.logger.PrefixLogger;
import net.ae97.pokebot.scheduler.Scheduler;

public final class PokeBot extends Thread {

    private static final PokeBotCore core;
    public static final String VERSION = "8.0.0";
    private static final File extensionFolder = new File("extensions");
    private static final Logger logger = new PrefixLogger("PokeBot");

    static {
        PokeBotCore tempCore = null;
        try {
            for (Handler h : logger.getHandlers()) {
                logger.removeHandler(h);
            }
            logger.addHandler(new FileLogHandler("output.log"));
            KeyboardListener listener = new KeyboardListener();
            logger.addHandler(new InputConsoleLogHandler(listener, System.out));
            PircBotY.getLogger().setParent(logger);
            LoggerStream out = new LoggerStream(System.out, logger, Level.INFO);
            LoggerStream err = new LoggerStream(System.err, logger, Level.SEVERE);
            System.setOut(out);
            System.setErr(err);
            listener.start();
            tempCore = new PokeBotCore();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error on creating core bot, cannot continue", e);
            System.exit(1);
        }
        core = tempCore;
    }

    public static void main(String[] startargs) {
        core.start();
        synchronized (core) {
            try {
                core.wait();
            } catch (InterruptedException ex) {
                getLogger().log(Level.SEVERE, "The instance was interrupted", ex);
            }
        }
        core.shutdown();
        System.exit(0);
    }

    public static void shutdown() {
        synchronized (core) {
            core.notify();
        }
    }

    public static EventHandler getEventHandler() {
        return core.getEventHandler();
    }

    public static ExtensionManager getExtensionManager() {
        return core.getExtensionManager();
    }

    public static Scheduler getScheduler() {
        return core.getScheduler();
    }

    public static Configuration getSettings() {
        return core.getSettings();
    }

    public static User getUser(String name) {
        return core.getUser(name);
    }

    public static Channel getChannel(String name) {
        return core.getChannel(name);
    }

    public static PircBotY getBot() {
        return core.getCore();
    }

    public static Logger getLogger() {
        return logger;
    }

    public static File getExtensionFolder() {
        return extensionFolder;
    }

    public static Configuration createConfig(String prefix) {
        return core.createConfig(prefix);
    }
}
