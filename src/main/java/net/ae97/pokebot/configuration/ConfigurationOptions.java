package net.ae97.pokebot.configuration;

public class ConfigurationOptions {

    private char pathSeparator = '.';
    private boolean copyDefaults = false;
    private final Configuration configuration;

    protected ConfigurationOptions(Configuration conf) {
        configuration = conf;
    }

    public Configuration configuration() {
        return configuration;
    }

    public char pathSeparator() {
        return pathSeparator;
    }

    public ConfigurationOptions pathSeparator(char value) {
        pathSeparator = value;
        return this;
    }

    public boolean copyDefaults() {
        return copyDefaults;
    }

    public ConfigurationOptions copyDefaults(boolean value) {
        copyDefaults = value;
        return this;
    }
}
