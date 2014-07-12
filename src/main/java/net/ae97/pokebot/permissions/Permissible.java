package net.ae97.pokebot.permissions;

public interface Permissible {

    public boolean hasPermission(String channel, String perm);

    public void addPermission(String channel, String perm);

    public void removePermission(String channel, String perm);
}
