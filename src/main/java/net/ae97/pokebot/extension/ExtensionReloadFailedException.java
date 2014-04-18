package net.ae97.pokebot.extension;

public class ExtensionReloadFailedException extends Exception {

    private static final long serialVersionUID = 503344L;

    public ExtensionReloadFailedException(Exception e) {
        super(e);
    }
}
