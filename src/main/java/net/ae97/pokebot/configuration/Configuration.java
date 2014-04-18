package net.ae97.pokebot.configuration;

import java.util.Map;

public interface Configuration extends ConfigurationSection {

    public void addDefaults(Map<String, Object> defaults);

    public void addDefaults(Configuration defaults);

    public void setDefaults(Configuration defaults);

    public Configuration getDefaults();

    public ConfigurationOptions options();
}
