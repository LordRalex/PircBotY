package net.ae97.pokebot;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocketFactory;
import net.ae97.pircboty.Channel;
import net.ae97.pircboty.Configuration.Builder;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.exception.IrcException;
import net.ae97.pokebot.config.Configuration;
import net.ae97.pokebot.config.impl.JsonConfiguration;
import net.ae97.pokebot.config.impl.MySQLConfiguration;
import net.ae97.pokebot.eventhandler.EventHandler;
import net.ae97.pokebot.extension.ExtensionManager;
import net.ae97.pokebot.scheduler.Scheduler;

public class PokeBotCore {

    private final EventHandler eventHandler;
    private final Configuration globalSettings, rootConfig;
    private final ExtensionManager extensionManager;
    private final Scheduler scheduler;
    private final PircBotY driver;

    protected PokeBotCore() throws IOException {
        rootConfig = new JsonConfiguration(new File("config.json"));

        globalSettings = createConfig("global");

        Builder<PircBotY> botConfigBuilder = new Builder<PircBotY>()
                .setEncoding(Charset.forName("UTF-8"))
                .setVersion("PokeBot - v" + PokeBot.VERSION)
                .setAutoReconnect(true)
                .setAutoNickChange(true)
                .setIdentServerEnabled(true)
                .setName(globalSettings.getString("nick", "PircBotY"))
                .setLogin(globalSettings.getString("nick", "PircBotY"))
                .setRealName(globalSettings.getString("nick", "PircBotY"))
                .setNickservPassword(globalSettings.getString("nick-pw", null))
                .setServerHostname(globalSettings.getString("server.ip", "irc.esper.net"))
                .setServerPort(globalSettings.getInt("server.port", 6667))
                .setIdentServerIP(globalSettings.getString("ident.ip", "localhost"))
                .setIdentServerPort(globalSettings.getInt("ident.port", 113));
        if (globalSettings.getBoolean("ssl")) {
            botConfigBuilder.setSocketFactory(SSLSocketFactory.getDefault());
        }
        botConfigBuilder.setLocalAddress(InetAddress.getByName(globalSettings.getString("bind-ip", "0.0.0.0")));
        if (!globalSettings.getStringList("channels").isEmpty()) {
            globalSettings.getStringList("channels").stream().forEach((chan) -> {
                botConfigBuilder.addAutoJoinChannel(chan);
            });
        } else {
            botConfigBuilder.addAutoJoinChannel("#ae97");
        }
        driver = new PircBotY(botConfigBuilder.buildConfiguration());
        eventHandler = new EventHandler(driver);
        extensionManager = new ExtensionManager();
        scheduler = new Scheduler();
    }

    public void start() {
        eventHandler.load();
        extensionManager.load();
        boolean eventSuccess = driver.getConfiguration().getListenerManager().addListener(eventHandler);
        if (eventSuccess) {
            PokeBot.getLogger().log(Level.INFO, "Listener hook attached to bot");
        } else {
            PokeBot.getLogger().log(Level.INFO, "Listener hook was unable to attach to the bot");
        }
        PokeBot.getLogger().log(Level.INFO, "Initial loading complete, engaging listeners");
        eventHandler.startQueue();
        PokeBot.getLogger().log(Level.INFO, "All systems operational, starting IRC bot");
        try {
            driver.startBot();
        } catch (IOException | IrcException e) {
            PokeBot.getLogger().log(Level.SEVERE, "Error starting bot", e);
        }
    }

    public void shutdown() {
        scheduler.shutdown();
        driver.shutdown(true);
        eventHandler.stopRunner();
        extensionManager.unload();
    }

    public EventHandler getEventHandler() {
        return eventHandler;
    }

    public ExtensionManager getExtensionManager() {
        return extensionManager;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public Configuration getSettings() {
        return globalSettings;
    }

    public Channel getChannel(String name) {
        return driver.getUserChannelDao().getChannel(name);
    }

    public User getUser(String name) {
        return driver.getUserChannelDao().getUser(name);
    }

    public User getBot() {
        return driver.getUserBot();
    }

    public PircBotY getCore() {
        return driver;
    }

    public final Logger getLogger() {
        return PokeBot.getLogger();
    }

    protected final Configuration createConfig(String prefix) {
        if (rootConfig.getBoolean("mysql.use")) {
            return new MySQLConfiguration(
                    rootConfig.getString("mysql.host", "127.0.0.1"),
                    rootConfig.getString("mysql.port", "3306"),
                    rootConfig.getString("mysql.database", "panel"),
                    rootConfig.getString("mysql.table", "config"),
                    prefix,
                    rootConfig.getString("mysql.username", "pokebot"),
                    rootConfig.getString("mysql.password", "pokebot"),
                    rootConfig.getInt("mysql.cache", 60 * 1000));
        } else {
            return rootConfig;
        }
    }
}
