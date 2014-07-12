package net.ae97.pokebot.permissions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.ae97.pircboty.User;
import net.ae97.pokebot.configuration.InvalidConfigurationException;
import net.ae97.pokebot.configuration.file.YamlConfiguration;

public class PermissionManager {

    private final YamlConfiguration permFile = new YamlConfiguration();
    private final Map<User, Long> cache = new ConcurrentHashMap<>();
    private final int CACHE_TIME = 1000 * 60 * 5;

    public void load() throws IOException {
        if (!new File("permissions.yml").exists()) {
            new File("permissions.yml").createNewFile();
        }
        try {
            permFile.load(new File("permissions.yml"));
        } catch (InvalidConfigurationException ex) {
            throw new IOException(ex);
        }
    }

    public void reload() throws IOException {
        synchronized (cache) {
            cache.clear();
        }
        load();
    }

    public Map<String, Set<String>> getPermissions(String user) {
        Map<String, Set<String>> permsForUser = new HashMap<>();
        synchronized (permFile) {
            List<String> list = permFile.getStringList(user);
            for (String line : list) {
                String chan = line.split("\\|")[0];
                String perm = line.split("\\|")[1];
                if (chan.isEmpty()) {
                    chan = null;
                }
                if (!permsForUser.containsKey(chan)) {
                    permsForUser.put(chan, new HashSet<String>());
                }
                permsForUser.get(chan).add(perm);
            }
        }
        return permsForUser;
    }
}
