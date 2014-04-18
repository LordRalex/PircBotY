package net.ae97.pokebot.extension;

public class ExtensionUnloadFailedException extends Exception {

    private static final long serialVersionUID = 503343L;

    public ExtensionUnloadFailedException(Exception e) {
        super(e);
    }
}
