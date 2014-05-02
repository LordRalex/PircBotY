package net.ae97.pokebot;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import net.ae97.pokebot.configuration.InvalidConfigurationException;
import net.ae97.pokebot.configuration.file.YamlConfiguration;
import net.ae97.pokebot.eventhandler.EventHandler;
import net.ae97.pokebot.extension.ExtensionManager;
import net.ae97.pokebot.permissions.PermissionManager;
import net.ae97.pokebot.scheduler.Scheduler;

public class PokeBotCore {

    private final EventHandler eventHandler;
    private final YamlConfiguration globalSettings;
    private final PermissionManager permManager;
    private final ExtensionManager extensionManager;
    private final Scheduler scheduler;
    private final PircBotY driver;
    private final Logger logger;

    protected PokeBotCore(Logger logger) throws IOException {
        this.logger = logger;
        if (!(new File("config.yml").exists())) {
            try (InputStream input = PokeBot.class.getResourceAsStream("/config.yml")) {
                try (BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(new File("config.yml")))) {
                    try {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = input.read(buffer)) >= 0) {
                            output.write(buffer, 0, len);
                        }
                        input.close();
                        output.close();
                    } catch (IOException ex) {
                        getLogger().log(Level.SEVERE, "An error occurred on copying the streams", ex);
                    }
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Error on saving config", ex);
            }
        }
        globalSettings = new YamlConfiguration();
        try {
            globalSettings.load(new File("config.yml"));
        } catch (IOException | InvalidConfigurationException ex) {
            logger.log(Level.SEVERE, "Failed to load config.yml", ex);
        }
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
                .setServerHostname(globalSettings.getString("server.ip"))
                .setServerPort(globalSettings.getInt("server.port", 6667))
                .setIdentServerIP(globalSettings.getString("ident.ip", "localhost"))
                .setIdentServerPort(globalSettings.getInt("ident.port", 113));
        if (globalSettings.getBoolean("ssl")) {
            botConfigBuilder.setSocketFactory(SSLSocketFactory.getDefault());
        }
        if (globalSettings.isString("bind-ip")) {
            botConfigBuilder.setLocalAddress(InetAddress.getByName(globalSettings.getString("bind-ip")));
        }
        if (globalSettings.isList("channels")) {
            for (String chan : globalSettings.getStringList("channels")) {
                botConfigBuilder.addAutoJoinChannel(chan);
            }
        }
        driver = new PircBotY(botConfigBuilder.buildConfiguration());
        eventHandler = new EventHandler(driver);
        extensionManager = new ExtensionManager();
        permManager = new PermissionManager();
        scheduler = new Scheduler();
    }

    public void start() {
        eventHandler.load();
        extensionManager.load();
        try {
            permManager.load();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading permissions file", e);
        }
        boolean eventSuccess = driver.getConfiguration().getListenerManager().addListener(eventHandler);
        if (eventSuccess) {
            logger.log(Level.INFO, "Listener hook attached to bot");
        } else {
            logger.log(Level.INFO, "Listener hook was unable to attach to the bot");
        }
        logger.log(Level.INFO, "Initial loading complete, engaging listeners");
        eventHandler.startQueue();
        logger.log(Level.INFO, "All systems operational, starting IRC bot");
        try {
            driver.startBot();
        } catch (IOException | IrcException e) {
            logger.log(Level.SEVERE, "Error starting bot", e);
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

    public PermissionManager getPermManager() {
        return permManager;
    }

    public YamlConfiguration getSettings() {
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
        return logger;
    }
}
