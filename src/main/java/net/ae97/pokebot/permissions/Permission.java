package net.ae97.pokebot.permissions;

public class Permission {

    protected final String name;
    protected final PermissionValue defaultValue;

    public Permission(String aName) {
        this(aName, PermissionValue.FALSE);
    }

    public Permission(String aName, PermissionValue def) {
        name = aName;
        defaultValue = def;
    }

    public String getName() {
        return name;
    }

    public PermissionValue getDefault() {
        return defaultValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Permission) {
            Permission prm = (Permission) obj;
            return prm.getName().equalsIgnoreCase(getName()) && prm.getDefault() == prm.getDefault();
        } else if (obj instanceof String) {
            String str = (String) obj;
            return getName().equalsIgnoreCase(str);
        } else {
            return false;
        }
    }

}
