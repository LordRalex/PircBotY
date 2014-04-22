package net.ae97.pokebot.permissions;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.ae97.pircboty.User;
import net.ae97.pokebot.configuration.InvalidConfigurationException;
import net.ae97.pokebot.configuration.file.YamlConfiguration;

public class PermissionManager {

    private final YamlConfiguration permFile;
    private final Map<User, Long> cache = new ConcurrentHashMap<>();
    private final int CACHE_TIME = 1000 * 60 * 5;

    public PermissionManager() {
        permFile = new YamlConfiguration();
    }

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
        try {
            permFile.load(new File("permissions.yml"));
        } catch (InvalidConfigurationException ex) {
            throw new IOException(ex);
        }
    }

    public void runPermissionEvent(PermissionEvent event) {
        User user = event.getUser();
        synchronized (cache) {
            if (!event.isForced()) {
                if (cache.containsKey(user) && cache.get(user) != null && cache.get(user) > System.currentTimeMillis() + CACHE_TIME) {
                    return;
                }
            }
            cache.put(user, System.currentTimeMillis() + CACHE_TIME);
        }
        String ver = user.getLogin();
        if (ver == null || ver.isEmpty()) {
            return;
        }
        Map<String, Set<Permission>> existing = user.getPermissions();
        for (String key : existing.keySet().toArray(new String[0])) {
            for (Permission perm : existing.get(key).toArray(new Permission[0])) {
                user.removePermission(key, perm.getName());
            }
        }
        List<String> list = permFile.getStringList(ver);
        for (String line : list) {
            String chan = line.split("\\|")[0];
            String perm = line.split("\\|")[1];
            if (chan.isEmpty()) {
                chan = null;
            }
            user.addPermission(chan, perm);
        }
    }
}
