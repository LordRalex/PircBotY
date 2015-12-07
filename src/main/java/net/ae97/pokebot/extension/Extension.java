package net.ae97.pokebot.extension;

import java.io.File;
import java.util.logging.Logger;
import net.ae97.pokebot.PokeBot;
import net.ae97.pokebot.config.Configuration;
import net.ae97.pokebot.logger.PrefixLogger;

public abstract class Extension {

    private final File dataFolder = new File(PokeBot.getExtensionFolder(), getName());
    private Configuration configuration;
    private final Logger logger = new PrefixLogger(getName(), PokeBot.getLogger());

    public final void initialize() throws ExtensionLoadFailedException {
        dataFolder.mkdirs();
        configuration = PokeBot.createConfig(getName().toLowerCase().replace(" ", ""));
    }

    public void load() throws ExtensionLoadFailedException {
    }

    public void unload() throws ExtensionUnloadFailedException {
    }

    public void reload() throws ExtensionReloadFailedException {
        configuration.reload();
    }

    public abstract String getName();

    public File getDataFolder() {
        return dataFolder;
    }

    public Configuration getConfig() {
        return configuration;
    }

    public final Logger getLogger() {
        return logger;
    }
}
