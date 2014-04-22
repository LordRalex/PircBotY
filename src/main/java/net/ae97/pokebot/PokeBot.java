package net.ae97.pokebot;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import jline.console.ConsoleReader;
import net.ae97.pircboty.Channel;
import net.ae97.pircboty.ConsoleLogHandler;
import net.ae97.pircboty.FileLogHandler;
import net.ae97.pircboty.LoggerStream;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.PrefixLogger;
import net.ae97.pircboty.User;
import net.ae97.pokebot.configuration.file.YamlConfiguration;
import net.ae97.pokebot.eventhandler.EventHandler;
import net.ae97.pokebot.extension.ExtensionManager;
import net.ae97.pokebot.permissions.PermissionManager;
import net.ae97.pokebot.scheduler.Scheduler;

public final class PokeBot extends Thread {

    private static final PokeBotCore core;
    public static final String VERSION = "6.0.0";

    static {
        Logger logger = new PrefixLogger("Pokebot");
        PokeBotCore tempCore = null;
        try {
            for (Handler h : logger.getHandlers()) {
                logger.removeHandler(h);
            }
            logger.addHandler(new FileLogHandler("output.log"));
            logger.addHandler(new ConsoleLogHandler());
            PircBotY.getLogger().setParent(logger);
            LoggerStream out = new LoggerStream(System.out, logger, Level.INFO);
            LoggerStream err = new LoggerStream(System.err, logger, Level.SEVERE);
            System.setOut(out);
            System.setErr(err);
            tempCore = new PokeBotCore(logger);
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

    public static EventHandler getEventHandler() {
        return core.getEventHandler();
    }

    public static ExtensionManager getExtensionManager() {
        return core.getExtensionManager();
    }

    public static Scheduler getScheduler() {
        return core.getScheduler();
    }

    public static ConsoleReader getConsole() {
        return core.getConsole();
    }

    public static PermissionManager getPermManager() {
        return core.getPermManager();
    }

    public static YamlConfiguration getSettings() {
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
        return core.getLogger();
    }
}
