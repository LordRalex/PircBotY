package net.ae97.pokebot.extension;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.ae97.pokebot.PokeBot;
import net.ae97.pokebot.api.CommandExecutor;
import net.ae97.pokebot.api.Listener;
import net.ae97.pokebot.loader.ExtensionPluginLoader;
import org.apache.commons.lang3.StringUtils;

public class ExtensionManager {

    private final ExtensionPluginLoader pluginLoader = new ExtensionPluginLoader();
    private final Set<Extension> loadedExtensions = new HashSet<>();

    public void load() {
        File extensionFolder = new File("extensions");
        File temp = new File("tempDir");
        if (temp.listFiles() != null) {
            for (File file : temp.listFiles()) {
                if (file != null) {
                    file.delete();
                }
            }
        }
        temp.delete();
        extensionFolder.mkdirs();
        for (File file : extensionFolder.listFiles()) {
            try {
                if (file.getName().endsWith(".class") && !file.getName().contains("$")) {
                    Set<Extension> extensionList = pluginLoader.findExtensions(file);
                    addExtensions(extensionList);
                } else if (file.getName().endsWith(".zip")) {
                    ZipFile zipFile = null;
                    try {
                        zipFile = new ZipFile(file);
                        Enumeration<? extends ZipEntry> entries = zipFile.entries();
                        while (entries.hasMoreElements()) {
                            ZipEntry entry = (ZipEntry) entries.nextElement();
                            if (entry.getName().contains("/")) {
                                (new File(temp, entry.getName().split("/")[0])).mkdir();
                            }
                            if (entry.isDirectory()) {
                                new File(temp, entry.getName()).mkdirs();
                                continue;
                            }
                            try (InputStream input = zipFile.getInputStream(entry)) {
                                try (OutputStream output = new BufferedOutputStream(new FileOutputStream(temp + File.separator + entry.getName()))) {
                                    byte[] buffer = new byte[1024];
                                    int len;
                                    while ((len = input.read(buffer)) >= 0) {
                                        output.write(buffer, 0, len);
                                    }
                                }
                            } catch (IOException ex) {
                                PokeBot.getLogger().log(Level.SEVERE, "An error occurred on copying the streams", ex);
                            }
                        }
                    } catch (IOException ex) {
                        PokeBot.getLogger().log(Level.SEVERE, "An error occured", ex);
                    } finally {
                        if (zipFile != null) {
                            try {
                                zipFile.close();
                            } catch (IOException ex) {
                                PokeBot.getLogger().log(Level.SEVERE, "An error occured", ex);
                            }
                        }
                    }
                } else if (file.getName().endsWith(".jar")) {
                    Set<Extension> extensionList = pluginLoader.findExtensions(file);
                    addExtensions(extensionList);
                }
            } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                PokeBot.getLogger().log(Level.SEVERE, "Error on loading extension: " + file.getName(), e);
            }
        }
        if (temp.listFiles() != null) {
            for (File file : temp.listFiles()) {
                try {
                    if ((file.getName().endsWith(".class") && !file.getName().contains("$")) || file.getName().endsWith(".jar")) {
                        Set<Extension> extensionList = pluginLoader.findExtensions(file);
                        addExtensions(extensionList);
                    }
                } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                    PokeBot.getLogger().log(Level.SEVERE, "Error on loading extension: " + file.getName(), e);
                }
            }
        }
        Stack<String> nameList = new Stack<>();
        for (Extension extension : loadedExtensions) {
            nameList.add(extension.getName());
        }
        PokeBot.getLogger().info("Loaded extensions: " + StringUtils.join(nameList, ", "));
    }

    public void unload() {
        for (Extension extension : loadedExtensions) {
            try {
                extension.unload();
            } catch (Exception e) {
                PokeBot.getLogger().log(Level.SEVERE, "Error on unloading " + extension.getName(), e);
            }
        }
        loadedExtensions.clear();
        PokeBot.getEventHandler().unload();
    }

    public void reload() throws ExtensionReloadFailedException {
        for (Extension extension : loadedExtensions) {
            try {
                extension.reload();
            } catch (Exception e) {
                PokeBot.getLogger().log(Level.SEVERE, "Error on reloading " + extension.getName(), e);
            }
        }
    }

    public void reload(String extension) throws ExtensionReloadFailedException {
        for (Extension e : loadedExtensions) {
            if (e.getName().replace(" ", "_").equalsIgnoreCase(extension)) {
                try {
                    e.reload();
                } catch (Exception ex) {
                    if (ex instanceof ExtensionReloadFailedException) {
                        throw (ExtensionReloadFailedException) ex;
                    } else {
                        throw new ExtensionReloadFailedException(ex);
                    }
                }
            }
        }
    }

    public Extension getExtension(String name) {
        for (Extension e : loadedExtensions) {
            if (e.getName().equalsIgnoreCase(name) || e.getName().replace("-", " ").equalsIgnoreCase(name) || e.getName().replace("_", " ").equalsIgnoreCase(name)) {
                return e;
            }
        }
        return null;
    }

    public void addListener(Listener list) {
        PokeBot.getEventHandler().registerListener(list);
    }

    public void addCommandExecutor(CommandExecutor executor) {
        PokeBot.getEventHandler().registerCommandExecutor(executor);
    }

    public void addExtension(Extension extension) throws ExtensionUnloadFailedException {
        try {
            extension.initialize();
            extension.load();
            loadedExtensions.add(extension);
        } catch (Exception e) {
            if (e instanceof ExtensionUnloadFailedException) {
                throw (ExtensionUnloadFailedException) e;
            } else {
                throw new ExtensionUnloadFailedException(e);
            }
        }
    }

    public void addExtensions(Set<Extension> extensionList) {
        for (Extension extension : extensionList) {
            try {
                addExtension(extension);
            } catch (ExtensionUnloadFailedException ex) {
                PokeBot.getLogger().log(Level.SEVERE, "Error on loading " + extension.getName(), ex);
            }
        }
    }
}
