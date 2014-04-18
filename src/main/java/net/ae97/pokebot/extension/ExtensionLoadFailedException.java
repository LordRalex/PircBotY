package net.ae97.pokebot.extension;

public class ExtensionLoadFailedException extends Exception {

    private static final long serialVersionUID = 503342L;

    public ExtensionLoadFailedException(Exception e) {
        super(e);
    }
}
