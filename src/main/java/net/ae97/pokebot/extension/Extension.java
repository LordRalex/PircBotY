package net.ae97.pokebot.extension;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;
import net.ae97.pokebot.logger.PrefixLogger;
import net.ae97.pokebot.PokeBot;
import net.ae97.pokebot.configuration.InvalidConfigurationException;
import net.ae97.pokebot.configuration.file.YamlConfiguration;

public abstract class Extension {

    private final File dataFolder = new File("config", getName().replace(" ", "_"));
    private final YamlConfiguration configuration = new YamlConfiguration();
    private final Logger logger = new PrefixLogger(getName(), PokeBot.getLogger());

    public final void initialize() throws ExtensionLoadFailedException {
        try {
            configuration.load(new File(dataFolder, "config.yml"));
        } catch (FileNotFoundException e) {
        } catch (IOException | InvalidConfigurationException ex) {
            throw new ExtensionLoadFailedException(ex);
        }
    }

    public void load() throws ExtensionLoadFailedException {
    }

    public void unload() throws ExtensionUnloadFailedException {
    }

    public void reload() throws ExtensionReloadFailedException {
        try {
            reloadConfig();
        } catch (IOException | InvalidConfigurationException ex) {
            throw new ExtensionReloadFailedException(ex);
        }
    }

    public abstract String getName();

    public File getDataFolder() {
        return dataFolder;
    }

    public YamlConfiguration getConfig() {
        return configuration;
    }

    public void saveConfig() throws IOException {
        configuration.save(new File(getDataFolder(), "config.yml"));
    }

    public void reloadConfig() throws IOException, InvalidConfigurationException {
        configuration.load(new File(getDataFolder(), "config.yml"));
    }

    public Logger getLogger() {
        return logger;
    }
}
