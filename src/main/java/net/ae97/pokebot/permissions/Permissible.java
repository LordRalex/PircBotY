package net.ae97.pokebot.permissions;

import java.util.Map;
import java.util.Set;

public interface Permissible {

    public boolean hasPermission(String channel, String perm);

    public void addPermission(String channel, String perm);

    public void removePermission(String channel, String perm);

    public Map<String, Set<Permission>> getPermissions();
}
